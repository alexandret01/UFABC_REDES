import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * Agente Padtec SIMPLIFICADO.
 * Este servidor não tem dependências externas e apenas retorna um JSON fixo
 * para validar a comunicação com o Driver do ONOS.
 */
public class PadtecAgent {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(10151), 0);
        server.createContext("/get-metrics", new MetricsHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Agente Padtec SIMPLES rodando na porta 10151...");
    }

    static class MetricsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            System.out.println("ONOS conectou e pediu os dados!");
            
            String jsonResponse = "{" +
                "\"devices\": [" +
                    "{" +
                        "\"type\": \"Amplifier\"," +
                        "\"name\": \"Amp-Simulado-1\"," +
                        "\"metrics\": {\"gain\": 12.5, \"isLOS\": false}" +
                    "}," +
                    "{" +
                        "\"type\": \"Transponder\"," +
                        "\"name\": \"Transponder-Simulado-1\"," +
                        "\"metrics\": {\"channel\": \"C21\", \"isLOS\": false}" +
                    "}" +
                "]" +
            "}";

            t.getResponseHeaders().add("Content-Type", "application/json");
            t.sendResponseHeaders(200, jsonResponse.length());
            OutputStream os = t.getResponseBody();
            os.write(jsonResponse.getBytes());
            os.close();
        }
    }
}
