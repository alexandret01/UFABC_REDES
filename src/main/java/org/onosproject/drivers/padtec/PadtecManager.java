package org.onosproject.drivers.padtec;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.ufabc.controlplane.metropad.PadtecMonitorJSON3;

@Component(immediate = true)
public class PadtecManager {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private Thread monitorThread;

    @Activate
    protected void activate() {
        log.info("Starting Padtec Integration Layer...");

        // PadtecMonitorJSON3.main() já inicia o PadtecAgentServer internamente.
        // Não iniciar PadtecAgentServer separadamente — causaria BindException na porta 10151.
        monitorThread = new Thread(() -> {
            PadtecMonitorJSON3.main(new String[0]);
        });
        monitorThread.setDaemon(true);
        monitorThread.setName("PadtecMonitorThread");
        monitorThread.start();

        log.info("Padtec Integration Layer Started (TCP agent na porta 10151).");
    }

    @Deactivate
    protected void deactivate() {
        log.info("Stopping Padtec Integration Layer...");
        if (monitorThread != null) {
            monitorThread.interrupt();
        }
        log.info("Padtec Integration Layer Stopped.");
    }
}
