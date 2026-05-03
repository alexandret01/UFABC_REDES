package org.onosproject.drivers.padtec;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.onosproject.net.DefaultAnnotations;
import org.onosproject.net.Device;
import org.onosproject.net.DeviceId;
import org.onosproject.net.MastershipRole;
import org.onosproject.net.Port;
import org.onosproject.net.PortNumber;
import org.onosproject.net.device.DefaultDeviceDescription;
import org.onosproject.net.device.DefaultPortDescription;
import org.onosproject.net.device.DefaultPortStatistics;
import org.onosproject.net.device.DeviceDescription;
import org.onosproject.net.device.DeviceProvider;
import org.onosproject.net.device.DeviceProviderRegistry;
import org.onosproject.net.device.DeviceProviderService;
import org.onosproject.net.device.PortDescription;
import org.onosproject.net.device.PortStatistics;
import org.onosproject.net.provider.ProviderId;
import org.onlab.packet.ChassisId;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;

import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Provider customizado para injetar o equipamento Padtec no ONOS.
 * Como não usamos OpenFlow ou Netconf, este provider avisa o ONOS
 * que o equipamento existe, forçando o carregamento do Driver.
 */
@Component(immediate = true)
public class PadtecDeviceProvider implements DeviceProvider {

    private final Logger log = getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected DeviceProviderRegistry providerRegistry;

    private DeviceProviderService providerService;
    private static final ProviderId PID = new ProviderId("padtec", "org.onosproject.drivers.padtec");

    // IP Fixo do laboratório conforme os scripts
    private static final String PADTEC_IP = "172.17.36.50";
    private static final DeviceId DEVICE_ID = DeviceId.deviceId("padtec:" + PADTEC_IP);

    // Endereço do agente TCP interno (PadtecAgentServer via PadtecManager)
    private static final String AGENT_IP   = "127.0.0.1";
    private static final int    AGENT_PORT = 10151;

    /** Substitui NaN por null antes de parsear (NaN não é JSON padrão). */
    private static final Pattern NAN_PATTERN = Pattern.compile(":\\s*NaN");

    /** Timestamp da última coleta — atualizado em readPortsFromAgent(). */
    private volatile String lastCollected = "unknown";

    /** Intervalo de atualização de estatísticas: 60 segundos. */
    private static final long STATS_INTERVAL_MS = 60_000L;

    private Timer statsTimer;

