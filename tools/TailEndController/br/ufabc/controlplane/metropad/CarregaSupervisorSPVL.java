package br.ufabc.controlplane.metropad;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;

import br.com.padtec.v3.data.Command;
import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.impl.SPVL4_Impl;
import br.com.padtec.v3.server.protocols.ppm3.PPM3Collector;
import br.com.padtec.v3.server.protocols.ppm3.PPM3Command;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3Factory;
import br.com.padtec.v3.server.protocols.ppm3.packet.TLV;
import br.com.padtec.v3.util.PartNumber;
import br.com.padtec.v3.util.log.Log;




public class CarregaSupervisorSPVL {
	private static final SerialNumber BROADCAST_SERIAL = new SerialNumber(0, 0);
	
	private static TCPClient tcpClient = new TCPClient("10.2.22.5", 8886);
	
	public static void main(String[] args) {
		SerialNumber serial = new SerialNumber(1316, 648);
//		SPVL4_Impl spvl = new SPVL4_Impl(serial);
//		System.out.println(spvl);
		      try {
		    	  SPVL4_Impl spvl = (SPVL4_Impl)PartNumber.getInstance(serial, false);
		    	  PPM3Collector coletor = new PPM3Collector();
		    	  
		         Socket skt = tcpClient.getSocket();
		         if (skt.isConnected())
		        	 System.out.println("o socket conectou");
		         
		         BufferedReader in = new BufferedReader(new
		            InputStreamReader(skt.getInputStream()));
		         System.out.print("Send packet desbloquear supervisor: ");
		         DataOutputStream outToServer = new DataOutputStream(skt.getOutputStream());
		         outToServer.write(sendRegen());
//		         PPM3 packet = unlockSupervisor(1316, 3);
//		         System.out.println(packet);
		         Command cmd = SupervisorCommands.getCommanRequestOfAccess(serial, "gustavo");
		         PPM3 pacote = coletor.command2Ppm3(cmd, spvl);
		         
		         outToServer.write(pacote.getBytes());
		         byte[] buffer = new byte[10000];
		         DataInputStream inFromServer = new DataInputStream(skt.getInputStream());
		         
		         while (!in.ready()) {
		        	 
		         }
		         inFromServer.read(buffer);
//		         String line = in.readLine();
		         PPM3 packetReceived = PPM3.getPPM3(buffer);
		         
//		         while (line.length()!= -1) {
//		        	 System.out.println(line); // Read one line and output it
//		        	 line = in.readLine();
//		        	 
//		         }
		         System.out.println("Pacote recebido: "+ packetReceived);

		         
		         in.close();
		      }
		      catch(Exception e) {
		         System.out.print("Whoops! It didn't work!\n");
		      }
		   

	}
	
	private static String getConection(){
		return tcpClient.getConnection();
	} 
	
	private static void log(Level level, String msg) {
	    StringBuilder sb = new StringBuilder();
	    sb.append("Collector ");
	    sb.append(getConection());
	    sb.append(": ");
	    sb.append(msg);
	    Log.getInstance().log(level, sb.toString());
	    sb = null;
	  }
	
	public static byte[] sendRegen()
	  {
	    PPM3 regen = PPM3Factory.getGetPPM3(0, BROADCAST_SERIAL);
	    regen.addTLV(new TLV(PPM3Command.REGEN.code()));
	    System.out.println(regen);
//	    enqueue(regen, false, true);
	    log(Level.INFO, "Sending regenerate traps for " + getConection());
	    System.out.println("Sending regenerate traps for " + getConection());
	    return regen.getBytes();

//	    Set<Integer> sites = this.neDb.getSites();
//	    for (Integer site : sites) {
//	      sendHistoryRequest(EnumHistoryType.READ_TRAP_NEW_AND_DEL, site.intValue(), null, null, null);
//	    }

	    
	  }
	
	public static PPM3 unlockSupervisor(int part, int address)
	{
		PPM3 packet = PPM3Factory.getSetPPM3(address, new SerialNumber(part, 0));
		TLV tlv = new TLV(32787);
		packet.addTLV(tlv);
		return packet;
	}

}
