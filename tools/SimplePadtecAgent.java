import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Um agente simples embutido para testes de integração.
 * Ele ouve na porta TCP 10151 e, quando o ONOS se conecta, envia
 * um JSON mockado simulando os dados da Padtec.
 */
public class SimplePadtecAgent {

    public static void main(String[] args) {
        int port = 10151;
        System.out.println("Iniciando SimplePadtecAgent na porta " + port + "...");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                System.out.println("Aguardando conexão do ONOS (Driver TCP)...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("ONOS conectado: " + clientSocket.getRemoteSocketAddress());

                String mockJson = "[\n" +
                        "  {\n" +
                        "    \"type\": \"Amplifier\",\n" +
                        "    \"name\": \"Amp-01\",\n" +
                        "    \"metrics\": {\n" +
                        "      \"gain\": 15.5,\n" +
                        "      \"isLOS\": false\n" +
                        "    }\n" +
                        "  },\n" +
                        "  {\n" +
                        "    \"type\": \"OTNTransponder\",\n" +
                        "    \"name\": \"Transponder-01\",\n" +
                        "    \"metrics\": {\n" +
                        "      \"channel\": \"CH-1\",\n" +
                        "      \"isLOS\": false\n" +
                        "    }\n" +
                        "  }\n" +
                        "]";

                OutputStream out = clientSocket.getOutputStream();
                out.write(mockJson.getBytes(StandardCharsets.UTF_8));
                out.flush();
                out.close();
                clientSocket.close();
                System.out.println("JSON mockado enviado para o ONOS com sucesso.");
            }
        } catch (IOException e) {
            System.err.println("Erro no SimplePadtecAgent: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
