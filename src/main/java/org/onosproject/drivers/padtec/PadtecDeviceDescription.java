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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
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
import java.util.Collection;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Driver implementation for Padtec devices integrating via TCP Agent.
 */
public class PadtecDeviceDescription extends AbstractHandlerBehaviour 
        implements DeviceDescriptionDiscovery, PortStatisticsDiscovery {

    private final Logger log = getLogger(getClass());

    // O IP do servidor de Agente agora é localhost
    private static final String AGENT_IP = "127.0.0.1";
    // A porta oficial que o PadtecAgentServer original da UFABC ouvia
    private static final int AGENT_PORT = 10151;

    @Override
    public DeviceDescription discoverDeviceDetails() {
        log.info("Discovering Padtec device details...");
        DeviceId deviceId = handler().data().deviceId();
        
        return new DefaultDeviceDescription(
                deviceId.uri(),
                Device.Type.TERMINAL_DEVICE,
                "Padtec",
                "SPVL4",
                "1.0",
                "TCP-Agent-Integrated",
                new ChassisId(),
                true,
                DefaultAnnotations.EMPTY);
    }

    @Override
    public List<PortDescription> discoverPortDetails() {
        log.info("Discovering ports on Padtec device via TCP Socket (porta {})...", AGENT_PORT);
        List<PortDescription> ports = Lists.newArrayList();

        try (Socket socket = new Socket(AGENT_IP, AGENT_PORT);
             InputStream inputStream = socket.getInputStream()) {

            // Lê todo o fluxo de dados retornado pelo socket TCP
            byte[] buffer = new byte[8192];
            int bytesRead;
            StringBuilder jsonResponse = new StringBuilder();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                jsonResponse.append(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8));
            }

            String jsonString = jsonResponse.toString();
            log.info("Dados TCP Recebidos do Agente: \n{}", jsonString);

            // Usa o ObjectMapper do Jackson que já é dependência do ONOS
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonString);

            if (rootNode.isArray()) {
                int portCounter = 1;
                for (JsonNode node : rootNode) {
                    String type = node.path("type").asText();
                    String name = node.path("name").asText();
                    JsonNode metrics = node.path("metrics");

                    if ("Amplifier".equals(type)) {
                        double gain = metrics.path("gain").asDouble();
                        boolean isLOS = metrics.path("isLOS").asBoolean(false);

                        ports.add(DefaultPortDescription.builder()
                                .withPortNumber(PortNumber.portNumber(portCounter++))
                                .isEnabled(!isLOS)
                                .type(Port.Type.FIBER)
                                .annotations(DefaultAnnotations.builder()
                                        .set("neName", name)
                                        .set("gain", String.valueOf(gain))
                                        .build())
                                .build());

                    } else if ("OTNTransponder".equals(type) || "Transponder".equals(type)) {
                        String channel = metrics.path("channel").asText();
                        boolean isLOS = metrics.path("isLOS").asBoolean(false);

                        ports.add(DefaultPortDescription.builder()
                                .withPortNumber(PortNumber.portNumber(portCounter++))
                                .isEnabled(!isLOS)
                                .type(Port.Type.OCH)
                                .annotations(DefaultAnnotations.builder()
                                        .set("neName", name)
                                        .set("channel", channel)
                                        .build())
                                .build());
                    }
                }
                log.info("Descoberta com sucesso! {} portas identificadas (FIBER/OCH).", ports.size());
            } else {
                log.warn("Formato JSON desconhecido recebido do Agente: Não é um Array.");
            }

        } catch (Exception e) {
            log.error("Erro na comunicação TCP com o Agente Padtec (Verifique se o start_agent.sh está rodando): ", e);
        }

        return ports;
    }

    @Override
    public Collection<PortStatistics> discoverPortStatistics() {
        log.info("Discovering port statistics for Padtec via TCP Agent...");
        DeviceId deviceId = handler().data().deviceId();
        List<PortStatistics> statsList = Lists.newArrayList();

        try {
            DefaultPortStatistics.Builder builder = DefaultPortStatistics.builder();
            builder.setPort(PortNumber.portNumber(1));
            builder.setDeviceId(deviceId);
            builder.setBytesReceived(0);
            builder.setBytesSent(0); 

            statsList.add(builder.build());
        } catch (Exception e) {
            log.error("Failed to mock statistics", e);
        }

        return statsList;
    }
}
