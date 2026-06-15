package org.onosproject.opticallab;

import org.onosproject.net.device.DeviceService;
import org.onosproject.net.flow.FlowRuleService;
import org.onosproject.net.link.LinkService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Componente OSGi principal do Optical Lab Monitor.
 *
 * Responsabilidades:
 *  - Inicia coleta periódica (a cada INTERVAL_SECONDS)
 *  - Armazena histórico em OpticalLabStore
 *  - Expõe instância estática para o OpticalLabWebResource
 *
 * Acesso: OpticalLabApp.getInstance() — padrão comum em apps ONOS
 *         para comunicação entre @Component e o web resource.
 */
@Component(immediate = true)
public class OpticalLabApp {

    private static final Logger log = LoggerFactory.getLogger(OpticalLabApp.class);
    private static final int    INTERVAL_SECONDS = 60;
    private static final int    INITIAL_DELAY_S  = 5; // aguarda ONOS estabilizar

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected DeviceService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected FlowRuleService flowRuleService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected LinkService linkService;

    // Singleton acessível pelo web resource
    private static volatile OpticalLabApp instance;

    private OpticalLabStore    store;
    private OpticalLabCollector collector;
    private ScheduledExecutorService scheduler;
    private final AtomicInteger collectCount = new AtomicInteger(0);

    @Activate
    protected void activate() {
        store     = new OpticalLabStore();
        collector = new OpticalLabCollector(deviceService, flowRuleService, linkService);
        scheduler = Executors.newSingleThreadScheduledExecutor(
                r -> new Thread(r, "opticallab-collector"));

        scheduler.scheduleAtFixedRate(this::runCollection,
                INITIAL_DELAY_S, INTERVAL_SECONDS, TimeUnit.SECONDS);

        instance = this;
        log.info("Optical Lab Monitor STARTED — coletando a cada {}s, "
                 + "dashboard em /onos/opticallab/ui", INTERVAL_SECONDS);
    }

    @Deactivate
    protected void deactivate() {
        instance = null;
        if (scheduler != null) {
            scheduler.shutdownNow();
            try { scheduler.awaitTermination(3, TimeUnit.SECONDS); }
            catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
        }
        log.info("Optical Lab Monitor STOPPED ({} coletas realizadas)",
                collectCount.get());
    }

    private void runCollection() {
        try {
            DataPoint dp = collector.collect();
            store.add(dp);
            int n = collectCount.incrementAndGet();
            if (n % 10 == 0 || n == 1) {
                log.info("Coleta #{}: {} devices, {} xconn, {} flows-ADDED, {} lldp-links",
                        n, dp.devices.size(), dp.crossConnects.size(),
                        dp.pavFlowsAdded, dp.lldpLinks);
            }
        } catch (Exception e) {
            log.error("Erro durante coleta: {}", e.getMessage(), e);
        }
    }

    // ── API pública para o web resource ────────────────────────────────────────

    public static OpticalLabApp getInstance() {
        return instance;
    }

    public OpticalLabStore getStore() {
        return store;
    }

    public int getCollectCount() {
        return collectCount.get();
    }

    public int getIntervalSeconds() {
        return INTERVAL_SECONDS;
    }
}
