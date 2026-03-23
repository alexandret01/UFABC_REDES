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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

// Imports Simulados/Nativos do Jaquison
import br.com.padtec.v3.data.ne.NE;
import br.com.padtec.v3.data.ne.Amplifier;
import br.com.padtec.v3.data.ne.Transponder;
import br.ufabc.equipment.Supervisor;
import br.ufabc.equipment.Amplifiers;
import br.ufabc.equipment.Transponders;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Driver implementation for Padtec devices integrating Jaquison native logic.
 */
public class PadtecDeviceDescription extends AbstractHandlerBehaviour 
        implements DeviceDescriptionDiscovery, PortStatisticsDiscovery {

    private final Logger log = getLogger(getClass());

    @Override
    public DeviceDescription discoverDeviceDetails() {
        log.info("Discovering Padtec device details via Jaquison logic...");
        DeviceId deviceId = handler().data().deviceId();
        
        return new DefaultDeviceDescription(
                deviceId.uri(),
                Device.Type.TERMINAL_DEVICE,
                "Padtec",
                "SPVL4",
                "1.0",
                "Jaquison-Integrated",
                new ChassisId(),
                true,
                DefaultAnnotations.EMPTY);
    }

    @Override
    public List<PortDescription> discoverPortDetails() {
        log.info("Discovering ports on Padtec device via Jaquison logic...");
        DeviceId deviceId = handler().data().deviceId();
        String ip = deviceId.uri().getSchemeSpecificPart();
        log.info("Connecting to Padtec Supervisor at IP: {}", ip);

        List<PortDescription> ports = Lists.newArrayList();

        try {
            Supervisor sup = new Supervisor(ip, Supervisor.TypeSupervisor.SPVL);
            sup.start();
            Thread.sleep(2000); 

            ArrayList<NE> monitored = new ArrayList<>();
            if (!Amplifiers.getAmplifiers(sup).isEmpty()) {
                monitored.addAll(Amplifiers.getAmplifiers(sup));
            }
            if (!Transponders.getTransponders(sup).isEmpty()) {
                monitored.addAll(Transponders.getTransponders(sup));
            }

            long portCounter = 1;

            if (monitored.size() > 0) {
                for (NE ne : monitored) {
                    if (ne instanceof Amplifier) {
                        Amplifier amp = (Amplifier) ne;
                        Amplifiers amplifier = new Amplifiers(sup, amp);
                        
                        ports.add(DefaultPortDescription.builder()
                            .withPortNumber(PortNumber.portNumber(portCounter++))
                            .isEnabled(true)
                            .type(Port.Type.FIBER)
                            .annotations(DefaultAnnotations.builder()
                                .set("neName", amp.getName())
                                .set("gain", String.valueOf(amplifier.getGain()))
                                .build())
                            .build());

                    } else if (ne instanceof Transponder) {
                        Transponder transp = (Transponder) ne;
                        Transponders transponder = new Transponders(sup, transp);
                        
                        ports.add(DefaultPortDescription.builder()
                            .withPortNumber(PortNumber.portNumber(portCounter++))
                            .isEnabled(!transponder.isLOS()) // Loss of Signal
                            .type(Port.Type.OCH)
                            .annotations(DefaultAnnotations.builder()
                                .set("neName", transp.getName())
                                .set("channel", transponder.getChannel())
                                .build())
                            .build());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error communicating with Padtec equipment via Jaquison: ", e);
        }

        return ports;
    }

    @Override
    public Collection<PortStatistics> discoverPortStatistics() {
        log.info("Discovering port statistics for Padtec...");
        DeviceId deviceId = handler().data().deviceId();
        String ip = deviceId.uri().getSchemeSpecificPart();
        List<PortStatistics> statsList = Lists.newArrayList();

        try {
            Supervisor sup = new Supervisor(ip, Supervisor.TypeSupervisor.SPVL);
            sup.start();
            
            ArrayList<NE> monitored = new ArrayList<>();
            if (!Amplifiers.getAmplifiers(sup).isEmpty()) {
                monitored.addAll(Amplifiers.getAmplifiers(sup));
            }

            long portCounter = 1;

            if (monitored.size() > 0) {
                for (NE ne : monitored) {
                    if (ne instanceof Amplifier) {
                        Amplifier amp = (Amplifier) ne;
                        Amplifiers amplifier = new Amplifiers(sup, amp);
                        
                        // Mapeia o ganho/power para PortStatistics (bytes enviados/recebidos é irrelevante em amplificador analógico)
                        // ONOS lida com isso em DefaultPortStatistics
                        DefaultPortStatistics.Builder builder = DefaultPortStatistics.builder();
                        builder.setPort(PortNumber.portNumber(portCounter));
                        builder.setDeviceId(deviceId);
                        builder.setBytesReceived(0);
                        builder.setBytesSent((long) amplifier.getGain()); // Hack visual para exibir o ganho no CLI se não houver GUI Optical

                        statsList.add(builder.build());
                    }
                    portCounter++;
                }
            }
        } catch (Exception e) {
            log.error("Failed to read statistics from Padtec", e);
        }

        return statsList;
    }
}
