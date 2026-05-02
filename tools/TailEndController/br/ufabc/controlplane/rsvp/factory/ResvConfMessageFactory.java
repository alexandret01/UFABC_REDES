package br.ufabc.controlplane.rsvp.factory;

import gmpls.signaling.RSVPPacket;
import gmpls.signaling.object.ErrorSpec;
import gmpls.signaling.object.FilterSpec;
import gmpls.signaling.object.FlowSpec;
import gmpls.signaling.object.Label;
import gmpls.signaling.object.RSVPObject;
import gmpls.signaling.object.RecordRoute;
import gmpls.signaling.object.ResvConfirm;
import gmpls.signaling.object.error.IPv4ErrorSpec;
import gmpls.signaling.object.recordroute.IPv4RecordRoute;

import java.util.Vector;

import util.ByteOperation;
import br.ufabc.controlplane.ControlPlane;
import br.ufabc.controlplane.rsvp.state.ResvState;

public class ResvConfMessageFactory extends RSVPPacketFactory{
		
	
	public static RSVPPacket create(ResvState resvState) throws Exception{
		int local = ControlPlane.getInstance().getLocalAddressAsInt();
		Vector<RSVPObject> newVector = new Vector<RSVPObject>();
		
		/*add session*/
		newVector.add(resvState.getSession());
		/*add ErrorSpec*/
		ErrorSpec error = new IPv4ErrorSpec(local, 0, 0, 0);
		newVector.add(error);
		ResvConfirm confirm = new ResvConfirm(ByteOperation.intToByteArray(local), ResvConfirm.IPV4);
		/*add flow spec*/
		FlowSpec flowSpec= resvState.getFlowSpec();
		newVector.add(flowSpec);
		/*add filter spec*/
		FilterSpec filterSpec = resvState.getFilterSpec();
		newVector.add(filterSpec);
		Label label = resvState.getLabel();
		newVector.add(label);
		/*add record route. always after a filter_spec*/
		RecordRoute rro = new RecordRoute();
		IPv4RecordRoute sub = new IPv4RecordRoute( local, 0 );
		rro.push( sub.getData() );
		newVector.add(rro);


		//Create new RSVP Packet
		int previousHop = ByteOperation.byteArrayToInt(resvState.getPreviousHop().getNodeAddress());
		RSVPPacket resvConf = getNewRsvpPacket(newVector, 4, 32, 192, 2, local, previousHop, RSVPPacket.TYPE_RESV_CONF, 1, 0);
		//Copy the objects
		resvConf.addObjects(newVector);
		//Compute checksum
		resvConf.computeRSVPChecksum();
		resvConf.computeIPChecksum();
		return resvConf;
	}
	
	

}
