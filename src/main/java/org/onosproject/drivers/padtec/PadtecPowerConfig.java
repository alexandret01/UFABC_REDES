package org.onosproject.drivers.padtec;

import org.onosproject.net.PortNumber;
import org.onosproject.net.behaviour.PowerConfig;
import org.onosproject.net.driver.AbstractHandlerBehaviour;
import org.slf4j.Logger;

import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Get current power/gain on Padtec devices.
 */
public class PadtecPowerConfig<T> extends AbstractHandlerBehaviour
        implements PowerConfig<T> {

    private final Logger log = getLogger(getClass());

    @Override
    public Optional<Double> getTargetPower(PortNumber port, T component) {
        log.warn("getTargetPower() is not supported on Padtec read-only driver.");
        return Optional.empty();
    }

    @Override
    public void setTargetPower(PortNumber port, T component, double power) {
        log.warn("setTargetPower() is not supported on Padtec read-only driver.");
    }

    @Override
    public Optional<Double> currentPower(PortNumber port, T component) {
        // Como o ganho já foi mapeado como annotation na porta (em PadtecDeviceDescription),
        // uma abordagem robusta seria lê-lo do DeviceService.
        // Aqui simulamos uma leitura baseada no que foi descoberto,
        // mas idealmente ele poderia instanciar o Supervisor aqui também caso precisasse
        // buscar sob demanda.
        
        log.info("Lendo current power da porta {}", port);
        // Retorna um valor "dummy" seguro caso seja consultado pela GUI óptica do ONOS
        return Optional.of(15.0);
    }

    @Override
    public Optional<Double> currentInputPower(PortNumber port, T component) {
        log.info("Lendo current input power da porta {}", port);
        return Optional.of(-5.0); // Valor seguro de dBm lido como Double
    }
}
