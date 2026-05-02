package br.ufabc.dataplane.alarms;

import br.ufabc.controlplane.ControlPlane;
import br.ufabc.dataplane.DataPlaneEvent;



public class AlarmGmpls implements Comparable<AlarmGmpls>{

	public static final int PRIORITY_CRITICAL = 60;
	public static final int PRIORITY_MAJOR = 50;
	public static final int PRIORITY_MINOR = 40;
	public static final int PRIORITY_WARNING = 30;
	private long time;
	private DataPlaneEvent event;
	private int priority;
	private double pin = -50;
	private double pout = -50;
	
	
	/**
	 * Creates a Alarm from state of data plane
	 * */
	public AlarmGmpls(DataPlaneEvent event, int priority, double pin, double pout) {
		this.event = event;
		time = System.currentTimeMillis() - ControlPlane.START_TIME;
		this.pin = pin;
		this.pout = pout;
	}
	
	
	@Override
//	public int compareTo(AlarmGmpls alarm) {
//		
//		if (alarm.getTime() < this.time)
//			return 1;
//		else if(alarm.getTime() > this.time)
//			return -1;
//		else return 0;
//	}
	/**
	 * Sort alarms by priority
	 * */
	public int compareTo(AlarmGmpls alarm) {
		
		if (alarm.priority > this.priority)
			return 1;
		else if(alarm.priority < this.priority)
			return -1;
		else return 0;
		
	}

	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(long time) {
		this.time = time;
	}
	
	/**
	 * @return the type
	 */
	public DataPlaneEvent getDataPlaneEvent() {
		return event;
	}


	/**
	 * @param type the type to set
	 */
	public void setDataPlaneEvent(DataPlaneEvent type) {
		this.event = type;
	}


	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}


	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("time: ");
		builder.append(time);
		builder.append(", Event: ");
		builder.append(event);
		builder.append(", Priority: ");
		builder.append(priority);
		return builder.toString();
		
	}


	/**
	 * @return the pin
	 */
	public double getPin() {
		return pin;
	}


	/**
	 * @param pin the pin to set
	 */
	public void setPin(double pin) {
		this.pin = pin;
	}


	
	
	

}
