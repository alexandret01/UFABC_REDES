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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Collection;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Driver implementation for Padtec devices integrating External Middleware logic.
 */
public class PadtecDeviceDescription extends AbstractHandlerBehaviour 
        implements DeviceDescriptionDiscovery, PortStatisticsDiscovery {

    private final Logger log = getLogger(getClass());

    // IP de loopback onde o Agente do Jaquison está rodando (via monitor.sh)
    private static final String AGENT_IP = "127.0.0.1";
    // A porta que vimos no arquivo PadtecAgentServer.java
    private static final int AGENT_PORT = 10151;

    @Override
    public DeviceDescription discoverDeviceDetails() {
        log.info("Discovering Padtec device details via Jaquison Agent...");
        DeviceId deviceId = handler().data().deviceId();
        
        return new DefaultDeviceDescription(
                deviceId.uri(),
                Device.Type.TERMINAL_DEVICE,
                "Padtec",
                "SPVL4",
                "1.0",
                "Jaquison",
                new ChassisId(),
                true,
                DefaultAnnotations.EMPTY);
    }

    @Override
    public List<PortDescription> discoverPortDetails() {
        log.info("Discovering ports on Padtec device via TCP Agent na porta {}...", AGENT_PORT);
        DeviceId deviceId = handler().data().deviceId();
        List<PortDescription> ports = Lists.newArrayList();

        try {
            // Conecta via TCP (Socket) no PadtecAgentServer (que o Jaquison inicia)
            log.debug("Tentando conectar no Socket {}:{}", AGENT_IP, AGENT_PORT);
            Socket socket = new Socket(AGENT_IP, AGENT_PORT);
            
            // O Agente no PadtecAgentServer.java apenas despeja (out.println) a string JSON
            // assim que a conexão é aceita, e depois fecha a conexão.
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuilder jsonResponse = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonResponse.append(line);
            }
            reader.close();
            socket.close();
            
            String json = jsonResponse.toString();
            log.info("Agente Padtec respondeu: {}", json);

            // Parsing do JSON que o PadtecMonitorJSON3 gerou
            if (json == null || json.trim().isEmpty() || json.equals("{}")) {
                log.warn("Agente Padtec retornou um JSON vazio. As portas ainda não foram carregadas pelo Jaquison?");
                return ports; // Retorna vazio
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(json);
            
            JsonNode devicesNode = rootNode.path("devices");
            if (devicesNode.isArray()) {
                long portCounter = 1;
                
                for (JsonNode deviceNode : devicesNode) {
                    String type = deviceNode.path("type").asText("");
                    String name = deviceNode.path("name").asText("Unknown");
                    JsonNode metrics = deviceNode.path("metrics");
                    
                    if (type.equals("Amplifier")) {
                        double gain = metrics.path("gain").asDouble(0.0);
                        
                        ports.add(DefaultPortDescription.builder()
                            .withPortNumber(PortNumber.portNumber(portCounter++))
                            .isEnabled(true)
                            .type(Port.Type.FIBER)
                            .annotations(DefaultAnnotations.builder()
                                .set("neName", name)
                                .set("gain", String.valueOf(gain))
                                .build())
                            .build());
                            
                    } else if (type.equals("Transponder") || type.equals("OTNTransponder")) {
                        String channel = metrics.path("channel").asText("");
                        boolean isLos = metrics.path("isLOS").asBoolean(false);
                        
                        ports.add(DefaultPortDescription.builder()
                            .withPortNumber(PortNumber.portNumber(portCounter++))
                            .isEnabled(!isLos) // Loss of Signal
                            .type(Port.Type.OCH)
                            .annotations(DefaultAnnotations.builder()
                                .set("neName", name)
                                .set("channel", channel)
                                .build())
                            .build());
                    }
                }
            }

        } catch (Exception e) {
            log.error("Erro na comunicação via TCP Socket com o Agente Padtec: ", e);
        }

        return ports;
    }

    @Override
    public Collection<PortStatistics> discoverPortStatistics() {
        // Implementação simplificada baseada no mesmo Agent
        List<PortStatistics> statsList = Lists.newArrayList();
        return statsList;
    }
}
