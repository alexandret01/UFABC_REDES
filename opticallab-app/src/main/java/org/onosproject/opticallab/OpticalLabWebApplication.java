package org.onosproject.opticallab;

import org.onlab.rest.AbstractWebApplication;

import java.util.Set;

/**
 * Registra os recursos JAX-RS do Optical Lab Monitor no Jersey.
 *
 * Usando AbstractWebApplication (padrão ONOS 2.7) — instancia os recursos
 * diretamente sem depender de OSGi service lookup (BundleContextUtils), o que
 * requer pax-web-extender-war funcionando corretamente.
 */
public class OpticalLabWebApplication extends AbstractWebApplication {
    @Override
    public Set<Class<?>> getClasses() {
        return getClasses(OpticalLabWebResource.class);
    }
}
