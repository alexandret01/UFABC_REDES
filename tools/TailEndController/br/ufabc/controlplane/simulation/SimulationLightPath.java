package br.ufabc.controlplane.simulation;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import br.com.padtec.v3.data.Command;
import br.com.padtec.v3.data.impl.T100D_GTSintonizavel_Impl;
import br.com.padtec.v3.data.ne.ClientInterface;
import br.com.padtec.v3.data.ne.NE;
import br.com.padtec.v3.data.ne.OTNInterface;
import br.com.padtec.v3.data.ne.OpticalInterface;
import br.com.padtec.v3.data.ne.PBAmp;
import br.com.padtec.v3.data.ne.Transponder;
import br.com.padtec.v3.data.ne.TransponderOTN;
import br.com.padtec.v3.data.ne.TrpOTNTerminal;
import br.com.padtec.v3.util.modelparser.TransponderModelParser;
import br.ufabc.controlplane.TimeControl;
import br.ufabc.controlplane.metropad.AmplifierCommands;
import br.ufabc.controlplane.metropad.Servidor;
import br.ufabc.controlplane.metropad.TransponderCommands;
import br.ufabc.controlplane.metropad.Servidor.TypeSupervisor;

/**
 * This class does the simulation to install a light path between two peers
 * using the padtec metropad equipament.
 * 
 * */
public class SimulationLightPath {

	public static String SPVL_IP = "10.2.22.5";
	public static String SPVJ_IP = "10.2.22.4";
	public static long start;
	public static long now;
	public static boolean fim = false;
	public static boolean continua = true;
	
	
	public String getServerTime() {
		DateFormat df = new SimpleDateFormat("HH:mm");
		return df.format(new Date());
	}
	
	

	public TimeZone getTimeZone()	{
		return TimeZone.getDefault();
	}

	public static void doConnection(String ip, Servidor.TypeSupervisor type){
		System.out.println("doing connection on supervisor type: " + type + ", by ip: "+ ip);
		Servidor server = Servidor.getInstance();
		server.setIp(ip);
		server.start(type);
	}

	/**
	 * 
	 * */
	public static Transponder getTransponder(TypeSupervisor type){
		for (NE ne: Servidor.getInstance().getAllNeIntoColector(type)){
			if (ne instanceof Transponder){
				return (Transponder)ne;
			}

		}
		return null;
	}

	public static void analyseTransponder(TransponderOTN tr) {
		OpticalInterface ladoA = null;
		OpticalInterface ladoB = null;   

		NumberFormat format = NumberFormat.getNumberInstance();
		format.setMaximumFractionDigits(2);
		format.setMinimumFractionDigits(2);
		format.setGroupingUsed(false);

		TrpOTNTerminal transponder = (TrpOTNTerminal)tr;
		ladoA = transponder.getOpticalWDMInterface();
		ladoB = transponder.getOpticalClientInterface();

		OTNInterface otn = transponder.getOTN_WDMInterface();
		System.out.println("OTN Interface");
		System.out.println("\tLOS Sync :" + otn.isLosSync());
		System.out.println("\tLOF: " + otn.isLof());

		ClientInterface c = transponder.getClientInterface();
		System.out.println("Client Interface");
		System.out.println("\tLOS Sync 2 :" + c.isLosSync());
		System.out.println("\tLOF 2: " + c.isLof());

		if (!(Double.isNaN(ladoA.getModuleTemperature()))) {
			System.out.println("Temperatura lado 1: " + ladoA.getModuleTemperature());
		}

		if (!(Double.isNaN(ladoB.getModuleTemperature()))) {
			System.out.println("Temperatura lado 2: " + ladoB.getModuleTemperature());
		}

		if (ladoA != null) {
			System.out.println("Optical Interface Lado A:");
			System.out.println("\tLOS: " + ladoA.isLos());
			System.out.println("\tFail: " + ladoA.isFail());
			System.out.println("\tLaser off: " + ladoA.isLaserOff());
			System.out.println("\tPin: " + ladoA.getPin());
			System.out.println("\tPout: " + ladoA.getPout());
			System.out.println("\tLambda Real: " + ladoA.getLambdaReal());
			if (ladoA.isDense())
				System.out.println("\tChannel: " + ladoA.getChannel() + 
						" (" + format.format(ladoA.getLambdaNominal()) + " nm)");
			else {
				System.out.println("Channel: " + ladoA.getChannel());
			}
			if ( ladoA.getLaserTemperature().isEnabled() )
				System.out.println("Temperatura do Laser : " + ladoA.getLaserTemperature());
		}

		if (ladoB != null) {
			System.out.println("Optical Interface Lado B:");
			System.out.println("\tLOS: " + ladoB.isLos());
			System.out.println("\tFail: " + ladoB.isFail());
			System.out.println("\tLaser off: " + ladoB.isLaserOff());
			System.out.println("\tPin: " + ladoB.getPin());
			System.out.println("\tPout: " + ladoB.getPout());
			System.out.println("\tLambda Real: " + ladoB.getLambdaReal());
			if (ladoA.isDense())
				System.out.println("\tChannel: " + ladoB.getChannel() + 
						" (" + format.format(ladoB.getLambdaNominal()) + " nm)");
			else {
				System.out.println("Channel: " + ladoB.getChannel());
			}

			if ( ladoB.getLaserTemperature().isEnabled() )
				System.out.println("Temperatura do Laser : " + ladoB.getLaserTemperature());
			
			
		}



		Map<String, String> parsed = TransponderModelParser.parse(tr.getModel());
		String caracteristica = parsed.get("caracteristica");
		if ((caracteristica != null) && (caracteristica.compareTo("T") == 0) ) {
			System.out.println("Taxa: 10GbE");
		}

	}

