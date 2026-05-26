package org.onosproject.drivers.padtec;

import org.onosproject.net.ChannelSpacing;
import org.onosproject.net.DeviceId;
import org.onosproject.net.OchSignal;
import org.onosproject.net.Port;
import org.onosproject.net.PortNumber;
import org.onosproject.net.behaviour.LambdaQuery;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.driver.AbstractHandlerBehaviour;
import org.slf4j.Logger;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.IntStream;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Lambda query implementation for Padtec devices.
 *
 * Se a porta tiver uma anotação "channel" com canal DWDM (ex: "C28", "C24"),
 * retorna apenas o OchSignal correspondente à frequência real do canal.
 *
 * Caso contrário, retorna toda a grade ITU-T banda C (50 GHz, C1–C80),
 * que é usada pelo ONOS para oferecer lambdas disponíveis no roteamento óptico.
 */
public class PadtecLambdaQuery extends AbstractHandlerBehaviour implements LambdaQuery {

    private final Logger log = getLogger(getClass());

    /**
     * Grade ITU-T banda C 50 GHz: n = −40..+40 em torno de 193,1 THz (freq ref).
     * Canal C(k) → multiplier = k − 61 (ex: C28 → −33, C24 → −37).
     */
    @Override
    public Set<OchSignal> queryLambdas(PortNumber port) {
        Set<OchSignal> lambdas = new LinkedHashSet<>();

        // Tenta ler o canal real da anotação da porta
        try {
            DeviceService ds = handler().get(DeviceService.class);
            DeviceId did = handler().data().deviceId();
            Port p = ds.getPort(did, port);
            if (p != null) {
                String channel = p.annotations().value("channel");
                if (channel != null && channel.startsWith("C") && channel.length() > 1) {
                    int channelNum = Integer.parseInt(channel.substring(1)); // ex: "C28" → 28
                    // Multiplier na grade ITU-T 50 GHz: n = channelNum − 61
                    int multiplier = channelNum - 61;
                    OchSignal sig = new OchSignal(
                            org.onosproject.net.GridType.DWDM,
                            ChannelSpacing.CHL_50GHZ,
                            multiplier,
                            4);
                    lambdas.add(sig);
                    log.debug("queryLambdas porta {}: canal {} → multiplier {}",
                            port, channel, multiplier);
                    return lambdas;
                }
            }
        } catch (Exception e) {
            log.debug("queryLambdas: não foi possível ler canal da porta {}: {}", port, e.getMessage());
        }

        // Fallback: toda a banda C ITU-T (50 GHz, C1–C80 ≈ multiplier −40..+40)
        log.debug("queryLambdas porta {}: retornando grade completa banda C.", port);
        IntStream.rangeClosed(-40, 40).forEach(n ->
                lambdas.add(new OchSignal(
                        org.onosproject.net.GridType.DWDM,
                        ChannelSpacing.CHL_50GHZ,
                        n,
                        4)));

        return lambdas;
    }
}
