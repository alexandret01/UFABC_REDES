package org.onosproject.drivers.padtec;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.onosproject.net.ConnectPoint;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Link;
import org.onosproject.net.PortNumber;
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

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Provedor de links ópticos entre o equipamento Padtec e os switches Polatis.
 *
 * Lê a topologia física do arquivo {@value LINKS_FILE}.
 * Se o arquivo não existir, nenhum link é injetado.
 *
 * Formato do arquivo JSON (array de objetos):
 * <pre>
 * [
 *   { "padtecPort": 1, "remoteDevice": "netconf:192.168.X.X/830", "remotePort": 5 },
 *   { "padtecPort": 2, "remoteDevice": "netconf:192.168.X.X/830", "remotePort": 7 },
 *   { "padtecPort": 3, "remoteDevice": "netconf:192.168.X.Y/830", "remotePort": 3 }
 * ]
 * </pre>
 *
 * Para obter os IDs dos dispositivos Polatis no ONOS:
 *   curl -s -u onos:rocks http://localhost:8181/onos/v1/devices | python3 -m json.tool
 *
 * Após criar/editar o arquivo, reinstale o bundle para reinjetar os links.
 */
@Component(immediate = true)
public class PadtecLinkProvider implements LinkProvider {

    private final Logger log = getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected LinkProviderRegistry linkProviderRegistry;

    private LinkProviderService providerService;

    private static final ProviderId PID =
            new ProviderId("optical.padtec", "org.onosproject.drivers.padtec.links");

    private static final DeviceId PADTEC_ID =
            DeviceId.deviceId("padtec:172.17.36.50");

    /**
     * Caminho do arquivo de topologia óptica.
     * Crie-o manualmente com o mapeamento Padtec ↔ Polatis.
     */
    static final String LINKS_FILE = "/home/sdn/padtec-links.json";

    /** Aguarda o device Padtec ser publicado antes de injetar links. */
    private static final long INJECT_DELAY_MS = 60_000L;

    // -----------------------------------------------------------------------
    // Lifecycle
    // -----------------------------------------------------------------------

    @Activate
    public void activate() {
        providerService = linkProviderRegistry.register(this);

        // Injeta links após o device Padtec estar registrado (~60s após ONOS subir)
        new Timer("padtec-link-inject", true).schedule(new TimerTask() {
            @Override
            public void run() {
                injectLinks();
            }
        }, INJECT_DELAY_MS);

        log.info("PadtecLinkProvider iniciado. Links serão injetados em {}s " +
                 "(arquivo: {}).", INJECT_DELAY_MS / 1000, LINKS_FILE);
    }

    @Deactivate
    public void deactivate() {
        if (providerService != null) {
            // Remove todos os links que este provider injetou
            providerService.linksVanished(PADTEC_ID);
            linkProviderRegistry.unregister(this);
            providerService = null;
        }
        log.info("PadtecLinkProvider encerrado. Links ópticos removidos da topologia.");
    }

    @Override
    public ProviderId id() {
        return PID;
    }

    // -----------------------------------------------------------------------
    // Link injection
    // -----------------------------------------------------------------------

    /**
     * Lê o arquivo de topologia e registra links bidirecionais no ONOS.
     * Cada entrada gera dois links: Padtec→Remoto e Remoto→Padtec.
     */
    private void injectLinks() {
        File f = new File(LINKS_FILE);
        if (!f.exists()) {
            log.info("Arquivo de topologia óptica não encontrado: {}. " +
                     "Para habilitar links no ONOS, crie o arquivo com o mapeamento " +
                     "Padtec↔Polatis (veja tools/padtec-links.example.json).", LINKS_FILE);
            return;
        }

        try {
            JsonNode links = new ObjectMapper().readTree(f);
            if (!links.isArray()) {
                log.warn("padtec-links.json deve ser um array JSON. " +
                         "Verifique o formato em tools/padtec-links.example.json.");
                return;
            }

            int count = 0;
            for (JsonNode entry : links) {
                int    padtecPort = entry.path("padtecPort").asInt(-1);
                String remoteDev  = entry.path("remoteDevice").asText("").trim();
                long   remotePort = entry.path("remotePort").asLong(-1);

                if (padtecPort < 1 || remoteDev.isEmpty() || remotePort < 1) {
                    log.warn("Entrada inválida ignorada em padtec-links.json: {}", entry);
                    continue;
                }

                ConnectPoint padtecCp = new ConnectPoint(
                        PADTEC_ID, PortNumber.portNumber(padtecPort));
                ConnectPoint remoteCp = new ConnectPoint(
                        DeviceId.deviceId(remoteDev), PortNumber.portNumber(remotePort));

                // Link bidirecional Padtec ↔ Polatis
                providerService.linkDetected(
                        new DefaultLinkDescription(padtecCp, remoteCp, Link.Type.OPTICAL));
                providerService.linkDetected(
                        new DefaultLinkDescription(remoteCp, padtecCp, Link.Type.OPTICAL));

                log.info("Link óptico: {}:{} <-> {}:{}",
                        PADTEC_ID, padtecPort, remoteDev, remotePort);
                count++;
            }

            if (count > 0) {
                log.info("{} link(s) óptico(s) injetado(s) no ONOS.", count);
            } else {
                log.warn("Nenhum link válido encontrado em {}.", LINKS_FILE);
            }

        } catch (Exception e) {
            log.error("Erro ao processar {}: {}", LINKS_FILE, e.getMessage());
        }
    }
}
