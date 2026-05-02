package br.com.padtec.v3.server.protocols.ppm3.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.padtec.v3.data.Alarm;
import br.com.padtec.v3.data.Notification;
import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.RackAddress.RackAddress;
import br.com.padtec.v3.data.impl.SPVL4_Impl;
import br.com.padtec.v3.data.ne.NE;
import br.com.padtec.v3.server.AlarmFactory;
import br.com.padtec.v3.server.protocols.codegenerator.Generator;
import br.com.padtec.v3.server.protocols.ppm2v2.handler.SpvjHandler;
import br.com.padtec.v3.util.Functions;
import br.com.padtec.v3.util.PartNumber;
import br.com.padtec.v3.util.log.Log;
import br.ufabc.controlplane.metropad.Servidor;




public class SPVL4Handler implements GenericHandler
{
	private Logger log = Log.getInstance();

	public boolean canHandle(Object board)
	{
		return board instanceof SPVL4_Impl;
	}

	public List<Integer> getRequestCode(Object board, GenericHandler.CodeType type)	{
		SPVL4_Impl sup = (SPVL4_Impl)board;
		List<Integer> result = new ArrayList<Integer>(3);
		for (TypeGet i : TypeGet.values()) {
			if(i.requestType == type 
					&& (i.code != TypeGet.SUP_GET_BLOCKING_MANDATORY.code && i.code != TypeGet.SUP_GET_REQUEST_OF_ACCESS.code 
							|| sup.isControleExclusivoSupported() ) 
							&& (i.code != TypeGet.SUP_GET_NENAME.code || sup.getNENameLimit() > 20) 
							&& (i != TypeGet.SUP_GET_TIME || sup.getVersion() != null 
									&& Functions.compareVersions(sup.getVersion(), "1.1.21") >= 0) ) {
				result.add(Integer.valueOf(i.code));
			}

		}
		return result;


	}

	public boolean analyzeTrap(Object board, int trapId, byte[] value, Collection<Notification> event)
	{
		SPVL4_Impl sup = (SPVL4_Impl)board;
		boolean isStart = (trapId & 0x8000) != 32768;

		trapId &= 32767;
		if ((trapId != 0) && (trapId != 128)) {
			TypeTrap trap = TypeTrap.getType(trapId);
			Alarm alarm = null;
			if (trap != null){

				switch (trap){
				case SUP_TRAP_BLOCKED:
					sup.setBlocked(isStart);
					alarm = AlarmFactory.createAlarm(sup, !(isStart), Alarm.TYPE_UNLOCK);
					event.add(alarm);
					break;
				case SUP_TRAP_LCT:
					sup.setLct(isStart);
					alarm = AlarmFactory.createAlarm(sup, isStart, Alarm.TYPE_LCT);
					event.add(alarm);
					break;
				case SUP_TRAP_COMMAND_RX:
					alarm = AlarmFactory.createAlarm(sup, isStart, Alarm.TYPE_COMMAND_RX);
					event.add(alarm);
					break;
				case SUP_TRAP_RESTART:
					alarm = AlarmFactory.createAlarm(sup, isStart, Alarm.TYPE_RESTART);
					event.add(alarm);
					return true;
				case SUP_TRAP_STARTED:
					alarm = AlarmFactory.createAlarm(sup, isStart, Alarm.TYPE_STARTED);
					event.add(alarm);
					return true;
				case SUP_TRAP_HARD_RESTART:
					alarm = AlarmFactory.createAlarm(sup, isStart, Alarm.TYPE_RESTART);
					event.add(alarm);
					return true;
				case SUP_TRAP_BLOCKING_MANDATORY:
					if ((value != null) && (value.length > 0)) {
						sup.setControleExclusivoObrigatorio(value[0] == 1);
						event.add( AlarmFactory.createAlarm(sup, value[0] == 1,	Alarm.TYPE_BLOCK_ON));
						event.add( AlarmFactory.createAlarm(sup, value[0] == 0, Alarm.TYPE_BLOCK_OFF));
					}
					return false;
				case SUP_TRAP_REQUEST_OF_ACCESS:
					String login = null;
					if ((value != null) && (value.length > 0)) {
						int fim = 1;
						for (int i = 1; i < value.length; ++i) {
							if (value[i] == 0) {
								fim = i;
								break;
							}
						}
						login = new String(value, 1, fim - 1).trim();
						if (value[0] == 1)
							sup.setLockedLogin(login);
						else {
							sup.setLockedLogin(null);
						}
						event.add( AlarmFactory.createGenericAlarm(sup, null, Alarm.TYPE_BLOCK_USER, null, 
								(value[0] == 1) ? true : false, "NC" + login + ";DC" + login));
					}
					return true;
				case SUP_TRAP_NE_RENAMED:
					String nename = new String(value).trim();
					if (!(nename.equals(sup.getNEName()))) {
						sup.setNeName(new String(value).trim());
						Notification n = new Notification(Notification.ID_NE_RENAMED,  sup.getSerial());
						event.add(n);
					}
					return false;
				default:
					break;
				}
			}else{

				this.log.warning("TrapId [" + Functions.getHexa(Functions.l2b(trapId, 2)) + 
						"] Not Found From " + sup.getSerial().toShortString());
				
			}
		}
		return false;
	}

