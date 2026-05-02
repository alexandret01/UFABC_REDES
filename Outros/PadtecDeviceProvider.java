package org.padtec.onos.provider;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.onosproject.net.*;
import org.onosproject.net.device.*;
import org.onosproject.net.provider.AbstractProvider;
import org.onosproject.net.provider.ProviderId;
import org.osgi.service.component.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.onlab.packet.ChassisId;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@Component(immediate = true)
public class PadtecDeviceProvider extends AbstractProvider implements DeviceProvider {

    private final Logger log = LoggerFactory.getLogger(getClass());

    // Injeta o serviço de registro de dispositivos do ONOS
    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected DeviceProviderRegistry providerRegistry;

    private DeviceProviderService providerService;
    private Thread pollThread;
    private boolean running = true;
    
    // O URI que aparecerá na interface gráfica do ONOS
    private final DeviceId padtecDeviceId = DeviceId.deviceId("padtec-provider:172.17.36.50:10151");
    // IP onde o seu Agente Java 18 está rodando
    private final String AGENT_IP = "172.17.36.231"; 
    private final int AGENT_PORT = 10151;

    public PadtecDeviceProvider() {
        super(new ProviderId("padtec", "org.padtec.onos.provider"));
    }

    @Activate
    public void activate() {
        providerService = providerRegistry.register(this);
        running = true;
        // Inicia a thread que vai ficar buscando o JSON do Agente
        pollThread = new Thread(this::pollAgent);
        pollThread.start();
        log.info("App Provedor Padtec Ativado.");
    }

    @Deactivate
    public void deactivate() {
        running = false;
        if (providerService != null) {
            providerService.deviceDisconnected(padtecDeviceId);
            providerRegistry.unregister(this);
            providerService = null;
        }
        log.info("App Provedor Padtec Desativado.");
    }

    // Thread para buscar dados na porta 10151 periodicamente (ex: a cada 10 segundos)
    private void pollAgent() {
        while (running) {
            try (Socket socket = new Socket(AGENT_IP, AGENT_PORT);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                
                StringBuilder jsonBuilder = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    jsonBuilder.append(line);
                }
                
                processPadtecJson(jsonBuilder.toString());
                
            } catch (Exception e) {
                log.warn("Falha ao conectar no Agente Padtec: {}", e.getMessage());
                if (providerService != null) {
                    providerService.deviceDisconnected(padtecDeviceId); // Marca como offline se cair
                }
            }
            
            try { Thread.sleep(10000); } catch (InterruptedException ignored) {}
        }
    }

    // Método que mapeia o seu JSON para entidades gráficas do ONOS
    private void processPadtecJson(String jsonStr) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            // ADICIONE ESTA LINHA: Habilita a leitura de valores como NaN, INF, etc.
            mapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
           
	    JsonNode root = mapper.readTree(jsonStr);

            // 1. Registra o Equipamento Pai (SPVL4)
            DeviceDescription deviceDescription = new DefaultDeviceDescription(
                    padtecDeviceId.uri(),
                    Device.Type.OPTICAL_AMPLIFIER, 
                    "Padtec",
                    "SPVL4 Controller",
                    "1.0",
                    "SN-172173650",
                    new ChassisId(),
                    DefaultAnnotations.builder().set("supervisor", root.get("supervisor").asText()).build()
            );
            providerService.deviceConnected(padtecDeviceId, deviceDescription);

            // 2. Registra os Transponders como Portas dentro do Equipamento
            JsonNode devices = root.get("devices");
            List<PortDescription> ports = new ArrayList<>();
            long portNumber = 1;

            if (devices != null && devices.isArray()) {
                for (JsonNode dev : devices) {
                    String type = dev.path("type").asText();
                    String name = dev.path("name").asText();
                    JsonNode metrics = dev.path("metrics");

                    // Adiciona os atributos como "Annotations" para aparecerem no ONOS GUI
                    DefaultAnnotations.Builder annotations = DefaultAnnotations.builder()
                            .set("TransponderName", name)
                            .set("Type", type);
                    
                    if (metrics.has("channel")) annotations.set("Channel", metrics.get("channel").asText());
                    if (metrics.has("isLOS")) annotations.set("isLOS", metrics.get("isLOS").asText());

                    PortDescription portDesc = DefaultPortDescription.builder()
                            .withPortNumber(PortNumber.portNumber(portNumber++))
                            .isEnabled(!metrics.path("isLOS").asBoolean(false)) // Se tem LOS (Loss of Signal), marca porta como DOWN
                            .type(Port.Type.OCH) // Tipo: Optical Channel
                            .portSpeed(10000)
                            .annotations(annotations.build())
                            .build();
                    
                    ports.add(portDesc);
                }
            }
            // Atualiza as portas gráficas do dispositivo
            providerService.updatePorts(padtecDeviceId, ports);

        } catch (Exception e) {
            log.error("Erro ao processar métricas do Padtec", e);
        }
    }

    // Métodos obrigatórios da interface DeviceProvider ignorados para simplificação
    @Override public void triggerProbe(DeviceId deviceId) {}
    @Override public void roleChanged(DeviceId deviceId, MastershipRole newRole) {}
    @Override public boolean isReachable(DeviceId deviceId) { return true; }
    @Override public void changePortState(DeviceId deviceId, PortNumber portNumber, boolean enable) {}
}
