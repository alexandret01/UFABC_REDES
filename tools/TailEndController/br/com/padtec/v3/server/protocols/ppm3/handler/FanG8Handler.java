package br.com.padtec.v3.server.protocols.ppm3.handler;


import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.padtec.v3.data.Notification;
import br.com.padtec.v3.data.impl.FanG8_Impl;
import br.com.padtec.v3.server.AlarmFactory;
import br.com.padtec.v3.server.protocols.codegenerator.Generator;
import br.com.padtec.v3.util.Functions;
import br.com.padtec.v3.util.log.Log;

public class FanG8Handler  implements GenericHandler {
	private Logger log = Log.getInstance();

	private Map<TypeGet, Integer> idx_get = new EnumMap<TypeGet, Integer>(TypeGet.class);

	private Map<TypeTrap, Integer> idx_trap = new EnumMap<TypeTrap, Integer>(TypeTrap.class);

	public FanG8Handler() {

		this.idx_get.put(TypeGet.FANG8_GET_SENSOR1_TEMP_THR, new Integer(0));
		this.idx_get.put(TypeGet.FANG8_GET_SENSOR2_TEMP_THR, new Integer(1));
		this.idx_get.put(TypeGet.FANG8_GET_SENSOR1_TEMPERATURE, new Integer(0));
		this.idx_get.put(TypeGet.FANG8_GET_SENSOR2_TEMPERATURE, new Integer(1));
		this.idx_get.put(TypeGet.FANG8_GET_COOLER1_SPEED, new Integer(0));
		this.idx_get.put(TypeGet.FANG8_GET_COOLER2_SPEED, new Integer(1));
		this.idx_get.put(TypeGet.FANG8_GET_COOLER3_SPEED, new Integer(2));
		this.idx_get.put(TypeGet.FANG8_GET_COOLER4_SPEED, new Integer(3));
		this.idx_get.put(TypeGet.FANG8_GET_COOLER5_SPEED, new Integer(4));
		this.idx_get.put(TypeGet.FANG8_GET_COOLER6_SPEED, new Integer(5));
		this.idx_get.put(TypeGet.FANG8_GET_COOLER7_SPEED, new Integer(6));
		this.idx_get.put(TypeGet.FANG8_GET_COOLER8_SPEED, new Integer(7));
		this.idx_get.put(TypeGet.FANG8_GET_COOLER1_MIN_SPEED, new Integer(0));
		this.idx_get.put(TypeGet.FANG8_GET_COOLER2_MIN_SPEED, new Integer(1));
		this.idx_get.put(TypeGet.FANG8_GET_COOLER3_MIN_SPEED, new Integer(2));
		this.idx_get.put(TypeGet.FANG8_GET_COOLER4_MIN_SPEED, new Integer(3));
		this.idx_get.put(TypeGet.FANG8_GET_COOLER5_MIN_SPEED, new Integer(4));
		this.idx_get.put(TypeGet.FANG8_GET_COOLER6_MIN_SPEED, new Integer(5));
		this.idx_get.put(TypeGet.FANG8_GET_COOLER7_MIN_SPEED, new Integer(6));
		this.idx_get.put(TypeGet.FANG8_GET_COOLER8_MIN_SPEED, new Integer(7));
		this.idx_get.put(TypeGet.FANG8_GET_COOLER1_MAX_SPEED, new Integer(0));
		this.idx_get.put(TypeGet.FANG8_GET_COOLER2_MAX_SPEED, new Integer(1));
		this.idx_get.put(TypeGet.FANG8_GET_COOLER3_MAX_SPEED, new Integer(2));
		this.idx_get.put(TypeGet.FANG8_GET_COOLER4_MAX_SPEED, new Integer(3));
		this.idx_get.put(TypeGet.FANG8_GET_COOLER5_MAX_SPEED, new Integer(4));
		this.idx_get.put(TypeGet.FANG8_GET_COOLER6_MAX_SPEED, new Integer(5));
		this.idx_get.put(TypeGet.FANG8_GET_COOLER7_MAX_SPEED, new Integer(6));
		this.idx_get.put(TypeGet.FANG8_GET_COOLER8_MAX_SPEED, new Integer(7));

		this.idx_trap.put(TypeTrap.FANG8_TRAP_SENSOR1_OVERHEAT, new Integer(0));
		this.idx_trap.put(TypeTrap.FANG8_TRAP_SENSOR2_OVERHEAT, new Integer(1));
		this.idx_trap.put(TypeTrap.FANG8_TRAP_FAIL_COOLER1, new Integer(0));
		this.idx_trap.put(TypeTrap.FANG8_TRAP_FAIL_COOLER2, new Integer(1));
		this.idx_trap.put(TypeTrap.FANG8_TRAP_FAIL_COOLER3, new Integer(2));
		this.idx_trap.put(TypeTrap.FANG8_TRAP_FAIL_COOLER4, new Integer(3));
		this.idx_trap.put(TypeTrap.FANG8_TRAP_FAIL_COOLER5, new Integer(4));
		this.idx_trap.put(TypeTrap.FANG8_TRAP_FAIL_COOLER6, new Integer(5));
		this.idx_trap.put(TypeTrap.FANG8_TRAP_FAIL_COOLER7, new Integer(6));
		this.idx_trap.put(TypeTrap.FANG8_TRAP_FAIL_COOLER8, new Integer(7));
	}

