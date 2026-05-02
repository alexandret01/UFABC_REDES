package br.com.padtec.v3.server.protocols.ppm3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import br.com.padtec.v3.data.Alarm;
import br.com.padtec.v3.data.ColectorConfig;
import br.com.padtec.v3.data.Command;
import br.com.padtec.v3.data.Data4;
import br.com.padtec.v3.data.GenericExtendedAlarm;
import br.com.padtec.v3.data.Notification;
import br.com.padtec.v3.data.NotificationListener;
import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.impl.NE_Impl;
import br.com.padtec.v3.data.impl.SPVL4_Impl;
import br.com.padtec.v3.data.impl.SupSPVJ_Impl;
import br.com.padtec.v3.data.impl.Supervisor_Impl;
import br.com.padtec.v3.data.ne.NE;
import br.com.padtec.v3.data.ne.Ne16Bit;
import br.com.padtec.v3.data.ne.SupSPVL;
import br.com.padtec.v3.data.ne.Unknown;
import br.com.padtec.v3.data.ne.Unmanaged;
import br.com.padtec.v3.server.Colector;
import br.com.padtec.v3.server.protocols.codegenerator.Generator;
import br.com.padtec.v3.server.protocols.ppm3.StateController.SiteState;
import br.com.padtec.v3.server.protocols.ppm3.handler.HandlerHelper;
import br.com.padtec.v3.server.protocols.ppm3.handler.HandlerInterface;
import br.com.padtec.v3.server.protocols.ppm3.handler.HandlerInterfaceFactory;
import br.com.padtec.v3.server.protocols.ppm3.packet.EnumHistoryType;
import br.com.padtec.v3.server.protocols.ppm3.packet.InvalidValueException;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3Ack;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3Error;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3Factory;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3Get;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3HistoryGet;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3HistoryResponse;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3Payload;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3Response;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3Set;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3Trap;
import br.com.padtec.v3.server.protocols.ppm3.packet.TLV;
import br.com.padtec.v3.server.protocols.ppm3.packet.TimeTLV;
import br.com.padtec.v3.server.protocols.ppm3.requestcontrol.RequestControl;
import br.com.padtec.v3.server.protocols.ppm3.requestcontrol.RequestEventHandlerHistory;
import br.com.padtec.v3.server.protocols.ppm3.requestcontrol.RequestEventHandler_Impl;
import br.com.padtec.v3.server.protocols.util.BoardManager;
import br.com.padtec.v3.server.protocols.util.io.AbstractConnectionAlarmManager;
import br.com.padtec.v3.server.protocols.util.io.ConnectionConfig;
import br.com.padtec.v3.server.protocols.util.io.IoSystem;
import br.com.padtec.v3.server.protocols.util.io.SerialConnectionConfig;
import br.com.padtec.v3.server.protocols.util.io.TcpConnectionConfig;
import br.com.padtec.v3.server.protocols.util.io.readerwriter.AbstractReaderWriter;
import br.com.padtec.v3.server.protocols.util.io.readerwriter.TcpIpEventHandlerImpl;
import br.com.padtec.v3.server.protocols.util.io.readerwriter.TcpIpReaderWriter;
import br.com.padtec.v3.server.protocols.util.timeout.SiteResponseMonitor;
import br.com.padtec.v3.server.protocols.util.timeout.SiteTimeoutController;
import br.ufabc.equipment.Supervisor;
import br.com.padtec.v3.util.Functions;
import br.com.padtec.v3.util.PartNumber;
import br.com.padtec.v3.util.StateHistory.State;
import br.com.padtec.v3.util.log.Log;
import br.com.padtec.v3.util.math.Average;
import br.com.padtec.v3.util.math.Counter;
import br.com.padtec.v3.util.math.Ratio;
import br.ufabc.controlplane.metropad.Servidor;



public class PPM3Collector implements Colector {
	static final long REGEN_INTERVAL = Functions.getProperty("polling", 3000);

	private static final SerialNumber BROADCAST_SERIAL = new SerialNumber(0, 0);

	private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
	private boolean shutdown;
	private boolean alive;
	private ColectorConfig collectorConfig;
	private final TreeMap<String, ConnectionConfig> connectionList = new TreeMap<String, ConnectionConfig>();

	private final Map<String, AbstractReaderWriter> rwList = new TreeMap<String, AbstractReaderWriter>();
	private Supervisor supervisor;
	private final Set<String> neverConnectedIpList = Collections.synchronizedSet(new TreeSet<String>());
	private final SiteTimeoutController siteTimeout;
	private List<NotificationListener> listener = new ArrayList<NotificationListener>(1);

	private final Map<SerialNumber, Long> lastTrapDel = new ConcurrentHashMap<SerialNumber, Long>();

	private final BoardManager neDb = new BoardManager();
//	private GenericMySQL db;
	private Map<SerialNumber, Long> lastSeen = Collections.synchronizedMap(new TreeMap<SerialNumber, Long>());

	public final StateController stateController = new StateController();

	private int connected = 0;
	private RequestControl requestControl;
	private final Counter counter = new Counter(1L, 16000L, true);
	private static LinkedList<NE_Impl> nes;
	private NE_Impl lastNENext;

	private long connectionLost = 9223372036854775807L;

	Map<SerialNumber, Integer> produtos;
	
	boolean supervisorCreated = false;
	
	Map<Long, PPM3> requestPackets;

	public PPM3Collector()	{
		this.siteTimeout = new SiteTimeoutController();
		this.siteTimeout.setHandler(new Ppm3SiteTimeoutHandler());
		produtos =  new HashMap<SerialNumber, Integer>();
		requestPackets = new LinkedHashMap<Long, PPM3>();
		nes= new LinkedList<NE_Impl>();
		
	}
	
	public PPM3Collector(Supervisor supervisor)	{
		 		 this();
				 this.supervisor = supervisor;
			}

	public void addProdutos(SerialNumber serial, int site){
		produtos.put(serial, site);

	}

	public void setProdutos(Map<SerialNumber, Integer> produtos){
		this.produtos = produtos;
	}

	public BoardManager getNeDb(){
		return this.neDb;
	}
	public void addCommand(Command cmd) {
		try {
			SerialNumber serial = cmd.getSerialNumber();

			NE_Impl ne = this.neDb.getNe(serial);
			if (ne == null) {
				log(Level.WARNING, "Tried to send a telecommand to a non existing NE");
				return;
			}

			PPM3 sendPacket = command2Ppm3(cmd, ne);

			//      boolean single = ne instanceof OpticalProtection;
			boolean single = false;

			enqueue(sendPacket, single, true);

			if (!(ne instanceof Unmanaged)) {
				update(serial);
			}
		} catch (Exception e) {
			log("Fail in addCommand", e);
		}
	}

	public void addMultipleCommand(List<Command> cmdList, NE ne){
		try	{
			while (cmdList.size() != 0) {
				PPM3 sendPacket = command2Ppm3(cmdList, ne);
				for (int i = 0; i < sendPacket.getTLVCount(); ++i) {
					cmdList.remove(0);
				}

				//        boolean single = ne instanceof OpticalProtection;
				boolean single = false;

				enqueue(sendPacket, single, true);
			}

			if (!(ne instanceof Unmanaged)) {
				update(ne.getSerial());
			}
		} catch (Exception e) {
			log("Fail sending multiple commands", e);
		}
	}

