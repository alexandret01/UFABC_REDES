package br.ufabc.controlplane.rsvp.factory;

import gmpls.common.PCAP;
import gmpls.signaling.RSVPPacket;
import gmpls.signaling.object.Association;
import gmpls.signaling.object.Label;
import gmpls.signaling.object.NotifyRequest;
import gmpls.signaling.object.Protection;
import gmpls.signaling.object.RSVPHop;
import gmpls.signaling.object.RSVPObject;
import gmpls.signaling.object.RecordRoute;
import gmpls.signaling.object.TimeValues;
import gmpls.signaling.object.label.UpstreamLabel;
import gmpls.signaling.object.labelrequest.GeneralizedLabelRequest;
import gmpls.signaling.object.recordroute.IPv4RecordRoute;
import gmpls.signaling.object.sendertemplate.LSPTunnelIPv4SenderTemplate;
import gmpls.signaling.object.session.LSPTunnelIPv4Session;

import java.util.Vector;

import util.ByteOperation;
import br.ufabc.controlplane.BadFormatObjectException;
import br.ufabc.controlplane.ControlPlane;
import br.ufabc.controlplane.RecoveryObject;
import br.ufabc.controlplane.rsvp.state.PathState;
import br.ufabc.dataplane.DataPlane;

public class PathMessageFactory extends RSVPPacketFactory{

	
//	public RSVPPacket create(){
//		int offset = 28;
//	}
	
	
	
	
	
	public static RSVPPacket create(int source, int destination, int lspID, boolean bidirection, Vector<RSVPObject> protectInfo) throws Exception{
		//Create the objects.
		//Create a vector for storing the objects.
		Vector<RSVPObject> vector = new Vector<RSVPObject>(); 
		//Create the SESSION object by RFC 3209
		
		LSPTunnelIPv4Session session = new LSPTunnelIPv4Session(destination,lspID,source);
		
		vector.add(session);
		//Create the RSVP_HOP object
		RSVPHop hop = new RSVPHop(ByteOperation.intToByteArray(source),1,RSVPHop.IPV4_TYPE);
		vector.add(hop);
		//Create the TIME_VALUES object
		TimeValues refresh = new TimeValues(ControlPlane.getInstance().getTimeout());
		vector.add(refresh);
		//Create the LABEL_REQUEST object
		GeneralizedLabelRequest label = new GeneralizedLabelRequest(GeneralizedLabelRequest.LAMBDA_ENCODING, 
				GeneralizedLabelRequest.LAMBDA_SWITCH_CAPABLE, GeneralizedLabelRequest.LAMBDA_PID);
		NotifyRequest notifyRequest = new NotifyRequest(1, ByteOperation.intToByteArray(source));
		vector.add(notifyRequest);
		vector.add(label);
		//Create the SENDER_TEMPLATE object by RFC 3209
		LSPTunnelIPv4SenderTemplate senderTemplate = new LSPTunnelIPv4SenderTemplate(source, lspID);
		vector.add(senderTemplate);
		//Create the PROTECTION_OBJECT by RFC 4872
		//Protection protection = new Protection(true,true,true,true,1,4);
		//vector.add(protection);
		//Create the ASSOCIATION_OBJECT by RFC 4872
		//Association association = new Association(0,lspID,source);
		//vector.add(association);
		vector.addAll(protectInfo);
//		SenderTSpec senderTSpec = new SenderTSpec() {
//		};
		// startDataPlane
		// path 172.17.36.20
		
		RecordRoute rro = new RecordRoute();
		IPv4RecordRoute sub = new IPv4RecordRoute( source, 0 );
		rro.push( sub.getData() );
		vector.add(rro);
		UpstreamLabel upstream = null;
		DataPlane dataPlane = ControlPlane.getInstance().getDataPlane();
		if (dataPlane == null)
			throw new PacketFactoryException("Não foi possível criar mensagem PATH pois o plano de dados não foi initicado");
		if(bidirection){
			//Gets servidor of padtec equipaments
				
			int numberChannel = 0;
			
			String channel = dataPlane.getChannel();
			numberChannel = Integer.parseInt(channel.substring(1, 3));
		
			if (numberChannel >= 21 || numberChannel <= 60){
				upstream = new UpstreamLabel(numberChannel, Label.GENERALIZED_LABEL);
			}
			
		}
		
		
		if (upstream != null)
			vector.add(upstream);
		
		
		
		
		
		//Create new RSVP Packet
		RSVPPacket rsvp = getNewRsvpPacket(vector, 4, 32, 192, 2, source, destination, RSVPPacket.TYPE_PATH, 1, 0);	
		//Copy the objects
		rsvp.addObjects(vector);
		//Compute checksum
		rsvp.computeRSVPChecksum();
		rsvp.computeIPChecksum();
//		//Write the packet
//		System.out.println("Writing rsvp.cap!");
//		PCAP pcap = new PCAP("rsvp.pcap");
//		pcap.write(rsvp.getData());

		return rsvp;
	}
	
	
	

}
