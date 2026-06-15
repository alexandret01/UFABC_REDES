package org.onosproject.drivers.padtec;

import com.google.common.collect.Range;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Port;
import org.onosproject.net.PortNumber;
import org.onosproject.net.behaviour.PowerConfig;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.driver.AbstractHandlerBehaviour;
import org.slf4j.Logger;

import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * PowerConfig para equipamentos Padtec.
 *
 * Lê os valores reais de potência das anotações de porta publicadas por
 * PadtecDeviceProvider/PadtecDeviceDescription:
 *
 *   Transponders (T100DCT):
 *     outputPowerWDM / outputPower  → currentPower
 *     inputPowerWDM  / inputPower   → currentInputPower
 *     Faixa de saída:  −6 a +3 dBm
 *     Faixa de entrada: −28 a +3 dBm
 *
 *   Amplificadores (EDFA):
 *     gain         → currentPower (ganho em dB)
 *     powerInput   → currentInputPower
 *     Faixa de ganho:   0 a 25 dB
 *     Faixa de entrada: −30 a 0 dBm
 *
 * setTargetPower() não é suportado (equipamentos somente-leitura neste driver).
 */
public class PadtecPowerConfig<T> extends AbstractHandlerBehaviour
        implements PowerConfig<T> {

    private final Logger log = getLogger(getClass());

    // Faixas de potência típicas para os equipamentos do laboratório UFABC
    private static final Range<Double> TRANSPONDER_OUTPUT_RANGE = Range.closed(-6.0,   3.0);
    private static final Range<Double> TRANSPONDER_INPUT_RANGE  = Range.closed(-28.0,  3.0);
    private static final Range<Double> AMP_GAIN_RANGE           = Range.closed(0.0,   25.0);
    private static final Range<Double> AMP_INPUT_RANGE          = Range.closed(-30.0,  0.0);

    // -----------------------------------------------------------------------
    // currentPower — potência de saída WDM ou ganho do amplificador
    // -----------------------------------------------------------------------

    @Override
    public Optional<Double> currentPower(PortNumber port, T component) {
        try {
            Port p = getPort(port);
            if (p == null) {
                return Optional.empty();
            }
            // Transponders: preferir campo WDM específico
            // Amplificadores: "gain" (dB)
            String val = firstNonNa(
                    p.annotations().value("outputPowerWDM"),
                    p.annotations().value("gain"),
                    p.annotations().value("outputPower"),
                    p.annotations().value("powerOutput")
            );
            if (val != null) {
                return Optional.of(Double.parseDouble(val));
            }
        } catch (Exception e) {
            log.warn("currentPower() porta {}: {}", port, e.getMessage());
        }
        return Optional.empty();
    }

    // -----------------------------------------------------------------------
    // currentInputPower — potência de entrada no equipamento
    // -----------------------------------------------------------------------

    @Override
    public Optional<Double> currentInputPower(PortNumber port, T component) {
        try {
            Port p = getPort(port);
            if (p == null) {
                return Optional.empty();
            }
            // Transponders: preferir campo WDM específico
            // Amplificadores: "powerInput" (dBm)
            String val = firstNonNa(
                    p.annotations().value("inputPowerWDM"),
                    p.annotations().value("powerInput"),
                    p.annotations().value("inputPower")
            );
            if (val != null) {
                return Optional.of(Double.parseDouble(val));
            }
        } catch (Exception e) {
            log.warn("currentInputPower() porta {}: {}", port, e.getMessage());
        }
        return Optional.empty();
    }

    // -----------------------------------------------------------------------
    // Faixas de potência — usadas pelo ONOS para validar setTargetPower()
    // -----------------------------------------------------------------------

    @Override
    public Optional<Range<Double>> getTargetPowerRange(PortNumber port, T component) {
        try {
            Port p = getPort(port);
            if (p != null && isAmplifier(p)) {
                return Optional.of(AMP_GAIN_RANGE);
            }
        } catch (Exception e) {
            log.debug("getTargetPowerRange() porta {}: {}", port, e.getMessage());
        }
        return Optional.of(TRANSPONDER_OUTPUT_RANGE);
    }

    @Override
    public Optional<Range<Double>> getInputPowerRange(PortNumber port, T component) {
        try {
            Port p = getPort(port);
            if (p != null && isAmplifier(p)) {
                return Optional.of(AMP_INPUT_RANGE);
            }
        } catch (Exception e) {
            log.debug("getInputPowerRange() porta {}: {}", port, e.getMessage());
        }
        return Optional.of(TRANSPONDER_INPUT_RANGE);
    }

    // -----------------------------------------------------------------------
    // Somente leitura — escrita não suportada neste driver
    // -----------------------------------------------------------------------

    @Override
    public Optional<Double> getTargetPower(PortNumber port, T component) {
        // Padtec T100DCT e EDFAs deste lab não expõem setpoint via agente TCP
        return Optional.empty();
    }

    @Override
    public void setTargetPower(PortNumber port, T component, double power) {
        log.warn("setTargetPower() não suportado no driver Padtec. " +
                 "Porta={}, potência={} dBm ignorados.", port, power);
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private Port getPort(PortNumber portNumber) {
        try {
            DeviceService ds = handler().get(DeviceService.class);
            DeviceId did = handler().data().deviceId();
            return ds.getPort(did, portNumber);
        } catch (Exception e) {
            log.debug("getPort() falhou: {}", e.getMessage());
            return null;
        }
    }

    /** Verifica se a porta pertence a um amplificador pelo campo "gain" ou anotação "type". */
    private boolean isAmplifier(Port p) {
        String type = p.annotations().value("type");
        if ("EDFA".equalsIgnoreCase(type) || "AMP".equalsIgnoreCase(type)) {
            return true;
        }
        // Se tem campo "gain" mas não tem "channel", é amplificador
        String gain    = p.annotations().value("gain");
        String channel = p.annotations().value("channel");
        return gain != null && channel == null;
    }

    /** Retorna o primeiro valor não nulo e diferente de "N/A" e não vazio. */
    private String firstNonNa(String... candidates) {
        for (String v : candidates) {
            if (v != null && !v.isEmpty() && !"N/A".equalsIgnoreCase(v) && !"null".equalsIgnoreCase(v)) {
                return v;
            }
        }
        return null;
    }
}
