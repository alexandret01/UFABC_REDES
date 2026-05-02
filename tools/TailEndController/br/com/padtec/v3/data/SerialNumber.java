package br.com.padtec.v3.data;

import java.io.Serializable;

public final class SerialNumber implements Serializable, Comparable<SerialNumber> {
	private static final long serialVersionUID = 3L;
	private static final int VENDOR1 = 789;
	private static final int VENDOR2 = 836792;
	private static final int VENDOR2B = 845719;
	private final int sn;
	private final int part;

	public SerialNumber(int p, int sn)
	{
		this.part = p;
		this.sn = sn;
	}

	private static int calcVerif(int p)
	{
		int vendor = 121;
		if (p > 999) {
			vendor = 102;
		}
		int total = vendor + 3 * p / 100 + p / 10 + 3 * p % 10;
		int maxnumber = (int)(Math.floor(total / 10) * 10.0D) + 10;
		int ret = maxnumber - total;
		if (ret == 10)
			ret = 0;
		return ret;
	}

	public String toString()
	{
		StringBuilder prodString;
		if (this.part > 999)
			prodString = new StringBuilder(String.valueOf(this.part - 1000));
		else {
			prodString = new StringBuilder(String.valueOf(this.part));
		}
		if (prodString.length() == 1)
			prodString.insert(0, "00");
		if (prodString.length() == 2)
			prodString.insert(0, "0");
		StringBuffer seqString = new StringBuffer(String.valueOf(this.sn));
		if (seqString.length() == 1)
			seqString.insert(0, "0000");
		if (seqString.length() == 2)
			seqString.insert(0, "000");
		if (seqString.length() == 3)
			seqString.insert(0, "00");
		if (seqString.length() == 4)
			seqString.insert(0, "0");
		StringBuffer fullSerialNumber = new StringBuffer("(01)");
		fullSerialNumber.append(String.valueOf(789));
		if (this.part > 999)
			fullSerialNumber.append(String.valueOf(845719));
		else {
			fullSerialNumber.append(String.valueOf(836792));
		}
		fullSerialNumber.append(' ');
		fullSerialNumber.append(prodString);
		fullSerialNumber.append(' ');
		fullSerialNumber.append(String.valueOf(calcVerif(this.part)));
		fullSerialNumber.append(" (21)");
		fullSerialNumber.append(seqString);
		return fullSerialNumber.toString();
	}

	public String toShortString() {
		return "[" + getPart() + "#" + 
		getSeq() + "]";
	}

	public int hashCode()
	{
		return ((this.part << 6) + this.sn);
	}

	public int getSeq()
	{
		return this.sn;
	}

	public Integer getSeqKey()
	{
		return new Integer(this.sn);
	}

	public int getPart()
	{
		return this.part;
	}

	public Integer getPartKey()
	{
		return new Integer(this.part);
	}

	public boolean equals(Object obj)
	{
		if (this == obj) {
			return true;
		}
		if (obj instanceof SerialNumber) {
			SerialNumber other = (SerialNumber)obj;
			return ((this.part == other.part) && (this.sn == other.sn));
		}
		return false;
	}

	public int compareTo(SerialNumber other)
	{
		int result = this.sn - other.sn;
		if (result != 0) {
			return result;
		}

		return (this.part - other.part);
	}

	public static SerialNumber parseSerial(String cod)
	throws RuntimeException
	{
		if (cod == null) {
			return null;
		}
		int idx = cod.indexOf(35);
		try {
			int codProd = Integer.parseInt(cod.substring(0, idx));
			int nSer = Integer.parseInt(cod.substring(idx + 1));
			return new SerialNumber(codProd, nSer);
		} catch (RuntimeException e) {
			throw new RuntimeException("Invalid serial number format: " + cod);
		}
	}
}