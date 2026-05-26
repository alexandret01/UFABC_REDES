package org.onosproject.drivers.padtec;

import org.onosproject.net.DeviceId;
import org.onosproject.net.Port;
import org.onosproject.net.PortNumber;
import org.onosproject.net.behaviour.PortAdmin;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.driver.AbstractHandlerBehaviour;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * PortAdmin para equipamentos Padtec.
 *
 * O driver é somente-leitura: enable() e disable() retornam false
 * (equipamento não suporta controle remoto de porta via este driver).
 *
 * isEnabled() lê o estado real da porta a partir do DeviceService,
 * refletindo o campo "isLOS" publicado pelo PadtecDeviceProvider.
 */
public class PadtecPortAdmin extends AbstractHandlerBehaviour implements PortAdmin {

    private final Logger log = getLogger(getClass());

    @Override
    public CompletableFuture<Boolean> enable(PortNumber number) {
        log.warn("enable() não suportado no driver Padtec (somente leitura). Porta: {}", number);
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public CompletableFuture<Boolean> disable(PortNumber number) {
        log.warn("disable() não suportado no driver Padtec (somente leitura). Porta: {}", number);
        return CompletableFuture.completedFuture(false);
    }

    /**
     * Retorna o estado real da porta lido do DeviceService.
     * Uma porta com isLOS=true é registrada como disabled (isEnabled=false).
     */
    @Override
    public CompletableFuture<Boolean> isEnabled(PortNumber number) {
        try {
            DeviceService deviceService = handler().get(DeviceService.class);
            DeviceId did = handler().data().deviceId();
            Port port = deviceService.getPort(did, number);
            if (port != null) {
                boolean enabled = port.isEnabled();
                log.debug("isEnabled() porta {}: {}", number, enabled);
                return CompletableFuture.completedFuture(enabled);
            }
        } catch (Exception e) {
            log.warn("isEnabled() porta {}: {}", number, e.getMessage());
        }
        // Fallback: assume habilitada se não conseguiu ler
        return CompletableFuture.completedFuture(true);
    }
}
