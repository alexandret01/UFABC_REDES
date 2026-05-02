package br.ufabc.controlplane.rsvp.factory;

import gmpls.signaling.RSVPPacket;
import gmpls.signaling.object.FilterSpec;
import gmpls.signaling.object.FlowSpec;
import gmpls.signaling.object.RSVPHop;
import gmpls.signaling.object.RSVPObject;
import gmpls.signaling.object.SenderTemplate;
import gmpls.signaling.object.Session;
import gmpls.signaling.object.TimeValues;
import gmpls.signaling.object.label.RSVPLabel;

import java.util.Vector;

import util.ByteOperation;
import br.ufabc.controlplane.ControlPlane;
import br.ufabc.controlplane.ControlPlaneException;
import br.ufabc.controlplane.rsvp.state.PathState;
import br.ufabc.controlplane.rsvp.state.ResvState;
import br.ufabc.controlplane.rsvp.state.State;

public class TearMessageFactory extends RSVPPacketFactory {


	public static RSVPPacket create(State state, boolean isDownStream) throws Exception{

		PathState pathState = null;
		ResvState resvState = null;
		RSVPPacket rsvp = null;
		if (state instanceof PathState){
			pathState = (PathState)state;
		} else if (state instanceof ResvState) {
			resvState = (ResvState)state;
		} else {
			throw new ControlPlaneException("TearMessageFactory: Estado não em contrado!");
		}
		//Create the objects.

		//Create a vector for storing the objects.
		Vector<RSVPObject> vector = new Vector<RSVPObject>();
		//Create the SESSION object by RFC 3209
		Session session = state.getSession();
		if (session != null){
			vector.add(session);
		} else {
			throw new ControlPlaneException("TearMessageFactory: Objeto Session não encontrado!");
		}
		
		//Create the RSVP_HOP object
		RSVPHop hop = new RSVPHop(ByteOperation.intToByteArray(ControlPlane.getInstance().getLocalAddressAsInt()),1,RSVPHop.IPV4_TYPE);
		if (hop != null){
			vector.add(hop);
		} else {
			throw new ControlPlaneException("TearMessageFactory: Objeto HOP não foi criado!");
		}

		//Create the TIME_VALUES object
		TimeValues refresh = new TimeValues(state.getRefreshPeriod());
		if (refresh != null)
				vector.add(refresh);
		else {
			throw new ControlPlaneException("TearMessageFactory: Objeto Time Values não foi criado!");
		}
		if (!isDownStream){
			if (resvState != null){
				/*add Flow Spec */
				FlowSpec g709FlowSpec = resvState.getFlowSpec();
				if (g709FlowSpec != null)
					vector.add(g709FlowSpec);
				else {
					throw new ControlPlaneException("TearMessageFactory: Objeto FlowSpec não encontrado!");
				}
				/*add filter spec*/
				FilterSpec filterSpec = resvState.getFilterSpec();
				if (filterSpec != null) 
					vector.add(filterSpec);
				else {
					throw new ControlPlaneException("TearMessageFactory: Objeto FilterSpec não encontrado!");
				}
				/*add Lavel*/
				RSVPLabel label = resvState.getLabel();
				if (label != null)
					vector.add(label);
				else {
					throw new ControlPlaneException("TearMessageFactory: Objeto Label não encontrado!");
				}
//				int previousHop = ByteOperation.byteArrayToInt(state.getPreviousHop().getNodeAddress());
				//Create the packet
				int source = resvState.getSource();
				
				rsvp = getNewRsvpPacket(vector, 4, 32, 192, 2, ControlPlane.getInstance().getLocalAddressAsInt(), 
						source, RSVPPacket.TYPE_RESV_TEAR, 1, 0);
			}
		} else {
		
			if (pathState != null){

				//gets the SENDER_TEMPLATE object by RFC 3209
				SenderTemplate senderTemplate = pathState.getSenderTemplate();
				if (senderTemplate != null) {
					vector.add(senderTemplate);
				} else {
					throw new ControlPlaneException("TearMessageFactory: Objeto Sender Template não foi recuperado do path state!");
				}
//				int previousHop = ByteOperation.byteArrayToInt(state.getPreviousHop().getNodeAddress());
				//Create the packet
				int destination = pathState.getDestination();
				/*System.out.println("PathTear objects:");
			for (RSVPObject obj: vector) {
				System.out.println("class obj :" +obj.getClassNum() + "\n");
				size = size + obj.size();
			}*/

				rsvp = getNewRsvpPacket(vector, 4, 32, 192, 2, ControlPlane.getInstance().getLocalAddressAsInt(), 
						destination, RSVPPacket.TYPE_PATH_TEAR, 1, 0);
			} 
		}
		rsvp.setSendTTL();
		//Copy the objects
		rsvp.addObjects(vector);
		//Compute checksum
		rsvp.computeRSVPChecksum();
		rsvp.computeIPChecksum();
		//Write the packet
//		System.out.println("Writing rsvp.cap!");
//		PCAP pcap = new PCAP("rsvp.pcap");
//		pcap.write(sendData);

		return rsvp;
	}

}
