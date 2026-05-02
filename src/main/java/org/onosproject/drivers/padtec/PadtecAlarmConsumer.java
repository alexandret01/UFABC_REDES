/*
 * Copyright 2018 Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import org.onosproject.alarm.Alarm;
import org.onosproject.alarm.AlarmConsumer;
import org.onosproject.alarm.AlarmId;
import org.onosproject.alarm.DefaultAlarm;
import org.onosproject.net.DeviceId;
import org.onosproject.net.driver.AbstractHandlerBehaviour;
import org.onosproject.net.driver.DriverHandler;

import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.onosproject.alarm.Alarm.SeverityLevel;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Padtec specific implementation to provide a list of current alarms via TCP Agent.
 */
public class PadtecAlarmConsumer extends AbstractHandlerBehaviour implements AlarmConsumer {
    private final Logger log = getLogger(getClass());

    private static final String AGENT_IP = "127.0.0.1";
    private static final int AGENT_PORT = 10151;

    @Override
    public List<Alarm> consumeAlarms() {
        DriverHandler handler = handler();
        DeviceId deviceId = handler.data().deviceId();
        
        log.info("Consumindo alarmes para Padtec device {}...", deviceId);

        List<Alarm> alarms = new ArrayList<>();

        try {
            Socket socket = new Socket(AGENT_IP, AGENT_PORT);
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            StringBuilder jsonResponse = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonResponse.append(line);
            }
            reader.close();
            socket.close();
            
            String json = jsonResponse.toString();
            
            if (json == null || json.trim().isEmpty() || "{}".equals(json.trim())) {
                return Collections.unmodifiableList(alarms);
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(json);
            
            JsonNode devicesNode = rootNode.path("devices");
            if (devicesNode.isArray()) {
                for (JsonNode deviceNode : devicesNode) {
                    JsonNode metrics = deviceNode.path("metrics");
                    
                    // Verifica se tem LOS (Loss of Signal) ativo
                    if (metrics.path("isLOS").asBoolean(false)) {
                         String neName = deviceNode.path("name").asText("Unknown");
                         log.warn("Detectado alarme de LOS (Loss of Signal) na porta/transponder {}!", neName);
                         
                         DefaultAlarm alarm = new DefaultAlarm.Builder(
                                 AlarmId.alarmId(deviceId, "LOS_ALARM_" + neName),
                                 deviceId,
                                 "Loss of Signal detectado no equipamento " + neName,
                                 SeverityLevel.CRITICAL,
                                 System.currentTimeMillis()
                         ).build();
                         
                         alarms.add(alarm);
                    }
                }
            }

        } catch (Exception e) {
            log.error("Erro na comunicação de alarmes TCP com o Agente Padtec: ", e);
        }

        return Collections.unmodifiableList(alarms);
    }
}
