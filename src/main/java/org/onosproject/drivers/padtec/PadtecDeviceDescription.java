/*
 * Copyright 2018-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onosproject.drivers.padtec;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.onosproject.net.DefaultAnnotations;
import org.onosproject.net.Device;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Port;
import org.onosproject.net.PortNumber;
import org.onosproject.net.device.DefaultDeviceDescription;
import org.onosproject.net.device.DefaultPortDescription;
import org.onosproject.net.device.DefaultPortStatistics;
import org.onosproject.net.device.DeviceDescription;
import org.onosproject.net.device.DeviceDescriptionDiscovery;
import org.onosproject.net.device.PortDescription;
import org.onosproject.net.device.PortStatistics;
import org.onosproject.net.device.PortStatisticsDiscovery;
import org.onosproject.net.driver.AbstractHandlerBehaviour;
import org.onlab.packet.ChassisId;
import org.slf4j.Logger;

import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Driver implementation for Padtec devices integrating via TCP Agent.
 */
public class PadtecDeviceDescription extends AbstractHandlerBehaviour
        implements DeviceDescriptionDiscovery, PortStatisticsDiscovery {

    private final Logger log = getLogger(getClass());

    private static final String AGENT_IP   = "127.0.0.1";
    private static final int    AGENT_PORT = 10151;

    /** Substitui NaN por null antes de parsear (NaN não é JSON padrão). */
    private static final Pattern NAN_PATTERN = Pattern.compile(":\\s*NaN");

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    /** Lê o JSON bruto do agente TCP e retorna o primeiro bloco. */
    private String readRawJson() throws Exception {
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
                return null;
            }
            // O agente pode enviar dois blocos separados por linha em branco; pega só o primeiro.
            String first = raw.contains("\n\n") ? raw.split("\n\n")[0] : raw;
            return NAN_PATTERN.matcher(first).replaceAll(": null");
        }
    }

    /** Cria ObjectMapper com suporte a NaN. */
    private ObjectMapper newMapper() {
        ObjectMapper m = new ObjectMapper();
        m.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
        return m;
    }

    // -----------------------------------------------------------------------
    // DeviceDescriptionDiscovery
    // -----------------------------------------------------------------------

    @Override
    public DeviceDescription discoverDeviceDetails() {
        log.debug("Discovering Padtec device details...");
        DeviceId deviceId = handler().data().deviceId();

        // Tenta ler o timestamp da última coleta para incluir nas anotações do device
        String lastCollected = "unknown";
        try {
            String jsonStr = readRawJson();
            if (jsonStr != null) {
                JsonNode root = newMapper().readTree(jsonStr);
                lastCollected = root.path("timestamp").asText("unknown");
            }
        } catch (Exception e) {
            log.debug("Não foi possível ler timestamp do agente: {}", e.getMessage());
        }

        return new DefaultDeviceDescription(
                deviceId.uri(),
                Device.Type.TERMINAL_DEVICE,
                "Padtec",
                "SPVL4",
                "1.0",
                "TCP-Agent-Integrated",
                new ChassisId(),
                true,
                DefaultAnnotations.builder()
                        .set("lastCollected", lastCollected)
                        .set("supervisor", "172.17.36.50:8886")
                        .build());
    }

    @Override
    public List<PortDescription> discoverPortDetails() {
        log.info("Discovering ports on Padtec device via TCP Socket (porta {})...", AGENT_PORT);
        List<PortDescription> ports = new ArrayList<>();

        try {
            String jsonString = readRawJson();
            if (jsonString == null) {
                log.warn("Agente ainda sem dados (JSON vazio). Nenhuma porta registrada.");
                return ports;
            }

            log.debug("JSON recebido do agente:\n{}", jsonString);

            JsonNode rootNode = newMapper().readTree(jsonString);
            JsonNode devicesNode = rootNode.isArray() ? rootNode : rootNode.path("devices");

            if (!devicesNode.isArray()) {
                log.warn("Formato JSON inesperado do Agente — nó 'devices' não encontrado ou não é array.");
                return ports;
            }

            int portCounter = 1;
            for (JsonNode node : devicesNode) {
                String type    = node.path("type").asText();
                String name    = node.path("name").asText();
                JsonNode metrics = node.path("metrics");

                if ("Amplifier".equals(type)) {
                    double gain  = metrics.path("gain").asDouble();
                    boolean isLOS = metrics.path("isLOS").asBoolean(false);

                    ports.add(DefaultPortDescription.builder()
                            .withPortNumber(PortNumber.portNumber(portCounter++))
                            .isEnabled(!isLOS)
                            .type(Port.Type.FIBER)
                            .annotations(DefaultAnnotations.builder()
                                    .set("neName", name)
                                    .set("gain", String.valueOf(gain))
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
                            .withPortNumber(PortNumber.portNumber(portCounter++))
                            .isEnabled(!isLOS)
                            .type(Port.Type.OCH)
                            .annotations(ann.build())
                            .build());
                }
            }
            log.info("Descoberta com sucesso! {} portas identificadas (FIBER/OCH).", ports.size());

        } catch (Exception e) {
            log.error("Erro na comunicação TCP com o Agente Padtec (porta {}): ", AGENT_PORT, e);
        }

        return ports;
    }

    // -----------------------------------------------------------------------
    // PortStatisticsDiscovery
    // -----------------------------------------------------------------------

    /**
     * Retorna estatísticas por porta lendo os dados reais do agente TCP.
     * Para dispositivos ópticos não há contadores de bytes/pacotes; usamos
     * os campos de potência (dBm × 1000 → long) como aproximação legível:
     *   packetsReceived  = inputPower  × 1000  (ex: -5.5 dBm → -5500 µW equiv.)
     *   packetsSent      = outputPower × 1000
     * Portas sem dados de potência (OTNTransponder) retornam 0.
     */
    @Override
    public Collection<PortStatistics> discoverPortStatistics() {
        log.debug("Discovering port statistics for Padtec via TCP Agent...");
        DeviceId deviceId = handler().data().deviceId();
        List<PortStatistics> statsList = new ArrayList<>();

        try {
            String jsonString = readRawJson();
            if (jsonString == null) {
                return statsList;
            }

            JsonNode rootNode = newMapper().readTree(jsonString);
            JsonNode devicesNode = rootNode.isArray() ? rootNode : rootNode.path("devices");

            if (!devicesNode.isArray()) {
                return statsList;
            }

            long now = System.currentTimeMillis() / 1000L;
            int portCounter = 1;

            for (JsonNode node : devicesNode) {
                String type    = node.path("type").asText();
                JsonNode metrics = node.path("metrics");

                long rxPower = 0L;
                long txPower = 0L;

                if ("Transponder".equals(type)) {
                    // Potência em dBm → multiplica por 1000 para preservar casas decimais como long
                    if (!metrics.path("inputPower").isMissingNode() && !metrics.path("inputPower").isNull()) {
                        rxPower = Math.round(metrics.path("inputPower").asDouble() * 1000.0);
                    }
                    if (!metrics.path("outputPower").isMissingNode() && !metrics.path("outputPower").isNull()) {
                        txPower = Math.round(metrics.path("outputPower").asDouble() * 1000.0);
                    }
                }

                statsList.add(DefaultPortStatistics.builder()
                        .setDeviceId(deviceId)
                        .setPort(PortNumber.portNumber(portCounter++))
                        .setBytesReceived(0)
                        .setBytesSent(0)
                        .setPacketsReceived(rxPower)   // inputPower  × 1000
                        .setPacketsSent(txPower)       // outputPower × 1000
                        .setPacketsRxDropped(0)
                        .setPacketsTxDropped(0)
                        .setPacketsRxErrors(0)
                        .setPacketsTxErrors(0)
                        .setDurationSec(now)
                        .setDurationNano(0)
                        .build());
            }

            log.debug("Estatísticas geradas para {} porta(s).", statsList.size());

        } catch (Exception e) {
            log.error("Erro ao gerar estatísticas de porta do Padtec: {}", e.getMessage());
        }

        return statsList;
    }
}
