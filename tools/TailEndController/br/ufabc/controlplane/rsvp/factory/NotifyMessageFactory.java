package br.ufabc.controlplane.rsvp.factory;

import gmpls.signaling.RSVPPacket;
import gmpls.signaling.object.ErrorSpec;
import gmpls.signaling.object.FilterSpec;
import gmpls.signaling.object.FlowSpec;
import gmpls.signaling.object.Label;
import gmpls.signaling.object.RSVPObject;
import gmpls.signaling.object.RecordRoute;
import gmpls.signaling.object.SenderTemplate;
import gmpls.signaling.object.Session;
import gmpls.signaling.object.recordroute.IPv4RecordRoute;

import java.util.Vector;

import br.ufabc.controlplane.ControlPlane;
import br.ufabc.controlplane.rsvp.state.PathState;
import br.ufabc.controlplane.rsvp.state.ResvState;
import br.ufabc.controlplane.rsvp.state.State;

public class NotifyMessageFactory extends RSVPPacketFactory{

	
//	public RSVPPacket create(){
//		int offset = 28;
//	}

	public static RSVPPacket create(int target, State state, ErrorSpec errorSpec) throws Exception{
		int local = ControlPlane.getInstance().getLocalAddressAsInt();

		//Create a vector for storing the objects.
		Vector<RSVPObject> vector = new Vector<RSVPObject>();
		//Create the SESSION object by RFC 3209
		Session session = state.getSession();
		vector.add(session);
		
		//Addition the ErroSpec object
		
		vector.add(errorSpec);
		
		//Create the SENDER_TEMPLATE object by RFC 3209
		if (state instanceof PathState){
			SenderTemplate senderTemplate = ((PathState)state).getSenderTemplate();
			vector.add(senderTemplate);
		} else if (state instanceof ResvState){
			FlowSpec flowSpec = ((ResvState)state).getFlowSpec();
			vector.add(flowSpec);
			FilterSpec filterSpec = (FilterSpec)((ResvState)state).getFilterSpec();
			vector.add(filterSpec);
		
			Label label = ((ResvState)state).getLabel();
			vector.add(label);
			RecordRoute rro = new RecordRoute();
			IPv4RecordRoute sub = new IPv4RecordRoute( local, 0 );
			rro.push( sub.getData() );
			vector.add(rro);
		}
		
		
		//Create the packet
		RSVPPacket notify = getNewRsvpPacket(vector, 4, 32, 192, 2,local, target, RSVPPacket.TYPE_NOTIFY, 1, 0);
		notify.setSendTTL();
		//Copy the objects
		notify.addObjects(vector);
		//Compute checksum
		notify.computeRSVPChecksum();
		notify.computeIPChecksum();
		//Write the packet
//		System.out.println("Writing rsvp.cap!");
//		PCAP pcap = new PCAP("rsvp.pcap");
//		pcap.write(sendData);

		return notify;
	}
	

}