	public static PPM3 command2Ppm3(Command cmd, NE ne)	throws InvalidValueException {
		byte[] type;
		byte[] value;
		byte command = Functions.i2b(cmd.getCommandCode());
		if (!(ne instanceof Ne16Bit)) {
			if (command != -1)
			{
				type = new byte[] { -128, command };
				value = EMPTY_BYTE_ARRAY;
			}
			else {
				type = new byte[] { -128, cmd.getParameters()[0] };

				value = Functions.getSubarray(cmd.getParameters(), 1, 
						cmd.getParameters().length - 1);
			}
		} else {
			type = new byte[] { cmd.getParameters()[0], cmd.getParameters()[1] };
			if (command != -1)
			{
				value = EMPTY_BYTE_ARRAY;
			}
			else {
				value = Functions.getSubarray(cmd.getParameters(), 2, 
						cmd.getParameters().length - 2);
			}
		}
		PPM3 sendPacket = null;
		if ((ne instanceof SPVL4_Impl) && (command == 38)) {
			sendPacket = PPM3Factory.getGetPPM3(Functions.i2b(ne.getSupAddress()), 
					ne.getSerial());

			TLV tlv = new TLV();
			tlv.setType(38);
			sendPacket.addTLV(tlv);
		}
		else
		{
			sendPacket = PPM3Factory.getSetPPM3(Functions.i2b(ne.getSupAddress()), 
					ne.getSerial());
			sendPacket.addTLV(new TLV(type, value));
		}
		return sendPacket;
	}

	public static PPM3 command2Ppm3(List<Command> cmdList, NE ne) throws InvalidValueException {
		PPM3 sendPacket = PPM3Factory.getSetPPM3(Functions.i2b(ne.getSupAddress()), ne.getSerial());

		for (Command cmd : cmdList) {
			if (ne != null) {
				byte[] type;
				byte[] value;
				byte command = Functions.i2b(cmd.getCommandCode());
				if (!(ne instanceof Ne16Bit)) {
					if (command != -1)
					{
						type = new byte[] { -128, command };
						value = EMPTY_BYTE_ARRAY;
					}
					else {
						type = new byte[] { -128, cmd.getParameters()[0] };

						value = Functions.getSubarray(cmd.getParameters(), 1, 
								cmd.getParameters().length - 1);
					}
				} else {
					type = new byte[] { cmd.getParameters()[0], cmd.getParameters()[1] };
					if (command != -1)
					{
						value = EMPTY_BYTE_ARRAY;
					}
					else {
						value = Functions.getSubarray(cmd.getParameters(), 2, 
								cmd.getParameters().length - 2);
					}
				}
				if ((ne instanceof SPVL4_Impl) && (command == 38))
				{
					TLV tlv = new TLV();
					tlv.setType(38);
					sendPacket.addTLV(tlv);
				} else {
					sendPacket.addTLV(new TLV(type, value));
				}
			}
		}
		return sendPacket;
	}

	public void addNotificationListener(NotificationListener list)
	{
		this.listener.add(list);
	}

	@Deprecated
	public boolean addSupervisor(int addr, String ip, int port)
	{
		return true;
	}

	private void analyzeError(PPM3 ppm3, PPM3Error pacote, String connection) {
		boolean todosTlvsTratados = true;
		for (int i = 0; i < pacote.getTLVCount(); ++i) {
			TLV tlv = pacote.getTLV(i);
			if (tlv.getTypeAsInt() == 3) {
				SerialNumber serial = pacote.getSerial();
				NE_Impl ne = this.neDb.getNe(serial);
				if ((ne != null) && (ne.isUp())) {
					log(Level.WARNING, "Error 3 " + ppm3);
				}
				setAsNotResponding(pacote.getSerial(), 
						Math.min(pacote.getTimestamp(), 
								System.currentTimeMillis()));
			} else {
				todosTlvsTratados = false;
			}
		}
		if (!(todosTlvsTratados))
			log(Level.WARNING, "PPM3 Error: " + ppm3);
	}

	public void analyzePacket(PPM3 pacote, String connection)
	{
		if (pacote.getPayload() instanceof PPM3Ack) {
			PPM3Ack packet = (PPM3Ack)pacote.getPayload();
			analyzeAck(pacote, packet, connection);
		} else if (pacote.getPayload() instanceof PPM3Response) {
			PPM3Response packet = (PPM3Response)pacote.getPayload();
			analyzeResponse(pacote, packet, connection, false);
			SerialNumber serial = packet.getSerial();
			this.lastSeen.put(serial, new Long(System.currentTimeMillis()));
		} else if (pacote.getPayload() instanceof PPM3Trap) {
			PPM3Trap packet = (PPM3Trap)pacote.getPayload();
			this.lastSeen.put(packet.getSerial(), new Long(System.currentTimeMillis()));
			analyzeTrap(pacote, packet, connection, null);
		} else if (pacote.getPayload() instanceof PPM3Error) {
			PPM3Error packet = (PPM3Error)pacote.getPayload();
			analyzeError(pacote, packet, connection); } 
		else /*if (pacote.getPayload() instanceof PPM3HistoryResponse){
			PPM3 request = requestPackets.get(pacote.getId());
				if(request.getPayload() instanceof PPM3HistoryGet){
					PPM3HistoryGet getPayload = (PPM3HistoryGet)request.getPayload();
					PPM3HistoryResponse responsePayload = (PPM3HistoryResponse)pacote.getPayload();
					CommunicationMonitor.getInstance().notifyHistoryResponse(connection, getPayload, pacote);
					if (getPayload.getHistoryType() == responsePayload.getHistoryType()){
						onReceiveHistory(request, pacote, connection);
						return;
					}
				}
	    	  
	    	  log(Level.WARNING, "PPM3: HistoryGet [" + request.toString() + 
	    			  "] received a response [" + pacote.toString() + "]");
		}else{*/
				if (!(pacote.getPayload() instanceof PPM3HistoryResponse)) {
					log(Level.WARNING, "Pacote não tratado " + connection + ":" + 
						pacote.toString());
//				}
		}
	}

	private void analyzeResponse(PPM3 ppm3, PPM3Response packet, String connection, boolean isResponseFromHistory)	{
		NE_Impl ne = this.neDb.getNe(packet.getSerial());
		if (ne == null) {
			return;
		}

		ne.update();
		if (!(isResponseFromHistory)) {
			setAsResponding(ne.getSerial(), packet.getTimestamp());
		}

		HandlerInterface<PPM3> handler = HandlerInterfaceFactory.getHandler(ne);
		if (handler != null) {
			try {
				List<Notification> alarmList = new LinkedList<Notification>();
				if (handler.onReceiveResponse(ne, ppm3, alarmList)) {
					sendNotification(alarmList);

					ne.setFullSync(true);
				}

				alarmList = null;
				if (ne instanceof SPVL4_Impl) {
					SPVL4_Impl spvl = (SPVL4_Impl)ne;
					for (AbstractReaderWriter item : this.rwList.values())
						if ((item != null) && (item.getConnection().equals(spvl.getIP()))) {
							item.getConnectionAlarmManager().setNe(spvl);
//							return;
						}
				}
			}
			catch (RuntimeException e) {
				log("Fail in analyzeResponse:" + packet.toString(), e);
			}
		}
	}

	private void analyzeAck(PPM3 pacote, PPM3Ack packet, String connection)
	{
		logReceivedPacket(connection, pacote.getSource(), "ACK " + 
				packet.getSerial().toShortString());
		int count = packet.getTLVCount();
		for (int i = 0; i < count; ++i) {
			onCommandAckReceived(packet.getTimestamp(), packet.getSerial(), 
					packet.getTLV(i).getType(), packet.getTLV(i).getValue());

			if ((Functions.isLct) && 
					((int)Functions.b2l(packet.getTLV(i).getType(), 0, 2) == 32787)) 
				sendRegen();
		}
	}

