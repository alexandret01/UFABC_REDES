/*
 * Copyright 2017-present Open Networking Foundation
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

import org.apache.commons.configuration.HierarchicalConfiguration;
import com.google.common.collect.ImmutableList;
import org.onosproject.net.DefaultAnnotations;
import org.onosproject.net.Device;
import org.onosproject.net.Device.Type;
import org.onosproject.net.DeviceId;
import org.onosproject.net.device.DefaultDeviceDescription;
import org.onosproject.net.device.DeviceDescription;
import org.onosproject.net.device.DeviceDescriptionDiscovery;
import org.onosproject.net.device.DefaultPortStatistics;
import org.onosproject.net.device.PortStatistics;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.device.PortDescription;
import org.onosproject.net.device.PortStatisticsDiscovery;
import org.onosproject.net.driver.AbstractHandlerBehaviour;

import org.onlab.packet.ChassisId;

import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Representation of device information and ports via NETCONF for all Polatis
 * optical circuit switches.
 */
public class PadtecDeviceDescription extends AbstractHandlerBehaviour
    implements DeviceDescriptionDiscovery, PortStatisticsDiscovery  {

    private final Logger log = getLogger(getClass());

    /**
     * Discovers device details, for padtec device by getting the system
     * information.
     *
     * @return device description
     */
    @Override
    public DeviceDescription discoverDeviceDetails() {
        log.debug("Discovering Polatis device detais...");
        return parseProductInformation();
    }

    private DeviceDescription parseProductInformation() {
        DeviceId devID = handler().data().deviceId();
        DefaultAnnotations annotations = DefaultAnnotations.builder().build();
        return new DefaultDeviceDescription(devID.uri(), Type.OLT,
                "Padtec", "HWVERSION", "SWVERSION", "SERIALNUMBER",
                new ChassisId(), true, annotations);
    }

    /**
     * Discovers port details, for polatis device.
     *
     * @return port list
     */
    @Override
    public List<PortDescription> discoverPortDetails() {
        log.debug("Discovering ports on Polatis switch...");
        DeviceService deviceService = handler().get(DeviceService.class);
        DeviceId deviceID = handler().data().deviceId();
        Device device = deviceService.getDevice(deviceID);
        List<PortDescription> descriptions = null;
        return descriptions;
    }
    
	@Override
    public Collection<PortStatistics> discoverPortStatistics() {
        List<PortStatistics> stats = new ArrayList<PortStatistics>();

        return stats;
    }

	
}
