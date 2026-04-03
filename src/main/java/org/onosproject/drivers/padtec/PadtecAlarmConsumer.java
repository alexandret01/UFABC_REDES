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

import com.google.common.collect.ImmutableList;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.onosproject.alarm.Alarm.SeverityLevel;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Padtec specific implementation to provide a list of current alarms via Middleware.
 */
public class PadtecAlarmConsumer extends AbstractHandlerBehaviour implements AlarmConsumer {
    private final Logger log = getLogger(getClass());

    // Mesmo IP do Middleware que usamos em PadtecDeviceDescription
    private static final String MIDDLEWARE_URL = "http://localhost:8080/api/padtec/ports";

    @Override
    public List<Alarm> consumeAlarms() {
        DriverHandler handler = handler();
        DeviceId deviceId = handler.data().deviceId();
        String ip = deviceId.uri().getSchemeSpecificPart();
        
        log.info("Consumindo alarmes para Padtec device {}...", deviceId);

        List<Alarm> alarms = new ArrayList<>();

        // Como o Middleware ainda não tem uma rota específica de Alarmes (ex: /api/padtec/alarms),
        // Vamos aproveitar a mesma requisição das portas para extrair alarmes de LOS (Loss of Signal)
        try {
            URL url = new URL(MIDDLEWARE_URL + "?ip=" + ip);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000); 
            
            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder jsonResponse = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonResponse.append(line);
                }
                reader.close();
                
                String json = jsonResponse.toString();
                
                // Parsing super simples (simulando a detecção de uma porta "enabled": false que indica LOS)
                if (json.contains("\"enabled\": false") || json.contains("\"enabled\":false")) {
                     log.warn("Detectado alarme de LOS (Loss of Signal) no JSON!");
                     
                     // Criando um alarme crítico no ONOS
                     DefaultAlarm alarm = new DefaultAlarm.Builder(
                             AlarmId.alarmId(deviceId, "LOS_ALARM"),
                             deviceId,
                             "Loss of Signal detectado no equipamento",
                             SeverityLevel.CRITICAL,
                             System.currentTimeMillis()
                     ).build();
                     
                     alarms.add(alarm);
                }

            } else {
                log.warn("Falha ao buscar alarmes do Middleware. HTTP code: {}", conn.getResponseCode());
            }
            conn.disconnect();

        } catch (Exception e) {
            log.error("Erro na comunicação de alarmes com o Middleware Padtec: ", e);
        }

        return ImmutableList.copyOf(alarms);
    }
}
