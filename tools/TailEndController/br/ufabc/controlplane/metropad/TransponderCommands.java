package br.ufabc.controlplane.metropad;

import java.util.LinkedHashMap;
import java.util.Map;

import br.com.padtec.v3.data.Command;
import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.Command.Data;
import br.com.padtec.v3.util.ChannelTable;
import br.com.padtec.v3.util.Functions;
import br.ufabc.dataplane.DataPlaneException;

public class TransponderCommands {
	/***/
	private static LinkedHashMap<String, Double> transponderChannels = new LinkedHashMap<String, Double>(); 
	private static boolean loadedList = false;
	private static int initialChannel = 21;
	private static int lastChannel = 60;
	
	
	public static Command getCommandResetErrorRateCounters(SerialNumber serial){
		byte[] cmd = new byte[3];
		int commandId = Command.TRP_RESET_COUNT;
        cmd[0] = (byte) commandId;
        Command command = new Command(serial, commandId, cmd);
        return command;
	}
	
	public static Command getCommandTurnOnLaserWDM(SerialNumber serial){
		byte[] cmd = new byte[3];
        cmd[0] = Command.TRP_SET_LASER_ON;
        int commandId = Command.TRP_SET_LASER_ON;
        Command command = new Command(serial, commandId, cmd);
//        Data data = Command.trpTable.get(commandId);
//        System.out.println(data);
        
        
        return command;
	}
	
	public static Command getCommandTurnOffLaserWDM(SerialNumber serial){
		byte[] cmd = new byte[3];
        cmd[0] = Command.TRP_SET_LASER_OFF;
        int commandId = Command.TRP_SET_LASER_OFF;
        Command command = new Command(serial, commandId, cmd);
        Data data = Command.trpTable.get(commandId);
//        System.out.println(data);
        
        return command;
	}
	
	/***/
	public static Command getCommandTurnOnLaserClient(SerialNumber serial){
		byte[] cmd = new byte[3];
        cmd[0] = Command.TRP_SET_LASER2_ON;
        int commandId = Command.TRP_SET_LASER2_ON;
        Command command = new Command(serial, commandId, cmd);
        Data data = Command.trpTable.get(commandId);
//        System.out.println(data);
        
        
        return command;
	}
	
	public static Command getCommandTurnOffLaserClient(SerialNumber serial){
		byte[] cmd = new byte[3];
        cmd[0] = Command.TRP_SET_LASER2_OFF;
        int commandId = Command.TRP_SET_LASER2_OFF;
        Command command = new Command(serial, commandId, cmd);
        Data data = Command.trpTable.get(commandId);
//        System.out.println(data);
        
        return command;
	}
	
	public static Command getCommandTurnOnAutoLaserWDM(SerialNumber serial){
		byte[] cmd = new byte[3];
		int commandId = Command.TRP_AUTOLASEROFF_ON;
        cmd[0] = (byte)commandId;
        
        Command command = new Command(serial, commandId, cmd);
        Data data = Command.trpTable.get(commandId);
//        System.out.println(data);
        
        
        return command;
	}
	
	public static Command getCommandTurnOffAutoLaserWDM(SerialNumber serial){
		byte[] cmd = new byte[3];
		int commandId = Command.TRP_AUTOLASEROFF_OFF;
        cmd[0] = (byte)commandId;
        Command command = new Command(serial, commandId, cmd);
        Data data = Command.trpTable.get(commandId);
//        System.out.println(data);
        
        return command;
	}
	
	/***/
	public static Command getCommandTurnOnAutoLaserClient(SerialNumber serial){
		byte[] cmd = new byte[3];
		int commandId = Command.TRP_AUTOLASEROFF2_ON;
        cmd[0] = (byte)commandId;
        Command command = new Command(serial, commandId, cmd);
        Data data = Command.trpTable.get(commandId);
//        System.out.println(data);
        
        
        return command;
	}
	
	public static Command getCommandTurnOffAutoLaserClient(SerialNumber serial){
		byte[] cmd = new byte[3];
		int commandId = Command.TRP_AUTOLASEROFF2_OFF;
        cmd[0] = (byte)commandId;
        Command command = new Command(serial, commandId, cmd);
        Data data = Command.trpTable.get(commandId);
//        System.out.println(data);
        
        return command;
	}
	public static void main(String[] args){
		Command c = getCommandTurnOnLaserWDM(new SerialNumber(0, 0));
		System.out.println(c);
	}
	
