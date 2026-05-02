package org.onosproject.drivers.padtec;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.ufabc.controlplane.metropad.PadtecAgentServer;
import br.ufabc.controlplane.metropad.PadtecMonitorJSON3;

@Component(immediate = true)
public class PadtecManager {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private Thread agentThread;
    private Thread monitorThread;

    @Activate
    protected void activate() {
        log.info("Starting Padtec Integration Layer...");

        // Inicia o PadtecAgentServer em uma nova thread
        agentThread = new Thread(() -> {
            PadtecAgentServer.main(new String[0]);
        });
        agentThread.setName("PadtecAgentServerThread");
        agentThread.start();

        // Inicia o PadtecMonitorJSON3 em uma nova thread
        monitorThread = new Thread(() -> {
            PadtecMonitorJSON3.main(new String[0]);
        });
        monitorThread.setName("PadtecMonitorThread");
        monitorThread.start();

        log.info("Padtec Integration Layer Started.");
    }

    @Deactivate
    protected void deactivate() {
        log.info("Stopping Padtec Integration Layer...");
        if (agentThread != null) {
            agentThread.interrupt();
        }
        if (monitorThread != null) {
            monitorThread.interrupt();
        }
        log.info("Padtec Integration Layer Stopped.");
    }
}
