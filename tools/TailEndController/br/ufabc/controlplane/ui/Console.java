package br.ufabc.controlplane.ui;



import event.Event;
import gmpls.common.PCAP;
import gmpls.net.RawSocketManager;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import org.savarese.rocksaw.net.RawSocket;

import br.com.padtec.v3.data.impl.SPVL4_Impl;
import br.com.padtec.v3.data.impl.T100D_GTSintonizavel_Impl;
import br.com.padtec.v3.data.ne.FEC;
import br.com.padtec.v3.data.ne.NE;
import br.com.padtec.v3.data.ne.PBAmp;
import br.com.padtec.v3.data.ne.TrpOTNTerminal;
import br.com.padtec.v3.util.log.Log;
import br.ufabc.controlplane.ControlPlane;
import br.ufabc.controlplane.conf.Configuracao;
import br.ufabc.controlplane.metropad.Servidor;
import br.ufabc.controlplane.metropad.Servidor.TypeSupervisor;
import br.ufabc.controlplane.util.log.LogTXT;
import br.ufabc.controlplane.util.log.LogTxtThread;
import br.ufabc.dataplane.DataPlane;

public class Console implements Runnable, Closable{
	private static final String CLASS = "Console";
	private String ip;
	protected String supervisor;
	protected Map<String,String> commandMap;
	protected Scanner input;
	protected Servidor server = null;
	protected boolean connected = false;
	protected static int INITIAL_LINE = 0;
	protected static int INITIAL_COLUMN = 0;
	protected static int POSITION_PROMPT_LINE = 0;
	protected static int POSITION_PROMPT_COLUMN = 8;
	protected static DataPlane dataPlane;
	protected static LogTXT txt;

	protected static final String PROMPT = "prompt# ";
	protected static final String USAGE = "Type \'show commands\' to list the supported commands.";
	protected LinkedHashMap<Integer, NE> nes;
	private HashMap<String, Runnable> windows;

	private String ipSPVL;
	private String ipSPVJ;
	private String name;
	private boolean hasAmplifier;
	protected ControlPlane controller;
	private int incrementGainValue;
	private int initialGainValue;
	private boolean downstream;
	private LogTxtThread gravaDados;
	Configuracao conf;
	

	public Console(){

	}


	public Console(Configuracao conf) {
		this.conf = conf;
		input = new Scanner(System.in);
		commandMap = new TreeMap<String,String>();
		initCommands();
		controller = ControlPlane.getInstance();
		nes = new LinkedHashMap<Integer, NE>();
		windows = new HashMap<String, Runnable>();
		txt = LogTXT.getInstance();
		ipSPVJ = conf.getIpSPVJ();
		ipSPVL = conf.getIpSPVL();
		name=conf.getNomeLocal();
		hasAmplifier = conf.isTemAmplificador();
		incrementGainValue = conf.getIncrementGainValue();
		initialGainValue = conf.getInitialGainValue();
		downstream = conf.isDownstream();
	}