	public boolean canHandle(Object board) {
		return board instanceof FanG8_Impl;
	}

	public List<Integer> getRequestCode(Object board, CodeType type) {
		List<Integer> result = new ArrayList<Integer>(3);
		for (TypeGet i : TypeGet.values()) {
			if (i.requestType == type) {
				result.add(Integer.valueOf(i.code));
			}
		}
		return result;
	}

	public boolean analyzeTrap(Object board, int trapId, byte[] value, Collection<Notification> event)  {
		FanG8_Impl fan = (FanG8_Impl)board;
		boolean isStart = (trapId & 0x8000) != 32768;
		trapId &= 32767;
		if ((trapId != 0) &&   (trapId != 128))   {
			TypeTrap trap = TypeTrap.getType(trapId);
			if (trap != null){
				switch (trap)
				{
				case FANG8_TRAP_SENSOR1_OVERHEAT:
					if (!(fan.haveTemperatureSensors())) 
						fan.getTemperatureSensor_Impl(0).setOverHeat(isStart);
					event.add(AlarmFactory.createAlarm(fan, isStart, 1034));	

					break;
				case FANG8_TRAP_SENSOR2_OVERHEAT:
					if (!(fan.haveTemperatureSensors())) 
						fan.getTemperatureSensor_Impl(1).setOverHeat(isStart);
					event.add( AlarmFactory.createAlarm(fan, isStart, 1035));

					break;
				case FANG8_TRAP_FAIL_COOLER1:
					fan.getCooler_Impl(0).setFail(isStart);
					event.add( AlarmFactory.createAlarm(fan, isStart, 1036));

					break;
				case FANG8_TRAP_FAIL_COOLER2:
					fan.getCooler_Impl(1).setFail(isStart);
					event.add( AlarmFactory.createAlarm(fan, isStart, 1037));

					break;
				case FANG8_TRAP_FAIL_COOLER3:
					fan.getCooler_Impl(2).setFail(isStart);
					event.add( AlarmFactory.createAlarm(fan, isStart, 1038));

					break;
				case FANG8_TRAP_FAIL_COOLER4:
					fan.getCooler_Impl(3).setFail(isStart);
					event.add( AlarmFactory.createAlarm(fan, isStart, 1039));

					break;
				case FANG8_TRAP_FAIL_COOLER5:
					fan.getCooler_Impl(4).setFail(isStart);
					event.add( AlarmFactory.createAlarm(fan, isStart, 1040));

					break;
				case FANG8_TRAP_FAIL_COOLER6:
					fan.getCooler_Impl(5).setFail(isStart);
					event.add( AlarmFactory.createAlarm(fan, isStart, 1041));

					break;
				case FANG8_TRAP_FAIL_COOLER7:
					fan.getCooler_Impl(6).setFail(isStart);
					event.add( AlarmFactory.createAlarm(fan, isStart, 1042));

					break;
				case FANG8_TRAP_FAIL_COOLER8:
					fan.getCooler_Impl(7).setFail(isStart);
					event.add( AlarmFactory.createAlarm(fan, isStart, 1043));

					break;
				case FANG8_TRAP_FAN_OFF:
					fan.setOff(isStart);
					event.add( AlarmFactory.createAlarm(fan, isStart, 1544));
				default:
					break;

				}
			}else{this.log.warning("TrapId [" + Functions.getHexa(Functions.l2b(trapId, 2)) +  "] Not Found From " + fan.getSerial().toShortString());
			}
		}
		return false;
	}

