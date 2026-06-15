package org.onosproject.drivers.padtec;

import org.onosproject.net.ChannelSpacing;
import org.onosproject.net.DeviceId;
import org.onosproject.net.GridType;
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
 * LambdaQuery para dispositivos Padtec.
 *
 * Comportamento por tipo de porta:
 *
 *   Transponder (anotação "channel" presente, ex: "C28"):
 *     Retorna somente o OchSignal do canal configurado.
 *     Grade ITU-T 50 GHz: multiplier = channelNum − 61
 *       C28 → −33 → 193,1 + (−33 × 0,05) = 191,45 THz
 *       C24 → −37 → 193,1 + (−37 × 0,05) = 191,25 THz
 *
 *   Amplificador (sem anotação "channel"):
 *     Retorna a grade completa banda C ITU-T (multipliers −40..+40, 50 GHz, slot 4×12,5 GHz).
 *     Amplificador é transparente — aceita qualquer lambda dentro da sua banda.
 *
 * Formato de canal aceito: "C28", "c28", "28" (só o número).
 * Fallback: banda C completa se o canal não puder ser interpretado.
 */
public class PadtecLambdaQuery extends AbstractHandlerBehaviour implements LambdaQuery {

    private final Logger log = getLogger(getClass());

    // Banda C ITU-T 50 GHz: C1 (multiplier −40) até C81 (multiplier +40)
    // Ref: 193,1 THz; passo = 50 GHz = 0,05 THz; slot width = 4 × 12,5 GHz
    private static final int    SLOT_WIDTH   = 4;
    private static final int    BAND_C_MIN   = -40;
    private static final int    BAND_C_MAX   = 40;

    // Offset da numeração de canal ITU-T para o multiplier da grade ONOS
    // Canal C(k): frequência = 193,1 + (k − 61) × 0,05 THz → multiplier = k − 61
    private static final int    CHANNEL_OFFSET = 61;

    @Override
    public Set<OchSignal> queryLambdas(PortNumber port) {
        Set<OchSignal> lambdas = new LinkedHashSet<>();

        try {
            DeviceService ds  = handler().get(DeviceService.class);
            DeviceId      did = handler().data().deviceId();
            Port          p   = ds.getPort(did, port);

            if (p != null) {
                String channelStr = p.annotations().value("channel");
                if (channelStr != null && !channelStr.isEmpty()) {
                    OchSignal sig = parseChannel(channelStr, port);
                    if (sig != null) {
                        lambdas.add(sig);
                        return lambdas;
                    }
                }
                // Amplificador: sem "channel" → retorna banda C completa
                log.debug("queryLambdas porta {}: amplificador ou canal desconhecido — retornando banda C completa", port);
            }
        } catch (Exception e) {
            log.debug("queryLambdas porta {}: erro ao ler porta — {}", port, e.getMessage());
        }

        // Banda C completa ITU-T 50 GHz
        IntStream.rangeClosed(BAND_C_MIN, BAND_C_MAX)
                 .forEach(n -> lambdas.add(new OchSignal(GridType.DWDM, ChannelSpacing.CHL_50GHZ, n, SLOT_WIDTH)));
        return lambdas;
    }

    /**
     * Converte string de canal ("C28", "c28", "28") em OchSignal.
     * Retorna null se o formato não for reconhecido ou estiver fora da banda C.
     */
    private OchSignal parseChannel(String channelStr, PortNumber port) {
        try {
            String s = channelStr.trim();
            // Aceita "C28", "c28" ou só "28"
            if (s.toUpperCase().startsWith("C")) {
                s = s.substring(1);
            }
            int channelNum = Integer.parseInt(s);
            int multiplier = channelNum - CHANNEL_OFFSET;

            // Valida que o canal está dentro da banda C suportada
            if (multiplier < BAND_C_MIN || multiplier > BAND_C_MAX) {
                log.warn("queryLambdas porta {}: canal {} (multiplier {}) fora da banda C [{},{}]",
                        port, channelStr, multiplier, BAND_C_MIN, BAND_C_MAX);
                return null;
            }

            log.debug("queryLambdas porta {}: canal {} → multiplier {} ({} THz)",
                    port, channelStr, multiplier,
                    String.format("%.2f", 193.1 + multiplier * 0.05));

            return new OchSignal(GridType.DWDM, ChannelSpacing.CHL_50GHZ, multiplier, SLOT_WIDTH);

        } catch (NumberFormatException e) {
            log.warn("queryLambdas porta {}: canal '{}' não é um número válido", port, channelStr);
            return null;
        }
    }
}
