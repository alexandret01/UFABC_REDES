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

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.onosproject.net.driver.AbstractDriverLoader;
import org.onosproject.net.driver.DriverAdminService;

/**
 * Loader for Padtec device drivers.
 */
@Component(immediate = true)
public class PadtecDriversLoader extends AbstractDriverLoader {

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected DriverAdminService myDriverAdminService;

    public PadtecDriversLoader() {
        super("/padtec-drivers.xml");
    }

    @Activate
    @Override
    protected void activate() {
        // Injeta a dependência na classe pai para evitar NullPointerException
        super.driverAdminService = this.myDriverAdminService;
        super.activate();
    }

    @Deactivate
    @Override
    protected void deactivate() {
        super.deactivate();
    }
}
