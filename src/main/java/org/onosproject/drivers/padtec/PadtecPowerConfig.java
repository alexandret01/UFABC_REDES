package org.onosproject.drivers.padtec;

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
 *   - Amplificadores : "gain" (currentPower), "powerInput" (currentInputPower)
 *   - Transponders   : "outputPower" (currentPower), "inputPower" (currentInputPower)
 *
 * O driver é somente-leitura: setTargetPower() e getTargetPower() não são suportados.
 */
public class PadtecPowerConfig<T> extends AbstractHandlerBehaviour
        implements PowerConfig<T> {

    private final Logger log = getLogger(getClass());

    // -----------------------------------------------------------------------
    // currentPower — potência de saída / ganho do equipamento
    // -----------------------------------------------------------------------

    @Override
    public Optional<Double> currentPower(PortNumber port, T component) {
        try {
            Port p = getPort(port);
            if (p == null) {
                return Optional.empty();
            }
            // Amplificadores: campo "gain" (dB); Transponders: "outputPower" (dBm)
            String val = firstNonNa(
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
            // Amplificadores: "powerInput" (dBm); Transponders: "inputPower" (dBm)
            String val = firstNonNa(
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
    // Somente leitura — escrita não suportada
    // -----------------------------------------------------------------------

    @Override
    public Optional<Double> getTargetPower(PortNumber port, T component) {
        log.debug("getTargetPower() não suportado no driver Padtec (somente leitura).");
        return Optional.empty();
    }

    @Override
    public void setTargetPower(PortNumber port, T component, double power) {
        log.warn("setTargetPower() não suportado no driver Padtec (somente leitura). " +
                 "Porta={}, potência={} dBm ignorados.", port, power);
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    /** Retorna a porta do DeviceService ou null se não disponível. */
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

    /**
     * Retorna o primeiro valor não nulo e diferente de "N/A", ou null se nenhum encontrado.
     */
    private String firstNonNa(String... candidates) {
        for (String v : candidates) {
            if (v != null && !"N/A".equals(v) && !v.isEmpty()) {
                return v;
            }
        }
        return null;
    }
}
