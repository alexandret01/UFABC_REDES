package br.ufabc.controlplane.testes;

import java.util.TimerTask;

import br.ufabc.controlplane.ControlPlane;
import br.ufabc.controlplane.ControlPlaneException;
import br.ufabc.dataplane.DataPlane;

public class SetGanhoBoosterTeste extends TimerTask{
	DataPlane dataPlane;
	ControlPlane controlPlane;
	int gain;
	public SetGanhoBoosterTeste(int gain){
		controlPlane = ControlPlane.getInstance();
		dataPlane = controlPlane.getDataPlane();
		this.gain = gain;
	}
	public void run() {
		try {
			String name = controlPlane.getName();
			if(dataPlane != null){
				if (dataPlane.hasAmplifierOut() && dataPlane.isTransmiting()){
					System.out.println("Configurando ganho do booster para "+ gain +" em " + name);
					dataPlane.setGain(dataPlane.getAmplifierOut(), gain);
				}
				
			} else {
				throw new ControlPlaneException("Plano de dados não foi iniciado");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}

}
