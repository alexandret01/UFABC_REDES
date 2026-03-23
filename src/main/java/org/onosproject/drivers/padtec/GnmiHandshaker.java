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
import org.onosproject.net.driver.AbstractHandlerBehaviour;
import org.onosproject.net.provider.ProviderId;
import org.onosproject.net.DeviceId;

import java.util.concurrent.CompletableFuture;

/**
 * Implementation of DeviceHandshaker for Padtec (Jaquison Integration).
 */
public class GnmiHandshaker extends AbstractHandlerBehaviour implements DeviceHandshaker {

    @Override
    public boolean isReachable() {
        // Na integração do Jaquison, vamos considerar sempre alcançável inicialmente,
        // ou você poderia implementar um Ping pro IP aqui.
        return true; 
    }

    @Override
    public CompletableFuture<Boolean> probeReachability() {
        return CompletableFuture.completedFuture(isReachable());
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
    public void roleChanged(MastershipRole newRole) {}

    @Override
    public MastershipRole getRole() {
        return MastershipRole.MASTER;
    }

    @Override
    public void addDeviceAgentListener(ProviderId providerId, DeviceAgentListener listener) {}

    @Override
    public void removeDeviceAgentListener(ProviderId providerId) {}

    @Override
    public boolean connect() {
        return true;
    }

    @Override
    public void disconnect() {}

    @Override
    public boolean hasConnection() {
        return true;
    }
}
