package br.ufabc.controlplane.rsvp.state;

import br.ufabc.dataplane.DataPlane;


public class WaitResvThread extends Thread{

		private final static String CLASS = "WaitResvConfirm";
		private boolean reseted = false; //contadores resetados?
		private PathStateSender pathState;
		private int time;
		private long initTime;
		private long timeout;
		public WaitResvThread(PathStateSender pathState){
			super("WaitResvThread");
			this.pathState = pathState;
			this.initTime = System.currentTimeMillis();
			this.time = pathState.getRefreshPeriod();
			timeout = initTime + time;
			start();
		}
		
		public void setTimeOut(long timeout){
			this.timeout = timeout;
		}
		
		
		public void run(){
			long now = System.currentTimeMillis();
			while( now < timeout && !reseted){
				try {
					if (pathState.isReceivedResv()){
						pathState.setConcludedSignaling(true);
						DataPlane dataPlane = pathState.getDataPlane();
						dataPlane.reserCountersFec();
						reseted = true;

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
