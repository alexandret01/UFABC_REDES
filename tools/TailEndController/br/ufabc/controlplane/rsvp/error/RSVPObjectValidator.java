package br.ufabc.controlplane.rsvp.error;

import gmpls.signaling.object.ErrorSpec;
import gmpls.signaling.object.Label;
import gmpls.signaling.object.LabelRequest;
import gmpls.signaling.object.LabelSet;
import gmpls.signaling.object.RSVPObject;
import gmpls.signaling.object.RecordRoute;
import gmpls.signaling.object.SenderTemplate;
import gmpls.signaling.object.Session;
import gmpls.signaling.object.error.IPv4ErrorSpec;
import gmpls.signaling.object.error.IPv4InterfaceIndexErrorSpec;
import gmpls.signaling.object.labelrequest.GeneralizedLabelRequest;
import gmpls.signaling.object.session.LSPTunnelIPv4Session;

import java.net.SocketException;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;

import br.ufabc.controlplane.ControlPlane;
import br.ufabc.controlplane.net.NetworkAddressOperation;
/**
 * @author Alaelson Jatobá
 * @version 1.0
 */
public class RSVPObjectValidator {

	private static final String CLASS = "RSVPObjectValidator";
	private HashMap<Integer, String> lspEncodingTypeSupported;
	private HashMap<Integer, String> lspSwitchingTypeSupported;
	private HashMap<Integer, String> lspGPIDSupported;
	private static int localAddress;

	/**
	 * Validates each Object in a RSVP Packet
	 * */
	public RSVPObjectValidator() throws SocketException{
		this.localAddress = NetworkAddressOperation.getLocalAddressAsInt();
		lspEncodingTypeSupported = new HashMap<Integer, String>();
		lspSwitchingTypeSupported = new HashMap<Integer, String>();
		lspGPIDSupported = new HashMap<Integer, String>();
		encodingTypes();
		switchingTypes();
		gPIDTypes();
	}

	/**
	 * Creates a new IPV4ErrorSpec object with the specified parameters.
	 * @param flags The flags.
	 * @param code The error code.
	 * @param value The error value.

	 */
	public static ErrorSpec getIPv4ErrorSpec(int flags, int code, int value){
		return new IPv4ErrorSpec(localAddress, flags, code, value);
	}

	/**
	 * Creates a new IPv4InterfaceIndexErrorSpec object with the specified parameters. 
	 * if tlvs parameter is null, will be return a IPV4ErrorSpec object
	 * @param flags The flags.
	 * @param code The error code.
	 * @param value The error value.
	 * @param tlvs The set of TLVs.
	 */
	private ErrorSpec getIPv4ErrorSpec(int flags, int code, int value, byte[] tlvs){
		if (tlvs == null) 
			return getIPv4ErrorSpec(flags, code, value);
		return new IPv4InterfaceIndexErrorSpec(localAddress, flags, code, value, tlvs);
	}

	/**
	 * Checks if the label request is supported for interfaces of router
	 * @param labelRequest The Label Request Object
	 * @return the TLVs with the errors founded
	 * */
	private byte[] checkInterfaces(LabelRequest labelRequest){
		//TODO checking the parameters in the interfaces and return the TLVs if necessary
		return null;
	}

