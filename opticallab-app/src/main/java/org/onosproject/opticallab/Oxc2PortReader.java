package org.onosproject.opticallab;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lê métricas de cada porta do OXC2 (Polatis) via RESTCONF (XML).
 *
 * Três endpoints consultados (mesmo módulo YANG optical-switch):
 *
 *   port-config → port-id, status (ENABLED/DISABLED), label, peer-port
 *   opm-power   → port-id, power (dBm) — OPM disponível nas portas de saída (9-16)
 *   voa         → port-id, atten-mode, atten-level (dB, quando mode=VOA_MODE_ABSOLUTE)
 *
 * Equivalente NETCONF ao PolatisPowerConfig.java:
 *   acquirePortPower()       → opm-power/port/power
 *   acquirePortAttenuation() → voa/port/atten-level
 */
public class Oxc2PortReader {

    private static final Logger log = LoggerFactory.getLogger(Oxc2PortReader.class);

    private static final String BASE = "http://172.17.36.22:8008/api/data";
    private static final String AUTH = "Basic " +
            java.util.Base64.getEncoder().encodeToString("admin:root".getBytes());
    private static final int TIMEOUT_MS = 5000;

    /**
     * Retorna lista de métricas por porta, mesclando os três endpoints.
     * Campos: portId, status, label, peerPort, power (dBm), attenMode, attenLevel (dB).
     */
    public List<Map<String, String>> readPortMetrics() {
        Map<Integer, Map<String, String>> byPort = new LinkedHashMap<>();

        // 1. port-config: status e cross-connect (todos as 16 portas)
        parseXml(fetch(BASE + "/optical-switch:port-config"), "port", byPort, (port, m) -> {
            m.put("status",   textOf(port, "status"));
            m.put("label",    textOf(port, "label"));
            m.put("peerPort", textOf(port, "peer-port"));
        });

        // 2. opm-power: potência atual em dBm (portas de saída, 9-16)
        parseXml(fetch(BASE + "/optical-switch:opm-power"), "port", byPort, (port, m) -> {
            String pw = textOf(port, "power");
            if (!pw.isEmpty()) m.put("power", pw);
        });

        // 3. voa: modo e nível de atenuação (portas de saída, 9-16)
        parseXml(fetch(BASE + "/optical-switch:voa"), "port", byPort, (port, m) -> {
            String mode  = textOf(port, "atten-mode");
            String level = textOf(port, "atten-level");
            if (!mode.isEmpty())  m.put("attenMode",  mode);
            if (!level.isEmpty()) m.put("attenLevel", level);
        });

        List<Map<String, String>> result = new ArrayList<>(byPort.values());
        result.sort((a, b) -> Integer.compare(
                Integer.parseInt(a.getOrDefault("portId", "0")),
                Integer.parseInt(b.getOrDefault("portId", "0"))));
        return result;
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    @FunctionalInterface
    interface PortConsumer {
        void accept(Element portElement, Map<String, String> portMap);
    }

    private void parseXml(String xml, String tag,
                          Map<Integer, Map<String, String>> byPort,
                          PortConsumer consumer) {
        if (xml == null || xml.isEmpty()) return;
        try {
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
            doc.getDocumentElement().normalize();
            NodeList ports = doc.getElementsByTagName(tag);
            for (int i = 0; i < ports.getLength(); i++) {
                Element port = (Element) ports.item(i);
                String idStr = textOf(port, "port-id");
                if (idStr.isEmpty()) continue;
                int id = Integer.parseInt(idStr);
                Map<String, String> m = byPort.computeIfAbsent(id, k -> {
                    Map<String, String> nm = new LinkedHashMap<>();
                    nm.put("portId", String.valueOf(k));
                    return nm;
                });
                consumer.accept(port, m);
            }
        } catch (Exception e) {
            log.warn("OXC2 XML parse error: {}", e.getMessage());
        }
    }

    private String fetch(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", AUTH);
            conn.setConnectTimeout(TIMEOUT_MS);
            conn.setReadTimeout(TIMEOUT_MS);
            if (conn.getResponseCode() != 200) return null;
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) sb.append(line);
            in.close();
            return sb.toString();
        } catch (Exception e) {
            log.warn("OXC2 fetch error {}: {}", urlStr, e.getMessage());
            return null;
        }
    }

    private static String textOf(Element parent, String tag) {
        NodeList nl = parent.getElementsByTagName(tag);
        return nl.getLength() > 0 ? nl.item(0).getTextContent().trim() : "";
    }
}