	public int getExpectedResponseSize(Object board, int code)
	{
		return TypeGet.getType(code).size;
	}

	public void analyzeResponse(Object board, Map<Integer, byte[]> tlvs, Map<Integer, byte[]> tlvHistory, Collection<Notification> alarmList, Map<Integer, Double> performanceData)
	{
		FanG8_Impl fan = (FanG8_Impl)board;

		for (Iterator<Entry<Integer,byte[]>> localIterator = tlvs.entrySet().iterator(); localIterator.hasNext(); ) { 
			int i;
			Integer id;
			Entry<Integer,byte[]> item = localIterator.next();
			byte[] data = item.getValue();
			TypeGet get_type = TypeGet.getType(item.getKey().intValue());
			if (get_type != null){
				switch (get_type)  {
				case FANG8_GET_DISCOVERY:
					this.log.warning("Response [" + Functions.getHexa(Functions.l2b(((Integer)item.getKey()).intValue(), 2)) + 
					"] - FANG8_GET_DISCOVERY - não devia ser recebido/tratado pelo Handler");
					break;
				case FANG8_GET_COOLERS_SPEED:
					for (i = 0; i < 8; ++i) {
						fan.getCooler_Impl(i).setSpeedPercentage(
								Generator.getPercentage(data, i));
					}
					break;
				case FANG8_GET_COOLER1_SPEED:
				case FANG8_GET_COOLER2_SPEED:
				case FANG8_GET_COOLER3_SPEED:
				case FANG8_GET_COOLER4_SPEED:
				case FANG8_GET_COOLER5_SPEED:
				case FANG8_GET_COOLER6_SPEED:
				case FANG8_GET_COOLER7_SPEED:
				case FANG8_GET_COOLER8_SPEED:
					id = (Integer)this.idx_get.get(get_type);
					if (id != null) {
						fan.getCooler_Impl(id.intValue()).setSpeedPercentage( Generator.getPercentage(data, 0));
					}
					break;
				case FANG8_GET_COOLERS_MIN_SPEED:
					for (i = 0; i < 8; ++i) {
						fan.getCooler_Impl(i).setMinSpeedPercentage( Generator.getPercentage(data, i));
					}
					break;
				case FANG8_GET_COOLER1_MIN_SPEED:
				case FANG8_GET_COOLER2_MIN_SPEED:
				case FANG8_GET_COOLER3_MIN_SPEED:
				case FANG8_GET_COOLER4_MIN_SPEED:
				case FANG8_GET_COOLER5_MIN_SPEED:
				case FANG8_GET_COOLER6_MIN_SPEED:
				case FANG8_GET_COOLER7_MIN_SPEED:
				case FANG8_GET_COOLER8_MIN_SPEED:
					id = (Integer)this.idx_get.get(get_type);
					if (id != null) {
						fan.getCooler_Impl(id.intValue()).setMinSpeedPercentage( Generator.getPercentage(data, 0));
					}
					break;
				case FANG8_GET_COOLERS_MAX_SPEED:
					for (i = 0; i < 8; ++i) {
						fan.getCooler_Impl(i).setMaxSpeedPercentage(Generator.getPercentage(data, i));
					}
					break;
				case FANG8_GET_COOLER1_MAX_SPEED:
				case FANG8_GET_COOLER2_MAX_SPEED:
				case FANG8_GET_COOLER3_MAX_SPEED:
				case FANG8_GET_COOLER4_MAX_SPEED:
				case FANG8_GET_COOLER5_MAX_SPEED:
				case FANG8_GET_COOLER6_MAX_SPEED:
				case FANG8_GET_COOLER7_MAX_SPEED:
				case FANG8_GET_COOLER8_MAX_SPEED:
					id = (Integer)this.idx_get.get(get_type);
					if (id != null) {
						fan.getCooler_Impl(id.intValue()).setMaxSpeedPercentage(Generator.getPercentage(data, 0));
					}
					break;
				case FANG8_GET_SENSORS_TEMP_THR:
					if (fan.haveTemperatureSensors()) {
						for (i = 0; i < 2; ++i) {
							fan.getTemperatureSensor_Impl(i).setTemperatureThreshold(Generator.getByte(data, i));
						}
					}
					break;
				case FANG8_GET_SENSOR1_TEMP_THR:
				case FANG8_GET_SENSOR2_TEMP_THR:
					if (fan.haveTemperatureSensors()) {
						id = (Integer)this.idx_get.get(get_type);
						if (id != null) {
							fan.getTemperatureSensor_Impl(id.intValue()).setTemperatureThreshold(Generator.getByte(data, 0));
						}
					}
					break;
				case FANG8_GET_SENSORS_TEMPERATURE:
					if (fan.haveTemperatureSensors()) {
						for (i = 0; i < 2; ++i) {
							fan.getTemperatureSensor_Impl(i).setTemperature( Generator.getByte(data, i));
						}
					}
					break;
				case FANG8_GET_SENSOR1_TEMPERATURE:
				case FANG8_GET_SENSOR2_TEMPERATURE:
					if (fan.haveTemperatureSensors()) {
						id = (Integer)this.idx_get.get(get_type);
						if (id != null) {
							fan.getTemperatureSensor_Impl(id.intValue()).setTemperature(Generator.getByte(data, 0));
						}
					}
					break;
				case FANG8_GET_COOLERS_TRAPS:
					for (i = 0; i < 8; ++i) {
						fan.getCooler_Impl(i).setFail(Generator.getBit(data, 0, i));
					}
					break;
				case FANG8_GET_SENSORS_TRAPS:
					if (fan.haveTemperatureSensors()) {
						for (i = 0; i < 2; ++i) {
							fan.getTemperatureSensor_Impl(i).setOverHeat(Generator.getBit(data, 0, i));
						}

					}

				default:
					break;
				}
			}else {
				this.log.warning("Response [" +   Functions.getHexa(Functions.l2b(((Integer)item.getKey()).intValue(), 2)) +
						"] Not Found From " + fan.getSerial().toShortString());
			}
		}
	}

