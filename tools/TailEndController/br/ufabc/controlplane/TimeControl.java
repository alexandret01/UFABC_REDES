package br.ufabc.controlplane;

import java.util.Timer;

public class TimeControl extends Timer{
	
	private static TimeControl timeControl;
	private TimeControl(){
		
	}
	
	public static TimeControl getInstance(){
		if (timeControl == null){
			timeControl = new TimeControl();
		}
		return timeControl;
		
	}
//	@Override
//	public void schedule(TimerTask task, Date firstTime, long period) {
//		super.schedule(task, firstTime, period);
//	}
//	public void scheduleAtFixedRate(TimerTask task, long delay, long period ){
//		super.scheduleAtFixedRate(task, delay, period);
//	}
}
