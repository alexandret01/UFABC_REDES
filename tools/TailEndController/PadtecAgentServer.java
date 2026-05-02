import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PadtecAgentServer implements Runnable {
    private final int port = 10151;
    // Variável que guarda o último JSON gerado pelos TRAPs do Jaquison
    private volatile String lastJsonData = "{}"; 

    // Método que o Jaquison.java chamará sempre que receber um TRAP novo
    public void updateMetrics(String novoJson) {
        this.lastJsonData = novoJson;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[Agente] Servidor TCP rodando na porta " + port);
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
                    
                    System.out.println("[Agente] Conexão recebida do ONOS: " + clientSocket.getInetAddress());
                    // Envia o JSON atualizado e fecha a conexão (Polling mode)
                    out.println(lastJsonData);
                    
                } catch (IOException e) {
                    System.err.println("Erro na comunicação com o cliente: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
