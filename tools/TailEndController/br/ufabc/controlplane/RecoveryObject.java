package br.ufabc.controlplane;

import gmpls.signaling.RSVPPacket;
import gmpls.signaling.object.AdSpec;
import gmpls.signaling.object.AdminStatus;
import gmpls.signaling.object.Association;
import gmpls.signaling.object.ErrorSpec;
import gmpls.signaling.object.ExplicitRoute;
import gmpls.signaling.object.FilterSpec;
import gmpls.signaling.object.FlowSpec;
import gmpls.signaling.object.Hello;
import gmpls.signaling.object.Integrity;
import gmpls.signaling.object.Label;
import gmpls.signaling.object.LabelRequest;
import gmpls.signaling.object.LabelSet;
import gmpls.signaling.object.NotifyRequest;
import gmpls.signaling.object.Protection;
import gmpls.signaling.object.RSVPHop;
import gmpls.signaling.object.RSVPObject;
import gmpls.signaling.object.RecordRoute;
import gmpls.signaling.object.ResvConfirm;
import gmpls.signaling.object.Scope;
import gmpls.signaling.object.SenderTSpec;
import gmpls.signaling.object.SenderTemplate;
import gmpls.signaling.object.Session;
import gmpls.signaling.object.Style;
import gmpls.signaling.object.TimeValues;
import gmpls.signaling.object.error.IPv4ErrorSpec;
import gmpls.signaling.object.error.IPv4InterfaceIndexErrorSpec;
import gmpls.signaling.object.error.IPv6ErrorSpec;
import gmpls.signaling.object.error.IPv6InterfaceIndexErrorSpec;
import gmpls.signaling.object.filterspec.IPv4FilterSpec;
import gmpls.signaling.object.filterspec.IPv4GPIFilterSpec;
import gmpls.signaling.object.filterspec.IPv6FilterSpec;
import gmpls.signaling.object.filterspec.IPv6FlowLabelFilterSpec;
import gmpls.signaling.object.filterspec.IPv6GPIFilterSpec;
import gmpls.signaling.object.filterspec.LSPTunnelIPv4FilterSpec;
import gmpls.signaling.object.filterspec.LSPTunnelIPv6FilterSpec;
import gmpls.signaling.object.filterspec.RSVPAggregateIP4FilterSpec;
import gmpls.signaling.object.filterspec.RSVPAggregateIP6FilterSpec;
import gmpls.signaling.object.flowspec.G709FlowSpec;
import gmpls.signaling.object.flowspec.IntServFlowSpec;
import gmpls.signaling.object.label.RSVPLabel;
import gmpls.signaling.object.label.SuggestedLabel;
import gmpls.signaling.object.label.UpstreamLabel;
import gmpls.signaling.object.labelrequest.GeneralizedLabelRequest;
import gmpls.signaling.object.sendertemplate.IPv4GPISenderTemplate;
import gmpls.signaling.object.sendertemplate.IPv4SenderTemplate;
import gmpls.signaling.object.sendertemplate.IPv6FlowLabelSenderTemplate;
import gmpls.signaling.object.sendertemplate.IPv6GPISenderTemplate;
import gmpls.signaling.object.sendertemplate.IPv6SenderTemplate;
import gmpls.signaling.object.sendertemplate.LSPTunnelIPv4SenderTemplate;
import gmpls.signaling.object.sendertemplate.LSPTunnelIPv6SenderTemplate;
import gmpls.signaling.object.sendertemplate.RSVPAggregateIP4SenderTemplate;
import gmpls.signaling.object.sendertemplate.RSVPAggregateIP6SenderTemplate;
import gmpls.signaling.object.sendertspec.G709SenderTSpec;
import gmpls.signaling.object.sendertspec.IntServSenderTSpec;
import gmpls.signaling.object.session.IPv4Session;
import gmpls.signaling.object.session.IPv6Session;
import gmpls.signaling.object.session.LSPTunnelIPv4Session;
import gmpls.signaling.object.session.LSPTunnelIPv6Session;

import java.util.Vector;

import util.ByteOperation;

public class RecoveryObject {

	private static Vector<RSVPObject> objects;

