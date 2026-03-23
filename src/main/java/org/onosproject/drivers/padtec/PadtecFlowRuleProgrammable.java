package org.onosproject.drivers.padtec;

import org.onosproject.net.driver.AbstractHandlerBehaviour;
import org.onosproject.net.flow.FlowEntry;
import org.onosproject.net.flow.FlowRule;
import org.onosproject.net.flow.FlowRuleProgrammable;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Collections;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Flow rule programmable behaviour for Padtec optical devices.
 */
public class PadtecFlowRuleProgrammable extends AbstractHandlerBehaviour implements FlowRuleProgrammable {

    private final Logger log = getLogger(getClass());

    @Override
    public Collection<FlowEntry> getFlowEntries() {
        log.info("Getting flow entries from Padtec device {}", handler().data().deviceId());
        // Em um equipamento puramente óptico e no modo "Read Only", não temos flows (tabelas OpenFlow)
        // ou cross-connects criados diretamente neste dispositivo.
        return Collections.emptyList();
    }

    @Override
    public Collection<FlowRule> applyFlowRules(Collection<FlowRule> rules) {
        log.warn("Applying flow rules is not supported on Padtec read-only driver.");
        // Impede que o controlador envie regras (Flows) para a placa SPVL4
        return Collections.emptyList();
    }

    @Override
    public Collection<FlowRule> removeFlowRules(Collection<FlowRule> rules) {
        log.warn("Removing flow rules is not supported on Padtec read-only driver.");
        return Collections.emptyList();
    }
}
