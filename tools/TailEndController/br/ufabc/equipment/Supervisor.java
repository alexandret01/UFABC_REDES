package br.ufabc.equipment;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.padtec.v3.data.ColectorConfig;
import br.com.padtec.v3.data.Command;
import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.impl.NE_Impl;
import br.com.padtec.v3.data.ne.NE;
import br.com.padtec.v3.server.Colector;
import br.com.padtec.v3.util.log.Log;
import br.ufabc.controlplane.GmplsAlarmListener;


public class Supervisor {
	/** TCP Port used by supervisor. */
	private static int tcpPort = 8886;
	/** Supervisor type. */
	public enum TypeSupervisor{
		/** Supervisor type: SPVL-4 */ SPVL, 
		/** Supervisor type: SPVJ-4 */ SPVJ;
	}
	/** Logger. */
	private Logger log = Log.getInstance();
	//	static LocalNEMapServer neMapServer;
	/** Padtec Board Collector. */
	private BoardCollector boardCollector; 
	/** Padtec Collector Config. */
	private ColectorConfig collectorConfig;
	/** */
	private GmplsAlarmListener gmplsAlarmListener;
	/** IP address of the supervisor. */
	private String ip = null;
	/** Supervisor type. */
	private TypeSupervisor type;

//	private Vector<TypeSupervisor> startedServers;

	
	/**
	 * Creates a new Server with the specified IP address and supervisor type.
	 * @param ip The IP address of the supervisor.
	 * @param supType The supervisor type  (SPVL or SPVJ).
	 */
	public Supervisor(String ip, TypeSupervisor supType) {
		// Start board collector.
		boardCollector = new BoardCollector();
		//Get IP and supervisor type
		this.ip = ip;
		this.type = supType;
	}

	/**
	 * Creates a new Server with the specified IP address and site number.
	 * @param ip The IP address of the supervisor.
	 * @param supType The site number.
	 */	
	public Supervisor(String ip, int site) {
		// Start board collector.
		boardCollector = new BoardCollector();
		//Get IP and supervisor type
		this.ip = ip;
		this.type = this.getTypeSupervisor(site);
	}
	
	public String getType(){
		return(this.type.toString());
	}

	
	/**
	 * Returns the supervisor type, given site number.
	 * @param site Site number.
	 * @return The supervisor type (SVPL or SVPJ).
	 */
	public TypeSupervisor getTypeSupervisor(int site){
		if (site == 1)
			return TypeSupervisor.SPVJ;
		else if (site == 3);
			return TypeSupervisor.SPVL;
	}
	
	
	
	
	public void start(){
		/* Start colector config */
		this.collectorConfig = new ColectorConfig();
		//Set supervisor type
		if (type.equals(TypeSupervisor.SPVJ)) {
			collectorConfig.setClassName("br.com.padtec.v3.server.protocols.ppm2v2.ColetorPPM2v2");
			collectorConfig.setType(2); 
		} else if (type.equals(TypeSupervisor.SPVL)) {
			collectorConfig.setClassName("br.com.padtec.v3.server.protocols.ppm3.PPM3Collector");
			collectorConfig.setType(6);
		}
		//Set IP address and port
		collectorConfig.setIp(ip);
		collectorConfig.setTypeName("Ethernet");
		collectorConfig.setPort(tcpPort);
		log(Level.INFO, "Set ColectorConfig for "+type+" at address "+ ip +" port: 8886");
		/* Set collector */	
		//Use Java reflection API for instanciating the collector (PPMv2 or v3)
		Colector collector = null;
		Class classcfn = null;
		try {
			classcfn = Class.forName(collectorConfig.getClassName());
			Class partypes[] = new Class[1];
			partypes[0] = Supervisor.class;
			Constructor constructor =  classcfn.getConstructor(partypes);
			Object arglist[] = new Object[1];
			arglist[0] = this;
			collector = ((Colector)constructor.newInstance(arglist));
		} catch (Exception e) {
			e.printStackTrace();
		} 
		//Initial collector
		int siteTemp = 0;
		while(boardCollector.getCollectors().containsKey(siteTemp)){
			siteTemp++;
		}

		boardCollector.addCollector(siteTemp, type, collector);
		collector.setColectorConfig(collectorConfig);
//		printAlarms = new PrintAlarms();
		//
		gmplsAlarmListener = new GmplsAlarmListener();
		//		printAlarms.startPrintNewAlarms();
//		coletor.addNotificationListener(printAlarms);
		//Start collector
		collector.addNotificationListener(gmplsAlarmListener);
		collector.setConnection(collectorConfig.getIp(),collectorConfig.getPort());
		//System.out.println("connectionDesc: "+connectionDesc);
		log(Level.CONFIG, "Starting Local Server for "+type+" at address "+ ip +" port: 8886");
		//Start thread to collect data from supervisor
		Thread colThread = new Thread(collector);
		String connectionDesc = getColectorConfig().getIp() + ":" + getColectorConfig().getPort();
		colThread.setName("Coletor"+connectionDesc);
		colThread.start();
		try  {
			Thread.sleep(2000L);
		} catch (InterruptedException localInterruptedException1) {
		}
		collector.sendRegen();
	}
	
