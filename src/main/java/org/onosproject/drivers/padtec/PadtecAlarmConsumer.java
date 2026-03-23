package org.onosproject.drivers.padtec;

import org.onosproject.alarm.Alarm;
import org.onosproject.alarm.AlarmConsumer;
import org.onosproject.net.driver.AbstractHandlerBehaviour;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Alarm Consumer for Padtec devices.
 */
public class PadtecAlarmConsumer extends AbstractHandlerBehaviour implements AlarmConsumer {

    private final Logger log = getLogger(getClass());

    @Override
    public List<Alarm> consumeAlarms() {
        log.info("Consuming alarms for Padtec device {}...", handler().data().deviceId());
        
        // Aqui no futuro você pode integrar com o Jaquison para buscar as falhas de equipamento
        // e converter para a classe Alarm do ONOS.
        
        return Collections.emptyList();
    }
}
