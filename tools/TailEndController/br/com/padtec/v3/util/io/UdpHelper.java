package br.com.padtec.v3.util.io;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

public final class UdpHelper{
  private static void write(DatagramSocket socket, DatagramPacket request)
    throws IOException
  {
    socket.send(request);
  }

  public static void write(DatagramSocket socket, byte[] data, SocketAddress dest)
    throws IOException
  {
    DatagramPacket packet = createDatagramPacket(data);
    packet.setSocketAddress(dest);
    write(socket, packet);
  }

  public static byte[] read(DatagramSocket socket)
    throws IOException
  {
    byte[] inbuf = new byte[1500];
    DatagramPacket packet = new DatagramPacket(inbuf, inbuf.length);
    socket.receive(packet);
    return getData(packet);
  }

  private static byte[] getData(DatagramPacket datagramPacket) {
    byte[] data = new byte[datagramPacket.getLength()];
    System.arraycopy(datagramPacket.getData(), 0, data, 0, 
      datagramPacket.getLength());
    return data;
  }

  private static DatagramPacket createDatagramPacket(byte[] data) {
    return new DatagramPacket(data, data.length);
  }
}