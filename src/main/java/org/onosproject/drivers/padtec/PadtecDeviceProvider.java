package org.onosproject.drivers.padtec;

import org.onosproject.net.Device;
import org.onosproject.net.DeviceId;
import org.onosproject.net.MastershipRole;
import org.onosproject.net.PortNumber;
import org.onosproject.net.device.DefaultDeviceDescription;
import org.onosproject.net.device.DeviceDescription;
import org.onosproject.net.device.DeviceProvider;
import org.onosproject.net.device.DeviceProviderRegistry;
import org.onosproject.net.device.DeviceProviderService;
import org.onosproject.net.provider.ProviderId;
import org.onlab.packet.ChassisId;
import org.onosproject.net.DefaultAnnotations;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;

import java.util.Timer;
import java.util.TimerTask;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Provider customizado para injetar o equipamento Padtec no ONOS.
 * Como não usamos OpenFlow ou Netconf, este provider avisa o ONOS
 * que o equipamento existe, forçando o carregamento do Driver.
 */
@Component(immediate = true)
public class PadtecDeviceProvider implements DeviceProvider {

    private final Logger log = getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected DeviceProviderRegistry providerRegistry;

    private DeviceProviderService providerService;
    private static final ProviderId PID = new ProviderId("padtec", "org.onosproject.drivers.padtec");

    // IP Fixo do laboratório conforme os scripts (pode ser expandido depois)
    private static final String PADTEC_IP = "172.17.36.50";
    private static final DeviceId DEVICE_ID = DeviceId.deviceId("padtec:" + PADTEC_IP);

    @Activate
    public void activate() {
        providerService = providerRegistry.register(this);
        log.info("Padtec Device Provider Started");

        // Injeta o dispositivo no ONOS com um delay maior (45s) para garantir 
        // que o ONOS tenha terminado de descobrir os outros switches (Polatis) e 
        // carregado o padtec-drivers.xml com folga, além do agente TailEnd em Java 18 estar no ar.
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                injectDevice();
            }
        }, 45000);
    }

    private void injectDevice() {
        log.info("Injetando equipamento Padtec {} no core do ONOS...", DEVICE_ID);
        
        DeviceDescription desc = new DefaultDeviceDescription(
                DEVICE_ID.uri(),
                Device.Type.TERMINAL_DEVICE,
                "Padtec",
                "SPVL4",
                "1.0",
                "Jaquison",
                new ChassisId(),
                true,
                DefaultAnnotations.EMPTY
        );
        
        // Isso avisa o ONOS: "Ei, achei um dispositivo!"
        // O ONOS vai registrar e em seguida chamar os Behaviours do driver para ler as portas.
        providerService.deviceConnected(DEVICE_ID, desc);
    }

    @Deactivate
    public void deactivate() {
        if (providerService != null) {
            providerService.deviceDisconnected(DEVICE_ID);
            providerRegistry.unregister(this);
            providerService = null;
        }
        log.info("Padtec Device Provider Stopped");
    }

    @Override
    public ProviderId id() {
        return PID;
    }

    @Override
    public void triggerProbe(DeviceId deviceId) {
        // Ignorado, o driver cuida das probes via PortStatisticsDiscovery
    }

    @Override
    public void roleChanged(DeviceId deviceId, MastershipRole newRole) {
        providerService.receivedRoleReply(deviceId, newRole, MastershipRole.MASTER);
    }

    @Override
    public boolean isReachable(DeviceId deviceId) {
        return true;
    }

    @Override
    public void changePortState(DeviceId deviceId, PortNumber portNumber, boolean enable) {
        // Read-only
    }
}
