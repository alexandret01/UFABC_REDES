package br.com.padtec.v3.server.protocols.ppm3.packet;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.util.Functions;



public class PPM3Trap implements PPM3Payload, HasSerialNumber, HasTlv<TimeTLV>{
	private SerialNumber serial;
	private List<TimeTLV> tlvList = new ArrayList<TimeTLV>(1);

	public PPM3Trap(){
	}

	public PPM3Trap(SerialNumber serial, long timestamp, int trapId)
	{
		setSerial(serial);
		addTLV(new TimeTLV(timestamp, trapId));
	}

	public PPM3Trap(SerialNumber serial, long timestamp, int trapId, byte[] data)
	throws InvalidValueException
	{
		setSerial(serial);
		addTLV(new TimeTLV(timestamp, trapId, data));
	}

	public PPM3Payload.Type getType()
	{
		return PPM3Payload.Type.TYPE_TRAP;
	}

	public SerialNumber getSerial()
	{
		return this.serial;
	}

	public void setSerial(SerialNumber serialNumber)
	{
		this.serial = serialNumber;
	}

	public void addTLV(TimeTLV tlv)
	{
		this.tlvList.add(tlv);
	}

	public TimeTLV getTLV(int idx)
	{
		return ((TimeTLV)this.tlvList.get(idx));
	}

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
		for (TimeTLV tlv : this.tlvList) {
			System.arraycopy(Functions.l2b(tlv.getTimestamp(), 8), 0, result, pos, 8);
			pos += 8;
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

	public int getSize()
	{
		int size = 4;
		for (TimeTLV tlv : this.tlvList) {
			size += tlv.getLength() + 12;
		}
		return size;
	}

	public void set(byte[] payload)
	throws BadPackageException
	{
		try
		{
			int pos = 0;
			int part = (int)Functions.b2l(payload, pos, 2);
			pos += 2;
			int serial = (int)Functions.b2l(payload, pos, 2);
			pos += 2;
			this.serial = new SerialNumber(part, serial);
			while (pos < payload.length) {
				TimeTLV tlv = new TimeTLV();
				tlv.setTimestamp(Functions.b2l(payload, pos, 8));
				pos += 8;
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
			if (e instanceof BadPackageException) {
				throw ((BadPackageException)e);
			}
			throw new BadPackageException(e);
		}
	}

	public String toString()
	{
		StringBuilder tlvStr = new StringBuilder();
		for (TimeTLV tlv : this.tlvList) {
			tlvStr.append(tlv.toString());
			tlvStr.append(' ');
		}
		return this.serial.toShortString() + ":" + tlvStr.toString();
	}

	public List<TimeTLV> getEvents()
	{
		return this.tlvList;
	}

	public void sort()
	{
		Collections.sort(this.tlvList, new Comparator<TimeTLV>() {
			public int compare(TimeTLV o1, TimeTLV o2) {
				if (o1 == o2) {
					return 0;
				}
				if (o1 == null) {
					return -1;
				}
				if (o2 == null) {
					return 1;
				}
				return (int)(o1.getTimestamp() - o2.getTimestamp());
			}
		});
	}
}