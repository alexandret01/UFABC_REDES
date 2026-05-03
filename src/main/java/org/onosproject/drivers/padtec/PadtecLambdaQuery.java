package org.onosproject.drivers.padtec;

import org.onosproject.net.ChannelSpacing;
import org.onosproject.net.OchSignal;
import org.onosproject.net.PortNumber;
import org.onosproject.net.behaviour.LambdaQuery;
import org.onosproject.net.driver.AbstractHandlerBehaviour;
import org.slf4j.Logger;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.IntStream;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Lambda query implementation for Padtec devices.
 */
public class PadtecLambdaQuery extends AbstractHandlerBehaviour implements LambdaQuery {

    private final Logger log = getLogger(getClass());

    @Override
    public Set<OchSignal> queryLambdas(PortNumber port) {
        log.info("Querying available lambdas for port {} on Padtec device {}", port, handler().data().deviceId());
        
        Set<OchSignal> lambdas = new LinkedHashSet<>();
        
        // Exemplo: Retornando lambdas da banda C ITU-T (grid de 50 GHz)
        // Isso é essencial para que o ONOS saiba quais comprimentos de onda podem ser usados no roteamento óptico.
        int startMultiplier = -40; // Aproximadamente 192.1 THz
        int endMultiplier = 40;    // Aproximadamente 196.1 THz

        IntStream.rangeClosed(startMultiplier, endMultiplier).forEach(x -> {
            lambdas.add(new OchSignal(org.onosproject.net.GridType.DWDM,
                                      ChannelSpacing.CHL_50GHZ,
                                      x,
                                      4));
        });

        return lambdas;
    }
}