	public static void main(String[] args) {

		Timer timer = TimeControl.getInstance();
		Servidor server = Servidor.getInstance();
		doConnection(SPVL_IP, TypeSupervisor.SPVL);
		timer.schedule(new ExecuteCommandTask(), 10000);
		timer.schedule(new PrintCaracteristicas(), 20000);
		start = System.currentTimeMillis();
//		try {
//			Thread.sleep(20000);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		boolean desligar = false;
		while (!desligar){
			if (fim){
				desligar = true;
				now = System.currentTimeMillis();
				System.out.println("Tempo de simulação: " + (now - start)/1000  + "s");
			}
		}
		

	}


}


class ExecuteCommandTask extends TimerTask{

	private int option;
	private TypeSupervisor type;

	public ExecuteCommandTask(TypeSupervisor type, int option) {
		this.option = option;
		this.type = type;
	}
	public ExecuteCommandTask() {
	
	}

	@Override
	public void run() {
//		Transponder t = SimulationLightPath.getTransponder(TypeSupervisor.SPVL);
		SimulationLightPath.now = System.currentTimeMillis();
//		switch (option) {

//		case 0: 
			
			System.out.println("DESLIGANDO LASER EM "  + (SimulationLightPath.now-SimulationLightPath.start)/1000 + "s");
			Transponder t = SimulationLightPath.getTransponder(TypeSupervisor.SPVL);
			if (t != null){
				ArrayList<Command> commands = new ArrayList<Command>();
				if(t instanceof TrpOTNTerminal){
					TrpOTNTerminal otn = (TrpOTNTerminal)t;
					if (!otn.getOpticalWDMInterface().isLaserOff()){
						Command cmd = TransponderCommands.getCommandTurnOffLaserWDM(t.getSerial());
						commands.add(cmd);
					}
				}
				Command cmd2 = TransponderCommands.getCommandResetErrorRateCounters(t.getSerial());
				commands.add(cmd2);
				Servidor.getInstance().doCommand(commands, t.getSupAddress());
			}
			
			for (NE ne : Servidor.getInstance().getAllNeIntoColector(TypeSupervisor.SPVJ)){
				//Amplificador Booster
				if (ne instanceof PBAmp && ne.getSlot() == 9){
//					PBAmp aplificador = (PBAmp)ne;
					Command resetGainCMD = AmplifierCommands.getCommandSetAGCGain(ne.getSerial(), 0); 
				}
			}
			
//			Timer timer = TimeControl.getInstance();
//			System.out.println("agendando religamento do laser para 60 segundos" );
			//###################################################################
			// Agenda esta o religamento do laser para 60s e a impressão das características para 80s
//			timer.schedule(new ExecuteCommandTask(TypeSupervisor.SPVL, 1), 60000);
//			timer.schedule(new PrintCaracteristicas(), 120000);
//			break;
		/*case 1:
			if (t != null){
				if(t instanceof T100D_GTSintonizavel_Impl && 
						((T100D_GTSintonizavel_Impl)t).getOpticalWDMInterface().isLaserOff()){
					System.out.println("RELIGANDO LASER EM " + (SimulationLightPath.now-SimulationLightPath.start)/1000 + "s");
					Command cmd = TransponderCommands.getCommandTurnOnLaserWDM(t.getSerial());
					Servidor.getInstance().doCommand(cmd, type);
				}

			}
			SimulationLightPath.continua = false;
			break;

		default:
			break;
		}*/


	}
	
	
}

class PrintCaracteristicas extends TimerTask{

	Transponder t;

	public PrintCaracteristicas() {
	}

	@Override
	public void run() {
		SimulationLightPath.now = System.currentTimeMillis();
		long now = SimulationLightPath.now;
		long start = SimulationLightPath.start;
		System.out.println("imprimindo características depois de: " + (now-start)/1000 + "s");
		
		
		t = SimulationLightPath.getTransponder(TypeSupervisor.SPVL);

		if (t instanceof TransponderOTN){
			TransponderOTN tr = (TransponderOTN)t;
			SimulationLightPath.analyseTransponder(tr);
		} else if (t instanceof T100D_GTSintonizavel_Impl){
			T100D_GTSintonizavel_Impl transponder = (T100D_GTSintonizavel_Impl)t;
			System.out.println("dormindo thread!");
			System.out.println(transponder.toStringDetalhed());
			boolean atLeastOneError = false;
			try {
				Thread.sleep(10000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			

			if (transponder.getOTN_WDMInterface().isLof()){
				System.out.println("LOF TRUE!");
				atLeastOneError = true;
			}
			if (transponder.getOTN_WDMInterface().isLom()){
				System.out.println("LOM TRUE!");
				atLeastOneError = true;
			} 
			if (transponder.getOpticalWDMInterface_Impl().isFail()){
				System.out.println("FAIL TRUE!");
				atLeastOneError = true;
			}
			if (transponder.getOpticalWDMInterface_Impl().isLaserOff()){
				System.out.println("OPTICAL WDM INTERFACE LASER OFF!");
				atLeastOneError = true;
			}
			if (transponder.getOpticalWDMInterface_Impl().isAutoLaserOff()){
				System.out.println("OPTICAL WDM INTERFACE AUTOLASER OFF!");
				atLeastOneError = true;
			}
			if (transponder.getOpticalWDMInterface_Impl().isLaserShutdown()){
				System.out.println("OPTICAL WDM INTERFACE LASER SHUTDOWN!");
				atLeastOneError = true;
			}

			if (atLeastOneError == false){
				System.out.println(transponder.toStringDetalhed());
			}

		}
		
		if (!SimulationLightPath.continua){
			SimulationLightPath.fim = true;
		}

	}


}
