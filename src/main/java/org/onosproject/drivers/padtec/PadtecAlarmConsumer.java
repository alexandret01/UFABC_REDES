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

import com.fasterxml.jackson.core.JsonParser;
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

import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static org.onosproject.alarm.Alarm.SeverityLevel;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Padtec specific implementation to provide a list of current alarms via TCP Agent.
 * Gera alarme CRITICAL para cada equipamento com isLOS=true (perda de sinal óptico).
 */
public class PadtecAlarmConsumer extends AbstractHandlerBehaviour implements AlarmConsumer {

    private final Logger log = getLogger(getClass());

    private static final String AGENT_IP   = "127.0.0.1";
    private static final int    AGENT_PORT = 10151;

    /** Substitui NaN por null antes de parsear (NaN não é JSON padrão). */
    private static final Pattern NAN_PATTERN = Pattern.compile(":\\s*NaN");

    @Override
    public List<Alarm> consumeAlarms() {
        DriverHandler handler = handler();
        DeviceId deviceId = handler.data().deviceId();

        log.debug("Consumindo alarmes para Padtec device {}...", deviceId);

        List<Alarm> alarms = new ArrayList<>();

        try (Socket socket = new Socket(AGENT_IP, AGENT_PORT);
             InputStream in = socket.getInputStream()) {

            // Lê todo o stream
            byte[] buf = new byte[8192];
            int n;
            StringBuilder sb = new StringBuilder();
            while ((n = in.read(buf)) != -1) {
                sb.append(new String(buf, 0, n, StandardCharsets.UTF_8));
            }

            String raw = sb.toString().trim();
            if (raw.isEmpty() || "{}".equals(raw)) {
                return Collections.unmodifiableList(alarms);
            }

            // Pegar apenas o primeiro bloco JSON (o agente envia dois blocos separados por \n\n)
            String jsonStr = raw.contains("\n\n") ? raw.split("\n\n")[0] : raw;

            // Substituir NaN por null para parsing válido
            jsonStr = NAN_PATTERN.matcher(jsonStr).replaceAll(": null");

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
            JsonNode root = mapper.readTree(jsonStr);

            // Suporta array na raiz ou objeto com "devices"
            JsonNode devicesNode = root.isArray() ? root : root.path("devices");

            if (!devicesNode.isArray()) {
                log.warn("Alarmes: JSON do agente sem campo 'devices'.");
                return Collections.unmodifiableList(alarms);
            }

            for (JsonNode deviceNode : devicesNode) {
                String neName = deviceNode.path("name").asText("Unknown");
                String type   = deviceNode.path("type").asText("Unknown");
                JsonNode metrics = deviceNode.path("metrics");

                boolean isLOS = metrics.path("isLOS").asBoolean(false);

                if (isLOS) {
                    log.warn("LOS detectado no equipamento {} ({})", neName, type);

                    alarms.add(new DefaultAlarm.Builder(
                            AlarmId.alarmId(deviceId, "LOS_" + neName),
                            deviceId,
                            "Loss of Signal no equipamento " + neName + " (tipo: " + type + ")",
                            SeverityLevel.CRITICAL,
                            System.currentTimeMillis()
                    ).build());
                }
            }

            if (alarms.isEmpty()) {
                log.debug("Nenhum alarme LOS ativo no Padtec.");
            } else {
                log.info("{} alarme(s) LOS ativo(s) no Padtec.", alarms.size());
            }

        } catch (Exception e) {
            log.error("Erro ao consumir alarmes do agente Padtec ({}:{}): {}",
                      AGENT_IP, AGENT_PORT, e.getMessage());
        }

        return Collections.unmodifiableList(alarms);
    }
}
