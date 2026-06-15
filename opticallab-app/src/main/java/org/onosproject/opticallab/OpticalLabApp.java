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

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component(immediate = true)
public class OpticalLabApp {

    private static final Logger log = LoggerFactory.getLogger(OpticalLabApp.class);
    private static final int    INTERVAL_SECONDS = 60;
    private static final int    INITIAL_DELAY_S  = 5;

    // Marcador 1: classe carregada pelo OSGi?
    static {
        diag("class-loaded", "OpticalLabApp class loaded");
    }

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected DeviceService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected FlowRuleService flowRuleService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected LinkService linkService;

    private static volatile OpticalLabApp instance;

    private OpticalLabStore          store;
    private OpticalLabCollector      collector;
    private OpticalLabHttpServer     httpServer;
    private ScheduledExecutorService scheduler;
    private final AtomicInteger      collectCount = new AtomicInteger(0);

    @Activate
    protected void activate() {
        // Marcador 2: activate() foi invocado?
        diag("activate-called", "activate() called");

        try {
            store     = new OpticalLabStore();
            collector = new OpticalLabCollector(deviceService, flowRuleService, linkService);
            scheduler = Executors.newSingleThreadScheduledExecutor(
                    r -> new Thread(r, "opticallab-collector"));

            diag("before-http-start", "about to start HTTP server on port " + OpticalLabHttpServer.PORT);

            httpServer = new OpticalLabHttpServer();
            httpServer.start();

            // Marcador 3: servidor iniciou sem exceção?
            diag("server-started", "HTTP server started OK on port " + OpticalLabHttpServer.PORT);

            scheduler.scheduleAtFixedRate(this::runCollection,
                    INITIAL_DELAY_S, INTERVAL_SECONDS, TimeUnit.SECONDS);

            instance = this;
            log.info("Optical Lab Monitor STARTED — porta {}", OpticalLabHttpServer.PORT);

        } catch (Throwable t) {
            // Captura Exception E Error (ex: NoClassDefFoundError, OutOfMemoryError)
            String err = t.getClass().getName() + ": " + t.getMessage();
            log.error("Falha em activate(): {}", err, t);
            System.err.println("[OpticalLab] FALHA activate(): " + err);
            diag("activate-error", err);
        }
    }

    @Deactivate
    protected void deactivate() {
        diag("deactivate-called", "deactivate() called");
        instance = null;
        if (httpServer != null) httpServer.stop();
        if (scheduler != null) {
            scheduler.shutdownNow();
            try { scheduler.awaitTermination(3, TimeUnit.SECONDS); }
            catch (InterruptedException ignored) { Thread.currentThread().interrupt(); }
        }
        log.info("Optical Lab Monitor STOPPED ({} coletas)", collectCount.get());
    }

    private void runCollection() {
        try {
            DataPoint dp = collector.collect();
            store.add(dp);
            int n = collectCount.incrementAndGet();
            if (n % 10 == 0 || n == 1) {
                log.info("Coleta #{}: {} devices, {} xconn, {} flows, {} links",
                        n, dp.devices.size(), dp.crossConnects.size(),
                        dp.pavFlowsAdded, dp.lldpLinks);
            }
        } catch (Throwable e) {
            log.error("Erro na coleta: {}", e.getMessage(), e);
        }
    }

    static void diag(String name, String msg) {
        String line = System.currentTimeMillis() + " " + msg + "\n";
        System.out.println("[OPTICALLAB-DIAG] " + name + ": " + msg);
        System.err.println("[OPTICALLAB-DIAG] " + name + ": " + msg);
        for (String dir : new String[]{"/tmp", System.getProperty("user.home", "/home/sdn"), "."}) {
            try {
                Files.write(Paths.get(dir + "/opticallab-" + name + ".txt"), line.getBytes("UTF-8"));
                break;
            } catch (Exception ignored) {}
        }
    }

    public static OpticalLabApp getInstance() { return instance; }
    public OpticalLabStore getStore()         { return store; }
    public int getCollectCount()              { return collectCount.get(); }
    public int getIntervalSeconds()           { return INTERVAL_SECONDS; }
}
