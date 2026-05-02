package br.com.padtec.v3.server.protocols.ppm2v2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import br.com.padtec.v3.data.Alarm;
import br.com.padtec.v3.data.ColectorConfig;
import br.com.padtec.v3.data.Command;
import br.com.padtec.v3.data.GenericExtendedAlarm;
import br.com.padtec.v3.data.Notification;
import br.com.padtec.v3.data.NotificationListener;
import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.impl.Fan_Impl;
import br.com.padtec.v3.data.impl.NE_Impl;
import br.com.padtec.v3.data.impl.PBAmp_Impl;
import br.com.padtec.v3.data.impl.SupSPVJ_Impl;
import br.com.padtec.v3.data.impl.T100D_GT_Impl;
import br.com.padtec.v3.data.impl.Transponder_Impl;
import br.com.padtec.v3.data.impl.TrpBiDWDMRate_Impl;
import br.com.padtec.v3.data.impl.Unknown_Impl;
import br.com.padtec.v3.data.ne.Bidirectional;
import br.com.padtec.v3.data.ne.LaserOff;
import br.com.padtec.v3.data.ne.NE;
import br.com.padtec.v3.data.ne.Rateable;
import br.com.padtec.v3.data.ne.Sloted;
import br.com.padtec.v3.data.ne.SupSPVJ;
import br.com.padtec.v3.data.ne.TransponderOTN;
import br.com.padtec.v3.data.ne.TrpOTNTerminal;
import br.com.padtec.v3.data.ne.Unmanaged;
import br.com.padtec.v3.server.AlarmFactory;
import br.com.padtec.v3.server.Colector;
import br.com.padtec.v3.server.CommandFactory;
import br.com.padtec.v3.server.protocols.ppm2v2.handler.AmplifierHandler;
import br.com.padtec.v3.server.protocols.ppm2v2.handler.HandlerInterface;
import br.com.padtec.v3.server.protocols.ppm2v2.handler.SpvjHandler;
import br.com.padtec.v3.server.protocols.ppm2v2.handler.TransponderHandler;
import br.com.padtec.v3.server.protocols.ppm2v2.handler.TrpOTNTerminalHandler;
import br.com.padtec.v3.server.protocols.util.collection.UpdateScheduler;
import br.com.padtec.v3.server.protocols.util.io.AbstractConnectionAlarmManager;
import br.com.padtec.v3.util.BoardSerialControl;
import br.com.padtec.v3.util.Functions;
import br.com.padtec.v3.util.StateHistory.State;
import br.com.padtec.v3.util.log.Log;
import br.ufabc.controlplane.metropad.Servidor;
import br.ufabc.equipment.Supervisor;

