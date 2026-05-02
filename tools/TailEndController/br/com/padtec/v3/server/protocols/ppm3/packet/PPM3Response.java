package br.com.padtec.v3.server.protocols.ppm3.packet;


import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.util.Functions;


/**
 * Improves a structure to the payload type Response
 * 
 *                0             1        
        +-------------+-------------+
        |        Serial(part)       |
        +-------------+-------------+
		|        Serial(Seq)        |
        +-------------+-------------+
        | 			    			|
        |        Time Stamp         |
        |         (8 bytes)         |
        |                           |
		+-------------+-------------+
        |             .             |
        |         TLV List          |
        |             .             |
        +-------------+-------------+
 *  
 * */
public class PPM3Response implements PPM3Payload, HasSerialNumber, HasTimestamp, HasTlv<TLV>
{
	private SerialNumber serial;
	private long timestamp;
	private List<TLV> tlvList = new ArrayList<TLV>();

	public PPM3Response() 	{
	}

	public PPM3Response(SerialNumber serial, long time, int type, byte[] value)
	throws InvalidValueException
	{
		setSerial(serial);
		setTimestamp(time);
		addTLV(new TLV(Functions.l2b(type, 2), value));
	}

	public SerialNumber getSerial()
	{
		return this.serial;
	}

	public void setSerial(SerialNumber serialNumber)
	{
		this.serial = serialNumber;
	}

	public long getTimestamp()
	{
		return this.timestamp;
	}

	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}

	public void addTLV(TLV tlv)
	{
		this.tlvList.add(tlv);
	}

	public TLV getTLV(int idx)
	{
		return ((TLV)this.tlvList.get(idx));
	}

	public int getTLVCount()
	{
		return this.tlvList.size();
	}

	public PPM3Payload.Type getType()
	{
		return PPM3Payload.Type.TYPE_RESPONSE;
	}

	public byte[] getBytes()
	{
		byte[] result = new byte[getSize()];
		if (this.serial != null) {
			Functions.setBytes(result, 0, this.serial.getPart(), 2);
			Functions.setBytes(result, 2, this.serial.getSeq(), 2);
		}
		Functions.setBytes(result, 4, this.timestamp, 8);
		int pos = 12;
		for (TLV tlv : this.tlvList) {
			System.arraycopy(tlv.getType(), 0, result, pos, 2);
			pos += 2;
			System.arraycopy(Functions.l2b(tlv.getLength(), 2), 0, result, pos, 2);
			pos += 2;
			if (tlv.getValue() != null) {
				System.arraycopy(tlv.getValue(), 0, result, pos, tlv.getLength());
				pos += tlv.getLength();
			}
		}
		return result;
	}

	public int getSize() {
		int size = 12;
		for (TLV tlv : this.tlvList) {
			size += tlv.getLength() + 4;
		}
		return size;
	}

	public void set(byte[] payload) throws BadPackageException {
		try {
			int part = (int)Functions.b2l(payload, 0, 2);
			int serial = (int)Functions.b2l(payload, 2, 2);
			this.serial = new SerialNumber(part, serial);
			this.timestamp = Functions.b2l(payload, 4, 8);
			int pos = 12;
			while (pos < payload.length) {
				TLV tlv = new TLV();
				tlv.setType(Functions.getSubarray(payload, pos, 2));
				pos += 2;
				int length = (int)Functions.b2l(payload, pos, 2);
				pos += 2;
				if (length > 0) {
					tlv.setValue(Functions.getSubarray(payload, pos, length));
					pos += length;
				}
				this.tlvList.add(tlv);
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (InvalidValueException e) {
			throw new BadPackageException(e);
		}
	}

	public String toString()
	{
		StringBuilder tlvStr = new StringBuilder();
		for (TLV tlv : this.tlvList) {
			tlvStr.append(tlv.toString());
			tlvStr.append(' ');
		}
		return this.serial.toShortString() + 
		":" + 
		DateFormat.getDateTimeInstance(2, 2)
		.format(new Date(this.timestamp)) + 
		":" + tlvStr.toString();
	}
}