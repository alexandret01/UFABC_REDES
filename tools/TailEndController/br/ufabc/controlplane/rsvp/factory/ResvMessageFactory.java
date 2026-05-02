package br.ufabc.controlplane.rsvp.factory;

import gmpls.signaling.RSVPPacket;
import gmpls.signaling.object.Label;
import gmpls.signaling.object.LabelSet;
import gmpls.signaling.object.RSVPHop;
import gmpls.signaling.object.RSVPObject;
import gmpls.signaling.object.RecordRoute;
import gmpls.signaling.object.ResvConfirm;
import gmpls.signaling.object.filterspec.LSPTunnelIPv4FilterSpec;
import gmpls.signaling.object.flowspec.G709FlowSpec;
import gmpls.signaling.object.label.RSVPLabel;
import gmpls.signaling.object.recordroute.IPv4RecordRoute;

import java.util.LinkedList;
import java.util.Vector;

import util.ByteOperation;
import br.ufabc.controlplane.ControlPlane;
import br.ufabc.controlplane.conf.Configuracao;
import br.ufabc.controlplane.rsvp.state.PathState;

public class ResvMessageFactory extends RSVPPacketFactory{
		
	
	public static RSVPPacket create(PathState pathState) throws Exception{
		int local = ControlPlane.getInstance().getLocalAddressAsInt();
		Vector<RSVPObject> newVector = new Vector<RSVPObject>();
		
		/*add session*/
		newVector.add(pathState.getSession());
		/*add hop*/
		RSVPHop hop = new RSVPHop(ByteOperation.intToByteArray(local),1,RSVPHop.IPV4_TYPE);
		newVector.add(hop);
		/*add time values*/
		newVector.add(pathState.getTimeValues());
		/*add resv confirm*/
		ResvConfirm confirm = new ResvConfirm(ByteOperation.intToByteArray(local), ResvConfirm.IPV4);
		newVector.add(confirm);
		/*add notify request*/
//		NotifyRequest notifyRequest = new NotifyRequest(1, ByteOperation.intToByteArray(local));
//		newVector.add(notifyRequest);
		/*add flow spec*/
		G709FlowSpec g709FlowSpec = new G709FlowSpec(1,1,1,1);
		newVector.add(g709FlowSpec);
		/*add filter spec*/
		LSPTunnelIPv4FilterSpec filterSpec = new LSPTunnelIPv4FilterSpec(pathState.getSource(), pathState.getIdLSP());
		newVector.add(filterSpec);
		/*Gets the LabelSet*/
		LabelSet labelSet = pathState.getLabelSet();
		Label label = null;
		if (labelSet != null){
			byte[] subchannels = labelSet.getSubChannels();
			LinkedList<Integer> channels = new LinkedList<Integer>();
			int action = labelSet.getAction();
			for (int i = 0 ; i < subchannels.length ; i += 4){
				byte[] channel= new byte[4];
				System.arraycopy(subchannels, i , channel, 0, 4);
				int c = ByteOperation.byteArrayToInt(channel);;
				channels.add(c);
			
			}
			
			/*add label. always after a filter_spec based in FirstFit algorithm*/  
			label = new RSVPLabel(channels.getFirst(), Label.GENERALIZED_LABEL);
		} else {
			/*add label. always after a filter_spec based in FirstFit algorithm*/
//			Configuracao configuracao = new Configuracao();
//			label = new RSVPLabel(configuracao.getLamda(), Label.GENERALIZED_LABEL);
			label = new RSVPLabel(28, Label.GENERALIZED_LABEL);
		}
		
		newVector.add(label);
		/*add record route. always after a filter_spec*/
		RecordRoute rro = new RecordRoute();
		IPv4RecordRoute sub = new IPv4RecordRoute( local, 0 );
		rro.push( sub.getData() );
		newVector.add(rro);


		//Create new RSVP Packet
		int previousHop = ByteOperation.byteArrayToInt(pathState.getPreviousHop().getNodeAddress());
		RSVPPacket resv = getNewRsvpPacket(newVector, 4, 32, 192, 2, pathState.getDestination(), previousHop, RSVPPacket.TYPE_RESV, 1, 0);
		//Copy the objects
		resv.addObjects(newVector);
		//Compute checksum
		resv.computeRSVPChecksum();
		resv.computeIPChecksum();
		return resv;
	}
	
	

}
