package br.ufabc.controlplane.metropad;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class PadtecAgentServer implements Runnable {
    private final int port = 10151;
    private volatile String lastJsonData = "{}"; 

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
                    out.println(lastJsonData);
                    
                } catch (IOException e) {
                    System.err.println("Erro na comunicação com o cliente: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Thread(new PadtecAgentServer()).start();
    }
}