	public int getExpectedResponseSize(Object board, int code)
	{
		return TypeGet.getType(code).size;
	}

	public void analyzeResponse(Object board, Map<Integer, byte[]> tlvs, Map<Integer, byte[]> tlvHistory, 
			Collection<Notification> alarmList, Map<Integer, Double> performanceData) {
		SPVL4_Impl sup = (SPVL4_Impl)board;
		for (Map.Entry<Integer,byte[]> item : tlvs.entrySet()) {
			byte[] data = item.getValue();
			TypeGet trap = TypeGet.getType(item.getKey().intValue());
			if (trap != null){
				switch (trap) {
				case SUP_GET_CFG:
					if (sup.getNENameLimit() > 20) {
						sup.setSupConf(data);
					} else {
						String newName = new String(data, 51, 20).trim();
						if ((newName.length() <= 0) || 	(!(sup.setSupConf(data, newName)))) {
							Notification n = new Notification(Notification.ID_NE_RENAMED, sup.getSerial());
							alarmList.add(n);
						}
					}

					break;
				case SUP_GET_IP:
					sup.setIP(Functions.byte2ip(Functions.getSubarray(data, 0, 4)));
					sup.setMask(Functions.byte2ip(Functions.getSubarray(data, 4, 4)));
					sup.setGateway(Functions.byte2ip(Functions.getSubarray(data, 8, 4)));
					break;
				case SUP_GET_RACK:
					int tamanho = Functions.b2i(data[0]);
					List<RackAddress> racks = new ArrayList<RackAddress>();
					try
					{
						int i = 1;
						if ((tamanho > 0) && (tamanho != 255))
							while (i + 9 <= tamanho)							{
								int type = Functions.b2i(data[i]);
								++i;

								int chave = (int)Functions.b2l(data, i, 4);
								i += 4;

								int part = (int)Functions.b2l(data, i, 2);
								i += 2;

								int serial = (int)Functions.b2l(data, i, 2);
								i += 2;
								SerialNumber element = new SerialNumber(part, serial);

								NE neVerify = PartNumber.getInstance(element, false);

								if (Functions.isLct)
//									neVerify = LocalServer.getNeMapServer().getNE(element);
									neVerify = Servidor.getInstance().getNE(element);
//								else {
//									neVerify = Server.getMapServer().getNE(element);
//								}
								if (neVerify != null) {
									RackAddress c = new RackAddress(type, chave, element);
									racks.add(c);
								}
							}
					}
					catch (Exception e) {
						Log.getInstance().log(	Level.SEVERE, "Fail loading rack config from " + 
								sup.getSerial().toShortString(), e);
					}
					sup.setRackAddress(racks);
					alarmList.add(	new Notification(Notification.ID_NE_RACK_SAVED, sup.getSerial()));
					break;
				case SUP_GET_UPTIME:
					break;
				case SUP_GET_COUNTERS:
					sup.setCanError(Generator.getInt(data, 0, 2));
					sup.setTintError(Generator.getInt(data, 2, 2));
					sup.setCrcError(Generator.getInt(data, 4, 2));
					sup.setTintTotal(Generator.getInt(data, 6, 2));
					sup.setRoBuffer(Generator.getInt(data, 12, 2));
					break;
					
				case SUP_GET_STATUS:
					sup.setLct(Generator.getBit(data, 0, 7));
					sup.setBlocked(Generator.getBit(data, 0, 6));
					break;
				case SUP_GET_SEGMENTATION:
					boolean on = data[0] != 0;
					int novoValor = 512;
					System.arraycopy(Functions.l2b(novoValor, 2), 0, data, 1, 2);
					sup.setSegmentation(Integer.valueOf((on) ? Generator.getInt(data, 1, 2) :	0));
					break;
				case SUP_GET_BLOCKING_MANDATORY:
					sup.setControleExclusivoObrigatorio(data[0] == 1);
					sup.setUpdatedBlockConfig(true);
					break;
				case SUP_GET_REQUEST_OF_ACCESS:
					if (data[0] == 1) {
						int fim = 1;
						for (int i = 1; i < data.length; ++i) {
							if (data[i] == 0) {
								fim = i;
								break;
							}
						}
						sup.setLockedLogin(new String(data, 1, fim - 1));
					} else {
						sup.setLockedLogin(null);
					}
					sup.setUpdatedUserLock(true);
					break;
				case SUP_GET_TIME:
					sup.setClockDelta( new Long(Functions.b2l(data) + System.currentTimeMillis()));
					break;
				case SUP_GET_NENAME:
					String nename = new String(data).trim();
					if (!(nename.equals(sup.getNEName()))) {
						sup.setNeName(nename);
						Notification n = new Notification(Alarm.WARNING, sup.getSerial());
						alarmList.add(n);
					}

				default:
					break;
				}
			}else{
					this.log.warning("Response [" + Functions.getHexa(Functions.l2b(((Integer)item.getKey()).longValue(), 2)) + 
							"] Not Found From " + sup.getSerial().toShortString());
				
			}
		}
	}

