package br.ufabc.controlplane;
/**
 * During reservation setup, an RSVP QoS request is passed to two local
   decision modules, "admission control" and "policy control".
   Admission control determines whether the node has sufficient
   available resources to supply the requested QoS.  Policy control
   determines whether the user has administrative permission to make the
   reservation.  If both checks succeed, parameters are set in the
   packet classifier and in the link layer interface (e.g., in the
   packet scheduler) to obtain the desired QoS.  If either check fails,
   the RSVP program returns an error notification to the application
   process that originated the request.
 * */
public class PolicyControl {

}
