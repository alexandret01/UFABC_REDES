package br.ufabc.controlplane.rsvp.state;

import gmpls.signaling.RSVPPacket;
import gmpls.signaling.object.FilterSpec;
import gmpls.signaling.object.FlowSpec;
import gmpls.signaling.object.RSVPHop;
import gmpls.signaling.object.RSVPObject;
import gmpls.signaling.object.Session;
import gmpls.signaling.object.TimeValues;
import gmpls.signaling.object.explicitroute.ExplicitRouteSubObject;
import gmpls.signaling.object.label.RSVPLabel;
import gmpls.signaling.object.recordroute.RecordRouteSubObject;

import java.util.Vector;

import br.ufabc.controlplane.BadFormatObjectException;
import br.ufabc.controlplane.ControlPlane;
import br.ufabc.controlplane.ControlPlaneException;
import br.ufabc.controlplane.RecoveryObject;
import br.ufabc.controlplane.TimeControl;
import br.ufabc.dataplane.DataPlane;
import br.ufabc.dataplane.alarms.RsvpNotifyIndication;

public class ResvState extends State{
	
	/**
	 * 
	 */
	private static final String CLASS = "ResvState";
	private static final long serialVersionUID = 1L;
	private int idLSP;
	private int source;
	private int destination;
	private RSVPPacket packet;
	private Vector<RSVPObject> objects;
	private Vector<RecordRouteSubObject> rroSubobjects;
	private Vector<ExplicitRouteSubObject> eroSubobjects;
	private Session session;
	private RSVPHop previousHop;
	private RSVPLabel label;
//	protected StateListener monitor;
	private TimeValues timeValues;
	private DataPlane dataPlane;
	private FilterSpec filterSpec;
	private FlowSpec flowSpec;
	private TimeControl time = TimeControl.getInstance();
	private Thread wrcThread;
	private boolean receivedResvConf = false;
	
	public ResvState(RSVPPacket packet, DataPlane dataPlane) throws ControlPlaneException{
		this.packet = packet;
		this.dataPlane = dataPlane;
		objects = RecoveryObject.getRsvpObjects(packet);
		setSession();
		setTimeValues();
		setPreviousHop();
		setFlowSpec();
		setFilterSpec();
		
		wrcThread = new WaitResvConfirmThread(this);
		wrcThread.setName("WaitResvConf");
		setName("ResvState");
		
	}
	
	public void setReceivedResvConf(boolean receivedResvConf){
		this.receivedResvConf = receivedResvConf;
	}
	
	public void updateState(RSVPPacket packet) throws ControlPlaneException{
		this.packet = packet;
		objects.clear();
		objects = RecoveryObject.getRsvpObjects(packet);
		setSession();
		setTimeValues();
		setPreviousHop();
		setFlowSpec();
		setFilterSpec();
	}
	
	public int getRefreshPeriod(){
		return timeValues.getRefreshPeriod();
	}
	/**
	 * Remove the Thread that has been waiting for a ResvConfirm Message
	 * */
	private void cancelWaitResvConf(){
		if (wrcThread != null && !wrcThread.isInterrupted()) {
			wrcThread.interrupt();
			wrcThread = null;
		}
	}
	
	
	/**
	 * Sets the FlowSpec Object based into packet received
	 * */
	private void setFlowSpec(){
		flowSpec = (FlowSpec) RecoveryObject.getRsvpObjectByClass(objects, RSVPObject.FLOWSPEC_CLASS);
	}
	
	private void setFilterSpec(){
		filterSpec = (FilterSpec) RecoveryObject.getRsvpObjectByClass(objects, RSVPObject.FILTER_SPEC_CLASS);
	}
	
	private void setPreviousHop(){
		previousHop = (RSVPHop) RecoveryObject.getRsvpObjectByClass(objects, RSVPObject.RSVP_HOP_CLASS);
	}
	private void setTimeValues(){
		timeValues = (TimeValues) RecoveryObject.getRsvpObjectByClass(objects, RSVPObject.TIME_VALUES_CLASS);
		setTimeout(timeValues.getRefreshPeriod());
	}

	private void setSession() {
		session = (Session) RecoveryObject.getRsvpObjectByClass(objects, RSVPObject.SESSION_CLASS);
	}
	
	public Session getSession() {
		return session;
	}
	
	public void setPreviousHop(RSVPHop previousHop) {
		this.previousHop = previousHop;
	}
	
	public RSVPHop getPreviousHop() {
		return previousHop;
	}

	public RSVPPacket getPacket() {
		return packet;
	}
	
	public void setPacket(RSVPPacket packet) throws BadFormatObjectException {
		this.packet = packet;
		objects = RecoveryObject.getRsvpObjects(packet);
		setSession();
		setTimeValues();
		setPreviousHop();
	}
	
	public int getIdLSP() {
		return idLSP;
	}
	
	public void setIdLSP(int idLSP) {
		this.idLSP = idLSP;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public int getDestination() {
		return destination;
	}

	public void setDestination(int destination) {
		this.destination = destination;
	}

	public Vector<RSVPObject> getObjects() {
		return objects;
	}

	public void setObjects(Vector<RSVPObject> objects) {
		this.objects = objects;
	}

	public Vector<RecordRouteSubObject> getRroSubobjects() {
		return rroSubobjects;
	}

	public void setRroSubobjects(Vector<RecordRouteSubObject> rroSubobjects) {
		this.rroSubobjects = rroSubobjects;
	}

	public Vector<ExplicitRouteSubObject> getEroSubobjects() {
		return eroSubobjects;
	}

	public void setEroSubobjects(Vector<ExplicitRouteSubObject> eroSubobjects) {
		this.eroSubobjects = eroSubobjects;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	

	/**
	 * @return the RSVP Label
	 */
	public RSVPLabel getLabel() {
		return label;
	}
	
	/**
	 * @param label the RSVP Label to set
	 */
	
	public void setLabel(RSVPLabel label) {
		this.label = label;
	}

	/**
	 * @param label the RSVP Label to set
	 */
	public boolean setLabel(RSVPLabel label, boolean isNew) throws Exception{
		if (label == null) {
			return false;
		}
		this.label = label;
		if (dataPlane != null){
			String channel = dataPlane.getChannelCBand(label.getIntLabel());
			dataPlane.startTransmission(channel, isNew);
		} else {
			throw new ControlPlaneException("DataPlane null no método setLabel() da classe ResvState");
		}
		
		return true;
	}
	
	
	public DataPlane getDataPlane() {
		return dataPlane;
	}

	public void setDataPlane(DataPlane dataPlane) {
		this.dataPlane = dataPlane;
	}



	public FilterSpec getFilterSpec() {
		return filterSpec;
	}



	public FlowSpec getFlowSpec() {
		return flowSpec;
	}



	@Override
	public void sendRsvpNotification(RsvpNotifyIndication notifyIndication)  {
		ControlPlane.getInstance().sendRsvpNotifyMessage(this, notifyIndication, getNotifyListeners());
		
	}

	/**
	 * @return the receivedResvConf
	 */
	public boolean isReceivedResvConf() {
		return receivedResvConf;
	}

	/**
	 * @return the timeValues
	 */
	public TimeValues getTimeValues() {
		return timeValues;
	}

	/**
	 * @param timeValues the timeValues to set
	 */
	public void setTimeValues(TimeValues timeValues) {
		this.timeValues = timeValues;
	}

	@Override
	public void cancel() {
		cancelWaitResvConf();
//		this.wrcThread.interrupt();
		update.cancel();
		
	}
}

