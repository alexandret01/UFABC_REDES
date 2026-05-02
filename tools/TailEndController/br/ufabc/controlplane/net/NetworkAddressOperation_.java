package br.ufabc.controlplane.net;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import util.ByteOperation;



public class NetworkAddressOperation {
	public enum TypeIP{
		IPv4,
		IPv6,
	}
	
	/**
	 * Return a <code>Enumeration<InetAddress></code> from <code>NetworkInterface</code> eth0
	 * @return Enumeration<InetAddress>
	 * */
	public static Enumeration<InetAddress> getLocalAddresses() throws SocketException{
		//	return getLocalAddresses("eth0");
		return getLocalAddresses("eno8303");
	}
	/**
	 * Return a <code>InetAddress</code> from <code>NetworkInterface</code> eth0
	 * @param name The type IP
	 * @return InetAddress
	 * */
	public static InetAddress getLocalAddress(TypeIP type) throws SocketException{
		//return getLocalAddress("eth0", type);
		return getLocalAddress("eno8303", type);
	}

	/**
	 * Returns a <code>Enumeration<InetAddress></code> from a specified <code>NetworkInterface</code> 
	 * @param name The name of Network Interface
	 * @return Enumeration<InetAddress>
	 * */
	public static Enumeration<InetAddress> getLocalAddresses(String name) throws SocketException{
		Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
		while (interfaces.hasMoreElements()){
			NetworkInterface iface = interfaces.nextElement();
			if (iface.getName().equalsIgnoreCase(name))
				return iface.getInetAddresses();

		}
		return null;
	}
	 /**
	  *  Returns a <code>InetAddress</code> from a specified <code>NetworkInterface</code>
	  *  @param name The name of Network Interface
	  *  @param type The Type IP
	  *  @return InetAddress 
	  * */
	public static InetAddress getLocalAddress(String name, TypeIP type) throws SocketException{
		Enumeration<InetAddress> addresses = getLocalAddresses(name);
		InetAddress address = null;
		while (addresses.hasMoreElements()){
			//address = addresses.nextElement();
			address = addresses.nextElement();
			if(type.equals(TypeIP.IPv4)){
				if(address instanceof Inet4Address){
					return (Inet4Address) address;
				}
			} else if (type.equals(TypeIP.IPv6)){
				if(address instanceof Inet6Address){
					return (Inet6Address) address;
				}
			}
		}
		return null;
		
	}
	
	/**
	 * Compares a address with the local address
	 * @param address A address to compare
	 * @return boolean
	 * */
	public static boolean compareWithLocalAddress(InetAddress address) throws SocketException{
		if (address instanceof Inet4Address){
			if(address.equals(getLocalAddress(TypeIP.IPv4)))
				return true;
		}
		
		if (address instanceof Inet6Address){
			if(address.equals(getLocalAddress(TypeIP.IPv6)))
				return true;
		} 
//		if ( address.isSiteLocalAddress()) { 
//			return true;
//		}
				
		return false;
	}
	
	
	
	/**
	 * @return the local address as a integer
	 * @throws SocketException 
	 */
	public static int getLocalAddressAsInt() throws SocketException {
		int local =  ByteOperation.byteArrayToInt(getLocalAddress(TypeIP.IPv4).getAddress());
		return local ;
	}
	
	public static void main (String[] args){
		TypeIP type = TypeIP.IPv4;
		Enumeration<InetAddress> addresses;
		try {
			addresses = getLocalAddresses("eno8303");

			InetAddress address = null;
			while (addresses.hasMoreElements()){
				address = addresses.nextElement();
				if(type.equals(TypeIP.IPv4)){
					if(address instanceof Inet4Address){
						
						System.out.println("IPV4: " + address);
					}
				} else if (type.equals(TypeIP.IPv6)){
					if(address instanceof Inet6Address){
						System.out.println("IPV6: " + address);
					}
				}
			}
			System.out.println(getLocalAddress(TypeIP.IPv4));
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
