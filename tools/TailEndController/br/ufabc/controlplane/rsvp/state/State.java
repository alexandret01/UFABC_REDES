package br.ufabc.controlplane.rsvp.state;

import gmpls.signaling.RSVPPacket;
import gmpls.signaling.object.RSVPHop;
import gmpls.signaling.object.Session;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.logging.Level;

import util.ByteOperation;
import br.ufabc.controlplane.ControlPlane;
import br.ufabc.controlplane.TimeControl;
import br.ufabc.dataplane.DataPlane;
import br.ufabc.dataplane.alarms.RsvpNotifyIndication;

public abstract class State implements Serializable{

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -1005092441145146449L;
	
	TimeControl timer;
	protected RemoveState update;
	private boolean actived = false;
	private List<Integer> notifyListeners;
	private boolean concludedSignaling = false;
	private String name;

	public State(){
		timer = TimeControl.getInstance();
		update = new RemoveState(this);
		notifyListeners = new ArrayList<Integer>();
	}
	
	/**
	 * Updates the Object's references into state
	 * */
	public abstract void updateState(RSVPPacket packet) throws Exception;
	
	/**
	 * Returns the Timevalues Object's value 
	 * */
	public abstract int getRefreshPeriod();

	/**
	 * Returns a RSVP Hop object that contains the previous hop address 
	 * */
	public abstract RSVPHop getPreviousHop();
	/**
	 * Returns a Session Object
	 * */
	public abstract Session getSession();
	public abstract int getIdLSP();
	
	/**
	 * The list of address witch this element must inform about a fail into LSP
	 * */
	public List<Integer> getNotifyListeners(){
		return notifyListeners;
	}
	
	/**
	 * Sets a address informed on NotifyRequest Object 
	 * @param address The IPV4 address to include in the list of Listeners
	 */
	public void setListener(int address){
		
		if (!notifyListeners.contains(address)){
			try {
				System.out.println("Adicionou listener" + 
						InetAddress.getByAddress(ByteOperation.intToByteArray(address)).getHostAddress());
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			notifyListeners.add(address);
		}
	}
	
	public abstract DataPlane getDataPlane();

	/**
	 * @param timeout how long to wait
	 */
	public void setTimeout(long timeout) {
		this.update.cancel();
		this.update = new RemoveState(this);
		timer.schedule(update, timeout);
		
		
	}
	
	/**
	 * @return the actived
	 */
	public boolean isActived() {
		return actived;
	}

	/**
	 * @param actived the actived to set
	 */
	public void setActived(boolean actived) {
		this.actived = actived;
	}

	public abstract void sendRsvpNotification(RsvpNotifyIndication notifyIndication);

	/**
	 * @return the concludedSignaling flag indicating that signaling stage is completed
	 */
	public boolean isConcludedSignaling() {
		return concludedSignaling;
	}

	/**
	 * @param concludedSignaling the concludedSignaling to set
	 */
	public void setConcludedSignaling(boolean concludedSignaling) {
		this.concludedSignaling = concludedSignaling;
	} 
	
	public abstract void cancel();

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}

/**
 * inner class, run method will be invoked when time is up.
 */
class RemoveState extends TimerTask{


	private State state;

	/**
	 * executed when time is up.
	 */

	public RemoveState(State state){
		this.state = state;
	}


	public void run(){
		try {
			if (state != null) {
				if (state.isActived()){
					/*if (state instanceof PathState){
						state.setActived(false);
						ControlPlane.getInstance().removePathState(state.getIdLSP());
					} else if (state instanceof ResvState) {
						state.setActived(false);
						ControlPlane.getInstance().removeResvState(state.getIdLSP());
					}*/
					ControlPlane.getInstance().log("RemoveState", Level.WARNING, "Removendo estados por: "+state.getName()+", tempo expirado para atualização");
					if (ControlPlane.getInstance().getDataPlane().isTransmiting()){
						ControlPlane.getInstance().removeLSP(state.getIdLSP());
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


}