	private void analyzeTrap(PPM3 ppm3, PPM3Trap pacote, String connection, EnumHistoryType historico){
		if (ppm3.getSource() == 254) {
			log(Level.WARNING, 
			"New Supervisor. You´ll need to configure it using GL.");
		}

		List<TimeTLV> eventList = pacote.getEvents();
		for (Iterator<TimeTLV> localIterator1 = eventList.iterator(); localIterator1.hasNext(); ) { 
			NE_Impl ne;
			TimeTLV event = (TimeTLV)localIterator1.next();
			boolean isStart = (event.getTypeAsInt() & 0x8000) == 0;
			int trapId = event.getTypeAsInt() & 0x7FFF;
			switch (trapId)	{
			case 0:
				if (isStart) {
					Long lastTrapDelTimeStamp = (Long)this.lastTrapDel.get(pacote.getSerial());

					if (((lastTrapDelTimeStamp == null) || 
							(lastTrapDelTimeStamp.longValue() <	event.getTimestamp())) && 
							(Functions.isLct || ppm3.getSource() != 254)){

						if (historico == null) {
							byte[] data = event.getValue();
							logReceivedPacket(connection, ppm3.getSource(), 
									"TRAP NEW " + pacote.getSerial().toShortString() +	" slot " + 
									Generator.getSlot(data, (data.length == 55) ? 21 : 22));
						}

						ne = getNE(pacote.getSerial());
						if (ne == null) {
							//            if (!(Functions.isLct)) {
							//              NEMap siteMap;
							//              if ((siteMap = Server.getMapServer().getNEMap(pacote.getSerial())) != null)
							//              {
							//                try
							//                {
							//                  log(Level.WARNING, "Board in wrong location: " + 
							//                    pacote.getSerial().toShortString() + " registered in " + 
							//                    siteMap.getParentMap().getName() + ">" + 
							//                    siteMap.getName() + " but located in Site " + 
							//                    ppm3.getSource());
							//                } catch (RemoteException e) {
							//                  log(Level.WARNING, "Board in wrong location: " + 
							//                    pacote.getSerial().toShortString() + 
							//                    " registered in unknown location but located in Site " + 
							//                    ppm3.getSource());
							//                }
							//
							//              }
							//
							//            }
							//            else
							//            {
							boolean trapNewEnabled = true;
							for (Supervisor_Impl sup : this.neDb.getSupervisor(ppm3.getSource())) {
								if ((sup instanceof SupSPVL) && (sup.isUp()) && (((SupSPVL)sup).isBoardDiscoveryEnabled())) {
									trapNewEnabled = true;
//									supervisorCreated = true;
									break;
								}
								trapNewEnabled = false;
							}

							if (trapNewEnabled) {
								ne = createNe(pacote.getSerial(), ppm3.getSource(), event.getTimestamp());
								if (ne instanceof SupSPVL) {
									update(ne.getSerial());
									//updates the Supervisor's site address in boardColectorObject
									
									
									Servidor.getInstance().updateSiteColector(ne.getSupAddress(), this);
									sendHistoryRequest(EnumHistoryType.READ_TRAP_NEW_AND_DEL, ne.getSupAddress(), null, null, null);
								}

							}

						}else if (ne.getSupAddress() != ppm3.getSource()){
							log(Level.WARNING, "Board in wrong location: " + 
									ne.getSerial().toShortString() + " registered in Site " + 
									ne.getSupAddress() + " but located in Site " + 
									ppm3.getSource());
							ne = null;
						}

						if (ne != null) {
							byte[] data = event.getValue();

							if (data.length == 40) {
								ne.setVersion(Generator.getVersion(data, 23, 8));
								ne.setSlot(Generator.getSlot(data, 21));
							} else if (data.length == 55) {
								ne.setVersion(Generator.getVersion(data, 23, 16));
								ne.setSlot(Generator.getSlot(data, 21));
							} else if (data.length == 56) {
								ne.setVersion(Generator.getVersion(data, 24, 16));
								ne.setSlot(Generator.getSlot(data, 22));
								ne.setHardwareVersion(Generator.getVersion(data, 40, 16));
							} else {
								ne.setSlot(0);
							}
							if ((ne.getVersion() == null) ||(ne.getVersion().trim().length() == 0))
								ne.setVersion("1.0.");
						}

					}
				} else {
					if ((Functions.isLct) || (ppm3.getSource() != 254)) {
						setAsNotResponding(pacote.getSerial(), event.getTimestamp());
						if (historico == null) {
							logReceivedPacket(connection, ppm3.getSource(), 
									"TRAP DEL " + 
									pacote.getSerial().toShortString());
						}
					}
					ne = getNE(pacote.getSerial());
					if (ne != null) {
						this.lastTrapDel.put(ne.getSerial(), Long.valueOf(event.getTimestamp()));
					}
				}
				break;
				
			default:
				boolean historicoAlarmes = historico == EnumHistoryType.READ_ALARMS;

				if (historicoAlarmes){
					ne = PartNumber.getInstance(pacote.getSerial(), false);
				}
				else ne = this.neDb.getNe(pacote.getSerial());

				if (ne != null) {
					if (!(historicoAlarmes)) {
						setAsResponding(pacote.getSerial(), event.getTimestamp());
					}

					HandlerInterface<PPM3> handler = HandlerInterfaceFactory.getHandler(ne);

					PPM3 newPpm3 = PPM3.newPpm3();
					newPpm3.setDestination(ppm3.getDestination());
					newPpm3.setId(ppm3.getId());
					newPpm3.setSource(ppm3.getSource());

					PPM3Trap ppm3Trap = new PPM3Trap();
					ppm3Trap.setSerial(pacote.getSerial());
					ppm3Trap.addTLV(event);

					newPpm3.setPayload(ppm3Trap);
					try {
						List<PPM3> packetToSend = new LinkedList<PPM3>();
						List<Notification> alarms = new LinkedList<Notification>();
						if (handler.onReceiveTrap(ne, newPpm3, packetToSend, alarms, historicoAlarmes)) {
							if (!(historicoAlarmes)) {
								enqueue(packetToSend, false, false);
							}
							for (Notification a : alarms) {
								sendNotification(a);
							}
							if (!(historicoAlarmes)) {
								ne.setFullSync(true);
							}
						}
					}
					catch (RuntimeException e)
					{
						log("Fail in analyzeTrap", e);
					}
				} else {
					log(Level.INFO, "TRAP: " + connection + " " + 
							Functions.b2l(event.getType()) + " Unknown NE " + 
							pacote.getSerial().toShortString() + " in Addr " + 
							ppm3.getSource());
				}
			}
		}
	}

	private NE_Impl createNe(SerialNumber serial, int addr, long eventTime)	{
		NE_Impl ne = null;
		if ((serial.getPart() > 0) && (serial.getSeq() > 0)){
			ne = PartNumber.getInstance(serial, true);

			if (ne != null) {

				ne.setSupAddress(addr);
				ne.setFullSync(false);
				this.neDb.addNe(ne);
				nes.add(ne);
				sendNotification(new Notification(1, serial));
				GenericExtendedAlarm alarm = AlarmFactoryPPM3.createAlarm(ne, null, 
						57, null, true, null);
				alarm.setTimestamp(new Date(eventTime));
				sendNotification(alarm);

				HandlerInterface<PPM3> handler = HandlerInterfaceFactory.getHandler(ne);
				handler.prepareFullUpdate(ne);

				return ne;
			}
		}
		return null;
	}

