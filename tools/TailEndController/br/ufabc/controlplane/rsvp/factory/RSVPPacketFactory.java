package br.ufabc.controlplane.rsvp.factory;

import gmpls.signaling.RSVPPacket;
import gmpls.signaling.object.RSVPObject;

import java.util.Vector;

import org.savarese.rocksaw.net.RawSocket;

public abstract class RSVPPacketFactory {
	
//	public abstract RSVPPacket createPathMessage(int source, int destination, int destinationPort) throws Exception; 

//	public RSVPObject getObjectByClass(Vector<RSVPObject> objects, int classNumber) throws Exception{
//		if(objects == null)
//			throw new Exception("Object's vector is null!");
//		for(RSVPObject o : objects){
//			if(o.getClassNum() == classNumber){
//				return o;
//			}
//		}
//		return null;
//	}
	/**
	 * Create and return a new RSVP Packet
	 * 
	 * @param size The total size of objects witch will be carried in the packet
	 * @param ipVersion The IP version, can be IPv4 (4) or IPv6 (6)
	 * @param ttl The Time To Live of packet
	 * @param typeOfService The type of service
	 * @param ipFlags The flag to set some options in the IP heather
	 * @param source The source address
	 * @param destination The destination address
	 * @param messageType The RSVP Message Type
	 * @param rsvpVersion The version of RSVP Packet
	 * @param rsvpFlags The flag to set some options in the RSVP heather
	 * 
	 * @return The RSVP Packet created 
	 * */
	protected static RSVPPacket getNewRsvpPacket(Vector<RSVPObject> vector, int ipVersion, int ttl, int typeOfService, 
			int ipFlags, int source, int destination, int messageType, int rsvpVersion, int rsvpFlags){
		//Alocate the necessary space
		int size = 0;
		for (RSVPObject obj: vector) {
			size = size + obj.size();
		}
		//Create the packet
		int packetLength = 20 + size + 8; //ip header + size of objects + rsvp header
		RSVPPacket rsvp = new RSVPPacket(packetLength);
		//Set the IP fields
		rsvp.setIPVersion(ipVersion); //IPv4
		rsvp.setTTL(ttl);
		rsvp.setTypeOfService(typeOfService); //Network traffic (Class selector 6)
		rsvp.setIPPacketLength(packetLength);
		rsvp.setIPHeaderLength(5);
		rsvp.setIdentification(666);
		rsvp.setIPFlags(ipFlags); // 2 = Dont fragment
		rsvp.setProtocol(RawSocket.getProtocolByName("rsvp"));
		rsvp.setSourceAsWord(source);
		rsvp.setDestinationAsWord(destination);
		//Set the RSVP fields
		rsvp.setRSVPVersion(rsvpVersion);
		rsvp.setRSVPLength(size+8);
		rsvp.setRSVPFlags(rsvpFlags);
		rsvp.setMessageType(messageType); //Path message
		rsvp.setSendTTL();
		return rsvp;
	}
	
	
}
