package br.ufabc.controlplane.rsvp.state;

import gmpls.signaling.RSVPPacket;

import java.util.TimerTask;
import java.util.logging.Level;

import br.ufabc.controlplane.ControlPlane;
import br.ufabc.dataplane.DataPlane;

public class PathStateSender extends PathState{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Timer task that is run to schedule
	 */
	private PathUpdate pathUpdate;
	private boolean receivedResv = false;
	private WaitResvThread wrt;


	public PathStateSender(RSVPPacket packet, DataPlane dataPlane) throws Exception {
		super(packet, dataPlane);
		pathUpdate = new PathUpdate(4, this);
		wrt = new WaitResvThread(this);
		setName("PathStateSender");
	}


	/**
	 * set up a packet to send later
	 * @param delayInMillis how long to wait
	 */
	public void setDelaytoRefresh(long delayInMillis){
		timer.scheduleAtFixedRate(pathUpdate, delayInMillis, delayInMillis );
	}

	
	public void setReceivedResv(boolean receivedResv) {
		this.receivedResv = receivedResv;
		pathUpdate.setReceivedResv(receivedResv);
	}
	
	/**
	 * @return the receivedResv
	 */
	public boolean isReceivedResv() {
		return receivedResv;
	}

//	public void cancelPathUpdate(){
//		pathUpdate.cancel();
//	}
	
	@Override
	public void cancel() {
		this.pathUpdate.cancel();
		update.cancel();
		
	}

}

/**
 * inner class, run method will be invoked when time is up.
 */
class PathUpdate extends TimerTask{

	private boolean receivedResv = false;
	private int countTimes = 0;
	private int maxTimes;
	private PathState pathState;
	private boolean reseted = false;

	/**
	 * executed when time is up.
	 */

	public PathUpdate(int maxTimes, PathState pathState){
		this.maxTimes = maxTimes;
		this.pathState = pathState;
	}

	public void run(){
		try {
			if (receivedResv){
				countTimes++;
				if (pathState.isActived()){
					ControlPlane.getInstance().send(pathState.getPacket());
					pathState.setTimeout(ControlPlane.getInstance().getTimeout());
					receivedResv = false;
					countTimes = 0;
					/*if (!reseted){
						ControlPlane.getInstance().getDataPlane().reserCountersFec();
						reseted = true;
					}*/
				} 
			} else {
				if (countTimes < maxTimes){
					countTimes++;
//					System.out.println("enviando path message com " + delay + " ms de atraso");
					ControlPlane.getInstance().send(pathState.getPacket());
					receivedResv = false;
				} else {
					if (pathState != null)
						pathState.setActived(false);
					if (ControlPlane.getInstance().getDataPlane().isTransmiting()){
						ControlPlane.getInstance().log(this.getClass().getName(), Level.WARNING, "Sending Path Tear");
			
						ControlPlane.getInstance().removeLSP(pathState.getIdLSP());
					} else {
						ControlPlane.getInstance().removeStates(pathState.getIdLSP());
					}
					this.cancel();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}


	public void setReceivedResv(boolean receivedResv) {
		this.receivedResv = receivedResv;
	}

	
} 

