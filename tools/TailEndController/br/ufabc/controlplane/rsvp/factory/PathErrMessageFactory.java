package br.ufabc.controlplane.rsvp.factory;

import gmpls.signaling.RSVPPacket;
import gmpls.signaling.object.ErrorSpec;
import gmpls.signaling.object.RSVPObject;
import gmpls.signaling.object.SenderTemplate;
import gmpls.signaling.object.Session;

import java.util.Vector;

import util.ByteOperation;

import br.ufabc.controlplane.ControlPlane;
import br.ufabc.controlplane.rsvp.state.PathState;

public class PathErrMessageFactory extends RSVPPacketFactory{

	
//	public RSVPPacket create(){
//		int offset = 28;
//	}

	public static RSVPPacket create(PathState pathState, ErrorSpec errorSpec) throws Exception{
		//Create the objects.

		//Create a vector for storing the objects.
		Vector<RSVPObject> vector = new Vector<RSVPObject>();
		//Create the SESSION object by RFC 3209
		Session session = pathState.getSession();
		vector.add(session);
		//Addition the ErroSpec object
		
		vector.add(errorSpec);
		
		//Create the SENDER_TEMPLATE object by RFC 3209
		SenderTemplate senderTemplate = pathState.getSenderTemplate();
		if (senderTemplate != null)
			vector.add(senderTemplate);
		
		int local = ControlPlane.getInstance().getLocalAddressAsInt();
		int previousHop = ByteOperation.byteArrayToInt(pathState.getPreviousHop().getNodeAddress());
		//Create the packet
		RSVPPacket pathErr = getNewRsvpPacket(vector, 4, 32, 192, 2,local, previousHop, RSVPPacket.TYPE_PATH_ERR, 1, 0);
		pathErr.setSendTTL();
		//Copy the objects
		pathErr.addObjects(vector);
		//Compute checksum
		pathErr.computeRSVPChecksum();
		pathErr.computeIPChecksum();
		//Write the packet
//		System.out.println("Writing rsvp.cap!");
//		PCAP pcap = new PCAP("rsvp.pcap");
//		pcap.write(sendData);

		return pathErr;
	}
	

}