	public byte[] analyzeGet(Object board, int code, byte[] data)
	{
		SPVL4_Impl sup = (SPVL4_Impl)board;
		TypeGet trap = TypeGet.getType(code);
		if (trap != null)	{
			int i;
			byte[] result = new byte[getExpectedResponseSize(board, code)];
			switch (trap.ordinal())
			{
			case 3:
				if (sup.getSupConf() == null) {
					byte[] conf = new byte[71];

					Functions.setBytes(conf, 7, 3L, 2);

					Functions.setBytes(conf, 9, 10L, 2);
					conf[13] = 1;
					conf[27] = 1;
					System.arraycopy(sup.getName().getBytes(), 0, conf, 51, 
							sup.getName().getBytes().length);
					sup.setSupConf(conf, sup.getName());
				}
				System.arraycopy(sup.getSupConf(), 0, result, 0, 
						sup.getSupConf().length);
				break;
			case 2:
				byte[] ip = Functions.ip2byte(sup.getIP());
				byte[] mask = Functions.ip2byte(sup.getMask());
				byte[] gw = Functions.ip2byte(sup.getGateway());
				System.arraycopy(ip, 0, result, 0, 4);
				System.arraycopy(mask, 0, result, 4, 4);
				System.arraycopy(gw, 0, result, 8, 4);
				break;
			case 4:
				break;
			case 1:
				break;
			case 5:
				Generator._getInt(result, 0, 2, sup.getCanError());
				Generator._getInt(result, 2, 2, sup.getTintError());
				Generator._getInt(result, 4, 2, sup.getCrcError());
				Generator._getInt(result, 6, 2, sup.getTintTotal());
				Generator._getInt(result, 12, 2, sup.getRoBuffer());
				break;
			case 7:
				Generator._getBit(data, 0, 7, sup.isLct());
				Generator._getBit(data, 0, 6, sup.isBlocked());
				break;
			case 6:
				result[0] = 1;
				System.arraycopy(Functions.l2b(512L, 2), 0, result, 1, 2);
				break;
			case 8:
				if (sup.isControleExclusivoObrigatorio() == null) {
					sup.setControleExclusivoObrigatorio(false);
				}
				Generator._getBit(result, 0, 0, 
						(sup.isControleExclusivoObrigatorio() == null) || 
						(sup.isControleExclusivoObrigatorio().booleanValue()));
				for (i = 1; i < result.length; ++i)
				{
					result[i] = 32;
				}
				if (sup.getLockedLogin() != null) {
					System.arraycopy(sup.getLockedLogin().getBytes(), 0, result, 1, 
							sup.getLockedLogin().getBytes().length);
				}
				break;
			case 9:
				Generator._getBit(result, 0, 0, sup.getLockedLogin() != null);
				for (i = 1; i < result.length - 2; ++i)
				{
					result[i] = 32;
				}
				if (sup.getLockedLogin() != null) {
					System.arraycopy(sup.getLockedLogin().getBytes(), 0, result, 1, 
							sup.getLockedLogin().getBytes().length);
					result[(1 + sup.getLockedLogin().getBytes().length)] = 0;
				}
				result[(result.length - 2)] = 0;
				result[(result.length - 1)] = 0;
				break;
			case 11:
				for (i = 0; i < result.length; ++i) {
					result[i] = 32;
				}
				System.arraycopy(sup.getNEName().getBytes(), 0, result, 0, sup.getNEName().getBytes().length);
				break;
			case 10:
			default:
				break;
			}
			return result;
		} else {
			this.log.warning("Get [" + Functions.getHexa(Functions.l2b(code, 2)) + 
				"] Not Found From " + sup.getSerial().toShortString());
		}

		return null;
	}

