/*
 * Copyright 2017 Open Networking Foundation
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

import com.google.common.collect.Range;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.onosproject.net.Direction;
import org.onosproject.net.OchSignal;
import org.onosproject.net.PortNumber;
import org.onosproject.net.behaviour.PowerConfig;
import org.onosproject.net.driver.DriverData;
import org.onosproject.net.driver.DriverHandler;
import org.onosproject.net.driver.HandlerBehaviour;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Get current or target port/channel power from a Polatis optical netconf device.
 * Set target port power or channel attenuation to an optical netconf device.
 */
public class PadtecPowerConfig<T> implements PowerConfig<T>, HandlerBehaviour {


    private static final Logger log = getLogger(PadtecPowerConfig.class);

    private DriverHandler handler;

    @Override
    public Optional<Double> getTargetPower(PortNumber port, T component) {
        Long power = 0L; //TODO
        if (power == null) {
            return Optional.empty();
        }
        return Optional.of(power.doubleValue());
    }

    @Override
    public void setTargetPower(PortNumber port, T component, double power) {
        if (component instanceof OchSignal) {
            log.warn("Channel power is not applicable.");
            return;
        }
        //TODO
    }

    @Override
    public Optional<Double> currentPower(PortNumber port, T component) {
        Long power = 0L;
        if (power == null) {
            return Optional.empty();
        }
        return Optional.of(power.doubleValue());
    }

    @Override
    public Optional<Range<Double>> getTargetPowerRange(PortNumber port, T component) {
        Range<Long> power =  null;
        if (power == null) {
            return Optional.empty();
        }
        return Optional.of(Range.closed((double) power.lowerEndpoint(), (double) power.upperEndpoint()));
    }

    @Override
    public Optional<Range<Double>> getInputPowerRange(PortNumber port, T component) {
        Range<Long> power = null;
        if (power == null) {
            return Optional.empty();
        }
        return Optional.of(Range.closed((double) power.lowerEndpoint(), (double) power.upperEndpoint()));
    }

    @Override
    public List<PortNumber> getPorts(T component) {
        if (component instanceof OchSignal) {
            log.warn("Channel component is not applicable.");
            return new ArrayList<PortNumber>();
        }
        log.debug("Get port config ports...");
        return null;
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