	public static Vector<RSVPObject> getRsvpObjects(RSVPPacket packet) throws BadFormatObjectException{
		objects = new Vector<RSVPObject>();
		int offset = 28;
		//Gets objects of packet
		while (offset < packet.getIPPacketLength()){ 
			RSVPObject object = getRsvpObject(packet, offset);
			
			offset += object.getLength();
			objects.add(object);
			
			
		}
		return objects;
	}

	public static RSVPObject getRsvpObjectByClass(Vector<RSVPObject> vector, int classNumber){
		for(RSVPObject o : vector){
			if(o.getClassNum() == classNumber){
				return o;
			}
		}
		return null;
	}

	public static RSVPObject getRsvpObjectByClass(RSVPPacket packet, int classNumber) throws BadFormatObjectException{
		Vector<RSVPObject> vector = getRsvpObjects(packet);
		for(RSVPObject o : vector){
			if(o.getClassNum() == classNumber){
				return o;
			}
		}
		return null;
	}

	public static RSVPObject getRsvpObject(RSVPPacket packet, int offset) throws BadFormatObjectException{
		RSVPObject object = null;


		byte[] lengthObjetc = new byte[4];
		byte[] c_number = new byte[4];
		byte[] c_type = new byte[4];
		//Gets the length, the class number and the class type 
		if(offset<packet.getIPPacketLength()){
		System.arraycopy(packet.getData(), offset, lengthObjetc, 2, 2);
		System.arraycopy(packet.getData(), offset+2, c_number, 3, 1);
		System.arraycopy(packet.getData(), offset+3, c_type, 3, 1);
		
		int length = ByteOperation.byteArrayToInt(lengthObjetc);
		int c_num = ByteOperation.byteArrayToInt(c_number);
		int type = ByteOperation.byteArrayToInt(c_type);
//		System.out.printf("length %d, number %d, type %d\n", length, c_num, type);
		
		//Gets the data of a object
		byte[] data = new byte[length];
		System.arraycopy(packet.getData(), offset, data, 0, length);


		object = null;

		switch (c_num) {

		case RSVPObject.SESSION_CLASS:
			if(type == Session.IPV4){
				object = new IPv4Session(data);
			} else if(type == Session.IPV6){
				object = new IPv6Session(data);
			} else if(type == Session.LSP_TUNNEL_IPV4){
				object = new LSPTunnelIPv4Session(data);
			} else if(type == Session.LSP_TUNNEL_IPV6){
				object = new LSPTunnelIPv6Session(data);
			} else {
				throw new BadFormatObjectException("session object didn't identifier!");
			}
			
			break;

		case RSVPObject.RSVP_HOP_CLASS:
			object = new RSVPHop(data);
			break;
			/** Identifier for the class SCOPE. [RFC2205, RFC2747] */
		case RSVPObject.INTEGRITY_CLASS:
			if(type == Integrity.TYPE_1_INTEGRITY){
				object = new Integrity(data);
			}
			break;

		case RSVPObject.ERROR_SPEC_CLASS:
			if(type == ErrorSpec.IPV4_TYPE){
				object = new IPv4ErrorSpec(data);
			} else if (type == ErrorSpec.IPV6_TYPE) {
				object = new IPv6ErrorSpec(data);
			} else if (type == ErrorSpec.IPV4_IF_ID_TYPE) {
				object = new IPv4InterfaceIndexErrorSpec(data);
			} else if (type == ErrorSpec.IPV6_IF_ID_TYPE) {
				object = new IPv6InterfaceIndexErrorSpec(data);
			}
			break;

		case RSVPObject.TIME_VALUES_CLASS:
			if (type == TimeValues.TYPE_1_TIME_VALUE)
				object = new TimeValues(data);
			break;
			/** Identifier for the class SCOPE. [RFC2205] */
		case RSVPObject.SCOPE_CLASS:
			if (type == Scope.IPV4 || type == Scope.IPV6)
				object = new Scope(data);
			break;
			/** Identifier for the class STYLE. [RFC2205] */
		case RSVPObject.STYLE_CLASS:
			if (type == Style.TYPE_1_STYLE)
				object = new TimeValues(data);
			break;
			/** Identifier for the class FLOWSPEC. [RFC2205] */
		case RSVPObject.FLOWSPEC_CLASS:
			if(type == FlowSpec.INT_SERV){
				object = new IntServFlowSpec(data);
			} else if (type == FlowSpec.G_709) {
				object = new G709FlowSpec(data);
			}
			break;
			/** Identifier for the class FILTER_SPEC. [RFC2205] */
		case RSVPObject.FILTER_SPEC_CLASS:
			if(type == FilterSpec.IPV4){
				object = new IPv4FilterSpec(data);
			} else if (type == FilterSpec.IPV4) {
				object = new IPv6FilterSpec(data);
			} else if (type == FilterSpec.IPV6) {
				object = new IPv6FilterSpec(data);
			} else if (type == FilterSpec.IPV6_FLOW_LABEL) {
				object = new IPv6FlowLabelFilterSpec(data);
			} else if (type == FilterSpec.IPV4_GPI) {
				object = new IPv4GPIFilterSpec(data);
			} else if (type == FilterSpec.IPV6_GPI) {
				object = new IPv6GPIFilterSpec(data);
			} else if (type == FilterSpec.LSP_TUNNEL_IPV4) {
				object = new LSPTunnelIPv4FilterSpec(data);
			} else if (type == FilterSpec.LSP_TUNNEL_IPV6) {
				object = new LSPTunnelIPv6FilterSpec(data);
			} else if (type == FilterSpec.RSVP_AGGREGATE_IP4) {
				object = new RSVPAggregateIP4FilterSpec(data);
			} else if (type == FilterSpec.RSVP_AGGREGATE_IP6) {
				object = new RSVPAggregateIP6FilterSpec(data);
			}
			break;
			/** Identifier for the class SENDER_TEMPLATE. [RFC2205] */
		case RSVPObject.SENDER_TEMPLATE_CLASS:
			if(type == SenderTemplate.IPV4){
				object = new IPv4SenderTemplate(data);
			} else if (type == SenderTemplate.IPV4) {
				object = new IPv6SenderTemplate(data);
			} else if (type == SenderTemplate.IPV6) {
				object = new IPv6SenderTemplate(data);
			} else if (type == SenderTemplate.IPV6_FLOW_LABEL) {
				object = new IPv6FlowLabelSenderTemplate(data);
			} else if (type == SenderTemplate.IPV4_GPI) {
				object = new IPv4GPISenderTemplate(data);
			} else if (type == SenderTemplate.IPV6_GPI) {
				object = new IPv6GPISenderTemplate(data);
			} else if (type == SenderTemplate.LSP_TUNNEL_IPV4) {
				object = new LSPTunnelIPv4SenderTemplate(data);
			} else if (type == SenderTemplate.LSP_TUNNEL_IPV6) {
				object = new LSPTunnelIPv6SenderTemplate(data);
			} else if (type == SenderTemplate.RSVP_AGGREGATE_IP4) {
				object = new RSVPAggregateIP4SenderTemplate(data);
			} else if (type == SenderTemplate.RSVP_AGGREGATE_IP6) {
				object = new RSVPAggregateIP6SenderTemplate(data);
			}
			break;
			/** Identifier for the class SENDER_TSPEC. [RFC2205] */
		case RSVPObject.SENDER_TSPEC_CLASS:
			if(type == SenderTSpec.INT_SERV){
				object = new IntServSenderTSpec(data);
			} else if (type == SenderTSpec.G_709) {
				object = new G709SenderTSpec(data);
			}
			break;
			/** Identifier for the class ADSPEC. [RFC2205] */
		case RSVPObject.ADSPEC_CLASS:
			if(type == AdSpec.INT_SERV){
				object = new AdSpec(data);
			}
			break;
			/** Identifier for the class POLICY DATA. [RFC2205] */
		case RSVPObject.POLICY_DATA_CLASS:
			break;
			/** Identifier for the class RESV_CONFIRM. [RFC2205] */
		case RSVPObject.RESV_CONFIRM_CLASS:
			if (type == ResvConfirm.IPV4 || type == ResvConfirm.IPV6)
				object = new ResvConfirm(data);
			break;
			/** Identifier for the class RSVP_LABEL. [RFC3209] */
		case RSVPObject.RSVP_LABEL_CLASS:
			if ( (type == Label.TYPE_1_LABEL) || (type == Label.GENERALIZED_LABEL)
					|| (type == Label.WAVEBAND_SWITCHING_LABEL) ){
				object = new RSVPLabel(data);
			} 
			break;
			/** Identifier for the class HOP_COUNT */
		case RSVPObject.HOP_COUNT_CLASS:
			break;
			/** Identifier for the class STRICT_SOURCE_ROUTE */
		case RSVPObject.STRICT_SOURCE_ROUTE_CLASS:
			break;
			/** Identifier for the class LABEL_REQUEST. [RFC3209] */
		case RSVPObject.LABEL_REQUEST_CLASS:
			if(type == LabelRequest.GENERALIZED_LABEL_REQUEST){
				object = new GeneralizedLabelRequest(data);
			} 
			break;
			/** Identifier for the class EXPLICIT_ROUTE. [RFC3209] */
		case RSVPObject.EXPLICIT_ROUTE_CLASS:
			if ( type == ExplicitRoute.TYPE_1_EXPLICIT_ROUTE ){
				object = new ExplicitRoute(data);
			} 
			break;
			/** Recorevy data object for the class RECORD_ROUTE. [RFC3209] */
		case RSVPObject.RECORD_ROUTE_CLASS:
			if ( type == RecordRoute.TYPE_1_RECORD_ROUTE ){
				object = new RecordRoute(data);
			}
			break;
			/** Identifier for the class HELLO. [RFC3209] */
		case RSVPObject.HELLO_CLASS:
			if ( type == Hello.REQUEST_TYPE || type == Hello.ACKNOWLEDGMENT_TYPE ){
				object = new Hello(data);
			}
			break;
			/** Identifier for the class MESSAGE_ID. [RFC2961] */
		case RSVPObject.MESSAGE_ID_CLASS:
			break;
			/** Identifier for the class MESSAGE_ID_ACK. [RFC2961] */
		case RSVPObject.MESSAGE_ID_ACK_CLASS:
			break;
			/** Identifier for the class MESSAGE_ID_LIST. [RFC2961] */
		case RSVPObject.MESSAGE_ID_LIST_CLASS:
			break;
			/** Identifier for the class DIAGNOSTIC. [RFC2745] */
		case RSVPObject.DIAGNOSTIC_CLASS:
			break;
			/** Identifier for the class ROUTE. [RFC2745] */
		case RSVPObject.ROUTE_CLASS:
			break;
			/** Identifier for the class DIAG_RESPONSE. [RFC2745] */
		case RSVPObject.DIAG_RESPONSE_CLASS:
			break;
			/** Identifier for the class DIAG_SELECT. [RFC2745] */
		case RSVPObject.DIAG_SELECT_CLASS:
			break;
			/** Identifier for the class RECOVERY_LABEL. [RFC3473] */
		case RSVPObject.RECOVERY_LABEL_CLASS:
			break;
			/** Identifier for the class UPSTREAM_LABEL. [RFC3473] */
		case RSVPObject.UPSTREAM_LABEL_CLASS:
			if ( (type == Label.TYPE_1_LABEL) || (type == Label.GENERALIZED_LABEL)
					|| (type == Label.WAVEBAND_SWITCHING_LABEL) ){
				object = new UpstreamLabel(data);
			} 
			break;
			/** Identifier for the class LABEL_SET. [RFC3473] */
		case RSVPObject.LABEL_SET_CLASS:
			if ( type == LabelSet.TYPE_1_LABEL_SET ){
				object = new LabelSet(data);
			}
			break;
			/** Identifier for the class PROTECTION. [RFC3473] */
		case RSVPObject.PROTECTION_CLASS:
			
				object = new Protection(data);
		
			
			break;
			/** Identifier for the class PRIMARY_PATH_ROUTE. [RFC4872] */
		case RSVPObject.PRIMARY_PATH_ROUTE_CLASS:
			break;
			/** Identifier for the class S2_SUB_LSP. [RFC4875] */
		case RSVPObject.S2_SUB_LSP_CLASS:
			break;
			/** Identifier for the class DETOUR. [RFC4090] */
		case RSVPObject.DETOUR_CLASS:
			break;
			/** Identifier for the class CHALLENGE. [RFC2747] */
		case RSVPObject.CHALLENGE_CLASS:
			break;
			/** Identifier for the class DIFFSERV. [RFC3270] */
		case RSVPObject.DIFFSERV_CLASS:
			break;
			/** Identifier for the class CLASSTYPE. [RFC4124] */
		case RSVPObject.CLASSTYPE_CLASS:
			break;
			/** Identifier for the class LSP_REQUIRED_ATTRIBUTES. [RFC4420] */
		case RSVPObject.LSP_REQUIRED_ATTRIBUTES_CLASS:
			break;
			/** Identifier for the class SUGGESTED_LABEL. [RFC3473] */
		case RSVPObject.SUGGESTED_LABEL_CLASS:
			if ( (type == Label.TYPE_1_LABEL) || (type == Label.GENERALIZED_LABEL)
					|| (type == Label.WAVEBAND_SWITCHING_LABEL) ){
				object = new SuggestedLabel(data);
			} 
			break;
			/** Identifier for the class ACCEPTABLE_LABEL_SET. [RFC3473] */
		case RSVPObject.ACCEPTABLE_LABEL_SET_CLASS:
			break;
			/** Identifier for the class RESTART_CAP. [RFC3473] */
		case RSVPObject.RESTART_CAP_CLASS:
			break;
			/** Identifier for the class SESSION_OF_INTEREST. [RFC4860] */
		case RSVPObject.SESSION_OF_INTEREST_CLASS:
			break;
			/** Identifier for the class LINK_CAPABILITY */
		case RSVPObject.LINK_CAPABILITY_CLASS:
			break;
			/** Identifier for the class LSP_TUNNEL_INTERFACE_ID. [RFC3477] */
		case RSVPObject.LSP_TUNNEL_INTERFACE_ID_CLASS:
			break;
			/** Identifier for the class NOTIFY_REQUEST. [RFC3473] */
		case RSVPObject.NOTIFY_REQUEST_CLASS:
			if ( type == NotifyRequest.IPV4_NOTIFY_REQUEST || type == NotifyRequest.IPV6_NOTIFY_REQUEST){
				object = new NotifyRequest(data);
			}
			break;
			/** Identifier for the class ADMIN_STATUS. [RFC3473] */
		case RSVPObject.ADMIN_STATUS_CLASS:
			if(type == AdminStatus.TYPE_1_ADMIN_STATUS){
				object = new AdminStatus(data);
			} 
			break;
			/** Identifier for the class LSP_ATTRIBUTES. [RFC4420] */
		case RSVPObject.LSP_ATTRIBUTES_CLASS:
			break;
			/** Identifier for the class ALARM_SPEC. [RFC4783] */
		case RSVPObject.ALARM_SPEC_CLASS:
			break;
			/** Identifier for the class ASSOCIATION. [RFC4872] */
		case RSVPObject.ASSOCIATION_CLASS:
			object = new Association(data);
			break;
			/** Identifier for the class SECONDARY_EXPLICIT_ROUTE. [RFC4873] */
		case RSVPObject.SECONDARY_EXPLICIT_ROUTE_CLASS:
			break;
			/** Identifier for the class SECONDARY_RECORD_ROUTE. [RFC4873] */
		case RSVPObject.SECONDARY_RECORD_ROUTE_CLASS:
			break;
			/** Identifier for the class FAST_REROUTE. [RFC4090] */
		case RSVPObject.FAST_REROUTE_CLASS:
			break;
			/** Identifier for the class SESSION_ATTRIBUTE. [RFC3209] */
		case RSVPObject.SESSION_ATTRIBUTE_CLASS:
			break;
			/** Identifier for the class CALL_OPS. [RFC3474] */
		case RSVPObject.CALL_OPS_CLASS:
			break;
			/** Identifier for the class GENERALIZED_UNI. [RFC3476] */
		case RSVPObject.GENERALIZED_UNI_CLASS:
			break;
			/** Identifier for the class CALL_ID. [RFC3474] */
		case RSVPObject.CALL_ID_CLASS:
			break;
			/** Identifier for the class EXCLUDE_ROUTE. [RFC4874] */
		case RSVPObject.EXCLUDE_ROUTE_CLASS:
			break;

		default:
			System.out.println("Object Data didn't identifier!");
			break;
		}
		}
		

		return object;
	}


}
