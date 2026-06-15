package org.onosproject.opticallab;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Link;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.flow.FlowEntry;
import org.onosproject.net.flow.FlowRuleService;
import org.onosproject.net.link.LinkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Coleta métricas do laboratório óptico UFABC de três fontes:
 *
 * 1. Agente Padtec TCP (localhost:10151) — transponders e amplificadores
 * 2. OXC2 REST (172.17.36.22:8008)       — cross-connects
 * 3. ONOS services                        — flows PAV, links LLDP, device status
 */
public class OpticalLabCollector {

    private final Logger log = LoggerFactory.getLogger(getClass());

    // Agente Padtec
    private static final String AGENT_HOST = "127.0.0.1";
    private static final int    AGENT_PORT = 10151;
    private static final int    AGENT_TIMEOUT_MS = 5000;

    // OXC2 REST
    private static final String OXC2_URL =
            "http://172.17.36.22:8008/api/data/optical-switch:cross-connects";
    private static final String OXC2_AUTH =
            "Basic " + java.util.Base64.getEncoder().encodeToString("admin:root".getBytes());

    // PAV switches
    private static final DeviceId PAV1 = DeviceId.deviceId("of:5e3ec454441280b9");
    private static final DeviceId PAV2 = DeviceId.deviceId("of:5e3ec454443294fb");
    private static final int PAV_FLOW_PRIORITY = 40000;

    // ONOS device
    private static final DeviceId PADTEC = DeviceId.deviceId("padtec:172.17.36.50");

    private final DeviceService   deviceService;
    private final FlowRuleService flowRuleService;
    private final LinkService     linkService;
    private final ObjectMapper    mapper = new ObjectMapper();

    public OpticalLabCollector(DeviceService deviceService,
                               FlowRuleService flowRuleService,
                               LinkService linkService) {
        this.deviceService   = deviceService;
        this.flowRuleService = flowRuleService;
        this.linkService     = linkService;
    }

    /**
     * Executa uma coleta completa e retorna um DataPoint.
     * Nunca lança exceção — retorna DataPoint com dados parciais em caso de falha.
     */
    public DataPoint collect() {
        List<Map<String, String>> devices      = readPadtecAgent();
        List<int[]>               crossConnects = readOxc2CrossConnects();
        int                       pavFlows      = countPavFlowsAdded();
        int                       lldpLinks     = countLldpLinks();
        boolean                   padtecOk      = isPadtecAvailable();

        return new DataPoint(devices, crossConnects, pavFlows, lldpLinks, padtecOk);
    }

    // ── 1. Padtec TCP Agent ────────────────────────────────────────────────────

    private List<Map<String, String>> readPadtecAgent() {
        List<Map<String, String>> result = new ArrayList<>();
        try (Socket s = new Socket(AGENT_HOST, AGENT_PORT)) {
            s.setSoTimeout(AGENT_TIMEOUT_MS);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(s.getInputStream()));
            // Agent sends JSON then closes
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            parseAgentJson(sb.toString(), result);
        } catch (Exception e) {
            log.warn("Padtec agent unreachable: {}", e.getMessage());
        }
        return result;
    }

    private void parseAgentJson(String json, List<Map<String, String>> out) {
        if (json == null || json.isEmpty()) return;
        try {
            // Replace NaN values (non-standard JSON from Padtec SDK)
            json = json.replaceAll(":\\s*NaN", ":null");

            JsonNode root = mapper.readTree(json);
            JsonNode devices = root.get("devices");
            if (devices == null || !devices.isArray()) return;

            for (JsonNode dev : devices) {
                Map<String, String> m = new LinkedHashMap<>();
                m.put("neName", textOrEmpty(dev, "name"));
                m.put("type",   textOrEmpty(dev, "type"));

                JsonNode metrics = dev.get("metrics");
                if (metrics != null && metrics.isObject()) {
                    metrics.fields().forEachRemaining(e -> {
                        JsonNode v = e.getValue();
                        if (!v.isNull()) {
                            m.put(e.getKey(), v.asText());
                        }
                    });
                }
                out.add(m);
            }
        } catch (Exception e) {
            log.warn("Failed to parse Padtec agent JSON: {}", e.getMessage());
        }
    }

    // ── 2. OXC2 REST ──────────────────────────────────────────────────────────

    private List<int[]> readOxc2CrossConnects() {
        List<int[]> result = new ArrayList<>();
        try {
            URL url = new URL(OXC2_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", OXC2_AUTH);
            conn.setRequestProperty("Accept", "application/yang-data+json");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() != 200) return result;

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) sb.append(line);
            in.close();

            JsonNode root = mapper.readTree(sb.toString());
            // Try both possible JSON keys from OXC2
            JsonNode xc = root.get("optical-switch:cross-connects");
            if (xc == null) xc = root.get("cross-connects");
            if (xc == null) return result;

            JsonNode pairs = xc.get("pair");
            if (pairs == null || !pairs.isArray()) return result;

            for (JsonNode p : pairs) {
                int ingress = p.has("ingress") ? p.get("ingress").asInt() : -1;
                int egress  = p.has("egress")  ? p.get("egress").asInt()  : -1;
                if (ingress > 0 && egress > 0) {
                    result.add(new int[]{ingress, egress});
                }
            }
        } catch (Exception e) {
            log.warn("OXC2 unreachable: {}", e.getMessage());
        }
        return result;
    }

    // ── 3. ONOS Services ──────────────────────────────────────────────────────

    private int countPavFlowsAdded() {
        int count = 0;
        for (DeviceId dev : new DeviceId[]{PAV1, PAV2}) {
            try {
                Iterable<FlowEntry> flows = flowRuleService.getFlowEntries(dev);
                for (FlowEntry f : flows) {
                    if (f.priority() == PAV_FLOW_PRIORITY
                            && f.state() == FlowEntry.FlowEntryState.ADDED) {
                        count++;
                    }
                }
            } catch (Exception e) {
                log.warn("Cannot read flows for {}: {}", dev, e.getMessage());
            }
        }
        return count;
    }

    private int countLldpLinks() {
        int count = 0;
        try {
            for (Link link : linkService.getActiveLinks()) {
                // Count only links between PAV switches (optical path confirmed)
                String src = link.src().deviceId().toString();
                String dst = link.dst().deviceId().toString();
                if (src.startsWith("of:") && dst.startsWith("of:")) {
                    count++;
                }
            }
        } catch (Exception e) {
            log.warn("Cannot read links: {}", e.getMessage());
        }
        return count;
    }

    private boolean isPadtecAvailable() {
        try {
            return deviceService.isAvailable(PADTEC);
        } catch (Exception e) {
            return false;
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static String textOrEmpty(JsonNode n, String field) {
        JsonNode v = n.get(field);
        return (v != null && !v.isNull()) ? v.asText() : "";
    }
}
