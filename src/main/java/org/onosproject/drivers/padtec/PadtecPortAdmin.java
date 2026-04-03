package org.onosproject.drivers.padtec;

import org.onosproject.net.PortNumber;
import org.onosproject.net.behaviour.PortAdmin;
import org.onosproject.net.driver.AbstractHandlerBehaviour;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Port Admin behaviour for Padtec devices.
 */
public class PadtecPortAdmin extends AbstractHandlerBehaviour implements PortAdmin {

    private final Logger log = getLogger(getClass());

    @Override
    public CompletableFuture<Boolean> enable(PortNumber number) {
        log.info("Request to enable port {} on Padtec device {}", number, handler().data().deviceId());
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletableFuture<Boolean> disable(PortNumber number) {
        log.info("Request to disable port {} on Padtec device {}", number, handler().data().deviceId());
        return CompletableFuture.completedFuture(false);
    }
    
    @Override
    public CompletableFuture<Boolean> isEnabled(PortNumber number) {
        log.info("Checking if port {} is enabled on Padtec device {}", number, handler().data().deviceId());
        // Retorna sempre true ou implementa a chamada real se o equipamento suportar.
        return CompletableFuture.completedFuture(true);
    }
}