	private void initServiceGMPLS(){
		try {

			//inicia um socket na camada IP
			RawSocket socket = new RawSocket();
			//associa o socket ao protocolo RSVP
			socket.open(RawSocket.PF_INET, RawSocket.getProtocolByName("rsvp"));
			//Inclue o cabeçalho IP
			socket.setIPHeaderInclude(true);
			//timeout infinito
			socket.setReceiveTimeout(0);
			socket.setSendTimeout(0);
			//configura o tamanho do buffer
			socket.setSendBufferSize(65000);
			socket.setReceiveBufferSize(65000);
			//cria um arquivo PCAP para debugging
			PCAP pcap = new PCAP("listener.pcap");
			//Inicia o serviço do plano de controle (Listener)
			controller = ControlPlane.getInstance();
			//associa o arquivo Pcap ao plano de controle
			controller.setPcap(pcap);
			//associa o socket aberto ao plano de controle
			controller.setSocket(socket);
			//Instancia um gerenciador de sockets
			RawSocketManager manager = new RawSocketManager(socket);
			//associa o plano de controle ao gerenciador
			manager.addListener(Event.Type.PACKET_ARRIVAL, controller);
			//Inicia uma tread EXECUTOR que transmite o evento para o CONTROLLER
			ExecutorService executor = Executors.newSingleThreadExecutor();
			executor.execute(manager);
			System.out.println("serviço iniciado!");



		} catch (UnknownHostException ue) {

			ue.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}




	public static void clear() {
		//	   System.out.printf("\033[2J");
		System.out.printf("\033c");

	} 

	public static void position(int linha, int coluna){
		System.out.printf("\033[%d;%dH", linha, coluna);
	} 

	/**
	 * Imprime uma mensagens coloridas
	 * @param mensagem Mensagem a ser mostrada na saída padrão
	 * @param listArgs Lista de argumentos como Cor do Fundo...
	 */  
	public static void printc(String mensagem, String... listArgs)	{
		String cmd = "";        

		// Separando os comandos com ";"
		for(String args:listArgs) {
			cmd = cmd + args + ";";            
		}

		// Retirando o ultimo ";"
		cmd = cmd.substring(0,cmd.length() -1);

		// Adicionando o m no final da frase e finalizando o comando
		cmd = cmd + "m";

		System.out.printf("%s%s%s",
				"\033["+cmd,
				mensagem,
		"\033[0m"); // O Comando \033[0m da um "reset" ao console
		// e volta tudo como anteriormente

	}

	public void printCommands(){
		clear();
		int colunaCmd = 5;
		int colunaDsc = 35;
		Console.position(INITIAL_LINE, colunaCmd);
		Console.printc("COMMAND", "0", "31", "1", "5");
		Console.position(INITIAL_LINE, colunaDsc);
		Console.printc("DESCRIPTION\n", "0", "31", "1", "5");
		int i = INITIAL_LINE + 3;
		for (Iterator<Entry<String,String>> it = commandMap.entrySet().iterator(); it.hasNext() ;){
			Entry<String,String> item = it.next(); 
			String cmd = item.getKey();
			String dsc = item.getValue();
			Console.position(i, colunaCmd);
			System.out.printf("%s",cmd);
			Console.position(i++, colunaDsc);
			System.out.printf("%s\n",dsc);	
		}
		POSITION_PROMPT_LINE += ++i;
		Console.position(POSITION_PROMPT_LINE, INITIAL_COLUMN);
	}



	public void initCommands(){
		commandMap.put("connect", "connects into supervisor");
		commandMap.put("show commands", "shows the list of supported commands.");
		commandMap.put("show all", "shows the list of network elements.");
		commandMap.put("show", "shows network element by ID. USAGE: show < ID NE >");
		commandMap.put("supervisors", "shows the supervisor information.");
		commandMap.put("transponder", "shows the transponder information.");
		commandMap.put("amplifiers", "shows the amplifier information.");
		commandMap.put("fec", "shows the transponder informations about FEC.");
		commandMap.put("close", "closes the viewer of network element by ID. USAGE: close < ID NE >.");
		//		commandMap.put("update supervisor", "updates the transponder's features.");
		commandMap.put("path", "sends a RSVP Path message toward a destination. USAGE: path < DESTINATION IP >.");
		commandMap.put("remove","sends a Tear message to disconnect the LSP. USAGE: remove <DESTINATION IP>.");
		commandMap.put("clear", "clears the screen.");
		commandMap.put("increaseGain", "increases the booster's gain.");
		commandMap.put("decreaseGain", "decreases the booster's gain.");
		commandMap.put("gainIn", "sets the gain in the pré amplifier. Usage: gainIn <value>.");
		commandMap.put("gainOut", " ets the gain in the booster. Usage: gainIn <value>.");
		commandMap.put("exit", "exits of system.");
		commandMap.put("startDataPlane", "connects on hardware supervisors.");
		commandMap.put("reset", "restart counters");
	}

	public void exit(){
		if (server != null)
			server.shutdown();
//		txt.flush();
//		txt.close();
		if (gravaDados != null)
			gravaDados.close();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	public void printTransponder(){
		if (server == null){
			System.out.println("Servidor não iniciado, use o comando \'connect\'");
			return;
		} 
		if (dataPlane == null){
			dataPlane = controller.getDataPlane();
		}
		if (dataPlane != null){
			for (NE ne : server.getAllNE()){
				if (ne instanceof T100D_GTSintonizavel_Impl){
					if (!windows.containsKey(ne.getName())){

						//					System.out.println("Transponder:\n"+ ((T100D_GTSintonizavel_Impl)ne).toStringDetalhed());
						TransponderConsole tc = new TransponderConsole();
						Thread t = new Thread(tc);
						t.setName(ne.getName());
						windows.put(ne.getName(), tc);
						t.start();

					}


				}
			}
		}

	}

	public void printAmplifiers(){
		if (server == null){
			System.out.println("Servidor não iniciado, use o comando \'connect\'");
			return;
		} 
		if(dataPlane != null) {
				System.out.println("Pre: "+dataPlane.getAmplifierIn().getAGCGain()+" dB");
				System.out.println("Booster: "+dataPlane.getAmplifierOut().getAGCGain()+ "dB");
		}
		else 
			System.out.println("Plano de Dados não iniciado");


	}

	public void showAll(){
		if (server == null){
			System.out.println("Servidor não iniciado, use o comando \'connect\'");
			return;
		} 
		int i = 0;try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (NE ne : server.getAllNE()){

			System.out.println(i + " - " + ne);
			i++;
		}

	}

	public void updateNeList(){

		int i = 0;
		for (NE ne : server.getAllNE()){
			if (!nes.containsValue(ne)){
				i = nes.size();
				nes.put(i, ne);
			} 
		}
	}

	public void showNE(int i){
		if (server == null){
			System.out.println("Servidor não iniciado, use o comando \'connect\'");
			return;
		} 

		updateNeList();


		for (Iterator<Entry<Integer,NE>> iterator = nes.entrySet().iterator(); iterator.hasNext() ; ){
			Entry<Integer,NE> item = iterator.next();

			if(i == item.getKey()){
				NE ne = item.getValue();
				if (ne instanceof T100D_GTSintonizavel_Impl){
					System.out.println("Transponder:\n"+ ((T100D_GTSintonizavel_Impl)ne).toStringDetalhed());
				} else if (ne instanceof PBAmp){
					System.out.println("Aplifier:\n"+ ((PBAmp)ne).toStringDetalhed());
				}else {
					System.out.println(ne);
				}

			}

		}

	}

	public void printTransponderFec(){
		//		System.out.println("printing transponder's FEC");
		for (NE ne : server.getAllNE()){
			if (ne instanceof T100D_GTSintonizavel_Impl){
				FEC fec = ((T100D_GTSintonizavel_Impl)ne).getFEC();
				System.out.println("FEC:\n"+ fec);
			}
		}

	}

	public void printSupervisor(){
		//		System.out.println("printing supervisor");
		for (NE ne : server.getAllNE()){
			if (ne instanceof SPVL4_Impl){
				System.out.println("Supervisor\n"+ ((SPVL4_Impl)ne).toExtendedString());
			}
		}
	}

	public void printSupervisores(){
		//		System.out.println("printing supervisor");
		System.out.println(server.getBoardColector().toString());
	}

	public void updateTransponder(){
		System.out.println("updating transponder");
	}

	public static void updateSupervisor(){
		System.out.println("updating supervisor");
	}

	public void commandShow(String[] arguments){
		if(arguments[0].equals("commands")){
			printCommands();
		} else if(arguments[0].equals("all")){
			showAll();
		} else if(arguments[0].equals("transponder")){

			if (arguments.length > 1){
				if(arguments[1].equals("fec")){
					printTransponderFec();
				}
			} else{

				printTransponder();
			}
		} else if(arguments[0].equals("supervisors")){
			printSupervisor();
		} else {
			try {

				int i = Integer.parseInt(arguments[0]);
				showNE(i);
			} catch (NumberFormatException e) {
				System.out.println("o argumento não é um número");
				System.err.println(e.getMessage());
			}
		}
	}

	public void commandClose(String[] arguments){
		if(arguments.length == 1){
			try {
				int i = Integer.parseInt(arguments[0]);
				NE ne = nes.get(i);
				Runnable runner = windows.get(ne.getName());
				if (runner instanceof Cloneable){
					((TransponderConsole)runner).close();
				}

			} catch (NumberFormatException e) {
				System.out.println("o argumento não é um número");
				System.err.println(e.getMessage());
			}
		}
	}


	public void commandSendPath(String[] arguments){
		if (arguments.length == 1){
			String destination = arguments[0];
			
			try {
				controller.sendPath(destination);
				
			} catch (Exception e) {
				Log.getInstance().log(Level.SEVERE, "ERROR SENDING PATH to " + destination +"\n"+ e.toString());
				e.printStackTrace();
			}
		} else if (arguments.length == 3){
			if(arguments[2].equals("upstreamLabel")){
				String label = arguments[2];
			} else {
				System.out.println("Invalid arguments upstreamLabel\nUSE:  path <ip> | send path <ip> upstreamLabel <lambda value>");
			}
		} else {
			System.out.println("Invalid arguments\nUSE:  path <ip> | send path <ip> upstreamLabel <lambda value>");
		}

	}

	public void commandUpdate(String[] arguments){
		if(arguments[0].equals("transponder")){
			updateTransponder();
		} else if(arguments[0].equals("supervisor")){
			updateSupervisor();
		}
	}

	public void getPrompt(){
		position(INITIAL_LINE, INITIAL_COLUMN);
		//		System.out.printf(PROMPT);
		//		String command = input.nextLine();
		//		return command;
	}

	@Override
	public void run() {

		//		clear();
		//		Console.position(5, 23);
		Console.printc("CONSOLE GERÊNCIA LOCAL!\n\n", "0", "31", "1", "5");

		System.out.println(USAGE);
		while(true){

			System.out.printf(PROMPT);
			String command = input.nextLine();


			if( command != null ) {
				String[] arguments = command.split(" ");

				if (commandMap.keySet().contains(command) || commandMap.keySet().contains(arguments[0]) ){
					if(arguments[0].equals("connect")){
						if(arguments.length != 3)
							System.out.println("USE: connect <ip> <type supervisor>");
						else {
							ip = arguments[1];
							supervisor = arguments[2];
							boolean hasTypeSupervisor = false; 
							for (TypeSupervisor s : TypeSupervisor.values()){
								if (s.name().equalsIgnoreCase(supervisor)){
									hasTypeSupervisor = true;
									TypeSupervisor type = TypeSupervisor.valueOf(supervisor.toUpperCase());
									if( ip!= null && type != null){
										doConnection(ip, type);
									} else {
										System.out.println("USAGE: connect <ip> <type supervisor>");
									}
									break;
								}

							}
							if(!hasTypeSupervisor){
								System.out.println("Type of Supervisor \'" + supervisor + "\' isn\'t compatible!");
							}

						}
					} else if (arguments[0].equals("show")){
						if (arguments.length > 1){
							String[] buffer = new String [arguments.length -1];
							System.arraycopy(arguments, 1, buffer, 0 , arguments.length -1);
							commandShow(buffer);
						} else { 
							System.out.println("Commando não encontrado!");
						}

					} else if (arguments[0].equals("close")){
						if (arguments.length > 1){
							String[] buffer = new String [arguments.length -1];
							System.arraycopy(arguments, 1, buffer, 0 , arguments.length -1);
							commandClose(buffer);
						} else { 
							System.out.println("Commando não encontrado!");
						}

					}else if (arguments[0].equals("transponder")){
						printTransponder();
					} else if (arguments[0].equals("supervisors")){
						printSupervisores();
					} else if (arguments[0].equals("fec")){
						printTransponderFec();

					} else if (arguments[0].equals("reset")){
						resetCounters();

					}else if (arguments[0].equals("increaseGain")){
						increaseGain();

					}else if (arguments[0].equals("decreaseGain")){
						decreaseGain();
					}else if (arguments[0].equals("gainIn")){
						if (arguments[1] != null){
							int value = Integer.parseInt(arguments[1]); 
							setGainIn(value);
						}

					}else if (arguments[0].equals("gainOut")){
						if (arguments[1] != null){
							int value = Integer.parseInt(arguments[1]); 
							setGainOut(value);
						}

					}else if(arguments[0].equals("path")){
						String[] buffer = new String [arguments.length -1];
						System.arraycopy(arguments, 1, buffer, 0 , arguments.length -1);
						commandSendPath(buffer);

					} else if(arguments[0].equals("clear")){
						clear();
					} else if(arguments[0].equals("startDataPlane")){
						startDataPlane();
					} else if(arguments[0].equals("exit")){

						exit();
					} else if(arguments[0].equals("amplifiers")) {
						printAmplifiers();
					}

				}else if(command.equals("")){
				}  else {
					System.out.println("Commando não encontrado!");
					System.out.println(USAGE);
				}


			}

		}

	}

	public void increaseGain(){
		if (dataPlane != null)
			dataPlane.increaseGainOut();
		else 
			System.out.println("Plano de Dados não iniciado");
	}
	
	public void setGainOut(int value){
		if(dataPlane != null){
			System.out.println("Configurando ganho do booster para "+ value +" em " + name);
			dataPlane.setGain(dataPlane.getAmplifierOut(), value);
		} else 
			System.out.println("Plano de Dados não iniciado");
	}
	
	public void setGainIn(int value){
		if(dataPlane != null)
			dataPlane.setGain(dataPlane.getAmplifierIn(), value);
		else 
			System.out.println("Plano de Dados não iniciado");
	}

	public void decreaseGain(){

		if(dataPlane != null)
			dataPlane.decreaseGainOut();
		else 
			System.out.println("Plano de Dados não iniciado");
	}

	public void doConnection(String ip, Servidor.TypeSupervisor type){
		System.out.println("Doing connection on supervisor type: " + type + ", by ip: "+ ip);
		server = Servidor.getInstance();
		server.setIp(ip);
		server.start(type);

	}
	/**
	 * Starts the data plane
	 * */
	public void startDataPlane(){

		Thread t = new StartDataPlane();
		t.start();
		
	}

	/**
	 * Resets the counters, need start the data Plane
	 * 
	 * */
	public void resetCounters(){
		if(dataPlane != null)
			dataPlane.reserCountersFec();
		else 
			System.out.println("Plano de Dados não iniciado");
	}

	@Override
	public void close() {
		txt.flush();

	}

	/*Inicia o plano de dados após o comando startDataPlane*/
	class StartDataPlane extends Thread{
		public StartDataPlane() {
			super("StartDataPlane");
		}

		public void run(String ip){
			try {
				if (hasAmplifier){
					doConnection(ipSPVL, TypeSupervisor.SPVL);
					doConnection(ipSPVJ, TypeSupervisor.SPVJ);
				} else {
					doConnection(ipSPVL, TypeSupervisor.SPVL);
				}
				sleep(10000);
			} catch (Exception e) {
				ControlPlane.getInstance().logSevere(CLASS, "Não foi possível iniciar o plano de dados!", e);
				
			}
			
			PBAmp pre = null;
			PBAmp booster = null;
			TrpOTNTerminal t = null;

			for (NE n : Servidor.getInstance().getAllNE()){
				if (n instanceof PBAmp && (n.getSlot() == 13)){
					pre = (PBAmp)n;
				} else if (n instanceof PBAmp && (n.getSlot() == 9)){
					booster = (PBAmp)n;
				} if (n instanceof TrpOTNTerminal && n.getSupAddress() == 3){
					t = (TrpOTNTerminal)n;
				}
			}

			if(hasAmplifier){

				if (pre != null && booster != null && t != null){
					dataPlane = new DataPlane(t, conf, pre, booster);
					ControlPlane.getInstance().setDataPlane(dataPlane);
					System.out.println("Plano de Dados em " + name + " Ok!");
				} 
			} else if (pre == null && booster == null && t != null){
				dataPlane = new DataPlane(t, conf);
				ControlPlane.getInstance().setDataPlane(dataPlane);
				System.out.println("Plano de Dados em " + name + " Ok!");


			}
			System.out.println("Chamando thread Grava dados em console");
			gravaDados = new LogTxtThread(dataPlane, controller);
			gravaDados.start();
			long now = (System.currentTimeMillis()-ControlPlane.START_TIME)/1000; 
			System.out.println("Desligando laser WDM ... em: " + now + "s");
			dataPlane.turnOFFLaserWdm();
			
			
			if (name.equals("UFABC")){
				try {
					/*configura o ganho inicial do amplificador*/
					setGainOut(conf.getInitialGainValue());
					
					sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
//				String destination = "10.1.5.10";
//				String destination = "143.106.45.131";
//				try {
//					controller.sendPath(destination);
//				} catch (Exception e) {
//					Log.getInstance().log(Level.SEVERE, "ERROR SENDING PATH to " + destination +"\n"+ e.toString());
//					e.printStackTrace();
//				}
			} 
			System.out.println();
			//Start the SNMP agent
                         
			SNMPAgent agent = new SNMPAgent(dataPlane, conf, ip + "/255");
			try {
				agent.start();
			} catch (Exception e){e.printStackTrace();}
			
			
		}
	}


}