	private void enqueue(Collection<PPM3> packets, boolean single, boolean highPriority)
	{
		for (PPM3 packet : packets)
			enqueue(packet, single, highPriority);
	}

	private boolean enqueue(PPM3 request, String connectionId, boolean highPriority)
	{
		if ((request.getPayload() instanceof PPM3Get) && 
				(((PPM3Get)request.getPayload()).getTLVCount() == 0))		{
			return false;
		}

		for (AbstractReaderWriter item : this.rwList.values()) {
			if (connectionId.equals(item.getConnection())) {
				if (!(item.isConnected())) break;
				request.setId(this.counter.next());
				PPM3SendQueue queue = (PPM3SendQueue)item.getSendQueue();
				queue.addPacket(request, highPriority);
				return true;
			}

		}

		return false;
	}

	private boolean enqueue(PPM3 request, boolean single, boolean highPriority)
	{
		if ((request.getPayload() instanceof PPM3Get) && 
				(((PPM3Get)request.getPayload()).getTLVCount() == 0))
		{
			return false;
		}
	
		request.setId(this.counter.next());
		if (request.getPayload() instanceof PPM3HistoryGet)
			requestPackets.put(request.getId(), request);
		
		
		if ((highPriority) || (single)) {
			if (single)  {
				PPM3SendQueue lastUpdatedRWQueue = null;

				String lastSiteUpdateConnection = this.siteTimeout.getCounter().getLastSiteUpdateConnection(request.getDestination());

				for (Iterator<AbstractReaderWriter> localIterator = this.rwList.values().iterator(); localIterator.hasNext(); ) { 
					AbstractReaderWriter item = localIterator.next();
					if ((item.isConnected()) && lastSiteUpdateConnection != null && 
							(item.getConnection().compareTo(lastSiteUpdateConnection) == 0))
					{
						lastUpdatedRWQueue = (PPM3SendQueue)item.getSendQueue();
						break;
					}
				}

				if (lastUpdatedRWQueue == null) {
					return false;
				}
				lastUpdatedRWQueue.addPacket(request, highPriority);
				return true;
			}
//		} // fechado antes do while para testes
			boolean result = false;
			Iterator<AbstractReaderWriter> localIterator = this.rwList.values().iterator(); 

			while (localIterator.hasNext()) { 
				AbstractReaderWriter item = localIterator.next();
				if (item.isConnected()) {
					PPM3SendQueue queue = (PPM3SendQueue)item.getSendQueue();
					queue.addPacket(request, highPriority);
					result = true;
				}
				 
			}
			return result;
		} //fim if ((highPriority) || (single))
		this.requestControl.send(request, 30000L, new RequestEventHandler_Impl(this,request)) ;
		return true;
	}

	public Collection<? extends NE> getAllNE(){
		return this.neDb.getNe().values();
	}

	public ColectorConfig getColectorConfig()
	{

		try
		{
			this.collectorConfig.setRunning(!(this.shutdown));
			this.collectorConfig.setTotalElements(this.lastSeen.size());

			Set<Integer> sitesOn = new TreeSet<Integer>();
			synchronized (this.lastSeen) {
				for (SerialNumber serial : this.lastSeen.keySet()) {
					NE_Impl ne = getNE(serial);
					if (ne != null) {
						sitesOn.add(Integer.valueOf(ne.getSupAddress()));
					}
				}
			}
			String addresses = "";
			for (Iterator<Integer> iterator = sitesOn.iterator(); iterator.hasNext(); ) { 
				int i = ((Integer)iterator.next()).intValue();
				addresses = addresses + i + " ";
			}
			sitesOn = null;
			//      this.collectorConfig.setAddresses(addresses);
		} catch (RuntimeException e) {
			log("Fail in getColectorConfig", e);
		}
		try {
			TreeMap<String,List<State<Exception>>> ns = new TreeMap<String, List<State<Exception>>>();
			for (AbstractReaderWriter item : this.rwList.values()) {
				if (item != null) {
					ns.put(item.getConnection(), item.getReport());
				}
			}
			this.collectorConfig.setNetworkState(ns);
		} catch (RuntimeException ns) {
			log("Fail in getColectorConfig", ns);
		}
		return this.collectorConfig;
	}

	public String getConection()
	{
		return ((String)this.connectionList.firstKey());
	}

	public String getIName()
	{
		return ((String)this.connectionList.firstKey());
	}

	public NE_Impl getNE(SerialNumber serial)
	{
		NE_Impl ne = this.neDb.getNe(serial); 
		return ne;
	}

	public boolean isAlive()
	{
		return this.alive;
	}

