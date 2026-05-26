import br.com.padtec.v3.data.ne.NE;
import br.com.padtec.v3.data.ne.Transponder;
import br.com.padtec.v3.data.ne.Amplifier;
import br.com.padtec.v3.data.ne.TrpOTNTerminal;
import br.ufabc.equipment.Amplifiers;
import br.ufabc.equipment.OTNTransponder;
import br.ufabc.equipment.Supervisor;
import br.ufabc.equipment.Transponders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.function.BooleanSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * Aplicação Java 18 que monitora o transponder Padtec SPVL4 e exporta
 * as métricas em formato JSON para consumo pelo aplicativo ONOS.
 *
 * Melhorias v3 (UFABC):
 *  - Loop de coleta contínuo: re-exporta JSON a cada POLLING_INTERVAL_MS
 *  - OTNTransponder: todos os campos disponíveis exportados (WDM, ODU-k, FEC, Client)
 *  - Helpers safe* para tolerar exceções/NaN por campo sem perder o restante
 */
public class PadtecMonitorJSON3 {

    public static Supervisor sup;
    public static List<NE> monitored;

    /** Instância do Agente TCP em memória. */
    public static PadtecAgentServer agentePadtec = new PadtecAgentServer();

    /** Intervalo entre coletas (ms). */
    private static final long POLLING_INTERVAL_MS = 60_000L;

    /** Diretório de saída (não utilizado — dados vão direto para o agente). */
    private static final String OUTPUT_DIR = "/tmp/padtec_metrics/";

    // -----------------------------------------------------------------------
    // main
    // -----------------------------------------------------------------------

