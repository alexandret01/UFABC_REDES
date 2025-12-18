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

import org.onosproject.net.MastershipRole;
import org.onosproject.net.device.DeviceAgentListener;
import org.onosproject.net.device.DeviceHandshaker;
import org.onosproject.net.driver.DriverHandler;
import org.onosproject.net.driver.HandlerBehaviour;
import org.onosproject.net.provider.ProviderId;

import java.util.concurrent.CompletableFuture;

/**
 * Implementation of DeviceHandshaker for gNMI.
 */
public class GnmiHandshaker implements DeviceHandshaker, HandlerBehaviour {

    private DriverHandler handler;

    @Override
    public DriverHandler handler() {
        return handler;
    }

    @Override
    public void setHandler(DriverHandler handler) {
        this.handler = handler;
    }

    @Override
    public boolean isReachable() {
        return true;
    }

    @Override
    public CompletableFuture<Boolean> probeReachability() {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public CompletableFuture<Boolean> probeAvailability() {
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public void roleChanged(MastershipRole newRole) {
        // Not handled
    }

    @Override
    public MastershipRole getRole() {
        return MastershipRole.MASTER;
    }

    @Override
    public void addDeviceAgentListener(ProviderId providerId, DeviceAgentListener listener) {
        // Not handled
    }

    @Override
    public void removeDeviceAgentListener(ProviderId providerId) {
        // Not handled
    }

    @Override
    public boolean connect() {
        // Not handled
        return false;
    }

    @Override
    public void disconnect() {
        // Not handled
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public boolean hasConnection() {
        return true;
    }
}
