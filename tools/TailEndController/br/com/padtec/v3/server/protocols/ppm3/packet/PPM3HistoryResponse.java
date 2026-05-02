package br.com.padtec.v3.server.protocols.ppm3.packet;

import java.util.ArrayList;
import java.util.List;

import br.com.padtec.v3.util.Functions;

public class PPM3HistoryResponse implements PPM3Payload
{
	private boolean hasNext = false;
	private int historyHeadSize = 8;

	private List<PPM3Payload> list = new ArrayList<PPM3Payload>(5);
	private EnumHistoryType historyType;

	public void addBlock(PPM3Payload tlv)
	{
		this.list.add(tlv);
	}

	public List<PPM3Payload> getList()
	{
		return this.list;
	}

	public PPM3Payload.Type getType()
	{
		return Type.TYPE_HISTORY_RESPONSE;
	}

	public byte[] getBytes()
	{
		byte[] result = new byte[getSize()];

		int pos = 4;

		Functions.setBytes(result, pos, this.historyType.getResponseCode(), 2);
		pos += 2;

		result[(pos++)] = Functions.l2b(result.length - 8, 2)[0];
		result[(pos++)] = Functions.l2b(result.length - 8, 2)[1];

		for (PPM3Payload item : this.list)	{
			pos += 2;

			byte[] boardBytes = item.getBytes();
			result[(pos++)] = Functions.l2b(boardBytes.length, 2)[0];
			result[(pos++)] = Functions.l2b(boardBytes.length, 2)[1];

			System.arraycopy(boardBytes, 0, result, pos, boardBytes.length);
			pos += boardBytes.length;
		}
		return result;
	}

	public void set(byte[] payload)
	throws BadPackageException
	{
		try
		{
			int pos = 4;
			int hasNext = 0;
			this.historyType = EnumHistoryType.getResponseType((int)Functions.b2l(
					payload, pos, 2));
			pos += 2;
			hasNext = (int)Functions.b2l(payload, pos, 2);
			if (hasNext == 0) {
				this.hasNext = false; return;
			}
			this.hasNext = true;
			pos += 2;
			int tlvSize = 0;
			while (pos != payload.length) {
				pos += 2;
				tlvSize = (int)Functions.b2l(payload, pos, 2);
				pos += 2;

				PPM3Trap trap = new PPM3Trap();
				trap.set(Functions.getSubarray(payload, pos, tlvSize));
				addBlock(trap);

				pos += tlvSize;
			}
		}
		catch (Exception e) {
			if (e instanceof BadPackageException) {
				throw ((BadPackageException)e);
			}
			throw new BadPackageException(e);
		}
	}

	public int getSize()
	{
		int size = 0;
		for (PPM3Payload item : this.list) {
			size += 4 + item.getSize();
		}
		return (size + this.historyHeadSize);
	}

	public boolean isHasNext() {
		return this.hasNext;
	}

	public String toString()
	{
		StringBuilder result = new StringBuilder();
		for (PPM3Payload item : this.list) {
			result.append("[");
			result.append(item.toString());
			result.append("]");
		}
		return result.toString();
	}

	public EnumHistoryType getHistoryType()
	{
		return this.historyType;
	}

	public void setHistoryType(EnumHistoryType historyType)
	{
		this.historyType = historyType;
	}
}