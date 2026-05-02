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
import org.onosproject.net.device.DeviceDescription;
import org.onosproject.net.device.DeviceDescriptionDiscovery;
import org.onosproject.net.driver.AbstractHandlerBehaviour;
import org.onlab.packet.ChassisId;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Driver para Padtec que consome dados de um Agente HTTP externo.
 */
public class PadtecDeviceDescription extends AbstractHandlerBehaviour 
        implements DeviceDescriptionDiscovery {

    private final Logger log = getLogger(getClass());
    private static final String AGENT_URL = "http://127.0.0.1:10151/get-metrics";

    @Override
    public DeviceDescription discoverDeviceDetails() {
        DeviceId deviceId = handler().data().deviceId();
        return new DefaultDeviceDescription(
                deviceId.uri(), Device.Type.TERMINAL_DEVICE, "Padtec", "SPVL4", "1.0",
                "Agente-Padtec", new ChassisId(), true, DefaultAnnotations.EMPTY);
    }

    @Override
    public List<PortDescription> discoverPortDetails() {
        log.info("Buscando portas do Padtec no Agente Externo: {}", AGENT_URL);
        List<PortDescription> ports = Lists.newArrayList();

        try {
            URL url = new URL(AGENT_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);

            if (conn.getResponseCode() != 200) {
                log.error("Falha ao conectar no Agente Padtec. Código HTTP: {}", conn.getResponseCode());
                return ports;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String json = reader.readLine();
            log.info("Agente respondeu com JSON: {}", json);
            
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(json);
            JsonNode devicesNode = rootNode.path("devices");

            if (devicesNode.isArray()) {
                long portCounter = 1;
                for (JsonNode deviceNode : devicesNode) {
                    String type = deviceNode.path("type").asText();
                    String name = deviceNode.path("name").asText("N/A");
                    JsonNode metrics = deviceNode.path("metrics");

                    if (type.equals("Amplifier")) {
                        ports.add(DefaultPortDescription.builder()
                                .withPortNumber(PortNumber.portNumber(portCounter++))
                                .isEnabled(true)
                                .type(Port.Type.FIBER)
                                .annotations(DefaultAnnotations.builder()
                                        .set("neName", name)
                                        .set("gain", metrics.path("gain").asText("0.0"))
                                        .build())
                                .build());
                    } else if (type.equals("Transponder")) {
                        ports.add(DefaultPortDescription.builder()
                                .withPortNumber(PortNumber.portNumber(portCounter++))
                                .isEnabled(!metrics.path("isLOS").asBoolean(false))
                                .type(Port.Type.OCH)
                                .annotations(DefaultAnnotations.builder()
                                        .set("neName", name)
                                        .set("channel", metrics.path("channel").asText("N/A"))
                                        .build())
                                .build());
                    }
                }
            }
            conn.disconnect();
        } catch (Exception e) {
            log.error("Erro crítico ao comunicar com o Agente Padtec: ", e);
        }
        return ports;
    }
}
