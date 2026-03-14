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

import org.onosproject.gnmi.api.GnmiClient;
import org.onosproject.gnmi.api.GnmiController;
import org.onosproject.net.MastershipRole;
import org.onosproject.net.device.DeviceAgentListener;
import org.onosproject.net.device.DeviceHandshaker;
import org.onosproject.net.driver.AbstractHandlerBehaviour;
import org.onosproject.net.provider.ProviderId;
import org.onosproject.net.DeviceId;

import java.util.concurrent.CompletableFuture;

/**
 * Implementation of DeviceHandshaker for gNMI.
 */
public class GnmiHandshaker extends AbstractHandlerBehaviour implements DeviceHandshaker {

    @Override
    public boolean isReachable() {
        final GnmiClient client = getClient();
        return client != null && client.isServerReachable();
    }

    @Override
    public CompletableFuture<Boolean> probeReachability() {
        final GnmiClient client = getClient();
        if (client == null) {
            return CompletableFuture.completedFuture(false);
        }
        return CompletableFuture.completedFuture(client.isServerReachable());
    }

    @Override
    public boolean isAvailable() {
        return isReachable();
    }

    @Override
    public CompletableFuture<Boolean> probeAvailability() {
        return probeReachability();
    }

    @Override
    public void roleChanged(MastershipRole newRole) {
        throw new UnsupportedOperationException("Mastership operation not supported");
    }

    @Override
    public MastershipRole getRole() {
        throw new UnsupportedOperationException("Mastership operation not supported");
    }

    @Override
    public void addDeviceAgentListener(ProviderId providerId, DeviceAgentListener listener) {
        throw new UnsupportedOperationException("Device agent listener not supported");
    }

    @Override
    public void removeDeviceAgentListener(ProviderId providerId) {
        throw new UnsupportedOperationException("Device agent listener not supported");
    }

    @Override
    public boolean connect() {
        return getClient() != null;
    }

    @Override
    public void disconnect() {
        // Connection is managed by the controller
    }

    @Override
    public boolean hasConnection() {
        return getClient() != null;
    }

    private GnmiClient getClient() {
        GnmiController controller = handler().get(GnmiController.class);
        DeviceId deviceId = handler().data().deviceId();
        return controller.get(deviceId);
    }
}
