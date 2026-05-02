package br.ufabc.controlplane.rsvp.factory;

import gmpls.signaling.RSVPPacket;
import gmpls.signaling.object.ErrorSpec;
import gmpls.signaling.object.RSVPObject;
import gmpls.signaling.object.Session;

import java.util.Vector;

import util.ByteOperation;

import br.ufabc.controlplane.ControlPlane;
import br.ufabc.controlplane.ControlPlaneException;
import br.ufabc.controlplane.rsvp.state.ResvState;

public class ResvErrMessageFactory extends RSVPPacketFactory{

	
//	public RSVPPacket create(){
//		int offset = 28;
//	}

	public static RSVPPacket create(ResvState resvState, ErrorSpec errorSpec) throws ControlPlaneException{
		//Create the objects.

		//Create a vector for storing the objects.
		Vector<RSVPObject> vector = new Vector<RSVPObject>();
		//Create the SESSION object by RFC 3209
		Session session = resvState.getSession();
		vector.add(session);
		//Addition the ErroSpec object
		
		vector.add(errorSpec);
		
		//Create the SENDER_TEMPLATE object by RFC 3209
//		SenderTemplate senderTemplate = resvState.getSenderTemplate();
//		vector.add(senderTemplate);
		
		int local = ControlPlane.getInstance().getLocalAddressAsInt();
		int previousHop = ByteOperation.byteArrayToInt(resvState.getPreviousHop().getNodeAddress());
		//Create the packet
		RSVPPacket resvErr = getNewRsvpPacket(vector, 4, 32, 192, 2,local, previousHop, RSVPPacket.TYPE_RESV_ERR, 1, 0);
		resvErr.setSendTTL();
		//Copy the objects
		resvErr.addObjects(vector);
		//Compute checksum
		resvErr.computeRSVPChecksum();
		resvErr.computeIPChecksum();
		//Write the packet
//		System.out.println("Writing rsvp.cap!");
//		PCAP pcap = new PCAP("rsvp.pcap");
//		pcap.write(sendData);

		return resvErr;
	}
	

}
