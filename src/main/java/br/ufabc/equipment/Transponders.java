package br.ufabc.equipment;

import br.com.padtec.v3.data.ne.Transponder;
import br.com.padtec.v3.data.ne.NE;
import java.util.ArrayList;
import java.util.List;

public class Transponders {
    private Supervisor sup;
    private Transponder transp;

    public Transponders(Supervisor sup, Transponder transp) {
        this.sup = sup;
        this.transp = transp;
    }

    public static List<NE> getTransponders(Supervisor sup) {
        // Stub
        List<NE> list = new ArrayList<>();
        list.add(new Transponder("Transponder-OTN-Mock"));
        return list;
    }

    public String getChannel() {
        return "CH-1"; // Stub channel
    }

    public boolean isLOS() {
        return false; // Stub Loss of Signal
    }

    public double getInputPower() {
        return -10.0;
    }

    public double getOutputPower() {
        return 5.0;
    }

    public double getLambda() {
        return 1550.12;
    }
}
