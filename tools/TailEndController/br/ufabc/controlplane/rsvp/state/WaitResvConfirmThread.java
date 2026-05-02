package br.ufabc.controlplane.rsvp.state;

import br.ufabc.dataplane.DataPlane;

/**
 * Wait for a Resv Confirm Message 
 */
public class WaitResvConfirmThread extends Thread{

	private final static String CLASS = "WaitResvConfirm";
	private int time;
	private ResvState resvState;
	private long initTime;
	private long timeout;
	private boolean reseted = false; //contadores resetados?

	
	public WaitResvConfirmThread(ResvState resvState){
		this.initTime = System.currentTimeMillis();
		this.time = resvState.getRefreshPeriod();
		timeout = initTime + time;
		this.resvState = resvState;
		start();
	}


	public void run(){
		long now = System.currentTimeMillis();
		while( now < timeout && !reseted){
			try {
				if (resvState.isActived()){
					if (resvState.isReceivedResvConf()){
						resvState.setConcludedSignaling(true);
						DataPlane dataPlane = resvState.getDataPlane();
						dataPlane.reserCountersFec();
						reseted = true;

					} 
				} 
			} catch (Exception e) {
				e.printStackTrace();
			} catch (Throwable t) {
				t.printStackTrace();
			}
			now = System.currentTimeMillis();
		}


	}

	
} 