/**
 * This class is used to treat the PPM2v2 packets received and sent 
 * to SPVJ Supervisor 
 * 
 * */public class ColetorPPM2v2  implements Colector {
	 private boolean debug = false;
	 private boolean debug2 = false;
	 private boolean debugTrap = false;
	 private ColectorConfig colectorConfig;
	 //  private GenericMySQL db;
	 static long HIGH_PRIORITY_INTERVAL = 300L;

	 private static long LOW_PRIORITY_INTERVAL = 3000L;

	 public static long REGEN_INTERVAL = 5000L;

	 private final UpdateScheduler nodeDb = new UpdateScheduler();

	 private List<NotificationListener> listener = new ArrayList<NotificationListener>(1);

	 private final ReaderWriter[] rw = new ReaderWriter[2];

	 boolean[] rwNotResponding = { true, true };
	 private PPM2v2Queue getsOutBuffer;
	 private PPM2v2Queue highPriorityOutBuffer;
	 private Map<SerialNumber, NE_Impl> addedE;
	 private NEFactoryPPM2v2 neFactory;
	 private boolean continua = true;
	 private Set<SerialNumber> sendAlarmNew;
	 private String conection;
	 private static long NO_CONNECTION_TIMEOUT = 180000L;

	 private long setAsNotResponding = System.currentTimeMillis() + 
	 NO_CONNECTION_TIMEOUT;

	 private int connected = 0;
	 private boolean rwWriteDirection;
	 public static final List<HandlerInterface<PPM2v2>> handlerList = new ArrayList<HandlerInterface<PPM2v2>>(8);
	 private Supervisor supervisor;
	 private Map<SerialNumber, Object> notResponding = new ConcurrentHashMap<SerialNumber, Object>();

	 static
	 {
		 handlerList.add(new TrpOTNTerminalHandler());
		 handlerList.add(new TransponderHandler());
		 handlerList.add(new AmplifierHandler());
		 //    handlerList.add(new SupSpvjFilhoHandler());
		 handlerList.add(new SpvjHandler());
		 //    handlerList.add(new CombinerHandler());
		 //    handlerList.add(new MuxponderHandler());
		 //    handlerList.add(new RoadmPadtecHandler());
	 }

	 /**
	  * Constructor
	  * 
	  * */
	 public ColetorPPM2v2()	{
		 this.addedE = Collections.synchronizedMap(new TreeMap<SerialNumber, NE_Impl>());
		 this.sendAlarmNew = Collections.synchronizedSet(new TreeSet<SerialNumber>());
		 this.getsOutBuffer = new PPM2v2Queue();
		 this.highPriorityOutBuffer = new PPM2v2Queue();
		 if (Functions.isLct) {
			 this.colectorConfig = new ColectorConfig();
		 }
		 this.neFactory = NEFactoryPPM2v2.getInstance();
		 //    this.db = DataBaseFactory.getColectorInstance();
	 }
	 public ColetorPPM2v2(Supervisor supervisor)	{
 		 this();
		 this.supervisor = supervisor;
	}
	 /**
	  * Sets up the number of connections up
	  *  
	  * */
	 private void onConnectionStateChange(String connectionName, Boolean connectionUp)	{
		 try	{
			 this.connected = 0;
			 for (ReaderWriter item : this.rw) {
				 if (item != null) {
					 if (item.isConnected()) {
						 this.connected += 1;
					 }
				 }
			 }

			 if (this.connected > 0)
				 this.setAsNotResponding = 9223372036854775807L; 
			 else 
				 this.setAsNotResponding = Math.min(this.setAsNotResponding,
						 System.currentTimeMillis() + NO_CONNECTION_TIMEOUT);
		 } catch (RuntimeException e) {
			 log("Fail in onConnectionStateChange", e);
		 }
	 }

	 public static boolean isTlv(NE_Impl ne)  {
		 //	    if (ne instanceof CombinerAsTerminal) {
		 //	      return false;
		 //	    }
		 //	    return (ne instanceof Tlv);
		 return false;
	 }


	 /**
	  * Sets the serial connection 
	  * */
	 public boolean setConnection(String porta, int initTimeout, int baudRate) {
		 this.conection = porta;
		 ReaderWriter r = new ReaderWriter(porta, initTimeout, baudRate);
		 r.setConnectionAlarmManager(new ConnectionAlarmManager());
		 r.setName("PPM2v2R/W:" + porta + ":" + baudRate);
		 this.rw[0] = r;
		 return true;
	 }

	 /**
	  * Sets the TCP connection
	  * @param ip The destination's IP
	  * @param port The destination's port
	  * */
	 public boolean setConnection(String ip, int port)	 {
		 this.conection = ip;
		 ReaderWriter r = IoSystem.getInstance().createConnection(ip, port);
		 r.setConnectionAlarmManager(new ConnectionAlarmManager());
		 r.setName("PPM2v2R/W:" + ip + ":" + port);
		 this.rw[0] = r;
		 return true;
	 }

	 /**
	  * Sets a TCP connection of backup
	  * @param ip The destination's IP
	  * @param port The destination's port
	  * */
	 public boolean setBackUpConnection(String ip, int port)  {
		 ReaderWriter r = IoSystem.getInstance().createConnection(ip, port);
		 r.setConnectionAlarmManager(new ConnectionAlarmManager());
		 r.setName("PPM2v2R/W:" + ip + ":" + port);
		 this.rw[1] = r;
		 return true;
	 }

	 /**
	  * Sends a PPM2v2 packet to regenerate traps from supervisor
	  * */
	 public void sendRegen() {
		 sendRegen((byte)0);
	 }

	 /**
	  * Sends a PPM2v2 packet to regenerate traps from supervisor
	  * @param addr the destination address
	  * */
	 private void sendRegen(byte addr)
	 {
		 log(Level.INFO, "Sending regenerate traps (PPM2v2.SET_REGTRAPS)");
		 byte[] address = new byte[PPM2v2.ADDR_SIZE];
		 address[0] = addr;
		 enqueue( new PPM2v2(PPM2v2.CMD_SET, PPM2v2.SET_REGTRAPS, null,  address , null));
	 }

	 /**
	  * Returns a collection of Network Elements from Board Manage Class 
	  * */
	 public Collection<? extends NE> getAllNE() {
		 return this.nodeDb.getAllNe().values();
	 }

	 /**
	  * Returns a network element implementation from Board Manage 
	  * by serial number
	  * 
	  * @param serial The serial Number of NE
	  * */
	 public NE_Impl getNE(SerialNumber serial) {
		 if (serial == null) {
			 return null;
		 }
		 return this.nodeDb.getNe(serial);
	 }

	 
	 public void addNotificationListener(NotificationListener list)
	 {
		 this.listener.add(list);
	 }

	 public void removeNotificationListener(NotificationListener list)
	 {
		 this.listener.remove(list);
	 }

	 public void addCommand(Command cmd)
	 {
		 PPM2v2 sendPacket;
		 SerialNumber serial = cmd.getSerialNumber();

		 NE_Impl ne = this.nodeDb.getNe(serial);
		 if (ne == null) {
			 log(Level.WARNING, "Tried to send a telecommand to a non existing NE: " + 
					 cmd.toString());
			 return;
		 }

		 if (cmd.getCommand() == 49) {
			 sendPacket = command2Ppm2v2(cmd, ne);
			 enqueue(sendPacket);
			 return;
		 }

		 if (alarmOnSend(ne.getSupAddress(), Functions.i2b(cmd.getCommandCode())))
			 //      if (ne instanceof SHK_Impl) {
			 //        boolean responding = ne.isUp();
			 //        if (responding) {
			 //          sendNotification(
			 //            AlarmFactoryPPM2v2.createCommandAlarm(ne, "", ShkHelper.getContactFromCommand(cmd.getCommandCode())));
			 //
			 //          PPM2v2 sendPacket = command2Ppm2v2(cmd, ne);
			 //          enqueue(sendPacket);
			 //        } else {
			 //          sendNotification(
			 //            AlarmFactoryPPM2v2.createCommandNotSentAlarm(ne, "", 
			 //            ShkHelper.getContactFromCommand(cmd.getCommandCode())));
			 //        }
			 //      } else {
			 sendNotification( AlarmFactoryPPM2v2.createCommandAlarm(ne, cmd.getCommandName(), 0));

		 sendPacket = command2Ppm2v2(cmd, ne);
		 enqueue(sendPacket);
		 //      }
	 }

	 public static PPM2v2 command2Ppm2v2(Command cmd, NE ne)  {
		 PPM2v2 sendPacket;
		 if (cmd.getCommand() == 49) {
			 sendPacket = new PPM2v2((byte)1, (byte)48, null, PPM2v2Helper.getBytes(ne), null);
			 return sendPacket;
		 }
		 sendPacket = new PPM2v2((byte)2, (byte)cmd.getCommandCode(),   null, PPM2v2Helper.getBytes(ne), cmd.getParameters());
		 return sendPacket;
	 }

	 public void shutdown() {
		 this.continua = false;
	 }

	 private void sendNotification(Notification n)  {
		 for (NotificationListener l : this.listener)
			 l.notify(n);
	 }

	 private void analyzeGet(PPM2v2 pacote, String fromAddress)  {
	 }

	 private void analyzeSet(PPM2v2 pacote, String fromAddress)  {
	 }

	 private void analyzeResponse(PPM2v2 pacote, byte comando, byte parametro, String fromAddress)  {
		 int site = PPM2v2.getSiteFromAddress(pacote.getSource());
		 SerialNumber serial = PPM2v2Helper.getSerial(pacote.getSource());
		 switch (pacote.getParameter())    {
		 case 0:
			 if ((comando == -2) || (comando == 1)) {
				 if (pacote.getDataSize() == 0)        {
					 logResponse(fromAddress, site, "RESPONSE OK_EMPTY", serial, null,  comando, parametro, null);
				 } else {
					 Integer newSlot;
					 List<Notification> notifs = new ArrayList<Notification>(4);
					 if (this.debug) {
						 logResponse(fromAddress, site, "RESPONSE OK", serial, pacote.toString(), comando, parametro, null);
					 }

					 NE_Impl ne = (NE_Impl)this.addedE.get(serial);
					 if (ne == null)   {
						 if (this.debug) {
							 log(fromAddress, site, "Board not recently added " +   serial.toShortString(), null);
						 }

						 ne = this.nodeDb.getNe(serial);
						 if (ne == null)
						 {
							 if (this.debug2) {
								 log(fromAddress, site, "Lost TRAP create " + pacote.toString(), 
										 null);
							 }
							 return;
						 }
						 if (this.debug) {
							 log(fromAddress, site, "Board exists " + serial.toShortString(), 
									 null);
						 }

						 if (parametro == -2) {
							 parametro = setExtendedParameter(ne, pacote.getDataArray().length);
						 }

						 Integer oldSlot = null;
						 if (ne instanceof Sloted)
							 oldSlot = Integer.valueOf(ne.getSlot());
						 try {
							 this.neFactory.setNE(this, ne, pacote, parametro, notifs);
							 if (ne instanceof SupSPVJ_Impl)
								 checkSupLink((SupSPVJ_Impl)ne);
						 }
						 catch (RuntimeException e) {
							 log(fromAddress, site, "Exception setting board data " + 
									 pacote.toString(), e);
						 }
						 newSlot = null;
						 if (ne instanceof Sloted) {
							 newSlot = Integer.valueOf(ne.getSlot());
						 }
						 if ((parametro == 0) && (oldSlot != null) && (newSlot != null) && 
								 (oldSlot.intValue() > 0) && (newSlot.intValue() >= 0) &&  (!(oldSlot.equals(newSlot))))  {
							 GenericExtendedAlarm a = AlarmFactory.createGenericAlarm(ne, null, 55, null, true, "NC" + 
									 oldSlot + "->" + newSlot);
							 sendNotification(a);
						 }

					 } else {
						 if (parametro == -2) {
							 parametro = setExtendedParameter(ne, pacote.getDataArray().length);
						 }
						 if (parametro == 0) {
							 if (this.debug2) {
								 log(fromAddress, site, "Board recently added " + 
										 pacote.toString(), null);
							 }

							 this.neFactory.setNE(this, ne, pacote, parametro, notifs);
							 if (ne instanceof SupSPVJ_Impl) {
								 checkSupLink((SupSPVJ_Impl)ne);
							 }
							 if (this.debug) {
								 log(fromAddress, site, "Consolidating " + serial.toShortString(), 
										 null);
							 }

							 if (ne.getSupAddress() != 0)
							 {
								 this.addedE.remove(serial);

								 this.nodeDb.addNe(ne);

								 sendNotification(new Notification(1, serial));

								 if (this.sendAlarmNew.remove(serial))
									 createAlarm(serial, true, 57);
							 }
							 else {
								 logResponse(fromAddress, site, "RESPONSE OK", serial, 
										 "Received packet from site ZERO " + pacote.toString(), 
										 comando, parametro, null);
							 }
						 }
					 }
					 if (this.nodeDb.createAlarmFromGetAll(serial)) {
						 try {
							 createAlarms(ne, parametro, pacote.getDataArray(), notifs, this);
						 } catch (RuntimeException e) {
							 log(fromAddress, site, 
									 "Exception getting alarms from packet: orig com " + 
									 Functions.getHexa(comando) + " orig par " + 
									 Functions.getHexa(parametro) + " " + pacote.toString(), e);
						 }
					 }

					 notifs.addAll(AlarmFactoryPPM2v2.generateTrpOtnCounterAlarms(ne, parametro));

					 for (Notification n : notifs) {
						 sendNotification(n);
					 }
					 setAsResponding(serial); }
			 } else if (comando == 2) {
				 log(fromAddress, site, "ACK " + serial.toShortString() + " par:" + 
						 Functions.getHexa(parametro) + " " + 
						 Functions.getHexa(pacote.getDataArray()), null);
				 NE_Impl ne = getNE(serial);
				 if ((ne != null) && 
						 (!(alarmOnSend(ne.getSupAddress(), parametro)))) {
					 Command cmd = CommandFactory.createOriginalCommand(getNE(serial),Functions.b2i(parametro), pacote.getDataArray());
					 /*if (ne instanceof SHK_Impl)
            sendNotification(
              AlarmFactoryPPM2v2.createCommandAlarm(ne, "", 
              ShkHelper.getContactFromCommand(cmd.getCommandCode())));
          else {*/
					 sendNotification( AlarmFactoryPPM2v2.createCommandAlarm(ne, cmd.getCommandName(), 0));
					 //          }
				 }
			 }

			 break;
		 case 1:
			 logResponse(fromAddress, site, "RESPONSE NOT_YET_AVAILABLE", serial, 
					 pacote.toString(), comando, parametro, null);
			 break;
		 case 3:
			 log(fromAddress, site, "RESPONSE BOARD_NOT_PRESENT " + 
					 serial.toShortString(), null);
			 if (Functions.getProperty("spvj.handlerespnotfound", true)) {
				 setAsNotResponding(serial);
			 }

			 break;
		 case 2:
			 logResponse(fromAddress, site, "RESPONSE COMMAND_FAILED", serial, 
					 pacote.toString(), comando, parametro, null);
			 break;
		 case 4:
			 logResponse(fromAddress, site, "RESPONSE INVALID_PARAMETER", serial, 
					 pacote.toString(), comando, parametro, null);
			 break;
		 case -1:
			 byte[] rea = pacote.getResponseExtendArray();
			 comando = rea[1];
			 parametro = rea[2];
			 analyzeResponse( new PPM2v2(PPM2v2.CMD_RESPONSE, pacote.getNonExtendedParameter(), pacote.getSource(), pacote.getDestiny(),
					 pacote.getNonExtendedResponseDataArray()), comando, parametro,fromAddress);
			 break;
		 case -2:
			 try   {
				 byte[] dados = pacote.getDataArray();
				 int pointer = 0;

				 while (pointer < dados.length) {
					 int equipmentRequetsResponseSize = Functions.b2i(dados[(pointer + 2)]);
					 byte[] tlvBlock = new byte[equipmentRequetsResponseSize + 3];
					 System.arraycopy(dados, pointer, tlvBlock, 0, tlvBlock.length);
					 pointer += tlvBlock.length;

					 if (parametro == 40) {
						 parametro = 0;
					 }

					 analyzeResponse(
							 new PPM2v2(PPM2v2.CMD_RESPONSE, PPM2v2.RESP_OK, pacote.getSource(), pacote.getDestiny(), tlvBlock), comando, 
							 parametro, fromAddress);
				 }
			 }
			 catch (RuntimeException e) {
				 logResponse(fromAddress, site, "RESPONSE TLV", serial, 
						 "Exception parsing tlv packet " + pacote.toString(), comando, 
						 parametro, e);
			 }
			 break;
		 default:
			 logResponse(fromAddress, site, "RESPONSE UNKNOWN(" +  Functions.getHexa(pacote.getParameter()) + ")", serial, 
					 "Could not handle response " + pacote.toString(), comando, parametro,  null);
		 }
		 if ((this.debug) && (pacote.getParameter() != -1)) {
			 log(Level.FINER, "====Status da existencia do elemento====");
			 log(Level.FINER, "Tabela dos recentes:" + this.addedE.containsKey(serial));
			 log(Level.FINER, "Tabela dos consolidados:" + (this.nodeDb.getNe(serial) != null));
		 }
	 }

	 public static void createAlarms(NE_Impl ne, byte parametro, byte[] data, List<Notification> notifs, ColetorPPM2v2 colector)  {
		 boolean handled = false;
		 for (HandlerInterface<PPM2v2> handler : handlerList) {
			 if (handler.canHandle(ne)) {
				 List<Notification> list = handler.getAlarmList(ne);
				 if (list != null) {
					 notifs.addAll(list);
					 handled = true;
					 break;
				 }
			 }
		 }
		 if (!(handled))
			 switch (parametro)
			 {
			 case 0:
				 notifs.addAll( AlarmFactoryPPM2v2.generateInitialAlarms(colector, data, ne));
				 break;
			 case 5:
				 notifs.addAll( AlarmFactoryPPM2v2.generateTrpOtnInitialAlarms(data[22], ne));
				 break;
			 case 12:
				 notifs.addAll( AlarmFactoryPPM2v2.generateTrpOtnInitialAlarms(data[40], ne));
				 //        if (ne instanceof CombinerAsTerminal) 
				 //        	return;
				 notifs.add(AlarmFactory.createAlarm(ne, !(((TransponderOTN)ne).isEncAIS()), 82));

				 break;
			 case 17:
				 notifs.addAll( AlarmFactoryPPM2v2.generateTrpOtnInitialAlarms(data[44],  ne));
				 notifs.add( AlarmFactory.createAlarm(ne, !(((TransponderOTN)ne).isEncAIS()), 82));
			 }
	 }

	 private boolean alarmOnSend(int site, byte commandCode)
	 {
		 return true;
	 }

	 public SupSPVJ getSiteSpvj(int site)  {
		 for (NE ne : getAllNE()) {
			 if ((ne instanceof SupSPVJ) && 
					 (ne.getSupAddress() == site)) {
				 return ((SupSPVJ)ne);
			 }
		 }

		 return null;
	 }
	 private boolean executeAlarm(PPM2v2 pacote)  {
		 SerialNumber serial = PPM2v2Helper.getSerial(pacote.getSource());
		 NE_Impl ne = nodeDb.getNe(serial);
		 if(debug2 && ne == null)    {
			 log(Level.FINER, (new StringBuilder("Serial = ")).append(serial).toString());
			 log(Level.FINER, "Received an alarm from NE == null");
		 }
		 if(ne instanceof Transponder_Impl)  {
			 Transponder_Impl transponder = (Transponder_Impl)ne;
			 boolean isStart = pacote.getParameter() > 0;
			 int trap = isStart ? ((int) (pacote.getParameter())) : pacote.getParameter() -128;
			 switch(trap) {
			 case PPM2v2.TRAP_LOS: 
				 transponder.setLos(isStart);
				 if(isStart)
					 transponder.setPin((0.0D / 0.0D));
				 break;

			 case PPM2v2.TRAP_N3DB_OR_LOF:
				 //Also it must set LOF to TrpDWDM25Otn_Impl and T100D_GC_Impl 
				 if(transponder instanceof T100D_GT_Impl) {
					 T100D_GT_Impl otn = (T100D_GT_Impl)transponder;
					 otn.setLof(isStart);
				 } else {
					 transponder.setN3db(isStart);
				 }
				 break;

			 case PPM2v2.TRAP_TRP_FAIL: 
				 transponder.setFail(isStart);
				 break;

			 case PPM2v2.TRAP_TRP_LASEROFF: 
				 if(transponder instanceof LaserOff)
				 {
					 ((LaserOff)transponder).setLaserOff(isStart);
					 if(isStart)
						 transponder.setPout((0.0D / 0.0D));
				 }
				 break;

			 case PPM2v2.TRAP_LOS2:
				 if(transponder instanceof Bidirectional) {
					 ((Bidirectional)transponder).setLos2(isStart);
					 if(isStart)
						 if(transponder instanceof T100D_GT_Impl) {
							 T100D_GT_Impl otn = (T100D_GT_Impl)transponder;
							 otn.setPin2((0.0D / 0.0D));
						 } //put here a 'else if' sentence to transponder  T100D_GC_Impl 

				 } else {
					 return false;
				 }
				 break;

			 case PPM2v2.TRAP_LASEROFF2: 
				 if(transponder instanceof Bidirectional)
				 {
					 ((Bidirectional)transponder).setLaserOff2(isStart);
					 if(isStart)
						 if(transponder instanceof T100D_GT_Impl)
						 {
							 T100D_GT_Impl otn = (T100D_GT_Impl)transponder;
							 otn.setPout2((0.0D / 0.0D));
						 } 
					 //put here a 'if' sentence to transponder  T100D_GC_Impl

				 } else {
					 return false;
				 }
				 break;

			 case PPM2v2.TRAP_TAXA_OR_LOF2:
				 /* if transponder is instance of TrpCWDMRate_Impl,
				  * 	run method setOver(isStart);
				  * if transponder is instance of TrpDWDM25Otn_Impl or T100D_GC_Impl,
				  * 	run method setLof2(isStart);'\001'
				  * 
				  * */ 
				 if(transponder instanceof TrpBiDWDMRate_Impl)
					 ((TrpBiDWDMRate_Impl)transponder).setOverRate(isStart);
				 else if(transponder instanceof T100D_GT_Impl)  {
					 T100D_GT_Impl otn = (T100D_GT_Impl)transponder;
					 otn.setLof2(isStart);
				 } else {
					 return false;
				 }
				 break;

			 case PPM2v2.TRAP_TRP_FAIL2:
				 /*if is instance of T100D_GC_Impl call setFail2(isStart)*/
				 if(transponder instanceof T100D_GT_Impl)
					 ((T100D_GT_Impl)transponder).setFail2(isStart);
				 else
					 return false;
				 break;

			 case PPM2v2.TRAP_TRPOTN_ODU_TIM: 
				 /*if is instance of TrpDWDM25Otn_Impl call setTim_ODUk(isStart)*/
				 if(transponder instanceof T100D_GT_Impl)
					 ((T100D_GT_Impl)transponder).setTim_ODUk(isStart);
				 else
					 return false;
				 break;

			 case PPM2v2.TRAP_TRPOTN_ODU_BDI: 
				 /*if is instance of TrpDWDM25Otn_Impl call setBdi_ODUk(isStart)*/
				 if(transponder instanceof T100D_GT_Impl)
					 ((T100D_GT_Impl)transponder).setBdi_ODUk(isStart);
				 else
					 return false;
				 break;

			 case PPM2v2.TRAP_TRPOTN_ODU_AIS: 
				 /*if is instance of TrpDWDM25Otn_Impl call setAis_ODUk(isStart)*/
				 if(transponder instanceof T100D_GT_Impl)
					 ((T100D_GT_Impl)transponder).setAis_ODUk(isStart);
				 else
					 return false;
				 break;

			 case PPM2v2.TRAP_TRPOTN_LOS_SYNC: 
				 /*if is instance of TrpDWDM25Otn_Impl or  T100D_GC_Impl call setLosSync(isStart)*/
				 if(transponder instanceof T100D_GT_Impl)
					 ((T100D_GT_Impl)transponder).setLosSync(isStart);
				 else
					 return false;
				 break;

			 case PPM2v2.TRAP_TRPOTN_LOS2_SYNC: 
				 /*if is instance of TrpDWDM25Otn_Impl or  T100D_GC_Impl call setLosSync2(isStart)*/
				 if(transponder instanceof T100D_GT_Impl)
					 ((T100D_GT_Impl)transponder).setLosSync2(isStart);
				 else
					 return false;
				 break;

			 case PPM2v2.TRAP_TRPOTN_J0: 
				 /*if it neither is instance of TrpDWDM25Otn_Impl nor TrpOTNTerminalSDH*/
				 return false;
				 //				break;

			 case PPM2v2.TRAP_TRPOTN_PT: 
				 if (transponder instanceof T100D_GT_Impl) {
					 /*or it's instance of TrpDWDM25Otn_Impl*/
					 PPM2v2 pacote2 = new PPM2v2((byte)1, (byte)8, PPM2v2.ADDR_NULL, pacote.getSource(), null);
					 highPriorityOutBuffer.add(pacote2);
				 } else   {
					 return false;
				 }
				 break;

			 case PPM2v2.TRAP_TRPOTN_ODU_SAPI_DAPI: 
				 if(transponder instanceof T100D_GT_Impl) {
					 /*or it's instance of TrpDWDM25Otn_Impl*/

					 PPM2v2 pacote2 = new PPM2v2((byte)1, (byte)6, PPM2v2.ADDR_NULL, pacote.getSource(), null);
					 highPriorityOutBuffer.add(pacote2);
				 } else  {
					 return false;
				 }
				 break;

			 case PPM2v2.TRAP_TRPOTN_OTU_SAPI_DAPI: 
				 if(transponder instanceof TransponderOTN) {
					 PPM2v2 pacote2 = new PPM2v2((byte)1, (byte)13, PPM2v2.ADDR_NULL, pacote.getSource(), null);
					 highPriorityOutBuffer.add(pacote2);
				 } else	{
					 return false;
				 }
				 break;

			 case PPM2v2.TRAP_TRPOTN_OTU_TIM: 
				 if(transponder instanceof T100D_GT_Impl)
					 ((T100D_GT_Impl)transponder).setTim_OTUk(isStart);
				 /* else if its instance of T100D_GC_Impl call setTim_OTUk_A(isStart);*/
				 else
					 return false;
				 break;

			 case PPM2v2.TRAP_TRPOTN_OTU_BDI: 
				 if(transponder instanceof T100D_GT_Impl)
					 ((T100D_GT_Impl)transponder).setBdi_OTUk(isStart);
				 /* else if its instance of T100D_GC_Impl call setBdi_OTUk_A(isStart);*/
				 else
					 return false;
				 break;

			 case PPM2v2.TRAP_TRPOTN_LOM: 
				 if(transponder instanceof T100D_GT_Impl)
					 ((T100D_GT_Impl)transponder).setLom(isStart);
				 /* else if its instance of T100D_GC_Impl call setLom_A(isStart);*/
				 else
					 return false;
				 break;

			 case PPM2v2.TRAP_TRPOTN_ENCAISOFF: 
				 if(transponder instanceof T100D_GT_Impl)
					 ((T100D_GT_Impl)transponder).setEncAIS(!isStart);
				 else 
					 /* else if its instance of T100D_GC_Impl call setEncAIS(!isStart)*/
					 return false;

				 break;

			 case PPM2v2.TRAP_TRPOTN_ODU_TTI_REF_TX: 
				 if(transponder instanceof TrpOTNTerminal)
				 {
					 PPM2v2 pacote2 = new PPM2v2(PPM2v2.CMD_GET, PPM2v2.GET_ODU_SAPI_DAPI_REF_TX, PPM2v2.ADDR_NULL, pacote.getSource(), null);
					 highPriorityOutBuffer.add(pacote2);
				 } else
				 {
					 return false;
				 }
				 break;

			 case PPM2v2.TRAP_TRPOTN_ODU_TTI_REF_RX: 
				 if(transponder instanceof TrpOTNTerminal)
				 {
					 PPM2v2 pacote2 = new PPM2v2(PPM2v2.CMD_GET, PPM2v2.GET_ODU_SAPI_DAPI_REF_RX, PPM2v2.ADDR_NULL, pacote.getSource(), null);
					 highPriorityOutBuffer.add(pacote2);
				 } else
				 {
					 return false;
				 }
				 break;

			 case PPM2v2.TRAP_TRPOTN_OTU_TTI_REF_TX: 
				 if(transponder instanceof TransponderOTN)
				 {
					 PPM2v2 pacote2 = new PPM2v2(PPM2v2.CMD_GET, PPM2v2.GET_OTU_SAPI_DAPI_REF_TX, PPM2v2.ADDR_NULL, pacote.getSource(), null);
					 highPriorityOutBuffer.add(pacote2);
				 } else
				 {
					 return false;
				 }
				 break;

			 case PPM2v2.TRAP_TRPOTN_OTU_TTI_REF_RX: 
				 if(transponder instanceof TransponderOTN)
				 {
					 PPM2v2 pacote2 = new PPM2v2(PPM2v2.CMD_GET, PPM2v2.GET_OTU_SAPI_DAPI_REF_RX, PPM2v2.ADDR_NULL, pacote.getSource(), null);
					 highPriorityOutBuffer.add(pacote2);
				 } else
				 {
					 return false;
				 }
				 break;

			 case PPM2v2.TRAP_TRPOTN_OTU_TTI_REF_TX2:
				 /* if transponder is instance of TrpOTNRegenerador,
				  * send packet GET with parameter PPM2v2.GET_OTU_SAPI_DAPI_REF_TX2
				  * using high priority*/
				 return false;
				 //				break;

			 case PPM2v2.TRAP_TRPOTN_OTU_TTI_REF_RX2:
				 /* if transponder is instance of TrpOTNRegenerador,
				  * send packet: PPM2v2 pacote2 = new PPM2v2(PPM2v2.CMD_GET, 
				  * 					PPM2v2.GET_OTU_SAPI_DAPI_REF_RX2, PPM2v2.ADDR_NULL, pacote.getSource(), null);
				  * using high priority*/
				 break;

			 case PPM2v2.TRAP_TRPOTN_OTU_TIM2:
				 /* if transponder is instance of T100D_GC_Impl,
				  * call method  setTim_OTUk_B(isStart) else return false ;
				  * */
				 return false;
				 //				break;

			 case PPM2v2.TRAP_TRPOTN_OTU_BDI2:
				 /* if transponder is instance of T100D_GC_Impl,
				  * call method  setBdi_OTUk_B(isStart) else return false ;
				  * */
				 return false;
				 //				break;

			 case PPM2v2.TRAP_TRPOTN_LOM2: 
				 /* if transponder is instance of T100D_GC_Impl,
				  * call method setLom_B(isStart) else return false  */
				 return false;
				 //				break;

			 case PPM2v2.TRAP_TRPOTN_OTU_SAPI_DAPI2: 
				 /* if transponder is instance of TrpOTNRegenerador,
				  * send packet:  PPM2v2 pacote2 = new PPM2v2(PPM2v2.CMD_GET, PPM2v2.GET_OTU_SAPI_DAPI2, 
				  * 					PPM2v2.ADDR_NULL, pacote.getSource(), null); 
				  * using high priority*/
				 return false;
				 //				break;

			 case PPM2v2.TRAP_TRPOTN_STAT: 
			 default:
				 return false;
			 }
		 } else if(ne instanceof PBAmp_Impl)  {
			 PBAmp_Impl amplifier = (PBAmp_Impl)ne;
			 switch(pacote.getParameter())  {
			 case PPM2v2.TRAP_LOS:  
				 amplifier.setLos(true);
				 break;

			 case PPM2v2.TRAP_AMP_FAIL: 
				 amplifier.setFail(true);
				 break;

			 case PPM2v2.TRAP_AMP_LASEROFF:
				 amplifier.setLaserOff(true);
				 break;

			 case PPM2v2.TRAP_AMP_MCS_TEMPERATURE_ALARM: 
				 amplifier.setMCS(true);
				 break;

			 case PPM2v2.TRAP_AMP_CURRENT_ALARM: 
				 amplifier.setCurrentAlarm(true);
				 break;

			 case PPM2v2.TRAP_AMP_TEMPERATURE_ALARM: 
				 amplifier.setTemperatureAlarm(true);
				 break;

			 case -127: 
				 amplifier.setLos(false);
				 break;

			 case -126: 
				 amplifier.setFail(false);
				 break;

			 case -125: 
				 amplifier.setLaserOff(false);
				 break;

			 case -123: 
				 amplifier.setMCS(false);
				 break;

			 case -122: 
				 amplifier.setCurrentAlarm(false);
				 break;

			 case -121: 
				 amplifier.setTemperatureAlarm(false);
				 break;

			 default:
				 return false;

			 case -124: 
			 case PPM2v2.TRAP_AMP_ALS: 
				 break;
			 }

		 } else if(ne instanceof Fan_Impl) {
			 Fan_Impl fan = (Fan_Impl)ne;
			 byte p = pacote.getParameter();
			 switch(p)
			 {
			 case -127: 
			 case PPM2v2.TRAP_FAN_OVERHEAT:
				 fan.setOverHeat(p == 1);
				 break;

			 case -126: 
			 case PPM2v2.TRAP_FAN_FAIL1: 
				 fan.setFanOk(1, p == -126);
				 break;

			 case -125: 
			 case PPM2v2.TRAP_FAN_FAIL2:
				 fan.setFanOk(2, p == -125);
				 break;

			 case -124: 
			 case PPM2v2.TRAP_FAN_FAIL3: 
				 fan.setFanOk(3, p == -124);
				 break;
			 }
			 /* to support RateMeter_Impl, FOA_Impl, OpticalProtection_Impl, OpticalSwitch8x1_Impl, 
			  * SHK_Impl, PowerSupply_Impl, AmplifierPowerSupply_Impl, Muxponder_Impl need implements the trap parameters
			  *  */
		 } else  {
			 return false;
		 }
		 return true;
	 }


	 private void analyzeTrap(PPM2v2 pacote, String fromAddress) {
		 List<Notification> event = new ArrayList<Notification>(2);
		 int site = PPM2v2.getSiteFromAddress(pacote.getSource());
		 SerialNumber serial = PPM2v2Helper.getSerial(pacote.getSource());

		 if (BoardSerialControl.isValidBoardSerial(serial)) {


			 switch (pacote.getParameter()){
			 case 0:
				 logTrap(fromAddress, site, pacote.getParameter(), serial, null, null);
				 analyzeNewElement(pacote);
				 NE_Impl ne = getNE(serial);
				 for (HandlerInterface<PPM2v2> handler : handlerList) {
					 if (handler.canHandle(ne)) {
						 List<PPM2v2> packetToSend = new ArrayList<PPM2v2>(2);
						 handler.onReceiveTrap(ne, pacote, packetToSend, event);
						 for (Iterator<PPM2v2> localIterator3 = packetToSend.iterator(); localIterator3.hasNext(); ) { 
							 PPM2v2 packet = localIterator3.next();
							 enqueue(packet);
						 }
						 packetToSend = null;
					 }
				 }
				 break;
			 case -128:
				 logTrap(fromAddress, site, pacote.getParameter(), serial, null, null);
				 break;
			 default:
				 if (this.debugTrap) {
					 logTrap(fromAddress, site, pacote.getParameter(), serial, "Received", 
							 null);
				 }

				 NE_Impl neCons = this.nodeDb.getNe(serial);
				 if ((neCons != null) && (!(neCons instanceof Unknown_Impl))) {
					 this.nodeDb.notifyAlarmTrapReceived(serial);
					 ne = getNE(serial);
					 boolean bool1 = false;
					 for (HandlerInterface<PPM2v2> handler : handlerList) {
						 if (handler.canHandle(ne)) {
							 List<PPM2v2> packetToSend = new ArrayList<PPM2v2>(2);

							 bool1 = bool1 | 
							 handler.onReceiveTrap(ne, pacote, packetToSend, event);
							 this.highPriorityOutBuffer.addAll(packetToSend);
							 packetToSend = null;
						 }
					 }
					 if (!(bool1)) {
						 try {
							 if (!(executeAlarm(pacote)))
								 logTrap(fromAddress, site, pacote.getParameter(), serial, 
										 "Could not execute alarm for board " + pacote.toString(), 
										 null);
						 }
						 catch (Exception e) {
							 logTrap(fromAddress, site, pacote.getParameter(), serial, 
									 "Exception setting board state " + pacote.toString(), e);
						 }
					 }

					 Alarm newAlarm = AlarmFactoryPPM2v2.getAlarm(pacote, neCons);
					 if (newAlarm != null)
						 event.add(newAlarm);
				 }
				 else {
					 logTrap(fromAddress, site, pacote.getParameter(), serial, 
							 "Trap received for an unknown board", null);
				 }
			 }
			 for (Notification n : event) {
				 if (n instanceof Alarm) {
					 Alarm a = (Alarm)n;

					 sendNotification(a);
				 }
			 }
			 event = null;
		 } else {
			 logTrap(fromAddress, site, pacote.getParameter(), serial, 
					 "Trap received for an invalid board", null);
		 }
	 }

	 /**
	  * Creates a network element from packet
	  * */
	 private NE_Impl createElement(PPM2v2 pacote){
		 byte[] source = pacote.getSource();
		 SerialNumber serial = PPM2v2Helper.getSerial(source);
		 int site = PPM2v2.getSiteFromAddress(pacote.getSource());
		 //		try {
		 NE ne = getNE(serial);
		 if ((ne != null) && (ne.getSupAddress() != site)) {
			 log(Level.WARNING, "Board in wrong location: " + 
					 serial.toShortString() + " registered in Site " + 
					 ne.getSupAddress() + " but located in Site " + site);
			 sendNotification(new Notification(4, serial));
			 /*
			  * Should call method Viewer.getMain().getMapServer().removeNE(ne, Viewer.getMain().getId());
			  * to remove this ne of graphical interface of rack configuration in supervisor;
			  * */
		 }
		 //		} catch (RemoteException e) {
		 //			log("Exception in remote access call [createElement()] pacote: " +	pacote.toString(),	e);
		 //		}


		 NE_Impl ne_impl = this.nodeDb.getNe(serial);
		 if (ne_impl != null)
		 {
			 ne_impl.setSupAddress(site);
			 return ne_impl;
		 }

		 //		if (!(this.db.isNeInDB(serial))) {
		 this.sendAlarmNew.add(serial);
		 //		}

		 ne_impl = this.addedE.get(serial);
		 if (ne_impl == null) {
			 ne_impl = this.neFactory.createNE(serial);
			 if (ne_impl instanceof SupSPVJ_Impl) {
				 int i = this.colectorConfig.getSite();
				 if (i >= 0) {
					 SupSPVJ_Impl spvj = (SupSPVJ_Impl)ne_impl;
					 spvj.setIP(this.colectorConfig.getIp());
					 checkSupLink(spvj);
				 }
				 //updates the Supervisor's site address in boardColectorObject 
				 Servidor.getInstance().updateSiteColector(site, this);
			 }
		 }

		 if (ne_impl != null) {
			 ne_impl.setSupAddress(site);
			 if (ne_impl instanceof Unknown_Impl) {
				 this.nodeDb.addNe(ne_impl);
				 sendNotification(new Notification(1, serial));
				 if (this.sendAlarmNew.remove(serial)) {
					 createAlarm(serial, true, 57);
				 }

			 } else	{
				 PPM2v2 sendPacket;
				 PPM2v2 pacote2;
				 this.addedE.put(serial, ne_impl);
				 this.nodeDb.addNe(ne_impl); // to test, doesn't exist in original code

				 sendSimpleGet(pacote.getSource(), ne_impl);

				 /*
				  * if ne instance of BasicOTN use parameter PPM2v2.GET_OTN
				  * if is a TrpOTNRegenerador use parameter GET_OTN_REGEN
				  * 
				  */

				 if (ne_impl instanceof TrpOTNTerminal) {
					 /* or is a CombinerAsTerminal*/
					 sendPacket = new PPM2v2(PPM2v2.CMD_GET, PPM2v2.GET_OTN_TERM, null, pacote.getSource(), null);

					 if (Functions.isLct)
						 this.highPriorityOutBuffer.add(sendPacket);
					 else
						 enqueue(sendPacket);
				 }


				 if (ne_impl instanceof TrpOTNTerminal) {
					 /* or is a CombinerAsTerminal or TrpPreOTN*/
					 pacote2 = new PPM2v2(PPM2v2.CMD_GET, PPM2v2.GET_ODU_SAPI_DAPI, PPM2v2.ADDR_NULL, pacote.getSource(), null);
					 enqueue(pacote2);

					 pacote2 = new PPM2v2(PPM2v2.CMD_GET, PPM2v2.GET_PT, PPM2v2.ADDR_NULL, pacote.getSource(), null);
					 enqueue(pacote2);
				 }
				 if (ne_impl instanceof TrpOTNTerminal) {
					 /* or is a CombinerAsTerminal*/
					 pacote2 = new PPM2v2(PPM2v2.CMD_GET, PPM2v2.GET_OTU_SAPI_DAPI, PPM2v2.ADDR_NULL, pacote.getSource(), null);
					 enqueue(pacote2);

					 pacote2 = new PPM2v2(PPM2v2.CMD_GET, PPM2v2.GET_STAT, PPM2v2.ADDR_NULL, pacote.getSource(), null);
					 enqueue(pacote2);
				 }

				 /*if ne is a TrpOTNRegenerador send two packets with parameters PPM2v2.GET_OTU_SAPI_DAPI and 
				  * PPM2v2.GET_OTU_SAPI_DAPI2*/

			 }

		 }else{
			 log(Level.WARNING, "Could not create board " + serial.toShortString() + 
			 " check list.txt");
		 }
		 return ne_impl;
	 }

	 private void checkSupLink(SupSPVJ_Impl spvj) {
		 for (ReaderWriter item : this.rw)
			 if ((item != null) && (item.getConection().equals(spvj.getIP()))) {
				 item.getConnectionAlarmManager().setNe(spvj);
				 return;
			 }
	 }

	 private void sendCompleteGet(NE_Impl ne) {
		 for (HandlerInterface<PPM2v2> handler : handlerList) {
			 if (handler.canHandle(ne)) {
				 handler.prepareFullUpdate(ne);
				 List<PPM2v2> packetList = handler.getUpdatePacketList(ne);
				 if (packetList != null) {
					 for (PPM2v2 packet : packetList) {
						 if (Functions.isLct)
							 this.highPriorityOutBuffer.add(packet);
						 else {
							 enqueue(packet);
						 }
					 }
				 }
			 }
		 }
		 int p = ne.getSerial().getPart();
		 int sn = ne.getSerial().getSeq();
		 byte[] destino = new byte[5];

		 destino[0] = (byte)ne.getSupAddress();
		 destino[1] = (byte)(p >> 8 & 0xFF);
		 destino[2] = (byte)(p & 0xFF);
		 destino[3] = (byte)(sn >> 8 & 0xFF);
		 destino[4] = (byte)(sn & 0xFF);

		 /*type Tlv of NE isn't defined to actual support
		 if it was supported would send a packet with PPM2v2.GET_ALL_TLV parameter. 
		PPM2v2 sendPacket = new PPM2v2(PPM2v2.CMD_GET, (isTlv(ne)) ? PPM2v2.GET_ALL_TLV : PPM2v2.GET_ALL, null, destino, null);  
		  */

		 PPM2v2 sendPacket = new PPM2v2(PPM2v2.CMD_GET, PPM2v2.GET_ALL, null, destino, null);

		 if (Functions.isLct)
			 this.highPriorityOutBuffer.add(sendPacket);
		 else {
			 enqueue(sendPacket);
		 }

		 if (ne instanceof Rateable) {
			 sendPacket = new PPM2v2(PPM2v2.CMD_GET, PPM2v2.GET_TAXA, null, destino, null);
			 if (Functions.isLct)
				 this.highPriorityOutBuffer.add(sendPacket);
			 else {
				 enqueue(sendPacket);
			 }
		 }
		 /* if ne instance of BasicOTN use parameter PPM2v2.GET_OTN 
		  * if is a TrpOTNRegenerador use parameter GET_OTN_REGEN*/

	 }

	 private void sendSimpleGet(byte[] destino, NE_Impl ne)	{

		 for (HandlerInterface<PPM2v2> handler : handlerList) {
			 if (handler.canHandle(ne)) {
				 List<PPM2v2> packets = handler.getUpdatePacketList(ne);
				 if (packets != null) {
					 if (Functions.isLct)
						 this.highPriorityOutBuffer.addAll(packets);
					 else {
						 this.getsOutBuffer.addAll(packets);
					 }
				 }
			 }
		 }
		 PPM2v2 sendPacket = new PPM2v2(PPM2v2.CMD_GET, PPM2v2.GET_ALL, null, 	destino, null);

		 if (Functions.isLct)
			 this.highPriorityOutBuffer.add(sendPacket);
		 else
			 this.getsOutBuffer.add(sendPacket);
	 }

	 public void enqueue(PPM2v2 pacote)	{
		 System.out.println("enqueuing packet: " + pacote);
		 if (pacote.getCommand() == 2) {
			 if (!(this.highPriorityOutBuffer.contains(pacote))) {
				 this.highPriorityOutBuffer.add(pacote);
			 }
		 }
		 else if (!(this.getsOutBuffer.contains(pacote)))
			 this.getsOutBuffer.add(pacote);
	 }

	 @Deprecated
	 private void createAlarm(SerialNumber serial, boolean isNew, int IdAlarm)
	 {
		 if (serial != null) {
			 NE_Impl ne = getNE(serial);

			 if (ne != null) {
				 Alarm a = AlarmFactory.createAlarm(ne, isNew, IdAlarm);
				 sendNotification(a);
			 }
		 }
	 }

	 private void createAlarm(NE_Impl ne, String location, int idAlarm, Integer contact, boolean isNew, String detail)
	 {
		 GenericExtendedAlarm alarm = AlarmFactory.createGenericAlarm(ne, location, 
				 idAlarm, contact, isNew, detail);
		 sendNotification(alarm);
	 }

	 private void analyzePacket(PPM2v2 pacote, String fromAddress) {
		 //		System.out.println("Analyzing Packet: " + pacote);
		 SerialNumber serial = PPM2v2Helper.getSerial(pacote.getSource());
		 switch (pacote.getCommand()) {
		 case PPM2v2.CMD_GET:
			 analyzeGet(pacote, fromAddress);
			 setAsResponding(serial);
			 break;
		 case PPM2v2.CMD_SET:
			 analyzeSet(pacote, fromAddress);
			 setAsResponding(serial);
			 break;
		 case PPM2v2.CMD_RESPONSE:
			 analyzeResponse(pacote, (byte)-2, (byte)-2, fromAddress);
			 break;
		 case PPM2v2.CMD_TRAP:
		 case PPM2v2.CMD_NOTIFICATION:
			 analyzeTrap(pacote, fromAddress);
			 if (pacote.getParameter() != -128) {
				 setAsResponding(serial);
			 }else {
				 setAsNotResponding(serial);
			 }

		 }
	 }

	 /*private void setRespondingStructures()	{
		Map l = this.db.getAllSerials(getConection());
		for (Map.Entry item : l.entrySet()) {
			SerialNumber serial = (SerialNumber)item.getKey();
			Integer site = (Integer)item.getValue();
			NE_Impl ne = PartNumber.getInstance(serial);
			if (ne != null)
				if (!(ne instanceof Unmanaged)) {
					if (site != null) {
						ne.setSupAddress(site.intValue());
					}
					this.nodeDb.addNe(ne);
					if (ne instanceof SupSPVJ_Impl) {
						int i = this.colectorConfig.getSites().indexOf(site);
						if (i >= 0) {
							SupSPVJ_Impl spvj = (SupSPVJ_Impl)ne;
							spvj.setIP((String)this.colectorConfig.getIP().get(i));
							checkSupLink(spvj);
						}
					}
					sendNotification(new Notification(1, serial));
				}
				else
					log(Level.WARNING, "Board on database has an unknown part number " + 
							serial.toShortString());
		}
	}*/

	 public void run() {
		 try	{
			 //      setRespondingStructures();
			 initialiseReaderWriters();

			 long lastPacketSent = 0L;

			 long nextPacketSend = 0L;

			 while (this.continua) {
				 try {
					 this.colectorConfig.setStatus(ColectorConfig.Status.STATUS_OK);

					 for (int i = 0; i < this.rw.length; ++i) {
						 if (this.rw[i] != null) {
							 while (!(this.rw[i].getInboundPackets().isEmpty())) {
								 analyzePacket((PPM2v2)this.rw[i].getInboundPackets().remove(0), this.rw[i].getConection());
								 if (this.rw[i].isSerial()) {
									 nextPacketSend = System.currentTimeMillis() + 300L;
								 }
							 }
						 }

					 }

					 long now = System.currentTimeMillis();
					 if (nextPacketSend < now) {
						 if ((!(this.highPriorityOutBuffer.isEmpty())) && (lastPacketSent + HIGH_PRIORITY_INTERVAL < now))
						 {
							 if (sendNow(this.highPriorityOutBuffer.getNext())) {
								 PPM2v2 pacoteEnviado = this.highPriorityOutBuffer.remove();
								 //								log(Level.INFO, "Sending packet " + pacoteEnviado.toString());
							 }
							 lastPacketSent = now;
						 } else if ((!(this.getsOutBuffer.isEmpty())) && (lastPacketSent + LOW_PRIORITY_INTERVAL < now)) {
							 PPM2v2 pacote = this.getsOutBuffer.getNext();
							 if (sendNow(pacote)) {
								 this.getsOutBuffer.remove();
							 }
							 lastPacketSent = now;
						 } else if ((this.highPriorityOutBuffer.isEmpty()) && 
								 (this.getsOutBuffer.isEmpty()) && 
								 (lastPacketSent + REGEN_INTERVAL < now))
						 {
							 scheduleRegen();
						 }

					 }

					 if (this.setAsNotResponding < now) {
						 SerialNumber serial;
						 for (Iterator<SerialNumber> localIterator = this.nodeDb.getAllNe().keySet().iterator(); localIterator.hasNext(); ) { 
							 serial = (SerialNumber)localIterator.next();
							 setAsNotResponding(serial);
						 }
						 for (Iterator<SerialNumber> localIterator = this.addedE.keySet().iterator(); localIterator.hasNext(); ) { 
							 serial = (SerialNumber)localIterator.next();
							 setAsNotResponding(serial);
						 }
						 this.setAsNotResponding = (now + NO_CONNECTION_TIMEOUT);
					 }

					 Thread.sleep(100L);
				 } catch (Exception e) {
					 log("Exception running collector: #run().", e);
				 }
			 }

			 stopReaderWriters();
		 } finally {
			 if (this.colectorConfig != null) {
				 this.colectorConfig.setStatus(ColectorConfig.Status.STATUS_STOP);
			 }
			 log(Level.INFO, "End");
		 }
	 }

	 private void scheduleRegen() {
		 SerialNumber serial = this.nodeDb.getNextToUpdate();
		 if (serial == null) {
			 return;
		 }
		 this.nodeDb.incGetAllSent(serial);

		 NE_Impl ne = getNE(serial);
		 if (ne == null) {
			 ne = (NE_Impl)this.addedE.get(serial);
		 }
		 if (ne == null) {
			 return;
		 }
		 if (this.nodeDb.getGetAllSent(serial) > 3) {
			 setAsNotResponding(serial);
		 }

		 if ((ne.isUp()) || (Functions.getProperty("spvj.alwayssendcompleteget", false))) {
			 sendCompleteGet(ne);
		 } else {
			 int p = serial.getPart();
			 int sn = serial.getSeq();
			 byte[] destino = new byte[5];

			 destino[0] = (byte)ne.getSupAddress();
			 destino[1] = (byte)(p >> 8 & 0xFF);
			 destino[2] = (byte)(p & 0xFF);
			 destino[3] = (byte)(sn >> 8 & 0xFF);
			 destino[4] = (byte)(sn & 0xFF);
			 sendSimpleGet(destino, ne);
		 }
	 }

	 private void initialiseReaderWriters() {
		 for (int i = 0; i < this.rw.length; ++i) {
			 if (this.rw[i] != null){
				 try {
					 Thread.sleep(2000L);
				 } catch (InterruptedException localInterruptedException) {
				 }
				 this.rw[i].start();
			 }
		 }
	 }

	 private void stopReaderWriters()
	 {
		 for (int i = 0; i < this.rw.length; ++i)
			 if (this.rw[i] != null) {
				 log(Level.INFO, "Stopping ReaderWriter " + this.rw[i].getConection());
				 this.rw[i].shutdown();
			 }
	 }

	 public boolean isAlive()
	 {
		 return this.continua;
	 }

	 private void log(Level level, String msg) {
		 StringBuilder sb = new StringBuilder();
		 sb.append("Collector ");
		 sb.append(getConection());
		 sb.append(": ");
		 sb.append(msg);
		 Log.getInstance().log(level, sb.toString());
		 sb = null;
	 }

	 private void log(String msg, Exception e) {
		 StringBuilder sb = new StringBuilder();
		 sb.append("Collector ");
		 sb.append(getConection());
		 sb.append(": ");
		 sb.append(msg);
		 Log.getInstance(1).log(Level.SEVERE, sb.toString(), e);
		 sb = null;
	 }

	 private void logResponse(String connection, int addr, String responseType, SerialNumber serial, String msg, byte origCommand, byte origPar, Exception e)
	 {
		 StringBuilder parStr = new StringBuilder(responseType).append(" ");
		 parStr.append(serial.toShortString());
		 if (msg != null) {
			 parStr.append(" ");
			 parStr.append(msg);
		 }
		 parStr.append(" [Orig Command:").append(Functions.b2i(origCommand)).append(
		 " Par:").append(Functions.b2i(origPar)).append("]");
		 log(connection, addr, parStr.toString(), e);
	 }

	 private void logTrap(String connection, int addr, byte par, SerialNumber serial, String msg, Exception e)
	 {
		 StringBuilder parStr = new StringBuilder();
		 switch (par)
		 {
		 case 0:
			 parStr.append("TRAP NEW ");
			 break;
		 case -128:
			 parStr.append("TRAP DEL ");
			 break;
		 default:
			 parStr.append("TRAP " + Functions.getHexa(par) + " ");
		 }

		 parStr.append(serial.toShortString());
		 if (msg != null) {
			 parStr.append(" ");
			 parStr.append(msg);
		 }
		 log(connection, addr, parStr.toString(), e);
	 }

	 private void log(String connection, int addr, String msg, Exception e)
	 {
		 if (e == null)
			 log(Level.INFO, 
					 "From " + connection + " addr " + addr + ": " + msg);
		 else
			 log(
					 "From " + connection + " addr " + addr + ": " + msg, e);
	 }

	 public String getConection()
	 {
		 return this.conection;
	 }

	 public String getIName() {
		 return this.rw[0].toString();
	 }

	 public void removeNE(SerialNumber serial)	 {
		 this.notResponding.remove(serial);
		 this.nodeDb.remove(serial);
		 this.addedE.remove(serial);

		 Notification notif = new Notification(2, serial);
		 sendNotification(notif);

		 for (ReaderWriter item : this.rw)
			 if (item != null) {
				 NE_Impl ne = item.getConnectionAlarmManager().getNe();
				 if ((ne != null) && (serial.equals(ne.getSerial()))) {
					 item.getConnectionAlarmManager().setNe(null);
					 return;
				 }
			 }
	 }

	 public void reSyncAlarms(SerialNumber serial) {
		 NE_Impl ne = getNE(serial);
		 if (ne == null) {
			 return;
		 }
		 if (this.notResponding.containsKey(serial)) {
			 sendNotification( AlarmFactory.createAlarm(ne, true, 50));
		 } else {
			 sendNotification( AlarmFactory.createAlarm(ne, false, 50));
			 this.nodeDb.setCreateAlarmFromGetAll(serial);
			 boolean handled = false;
			 for (Iterator<HandlerInterface<PPM2v2>> localIterator1 = handlerList.iterator(); localIterator1.hasNext(); ) { 
				 List<Notification> notifs;
				 HandlerInterface<PPM2v2> handler = localIterator1.next();
				 if (handler.canHandle(ne)) {
					 notifs = handler.getAlarmList(ne);
					 if (notifs != null) {
						 for (Notification item : notifs) {
							 sendNotification(item);
						 }
						 handled = true;
						 break;
					 }
				 }
			 }
			 if (!(handled))
			 {
				 this.nodeDb.setAsNextToUpdate(serial);
			 }
		 }
		 ReaderWriter[] rwList = this.rw; 
		 for (int i = 0; i < rwList.length; i++) { 
			 ReaderWriter item = rwList[i];
			 if (item != null)
				 item.getConnectionAlarmManager().reSyncAlarm();
		 }
	 }

	 public ColectorConfig getColectorConfig()	{
		 this.colectorConfig.setTotalElements(this.nodeDb.size());
		 this.colectorConfig.setRunning(this.continua);
		 //		boolean[] sitesOn = new boolean[16];
		 //		String addresses = "";
		 //		for (NE_Impl ne : this.nodeDb.getAllNe().values()) {
		 //			sitesOn[ne.getSupAddress()] = true;
		 //		}
		 //		for (int i = 0; i < sitesOn.length; ++i) {
		 //			if (sitesOn[i] != false) {
		 //				addresses = addresses + i + " ";
		 //			}
		 //		}
		 //		this.colectorConfig.setAddresses(addresses);

		 TreeMap<String, List<State<Exception>>> ns = new TreeMap<String, List<State<Exception>>>();
		 for (ReaderWriter item : this.rw) {
			 if (item != null) {
				 ns.put(item.getConection(), ReaderWriter.connectionStatePool.get(item.getConection()).getReport());
			 }
		 }
		 this.colectorConfig.setNetworkState(ns);

		 return this.colectorConfig;
	 }

	 public void setColectorConfig(ColectorConfig c) {
		 this.colectorConfig = c;
	 }

	 public void unlockSupervisor(int part, int address) {
		 Command cmd = new Command(new SerialNumber(part, 0), 46);
		 byte[] destino = new byte[5];
		 destino[0] = (byte)address;
		 destino[1] = (byte)(part >> 8 & 0xFF);
		 destino[2] = (byte)(part & 0xFF);
		 destino[3] = 0;
		 destino[4] = 0;
		 try {
			 PPM2v2 sendPacket = new PPM2v2(PPM2v2.CMD_SET, (byte)cmd.getCommandCode(), null, destino, cmd.getParameters());
			 log(Level.FINER, "Send Command Package: " + sendPacket.toString());
			 if ((this.rw[0] != null) && (this.rw[0].isConnected())) {
				 this.rw[0].write(sendPacket.getRawBytes());
				 CommunicationMonitor.getInstance().notifyPacket(this.rw[0].getConection(), 
						 sendPacket, PPM2v2Helper.getSerial(sendPacket.getDestiny()));
			 } else if ((this.rw[1] != null) && (this.rw[1].isConnected())) {
				 this.rw[1].write(sendPacket.getRawBytes());
				 CommunicationMonitor.getInstance().notifyPacket(this.rw[1].getConection(), 
						 sendPacket, PPM2v2Helper.getSerial(sendPacket.getDestiny()));
			 } else {
				 log(Level.WARNING, "No connection avaliable");
			 }

			 if (!(Functions.isLct))
				 return;
			 Thread.sleep(500L);
			 sendRegen();
		 }
		 catch (InterruptedException e)
		 {
			 log("Exception unlocking supervisor: #unlockSupervisor(): part: " + 
					 part + 
					 "address: " + address, 
					 e);
		 }
	 }
	 private byte setExtendedParameter(NE_Impl ne, int dataLength)  {
		 if(ne == null){
			 return -2;
		 }
		 /*if(ne instanceof Muxponder_Impl) {
            if(dataLength == 30)
                return 6;
            if(dataLength == 1)
                return 8;
            return ((byte)(dataLength != 56 ? 0 : 29));
        }*/
		 if(ne instanceof Transponder_Impl) {
			 if(dataLength == 15)
				 return 0;
			 /*if(ne instanceof TrpCWDMRate_Impl)
                return 1;
            if(ne instanceof TrpDWDM25Otn_Impl)  {
                if(dataLength == 34)
                    return 5;
                if(dataLength == 30)
                    return 6;
                if(dataLength == 1)
                    return 8;
            }*/ else if(ne instanceof T100D_GT_Impl)  {
            	if(dataLength == 50)
            		return 12;
            	if(dataLength == 30)
            		return 6;
            	if(dataLength == 1)
            		return 8;
            } /*else if(ne instanceof T100D_GC_Impl) {
                if(dataLength == 56)
                    return 17;
                if(dataLength == 30)
                    return 13;
            }*/
		 } else  {
			 /*if(ne instanceof RateMeter_Impl)
                return ((byte)(dataLength != 15 ? 1 : 0));*/
			 if(ne instanceof PBAmp_Impl)
				 return 0;
		 }

		 return 0;
	 }



	 private void analyzeNewElement(PPM2v2 pacote) {
		 NE_Impl ne = createElement(pacote);

		 if (ne != null)
			 ne.setFullSync(true);
	 }

	 private boolean sendNow(PPM2v2 pacote)	{

		 int i;
		 int step;
		 if (this.debug) {
			 log(Level.FINER, "Trying to send packet to" + 
					 PPM2v2Helper.getSerial(pacote.getDestiny()));
		 }

		 SerialNumber serial = PPM2v2Helper.getSerial(pacote.getDestiny());
		 NE_Impl ne = this.nodeDb.getNe(serial);

		 int sendCount = 0;

		 if (this.rwWriteDirection) {
			 step = 1;
			 i = 0;
		 } else {
			 step = -1;
			 i = this.rw.length - 1;
		 }
		 this.rwWriteDirection = (!(this.rwWriteDirection));
		 for (; (i >= 0) && (i < this.rw.length); i += step) {
			 if ((this.rw[i] != null) && this.rw[i].isConnected() && this.rw[i].write(pacote.getRawBytes())) {
				 CommunicationMonitor.getInstance().notifyPacket(this.rw[i].getConection(),	pacote, PPM2v2Helper.getSerial(pacote.getDestiny()));
				 ++sendCount;
			 }

		 }

		 return (sendCount > 0);
	 }

	 @Deprecated
	 public boolean addSupervisor(int addr, String ip, int port)
	 {
		 return false;
	 }

	 @Deprecated
	 public void setSupIp(Integer site, String ip) {
	 }

	 private void setAsNotResponding(SerialNumber serial) {
		 NE_Impl ne = getNE(serial);
		 if (!this.notResponding.containsKey(serial) && !(ne instanceof Unmanaged)) {
			 this.notResponding.put(serial, serial);
			 if (ne != null) {
				 ne.setIsUp(false);
				 createAlarm(serial, true, 50);
			 }
		 }
	 }

	 private void setAsResponding(SerialNumber serial)
	 {
		 this.nodeDb.clearGetAllSent(serial);
		 this.notResponding.remove(serial);

		 NE_Impl ne = getNE(serial);
		 if (ne != null) {
			 ne.setIsUp(true);
			 createAlarm(serial, false, 50);
		 }
	 }

	 public void update(SerialNumber serial)
	 {
		 NE_Impl ne = getNE(serial);
		 if (ne != null) {
			 for (HandlerInterface<PPM2v2> handler : handlerList) {
				 if ((handler.canHandle(ne)) && 
						 (handler.prepareFullUpdate(ne))) {
					 break;
				 }
			 }

			 reSyncAlarms(serial);
			 this.nodeDb.setAsNextToUpdate(serial);
		 }
	 }

	 private class ConnectionAlarmManager extends AbstractConnectionAlarmManager
	 {
		 public boolean setConnectionUp() {
			 boolean result = super.setConnectionUp();
			 onConnectionStateChange(this.connectionName, this.connectionUp);
			 return result;
		 }

		 public boolean setConnectionDown(Exception e) {
			 boolean result = super.setConnectionDown(e);
			 onConnectionStateChange(this.connectionName, this.connectionUp);
			 return result;
		 }

		 public void sendAlarm(NE_Impl ne, String ip, int alarmType, boolean isNew, String detail){
			 if (detail != null) {
				 detail = "NC" + detail + ";DC" + detail;
			 }
			 createAlarm(ne, ip, alarmType, null, isNew, detail);
		 }
	 }
 }