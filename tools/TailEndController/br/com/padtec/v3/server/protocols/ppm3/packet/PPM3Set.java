package br.com.padtec.v3.server.protocols.ppm3.packet;


import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.util.Functions;

public class PPM3Set extends AbstractSerialNumberPayload
{
	public PPM3Set() {
	}

	public PPM3Set(SerialNumber serial, int type)
	{
		super(serial, type);
	}

	public PPM3Set(SerialNumber serial, int type, byte[] value) throws InvalidValueException
	{
		super(serial, Functions.l2b(type, 2), value);
	}

	public PPM3Payload.Type getType()
	{
		return PPM3Payload.Type.TYPE_SET;
	}
}