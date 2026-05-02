package br.com.padtec.v3.server.protocols.ppm2v2;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.TreeMap;

import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.util.Functions;
import br.com.padtec.v3.util.io.UdpHelper;

public class SupjUdpService
{
  public static void main(String[] args)
  {
    for (int i = 0; i < 20; ++i) {
      int item = i;
      System.err.println(item + SupjUdpService.getSupConnection("192.168.0.199").toString()); 
            
    }
  }

  public static final TreeMap<String, Object> getSupConnection(String ip)  {
    try {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(3000);
        SocketAddress dest = new InetSocketAddress(ip, 8876);
        TreeMap<String, Object> result = getSupConnection(socket, dest, 3);
        socket.close();
        return result;
    }catch (SocketException e) {
         return null;
    } catch (Exception none) {
    	return null;
    }
    	
  }

  private static final TreeMap<String, Object> getSupConnection(DatagramSocket socket, SocketAddress dest, int atempts)
    throws IOException  {
    byte[] query = new PPM2v2((byte)1, (byte)34, null,new byte[5], null).getRawBytes();

    byte[] queryResult = (byte[])null;
    
    int i = 0;
    try   {
      UdpHelper.write(socket, query, dest);

      queryResult = UdpHelper.read(socket);
    } catch (IOException e) {
    	do {
    		IOException error = e;
        ++i; 
    	} while (i < atempts);
    }

    query = (byte[])null;
    if (queryResult == null) {
      throw new IOException();
    }

    PPM2v2 packet = new PPM2v2(queryResult);
    queryResult = (byte[])null;

    TreeMap<String, Object> result = new TreeMap<String, Object>();
    SerialNumber supSerial = PPM2v2.getSerialFromAddress(packet.getSource());
    result.put("SUP_SERIAL_NUMBER", supSerial);
    byte[] dados = packet.getDataArray();

    result.put("CONNECTED_IP", Functions.b2i(dados[5]) + "." + Functions.b2i(dados[6]) + "." + 
    		Functions.b2i(dados[7]) + "." + Functions.b2i(dados[8]));
    result.put("CONNECTED_PORT",  new Integer(Functions.b2i(dados[9]) * 256 +	Functions.b2i(dados[10])));
    result.put("RETRIES", i);
    packet = null;
    dados = (byte[])null;
    return result;
  }
}