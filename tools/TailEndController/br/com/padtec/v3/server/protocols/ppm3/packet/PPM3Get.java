package br.com.padtec.v3.server.protocols.ppm3.packet;

import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.util.Functions;
/**
 * This class represents a payload type GET
 * Sees the structure this in the AbstractSerialNumberPayload class 
 * */
public class PPM3Get extends AbstractSerialNumberPayload
{
	public PPM3Get(){
		
	}

	public PPM3Get(SerialNumber serial, int type)
	{
		super(serial, type);
	}

	public PPM3Get(SerialNumber serial, int type, byte[] value)
	throws InvalidValueException
	{
		super(serial, Functions.l2b(type, 2), value);
	}

	public Type getType()
	{
		return Type.TYPE_GET;
	}
}