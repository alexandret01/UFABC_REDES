package br.com.padtec.v3.server.protocols.ppm3.packet;

import br.com.padtec.v3.util.Functions;
/**
 * This class represents the tuple (Type, Length, Value).
 * TLV

               0             1  
        +-------------+-------------+
        |          TLV_Type         |
        +-------------+-------------+
        +         TLV_Length        |
        +-------------+-------------+
        |   TLV_Value (Variable)    |
        +-------------+-------------+
 * */
public class TLV {
	private byte[] type;
	private byte[] value;

	public TLV() {
	}

	public TLV(int type)
	{
		try
		{
			setType(Functions.l2b(type, 2));
		}
		catch (InvalidValueException e) {
			e.printStackTrace();
		}
	}

	public TLV(byte[] type, byte[] value)
	throws InvalidValueException
	{
		setType(type);
		setValue(value);
	}

	public byte[] getType()
	{
		return this.type;
	}

	public void setType(int type)
	{
		this.type = Functions.l2b(type, 2);
	}

	public void setType(byte[] type)
	throws InvalidValueException
	{
		if ((type != null) && 
				(type.length != 2)) {
			throw new InvalidValueException(
					"tlv type should have 2 bytes in length:" + Functions.getHexa(type));
		}

		this.type = type;
	}

	public byte[] getValue()
	{
		return this.value;
	}

	public void setValue(byte[] value)
	throws InvalidValueException
	{
		if ((value != null) && 
				(value.length >= 65535)) {
			throw new InvalidValueException(
					"tlv value should be less than 65535 bytes in length:" + 
					Functions.getHexa(value));
		}

		this.value = value;
	}

	public int getLength()
	{
		if (this.value == null) {
			return 0;
		}
		return this.value.length;
	}

	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("[type:");
		buffer.append(Functions.getHexa(getType()));
		buffer.append(" len:");
		buffer.append(getLength());
		buffer.append(" val:");
		buffer.append(Functions.getHexa(getValue()));
		buffer.append("]");
		return buffer.toString();
	}

	public int getTypeAsInt()
	{
		return (int)Functions.b2l(getType());
	}

	@Deprecated
	public void setCommand(byte[] bs)
	{
		try
		{
			setType(bs);
		} catch (InvalidValueException e) {
			e.printStackTrace();
		}
	}

	@Deprecated
	public byte[] getCommand()
	{
		return getType();
	}

	@Deprecated
	public byte[] getData()
	{
		return getValue();
	}
}