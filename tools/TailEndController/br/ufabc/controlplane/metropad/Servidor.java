package br.ufabc.controlplane.metropad;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.padtec.v3.data.ColectorConfig;
import br.com.padtec.v3.data.Command;
import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.impl.NE_Impl;
import br.com.padtec.v3.data.impl.T100D_GTSintonizavel_Impl;
import br.com.padtec.v3.data.ne.NE;
import br.com.padtec.v3.data.ne.Transponder;
import br.com.padtec.v3.server.Colector;
import br.com.padtec.v3.util.log.Log;
import br.ufabc.controlplane.GmplsAlarmListener;


public class Servidor {
	public static final String S_SPVLNX = "SPVL-4";
	public static final String S_SPVJ = "SPVJ";
	public static int SPVJ = 2;
	public static int SPVLNX = 6;
	private Logger log = Log.getInstance();
	//	static LocalNEMapServer neMapServer;
	private ColectorConfig coletorConfig;
	private String DEFAUT_IP_SPVL = "200.133.215.156";
	private String DEFAUT_IP_SPVJ = "200.133.215.155";
	//	private String ipSPVJ = "10.2.22.4";
	private static int tcpPort = 8886;
	private String connectionDesc;
	private static Servidor instance;
//	private PrintAlarms printAlarms;
	private GmplsAlarmListener gmplsAlarmListener;
	private String ip = null;
	private TypeSupervisor type;
	private int siteTemp = 0;

	private Vector<TypeSupervisor> startedServers;

	public static int numberColectors = 0; 
	private BoardColector boardColector; 



	public enum TypeSupervisor{
		SPVL, SPVJ;
	}
	
	public TypeSupervisor getTypeSupervisor(int site){
		if (site == 1)
			return TypeSupervisor.SPVJ;
		else if (site == 3);
			return TypeSupervisor.SPVL;
	}

	private Servidor(){
		startedServers = new Vector<TypeSupervisor>();
		boardColector = new BoardColector();

	}
	
	public void start(TypeSupervisor type){
		if (startedServers.contains(type)){
			log.info("Server "  + type + " have already started!");
			return;
		}
		updateColectorConfig(type);
		//		coletor = new PPM3Collector();
		Colector coletor = null;
		Class classcfn = null;
		try {
			classcfn = Class.forName(coletorConfig.getClassName());
			coletor = ((Colector)classcfn.newInstance());
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
			return;
		}
		numberColectors++;
		startedServers.add(type);
		while(boardColector.getColectores().containsKey(siteTemp)){
			siteTemp++;
		}
		
		boardColector.addColector(siteTemp, type, coletor);
		coletor.setColectorConfig(coletorConfig);
		//		coletor.setProdutos(iniciaProdutos());
//		printAlarms = new PrintAlarms();
		gmplsAlarmListener = new GmplsAlarmListener();
		//		printAlarms.startPrintNewAlarms();
//		coletor.addNotificationListener(printAlarms);
		coletor.addNotificationListener(gmplsAlarmListener);
		coletor.setConnection(coletorConfig.getIp(),coletorConfig.getPort());
		connectionDesc = getColectorConfig().getIp() + ":" + getColectorConfig().getPort();
		log(Level.CONFIG, "Starting Local Server");
		Thread colThread = new Thread(coletor);
		colThread.setName("Coletor");
		colThread.start();

		try  {
			Thread.sleep(2000L);
		} catch (InterruptedException localInterruptedException1) {
		}
		coletor.sendRegen();
		

	}
	
	public void updateSiteColector(int site, Colector colector){
		boardColector.updateColector(colector, site);
	}
	
	public BoardColector getBoardColector() {
		return boardColector;
	}

	public Map<SerialNumber,Integer> iniciaProdutos(){
		Map<SerialNumber,Integer> result = new TreeMap<SerialNumber, Integer>();
		result.put(new SerialNumber(1316,684), 3);
		result.put(new SerialNumber(1399, 2), 3);
		result.put(new SerialNumber(1427, 663), 3);
		return result;
	}

	/**Desbloqueia o supervisor*/
	public void sendCommandUnlockSupervisor(int idColetor, int part, int address) {
		Colector coletor = boardColector.getColector(idColetor);
		if (coletor != null)
			coletor.unlockSupervisor(part, address);
	}

	public ColectorConfig getColectorConfig(){
		return coletorConfig;
	}


	public void setIp(String ip){
		this.ip = ip;
	}

	public void updateColectorConfig(TypeSupervisor type){
		if(this.coletorConfig == null){
			this.coletorConfig = new ColectorConfig();
		}
		switch (type){
		case SPVJ:
			coletorConfig.setType(SPVJ); 
			coletorConfig.setClassName("br.com.padtec.v3.server.protocols.ppm2v2.ColetorPPM2v2");
			if(ip == null){
				coletorConfig.setIp(DEFAUT_IP_SPVJ);
				ip = DEFAUT_IP_SPVJ;
			} else {
				coletorConfig.setIp(ip);

			}

			break;
		case SPVL:
			coletorConfig.setClassName("br.com.padtec.v3.server.protocols.ppm3.PPM3Collector");
			coletorConfig.setType(SPVLNX);
			if(ip == null){
				coletorConfig.setIp(DEFAUT_IP_SPVL);
				ip = DEFAUT_IP_SPVL;
			} else {
				coletorConfig.setIp(ip);
			}
		}

		coletorConfig.setTypeName("Ethernet");
		coletorConfig.setPort(tcpPort);
		log(Level.INFO, "Set ColectorConfig "+ ip +" port: 8886");
	}

	public void sendRegen(int idColetor) {
		Colector coletor = boardColector.getColector(idColetor);
		coletor.sendRegen();

	}
	/**
	 * Returns all network elements into one colector;
	 * @param idColetor the id of colector in server
	 * */
	public Collection<? extends NE> getAllNeIntoColector(int idColetor){
		Colector coletor = boardColector.getColector(idColetor);
		return coletor.getAllNE();
	}
	
	/**
	 * Returns all network elements into one colector;
	 * @param idColetor the id of colector in server
	 * */
	public Collection<? extends NE> getAllNeIntoColector(TypeSupervisor type) {
		Colector coletor = boardColector.getColector(type);
		return coletor.getAllNE();
	}
	/**
	 * Returns all network elements in all colectores have been started;
	 * 
	 * */
	public Collection<? extends NE> getAllNE() {
		List<NE> allNes = new ArrayList<NE>();
		for (Colector c : boardColector.getAllColector()) {
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
		for (Colector c : boardColector.getAllColector()){
			if (c != null) {
				result = c.getNE(s);

			}
		}

		return result;
	}

	public static Servidor getInstance(){
		if(instance == null){
			instance = new Servidor();
		}

		return instance;
	}
	
	public Colector getColector(TypeSupervisor type){
		return boardColector.getColector(type);
	}
	public Colector getColector(int  supervisorSite){
		return boardColector.getColector(supervisorSite);
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

	public static void main(String[] args) {
		//		Log.setConsole(true);
		Servidor server = Servidor.getInstance();
		server.start(Servidor.TypeSupervisor.SPVJ);
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
		for (Colector c : boardColector.colectores.values()){
			c.shutdown();
		}
		
	}

	
}
