package br.ufabc.controlplane;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

import br.com.padtec.v3.data.Alarm;
import br.com.padtec.v3.data.Notification;
import br.com.padtec.v3.data.SerialNumber;
import br.ufabc.dataplane.DataPlane;

public class GmplsAlarmListener implements GmplsListener  {
	private static final String CLASS = "GmplsAlarmListener";
	public HashMap<Integer, DataPlane> dataPlanes;
	public Map<SerialNumber, Notification> notifications;
	public Map<SerialNumber, Notification> readNotifications;
	public Map<SerialNumber, Alarm> alarms;
	public Map<SerialNumber, Alarm> readAlarms;
	
	public GmplsAlarmListener() {
		notifications = Collections.synchronizedMap( new LinkedHashMap<SerialNumber, Notification>());
		alarms = Collections.synchronizedMap(new LinkedHashMap<SerialNumber, Alarm>());
		readNotifications = new LinkedHashMap<SerialNumber, Notification>();
		readAlarms = new LinkedHashMap<SerialNumber, Alarm>();
		dataPlanes = new HashMap<Integer, DataPlane>();
		
	}
	
	@Override
	public void notify(Notification notification) {
		
		if(notification instanceof Alarm){
			Alarm a = (Alarm)notification;
//			log(CLASS, Level.WARNING, a.toString() + ", ALARM Name: " + a.getAlarmName() + ", ALARM ID: " + a.getAlType());
			alarms.put(a.getNeOrigin(), a);
			DataPlane dp = ControlPlane.getInstance().getDataPlane();
			if(dp != null){
//				for (DataPlane dp : dataPlanes.values()){
					if (dp.contains(a.getNeOrigin())){
						dp.analizeAlarm(a);
					}
//				}
			}
			
		} else {
//			log(CLASS, Level.WARNING, notification.toString());
			notifications.put(notification.getNeOrigin(), notification);
		}
		
	}

	@Override
	public DataPlane getDataPlane(int id) {
		return dataPlanes.get(id);
	}

	@Override
	public void setDataPlane(int id, DataPlane dataPlane) {
		dataPlanes.put(id, dataPlane);
		
	}
	
	public void log(String CLASS, Level level, String message){
		ControlPlane.getInstance().log(CLASS, level, message);
	}

}
