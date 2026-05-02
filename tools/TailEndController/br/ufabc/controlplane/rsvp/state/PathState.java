package br.ufabc.controlplane.rsvp.state;

import gmpls.signaling.RSVPPacket;
import gmpls.signaling.object.Association;
import gmpls.signaling.object.LabelRequest;
import gmpls.signaling.object.LabelSet;
import gmpls.signaling.object.Protection;
import gmpls.signaling.object.RSVPHop;
import gmpls.signaling.object.RSVPObject;
import gmpls.signaling.object.SenderTemplate;
import gmpls.signaling.object.Session;
import gmpls.signaling.object.TimeValues;
import gmpls.signaling.object.explicitroute.ExplicitRouteSubObject;
import gmpls.signaling.object.label.UpstreamLabel;
import gmpls.signaling.object.recordroute.RecordRouteSubObject;

import java.util.Vector;

import br.ufabc.controlplane.ControlPlane;
import br.ufabc.controlplane.RecoveryObject;
import br.ufabc.dataplane.DataPlane;
import br.ufabc.dataplane.DataPlaneException;
import br.ufabc.dataplane.alarms.RsvpNotifyIndication;

public class PathState extends State {
	
	/**
	 * 
	 */
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
	private TimeValues timeValues ;
	private SenderTemplate senderTemplate;
	private Protection protection;
	private Association association;
	private UpstreamLabel upstreamLabel;
	private DataPlane dataPlane;
	private LabelSet labelSet;
	private LabelRequest labelRequest;
	
 //	protected StateListener monitor;
	
	public DataPlane getDataPlane() {
		return dataPlane;
	}


	public void setDataPlane(DataPlane dataPlane) {
		this.dataPlane = dataPlane;
	}


	public PathState(RSVPPacket packet, DataPlane dataPlane) throws Exception{
		this.packet = packet;
		this.dataPlane = dataPlane;
		objects = RecoveryObject.getRsvpObjects(packet);
		setSession();
		setTimeValues();
		setPreviousHop();
		setSenderTemplate();
		setProtectionObject();
		setAssociationObject();
		setName("PathState");
	}
	
	public void setLabelRequest(){
		labelRequest = (LabelRequest) RecoveryObject.getRsvpObjectByClass(objects, RSVPObject.LABEL_REQUEST_CLASS);
	}
	
	private void setLabelSet(){
		labelSet = (LabelSet) RecoveryObject.getRsvpObjectByClass(objects, RSVPObject.LABEL_SET_CLASS);
	}
	
	private void setSenderTemplate(){
		senderTemplate = (SenderTemplate) RecoveryObject.getRsvpObjectByClass(objects, RSVPObject.SENDER_TEMPLATE_CLASS);
	}
	
	public void setProtectionObject(){
		protection = (Protection) RecoveryObject.getRsvpObjectByClass(objects, RSVPObject.PROTECTION_CLASS);
	}
	
	public void setAssociationObject(){
		association = (Association) RecoveryObject.getRsvpObjectByClass(objects, RSVPObject.ASSOCIATION_CLASS);
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
	
	public void setPacket(RSVPPacket packet) {
		this.packet = packet;
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
	
	public int getRefreshPeriod(){
		return timeValues.getRefreshPeriod();
	}

	/**
	 * @return the senderTemplate
	 */
	public SenderTemplate getSenderTemplate() {
		return senderTemplate;
	}

	/**
	 * @param senderTemplate the senderTemplate to set
	 */
	public void setSenderTemplate(SenderTemplate senderTemplate) {
		this.senderTemplate = senderTemplate;
	}
	/**
	 * 
	 * @return the protection object
	 */
	public Protection getProtection() {
		return protection;
	}

	/**
	 * @param the protection object
	 */
	public void setProtection(Protection protection) {
		this.protection = protection;
	}
	
	/**
	 * 
	 * @return the protection object
	 */
	public Association getAssociation() {
		return association;
	}

	/**
	 * @param the protection object
	 */
	public void setAssociation(Association association) {
		this.association = association;
	}
	
	/**
	 * @return the upstreamLabel
	 */
	public UpstreamLabel getUpstreamLabel() {
		return upstreamLabel;
	}

	/**
	 * @param upstreamLabel the upstreamLabel to set
	 */
	public boolean setUpstreamLabel(UpstreamLabel upstreamLabel, boolean isNew) throws DataPlaneException {
		if (upstreamLabel == null)
			return false;
		this.upstreamLabel = upstreamLabel;
		int label = upstreamLabel.getIntLabel();
		String channel = dataPlane.getChannelCBand(label);
		getDataPlane().startTransmission(channel, isNew);
		return true;
				
	}

	/**
	 * @param upstreamLabel the upstreamLabel to set
	 */
	public boolean setUpstreamLabel(UpstreamLabel upstreamLabel) throws DataPlaneException {
		if (upstreamLabel == null)
			return false;
		this.upstreamLabel = upstreamLabel;
		return true;
				
	}

	@Override
	public void sendRsvpNotification(RsvpNotifyIndication notifyIndication) {
		ControlPlane.getInstance().sendRsvpNotifyMessage(this, notifyIndication, getNotifyListeners());
		
	}


	@Override
	public void updateState(RSVPPacket packet) throws Exception {
		this.packet = packet;
		objects = RecoveryObject.getRsvpObjects(packet);
		if(!isActived()){
			setActived(true);
		}
		setSession();
		setTimeValues();
		setPreviousHop();
		setSenderTemplate();
		setLabelSet();
		setLabelRequest();
		
	}


	/**
	 * @return the labelSet
	 */
	public LabelSet getLabelSet() {
		return labelSet;
	}


	/**
	 * @param labelSet the labelSet to set
	 */
	public void setLabelSet(LabelSet labelSet) {
		this.labelSet = labelSet;
	}


	/**
	 * @return the labelRequest
	 */
	public LabelRequest getLabelRequest() {
		return labelRequest;
	}


	/**
	 * @param labelRequest the labelRequest to set
	 */
	public void setLabelRequest(LabelRequest labelRequest) {
		this.labelRequest = labelRequest;
	}
	
	@Override
	public void cancel() {
		update.cancel();
		
	}
	
}
