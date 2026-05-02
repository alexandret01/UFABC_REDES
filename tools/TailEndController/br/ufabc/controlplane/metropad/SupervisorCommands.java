package br.ufabc.controlplane.metropad;

import br.com.padtec.v3.data.Command;
import br.com.padtec.v3.data.SerialNumber;



public class SupervisorCommands {
	
	public static Command getCommanRequestOfAccess(SerialNumber serial, String user){
		byte[] dataC = new byte[36];
	      dataC[0] = 1;
	      byte[] login = user.getBytes();
	      System.arraycopy(login, 0, dataC, 1, login.length);
	      dataC[(login.length + 1)] = 0;
//	      int date = this.fieldTempo.getInt();
//	      Functions.setBytes(data, 34, date, 2);
	      int commandId = Command.SUP_SET_REQUEST_OF_ACCESS;
	      Command command = new Command(serial, commandId, dataC);
//	        Data data = Command.supTable.get(commandId);
//	        System.out.println(data);
	      return command;
	} 
	
	
}