	public byte[] analyzeGet(Object board, int code, byte[] payload) {
		int i;
		Integer id;
		FanG8_Impl fan = (FanG8_Impl)board;
		byte[] data = new byte[getExpectedResponseSize(board, code)];
		TypeGet get_type = TypeGet.getType(code);

		if (get_type != null) {
			switch (get_type)   {
			case FANG8_GET_DISCOVERY:
				break;
			case FANG8_GET_COOLERS_SPEED:
				for (i = 0; i < 8; ++i) {
					Generator._getPercentage(data, i, 
							(int)fan.getCooler(i).getSpeedPercentage());
				}
				break;
			case FANG8_GET_COOLER1_SPEED:
			case FANG8_GET_COOLER2_SPEED:
			case FANG8_GET_COOLER3_SPEED:
			case FANG8_GET_COOLER4_SPEED:
			case FANG8_GET_COOLER5_SPEED:
			case FANG8_GET_COOLER6_SPEED:
			case FANG8_GET_COOLER7_SPEED:
			case FANG8_GET_COOLER8_SPEED:
				id = (Integer)this.idx_get.get(get_type);
				if (id != null) 
					Generator._getPercentage(data, 0, (int)fan.getCooler(id.intValue()).getSpeedPercentage());
				break;
			case FANG8_GET_COOLERS_MIN_SPEED:
				for (i = 0; i < 8; ++i) {
					Generator._getPercentage(data, i, 
							(int)fan.getCooler(i).getMinSpeedPercentage());
				}
				break;
			case FANG8_GET_COOLER1_MIN_SPEED:
			case FANG8_GET_COOLER2_MIN_SPEED:
			case FANG8_GET_COOLER3_MIN_SPEED:
			case FANG8_GET_COOLER4_MIN_SPEED:
			case FANG8_GET_COOLER5_MIN_SPEED:
			case FANG8_GET_COOLER6_MIN_SPEED:
			case FANG8_GET_COOLER7_MIN_SPEED:
			case FANG8_GET_COOLER8_MIN_SPEED:
				id = (Integer)this.idx_get.get(get_type);
				if (id != null)
					Generator._getPercentage(data, 0, (int)fan.getCooler(id.intValue()).getMinSpeedPercentage());
				break;
			case FANG8_GET_COOLERS_MAX_SPEED:
				for (i = 0; i < 8; ++i) {
					Generator._getPercentage(data, i, 
							(int)fan.getCooler(i).getMaxSpeedPercentage());
				}
				break;
			case FANG8_GET_COOLER1_MAX_SPEED:
			case FANG8_GET_COOLER2_MAX_SPEED:
			case FANG8_GET_COOLER3_MAX_SPEED:
			case FANG8_GET_COOLER4_MAX_SPEED:
			case FANG8_GET_COOLER5_MAX_SPEED:
			case FANG8_GET_COOLER6_MAX_SPEED:
			case FANG8_GET_COOLER7_MAX_SPEED:
			case FANG8_GET_COOLER8_MAX_SPEED:
				id = (Integer)this.idx_get.get(get_type);
				if (id != null) 
					Generator._getPercentage(data, 0, (int)fan.getCooler(id.intValue()).getMaxSpeedPercentage());

				break;
			case FANG8_GET_SENSORS_TEMP_THR:
				for (i = 0; i < 2; ++i) {
					Generator._getByte(data, i, 
							(byte)(int)fan.getTemperatureSensor(i).getTemperatureThreshold());
				}
				break;
			case FANG8_GET_SENSOR1_TEMP_THR:
			case FANG8_GET_SENSOR2_TEMP_THR:
				id = (Integer)this.idx_get.get(get_type);
				if (id != null)
					Generator._getByte(data, 0, (byte)(int)fan.getTemperatureSensor(id.intValue()).getTemperatureThreshold());
				break;
			case FANG8_GET_SENSORS_TEMPERATURE:
				for (i = 0; i < 2; ++i) {
					Generator._getByte(data, i, 
							(byte)(int)fan.getTemperatureSensor(i).getTemperature());
				}
				break;
			case FANG8_GET_SENSOR1_TEMPERATURE:
			case FANG8_GET_SENSOR2_TEMPERATURE:
				id = (Integer)this.idx_get.get(get_type);
				if (id != null) 
					Generator._getByte(data, 0, (byte)(int)fan.getTemperatureSensor(id.intValue()).getTemperature());

				break;
			case FANG8_GET_COOLERS_TRAPS:
				for (i = 0; i < 8; ++i) {
					Generator._getBit(data, 0, i, fan.getCooler(i).isFail());
				}
				break;
			case FANG8_GET_SENSORS_TRAPS:
				for (i = 0; i < 2; ++i) {
					Generator._getBit(data, 0, i, 
							fan.getTemperatureSensor(i).isOverHeat());
				}

			default:
				break;
			} 
		}else { 
			this.log.warning("Get [" + Functions.getHexa(Functions.l2b(code, 2)) + "] Not Found From " + fan.getSerial().toShortString());
		}
		return data;
	}