    @Activate
    public void activate() {
        providerService = providerRegistry.register(this);
        log.info("Padtec Device Provider Started");

        // Injeta o dispositivo após 45s (aguarda ONOS + agente Java 18 estarem prontos)
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                injectDevice();
                // Inicia polling periódico de estatísticas após a injeção
                startStatsPolling();
            }
        }, 45000);
    }

    /** Inicia timer periódico para atualizar estatísticas de porta no ONOS. */
    private void startStatsPolling() {
        statsTimer = new Timer("padtec-stats", true);
        statsTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    Collection<PortStatistics> stats = readStatsFromAgent();
                    if (!stats.isEmpty() && providerService != null) {
                        providerService.updatePortStatistics(DEVICE_ID, stats);
                        log.debug("Estatísticas de {} porta(s) atualizadas no ONOS.", stats.size());
                    }
                } catch (Exception e) {
                    log.warn("Erro ao atualizar estatísticas Padtec: {}", e.getMessage());
                }
            }
        }, 5000, STATS_INTERVAL_MS);
    }

    private void injectDevice() {
        log.info("Injetando equipamento Padtec {} no core do ONOS...", DEVICE_ID);

        // 1. Lê portas primeiro — também preenche lastCollected com o timestamp do JSON
        List<PortDescription> ports = readPortsFromAgent();

        // 2. Registra o dispositivo com o timestamp da última coleta nas anotações
        DeviceDescription desc = new DefaultDeviceDescription(
                DEVICE_ID.uri(),
                Device.Type.OPTICAL_AMPLIFIER,
                "Padtec",
                "SPVL4 Controller",
                "1.0",
                "TCP-Agent",
                new ChassisId(),
                true,
                DefaultAnnotations.builder()
                        .set("lastCollected", lastCollected)
                        .set("supervisor", "172.17.36.50:8886")
                        .build()
        );

        providerService.deviceConnected(DEVICE_ID, desc);
        log.info("Dispositivo Padtec registrado (última coleta: {}).", lastCollected);

        // 3. Publica as portas
        if (!ports.isEmpty()) {
            providerService.updatePorts(DEVICE_ID, ports);
            log.info("Publicadas {} porta(s) Padtec no ONOS.", ports.size());
        } else {
            log.warn("Agente TCP (porta {}) não retornou portas. " +
                     "Verifique se o PadtecManager subiu corretamente.", AGENT_PORT);
        }
    }

    /**
     * Conecta ao PadtecAgentServer (TCP 10151), lê o JSON e converte
     * em lista de PortDescription para publicação no core do ONOS.
     */
    private List<PortDescription> readPortsFromAgent() {
        List<PortDescription> ports = new ArrayList<>();
        try (Socket socket = new Socket(AGENT_IP, AGENT_PORT);
             InputStream in = socket.getInputStream()) {

            byte[] buf = new byte[8192];
            int n;
            StringBuilder sb = new StringBuilder();
            while ((n = in.read(buf)) != -1) {
                sb.append(new String(buf, 0, n, StandardCharsets.UTF_8));
            }

            String raw = sb.toString().trim();
            if (raw.isEmpty() || "{}".equals(raw)) {
                log.warn("Agente TCP retornou JSON vazio — dados ainda não disponíveis.");
                return ports;
            }

            // Pegar apenas o primeiro bloco JSON e substituir NaN por null
            String jsonStr = raw.contains("\n\n") ? raw.split("\n\n")[0] : raw;
            jsonStr = NAN_PATTERN.matcher(jsonStr).replaceAll(": null");

            log.info("JSON recebido do agente TCP:\n{}", jsonStr);

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
            JsonNode root = mapper.readTree(jsonStr);

            // Captura timestamp da última coleta do hardware
            lastCollected = root.path("timestamp").asText("unknown");
            // Suporta array na raiz OU objeto {"devices":[...]}
            JsonNode devicesNode = root.isArray() ? root : root.path("devices");

            if (devicesNode.isArray()) {
                int portIdx = 1;
                for (JsonNode node : devicesNode) {
                    String type    = node.path("type").asText();
                    String name    = node.path("name").asText();
                    JsonNode metrics = node.path("metrics");

                    if ("Amplifier".equals(type)) {
                        boolean isLOS = metrics.path("isLOS").asBoolean(false);
                        ports.add(DefaultPortDescription.builder()
                                .withPortNumber(PortNumber.portNumber(portIdx++))
                                .isEnabled(!isLOS)
                                .type(Port.Type.FIBER)
                                .annotations(DefaultAnnotations.builder()
                                        .set("neName", name)
                                        .set("gain", String.valueOf(metrics.path("gain").asDouble()))
                                        .set("isLOS", String.valueOf(isLOS))
                                        .build())
                                .build());

                    } else if ("OTNTransponder".equals(type) || "Transponder".equals(type)) {
                        boolean isLOS = metrics.path("isLOS").asBoolean(false);
                        DefaultAnnotations.Builder ann = DefaultAnnotations.builder()
                                .set("neName", name)
                                .set("type", type)
                                .set("channel", metrics.path("channel").asText())
                                .set("isLOS", String.valueOf(isLOS));

                        // Campos extras presentes nos Transponders (podem ser null/NaN no hardware)
                        if (!metrics.path("inputPower").isMissingNode()) {
                            ann.set("inputPower", metrics.path("inputPower").isNull()
                                    ? "N/A" : String.valueOf(metrics.path("inputPower").asDouble()));
                        }
                        if (!metrics.path("outputPower").isMissingNode()) {
                            ann.set("outputPower", metrics.path("outputPower").isNull()
                                    ? "N/A" : String.valueOf(metrics.path("outputPower").asDouble()));
                        }
                        if (!metrics.path("lambda").isMissingNode()) {
                            ann.set("lambda", String.valueOf(metrics.path("lambda").asDouble()));
                        }

                        ports.add(DefaultPortDescription.builder()
                                .withPortNumber(PortNumber.portNumber(portIdx++))
                                .isEnabled(!isLOS)
                                .type(Port.Type.OCH)
                                .annotations(ann.build())
                                .build());
                    }
                }
                log.info("{} porta(s) parseada(s) do JSON do agente.", ports.size());
            } else {
                log.warn("JSON do agente não contém array 'devices'.");
            }

        } catch (Exception e) {
            log.error("Falha ao conectar no agente TCP {}:{} — {}", AGENT_IP, AGENT_PORT, e.getMessage());
        }
        return ports;
    }

    /**
     * Lê dados do agente TCP e retorna estatísticas por porta.
     * inputPower/outputPower (dBm) são codificados × 1000 nos campos de pacotes.
     */
    private Collection<PortStatistics> readStatsFromAgent() {
        List<PortStatistics> stats = new ArrayList<>();
        try (Socket socket = new Socket(AGENT_IP, AGENT_PORT);
             InputStream in = socket.getInputStream()) {

            byte[] buf = new byte[8192];
            int n;
            StringBuilder sb = new StringBuilder();
            while ((n = in.read(buf)) != -1) {
                sb.append(new String(buf, 0, n, StandardCharsets.UTF_8));
            }

            String raw = sb.toString().trim();
            if (raw.isEmpty() || "{}".equals(raw)) {
                return stats;
            }

            String jsonStr = raw.contains("\n\n") ? raw.split("\n\n")[0] : raw;
            jsonStr = NAN_PATTERN.matcher(jsonStr).replaceAll(": null");

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
            JsonNode root = mapper.readTree(jsonStr);
            JsonNode devicesNode = root.isArray() ? root : root.path("devices");

            if (!devicesNode.isArray()) {
                return stats;
            }

            long now = System.currentTimeMillis() / 1000L;
            int portIdx = 1;
            for (JsonNode node : devicesNode) {
                String type    = node.path("type").asText();
                JsonNode metrics = node.path("metrics");

                long rxPower = 0L;
                long txPower = 0L;

                if ("Transponder".equals(type)) {
                    if (!metrics.path("inputPower").isMissingNode() && !metrics.path("inputPower").isNull()) {
                        rxPower = Math.round(metrics.path("inputPower").asDouble() * 1000.0);
                    }
                    if (!metrics.path("outputPower").isMissingNode() && !metrics.path("outputPower").isNull()) {
                        txPower = Math.round(metrics.path("outputPower").asDouble() * 1000.0);
                    }
                }

                stats.add(DefaultPortStatistics.builder()
                        .setDeviceId(DEVICE_ID)
                        .setPort(PortNumber.portNumber(portIdx++))
                        .setBytesReceived(0)
                        .setBytesSent(0)
                        .setPacketsReceived(rxPower)
                        .setPacketsSent(txPower)
                        .setPacketsRxDropped(0)
                        .setPacketsTxDropped(0)
                        .setPacketsRxErrors(0)
                        .setPacketsTxErrors(0)
                        .setDurationSec(now)
                        .setDurationNano(0)
                        .build());
            }

        } catch (Exception e) {
            log.debug("Erro ao ler estatísticas do agente: {}", e.getMessage());
        }
        return stats;
    }

    @Deactivate
    public void deactivate() {
        if (statsTimer != null) {
            statsTimer.cancel();
            statsTimer = null;
        }
        if (providerService != null) {
            providerService.deviceDisconnected(DEVICE_ID);
            providerRegistry.unregister(this);
            providerService = null;
        }
        log.info("Padtec Device Provider Stopped");
    }

    @Override
    public ProviderId id() {
        return PID;
    }

    @Override
    public void triggerProbe(DeviceId deviceId) {
        // Ignorado, o driver cuida das probes via PortStatisticsDiscovery
    }

    @Override
    public void roleChanged(DeviceId deviceId, MastershipRole newRole) {
        providerService.receivedRoleReply(deviceId, newRole, MastershipRole.MASTER);
    }

    @Override
    public boolean isReachable(DeviceId deviceId) {
        return true;
    }

    @Override
    public void changePortState(DeviceId deviceId, PortNumber portNumber, boolean enable) {
        // Read-only
    }
}
