package org.onosproject.drivers.padtec;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

/**
 * Gerencia o ciclo de vida do processo externo PadtecMonitorJSON3.
 *
 * Quando o bundle ONOS ativa, este componente lança automaticamente o
 * processo Java 18 (PadtecMonitorJSON3 + PadtecAgentServer) que:
 *   1. Conecta ao hardware Padtec (Supervisor 172.17.36.50)
 *   2. Coleta métricas (Amplificadores e Transponders)
 *   3. Expõe os dados via servidor TCP na porta 10151
 *
 * O PadtecDeviceProvider se conecta a esse servidor (127.0.0.1:10151)
 * para ler o JSON e publicar as portas no core do ONOS.
 *
 * Isso elimina a necessidade de executar monitor.sh manualmente.
 */
@Component(immediate = true)
public class PadtecManager {

    private final Logger log = LoggerFactory.getLogger(getClass());

    // ---------------------------------------------------------------
    // Configuração do processo externo — ajuste conforme o ambiente
    // ---------------------------------------------------------------
    /** Binário Java 18 (necessário para as libs proprietárias da Padtec). */
    private static final String JAVA18 = "/usr/lib/jvm/jdk-18.0.2.1/bin/java";

    /** Diretório raiz do TailController (onde estão lib/ e os .class). */
    private static final String TAIL_DIR = "/home/sdn/TailController";

    /** Classe principal do agente Padtec. */
    private static final String MAIN_CLASS = "PadtecMonitorJSON3";

    /** Porta TCP onde o PadtecAgentServer escuta. */
    private static final int AGENT_PORT = 10151;

    /** Arquivo de log do processo externo (visível com: tail -f /tmp/padtec_monitor.log). */
    private static final String LOG_FILE = "/tmp/padtec_monitor.log";
    // ---------------------------------------------------------------

    private Process monitorProcess;

    @Activate
    protected void activate() {
        log.info("Padtec Integration Layer: iniciando...");

        // Se o servidor TCP já está ativo (processo iniciado externamente), reutiliza.
        if (isAgentRunning()) {
            log.info("PadtecAgentServer já está ativo na porta {}. Reutilizando.", AGENT_PORT);
            return;
        }

        launchMonitorProcess();
    }

    /**
     * Lança o processo Java 18 equivalente ao monitor.sh:
     *   java -Djava.library.path=<TAIL_DIR>/lib
     *        -cp "<TAIL_DIR>/lib/*:<TAIL_DIR>/lib/commons-digester-1.7.zip:<TAIL_DIR>"
     *        PadtecMonitorJSON3
     */
    private void launchMonitorProcess() {
        File workDir = new File(TAIL_DIR);
        if (!workDir.exists() || !workDir.isDirectory()) {
            log.error("Diretório TailController não encontrado: {}. " +
                      "Coloque os arquivos do monitor em {} e reinicie o bundle.",
                      TAIL_DIR, TAIL_DIR);
            return;
        }

        File java18 = new File(JAVA18);
        if (!java18.exists()) {
            log.error("Java 18 não encontrado em {}. Ajuste a constante JAVA18 em PadtecManager.",
                      JAVA18);
            return;
        }

        // Classpath: todos os .jar em lib/, o commons-digester zip, e o diretório raiz
        String classpath = TAIL_DIR + "/lib/*"
                         + ":" + TAIL_DIR + "/lib/commons-digester-1.7.zip"
                         + ":" + TAIL_DIR;

        ProcessBuilder pb = new ProcessBuilder(
                JAVA18,
                "-Dorg.apache.logging.log4j.level=INFO",
                "-Djava.library.path=" + TAIL_DIR + "/lib",
                "-cp", classpath,
                MAIN_CLASS
        );
        pb.directory(workDir);
        pb.redirectErrorStream(true);
        pb.redirectOutput(new File(LOG_FILE));

        try {
            monitorProcess = pb.start();
            log.info("PadtecMonitorJSON3 iniciado (Java 18). " +
                     "Aguardando servidor TCP na porta {}... (log: {})", AGENT_PORT, LOG_FILE);

            // Aguarda o servidor TCP ficar disponível (máx 30s)
            waitForAgent(30);

        } catch (IOException e) {
            log.error("Falha ao iniciar PadtecMonitorJSON3: {}", e.getMessage());
        }
    }

    /**
     * Espera até maxSeconds segundos pelo PadtecAgentServer ficar acessível.
     */
    private void waitForAgent(int maxSeconds) {
        for (int i = 0; i < maxSeconds; i++) {
            if (isAgentRunning()) {
                log.info("PadtecAgentServer disponível na porta {} após {}s.", AGENT_PORT, i);
                return;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        log.warn("PadtecAgentServer ainda não respondeu após {}s. " +
                 "Verifique o log em {}.", maxSeconds, LOG_FILE);
    }

    /**
     * Testa se algo já está escutando na porta AGENT_PORT (localhost).
     */
    private boolean isAgentRunning() {
        try (Socket s = new Socket("127.0.0.1", AGENT_PORT)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Deactivate
    protected void deactivate() {
        log.info("Padtec Integration Layer: encerrando...");
        if (monitorProcess != null && monitorProcess.isAlive()) {
            monitorProcess.destroyForcibly();
            log.info("Processo PadtecMonitorJSON3 encerrado.");
        }
    }
}
