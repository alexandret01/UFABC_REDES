package br.ufabc.polatis;


import br.com.padtec.v3.data.ne.NE;
import br.com.padtec.v3.data.ne.Transponder;
import br.ufabc.controlplane.metropad.Servidor;
import br.ufabc.controlplane.metropad.Servidor.TypeSupervisor;
import br.ufabc.polatis.PolatisOXC.AlarmTriggerType;

/*
 * Created on 06/12/2011.
 */

/**
 * @author Gustavo Sousa Pavani
 * @version 1.0
 */
public class Claudecir {
	public static Servidor server;

	public Claudecir() {

	}
	
	public static void main(String args[]) {
		//Configura o OXC Polatis
		//Inicialiaza o OXC
		SNMPClient polatis = null;
		//SNMPClient polatis = PolatisOXC.init();
		try { //Configura para o experimento
			/*			//Informaçoes basicas
			System.out.println("Description: "+polatis.getAsString(PolatisOXC.sysDescr));
			System.out.println("UpTime: "+polatis.getAsString(PolatisOXC.upTime));
			//Reset any protection between port 1 and 2
			System.out.println("Reset protection: "+PolatisOXC.resetProtection(1,2));
			//Habilita portas 1 e 2 de entrada e 9 de saída
			System.out.println("Status: "+PolatisOXC.setStateOXCInputPort(1, true));
			System.out.println("Status: "+PolatisOXC.setStateOXCInputPort(2, true));
			System.out.println("Status: "+PolatisOXC.setStateOXCInputPort(9, true));
			//Seta o VOA e offset da porta de saída 9 para zero
			System.out.println("VOA Mode: " +PolatisOXC.setVOAMode(9,PolatisOXC.VOAMode.NONE));
			System.out.println("Offset: "+PolatisOXC.setPowerLevelOffset(9,0.0));
			//Seta comprimento de onda e o averaging time da porta de saída 9
			System.out.println("OPM wavelength: " + PolatisOXC.setOPMWavelength(9, 1540.5)); //Canal C28 (1540.54 nm)
			//System.out.println("OPM wavelength: " + PolatisOXC.setOPMWavelength(9, 1310)); //10GBase-LR
			System.out.println("Averaging Time: " + PolatisOXC.setAveragingTime(9,PolatisOXC.AveragingTime.Fifty));
			//Conecta a porta 1 na porta 9
			System.out.println("Connect: "+ PolatisOXC.setConnectPort (1, PolatisOXC.ConnectPort.PORT_9));
			//Seta os alarmes e limiares da porta 9
			System.out.println("Alarm mode: "+PolatisOXC.setAlarmModeOption(9,PolatisOXC.AlarmMonitorPower.CONTINUOUS));
			System.out.println("Alarm edge: "+PolatisOXC.setAlarmEdgeOption(9,AlarmTriggerType.LOW));
			System.out.println("Alarm Threshold Low: "+PolatisOXC.setAlarmThresholdLow(9,-35.0));
			System.out.println("Alarm Threshold High: "+PolatisOXC.setAlarmThresholdHigh(9,5.0));
			// Set protection for working 1 in port 2 - trigger at 9 (APS)
			System.out.println("Protection: "+PolatisOXC.setProtectionGroup(1,2,9,true));
			//Second case - Polatis attenuation
			System.out.println("Status: "+PolatisOXC.setStateOXCInputPort(6, true));
			System.out.println("Status: "+PolatisOXC.setStateOXCInputPort(12, true));			
			System.out.println("Connect: "+ PolatisOXC.setConnectPort (6, PolatisOXC.ConnectPort.PORT_12));
			//Start trap listening
			//PolatisOXC.startTrapListening("172.17.36.226/162");
*/
		} catch (Exception e) {e.printStackTrace();}
		/*
		// Inicializa o supervisor do canal experimental
		server = Servidor.getInstance();
		server.setIp("172.17.36.30");
		server.start(TypeSupervisor.SPVL);
		try {
			System.out.println("Vou dormir 20 segundos para carregar os NE...");
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// Obter elementos do sistema de comunicacao
		Transponder transponder = getTransponder(TypeSupervisor.SPVL);
		TrpOTNTerminal terminal = null;
		if(transponder instanceof TrpOTNTerminal){
			terminal = (TrpOTNTerminal)transponder;
			//System.out.println("*");
		}
		if (transponder != null) {
			//System.out.println("**");
			MonitorClaudecir monitor = new MonitorClaudecir(server, terminal);
			monitor.start();
		}
		*/
	}
	
	public static Transponder getTransponder(TypeSupervisor type){
		for (NE ne: Servidor.getInstance().getAllNeIntoColector(type)){
			if (ne instanceof Transponder){
				return (Transponder)ne;
			
			}

		}

			
		return null;
		
	}
}
