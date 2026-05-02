package br.ufabc.monitor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Logger;

import br.com.padtec.v3.data.ne.Amplifier;
import br.com.padtec.v3.data.ne.NE;
import br.com.padtec.v3.data.ne.Transponder;
import br.com.padtec.v3.data.ne.TrpOTNTerminal;
import br.com.padtec.v3.util.log.Log;
import br.ufabc.equipment.Amplifiers;
import br.ufabc.equipment.OTNTransponder;
import br.ufabc.equipment.OXC;
import br.ufabc.equipment.Supervisor;
import br.ufabc.equipment.Transponders;
import br.ufabc.polatis.PolatisOXC;

public class NetworkMonitor {

	/** Servidor para o canal experimental (10Gbps). */
	// public static Servidor server1;
	/** Servidor para o canal da rede estável. */
	// public static Servidor server2;
	/** Servidor para o canal experimental (10Gbps). */
	public static Supervisor sup1;
	/** Servidor para o canal da rede estável. */
	public static Supervisor sup2;
	/** Logger. */
	private static Logger log = Log.getInstance();
	/** monitored */
	private static Vector<NE> monitored;

	/** Classe interna para Monitoração e Gravação de Log */
	public class MonitoredElement implements Runnable {

		/** Contador de iterações */
		private int timer = 0;
		/** Referencia ao arquivo de Log */
		private FileWriter logFile;
		/** Elemento Genérico que será monitorado */
		private Vector<? extends NE> element;