	public void analyzeSet(Object board, int code, byte[] data)
	{
		SPVL4_Impl sup = (SPVL4_Impl)board;
		code &= 32767;
		if (code == 27) {
			if (data.length > 0)
				sup.setControleExclusivoObrigatorio(data[0] == 1);
		}
		else if (code == 28) {
			if (data != null)
				sup.setLockedLogin(new String(data));
		}
		else if (code == 32)
			sup.setNeName(new String(data).trim());
	}

	public static List<Notification> getAlarmList(Object board)
	{
		List<Notification> alarms = new ArrayList<Notification>();
		SPVL4_Impl spvl = (SPVL4_Impl)board;

		alarms.addAll(new SpvjHandler().getAlarmList(spvl));

		alarms.add(	AlarmFactory.createGenericAlarm(spvl, null, 1527, null, true, null));

		alarms.add( AlarmFactory.createGenericAlarm(spvl, null, 1528, null, true, null));

		alarms.add(	AlarmFactory.createGenericAlarm(spvl, null,	1529, null, true, null));

		alarms.add(	AlarmFactory.createGenericAlarm(spvl, null,	1542, null, true, null));

		return alarms;
	}

	public Map<Integer, Boolean> getTrapsFromBean(Object board) {
		HashMap<Integer, Boolean> lastTraps = new HashMap<Integer, Boolean>();
		SPVL4_Impl sup = (SPVL4_Impl)board;

		lastTraps.put(Integer.valueOf(TypeTrap.SUP_TRAP_BLOCKED.code), 	Boolean.valueOf(sup.isBlocked()));
		lastTraps.put(Integer.valueOf(TypeTrap.SUP_TRAP_LCT.code), 	Boolean.valueOf(sup.isLct()));
		lastTraps.put(Integer.valueOf(TypeTrap.SUP_TRAP_BLOCKING_MANDATORY.code), sup.isControleExclusivoObrigatorio());
		lastTraps.put(Integer.valueOf(TypeTrap.SUP_TRAP_REQUEST_OF_ACCESS.code), Boolean.valueOf(sup.isControleExclusivo()));

		return lastTraps;
	}

	public static enum TypeGet
	{
		SUP_GET_UPTIME(4, 0, CodeType.CONFIGURATOR), 
		SUP_GET_IP(33, 12, CodeType.OCCASIONAL), 
		SUP_GET_CFG(35, 72, CodeType.DYNAMIC), 
		SUP_GET_RACK (38, 150, CodeType.STATIC), 
		SUP_GET_COUNTERS(39, 24, CodeType.DYNAMIC), 
		SUP_GET_SEGMENTATION (26, 3, CodeType.DYNAMIC), 
		SUP_GET_STATUS(40, 0, CodeType.CONFIGURATOR), 
		SUP_GET_BLOCKING_MANDATORY( 29, 34, CodeType.STATIC),
		SUP_GET_REQUEST_OF_ACCESS (30, 36, CodeType.STATIC), 
		SUP_GET_TIME(31, 8, CodeType.DYNAMIC), 
		SUP_GET_NENAME(32, 64, CodeType.STATIC);


		private TypeGet( int code, int size, CodeType requestType) {

			this.code = code;
			this.size = size;
			this.requestType = requestType;
		}


		public final int code;
		public int size;
		public GenericHandler.CodeType requestType;

		public static TypeGet getType(int code)
		{
			for (TypeGet type : values()) {
				if (type.code == code) {
					return type;
				}
			}
			return null; }
	}

	public static enum TypeTrap {

		SUP_TRAP_BLOCKED(1), 
		SUP_TRAP_LCT(2), 
		SUP_TRAP_COMMAND_RX(5), 
		SUP_TRAP_RESTART(6), 
		SUP_TRAP_STARTED(7), 
		SUP_TRAP_HARD_RESTART(25), 
		SUP_TRAP_BLOCKING_MANDATORY(29), 
		SUP_TRAP_REQUEST_OF_ACCESS(30), 
		SUP_TRAP_NE_RENAMED(32);


		private TypeTrap(int code)
		{
			this.code = code;
		}


		public final int code;

		public static TypeTrap getType(int code)
		{
			for (TypeTrap type : values()) {
				if (type.code == code) {
					return type;
				}
			}
			return null;
		}
	}
}