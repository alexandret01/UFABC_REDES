package org.onosproject.opticallab;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Snapshot de métricas coletadas em um instante de tempo.
 *
 * Contém:
 *  - devices: lista de transponders/amplificadores com suas métricas
 *  - crossConnects: pares ingress→egress do OXC2
 *  - pavFlowsAdded: flows com priority=40000 nos switches PAV (estado ADDED)
 *  - lldpLinks: links ópticos descobertos via LLDP no ONOS
 */
public class DataPoint {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC);

    public final long timestampMs;
    public final String timestamp;

    /** Métricas por dispositivo Padtec (transponders + amplificadores). */
    public final List<Map<String, String>> devices;

    /** Pares cross-connect: int[2] = {ingress, egress}. */
    public final List<int[]> crossConnects;

    /** Quantos flows priority=40000 estão ADDED nos PAVs. */
    public final int pavFlowsAdded;

    /** Quantidade de links ópticos LLDP no ONOS. */
    public final int lldpLinks;

    /** ID do dispositivo Padtec (para debug). */
    public final boolean padtecAvailable;

    public DataPoint(
            List<Map<String, String>> devices,
            List<int[]> crossConnects,
            int pavFlowsAdded,
            int lldpLinks,
            boolean padtecAvailable) {
        this.timestampMs = System.currentTimeMillis();
        this.timestamp = FMT.format(Instant.ofEpochMilli(this.timestampMs));
        this.devices = devices != null ? devices : new ArrayList<>();
        this.crossConnects = crossConnects != null ? crossConnects : new ArrayList<>();
        this.pavFlowsAdded = pavFlowsAdded;
        this.lldpLinks = lldpLinks;
        this.padtecAvailable = padtecAvailable;
    }

    /** Cabeçalho CSV: expande um device por linha, uma linha por porta. */
    public static String csvHeader() {
        return "timestamp,deviceName,type,channel,lambda," +
               "inputPower,outputPower,inputPowerWDM,outputPowerWDM," +
               "gain,powerInput,powerOutput,isAGC," +
               "isLOS,isLOF,isBDI,isClientLOS,isClientLOF," +
               "fecRate,fecErrors," +
               "oxc2Pairs,pavFlowsAdded,lldpLinks\n";
    }

    /**
     * Linhas CSV para este DataPoint: uma linha por device.
     * Campos de contexto (oxc2Pairs, pavFlows, lldpLinks) são repetidos.
     */
    public String toCsvRows() {
        String oxcPairs = crossConnects.stream()
                .map(p -> p[0] + "→" + p[1])
                .reduce((a, b) -> a + "|" + b)
                .orElse("");

        StringBuilder sb = new StringBuilder();
        for (Map<String, String> d : devices) {
            sb.append(timestamp).append(',');
            sb.append(escape(d.get("neName"))).append(',');
            sb.append(escape(d.get("type"))).append(',');
            sb.append(escape(d.get("channel"))).append(',');
            sb.append(escape(d.get("lambda"))).append(',');
            sb.append(escape(d.get("inputPower"))).append(',');
            sb.append(escape(d.get("outputPower"))).append(',');
            sb.append(escape(d.get("inputPowerWDM"))).append(',');
            sb.append(escape(d.get("outputPowerWDM"))).append(',');
            sb.append(escape(d.get("gain"))).append(',');
            sb.append(escape(d.get("powerInput"))).append(',');
            sb.append(escape(d.get("powerOutput"))).append(',');
            sb.append(escape(d.get("isAGC"))).append(',');
            sb.append(escape(d.get("isLOS"))).append(',');
            sb.append(escape(d.get("isLOF"))).append(',');
            sb.append(escape(d.get("isBDI"))).append(',');
            sb.append(escape(d.get("isClientLOS"))).append(',');
            sb.append(escape(d.get("isClientLOF"))).append(',');
            sb.append(escape(d.get("fecRate"))).append(',');
            sb.append(escape(d.get("fecErrors"))).append(',');
            sb.append('"').append(oxcPairs).append('"').append(',');
            sb.append(pavFlowsAdded).append(',');
            sb.append(lldpLinks).append('\n');
        }
        // Se não tiver devices, grava uma linha com context only
        if (devices.isEmpty()) {
            sb.append(timestamp).append(",,,,,,,,,,,,,,,,,,,,");
            sb.append('"').append(oxcPairs).append('"').append(',');
            sb.append(pavFlowsAdded).append(',');
            sb.append(lldpLinks).append('\n');
        }
        return sb.toString();
    }

    private static String escape(String s) {
        if (s == null || s.isEmpty()) return "";
        if (s.contains(",") || s.contains("\"")) return "\"" + s.replace("\"", "\"\"") + "\"";
        return s;
    }

    /** Converte para Map para serialização JSON. */
    public Map<String, Object> toMap() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("timestamp", timestamp);
        m.put("timestampMs", timestampMs);
        m.put("padtecAvailable", padtecAvailable);
        m.put("devices", devices);
        List<Map<String, Integer>> xc = new ArrayList<>();
        for (int[] p : crossConnects) {
            Map<String, Integer> pair = new LinkedHashMap<>();
            pair.put("ingress", p[0]);
            pair.put("egress", p[1]);
            xc.add(pair);
        }
        m.put("crossConnects", xc);
        m.put("pavFlowsAdded", pavFlowsAdded);
        m.put("lldpLinks", lldpLinks);
        return m;
    }
}
