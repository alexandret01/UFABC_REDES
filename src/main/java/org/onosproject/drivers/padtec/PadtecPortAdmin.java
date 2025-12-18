/*
 * Copyright 2018-present Open Networking Foundation
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

import org.onosproject.net.PortNumber;
import org.onosproject.net.behaviour.PortAdmin;
import org.onosproject.net.driver.DriverData;
import org.onosproject.net.driver.DriverHandler;
import org.onosproject.net.driver.HandlerBehaviour;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;

import static org.slf4j.LoggerFactory.getLogger;



/**
 * Handles port administration for Polatis switches using NETCONF.
 */
public class PadtecPortAdmin implements PortAdmin, HandlerBehaviour {

    public static final Logger log = getLogger(PadtecPortAdmin.class);

    private DriverHandler handler;


    /**
     * Sets the administrative state of the given port to the given value.
     *
     * @param portNumber Port number
     * @param state      State, PC_ENABLED or PC_DISABLED
     * @return           True if successfully set
     */
    private CompletableFuture<Boolean> setAdminState(PortNumber portNumber, String state) {

        boolean result = false;
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletableFuture<Boolean> enable(PortNumber portNumber) {
        return setAdminState(portNumber, "PORT_ENABLED");
    }

    @Override
    public CompletableFuture<Boolean> disable(PortNumber portNumber) {
        return setAdminState(portNumber, "PORT_DISABLED");
    }

    @Override
    public CompletableFuture<Boolean> isEnabled(PortNumber portNumber) {
        boolean result = false;
        return CompletableFuture.completedFuture(result);
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
