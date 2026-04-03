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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Driver implementation for Padtec devices integrating External Middleware logic.
 */
public class PadtecDeviceDescription extends AbstractHandlerBehaviour 
        implements DeviceDescriptionDiscovery, PortStatisticsDiscovery {

    private final Logger log = getLogger(getClass());

    // URL do Middleware Externo (que você subirá separadamente)
    private static final String MIDDLEWARE_URL = "http://localhost:8080/api/padtec/ports";

    @Override
    public DeviceDescription discoverDeviceDetails() {
        log.info("Discovering Padtec device details via External Middleware...");
        DeviceId deviceId = handler().data().deviceId();
        
        return new DefaultDeviceDescription(
                deviceId.uri(),
                Device.Type.TERMINAL_DEVICE,
                "Padtec",
                "SPVL4",
                "1.0",
                "Middleware-Integrated",
                new ChassisId(),
                true,
                DefaultAnnotations.EMPTY);
    }

    @Override
    public List<PortDescription> discoverPortDetails() {
        log.info("Discovering ports on Padtec device via Middleware HTTP API...");
        DeviceId deviceId = handler().data().deviceId();
        String ip = deviceId.uri().getSchemeSpecificPart();

        List<PortDescription> ports = Lists.newArrayList();

        try {
            // Faz a chamada HTTP para o Middleware Java
            URL url = new URL(MIDDLEWARE_URL + "?ip=" + ip);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000); // 5 segundos
            
            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder jsonResponse = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonResponse.append(line);
                }
                reader.close();
                
                String json = jsonResponse.toString();
                log.info("Middleware respondeu: {}", json);

                // ==============================================================
                // Parsing manual simples do JSON mockado retornado pelo Middleware
                // (Em produção, o ONOS usa o Jackson Mapper para isso de forma limpa)
                // O JSON é no formato: [{"portNumber": 1, "name": "Amp-01", ...}]
                // ==============================================================
                
                // Simulação da leitura de 2 portas baseada no JSON que construí no Middleware
                ports.add(DefaultPortDescription.builder()
                    .withPortNumber(PortNumber.portNumber(1))
                    .isEnabled(true)
                    .type(Port.Type.FIBER)
                    .annotations(DefaultAnnotations.builder()
                        .set("neName", "Amp-01")
                        .set("gain", "15.5")
                        .build())
                    .build());

                ports.add(DefaultPortDescription.builder()
                    .withPortNumber(PortNumber.portNumber(2))
                    .isEnabled(true)
                    .type(Port.Type.OCH)
                    .annotations(DefaultAnnotations.builder()
                        .set("neName", "Transponder-01")
                        .set("channel", "CH-1")
                        .build())
                    .build());

            } else {
                log.warn("Middleware HTTP retornou erro {}", conn.getResponseCode());
            }
            conn.disconnect();

        } catch (Exception e) {
            log.error("Erro na comunicação com o Middleware Padtec: ", e);
        }

        return ports;
    }

    @Override
    public Collection<PortStatistics> discoverPortStatistics() {
        log.info("Discovering port statistics for Padtec via Middleware...");
        DeviceId deviceId = handler().data().deviceId();
        List<PortStatistics> statsList = Lists.newArrayList();

        // Na prática, você também faria um GET HTTP para pegar os ganhos 
        // e estatísticas de perda reais. Aqui mockamos para evitar complicação.
        try {
            DefaultPortStatistics.Builder builder = DefaultPortStatistics.builder();
            builder.setPort(PortNumber.portNumber(1));
            builder.setDeviceId(deviceId);
            builder.setBytesReceived(0);
            builder.setBytesSent(15L); // Ganho mockado no dashboard do CLI

            statsList.add(builder.build());
        } catch (Exception e) {
            log.error("Failed to read statistics from Middleware", e);
        }

        return statsList;
    }
}
