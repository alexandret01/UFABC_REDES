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

import org.onosproject.alarm.Alarm;
import org.onosproject.alarm.AlarmConsumer;
import org.onosproject.alarm.AlarmId;
import org.onosproject.alarm.DefaultAlarm;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Port;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.driver.AbstractHandlerBehaviour;
import org.onosproject.net.driver.DriverHandler;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.onosproject.alarm.Alarm.SeverityLevel;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Padtec alarm consumer — lê anotações de porta já publicadas no ONOS
 * (populadas por PadtecDeviceProvider) e gera alarmes para cada condição
 * de falha detectada.
 *
 * Condições monitoradas:
 *   isLOS=true        → CRITICAL  (Loss of Signal — sem luz na fibra WDM)
 *   isLOF=true        → MAJOR     (Loss of Frame — OTN frame sync perdida)
 *   isBDI=true        → MAJOR     (Backward Defect Indicator — falha reportada pelo far-end)
 *   isClientLOS=true  → MAJOR     (Loss of Signal na interface cliente)
 *   isClientLOF=true  → MINOR     (Loss of Frame na interface cliente)
 *   fecRate alto      → WARNING   (Taxa de erro FEC acima do limiar)
 *
 * Lê dados das anotações de porta (DeviceService) em vez de abrir uma
 * conexão TCP separada ao agente — os dados já estão no ONOS.
 */
public class PadtecAlarmConsumer extends AbstractHandlerBehaviour implements AlarmConsumer {

    private final Logger log = getLogger(getClass());

    /** Taxa de erro FEC (BER) acima deste valor gera WARNING. */
    private static final double FEC_RATE_THRESHOLD = 1e-4;

    @Override
    public List<Alarm> consumeAlarms() {
        DriverHandler handler  = handler();
        DeviceId      deviceId = handler.data().deviceId();

        log.debug("Consumindo alarmes para Padtec device {}...", deviceId);

        List<Alarm> alarms = new ArrayList<>();

        try {
            DeviceService deviceService = handler.get(DeviceService.class);
            List<Port> ports = deviceService.getPorts(deviceId);

            if (ports == null || ports.isEmpty()) {
                log.debug("Nenhuma porta encontrada para {}. Aguardando próximo ciclo.", deviceId);
                return Collections.emptyList();
            }

            for (Port port : ports) {
                String neName = port.annotations().value("neName");
                String type   = port.annotations().value("type");

                if (neName == null || neName.isEmpty()) {
                    continue; // porta ainda não populada
                }

                // ----------------------------------------------------------
                // isLOS → CRITICAL
                // ----------------------------------------------------------
                if (isTrue(port, "isLOS")) {
                    alarms.add(buildAlarm(deviceId, "LOS_" + neName,
                            "Loss of Signal no equipamento " + neName + " (tipo: " + type + ")",
                            SeverityLevel.CRITICAL));
                    log.warn("[CRITICAL] LOS em {} ({})", neName, type);
                }

                // ----------------------------------------------------------
                // isLOF → MAJOR
                // ----------------------------------------------------------
                if (isTrue(port, "isLOF")) {
                    alarms.add(buildAlarm(deviceId, "LOF_" + neName,
                            "Loss of Frame no equipamento " + neName,
                            SeverityLevel.MAJOR));
                    log.warn("[MAJOR] LOF em {}", neName);
                }

                // ----------------------------------------------------------
                // isBDI → MAJOR
                // ----------------------------------------------------------
                if (isTrue(port, "isBDI")) {
                    alarms.add(buildAlarm(deviceId, "BDI_" + neName,
                            "Backward Defect Indicator ativo em " + neName
                                    + " (falha reportada pelo far-end)",
                            SeverityLevel.MAJOR));
                    log.warn("[MAJOR] BDI em {}", neName);
                }

                // ----------------------------------------------------------
                // isClientLOS → MAJOR
                // ----------------------------------------------------------
                if (isTrue(port, "isClientLOS")) {
                    alarms.add(buildAlarm(deviceId, "CLIENT_LOS_" + neName,
                            "Loss of Signal na interface cliente de " + neName,
                            SeverityLevel.MAJOR));
                    log.warn("[MAJOR] Client LOS em {}", neName);
                }

                // ----------------------------------------------------------
                // isClientLOF → MINOR
                // ----------------------------------------------------------
                if (isTrue(port, "isClientLOF")) {
                    alarms.add(buildAlarm(deviceId, "CLIENT_LOF_" + neName,
                            "Loss of Frame na interface cliente de " + neName,
                            SeverityLevel.MINOR));
                    log.warn("[MINOR] Client LOF em {}", neName);
                }

                // ----------------------------------------------------------
                // FEC rate alto → WARNING
                // ----------------------------------------------------------
                String fecRateStr = port.annotations().value("fecRate");
                if (fecRateStr != null && !"N/A".equals(fecRateStr) && !fecRateStr.isEmpty()) {
                    try {
                        double fecRate = Double.parseDouble(fecRateStr);
                        if (!Double.isNaN(fecRate) && fecRate > FEC_RATE_THRESHOLD) {
                            alarms.add(buildAlarm(deviceId, "FEC_HIGH_" + neName,
                                    String.format("Taxa de erro FEC alta em %s: %.2e (limiar: %.0e)",
                                                  neName, fecRate, FEC_RATE_THRESHOLD),
                                    SeverityLevel.WARNING));
                            log.warn("[WARNING] FEC rate alto em {}: {}", neName, fecRate);
                        }
                    } catch (NumberFormatException ignored) {
                        // valor inválido — ignora
                    }
                }
            }

            if (alarms.isEmpty()) {
                log.debug("Nenhum alarme ativo no Padtec.");
            } else {
                log.info("{} alarme(s) ativo(s) no Padtec.", alarms.size());
            }

        } catch (Exception e) {
            log.error("Erro ao consumir alarmes do Padtec {}: {}", deviceId, e.getMessage());
        }

        return Collections.unmodifiableList(alarms);
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    /** Retorna true se a anotação da porta tiver o valor "true". */
    private boolean isTrue(Port port, String annotation) {
        return "true".equalsIgnoreCase(port.annotations().value(annotation));
    }

    /** Cria um DefaultAlarm com os campos padrão. */
    private Alarm buildAlarm(DeviceId deviceId, String alarmKey,
                             String description, SeverityLevel severity) {
        return new DefaultAlarm.Builder(
                AlarmId.alarmId(deviceId, alarmKey),
                deviceId,
                description,
                severity,
                System.currentTimeMillis()
        ).build();
    }
}