	public void analyzeSet(Object board, int code, byte[] data)  {
	}

	public Map<Integer, Boolean> getTrapsFromBean(Object board)
	{
		HashMap<Integer, Boolean> lastTraps = new HashMap<Integer, Boolean>();

		FanG8_Impl fang8 = (FanG8_Impl)board;

		lastTraps.put(Integer.valueOf(TypeTrap.FANG8_TRAP_FAIL_COOLER1.getCode()), Boolean.valueOf( fang8.getCooler(0).isFail()));
		lastTraps.put(Integer.valueOf(TypeTrap.FANG8_TRAP_FAIL_COOLER2.getCode()), Boolean.valueOf( fang8.getCooler(1).isFail()));
		lastTraps.put(Integer.valueOf(TypeTrap.FANG8_TRAP_FAIL_COOLER3.getCode()), Boolean.valueOf( fang8.getCooler(2).isFail()));
		lastTraps.put(Integer.valueOf(TypeTrap.FANG8_TRAP_FAIL_COOLER4.getCode()), Boolean.valueOf( fang8.getCooler(3).isFail()));
		lastTraps.put(Integer.valueOf(TypeTrap.FANG8_TRAP_FAIL_COOLER5.getCode()), Boolean.valueOf( fang8.getCooler(4).isFail()));
		lastTraps.put(Integer.valueOf(TypeTrap.FANG8_TRAP_FAIL_COOLER6.getCode()), Boolean.valueOf( fang8.getCooler(5).isFail()));
		lastTraps.put(Integer.valueOf(TypeTrap.FANG8_TRAP_FAIL_COOLER7.getCode()), Boolean.valueOf( fang8.getCooler(6).isFail()));
		lastTraps.put(Integer.valueOf(TypeTrap.FANG8_TRAP_FAIL_COOLER8.getCode()), Boolean.valueOf( fang8.getCooler(7).isFail()));
		lastTraps.put(Integer.valueOf(TypeTrap.FANG8_TRAP_FAN_OFF.getCode()), Boolean.valueOf(fang8.isOff()));

		if (fang8.haveTemperatureSensors()) {
			lastTraps.put(Integer.valueOf(TypeTrap.FANG8_TRAP_SENSOR1_OVERHEAT.getCode()), Boolean.valueOf( fang8.getTemperatureSensor(0).isOverHeat()));
			lastTraps.put(Integer.valueOf(TypeTrap.FANG8_TRAP_SENSOR2_OVERHEAT.getCode()), Boolean.valueOf( fang8.getTemperatureSensor(1).isOverHeat()));
		}

		return lastTraps;
	}