	/**
	 * Updates the implementation of collector (PPMv2 e v3) - see br.com.padtec.server.v3.protocols
	 * @param site
	 * @param colector
	 */
	public void updateSiteCollector(int site, Colector colector){
		boardCollector.updateCollector(colector, site);
	}
	
	public BoardCollector getBoardColector() {
		return boardCollector;
	}

	
	/**Desbloqueia o supervisor*/
	public void sendCommandUnlockSupervisor(int idColetor, int part, int address) {
		Colector coletor = boardCollector.getCollector(idColetor);
		if (coletor != null)
			coletor.unlockSupervisor(part, address);
	}

	public ColectorConfig getColectorConfig(){
		return collectorConfig;
	}

	public void sendRegen(int idColetor) {
		Colector coletor = boardCollector.getCollector(idColetor);
		coletor.sendRegen();

	}
	/**
	 * Returns all network elements into one colector;
	 * @param idColetor the id of colector in server
	 * */
	public Collection<? extends NE> getAllNeIntoColector(int idColetor){
		Colector coletor = boardCollector.getCollector(idColetor);
		return coletor.getAllNE();
	}
	
	/**
	 * Returns all network elements into one colector;
	 * @param idColetor the id of colector in server
	 * */
	public Collection<? extends NE> getAllNeIntoColector() {
		Colector coletor = boardCollector.getCollector(type);
		return coletor.getAllNE();
	}
	/**
	 * Returns all network elements in all colectores have been started;
	 * 
	 * */
	public Collection<? extends NE> getAllNE() {
		List<NE> allNes = new ArrayList<NE>();
		for (Colector c : boardCollector.getAllCollector()) {
			allNes.addAll(c.getAllNE());
		}
		return allNes;
	}

	public void log(Level level, String msg) {
		StringBuilder sb = new StringBuilder();
		sb.append("Servidor: ");
		sb.append(msg);
		Log.getInstance().log(level, sb.toString());
		sb = null;
	}

	public void log(String msg, Exception e) {
		StringBuilder sb = new StringBuilder();
		sb.append("Servidor: ");
		sb.append(msg);
		Log.getInstance(1).log(Level.SEVERE, sb.toString(), e);
		sb = null;
	}

	/**
	 * Searchs a network element by its serial number
	 * @param s the serial number of network element
	 * */
	public NE getNE(SerialNumber s) {
		if (s == null) {
			return null;
		}
		NE_Impl result = null;
		for (Colector c : boardCollector.getAllCollector()){
			if (c != null) {
				result = c.getNE(s);

			}
		}

		return result;
	}

	/**
	public static Supervisor getInstance(){
		if(instance == null){
			instance = new Supervisor();
		}

		return instance;
	}
	*/
	
	public Colector getColector(TypeSupervisor type){
		return boardCollector.getCollector(type);
	}
	
	public Colector getColector(int  supervisorSite){
		return boardCollector.getCollector(supervisorSite);
	}
	
	public synchronized boolean doCommand(Command cmd, int supervisorSite)	{
		SerialNumber serial = null;
		serial = cmd.getSerialNumber();
		Colector coletor = getColector(supervisorSite);
				
		if (cmd.getCommand() == 3) {
			NE_Impl ne = coletor.getNE(serial);
			if (ne != null)
			{
				for (Method m : ne.getClass().getMethods()) {
					if (((m.getName().startsWith("resetCounters"))) && m.getParameterTypes().length == 0)
						try {
							m.invoke(ne, new ArrayList<Object>().toArray());
						} catch (Exception e) {
							this.log.severe("Servidor: erro resetando contadores | NE: " + ne.getName() + ", Serial: " + ne.getSerial());
						}
				}
			}
		}

		this.log.info("Sending " + cmd);
		coletor.addCommand(cmd);
		return true;
	}
	
	/**
	 * Execute a list of commands
	 * @param commands List of commands
	 * @param site Site address of supervisor device 
	 * */
	public void doCommand(List<Command> commands, int supervisorSite)	{
		for (Command c : commands){
			doCommand(c, supervisorSite);
		}
	}
	
	public void senRegen(TypeSupervisor type)	{
		Colector coletor = getColector(type);
		coletor.sendRegen();
	}

	/*
	public static void main(String[] args) {
		//		Log.setConsole(true);
		Supervisor server = Supervisor.getInstance();
		server.start(Supervisor.TypeSupervisor.SPVJ);
		//		Servidor server = new Servidor("10.2.22.5", TypeSupervisor.SPVL);
		try {
			//			Thread.sleep(3000);

			for (NE ne : server.getAllNE()){
				if (ne instanceof Transponder){
					//					server.coletor.sendHistoryRequest(EnumHistoryType.READ_METRICS, ne.getSupAddress(), 
					//							server.coletor.stateController.getBoardState(ne.getSerial()).getLastLastAlarmHistoryReceived(), null, ne.getSerial());

					Thread.sleep(3000);
					System.out.println("Transponder:\n"+
							((T100D_GTSintonizavel_Impl)ne).toStringDetalhed());
				}
			}
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} 


		//		server.sendRegen();



		//		server.sendRegen();
		//		System.out.println(Msg.getString("JMainMenu.4"));
	}

	public void shutdown() {
		for (Colector c : boardCollector.colectores.values()){
			c.shutdown();
		}
		
	}
*/
	
}