	/**
	 * check the integrity of the attributes of Label Request object
	 * @param labelRequest The Label Request Object
	 * @param destination The Destination Address 
	 * */
	public ErrorSpec checkIntegrity(LabelRequest labelRequest, int destination){
		int flags = 0; // No flag
		int code = 0;
		int value = 0;
		ErrorSpec err = null;
		switch (labelRequest.getCType()){
		//C-Type = 1
		case (LabelRequest.WITHOUT_LABEL_RANGE):
			break;
		//C-Type = 2
		case (LabelRequest.WITH_ATM_LABEL_RANGE):
			break;
		//C-Type = 3
		case (LabelRequest.WITH_FRAME_RELAY_LABEL_RANGE):
			break;
		//C-Type = 4
		case (LabelRequest.GENERALIZED_LABEL_REQUEST):
			if( labelRequest != null ) {
				
				if (!lspEncodingTypeSupported.containsKey(((GeneralizedLabelRequest) labelRequest).getLSPEncodingType())){
					System.out.println("Tipo de codificação não suportada!");
					//retornar mensagem patherr com indicador "Routing Problem/Unsupported Enconding"
					code = ErrorSpec.ERROR_CODE.ROUTING_PROBLEM.value();
					value = ErrorSpec.ROUTING_PROBLEM.USUPPORTED_ENCODING.value();
					err = getIPv4ErrorSpec(flags, code, value, checkInterfaces(labelRequest));

				}
				if (!lspSwitchingTypeSupported.containsKey(((GeneralizedLabelRequest) labelRequest).getSwitchingType())){
					System.out.println("Tipo de comutação não suportada!");
					//retornar mensagem patherr com indicador "Routing Problem/Switching Type"
					code = ErrorSpec.ERROR_CODE.ROUTING_PROBLEM.value();
					value = ErrorSpec.ROUTING_PROBLEM.SWITCHING_TYPE.value();
					err = getIPv4ErrorSpec(flags, code, value, checkInterfaces(labelRequest));
				}
				if (!lspGPIDSupported.containsKey(((GeneralizedLabelRequest) labelRequest).getGPID())){
					System.out.println("Tipo de codificação de largura de banda não suportada!");
					//retornar mensagem patherr com indicador "Routing Problem/Unsupported L3PID"
					//TODO só deve ser verificado no destino
					if (localAddress == destination){
						code = ErrorSpec.ERROR_CODE.ROUTING_PROBLEM.value();
						value = ErrorSpec.ROUTING_PROBLEM.UNSUPPORTED_L3PID.value();
						err = getIPv4ErrorSpec(flags, code, value, checkInterfaces(labelRequest));
					}
				}
			}

		break;


		}
		return err;

	} 

	private void encodingTypes(){
		lspEncodingTypeSupported.put(1, "Packet");
		lspEncodingTypeSupported.put(7, "Digital Wrapper");
		lspEncodingTypeSupported.put(8, "Lambda (photonic)");  
	}

	private void switchingTypes(){
		lspSwitchingTypeSupported.put(GeneralizedLabelRequest.LAMBDA_SWITCH_CAPABLE, "Lambda Switching Capable");
	}

	private void gPIDTypes(){
		lspGPIDSupported.put(37, "Lambda");
		lspGPIDSupported.put(36, "Digital Wrapper");
	}

	/**
	 * check the integrity of the attributes of session object
	 * @param session The Session Object
	 * */
	public static ErrorSpec checkIntegrity(Session session){
		switch (session.getCType()){
		case (Session.IPV4):
		case (Session.IPV6):
		case (Session.IPV4_GPI):
		case (Session.IPV6_GPI):
		case (Session.LSP_TUNNEL_IPV4):
			session = (LSPTunnelIPv4Session)session;
		case (Session.LSP_TUNNEL_IPV6):
		case (Session.GENERIC_AGGREGATE_IPV4):
		case (Session.GENERIC_AGGREGATE_IPV6):
		}

		return null;
	} 

	/**
	 * check the integrity of the attributes of the Sender Template object
	 * @param st The Sender Template Object
	 * */
	public ErrorSpec checkIntegrity(SenderTemplate st){
		//TODO
		int flags = 0; // No flag
		int code = 0;
		int value = 0;
		ErrorSpec err = null;
		if (st == null)
			err = getIPv4ErrorSpec( flags, code, value ); 
		switch (st.getCType()){
		case SenderTemplate.LSP_TUNNEL_IPV4:

		}

		return err;
	} 

	/**
	 * check the integrity of the attributes of session object
	 * @param session The Session Object
	 * */
	public ErrorSpec checkIntegrity(RecordRoute recordRoute){
		switch (recordRoute.getClassNum()){
		case (Session.IPV4):
		case (Session.IPV6):
		case (Session.IPV4_GPI):
		case (Session.IPV6_GPI):
		case (Session.LSP_TUNNEL_IPV4):
		case (Session.LSP_TUNNEL_IPV6):
		case (Session.GENERIC_AGGREGATE_IPV4):
		case (Session.GENERIC_AGGREGATE_IPV6):
		}

		return null;
	} 

