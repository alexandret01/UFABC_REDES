package br.ufabc.controlplane.metropad;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.padtec.v3.data.Alarm;
import br.com.padtec.v3.data.Notification;
import br.com.padtec.v3.data.NotificationListener;
import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.util.log.Log;

public class PrintAlarms implements NotificationListener{

	public Map<SerialNumber, Notification> notifications;
	public Map<SerialNumber, Notification> readNotifications;
	public Map<SerialNumber, Alarm> alarms;
	public Map<SerialNumber, Alarm> readAlarms;
	private transient Logger log = Log.getInstance();
	private Thread t;
	
	public PrintAlarms() {
		notifications = Collections.synchronizedMap( new LinkedHashMap<SerialNumber, Notification>());
		alarms = Collections.synchronizedMap(new LinkedHashMap<SerialNumber, Alarm>());
		readNotifications = new LinkedHashMap<SerialNumber, Notification>();
		readAlarms = new LinkedHashMap<SerialNumber, Alarm>();
		t = new Thread(new ReadAlarms());
		t.setName("PrintAlarms");
		
	}
	
	public void startPrintNewAlarms(){
		t.start();
	}
	
	public void stopPrintNewAlarms(){
		t.interrupt();
	}

	@Override
	public void notify(Notification notification) {
//		log.log(Level.INFO, notification.toString() );
		if(notification instanceof Alarm){
			Alarm a = (Alarm)notification;
			alarms.put(a.getNeOrigin(), a);
			
		} else {
			notifications.put(notification.getNeOrigin(), notification);
		}
	}
	
	public void showNotifications(){
		for(Notification n : notifications.values()){
			log.log(Level.INFO, n.toString() );
		}
	}
	
	public Map<SerialNumber, Notification> getNotifications(){
		return notifications;
	}
	
	public void showAlarms(){
		for(Alarm a : alarms.values()){
			log.log(Level.INFO, a.toString() );
		}
	}
	
	public Map<SerialNumber, Alarm> getAlarms(){
		return alarms;
	}

	class ReadAlarms implements Runnable{

		@Override
		public void run() {
			while(true){
//				if(!notifications.isEmpty()){
//					showNotifications();
//					readNotifications.putAll(notifications);
//					notifications.clear();
//				}

				if(!alarms.isEmpty()){
					showAlarms();
					readAlarms.putAll(alarms);
					alarms.clear();
				}

//				try {
//					Thread.sleep(1000L);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
			}

		}

	}	
}