	public static List<Notification> getAlarmList(Object board)
	{
		List<Notification> alarms = new ArrayList<Notification>();

		FanG8_Impl fanG8 = (FanG8_Impl)board;

		if (fanG8 != null)    {
			if (fanG8.haveTemperatureSensors()) {
				alarms.add(  AlarmFactory.createGenericAlarm(fanG8, null, 1034, null, true, null));

				alarms.add(  AlarmFactory.createGenericAlarm(fanG8, null, 1035, null, true, null));
			}

			alarms.add(   AlarmFactory.createGenericAlarm(fanG8, null,  1036, null, true, null));

			alarms.add(   AlarmFactory.createGenericAlarm(fanG8, null,  1037, null, true, null));

			alarms.add(   AlarmFactory.createGenericAlarm(fanG8, null,  1038, null, true, null));

			alarms.add(   AlarmFactory.createGenericAlarm(fanG8, null,  1039, null, true, null));

			alarms.add(   AlarmFactory.createGenericAlarm(fanG8, null,  1040, null, true, null));

			alarms.add(   AlarmFactory.createGenericAlarm(fanG8, null,  1041, null, true, null));

			alarms.add(   AlarmFactory.createGenericAlarm(fanG8, null,  1042, null, true, null));

			alarms.add(   AlarmFactory.createGenericAlarm(fanG8, null,  1043, null, true, null));

			alarms.add( AlarmFactory.createGenericAlarm(fanG8, null, 1544, null, true, null));
			Log.getInstance().log(Level.INFO, "need implement the alarms em class FanG8_Impl");
		}

		return alarms;
	}

	public static enum TypeGet  {

