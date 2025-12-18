/*
 * Copyright 2016-present Open Networking Foundation
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

import org.onosproject.net.ConnectPoint;
import org.onosproject.net.Device;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Link;
import org.onosproject.net.device.PortDescription;
import org.onosproject.net.PortNumber;
import org.onosproject.net.Port;
import org.onosproject.net.DefaultAnnotations;
import org.onosproject.net.behaviour.LinkDiscovery;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.driver.DriverData;
import org.onosproject.net.driver.DriverHandler;
import org.onosproject.net.driver.HandlerBehaviour;
import org.onosproject.net.link.DefaultLinkDescription;
import org.onosproject.net.link.LinkDescription;
import org.onosproject.netconf.NetconfController;
import org.onosproject.netconf.NetconfSession;
import org.slf4j.Logger;

import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


import static com.google.common.base.Preconditions.checkNotNull;
import static org.slf4j.LoggerFactory.getLogger;


/**
 * Reads peer-port fields from a Polatis switch, parses them and returns a set of LinkDescriptions.
 */
public class PadtecLinkDiscovery implements LinkDiscovery, HandlerBehaviour {

    private final Logger log = getLogger(getClass());

    private DriverHandler handler;


    /**
     * Constructor just used to initiate logging when LinkDiscovery behaviour is invoked.
     */
    public PadtecLinkDiscovery() {
        log.debug("Running PolatisLinkDiscovery handler");
    }

    /**
     * Returns the set of LinkDescriptions originating from a Polatis switch.
     * <p>
     * This is the callback required by the LinkDiscovery behaviour.
     * @return Set of outbound unidirectional links as LinkDescriptions
     */
    @Override
    public Set<LinkDescription> getLinks() {
        Set<LinkDescription> links = new HashSet<LinkDescription>();
        DeviceId deviceID = handler().data().deviceId();
        log.debug("*** Checking peer-port fields on device {}", deviceID.toString());
        DeviceService deviceService = checkNotNull(handler().get(DeviceService.class));
        Device device = deviceService.getDevice(deviceID);
        List<PortDescription> ports = new ArrayList<PortDescription>();
        for (PortDescription port : ports) {
            if (deviceService.getPort(new ConnectPoint(deviceID, port.portNumber())).isEnabled()) {
                String peerPortData = port.annotations().value("POLATIS");
                if (!peerPortData.equals("")) {
                    if (peerPortData.charAt(0) == '{') {
                        ConnectPoint nearEndCP = new ConnectPoint(deviceID, port.portNumber());
                        ConnectPoint farEndCP = new ConnectPoint(deviceID, port.portNumber());
                        // now add link to Set<LinkDescription>
                        DefaultAnnotations annotations = DefaultAnnotations.builder().build();
                        ConnectPoint aEndCP = nearEndCP;
                        ConnectPoint bEndCP = farEndCP;
                        LinkDescription newLinkDesc = new DefaultLinkDescription(aEndCP, bEndCP,
                        		Link.Type.OPTICAL, true, annotations);
                        links.add(newLinkDesc);
                        log.debug("Adding link {}", newLinkDesc);
                    }
                }
            }
        }
        log.trace("Links found on this iteration: {}", links);
        return links;
    }

    @Override
    public DriverHandler handler() {
        return handler;
    }

    @Override
    public void setHandler(DriverHandler handler) {
        this.handler = handler;
    }

    @Override
    public DriverData data() {
        return null;
    }

    @Override
    public void setData(DriverData data) {

    }
}
