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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gnmi.Gnmi;
import gnmi.Gnmi.GetRequest;
import gnmi.Gnmi.GetResponse;
import gnmi.Gnmi.Path;
import gnmi.Gnmi.PathElem;
import gnmi.Gnmi.Update;
import org.onosproject.gnmi.api.GnmiClient;
import org.onosproject.gnmi.api.GnmiController;
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
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Driver implementation for Padtec devices using gNMI (OpenConfig).
 */
public class PadtecDeviceDescription extends AbstractHandlerBehaviour 
        implements DeviceDescriptionDiscovery, PortStatisticsDiscovery {

    private final Logger log = getLogger(getClass());
    private static final int REQUEST_TIMEOUT_SECONDS = 5;

    @Override
    public DeviceDescription discoverDeviceDetails() {
        log.info("Discovering Padtec device details...");
        DeviceId deviceId = handler().data().deviceId();
        
        // Retorna descrição estática. Em produção, poderia ler /system/state/hostname, etc.
        return new DefaultDeviceDescription(
                deviceId.uri(),
                Device.Type.TERMINAL_DEVICE,
                "Padtec",
                "SPVL4",
                "1.0",
                "Unknown",
                new ChassisId(),
                true,
                DefaultAnnotations.EMPTY);
    }

    @Override
    public List<PortDescription> discoverPortDetails() {
        log.info("Discovering ports on Padtec device via gNMI...");
        GnmiClient client = getClient();
        if (client == null) {
            return Collections.emptyList();
        }

        // Request para /interfaces/interface
        GetRequest request = GetRequest.newBuilder()
                .addPath(buildPath("interfaces", "interface"))
                .setType(Gnmi.GetRequest.DataType.STATE)
                .setEncoding(Gnmi.Encoding.PROTO)
                .build();

        try {
            GetResponse response = client.get(request).get(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            return parsePortDescriptions(response);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Failed to discover ports for device {}", handler().data().deviceId(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public Collection<PortStatistics> discoverPortStatistics() {
        log.info("Discovering port statistics on Padtec device via gNMI...");
        GnmiClient client = getClient();
        if (client == null) {
            return Collections.emptyList();
        }

        // Request para /interfaces/interface/.../state/counters
        GetRequest request = GetRequest.newBuilder()
                .addPath(buildPath("interfaces", "interface", "...", "state", "counters"))
                .setType(Gnmi.GetRequest.DataType.STATE)
                .setEncoding(Gnmi.Encoding.PROTO)
                .build();

        try {
            GetResponse response = client.get(request).get(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            return parsePortStatistics(response);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Failed to discover port statistics for device {}", handler().data().deviceId(), e);
            return Collections.emptyList();
        }
    }

    private GnmiClient getClient() {
        GnmiController controller = handler().get(GnmiController.class);
        DeviceId deviceId = handler().data().deviceId();
        GnmiClient client = controller.get(deviceId);
        if (client == null) {
            log.warn("Cannot get gNMI client for device {}", deviceId);
        }
        return client;
    }

    // --- Parsing Logic ---

    private List<PortDescription> parsePortDescriptions(GetResponse response) {
        Map<String, DefaultPortDescription.Builder> ports = Maps.newHashMap();

        for (Gnmi.Notification notification : response.getNotificationList()) {
            for (Update update : notification.getUpdateList()) {
                Path path = update.getPath();
                // Esperado: interfaces/interface[name=...]/state/...
                if (path.getElemCount() < 2) continue;

                String ifName = path.getElem(1).getKeyOrDefault("name", null);
                if (ifName == null) continue;

                ports.putIfAbsent(ifName, DefaultPortDescription.builder());
                DefaultPortDescription.Builder builder = ports.get(ifName);
                
                // Tenta extrair número da porta do nome (ex: Ethernet1/1 -> 101) ou usa hash
                PortNumber portNumber = PortNumber.portNumber(getPortNumberFromName(ifName));
                builder.withPortNumber(portNumber);

                String leaf = path.getElem(path.getElemCount() - 1).getName();
                
                if (leaf.equals("oper-status")) {
                    String status = update.getVal().getStringVal().toUpperCase();
                    builder.isEnabled(status.equals("UP"));
                } else if (leaf.equals("port-speed")) {
                    // Lógica simplificada para velocidade
                    builder.portSpeed(10000); // Default 10G
                }
            }
        }
        
        List<PortDescription> descriptions = Lists.newArrayList();
        ports.forEach((name, builder) -> descriptions.add(builder.type(Port.Type.FIBER).build()));
        return descriptions;
    }

    private Collection<PortStatistics> parsePortStatistics(GetResponse response) {
        Map<PortNumber, DefaultPortStatistics.Builder> statsMap = Maps.newHashMap();

        for (Gnmi.Notification notification : response.getNotificationList()) {
            for (Update update : notification.getUpdateList()) {
                Path path = update.getPath();
                // Esperado: interfaces/interface[name=...]/state/counters/...
                if (path.getElemCount() < 2) continue;

                String ifName = path.getElem(1).getKeyOrDefault("name", null);
                if (ifName == null) continue;

                PortNumber portNumber = PortNumber.portNumber(getPortNumberFromName(ifName));
                statsMap.putIfAbsent(portNumber, DefaultPortStatistics.builder());
                DefaultPortStatistics.Builder builder = statsMap.get(portNumber);
                builder.setPort(portNumber);
                builder.setDeviceId(handler().data().deviceId());

                String counterName = path.getElem(path.getElemCount() - 1).getName();
                long value = update.getVal().getUintVal();

                switch (counterName) {
                    case "in-octets": builder.setBytesReceived(value); break;
                    case "out-octets": builder.setBytesSent(value); break;
                    case "in-pkts": builder.setPacketsReceived(value); break;
                    case "out-pkts": builder.setPacketsSent(value); break;
                    case "in-errors": builder.setPacketsRxDropped(value); break; // Mapping errors to dropped for simplicity
                    case "out-errors": builder.setPacketsTxDropped(value); break;
                }
            }
        }

        List<PortStatistics> stats = Lists.newArrayList();
        statsMap.values().forEach(b -> stats.add(b.build()));
        return stats;
    }

    // Helper simples para gerar número de porta a partir de string
    private long getPortNumberFromName(String name) {
        // Tenta extrair dígitos finais
        try {
            return Long.parseLong(name.replaceAll("\\D+", ""));
        } catch (NumberFormatException e) {
            return name.hashCode() & 0xFFFFFFFFL;
        }
    }

    private Path buildPath(String... elems) {
        Path.Builder builder = Path.newBuilder();
        for (String elem : elems) {
            builder.addElem(PathElem.newBuilder().setName(elem).build());
        }
        return builder.build();
    }
}
