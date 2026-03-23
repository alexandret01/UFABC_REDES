package org.onosproject.drivers.padtec;

import org.onosproject.net.ConnectPoint;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Link;
import org.onosproject.net.PortNumber;
import org.onosproject.net.DefaultAnnotations;
import org.onosproject.net.behaviour.LinkDiscovery;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.device.PortDescription;
import org.onosproject.net.driver.AbstractHandlerBehaviour;
import org.onosproject.net.link.DefaultLinkDescription;
import org.onosproject.net.link.LinkDescription;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Link discovery for Padtec optical devices.
 */
public class PadtecLinkDiscovery extends AbstractHandlerBehaviour implements LinkDiscovery {

    private final Logger log = getLogger(getClass());

    @Override
    public Set<LinkDescription> getLinks() {
        DeviceId deviceId = handler().data().deviceId();
        log.info("Discovering optical links for Padtec device {}", deviceId);
        Set<LinkDescription> links = new HashSet<>();

        // Como a descoberta de links em rede óptica (OTN/DWDM) muitas vezes não usa LLDP
        // como nas redes Ethernet, a construção dos links precisa ser declarativa via netcfg
        // ou lida de tags no equipamento físico.
        
        // Aqui nós apenas declaramos que a classe está pronta para retornar os links
        // físicos se a placa SPVL4 for capaz de informar quem é o seu vizinho (Peer Port).
        
        return links;
    }
}
