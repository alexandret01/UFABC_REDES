package br.ufabc.equipment;

import br.com.padtec.v3.data.ne.Amplifier;
import br.com.padtec.v3.data.ne.NE;
import java.util.ArrayList;
import java.util.List;

public class Amplifiers {
    private Supervisor sup;
    private Amplifier amp;

    public Amplifiers(Supervisor sup, Amplifier amp) {
        this.sup = sup;
        this.amp = amp;
    }

    public static List<NE> getAmplifiers(Supervisor sup) {
        // Stub: Em produção, isso fará chamadas reais via socket/SNMP
        List<NE> list = new ArrayList<>();
        list.add(new Amplifier("Amp-SPVL-Mock"));
        return list;
    }

    public double getGain() {
        return 15.5; // Stub gain
    }

    public double getPowerInput() {
        return -5.0;
    }

    public double getPowerOutput() {
        return 10.5;
    }

    public boolean isAGC() {
        return true;
    }

    public boolean isLOS() {
        return false;
    }
}