    public static void main(String[] args) {

        // 1. Inicia o Agente TCP antes de qualquer coisa
        new Thread(agentePadtec).start();
        System.out.println("[Monitor] Agente Padtec (RAM) iniciado na porta 10151.");

        monitored = new ArrayList<>();

        System.out.println("[Monitor] === PadtecMonitorJSON3 ===");
        System.out.println("[Monitor] Conectando ao supervisor 172.17.36.50...");

        // 2. Inicialização do Supervisor
        sup = new Supervisor("172.17.36.50", Supervisor.TypeSupervisor.SPVL);
        sup.start();

        // 3. Aguardar carregamento dos NEs
        try {
            System.out.println("[Monitor] Aguardando NEs carregarem (20s)...");
            Thread.sleep(20_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("[Monitor] Interrompido durante inicialização.");
            return;
        }

        // 4. Descoberta dos elementos
        System.out.println("[Monitor] Coletando elementos de rede...");

        List<? extends NE> amplifiers = Amplifiers.getAmplifiers(sup);
        if (!amplifiers.isEmpty()) {
            monitored.addAll(amplifiers);
            System.out.println("[Monitor] " + amplifiers.size() + " Amplificador(es) encontrado(s).");
        } else {
            System.out.println("[Monitor] Nenhum Amplificador encontrado.");
        }

        List<? extends NE> transponders = Transponders.getTransponders(sup);
        if (!transponders.isEmpty()) {
            monitored.addAll(transponders);
            System.out.println("[Monitor] " + transponders.size() + " Transponder(es) OTN encontrado(s).");
        } else {
            System.out.println("[Monitor] Nenhum Transponder encontrado.");
        }

        if (monitored.isEmpty()) {
            System.out.println("[Monitor] Nenhum elemento para monitorar. Encerrando.");
            return;
        }

        System.out.println("[Monitor] Iniciando loop de coleta a cada " +
                           (POLLING_INTERVAL_MS / 1000) + "s para " +
                           monitored.size() + " elemento(s)...");

        // 5. Loop contínuo de coleta e publicação
        while (!Thread.currentThread().isInterrupted()) {
            try {
                exportMetricsToJSON();
            } catch (Exception e) {
                System.err.println("[Monitor] Erro no ciclo de coleta: " + e.getMessage());
            }

            try {
                Thread.sleep(POLLING_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("[Monitor] Loop encerrado.");
    }

    // -----------------------------------------------------------------------
    // Exportação JSON
    // -----------------------------------------------------------------------

    private static void exportMetricsToJSON() {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\n");
        jsonBuilder.append("  \"timestamp\": \"").append(timestamp).append("\",\n");
        jsonBuilder.append("  \"supervisor\": \"172.17.36.50:8886\",\n");
        jsonBuilder.append("  \"devices\": [\n");

        boolean first = true;
        for (NE ne : monitored) {
            if (!first) jsonBuilder.append(",\n");
            first = false;

            try {
                if (ne instanceof Amplifier) {
                    jsonBuilder.append(exportAmplifierJSON((Amplifier) ne));
                } else if (ne instanceof TrpOTNTerminal) {
                    jsonBuilder.append(exportOTNTransponderJSON((TrpOTNTerminal) ne));
                } else if (ne instanceof Transponder) {
                    jsonBuilder.append(exportTransponderJSON((Transponder) ne));
                }
            } catch (Exception e) {
                System.err.println("[Monitor] Erro ao exportar " +
                        ne.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }

        jsonBuilder.append("\n  ]\n}\n");

        enviarParaAgente(jsonBuilder.toString(), timestamp);
    }

    // -----------------------------------------------------------------------
    // Amplifier
    // -----------------------------------------------------------------------

    private static String exportAmplifierJSON(Amplifier amp) {
        Amplifiers amplifier = new Amplifiers(sup, amp);
        sleepSafe(500);

        StringBuilder json = new StringBuilder();
        json.append("    {\n");
        json.append("      \"type\": \"Amplifier\",\n");
        json.append("      \"name\": \"").append(escapeJSON(amp.getName())).append("\",\n");
        json.append("      \"metrics\": {\n");
        json.append("        \"powerInput\":  ").append(safeDouble(amplifier::getPowerInput)).append(",\n");
        json.append("        \"powerOutput\": ").append(safeDouble(amplifier::getPowerOutput)).append(",\n");
        json.append("        \"gain\":         ").append(safeDouble(amplifier::getGain)).append(",\n");
        json.append("        \"isAGC\":        ").append(safeBool(amplifier::isAGC)).append(",\n");
        json.append("        \"isLOS\":        ").append(safeBool(amplifier::isLOS)).append("\n");
        json.append("      }\n");
        json.append("    }");
        return json.toString();
    }

    // -----------------------------------------------------------------------
    // OTN Transponder — todos os campos disponíveis
    // -----------------------------------------------------------------------

    private static String exportOTNTransponderJSON(TrpOTNTerminal otnTrp) {
        OTNTransponder t = new OTNTransponder(sup, otnTrp);
        sleepSafe(500);

        StringBuilder json = new StringBuilder();
        json.append("    {\n");
        json.append("      \"type\": \"OTNTransponder\",\n");
        json.append("      \"name\": \"").append(escapeJSON(otnTrp.getName())).append("\",\n");
        json.append("      \"metrics\": {\n");

        // --- Interface WDM ---
        json.append("        \"channel\":          \"").append(escapeJSON(safeStr(t::getChannel))).append("\",\n");
        json.append("        \"lambda\":            ").append(safeDouble(t::getLambda)).append(",\n");
        json.append("        \"inputPowerWDM\":     ").append(safeDouble(t::getInputPowerWDM)).append(",\n");
        json.append("        \"outputPowerWDM\":    ").append(safeDouble(t::getOutputPowerWDM)).append(",\n");
        json.append("        \"isLOS\":             ").append(safeBool(t::isLOS)).append(",\n");
        json.append("        \"isLOF\":             ").append(safeBool(t::isLOF)).append(",\n");
        json.append("        \"isOff\":             ").append(safeBool(t::isOff)).append(",\n");

        // --- ODU-k (qualidade do sinal) ---
        json.append("        \"bip8Rate\":          ").append(safeDouble(t::getBIP8Rate)).append(",\n");
        json.append("        \"beiRate\":           ").append(safeDouble(t::getBEIRate)).append(",\n");
        json.append("        \"isBDI\":             ").append(safeBool(t::isBDI)).append(",\n");

        // --- FEC ---
        json.append("        \"fecName\":           \"").append(escapeJSON(safeStr(t::getFECName))).append("\",\n");
        json.append("        \"fecErrors\":         ").append(safeLong(t::getFECErrors)).append(",\n");
        json.append("        \"fecRate\":           ").append(safeDoubleObj(t::getFECRate)).append(",\n");
        json.append("        \"fecRxEnabled\":      ").append(safeBool(t::isFECReceptionEnabled)).append(",\n");
        json.append("        \"fecTxEnabled\":      ").append(safeBool(t::isFECTransmissionEnabled)).append(",\n");

        // --- Interface Cliente ---
        json.append("        \"inputPowerClient\":  ").append(safeDouble(t::getInputPowerClient)).append(",\n");
        json.append("        \"outputPowerClient\": ").append(safeDouble(t::getOutputPowerClient)).append(",\n");
        json.append("        \"clientLambda\":      ").append(safeDouble(t::getClientLambda)).append(",\n");
        json.append("        \"isClientLOS\":       ").append(safeBool(t::isClientLOS)).append(",\n");
        json.append("        \"isClientLOF\":       ").append(safeBool(t::isClientLOF)).append(",\n");
        json.append("        \"isClientOff\":       ").append(safeBool(t::isClientOff)).append("\n");

        json.append("      }\n");
        json.append("    }");
        return json.toString();
    }

    // -----------------------------------------------------------------------
    // Transponder genérico
    // -----------------------------------------------------------------------

    private static String exportTransponderJSON(Transponder transp) {
        Transponders t = new Transponders(sup, transp);
        sleepSafe(500);

        StringBuilder json = new StringBuilder();
        json.append("    {\n");
        json.append("      \"type\": \"Transponder\",\n");
        json.append("      \"name\": \"").append(escapeJSON(transp.getName())).append("\",\n");
        json.append("      \"metrics\": {\n");
        json.append("        \"inputPower\":  ").append(safeDouble(t::getInputPower)).append(",\n");
        json.append("        \"outputPower\": ").append(safeDouble(t::getOutputPower)).append(",\n");
        json.append("        \"channel\":     \"").append(escapeJSON(safeStr(t::getChannel))).append("\",\n");
        json.append("        \"lambda\":      ").append(safeDouble(t::getLambda)).append(",\n");
        json.append("        \"isLOS\":       ").append(safeBool(t::isLOS)).append("\n");
        json.append("      }\n");
        json.append("    }");
        return json.toString();
    }

    // -----------------------------------------------------------------------
    // Publicação no agente
    // -----------------------------------------------------------------------

    private static void enviarParaAgente(String json, String timestamp) {
        try {
            agentePadtec.updateMetrics(json);
            System.out.println("[" + timestamp + "] JSON atualizado no agente TCP :10151 " +
                               "(" + json.length() + " bytes).");
        } catch (Exception e) {
            System.err.println("[Monitor] Erro ao enviar para o agente: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------------
    // Helpers safe* — isolam exceções e NaN por campo
    // -----------------------------------------------------------------------

    /** double → "valor" ou null (NaN vira null). */
    private static String safeDouble(DoubleSupplier s) {
        try {
            double v = s.getAsDouble();
            return Double.isNaN(v) || Double.isInfinite(v) ? "null" : String.valueOf(v);
        } catch (Exception e) {
            return "null";
        }
    }

    /** Double (boxed) → "valor" ou null. */
    private static String safeDoubleObj(Supplier<Double> s) {
        try {
            Double v = s.get();
            if (v == null || v.isNaN() || v.isInfinite()) return "null";
            return String.valueOf(v);
        } catch (Exception e) {
            return "null";
        }
    }

    /** boolean → "true"/"false" ou null. */
    private static String safeBool(BooleanSupplier s) {
        try {
            return String.valueOf(s.getAsBoolean());
        } catch (Exception e) {
            return "null";
        }
    }

    /** long → "valor" ou 0. */
    private static String safeLong(LongSupplier s) {
        try {
            return String.valueOf(s.getAsLong());
        } catch (Exception e) {
            return "0";
        }
    }

    /** String → valor ou "". */
    private static String safeStr(Supplier<String> s) {
        try {
            String v = s.get();
            return v != null ? v : "";
        } catch (Exception e) {
            return "";
        }
    }

    private static void sleepSafe(long ms) {
        try { Thread.sleep(ms); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    // -----------------------------------------------------------------------
    // Escape JSON
    // -----------------------------------------------------------------------

    private static String escapeJSON(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