	private static void initChannelList(){
		boolean bandaH = true;
		boolean bandaC = true;

		String channelStringC = "";
		String channelStringH = "";
		for (int channel = initialChannel; channel <= lastChannel; channel++) {
			if (channel < 10) {
				channelStringC = "C0";
				channelStringH = "H0";
			} else {
				channelStringC = "C";
				channelStringH = "H";
			}
			if (bandaH) {
//				transponderChannels.put(channelStringH + channel, MessageFormat.format("{0,NUMBER,0.00}", 
//						new Object[] { ChannelTable.channel2Wavelength(channelStringH + channel) * 1000000000.0D }) );
				transponderChannels.put(channelStringC + channel, ChannelTable.channel2Wavelength(channelStringC + channel) * 1000000000.0 );
			}
			if (bandaC) {
//				transponderChannels.put(channelStringC + channel, MessageFormat.format("{0,NUMBER,0.00}", 
//						new Object[] { ChannelTable.channel2Wavelength(channelStringC + channel) * 1000000000.0D }) );
				transponderChannels.put(channelStringC + channel, ChannelTable.channel2Wavelength(channelStringC + channel) * 1000000000.0 );
			}
		}
	}

	public static Map<String,Double> getChannelList(){
		
		if (!loadedList){
			initChannelList();
			loadedList = true;
		}
		
		return transponderChannels;
		
	}
	
	/**
	 * Returns the command to change the lambda by channel in WDM side of Transponder
	 * @param serial The serial number of transponder
	 * @param channel The channels type (e.g. C28)
	 * @throws DataPlaneException 
	 * */
	public static Command configureLambdaWDM(SerialNumber serial, String channel) throws DataPlaneException {
		Command cmd = null;
		if (channel != null) {

			int channelValue = Integer.valueOf(channel.substring(1, 3)).intValue();
			
			if (channelValue < initialChannel || channelValue > lastChannel){
				throw new DataPlaneException("Tentantiva de configurar o canal " + channel + " fora dos limites permitidos: C"+initialChannel +
						"-C"+lastChannel);
			} 
			byte[] cmdData = new byte[3];
			char type = channel.charAt(0);
			if ((type == 'C') || (type == 'H')) {

				channelValue = Functions.convertITUChannel(channelValue, type);
				cmdData[0] = Functions.i2b(Integer.valueOf(67).intValue());
				cmdData[1] = Functions.l2b(channelValue, 2)[0];
				cmdData[2] = Functions.l2b(channelValue, 2)[1];

				cmd = new Command(serial, 400, cmdData);

			}
		}
		
		return cmd;
			
	}
	
	/**
	 * Returns the command to change the lambda by channel in client side of Transponder
	 * @param serial The serial number of transponder
	 * @param channel The channels type (e.g. C28)
	 * */
	public static Command configureLambdaClient(SerialNumber serial, String channel) {
		Command cmd = null;
		if (channel != null) {

			int channelValue = Integer.valueOf(channel.substring(1, 3)).intValue();
			byte[] cmdData = new byte[3];
			char type = channel.charAt(0);
			if ((type == 'C') || (type == 'H')) {

				channelValue = Functions.convertITUChannel(channelValue, type);
				cmdData[0] = Functions.i2b(Integer.valueOf(67).intValue());
				cmdData[1] = Functions.l2b(channelValue, 2)[0];
				cmdData[2] = Functions.l2b(channelValue, 2)[1];

				cmd = new Command(serial, 401, cmdData);

			}
		}
		
		return cmd;
			
	}
	
	public static Command disableFecReceptor(SerialNumber serial) {
	    int resp;
	    byte[] cmd = new byte[3];
	    /*Command.TRP_FEC_RX_OFF = 11*/
	    int commandId = Command.TRP_FEC_RX_OFF;
	    cmd[0] = (byte)commandId;
	    Command command = new Command(serial, commandId, cmd);
	    return command;
	}
	
	public static Command enableFecReceptor(SerialNumber serial){
		int resp;
	    byte[] cmd = new byte[3];
	    /*Command.TRP_FEC_RX_ON = 12*/
	    int commandId = Command.TRP_FEC_RX_ON;
	    cmd[0] = (byte)commandId;
	    Command command = new Command(serial, commandId, cmd);
	    return command;
	}
	
	public static Command disableFecTransmissor(SerialNumber serial) {
	    int resp;
	    byte[] cmd = new byte[3];
	    /*Command.TRP_FEC_TX_OFF = 9*/
	    int commandId = Command.TRP_FEC_TX_OFF;
	    cmd[0] = (byte)commandId;
	    Command command = new Command(serial, commandId, cmd);
	    return command;
	}
	
	public static Command enableFecTransmissor(SerialNumber serial){
		int resp;
	    byte[] cmd = new byte[3];
	    /*Command.TRP_FEC_TX_ON = 10*/
	    int commandId = Command.TRP_FEC_TX_ON;
	    cmd[0] = (byte)commandId;
	    Command command = new Command(serial, commandId, cmd);
	    return command;
	}
	
	
	 

}
