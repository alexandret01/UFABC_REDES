package org.onosproject.opticallab;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lê métricas por porta do OXC2 (Polatis) via REST RESTCONF.
 *
 * YANG paths consultados:
 *   port-config → atenuação por porta (unidade: 0.01 dB, ex: 100 = 1.00 dB)
 *   port-state  → leitura de potência óptica por porta (unidade: 0.01 dBm)
 *
 * Os mesmos campos são expostos pelo driver NETCONF Polatis via:
 *   PolatisPowerConfig.acquirePortAttenuation()  → port-config/port/attenuation
 *   PolatisPowerConfig.currentPower()            → port-state/port/power-reading
 */
public class Oxc2PortReader {

    private static final Logger log = LoggerFactory.getLogger(Oxc2PortReader.class);

    private static final String OXC2_BASE =
            "http://172.17.36.22:8008/api/data/optical-switch:optical-switch";
    private static final String OXC2_AUTH =
            "Basic " + java.util.Base64.getEncoder()
                    .encodeToString("admin:root".getBytes());
    private static final int TIMEOUT_MS = 5000;

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Retorna lista com métricas de cada porta do OXC2.
     * Cada mapa contém: portId, attenuation (dB string), powerReading (dBm string).
     * Nunca lança exceção — retorna lista vazia em caso de falha.
     */
    public List<Map<String, String>> readPortMetrics() {
        // Lê config (atenuação) e state (potência) em paralelo lógico
        Map<Integer, Double> attenuation  = fetchPortAttenuation();
        Map<Integer, Double> powerReading = fetchPortPower();

        // Une os dois mapas indexados por portId
        List<Map<String, String>> result = new ArrayList<>();
        for (Integer portId : attenuation.keySet()) {
            Map<String, String> m = new LinkedHashMap<>();
            m.put("portId",       String.valueOf(portId));
            m.put("attenuation",  fmt(attenuation.get(portId)));
            m.put("powerReading", powerReading.containsKey(portId)
                    ? fmt(powerReading.get(portId)) : "N/A");
            result.add(m);
        }

        // Portas com potência mas sem config de atenuação (estado apenas)
        for (Integer portId : powerReading.keySet()) {
            if (!attenuation.containsKey(portId)) {
                Map<String, String> m = new LinkedHashMap<>();
                m.put("portId",       String.valueOf(portId));
                m.put("attenuation",  "N/A");
                m.put("powerReading", fmt(powerReading.get(portId)));
                result.add(m);
            }
        }

        result.sort((a, b) ->
                Integer.compare(Integer.parseInt(a.get("portId")),
                                Integer.parseInt(b.get("portId"))));
        return result;
    }

    // ── REST helpers ──────────────────────────────────────────────────────────

    /**
     * GET /port-config — retorna mapa portId → atenuação em dB (já convertida de 0.01dB).
     */
    private Map<Integer, Double> fetchPortAttenuation() {
        Map<Integer, Double> result = new LinkedHashMap<>();
        try {
            JsonNode root = getJson(OXC2_BASE + "/port-config");
            JsonNode portConfig = firstOf(root,
                    "optical-switch:port-config", "port-config");
            if (portConfig == null) return result;

            JsonNode ports = portConfig.get("port");
            if (ports == null || !ports.isArray()) return result;

            for (JsonNode p : ports) {
                int portId = p.path("port-id").asInt(-1);
                if (portId < 0) continue;
                JsonNode att = p.get("attenuation");
                if (att != null && !att.isNull()) {
                    // Polatis armazena em unidades de 0.01 dB
                    result.put(portId, att.asDouble() / 100.0);
                }
            }
        } catch (Exception e) {
            log.warn("OXC2 port-config unreachable: {}", e.getMessage());
        }
        return result;
    }

    /**
     * GET /port-state — retorna mapa portId → potência atual em dBm (convertida de 0.01dBm).
     */
    private Map<Integer, Double> fetchPortPower() {
        Map<Integer, Double> result = new LinkedHashMap<>();
        try {
            JsonNode root = getJson(OXC2_BASE + "/port-state");
            JsonNode portState = firstOf(root,
                    "optical-switch:port-state", "port-state");
            if (portState == null) return result;

            JsonNode ports = portState.get("port");
            if (ports == null || !ports.isArray()) return result;

            for (JsonNode p : ports) {
                int portId = p.path("port-id").asInt(-1);
                if (portId < 0) continue;
                // Tenta power-reading (padrão Polatis) e current-power (variante)
                JsonNode pw = p.get("power-reading");
                if (pw == null) pw = p.get("current-power");
                if (pw != null && !pw.isNull()) {
                    // Polatis armazena em unidades de 0.01 dBm
                    result.put(portId, pw.asDouble() / 100.0);
                }
            }
        } catch (Exception e) {
            log.warn("OXC2 port-state unreachable: {}", e.getMessage());
        }
        return result;
    }

    private JsonNode getJson(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", OXC2_AUTH);
        conn.setRequestProperty("Accept", "application/yang-data+json");
        conn.setConnectTimeout(TIMEOUT_MS);
        conn.setReadTimeout(TIMEOUT_MS);
        int code = conn.getResponseCode();
        if (code != 200) {
            throw new Exception("HTTP " + code + " from " + urlStr);
        }
        BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) sb.append(line);
        in.close();
        return mapper.readTree(sb.toString());
    }

    /** Retorna o primeiro nó encontrado de uma lista de chaves candidatas. */
    private static JsonNode firstOf(JsonNode root, String... keys) {
        for (String k : keys) {
            JsonNode n = root.get(k);
            if (n != null) return n;
        }
        return null;
    }

    /** Formata double com 2 casas decimais. */
    private static String fmt(double v) {
        return String.format("%.2f", v);
    }
}
