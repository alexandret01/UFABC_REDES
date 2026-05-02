package br.ufabc.controlplane.net;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class NetworkAddressOperation {

    private static InetAddress cachedAddress = null;

    // Mantendo a compatibilidade com binários legados
    public enum TypeIP {
        IPv4, IPv6
    }

    public static InetAddress getLocalAddress() {
        return getLocalAddress(TypeIP.IPv4);
    }

    public static int getLocalAddressAsInt() {
        try {
            InetAddress addr = getLocalAddress();
            if (addr == null) return 0;
            byte[] b = addr.getAddress();
            if (b.length == 4) {
                return ((b[0] & 0xFF) << 24) | ((b[1] & 0xFF) << 16) | ((b[2] & 0xFF) << 8) | ((b[3] & 0xFF) << 0);
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public static InetAddress getLocalAddress(TypeIP type) {
        // Cache: Só usa se for um IP válido e NÃO for loopback (127.0.0.1)
        if (cachedAddress != null && !cachedAddress.isLoopbackAddress()) {
            // Verifica se o tipo bate (assumindo IPv4 como padrão)
            if (type == TypeIP.IPv4 && cachedAddress instanceof Inet4Address) return cachedAddress;
        }

        InetAddress bestCandidate = null;
        int maxScore = -9999;

        try {
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            if (netInterfaces == null) return InetAddress.getLocalHost();

            while (netInterfaces.hasMoreElements()) {
                NetworkInterface ni = netInterfaces.nextElement();
                if (ni == null) continue;

                // Análise da Interface
                String name = ni.getName().toLowerCase();
                boolean isUp = ni.isUp(); // Pode falhar em alguns linux, mas ajuda na pontuação
                boolean isLoopback = ni.isLoopback();
                
                // --- SISTEMA DE PONTUAÇÃO ---
                int currentScore = 0;

                if (isLoopback) currentScore -= 1000; // Penalidade máxima para localhost
                if (!isUp) currentScore -= 500;       // Penalidade para interface Down

                // Tipos de Interface
                if (name.startsWith("eno") || name.startsWith("enp") || name.startsWith("eth") || name.startsWith("wlan")) {
                    currentScore += 100; // Preferência por Físicas reais
                } else if (name.startsWith("br-") || name.startsWith("docker") || name.startsWith("virbr") || name.startsWith("veth")) {
                    currentScore -= 50;  // Penalidade para Virtuais/Docker
                } else if (name.startsWith("ppp") || name.startsWith("tun")) {
                    currentScore -= 20;  // Penalidade leve para túneis VPN
                }

                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    
                    // Filtra pelo tipo (IPv4/IPv6)
                    boolean isCorrectType = (type == TypeIP.IPv4 && addr instanceof Inet4Address) ||
                                            (type == TypeIP.IPv6 && addr instanceof Inet6Address);

                    if (!isCorrectType) continue;

                    int addrScore = currentScore;

                    // Análise do Endereço
                    if (addr.isSiteLocalAddress()) {
                        addrScore += 50; // Bônus alto para IPs privados (192.168, 172.16+, 10.)
                        
                        // Bônus Extra para subnets comuns de gerenciamento (ex: 172.17.x.x)
                        // Isso ajuda a desempatar entre Docker (172.17.0.1) e Rede Real (172.17.84.209)
                        // Se a interface for física E site-local, ganha +150 total.
                    }
                    
                    if (addr.isLoopbackAddress()) addrScore = -1000;

                    // Verifica se esse é o novo campeão
                    if (addrScore > maxScore) {
                        maxScore = addrScore;
                        bestCandidate = addr;
                        // Log para debug (aparecerá no console)
                        // System.out.println("NetworkAddressOperation: Novo candidato: " + addr.getHostAddress() + " (" + name + ") Score: " + maxScore);
                    }
                }
            }

            if (bestCandidate != null) {
                //System.out.println("NetworkAddressOperation: IP Selecionado: " + bestCandidate.getHostAddress() + " (Score: " + maxScore + ")");
                System.out.println("NetworkAddressOperation: IP Selecionado: " + bestCandidate.getHostAddress());
                cachedAddress = bestCandidate;
                return bestCandidate;
            }

            return InetAddress.getLocalHost();

        } catch (Exception e) {
            try { return InetAddress.getLocalHost(); } catch (Exception ex) { return null; }
        }
    }
    
    // Método MAIN para teste isolado no terminal
    public static void main(String[] args) {
        System.out.println("--- Teste de Seleção de IP ---");
        InetAddress ip = getLocalAddress();
        System.out.println("Resultado Final: " + (ip != null ? ip.getHostAddress() : "null"));
    }
}