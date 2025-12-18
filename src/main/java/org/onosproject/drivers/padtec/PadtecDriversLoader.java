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

import com.google.common.collect.ImmutableList;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.onosproject.net.driver.AbstractDriverLoader;
import org.onosproject.net.optical.OpticalDevice;
import org.onosproject.ui.UiGlyph;
import org.onosproject.ui.UiGlyphFactory;
import org.onosproject.ui.UiExtensionService;

/**
 * Loader for Padtec device drivers.
 */
@Component(immediate = true)
public class PadtecDriversLoader extends AbstractDriverLoader {

    // OSGI: help bundle plugin discover runtime package dependency.
    @SuppressWarnings("unused")
    private OpticalDevice optical;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected UiExtensionService uiExtensionService;

    private UiGlyphFactory glyphFactory =
        () -> ImmutableList.of(
            new UiGlyph("policon", "0 0 64 64",
            		"m 29.127274,38.454544 h 10.8 L 40.09091,63.490907 29.127274,63.327271 Z"
            		+ "m 32.072727,43.036362 h 5.236364 v 3.927272 h -5.236364 z")
            );

    public PadtecDriversLoader() {
        super("/padtec-drivers.xml");
    }

    @Activate
    @Override
    protected void activate() {
        uiExtensionService.register(glyphFactory);
        super.activate();
    }

    @Deactivate
    @Override
    protected void deactivate() {
        uiExtensionService.unregister(glyphFactory);
        super.deactivate();
    }
}
