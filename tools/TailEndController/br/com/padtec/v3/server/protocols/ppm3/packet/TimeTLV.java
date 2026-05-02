package br.com.padtec.v3.server.protocols.ppm3.packet;

import java.text.DateFormat;
import java.util.Date;

import br.com.padtec.v3.util.DateUtils;
import br.com.padtec.v3.util.Functions;

/**
 * This class represents the tuple (Type, Length, Value) more a date.
 * Time_TLV
               0             1  
        +-------------+-------------+
        | 			    			|
        |        Time Stamp         |
        |         (8 bytes)         |
        |                           |
        +-------------+-------------+
        |          TLV_Type         |
        +-------------+-------------+
        +         TLV_Length        |
        +-------------+-------------+
        |   TLV_Value (Variable)    |
        +-------------+-------------+

 * */
public class TimeTLV extends TLV {
	private long timestamp;

	public TimeTLV()
	{
	}

	public TimeTLV(long time, int type)
	{
		setTimestamp(time);
		try {
			setType(Functions.l2b(type, 2));
		}
		catch (InvalidValueException e) {
			e.printStackTrace();
		}
	}

	public TimeTLV(long time, int type, byte[] value) throws InvalidValueException
	{
		this(time, type);
		setValue(value);
	}

	public long getTimestamp()
	{
		return this.timestamp;
	}

	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}

	public String toString()
	{
		Date date = new Date(this.timestamp);

		StringBuffer buffer = new StringBuffer();
		buffer.append("[time:");
		buffer.append(DateFormat.getDateInstance(2).format(date));
		buffer.append(" ");
		buffer.append(DateUtils.getTimeAll(date));
		buffer.append(" type:");
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
		return (int)Functions.b2l(getType(), 0, 2);
	}

	@Deprecated
	public int getTrapID()
	{
		return getTypeAsInt();
	}
}