package br.ufabc.equipment;

import br.com.padtec.v3.data.Command;
import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.ne.Ne16Bit;

public class AmplifierCommands {
		
	/**
	 * Return a Command object with the value of gain to be setup into the specified amplifier.
	 * @param serial The Serial Number of amplifier.
	 * @param gain The value of gain to set.
	 *
	 * */
	public static Command getCommandSetGain(Supervisor sup, SerialNumber serial, double gain){
		int dado = (int)(gain * 10.0D);
		byte[] code = new byte[3];
        code[0] = Command.AMP_SET_AGC_GAIN;
        code[1] = (byte)(dado % 256);
	    code[2] = (byte)(dado / 256);
        Command command = getCommand(sup, serial, code);
        return command;
	}
	
	/**
	 * Return a Command object 
	 * @param serial The Serial Number
	 * @param code The byte array with the code of command
	 *
	 * */
	private static Command getCommand(Supervisor sup, SerialNumber serial, byte[] code ){
		return new Command(serial, code, sup.getNE(serial) instanceof Ne16Bit);
	}
	
	
}
