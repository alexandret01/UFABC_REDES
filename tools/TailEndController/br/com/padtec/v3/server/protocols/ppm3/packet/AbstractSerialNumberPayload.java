package br.com.padtec.v3.server.protocols.ppm3.packet;


import java.util.ArrayList;
import java.util.List;

import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.util.Functions;
/**
 * Improves a structure to the payloads GET and SET
 * 
 * 	Structure of the PPM3 packets's payload type GET e SET

               0             1        
        +-------------+-------------+
        |       Serial(part)        |
        +-------------+-------------+
		|       Serial(Seq)         |
        +-------------+-------------+
        |             .             |
        |         TLV List          |
        |             .             |
        +-------------+-------------+
 * 
 * */
public abstract class AbstractSerialNumberPayload implements PPM3Payload, HasSerialNumber, HasTlv<TLV>
{
	protected SerialNumber serial;
	protected List<TLV> tlvList = new ArrayList<TLV>();

	public AbstractSerialNumberPayload()	{
	}

	/**Creates a new Payload*/
	public AbstractSerialNumberPayload(SerialNumber serial, int type)
	{
		setSerial(serial);
		TLV tlv = new TLV();
		try {
			tlv.setType(Functions.l2b(type, 2));
		}
		catch (InvalidValueException e) {
			e.printStackTrace();
		}
		addTLV(tlv);
	}

	public AbstractSerialNumberPayload(SerialNumber serial, byte[] type, byte[] value)
	throws InvalidValueException
	{
		setSerial(serial);
		TLV tlv = new TLV(type, value);
		addTLV(tlv);
	}

	public SerialNumber getSerial()
	{
		return this.serial;
	}

	public void setSerial(SerialNumber serialNumber)
	{
		this.serial = serialNumber;
	}

	public void addTLV(TLV tlv)
	{
		this.tlvList.add(tlv);
	}

	/**
	 * Returns a TLV given a index of TLV list
	 * @param idx the index of TLV
	 * */
	public TLV getTLV(int idx)
	{
		return ((TLV)this.tlvList.get(idx));
	}
	/**
	 * Returns the length of the TLV List
	 * */
	public int getTLVCount()
	{
		return this.tlvList.size();
	}

	public byte[] getBytes()
	{
		byte[] result = new byte[getSize()];
		if (this.serial != null) {
			Functions.setBytes(result, 0, this.serial.getPart(), 2);
			Functions.setBytes(result, 2, this.serial.getSeq(), 2);
		}
		int pos = 4;
		for (TLV tlv : this.tlvList) {
			System.arraycopy(tlv.getType(), 0, result, pos, 2);
			pos += 2;
			Functions.setBytes(result, pos, tlv.getLength(), 2);
			pos += 2;
			if (tlv.getValue() != null) {
				System.arraycopy(tlv.getValue(), 0, result, pos, tlv.getLength());
				pos += tlv.getLength();
			}
		}
		return result;
	}

	public int getSize()
	{
		int size = 4;
		for (TLV tlv : this.tlvList) {
			size += tlv.getLength() + 4;
		}
		return size;
	}

	public void set(byte[] payload)
	throws BadPackageException
	{
		try
		{
			int part = (int)Functions.b2l(payload, 0, 2);
			int serial = (int)Functions.b2l(payload, 2, 2);
			this.serial = new SerialNumber(part, serial);
			int pos = 4;
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
		} catch (Exception e) {
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
		return this.serial.toShortString() + ":" + tlvStr.toString();
	}
}