	public void log(Level level, String msg) {
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

	private void logReceivedPacket(String connection, int addr, String msg)
	{
		log(Level.INFO, 
				"From " + connection + " addr " + addr + ": " + msg);
	}

	private void onCommandAckReceived(long timestamp, SerialNumber serial, byte[] type, byte[] data)
	{
		NE_Impl ne = getNE(serial);
		if (ne != null) {
			Alarm notif;
			Command cmd = Ppm3Helper.ppm32Command(ne, type, data);
			if (cmd.getCommand() == -1)
			{
				log(Level.WARNING, "Unknown command received by supervisor: " + 
						serial.toShortString() + " type:" + Functions.getHexa(type) + 
						" data:" + Functions.getHexa(data));
				return;
			}

			//      if (ne instanceof SHK_Impl)
			//        notif = AlarmFactoryPPM3.createCommandAlarm(ne, cmd.getCommandName(), 
			//          ShkHelper.getContactFromCommand(cmd.getCommandCode()));
			//      else {
			notif = AlarmFactoryPPM3.createCommandAlarm(ne, cmd.getCommandName(), 0);
			//      }
			notif.setTimestamp(new Date(timestamp));
			sendNotification(notif);
		}
	}

	private void onConnectionStateChange(String connectionName, Boolean connectionUp)
	{
		try
		{
			this.connected = 0;
			for (AbstractReaderWriter item : this.rwList.values()) {
				if (item != null) {
					if (item.isConnected()) {
						this.connected += 1;
					}
				}
			}

			if (this.connected > 0) {
				this.connectionLost = 9223372036854775807L;

				this.stateController.resetHistoryRequest();
			}
			else {
				this.connectionLost = Math.min(this.connectionLost, System.currentTimeMillis());
			}

			if ((Boolean.TRUE.equals(connectionUp)) &&	((this.neverConnectedIpList.remove(connectionName))))
				sendRegen(connectionName);
		}
		catch (RuntimeException e)
		{
			log("Fail in onConnectionStateChange", e);
		}
	}

	public void onReceiveHistory(PPM3 ppm3Request, PPM3 ppm3Response, String connection) {

		PPM3HistoryGet request = (PPM3HistoryGet)ppm3Request.getPayload();
		PPM3HistoryResponse response = (PPM3HistoryResponse)ppm3Response.getPayload();

		switch (request.getHistoryType())    {
		case READ_TRAP_NEW_AND_DEL:
			for (PPM3Payload item : response.getList()) {
				if (item instanceof PPM3Trap) {
					PPM3Trap payload = (PPM3Trap)item;
					if (payload.getTLVCount() > 0) {
						payload.sort();
						PPM3 ppm3 = PPM3.newPpm3(ppm3Response.getSource(), ppm3Response.getDestination(),
								ppm3Response.getId(), item);
						analyzeTrap(ppm3, payload, connection, request.getHistoryType());
					}

				}

			}

			Map<SerialNumber, NE_Impl> nes = this.neDb.getNe(ppm3Response.getSource());

			for (Iterator<PPM3Payload> it = response.getList().iterator(); it.hasNext(); ) { 
				PPM3Payload item = it.next();
				if (item instanceof PPM3Trap) {
					PPM3Trap payload = (PPM3Trap)item;

					logReceivedPacket(connection, ppm3Response.getSource(), "TRAP_NEW_DEL " +  
							payload.getSerial().toShortString() +	" responding.");

					NE_Impl ne = (NE_Impl)nes.remove(payload.getSerial());
					if ((ne != null) && (Functions.getProperty("useTrapNewDel", true))){
						setAsResponding(payload.getSerial(), Math.max(ne.getUpdate() + 1L, 
								System.currentTimeMillis()));
					}
				}

			}

			for (Iterator<NE_Impl> it = nes.values().iterator(); it.hasNext(); ) { 
				NE_Impl ne = it.next();
				SerialNumber serial = ne.getSerial();

				logReceivedPacket(connection, ppm3Response.getSource(), "TRAP_NEW_DEL " + serial.toShortString() + 
				" not responding.");
				if (Functions.getProperty("useTrapNewDel", true)) {
					setAsNotResponding(serial, Math.min(((NE_Impl)ne).getUpdate(), System.currentTimeMillis()));
				}
			}

			break;
		case READ_ALARMS:
			for (Iterator<PPM3Payload> it = response.getList().iterator(); it.hasNext(); ) { 
				PPM3Payload item = it.next();
				if (item instanceof PPM3Trap) {
					PPM3Trap payload = (PPM3Trap)item;

					if (payload.getTLVCount() > 0) {
						payload.sort();
						StateController.BoardState state = this.stateController.getBoardState(payload.getSerial());

						state.setLastAlarmHistoryReceived(Math.max( state.getLastAlarmHistoryReceived(), 
								Ppm3Helper.getNewestTlv(payload).getTimestamp()));

						PPM3 ppm3 = PPM3.newPpm3(ppm3Response.getSource(),  ppm3Response.getDestination(), ppm3Response.getId(), item);
						analyzeTrap(ppm3, payload, connection, request.getHistoryType());
					}
				}
			}
			break;
		case READ_LAST_ALARMS:
			for (Iterator<PPM3Payload> it = response.getList().iterator(); it.hasNext(); ) { 
				PPM3Payload item = it.next();
				if (item instanceof PPM3Trap) {
					PPM3Trap payload = (PPM3Trap)item;
					StateController.BoardState state = this.stateController.getBoardState(payload.getSerial());

					if (payload.getTLVCount() > 0) {
						payload.sort();
						long newLastLastAlarmHistoryReceived = Math.max(state.getLastLastAlarmHistoryReceived(), 
								Ppm3Helper.getNewestTlv(payload).getTimestamp());
						if (newLastLastAlarmHistoryReceived != 
							state.getLastLastAlarmHistoryReceived()) {
							state.setLastLastAlarmHistoryReceived(newLastLastAlarmHistoryReceived);
							if (!(state.lastAlarmsSynchronized)) {
								List<SupSPVJ_Impl> sups = this.neDb.getSupervisor( ppm3Response.getSource());
								for (Supervisor_Impl supervisor : sups) {
									if (Functions.compareVersions(supervisor.getVersion(), "1.1.10") > 0){
										sendHistoryRequest(EnumHistoryType.READ_LAST_ALARMS,  ppm3Response.getSource(), 
												state.getLastLastAlarmHistoryReceived(), null, payload.getSerial());
										break;
									}
								}
							}

						}

						PPM3 ppm3 = PPM3.newPpm3(ppm3Response.getSource(), ppm3Response.getDestination(), ppm3Response.getId(), 
								item);
						analyzeTrap(ppm3, payload, connection, request.getHistoryType());
					} else {
						state.lastAlarmsSynchronized = true;
					}
				}
			}
			break;
		case READ_COMMANDS:
			for (Iterator<PPM3Payload> it = response.getList().iterator(); it.hasNext(); ) { 
				PPM3Payload item = it.next();
				if (item instanceof PPM3Trap) {
					PPM3Trap payload = (PPM3Trap)item;

					if (payload.getTLVCount() != 0) {

						Integer colId = this.collectorConfig.getId();
						SiteState state = this.stateController.getSiteState( (colId == null) ? 1 : colId.intValue(), 
								ppm3Response.getSource());
						state.setLastCommandHistoryReceived(Math.max(  state.getLastCommandHistoryReceived(), 
								Ppm3Helper.getNewestTlv(payload).getTimestamp()));

						for (int i = 0; i < payload.getTLVCount(); ++i) {
							TimeTLV payloadTlv = payload.getTLV(i);
							onCommandAckReceived(payloadTlv.getTimestamp(), payload.getSerial(), payloadTlv.getType(),
									payloadTlv.getValue());
						}
					}
				}
			}
			break;
		case READ_METRICS:
			
			//			if (!(Functions.isLct)) {
			//				for (PPM3Payload responsePayload : response.getList()) {
			//					if (!(responsePayload instanceof PPM3Trap)) {
			//						continue;
			//					}
			//					PPM3Trap trap = (PPM3Trap)responsePayload;
			//					long firstItem = 9223372036854775807L;
			//					long lastItem = 0L;
			//					List list = Ppm3Helper.trapToResponse(trap);
			//
			//					Object lastValue = new TreeMap();
			//
			//					for (PPM3Response payload : list) {
			//						if (payload.getTLVCount() == 0)
			//						{
			//							break;
			//						}
			//
			//						long day = 86400000L;
			//						long month = day * 30L;
			//						long lowDate = System.currentTimeMillis() - month;
			//						long highDate = System.currentTimeMillis() + day;
			//						if (payload.getTimestamp() >= lowDate) {
			//							if (payload.getTimestamp() > highDate)
			//							{
			//								continue;
			//							}
			//
			//							firstItem = Math.min(firstItem, payload.getTimestamp());
			//							lastItem = Math.max(lastItem, payload.getTimestamp());
			//
			//							StateController.BoardState state = this.stateController
			//							.getBoardState(payload.getSerial());
			//							state.setLastMetricHistoryReceived(Math.max(
			//									state.getLastMetricHistoryReceived(), payload.getTimestamp()));
			//
			//							NE_Impl ne = PartNumber.getInstance(payload.getSerial(), false);
			//
			//							NE_Impl neAtual = getNE(payload.getSerial());
			//
			//							ne.setVersion(neAtual.getVersion());
			//
			//							if ((neAtual instanceof TrpDWDM_Impl) && 
			//									(!(neAtual instanceof Transponder4b2s)))
			//							{
			//								TrpDWDM_Impl current = (TrpDWDM_Impl)neAtual;
			//								TrpDWDM_Impl trp = (TrpDWDM_Impl)ne;
			//								trp.setTableLamda(current.getTableLambda());
			//								trp.setTablePin(current.getTablePin());
			//								trp.setTablePout(current.getTablePout());
			//							}
			//							for (int i = 0; i < payload.getTLVCount(); ++i) {
			//								HandlerInterface h;
			//								TLV tlv = payload.getTLV(i);
			//
			//								Map measuresBefore = 
			//									NEPerformanceData.getPerformanceData(ne);
			//								try
			//								{
			//									PPM3Response virtualResponse = new PPM3Response(
			//											payload.getSerial(), payload.getTimestamp(), (int)Functions.b2l(
			//													tlv.getType()), tlv.getValue());
			//									PPM3 virtualPpm3 = PPM3.newPpm3(ppm3Response.getSource(), 
			//											ppm3Response.getDestination(), ppm3Response.getId(), 
			//											virtualResponse);
			//									h = HandlerInterfaceFactory.getHandler(ne);
			//									List alarmList = new ArrayList(1);
			//									h.onReceiveResponse(ne, virtualPpm3, alarmList);
			//
			//									alarmList = null;
			//								}
			//								catch (InvalidValueException e) {
			//									log("Fail in onReceiveHistory:READ_METRICS", 
			//											new RuntimeException(e));
			//								}
			//
			//								Map data = 
			//									NEPerformanceData.getPerformanceData(ne);
			//								for (Map.Entry entry : measuresBefore.entrySet()) {
			//									if (Functions.equals(data.get(entry.getKey()), entry.getValue())) {
			//										data.remove(entry.getKey());
			//									}
			//								}
			//								Date date = new Date(payload.getTimestamp());
			//								Scheduler.logPerformanceData(ne.getSerial(), true, date, data, 
			//										(Map)lastValue, Server.getScheduler(), false);
			//							}
			//						}
			//					}
			//
			//					if (firstItem != 9223372036854775807L) {
			//						long time15Min = 900000L;
			//						Scheduler.removeNullEntries(trap.getSerial(), 
			//								new Date(firstItem - 
			//										time15Min), new Date(lastItem + time15Min));
			//					}
			//				}
			//			}
		}
		if (!(response.isHasNext())) {
			//			label1650: 
			SerialNumber serial;
			StateController.BoardState state;
			switch (request.getHistoryType()){
			case READ_TRAP_NEW_AND_DEL:
				break;
			case READ_LAST_ALARMS:
				break;
			case READ_ALARMS:
				serial = request.getSerial();
				if (serial != null) {
					state = this.stateController.getBoardState(serial);
					state.requestAlarmHistory = false;

				}
				break;
			case READ_COMMANDS:
				break;
			case READ_METRICS:
				serial = request.getSerial();
				if (serial != null) {
					state = this.stateController.getBoardState(serial);
					state.requestMetricHistory = false;
				}
				break;
			}
		}
	}

	public void removeNE(SerialNumber serial){
		this.neDb.removeNe(serial);

		sendNotification(new Notification(2, serial));
	}

	public void removeNotificationListener(NotificationListener list){
		this.listener.remove(list);
	}

	public void reSyncAlarms(SerialNumber serial)	{
		NE_Impl ne = getNE(serial);
		if (ne == null) {
			return;
		}
		sendNotification( AlarmFactoryPPM3.createAlarm(ne, null, 50, null, !(ne.isUp()), null));
		sendNotification(new Notification(3, serial));
		StateController.BoardState state = this.stateController.getBoardState(serial);
		state.setLastLastAlarmHistoryReceived(0L);
		state.lastAlarmsSynchronized = false;
		this.neDb.setNext(serial);
		for (AbstractReaderWriter item : this.rwList.values())
			item.getConnectionAlarmManager().reSyncAlarm();
	}


	public void run()
	{
		List<AbstractReaderWriter> connectionStartUp = new ArrayList<AbstractReaderWriter>(2);
		try {
			this.alive = true;
			this.collectorConfig.setStatus(ColectorConfig.Status.STATUS_OK);

			//				this.db = DataBaseFactory.getColectorInstance();
			Set<Integer> sites = new TreeSet<Integer>();

			//				Iterator localIterator1 = this.db.getAllSerials(getConection()).entrySet().iterator();
			Iterator<Entry<SerialNumber, Integer>> localIterator1 = produtos.entrySet().iterator();

			while (localIterator1.hasNext()) {
				Map.Entry<SerialNumber, Integer> item = localIterator1.next();
				SerialNumber serial = item.getKey();
				Integer site = item.getValue();
				sites.add(site);
				try {
					NE_Impl ne = PartNumber.getInstance(serial);
					if (ne != null) {
						if (!(ne instanceof Unmanaged)) {
							ne.setSupAddress(site.intValue());
							ne.setFullSync(false);
							this.neDb.addNe(ne);
							sendNotification( new Notification(1 ,serial));
							//								break label213:
//							break;
						} 

					} else {
						log(Level.WARNING, "NE on database has an unknown PartNumber " + 
								serial.toShortString());
					}

					//						label213: 
					
				}
				catch (RuntimeException e) {
					log("Fail in run", e);
				}
			}

			SiteResponseMonitor siteRequestCounter = new SiteResponseMonitor(sites);
			this.siteTimeout.setCounter(siteRequestCounter);

			Iterator<Entry<String,ConnectionConfig>>  it= this.connectionList.entrySet().iterator();

			while (it.hasNext()) {
				Entry<String, ConnectionConfig>item = it.next();
				PPM3ReceiveQueue rQueue = new PPM3ReceiveQueue();
				rQueue.setSiteResponseMonitor(siteRequestCounter);

				PPM3SendQueue sQueue = new PPM3SendQueue()
				{
					protected void onHighPriorityPacketSent(String connection, PPM3 packet)
					{
						if (packet.getPayload() instanceof PPM3Set)
							log(Level.INFO, "Sending packet through " + connection + " " + 
									packet.toString());
					}

				};
				AbstractReaderWriter connection = IoSystem.getInstance().createConnection((ConnectionConfig)item.getValue(), 
						new ConnectionAlarmManager(), rQueue, sQueue);
				connectionStartUp.add(connection);

				if (connection instanceof TcpIpReaderWriter) {
					((TcpIpReaderWriter)connection).setHandler(new TcpIpEventHandlerImpl(this, connection));
					//					} else if (connection instanceof SerialReaderWriter) {
					//						((SerialReaderWriter)connection)
					//						.setHandler(new SerialReaderWriter.SerialEventHandler(connection)
					//						{
					//							private boolean dataReceived;
					//
					//							public boolean handleReadException(Exception e)
					//							{
					//								if (("Read timed out".equalsIgnoreCase(e.getMessage())) && 
					//										(this.dataReceived))
					//								{
					//									Iterator localIterator = PPM3Collector.this.neDb.getNe().values().iterator(); break label111:
					//										while (true) { NE_Impl ne = (NE_Impl)localIterator.next();
					//										if (ne instanceof SPVL4_Impl)
					//										{
					//											PPM3Collector.this.neDb.setNext(ne.getSerial());
					//											PPM3Collector.this
					//											.log(Level.INFO, "Sending keep-alive to " + 
					//													this.val$connection.getConnection());
					//											this.dataReceived = false;
					//										}
					//										if (!(localIterator.hasNext()))
					//										{
					//											label111: return true; } }
					//								}
					//								return false;
					//							}
					//
					//							public void onConnect(SerialReaderWriter writer) {
					//							}
					//
					//							public int onRead(int data) {
					//								this.dataReceived = true;
					//								return data;
					//							}
					//						});
				}

				this.rwList.put((String)item.getKey(), connection);
				this.neverConnectedIpList.add((String)item.getKey());
				try {
					Thread.sleep(3000L);
				} catch (InterruptedException e) {
					log("Fail in run", e);
				}
			}

			this.requestControl =	new RequestControl( this.rwList.values().toArray(new AbstractReaderWriter[0]) );

			for (AbstractReaderWriter item : connectionStartUp) {
				item.startup();
			}
			connectionStartUp = null;

			long nextRegen = 0L;

			long shortTimer = 0L;
			long longTimer = 0L;
			long mediumTimer = 0L;
			long shortestTimer = 0L;

			while (!(this.shutdown)) {
				AbstractReaderWriter item;
				String msg;
				long now = System.currentTimeMillis();

				PPM3 packet = null;
				try {
					for (Iterator<AbstractReaderWriter> localIterator3 = this.rwList.values().iterator(); localIterator3.hasNext(); ) { 
						item = localIterator3.next();
						PPM3ReceiveQueue queue = (PPM3ReceiveQueue)item.getReceiveQueue();
						while (!(queue.isEmpty())) {
							packet = queue.nextPacket();
							if (!(this.requestControl.onReceiveResponse(item.getConnection(), packet))) {
								analyzePacket(packet, item.getConnection());
							}
							packet = null;
						}
					}
				} catch (RuntimeException e) {
					msg = "Fail in run analyzing incoming packet";
					if (packet != null) {
						msg = msg + ": " + packet.toString();
					}
					log((String)msg, e);
				}
				try	{
					if (nextRegen < now) {
						
						nextRegen = now + REGEN_INTERVAL;
						for (Iterator<AbstractReaderWriter> itA = this.rwList.values().iterator(); itA.hasNext(); ) { 
							AbstractReaderWriter connection = itA.next();
							if (connection.isConnected()) {
								PPM3SendQueue queue = (PPM3SendQueue)connection.getSendQueue();
								if (queue.size() <= 0) {
									scheduleRegen();
									break;
								}
							}
						}
					}
				} catch (RuntimeException e) {
					log("Fail in run scheduling regenerate packets", e);
				}
				try {
					if (shortestTimer < now) {
						shortestTimer = now + 5000L;
						this.requestControl.check();
						this.siteTimeout.check();
					}
				} catch (RuntimeException e) {
					log("Fail in run", e);
				}
				try {
					if (mediumTimer < now) {
						mediumTimer = now + 30000L;
						if (this.connectionLost < now - 90000L) {
							long timeout = this.connectionLost;
							this.connectionLost = 9223372036854775807L;

							for (Iterator<Integer> localIterator4 = this.neDb.getSites().iterator(); localIterator4.hasNext(); ) { 
								int site = localIterator4.next().intValue();
								this.siteTimeout.getHandler().onSiteTimeout(site, timeout);
							}
						}
					}
				} catch (RuntimeException timeout) {
					log("Fail in run", timeout);
				}
				try {
					if (shortTimer < now) {
						shortTimer = now + 120000L;
						if (this.connected > 0)	{
							sites = this.neDb.getSites();
							sites.remove(Integer.valueOf(0));
							for (Iterator<Integer> iterator5 = sites.iterator(); iterator5.hasNext(); ) { 
								Integer site = iterator5.next();
								Integer colId = getColectorConfig().getId();
								StateController.SiteState state = this.stateController.getSiteState(
										(colId == null) ? 1 : colId.intValue(), site.intValue());

								sendHistoryRequest(EnumHistoryType.READ_COMMANDS, site.intValue(), 
										state.getLastCommandHistoryReceived() + 1L, null, null);
							}
						}
					}
				} catch (RuntimeException site) {
					log("Fail in run getting commands", site);
				}
				try
				{
					if (longTimer < now) {
						longTimer = now + 900000L;

						this.stateController.resetHistoryRequest();
					}
				} catch (RuntimeException site) {
					log("Fail in run preparing history regen", site);
				}
				try
				{
					Thread.sleep(200L);
				} catch (Exception e) {
					log("Fail in run", e);
				}

			}

			for (Entry<String,AbstractReaderWriter> item : this.rwList.entrySet()) {
				log(Level.INFO, "Stopping ReaderWriter " + item.getValue().getConnection());
				item.getValue().shutdown();
			}

			log(Level.INFO, "End");
		} catch (RuntimeException e) {
			log("Fail in run", e);
		} finally {
			this.alive = false;
			this.collectorConfig.setStatus(ColectorConfig.Status.STATUS_STOP);
		}
	}

	private void scheduleRegen() {
		NE_Impl ne = null;
//		SerialNumber nextToUpdate = null;

//		for (int i = 0; i < 100; ++i) {
//			nextToUpdate = this.neDb.next();
//			if (nextToUpdate != null) {
//				ne = getNE(nextToUpdate);
//				if (ne != null) {
//					break;
//				}
//			}
//		}
		
		//bloco if-else de teste
		if (lastNENext == null){
			for (NE_Impl n : nes){
				if (n instanceof SPVL4_Impl){
					ne = n;
					lastNENext = n;
					break;
				}
			}
		} else {
			NE_Impl n = nes.removeFirst();
			ne = n;
			lastNENext = n;
			nes.add(n);
		}
		
//		if ((ne == null) || (nextToUpdate == null)) {
//			return;
//		}

		if (ne == null)
			return;
		
		SerialNumber serial = ne.getSerial();
		
		if (!(ne instanceof Unknown)) {
			sendHistoryRequest(EnumHistoryType.READ_LAST_ALARMS, ne.getSupAddress(), 
					this.stateController.getBoardState(serial).getLastLastAlarmHistoryReceived(), null, serial);
			if ((ne.isUp()) && (((!(Functions.isLct)) || (Functions.getProperty("getMetrics", false)))))
			{
				StateController.BoardState state = this.stateController
				.getBoardState(serial);
				if (state.requestAlarmHistory) {
					sendHistoryRequest(EnumHistoryType.READ_ALARMS, ne.getSupAddress(), state.getLastAlarmHistoryReceived() + 1L, null, serial);
				}
				if (state.requestMetricHistory) {
					sendHistoryRequest(EnumHistoryType.READ_METRICS, ne.getSupAddress(), state.getLastMetricHistoryReceived() + 1L, null, serial);
				}
			}
		}

		sendCommonGet(ne);
	}

	private void sendCommonGet(NE_Impl ne)
	{
		HandlerInterface<PPM3> handler = HandlerInterfaceFactory.getHandler(ne);
		if (handler != null) {
			List<PPM3> packets = handler.getUpdatePacketList(ne);
			if ((packets != null) && (!(packets.isEmpty()))) {
				if (ne.isUp()) {
					enqueue(packets, false, false);
				}
				else
				{
					enqueue((PPM3)packets.get(0), false, false);
				}
				return;
			}
		}

		PPM3 sendPacket = PPM3Factory.getGetPPM3(ne.getSupAddress(), ne.getSerial());
		HandlerHelper.addTlv(sendPacket, 0);
		enqueue(sendPacket, false, false);
	}

	public void sendHistoryRequest(EnumHistoryType historyType, int neAddress, Long startDate, Long endDate, SerialNumber serial)	{
		if (this.connected != 0) {

			if (serial == null) {
				serial = new SerialNumber(0, 0);
			}
			
			PPM3HistoryGet get = new PPM3HistoryGet(historyType, serial, startDate, endDate);
			PPM3 request = PPM3.newPpm3(0, neAddress, this.counter.next(), get);
			this.requestControl.send(request, 30000L, new RequestEventHandlerHistory(this, request));
		}
	}

	private void sendNotification(Collection<? extends Notification> list)
	{
		if (list == null) {
			return;
		}
		for (Notification item : list)
			sendNotification(item);
	}

	private void sendNotification(Notification notification)
	{
		if (notification == null) {
			return;
		}
		for (NotificationListener l : this.listener)
			l.notify(notification);
	}

	public void sendRegen()
	{
		PPM3 regen = PPM3Factory.getGetPPM3(0, BROADCAST_SERIAL);
		regen.addTLV(new TLV(PPM3Command.REGEN.code()));
		enqueue(regen, false, true);

		Set<Integer> sites = this.neDb.getSites();
		for (Integer site : sites) {
			sendHistoryRequest(EnumHistoryType.READ_TRAP_NEW_AND_DEL,site.intValue(), null, null, null);
		}

		log(Level.INFO, "Sending regenerate traps for " + getConection());
	}

	private void sendRegen(String ip) {
		PPM3 regen = PPM3Factory.getGetPPM3(0, BROADCAST_SERIAL);
		regen.addTLV(new TLV(PPM3Command.REGEN.code()));
		enqueue(regen, ip, true);

		Set<Integer> sites = this.neDb.getSites();
		for (Integer site : sites) {
			sendHistoryRequest(EnumHistoryType.READ_TRAP_NEW_AND_DEL, site.intValue(), null, null, null);
		}

		log(Level.INFO, "Sending regenerate traps for " + getConection());
	}

	private void setAsNotResponding(SerialNumber serial, long time) {
		NE_Impl ne = this.neDb.getNe(serial);
		if (ne != null) {

			if (!(ne instanceof Unmanaged)){
				List<Notification> alarms = new ArrayList<Notification>(2);
				alarms.add(	AlarmFactoryPPM3.createAlarm(ne, null, 50, null, true, null));
				HandlerHelper.setAlarmTimestamp(alarms, time);
				if (ne.isUp()) {
					ne.setIsUp(false);
					alarms.add(new Notification(3, serial));
				}
				sendNotification(alarms);
			}
		}
	}

	private void setAsResponding(SerialNumber serial, long time){
		NE_Impl ne = this.neDb.getNe(serial);
		if (ne != null) {
			List<Notification> alarms = new ArrayList<Notification>(2);
			Alarm alarm = AlarmFactoryPPM3.createAlarm(ne, null,	50, null, false, null);
			alarm.setAlarmName("Equipamento respondendo");
			alarms.add( alarm );
			HandlerHelper.setAlarmTimestamp(alarms, time);
			ne.update();
			if (!(ne.isUp())) {
				ne.setIsUp(true);
				alarms.add(new Notification(3, serial));

				HandlerInterface<PPM3> handler = HandlerInterfaceFactory.getHandler(ne);
				if (handler != null) {
					handler.prepareFullUpdate(ne);
				}
			}
			sendNotification(alarms);
		}
	}

	@Deprecated
	public boolean setBackUpConnection(String ip, int port)
	{
		return true;
	}

	public void setColectorConfig(ColectorConfig c)
	{
		this.collectorConfig = c;
//		Iterator ipI = c.getIP().iterator();
//		Iterator portI = c.getPort().iterator();
		
		this.connectionList.clear();
//		while (ipI.hasNext()) {
//			String ip = (String)ipI.next();
//			Integer port = (Integer)portI.next();
			this.connectionList.put(c.getIp(), new TcpConnectionConfig(c.getIp(), c.getPort()));
//		}
	}

	@Deprecated
	public boolean setConnection(String ip, int port)
	{
		return true;
	}

	@Deprecated
	public boolean setConnection(String porta, int initTimeout, int baudRate) {
		this.connectionList.clear();
		this.connectionList.put(porta, 
				new SerialConnectionConfig(porta, initTimeout, 
						baudRate));
		return true;
	}

	@Deprecated
	public void setSupIp(Integer integer, String string){
	}

	public void shutdown()
	{
		this.shutdown = true;
	}

	public void unlockSupervisor(int part, int address)	{
		PPM3 packet = PPM3Factory.getSetPPM3(address, new SerialNumber(part, 0));
		TLV tlv = new TLV(32787);
		packet.addTLV(tlv);
		enqueue(packet, false, true);
	}

	public void update(SerialNumber serial)
	{
		this.neDb.setNext(serial);
		NE_Impl board = getNE(serial);
		HandlerInterface<PPM3> handler = HandlerInterfaceFactory.getHandler(board);
		handler.prepareFullUpdate(board);
	}

	public Date sendClockToSupervisor()
	{
		try
		{
			PPM3 packet = PPM3Factory.getSetPPM3(0, new SerialNumber(1316, 0));
			TLV tlv = new TLV(32775);

			byte[] time = new byte[8];
			long t = System.currentTimeMillis();

			time[7] = (byte)(int)(t >> 0);
			time[6] = (byte)(int)(t >> 8);
			time[5] = (byte)(int)(t >> 16);
			time[4] = (byte)(int)(t >> 24);
			time[3] = (byte)(int)(t >> 32);
			time[2] = (byte)(int)(t >> 40);
			time[1] = (byte)(int)(t >> 48);
			time[0] = (byte)(int)(t >> 56);

			tlv.setValue(time);
			packet.addTLV(tlv);

			enqueue(packet, false, true);

			for (SerialNumber sn : this.neDb.getNe().keySet()) {
				this.stateController.removeBoardState(sn);
			}
			this.stateController.removeSiteState();

			return new Date(t);
		} catch (InvalidValueException e) {
			log("Fail in sendClockToSupervisor", e); }
		return null;
	}

	public void resetCommunicationsStatus() {
		this.requestControl.resetStatistics();
	}

	public List<Data4<Integer, Integer, Double, Double>> getCommunicationStatus()
	{
		List<Data4<Integer, Integer, Double, Double>> result = new LinkedList<Data4<Integer,Integer,Double,Double>>();

		Set<Integer> sites = this.neDb.getSites();
		Map<Integer,Average> responseTime = this.requestControl.getAverageResponseTime();
		Map<Integer,Ratio> failureRatio = this.requestControl.getFailureRatio();
		for (Integer site : sites) {
			Integer boards = Integer.valueOf(this.neDb.getNeCount(site.intValue()));
			Average response = (Average)responseTime.get(site);
			Ratio fail = (Ratio)failureRatio.get(site);
			result.add(	new Data4<Integer, Integer, Double, Double>(site, boards, new Double((response == null) ? (0.0D / 0.0D) : response.getAverage()), 
							new Double((fail == null) ? (0.0D / 0.0D) : fail.getRatio())));
		}
		sites = null;

		return result;
	}

	private class ConnectionAlarmManager extends AbstractConnectionAlarmManager	{
		public void sendAlarm(NE_Impl ne, String ip, int alarmType, boolean isNew, String message)	{
			if (message != null) {
				message = "NC" + message + ";DC" + message;
			}
			sendNotification(AlarmFactoryPPM3.createAlarm(ne, ip, alarmType, null, 
					isNew, message));
		}

		public boolean setConnectionDown(Exception e)	{
			if (super.setConnectionDown(e)) {
				onConnectionStateChange(connectionName, connectionUp);
				return true;
			}
			return false;
		}

		public boolean setConnectionUp()	{
			if (super.setConnectionUp()) {
				onConnectionStateChange(this.connectionName, 
						this.connectionUp);
				return true;
			}
			return false;
		}
	}

	private final class Ppm3SiteTimeoutHandler implements SiteTimeoutController.SiteTimeoutHandler	{
		public void onSiteTimeout(int site, long time)	{
			log(Level.WARNING, "Site " + site + " not responding");
			for (Entry<SerialNumber, NE_Impl> item : neDb.getNe(site).entrySet()) {
				long neTime = time;
				if (item.getValue() != null) {
					neTime = Math.min(neTime, ((NE_Impl)item.getValue()).getUpdate());
				}
				setAsNotResponding((SerialNumber)item.getKey(), neTime);
			}
		}

		public void onSiteResume(int site, long time) {
			log(Level.WARNING, "Site " + site + " responding");
		}
	}
}