
package br.com.padtec.v3.server;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import br.com.padtec.v3.data.Alarm;
import br.com.padtec.v3.data.AlarmConfig;
import br.com.padtec.v3.data.GenericExtendedAlarm;
import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.impl.NE_Impl;

public abstract class AlarmFactory  {
	private static final Map<Integer, AlarmConfig> alarmConfigs = new ConcurrentHashMap<Integer, AlarmConfig>();
	private static Map<Integer, AlarmConfig> allAlarmConfigs = new Hashtable<Integer, AlarmConfig>();

	//  private static NEMap_Impl rootMap;

	//  static
	//  {
	//    if (!(Functions.isLct))
	//      rootMap = Server.getRootMap();
	//  }

	protected static boolean applyAlarmConfig(Alarm a){
		if (a.getAlType() > 0) {
			AlarmConfig config = getAlarmConfig(a.getAlType());
			if (config != null) {
				applyAlarmConfig(a, config);
				return true;
			}
		}
		a.setPriority(20);
		a.setAlarmName("Unknown");
		a.setDescription("Alarme Desconhecido");
		return false;
	}

	public static void applyAlarmConfig(Alarm a, AlarmConfig config){
		a.setPriority(config.getPriority());
		a.setAlarmName(config.getNome());
		a.setEmail(config.getMail().booleanValue());
		a.setDescription(config.getDesc());
		a.setIntermitenceTime(config.getIntermitenceTime());
		if (a.getNeOrigin().getPart() == 23) {
			a.setMapName("Servidor");
		}
		//    else if (rootMap != null)
		//    {
		//      NEMap_Impl map = null;
		//      NEMap_Impl phyMap = rootMap.getMapbyKey(NEMapServer_Impl.PHKEY);
		//      if (phyMap != null) {
		//        map = phyMap.getMap(a.getNeOrigin());
		//      }
		//      if (map != null) {
		//        a.setMapName(map.getName());
		//        a.setMapKey(map.getKey());
		//      }
		//      else
		//      {
		//        a.setMapName(Msg.getString("AlarmFactoryPPM2v2.Unknown_Map"));
		//        a.setMapKey(new Integer(1));
		//      }
		//    }
		//    else if (Functions.isLct) {
		//      LocalNEMap aux = LocalServer.getNeMapServer().getMap(a.getNeOrigin());
		//      if (aux != null) {
		//        a.setMapName(aux.getName());
		//        a.setMapKey(aux.getKey());
		//      } else {
		//        a.setMapName(Msg.getString("AlarmFactoryPPM2v2.Unknown_Map"));
		//        a.setMapKey(new Integer(1));
		//      }
		//    } else {
		//      a.setMapName(Msg.getString("AlarmFactoryPPM2v2.Unknown_Map"));
		a.setMapName("mapa não é usado");
		a.setMapKey(new Integer(1));
		//    }
	}

	public static void resetAlarmConfig()	{
		alarmConfigs.clear();
	}
	//
	//  protected static SHKConfig getSHKConfig(SerialNumber serial, int contact) {
	//    SHKConfig c = DataBaseFactory.getColectorInstance().getSHKConfig(serial, 
	//      new Integer(contact));
	//    return c;
	//  }

	private static AlarmConfig getAlarmConfig(int alarmType) {
		if (allAlarmConfigs.isEmpty()){
			Alarm.initiateAlarmConfig(allAlarmConfigs);
		}
		AlarmConfig c = (AlarmConfig)alarmConfigs.get(Integer.valueOf(alarmType));
		if (c == null)	{


			c = (AlarmConfig)allAlarmConfigs.get(alarmType);
			alarmConfigs.put(c.getId(), c);
			//      c = DataBaseFactory.getColectorInstance().getAlarmConfig(alarmType);

		}
		return c;
	}

	public static Alarm createAlarm(NE_Impl ne, boolean isNew, int idAlarm)	{
		if (ne == null) {
			return null;
		}
		Alarm a = new Alarm(ne.getSerial(), idAlarm);
		if (!(isNew))
			a.setEndDate(System.currentTimeMillis());
		a.setNeName(ne.getName());
		a.setNeOrigin(ne.getSerial());
		if (applyAlarmConfig(a)) {
			return a;
		}
		return null;
	}

	public static Alarm createAlarm(NE_Impl ne, boolean isNew, int idAlarm, Integer contact)
	{
		Alarm a = new Alarm(ne.getSerial(), idAlarm);
		if (!(isNew))
			a.setEndDate(System.currentTimeMillis());
		a.setNeName(ne.getName());
		a.setNeOrigin(ne.getSerial());
		a.setContact(contact);
		if (applyAlarmConfig(a)) {
			return a;
		}
		return null;
	}

	public static GenericExtendedAlarm createGenericAlarm(NE_Impl ne, String location, int idAlarm, Integer contact, 
			boolean isNew, String detail) {
		SerialNumber serial;
		if (ne == null)	{
			serial = new SerialNumber(23, 1);
		} else 
			serial = ne.getSerial();

		GenericExtendedAlarm alarm = new GenericExtendedAlarm(serial, idAlarm, contact);
		if (!(isNew)) {
			alarm.setEndDate(System.currentTimeMillis());
		}
		if (ne != null) {
			alarm.setNeName(ne.getName());
		}
		alarm.setNeOrigin(serial);
		if (!(applyAlarmConfig(alarm))) {
			return null;
		}
		if (location != null) {
			alarm.setMapName(location);
		}
		if (detail != null) {
			alarm.setDetail(detail);
		}
		return alarm;
	}
}