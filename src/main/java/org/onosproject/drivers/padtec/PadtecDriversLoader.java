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
import org.onosproject.core.CoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * Loader for Padtec device drivers.
 */
@Component(immediate = true)
public class PadtecDriversLoader extends AbstractDriverLoader {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected DriverAdminService driverAdminServiceLocal;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected CoreService coreServiceLocal;

    public PadtecDriversLoader() {
        super("/padtec-drivers.xml");
    }

    @Activate
    @Override
    protected void activate() {
        log.info("Ativando PadtecDriversLoader...");
        injectSuperclassField("coreService", coreServiceLocal);
        injectSuperclassField("driverAdminService", driverAdminServiceLocal);
        
        try {
            super.activate();
            log.info("PadtecDriversLoader ativado com sucesso via super.activate().");
        } catch (Exception e) {
            log.error("Erro no super.activate()", e);
            throw e;
        }
    }

    @Deactivate
    @Override
    protected void deactivate() {
        super.deactivate();
    }

    private void injectSuperclassField(String fieldName, Object value) {
        Class<?> clazz = getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(this, value);
                log.info("Injetado com sucesso o campo {} na classe {}", fieldName, clazz.getName());
                return;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                log.error("Falha ao injetar campo {}", fieldName, e);
                return;
            }
        }
        log.warn("Campo {} não encontrado em nenhuma superclasse!", fieldName);
    }
}
