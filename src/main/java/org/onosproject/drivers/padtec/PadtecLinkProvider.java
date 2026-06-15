package org.onosproject.drivers.padtec;

import org.onosproject.net.ConnectPoint;
import org.onosproject.net.Device;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Link;
import org.onosproject.net.LinkKey;
import org.onosproject.net.config.NetworkConfigEvent;
import org.onosproject.net.config.NetworkConfigListener;
import org.onosproject.net.config.NetworkConfigService;
import org.onosproject.net.config.basics.BasicLinkConfig;
import org.onosproject.net.device.DeviceEvent;
import org.onosproject.net.device.DeviceListener;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.link.DefaultLinkDescription;
import org.onosproject.net.link.LinkProvider;
import org.onosproject.net.link.LinkProviderRegistry;
import org.onosproject.net.link.LinkProviderService;
import org.onosproject.net.provider.ProviderId;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;

import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Injeta links Padtec no ONOS lendo a topologia do network-config (netcfg).
 *
 * Em ONOS 3.x o LinkConfigOperator nao cria links durables automaticamente
 * — eles precisam ser detectados por um LinkProvider. Este componente le as
 * entradas BasicLinkConfig do netcfg que envolvem dispositivos padtec:* e
 * chama providerService.linkDetected() para materializa-las na topologia.
 *
 * Aciona re-injecao quando:
 *   - o componente ativa (dispositivos Padtec ja disponiveis)
 *   - um dispositivo Padtec torna-se disponivel (DeviceEvent)
 *   - uma entrada de link e adicionada/atualizada no netcfg (NetworkConfigEvent)
 */
@Component(immediate = true)
public class PadtecLinkProvider implements LinkProvider {

    private final Logger log = getLogger(getClass());

    private static final ProviderId PID =
            new ProviderId("padtec.link", "org.onosproject.drivers.padtec.links");

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected LinkProviderRegistry linkProviderRegistry;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected NetworkConfigService netcfgService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected DeviceService deviceService;

    private LinkProviderService providerService;

    private final DeviceListener deviceListener     = new InternalDeviceListener();
    private final NetworkConfigListener netcfgListener = new InternalNetcfgListener();

    @Activate
    public void activate() {
        providerService = linkProviderRegistry.register(this);
        deviceService.addListener(deviceListener);
        netcfgService.addListener(netcfgListener);

        // Injeta links para qualquer dispositivo Padtec ja online
        deviceService.getDevices().forEach(d -> {
            if (isPadtec(d.id()) && deviceService.isAvailable(d.id())) {
                injectLinksForDevice(d.id());
            }
        });

        log.info("PadtecLinkProvider started — lendo topologia do netcfg");
    }

    @Deactivate
    public void deactivate() {
        deviceService.removeListener(deviceListener);
        netcfgService.removeListener(netcfgListener);
        if (providerService != null) {
            linkProviderRegistry.unregister(this);
            providerService = null;
        }
        log.info("PadtecLinkProvider stopped");
    }

    @Override
    public ProviderId id() {
        return PID;
    }

    // ── injecao de links ──────────────────────────────────────────────────────

    /**
     * Le todas as entradas BasicLinkConfig do netcfg que envolvem deviceId
     * e chama linkDetected() para cada link permitido.
     */
    private void injectLinksForDevice(DeviceId deviceId) {
        Set<LinkKey> keys = netcfgService.getSubjects(LinkKey.class, BasicLinkConfig.class);
        if (keys == null || keys.isEmpty()) {
            log.debug("Nenhuma entrada de link no netcfg");
            return;
        }

        int count = 0;
        for (LinkKey key : keys) {
            boolean srcMatch = key.src().deviceId().equals(deviceId);
            boolean dstMatch = key.dst().deviceId().equals(deviceId);
            if (!srcMatch && !dstMatch) {
                continue;
            }

            BasicLinkConfig cfg = netcfgService.getConfig(key, BasicLinkConfig.class);
            if (cfg == null || !cfg.isAllowed()) {
                continue;
            }

            Link.Type type = cfg.type();
            if (type == null) {
                type = Link.Type.DIRECT;
            }

            providerService.linkDetected(
                    new DefaultLinkDescription(key.src(), key.dst(), type, cfg.isDurable(), null));
            log.debug("Link injetado: {} -> {} [{}]", key.src(), key.dst(), type);
            count++;
        }

        if (count > 0) {
            log.info("{} link(s) Padtec injetado(s) a partir do netcfg para {}", count, deviceId);
        } else {
            log.info("Nenhum link encontrado no netcfg para {} — configure em tools/lab-topology.json",
                    deviceId);
        }
    }

    /** Injeta todos os links de todos os dispositivos Padtec. */
    private void injectAllPadtecLinks() {
        deviceService.getDevices().forEach(d -> {
            if (isPadtec(d.id()) && deviceService.isAvailable(d.id())) {
                injectLinksForDevice(d.id());
            }
        });
    }

    private boolean isPadtec(DeviceId id) {
        return id.toString().startsWith("padtec:");
    }

    // ── listeners ─────────────────────────────────────────────────────────────

    private class InternalDeviceListener implements DeviceListener {
        @Override
        public void event(DeviceEvent event) {
            Device device = event.subject();
            if (!isPadtec(device.id())) {
                return;
            }
            if (event.type() == DeviceEvent.Type.DEVICE_ADDED ||
                event.type() == DeviceEvent.Type.DEVICE_AVAILABILITY_CHANGED) {
                if (deviceService.isAvailable(device.id())) {
                    log.info("Dispositivo Padtec disponivel — injetando links do netcfg");
                    injectLinksForDevice(device.id());
                }
            }
        }
    }

    private class InternalNetcfgListener implements NetworkConfigListener {
        @Override
        public void event(NetworkConfigEvent event) {
            if (!(event.subject() instanceof LinkKey)) {
                return;
            }
            LinkKey key = (LinkKey) event.subject();
            boolean involvesPadtec = isPadtec(key.src().deviceId())
                                  || isPadtec(key.dst().deviceId());
            if (!involvesPadtec) {
                return;
            }

            if (event.type() == NetworkConfigEvent.Type.CONFIG_ADDED ||
                event.type() == NetworkConfigEvent.Type.CONFIG_UPDATED) {
                BasicLinkConfig cfg = netcfgService.getConfig(key, BasicLinkConfig.class);
                if (cfg != null && cfg.isAllowed()) {
                    Link.Type type = cfg.type() != null ? cfg.type() : Link.Type.DIRECT;
                    ConnectPoint src = key.src();
                    ConnectPoint dst = key.dst();
                    providerService.linkDetected(
                            new DefaultLinkDescription(src, dst, type, cfg.isDurable(), null));
                    log.info("Link Padtec atualizado via netcfg: {} -> {} [{}]", src, dst, type);
                }
            } else if (event.type() == NetworkConfigEvent.Type.CONFIG_REMOVED) {
                providerService.linkVanished(key);
                log.info("Link Padtec removido do netcfg: {}", key);
            }
        }
    }
}