		public MonitoredElement() {
			// Cria o arquivo de log
			try {
				logFile = new FileWriter(new File("LogFile.xls"));
				logFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		// Grava no arquivo de log
		public void logRecord(String entry) {
			try {
				logFile = new FileWriter(new File("LogFile.xls"), true);
				logFile.write("\t" + entry);
				logFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Gera o cabeçalho no arquivo de log
		public void headerRecorder() {
			for (NE ne : element) {
				System.out.println("Iniciei header");
				if (ne instanceof Amplifier) {
					Amplifier amp = (Amplifier) ne;
					System.out.println("Nome do amp"+amp.getName());
					logRecord(amp.getName() + "- GAIN");
					logRecord("-PIN");
					logRecord("-POUT");
					logRecord("-AGC");
					logRecord("-LOS");

				}

				else if (ne instanceof TrpOTNTerminal) {
					Transponder transp = (Transponder) ne;
					logRecord(transp.getName() + "-POUT CLI");
					System.out.println("Nome do amp"+transp.getName());
					logRecord("-PIN CLI");
					logRecord("-LAMBDA CLI");
					logRecord("-POUT WDM");
					logRecord("-PIN WDM");
					logRecord("-LAMBDA WDM");
					logRecord("-CHANNEL WDM");
					logRecord("-LOS");
					logRecord("-LOF");
					logRecord("-OFF");
					logRecord("-BIP8");
					logRecord("-BEI");
					logRecord("-FRAMES OTN");
					logRecord("-BDI");
					logRecord("-FEC ERRORS");
					logRecord("-LOS CLI");
					logRecord("-OFF CLI");

				} else if (ne instanceof Transponder) {

					Transponder transp = (Transponder) ne;
					logRecord(transp.getName() + "-PIN");
				
					logRecord("-POUT ");
					logRecord("-CHANNEL ");
					logRecord("-NOMINAL_LAMBDA");
					logRecord("-LOS");
					logRecord("-UP");

				} else if (ne instanceof OXC) {
					OXC oxc = (OXC) ne;
					PolatisOXC polatisOXC = oxc.getPolatis();
					logRecord(String.valueOf(oxc.getName()));
					System.out.println("Nome do OXC"+oxc.getName());
					for(String i: polatisOXC.getActivedIgressPorts()){
						logRecord("PORT_"+String.valueOf(i)+"-ConnectedPort");
						logRecord("-MonAlarm");
						logRecord("-DesiredCond");
						logRecord("-DesiredState");
						logRecord("ProtectingPort");
					}
					for(String i: polatisOXC.getActivedEgressPorts()){
						logRecord("PORT_"+String.valueOf(i)+"-ConnectedPort");
						logRecord("-Power");
						logRecord("-LevelOffset");
						logRecord("-AlarmEdge");
						logRecord("-AlarmMode");
						logRecord("-VOAMode");
						logRecord("-MonitorAlarm");
						logRecord("-DesiredCond");
						logRecord("-DesiredState");
						logRecord("-AttLVL");
						logRecord("-OPMType");
						logRecord("-OPMWavelenght");
						}
				}

			}
		}

		// Obtêm leituras do elemento a ser monitorado
		public void getInf() {
			System.out.println("Iniciei inf");
			for (NE ne : element) {
				if (ne instanceof Amplifier) {
					Amplifier amp = (Amplifier) ne;
					Amplifiers amplifier = new Amplifiers(sup2, amp);
					System.out.println(String.valueOf(amplifier.getGain()));
					logRecord(String.valueOf(amplifier.getGain()));
					logRecord(String.valueOf(+amplifier.getPowerInput()));
					logRecord(String.valueOf(amplifier.getPowerOutput()));
					logRecord(String.valueOf(amplifier.isAGC()));
					logRecord(String.valueOf(amplifier.isLOS()));
				} else if (ne instanceof TrpOTNTerminal) {
					TrpOTNTerminal term = (TrpOTNTerminal) ne;
					OTNTransponder transp = new OTNTransponder(sup1, term);

					logRecord(String.valueOf(transp.getOutputPowerClient()));
					logRecord(String.valueOf(transp.getInputPowerClient()));
					logRecord(String.valueOf(transp.getClientLambda()));
					logRecord(String.valueOf(transp.getOutputPowerWDM()));
					logRecord(String.valueOf(transp.getInputPowerWDM()));
					logRecord(String.valueOf(transp.getLambda()));
					logRecord(String.valueOf(transp.getChannel()));
					logRecord(String.valueOf(transp.isLOS()));
					logRecord(String.valueOf(transp.isLOF()));
					logRecord(String.valueOf(transp.isOff()));
					logRecord(String.valueOf(transp.getBIP8()));
					logRecord(String.valueOf(transp.getBEI()));
					logRecord(String.valueOf(transp.getFramesOTN()));
					logRecord(String.valueOf(transp.isBDI()));
					logRecord(String.valueOf(transp.getFECErrors()));
					logRecord(String.valueOf(transp.isClientLOS()));
					logRecord(String.valueOf(transp.isClientOff()));

				} else if (ne instanceof Transponder) {
					Transponder transp = (Transponder) ne;
					logRecord(String.valueOf(transp.getPin()));
					logRecord(String.valueOf(transp.getPout()));
					logRecord(String.valueOf(transp.getChannel()));
					logRecord(String.valueOf(transp.getNominalLambda()));
					logRecord(String.valueOf(transp.isLos()));
					logRecord(String.valueOf(transp.isUp()));
				} else if (ne instanceof OXC) {
					OXC oxc = (OXC) ne;
					oxc.setActivedEgressPorts();
					oxc.setActivedIgressPorts();
					PolatisOXC polatisOXC = oxc.getPolatis();
					
					for(String i: polatisOXC.getActivedIgressPorts()){
						int port=Integer.valueOf(i);
						logRecord(String.valueOf(polatisOXC.getConnectPort(port)));
						logRecord(String.valueOf(polatisOXC.getMonitorAlarmInPort(port)));
						logRecord(String.valueOf(polatisOXC.getPortDesiredCondition(port)));
						logRecord(String.valueOf(polatisOXC.getPortDesiredState(port)));
						logRecord(String.valueOf(polatisOXC.getProtectingPort(port)));
					}
					for(String i: polatisOXC.getActivedEgressPorts()){
						int port=Integer.valueOf(i);
						logRecord(String.valueOf(polatisOXC.getConnectPort(port)));
						logRecord(String.valueOf(polatisOXC.getMeasurePower(port)));
						logRecord(String.valueOf(polatisOXC.getPowerLevelOffset(port)));
						logRecord(String.valueOf(polatisOXC.getAlarmEdgeOption(port)));
						logRecord(String.valueOf(polatisOXC.getAlarmModeOption(port)));
						logRecord(String.valueOf(polatisOXC.getVOAMode(port)));
						logRecord(String.valueOf(polatisOXC.getMonitorAlarmInPort(port)));
						logRecord(String.valueOf(polatisOXC.getPortDesiredCondition(port)));
						logRecord(String.valueOf(polatisOXC.getPortDesiredState(port)));
						logRecord(String.valueOf(polatisOXC.getAttenuationLevel(port)));
						logRecord(String.valueOf(polatisOXC.getOPMType(port)));
						logRecord(String.valueOf(polatisOXC.getOPMWavelength(port)));
						}

				}
			}

		}

		// Define o elemento a ser monitorado
		public void setElement(Vector<? extends NE> element) {
			this.element = element;

		}

		public void run() {
			int i = 0;

			headerRecorder();
			System.out.println("Gerando arquivo de log LogFile.xls");

			while (i < 100) {
				// gravação do identificador de iteração, checar
				// intervalo*******
				logRecord("\n" + timer);
				timer++;

				getInf();

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

		}

	}

	/** Criação da classe de monitoração */
	public static MonitoredElement monitoredElement;

	public NetworkMonitor() {
	}

	public static void main(String args[]) {
		// *****************
		// teste xml

		// ************
		
		
		
			
		NetworkMonitor networkMonitor = new NetworkMonitor();
		monitored = new Vector<NE>();// inicia vetor de NE
		sup1 = new Supervisor("172.17.36.32", Supervisor.TypeSupervisor.SPVL);
		sup1.start();
		sup2 = new Supervisor("172.17.36.32", Supervisor.TypeSupervisor.SPVJ);
		sup2.start();
		OXC polatis = new OXC("172.17.36.20");
		Vector <Supervisor> networkComponents = new Vector<Supervisor>();
		networkComponents.add(sup1);
		networkComponents.add(sup2);
		
		// System.out.println("EXIBIR"+polatis.getStatePort());
	

		// ****** testando leituras do polatis

		// checa o número de portas de entrada e de saída do OXC

		// Inicializa o supervisor da rede estável
		/*
		 * server2 = Servidor.getInstance(); server2.setIp("172.17.36.31");
		 * server2.start(TypeSupervisor.SPVJ);
		 */

		try {
			log.config("Going to sleep for 20 seconds for loading NE...");
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (String monitoredType : args) {
			// Verifica o elemento a ser monitorado passado como parâmetro
			if (monitoredType.equals(sup2.getType())) {

				// Obtem elementos do supervisor SPVJ

				System.out.println("Monitorando equipamento SPVJ");

				if (!Amplifiers.getAmplifiers(sup2).isEmpty()) {
					// Se houver amplificadores no supervisor, monitorar
					monitored.addAll(Amplifiers.getAmplifiers(sup2));
					System.out.println("Adicionei os Amplificadores SPVJ para monitoração");
				} else {
					System.out.println("Não há Amplificadores a serem monitorados em SPVJ");
				}

				if (!Transponders.getTransponders(sup2).isEmpty()) {
					// Se houver Transponders no supervisor, monitorar
					monitored.addAll(Transponders.getTransponders(sup2));
					System.out.println("Adicionei os transponders OTN de SPVJ para monitoração");
				} else {
					System.out.println("Não há Transponders OTN a serem monitorados em SPVJ");

				}

			} else if (monitoredType.equals(sup1.getType())) {
				System.out.println("Monitorando equipamento SPVL");

				if (!Amplifiers.getAmplifiers(sup1).isEmpty()) {
					// Se houver amplificadores no supervisor, monitorar
					monitored.addAll(Amplifiers.getAmplifiers(sup1));
					System.out.println("Adicionei os Amplificadores SPVL para monitoração");
				} else {
					System.out.println("Não há Amplificadores a serem monitorados em SPVL");
				}

				if (!Transponders.getTransponders(sup1).isEmpty()) {
					// Se houver Transponders no supervisor, monitorar
					monitored.addAll(Transponders.getTransponders(sup1));
					System.out.println("Adicionei os transponders OTN de SPVL para monitoração");
				} else {
					System.out.println("Não há Transponders OTN a serem monitorados em SPVL");

				}

			} else if (monitoredType.equals("OXC")) {
				
				monitored.add(polatis);
				System.out.println("Adicionei o OXC Polatis para monitoração");
			}

			else {// se não for identificado o parâmetro passado, sair
				System.out.println("**Elemento " + monitoredType + " Inválido Para Monitoração");

			}
		}
		monitoredElement = networkMonitor.new MonitoredElement();// Instancia a
																	// classe do
																	// elemento
																	// a ser
																	// monitorado

		if (monitored.isEmpty()) { // se a variavel monitored estiver vazia, não
									// foram passados parâmetros para
									// monitoração
			System.out.println("Nenhum parâmetro válido informado para monitoração!!!!");
			System.exit(0);
		}
			
				
			
			monitoredElement.setElement(monitored);// passa os elementos a serem
													// monitoradas a classe de
													// monitoração

			Thread t = new Thread(monitoredElement);// inicia Trhead com
													// intervalos de 1 segundo
													// entre as leituras
			t.start();
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}