		FANG8_GET_DISCOVERY(0,56,CodeType.CONFIGURATOR),
		FANG8_GET_SENSORS_TEMP_THR(256,2,CodeType.DYNAMIC),
		FANG8_GET_SENSOR1_TEMP_THR(257,1,CodeType.CONFIGURATOR),
		FANG8_GET_SENSOR2_TEMP_THR(258,1,CodeType.CONFIGURATOR),
		FANG8_GET_COOLERS_SPEED(512,8,CodeType.DYNAMIC),
		FANG8_GET_COOLER1_SPEED(513,1,CodeType.CONFIGURATOR),
		FANG8_GET_COOLER2_SPEED(514,1,CodeType.CONFIGURATOR),
		FANG8_GET_COOLER3_SPEED(515,1,CodeType.CONFIGURATOR),
		FANG8_GET_COOLER4_SPEED(516,1,CodeType.CONFIGURATOR),
		FANG8_GET_COOLER5_SPEED(517,1,CodeType.CONFIGURATOR),
		FANG8_GET_COOLER6_SPEED(518,1,CodeType.CONFIGURATOR),
		FANG8_GET_COOLER7_SPEED(519,1,CodeType.CONFIGURATOR),
		FANG8_GET_COOLER8_SPEED(520,1,CodeType.CONFIGURATOR),
		FANG8_GET_COOLERS_MIN_SPEED(768,8,CodeType.STATIC),
		FANG8_GET_COOLER1_MIN_SPEED(769,1,CodeType.CONFIGURATOR),
		FANG8_GET_COOLER2_MIN_SPEED(770,1,CodeType.CONFIGURATOR),
		FANG8_GET_COOLER3_MIN_SPEED(771,1,CodeType.CONFIGURATOR),
		FANG8_GET_COOLER4_MIN_SPEED(772,1,CodeType.CONFIGURATOR),
		FANG8_GET_COOLER5_MIN_SPEED(773,1,CodeType.CONFIGURATOR),
		FANG8_GET_COOLER6_MIN_SPEED(774,1,CodeType.CONFIGURATOR),
		FANG8_GET_COOLER7_MIN_SPEED(775,1,CodeType.CONFIGURATOR),
		FANG8_GET_COOLER8_MIN_SPEED(776,1,CodeType.CONFIGURATOR),
		FANG8_GET_COOLERS_MAX_SPEED(1024,8,CodeType.STATIC),
		FANG8_GET_COOLER1_MAX_SPEED(1025,1,CodeType.CONFIGURATOR),
		FANG8_GET_COOLER2_MAX_SPEED(1026,1,CodeType.CONFIGURATOR),
		FANG8_GET_COOLER3_MAX_SPEED(1027,1,CodeType.CONFIGURATOR),
		FANG8_GET_COOLER4_MAX_SPEED(1028,1,CodeType.CONFIGURATOR),
		FANG8_GET_COOLER5_MAX_SPEED(1029,1,CodeType.CONFIGURATOR),
		FANG8_GET_COOLER6_MAX_SPEED(1030,1,CodeType.CONFIGURATOR),
		FANG8_GET_COOLER7_MAX_SPEED(1031,1,CodeType.CONFIGURATOR),
		FANG8_GET_COOLER8_MAX_SPEED(1032,1,CodeType.CONFIGURATOR),
		FANG8_GET_SENSORS_TEMPERATURE(4096,2,CodeType.DYNAMIC),
		FANG8_GET_SENSOR1_TEMPERATURE(4097,1,CodeType.CONFIGURATOR),
		FANG8_GET_SENSOR2_TEMPERATURE(4098,1,CodeType.CONFIGURATOR),
		FANG8_GET_COOLERS_TRAPS(8192,1,CodeType.CONFIGURATOR),
		FANG8_GET_SENSORS_TRAPS(8448,1,CodeType.CONFIGURATOR);

		private final int code;
		public int size;
		public GenericHandler.CodeType requestType;

		private TypeGet(int code, int size, CodeType requestType){
			this.code = code;
			this.size = size;
			this.requestType = requestType;
		}

		public int getCode()
		{
			return this.code;
		}

		public static TypeGet getType(int code) {
			for (TypeGet type : values()) {
				if (type.getCode() == code) {
					return type;
				}
			}
			return null; }
	}

	public static enum TypeTrap {

		FANG8_TRAP_SENSOR1_OVERHEAT(257),
		FANG8_TRAP_SENSOR2_OVERHEAT(258),
		FANG8_TRAP_FAIL_COOLER1(1), 
		FANG8_TRAP_FAIL_COOLER2(2), 
		FANG8_TRAP_FAIL_COOLER3(3), 
		FANG8_TRAP_FAIL_COOLER4(4), 
		FANG8_TRAP_FAIL_COOLER5(5), 
		FANG8_TRAP_FAIL_COOLER6(6), 
		FANG8_TRAP_FAIL_COOLER7(7), 
		FANG8_TRAP_FAIL_COOLER8(8), 
		FANG8_TRAP_FAN_OFF(513);


		private final int code;

		private TypeTrap(int code)
		{
			this.code = code;
		}

		public int getCode()
		{
			return this.code;
		}

		public static TypeTrap getType(int code) {
			for (TypeTrap type : values()) {
				if (type.getCode() == code) {
					return type;
				}
			}
			return null;
		}
	}
}