	/**
	 * check the integrity of the attributes of the RSVP Label object
	 * @param label The RSVP Label Object
	 * */
	public ErrorSpec checkIntegrity(Label label){
		int flags = 0; // No flag
		int code = ErrorSpec.ERROR_CODE.ROUTING_PROBLEM.value();
		int value = 0;
		ErrorSpec err = null;
		if ( label.getClassNum() == RSVPObject.RSVP_LABEL_CLASS ||  
				label.getClassNum() == RSVPObject.UPSTREAM_LABEL_CLASS) {
			if ( label.getLabel().length == 0){
				System.out.println("There isn't any label!");
				value = ErrorSpec.ROUTING_PROBLEM.MPLS_LABEL_ALLOCATION_FAILURE.value();
				err = getIPv4ErrorSpec(flags, code, value);
			}
			switch ( label.getCType() ) {
			//C-type = 1
			case (Label.TYPE_1_LABEL):
				break;
			//C-Type = 2
			case (Label.GENERALIZED_LABEL):
			
				if ( !ControlPlane.getInstance().getSupportedLabels().contains( label.getIntLabel() ) ) {
					ControlPlane.getInstance().log(CLASS, Level.WARNING, "Label não suportado");
					if ( label.getClassNum() == RSVPObject.RSVP_LABEL_CLASS ) {
						// label is a RSVP Label Object
						value = ErrorSpec.ROUTING_PROBLEM.MPLS_LABEL_ALLOCATION_FAILURE.value();
					} else {
						// label is an Upstream Label Object
						value = ErrorSpec.ROUTING_PROBLEM.UNACCEPTABLE_LABEL_VALUE.value();
					}
				} 
				
				if ( label.getLength() != Label.SINGLE_WORD_LENGTH )  {
					ControlPlane.getInstance().log(CLASS, Level.WARNING, "Tamanho do label diferente de 32 bits");
					if ( label.getClassNum() == RSVPObject.RSVP_LABEL_CLASS ) {
						// label is a RSVP Label Object
						value = ErrorSpec.ROUTING_PROBLEM.MPLS_LABEL_ALLOCATION_FAILURE.value();
					} else {
						// label is an Upstream Label Object
						value = ErrorSpec.ROUTING_PROBLEM.UNACCEPTABLE_LABEL_VALUE.value();
					}
				}
				
			break;
			//C-type = 3
			case (Label.WAVEBAND_SWITCHING_LABEL):
				//TODO Verity if the waveband is compatible

				break;


			}
			
			
			if (value != 0)
				err = getIPv4ErrorSpec( flags, code, value );
			
		} else if ( label.getClassNum() == RSVPObject.SUGGESTED_LABEL_CLASS){
			/* TODO
			 * Errors in received Suggested_Label objects MUST be ignored.  
			 * This includes any received inconsistent or unacceptable values.
			 * 
			 * Per [RFC3471], if a downstream node passes a label value that differs
			 * from the suggested label upstream, the upstream LSR MUST either
			 * reconfigure itself so that it uses the label specified by the
			 * downstream node or generate a ResvErr message with a "Routing 
			 * problem/Unacceptable label value" indication.  Furthermore, an
			 * ingress node SHOULD NOT transmit data traffic using a suggested label
			 * until the downstream node passes a corresponding label upstream.
			 * 
			 * */		
		}

		return err;
	}

	

	/**
	 * check the integrity of the attributes of Label Set object
	 * @param labelSet The Label Set Object
	 * */
	public ErrorSpec checkIntegrity(LabelSet labelSet){
		int flags = 0; // No flag
		int code = ErrorSpec.ERROR_CODE.ROUTING_PROBLEM.value();
		int value = ErrorSpec.ROUTING_PROBLEM.LABEL_SET.value();

		ErrorSpec err = null;

		Vector<Label> labels = new Vector<Label>();

		byte[] subchannels = labelSet.getSubChannels();

		if (subchannels.length == 0) {
			err = getIPv4ErrorSpec(flags, code, value);
		}

		//parsing into subchannels
		//		try {
		//			for (int offset = LabelSet.OFFSET_SUBCHANNELS; offset < labelSet.getLength() ; ) {
		//				byte[] lengthInBytes = new byte[4];
		//				System.arraycopy(subchannels, offset, lengthInBytes, 2, 2);
		//				int length = ByteOperation.byteArrayToInt(lengthInBytes);
		//				
		//				byte[] label = new byte[length]; 
		//				labels.add(new RSVPLabel(label));
		//				
		//				offset += length;
		//			}
		//		} catch (Exception e) {
		//			return err = getIPv4ErrorSpec(flags, code, value);
		//		} 

		return err;
	}

}
