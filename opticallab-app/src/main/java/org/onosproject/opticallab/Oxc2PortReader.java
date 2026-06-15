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
 * Lê métricas de cada porta do OXC2 (Polatis) via RESTCONF JSON.
 *
 * Três endpoints do módulo YANG optical-switch:
 *
 *   port-config → status (ENABLED/DISABLED), label, peer-port
 *   opm-power   → power (dBm) — portas de saída (9-16)
 *   voa         → atten-mode, atten-level (dB quando mode=VOA_MODE_ABSOLUTE)
 *
 * Usa Jackson (já disponível no OSGi do ONOS) para evitar problemas com
 * DocumentBuilderFactory.newInstance() em OSGi (FactoryConfigurationError).
 */
public class Oxc2PortReader {

    private static final Logger log = LoggerFactory.getLogger(Oxc2PortReader.class);

    private static final String BASE = "http://172.17.36.22:8008/api/data";
    private static final String AUTH = "Basic " +
            java.util.Base64.getEncoder().encodeToString("admin:root".getBytes());
    private static final int TIMEOUT_MS = 5000;

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Retorna métricas por porta mesclando três endpoints.
     * Campos: portId, status, label, peerPort, power (dBm), attenMode, attenLevel (dB).
     *
     * @return lista de mapas, um por porta, ordenada por portId
     */
    public List<Map<String, String>> readPortMetrics() {
        Map<Integer, Map<String, String>> byPort = new LinkedHashMap<>();

        // 1. port-config: status e cross-connect (portas 1-16)
        JsonNode portConfig = fetchJson(BASE + "/optical-switch:port-config");
        for (JsonNode port : portArray(portConfig, "port-config")) {
            int id = port.path("port-id").asInt();
            Map<String, String> m = entry(byPort, id);
            m.put("status",   textOf(port, "status"));
            m.put("label",    textOf(port, "label"));
            m.put("peerPort", textOf(port, "peer-port"));
        }

        // 2. opm-power: potência em dBm (portas de saída 9-16)
        JsonNode opmPower = fetchJson(BASE + "/optical-switch:opm-power");
        for (JsonNode port : portArray(opmPower, "opm-power")) {
            int id = port.path("port-id").asInt();
            Map<String, String> m = entry(byPort, id);
            if (!port.path("power").isMissingNode()) {
                m.put("power", port.path("power").asText());
            }
        }

        // 3. voa: modo e nível de atenuação (portas de saída 9-16)
        JsonNode voa = fetchJson(BASE + "/optical-switch:voa");
        for (JsonNode port : portArray(voa, "voa")) {
            int id = port.path("port-id").asInt();
            Map<String, String> m = entry(byPort, id);
            if (!port.path("atten-mode").isMissingNode()) {
                m.put("attenMode",  port.path("atten-mode").asText());
            }
            if (!port.path("atten-level").isMissingNode()) {
                m.put("attenLevel", port.path("atten-level").asText());
            }
        }

        List<Map<String, String>> result = new ArrayList<>(byPort.values());
        result.sort((a, b) -> Integer.compare(
                Integer.parseInt(a.getOrDefault("portId", "0")),
                Integer.parseInt(b.getOrDefault("portId", "0"))));
        return result;
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    /** Obtém ou cria entrada no mapa, garantindo que portId esteja presente. */
    private static Map<String, String> entry(Map<Integer, Map<String, String>> byPort, int id) {
        return byPort.computeIfAbsent(id, k -> {
            Map<String, String> m = new LinkedHashMap<>();
            m.put("portId", String.valueOf(k));
            return m;
        });
    }

    /**
     * Extrai o array "port" do container YANG.
     * Tenta "optical-switch:container" e "container" como chave raiz.
     */
    private static Iterable<JsonNode> portArray(JsonNode root, String container) {
        if (root == null) {
            return new ArrayList<>();
        }
        for (String key : new String[]{"optical-switch:" + container, container}) {
            JsonNode c = root.get(key);
            if (c != null) {
                JsonNode ports = c.get("port");
                if (ports != null && ports.isArray()) {
                    return ports;
                }
            }
        }
        // raiz é o próprio container
        JsonNode ports = root.get("port");
        return (ports != null && ports.isArray()) ? ports : new ArrayList<>();
    }

    private JsonNode fetchJson(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", AUTH);
            conn.setRequestProperty("Accept", "application/yang-data+json");
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            if (conn.getResponseCode() != 200) {
                log.warn("OXC2 {} retornou HTTP {}", urlStr, conn.getResponseCode());
                return null;
            }
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            in.close();
            return mapper.readTree(sb.toString());
        } catch (Exception e) {
            log.warn("OXC2 fetch error {}: {}", urlStr, e.getMessage());
            return null;
        }
    }

    private static String textOf(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return (v != null && !v.isNull()) ? v.asText() : "";
    }
}
