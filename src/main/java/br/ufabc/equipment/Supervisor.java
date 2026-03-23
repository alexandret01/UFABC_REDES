package br.ufabc.equipment;

public class Supervisor {
    public enum TypeSupervisor { SPVL }
    
    private String ip;
    private TypeSupervisor type;

    public Supervisor(String ip, TypeSupervisor type) {
        this.ip = ip;
        this.type = type;
    }

    public void start() {
        System.out.println("Supervisor started for IP " + ip);
    }
}
