import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;

// Imports reais do seu install_gl.jar e dependências da Padtec/UFABC
import br.com.padtec.v3.data.ne.NE;
import br.com.padtec.v3.data.ne.Amplifier;
import br.com.padtec.v3.data.ne.Transponder;
import br.com.padtec.v3.data.ne.TrpOTNTerminal;
import br.ufabc.equipment.Amplifiers;
import br.ufabc.equipment.OTNTransponder;
import br.ufabc.equipment.Supervisor;
import br.ufabc.equipment.Transponders;

/**
 * Middleware Externo para o Equipamento Padtec.
 * 
 * Este servidor se conecta nativamente aos equipamentos físicos usando
 * as bibliotecas do "install_gl.jar" e expõe as portas em JSON para o ONOS.
 */
public class PadtecMiddleware {

    public static void main(String[] args) throws Exception {
        // Inicia um servidor HTTP na porta 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        // Endpoint para descobrir as portas de um IP específico
        server.createContext("/api/padtec/ports", new PortsHandler());
        
        server.setExecutor(null);
        server.start();
        System.out.println("Middleware Padtec rodando na porta 8080...");
        System.out.println("Aguardando chamadas do ONOS...");
    }

    static class PortsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            // Pega o IP que o ONOS mandou na query string
            String query = t.getRequestURI().getQuery();
            String ip = query != null ? query.split("=")[1] : "172.17.36.50";

            System.out.println("ONOS solicitou dados físicos do equipamento: " + ip);

            StringBuilder jsonResponse = new StringBuilder();
            jsonResponse.append("[");

            try {
                // LÓGICA ORIGINAL DO SEU JAQUISON.JAVA
                Supervisor sup = new Supervisor(ip, Supervisor.TypeSupervisor.SPVL);
                sup.start();
                
                System.out.println("Conectado ao Supervisor. Buscando NEs (Pode demorar uns segundos)...");
                // Aguarda o supervisor carregar os dados físicos do equipamento
                Thread.sleep(10000); 

                ArrayList<NE> monitored = new ArrayList<>();
                
                if (!Amplifiers.getAmplifiers(sup).isEmpty()) {
                    monitored.addAll(Amplifiers.getAmplifiers(sup));
                }
                if (!Transponders.getTransponders(sup).isEmpty()) {
                    monitored.addAll(Transponders.getTransponders(sup));
                }

                int portCounter = 1;
                boolean isFirst = true;

                if (monitored.size() > 0) {
                    for (NE ne : monitored) {
                        if (!isFirst) {
                            jsonResponse.append(",");
                        }
                        isFirst = false;

                        if (ne instanceof Amplifier) {
                            Amplifier amp = (Amplifier) ne;
                            Amplifiers amplifier = new Amplifiers(sup, amp);
                            
                            // Monta o JSON da porta do Amplificador
                            jsonResponse.append(String.format(
                                "{\"portNumber\": %d, \"name\": \"%s\", \"type\": \"FIBER\", \"enabled\": true, \"gain\": %s}",
                                portCounter++, amp.getName(), String.valueOf(amplifier.getGain())
                            ));
                            
                        } else if (ne instanceof Transponder || ne instanceof TrpOTNTerminal) {
                            Transponder transp = (Transponder) ne;
                            Transponders transponder = new Transponders(sup, transp);
                            
                            // Monta o JSON da porta do Transponder
                            jsonResponse.append(String.format(
                                "{\"portNumber\": %d, \"name\": \"%s\", \"type\": \"OCH\", \"enabled\": %b, \"channel\": \"%s\"}",
                                portCounter++, transp.getName(), !transponder.isLOS(), transponder.getChannel()
                            ));
                        }
                    }
                } else {
                    System.out.println("Aviso: Nenhum Amplificador ou Transponder foi retornado pelo Supervisor.");
                }
                
            } catch (Exception e) {
                System.err.println("Erro ao comunicar com a Padtec: " + e.getMessage());
                e.printStackTrace();
            }

            jsonResponse.append("]");

            // Envia a resposta de volta para o ONOS
            t.getResponseHeaders().add("Content-Type", "application/json");
            byte[] responseBytes = jsonResponse.toString().getBytes("UTF-8");
            t.sendResponseHeaders(200, responseBytes.length);
            
            OutputStream os = t.getResponseBody();
            os.write(responseBytes);
            os.close();
            
            System.out.println("Dados enviados com sucesso para o ONOS!");
        }
    }
}
