package org.onosproject.opticallab;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lê status de cada porta do OXC2 (Polatis) via REST RESTCONF.
 *
 * Endpoint disponível neste firmware:
 *   GET /api/data/optical-switch:port-config  → XML com port-id, status, label, peer-port
 *
 * Nota: port-state (potência óptica) e atenuação não estão disponíveis
 * via REST neste firmware Polatis. Os valores existem via NETCONF (SSH 830)
 * conforme PolatisPowerConfig.java do driver ONOS, mas requerem cliente SSH.
 */
public class Oxc2PortReader {

    private static final Logger log = LoggerFactory.getLogger(Oxc2PortReader.class);

    private static final String OXC2_PORT_CONFIG =
            "http://172.17.36.22:8008/api/data/optical-switch:port-config";
    private static final String OXC2_AUTH =
            "Basic " + java.util.Base64.getEncoder()
                    .encodeToString("admin:root".getBytes());
    private static final int TIMEOUT_MS = 5000;

    /**
     * Retorna lista com status de cada porta do OXC2.
     * Cada mapa contém: portId, status (ENABLED/DISABLED), label, peerPort.
     * Nunca lança exceção — retorna lista vazia em caso de falha.
     */
    public List<Map<String, String>> readPortMetrics() {
        List<Map<String, String>> result = new ArrayList<>();
        try {
            String xml = fetchXml(OXC2_PORT_CONFIG);
            if (xml == null || xml.isEmpty()) return result;

            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
            doc.getDocumentElement().normalize();

            NodeList ports = doc.getElementsByTagName("port");
            for (int i = 0; i < ports.getLength(); i++) {
                Element port = (Element) ports.item(i);
                Map<String, String> m = new LinkedHashMap<>();
                m.put("portId",   textOf(port, "port-id"));
                m.put("status",   textOf(port, "status"));
                m.put("label",    textOf(port, "label"));
                m.put("peerPort", textOf(port, "peer-port"));
                result.add(m);
            }
            log.debug("OXC2 port-config: {} portas lidas.", result.size());
        } catch (Exception e) {
            log.warn("OXC2 port-config inacessível: {}", e.getMessage());
        }
        return result;
    }

    private String fetchXml(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", OXC2_AUTH);
        conn.setConnectTimeout(TIMEOUT_MS);
        conn.setReadTimeout(TIMEOUT_MS);
        int code = conn.getResponseCode();
        if (code != 200) throw new Exception("HTTP " + code);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) sb.append(line);
        in.close();
        return sb.toString();
    }

    private static String textOf(Element parent, String tag) {
        NodeList nl = parent.getElementsByTagName(tag);
        return nl.getLength() > 0 ? nl.item(0).getTextContent().trim() : "";
    }
}
