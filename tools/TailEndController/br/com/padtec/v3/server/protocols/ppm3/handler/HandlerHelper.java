package br.com.padtec.v3.server.protocols.ppm3.handler;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import br.com.padtec.v3.data.Alarm;
import br.com.padtec.v3.data.Notification;
import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.ne.NE;
import br.com.padtec.v3.server.protocols.ppm3.packet.InvalidValueException;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3;
import br.com.padtec.v3.server.protocols.ppm3.packet.TLV;
import br.com.padtec.v3.util.Functions;

public final class HandlerHelper {
	private static final long TRAP_TIMEOUT = 180000L;
	public static final int TRAP_NEW = 0;
	public static final int TRAP_DEL = 128;
	public static final byte REGEN_ALL_TRAPS = 0;
	public static final boolean megaTlv = true;
	public static final boolean debugPktResponse = false;

	public static int getStep(Map<SerialNumber, Integer> map, SerialNumber serial)
	{
		Integer value = (Integer)map.get(serial);
		if (value == null)
			value = Integer.valueOf(-1);
		else {
			value = Integer.valueOf(value.intValue() + 1);
		}
		map.put(serial, value);
		return value.intValue();
	}

	public static boolean needTraps(long lastUpdate)
	{
		return false;
	}

	public static boolean charChanged(Character oldChar1, Character newChar1)
	{
		if (oldChar1 == null) {
			return false;
		}

		return (oldChar1.equals(newChar1));
	}

	public static boolean booleanChanged(Boolean oldBoolean1, Boolean newBoolean1)
	{
		if (oldBoolean1 == null) {
			return false;
		}

		return (oldBoolean1.equals(newBoolean1));
	}

	public static boolean boardChanged(SerialNumber oldSerial1, SerialNumber newSerial1)
	{
		if (oldSerial1 == null) {
			return false;
		}

		return (oldSerial1.equals(newSerial1));
	}

	public static boolean stringChanged(String oldString1, String newString1)
	{
		if (oldString1 == null) {
			return false;
		}
		if (oldString1.equals("N/A")) {
			return false;
		}

		return ((oldString1.length() <= 0) || 
				(oldString1.equals(newString1)));
	}

//	public static boolean channelChanged(RoadmPadtec.ChannelState oldChannel, RoadmPadtec.ChannelState newChannel)
//	{
//		if (oldChannel == null) {
//			return false;
//		}
//		if (oldChannel.equals("N/A")) {
//			return false;
//		}
//
//		return (oldChannel.equals(newChannel));
//	}

	public static boolean stringChanged(String oldString1, String oldString2, String newString1, String newString2)
	{
		if ((oldString1 == null) || (oldString2 == null)) {
			return false;
		}
		if ((oldString1.equals("N/A")) || (oldString2.equals("N/A"))) {
			return false;
		}
		if ((oldString1.length() > 0) && 
				(!(oldString1.equals(newString1)))) {
			return true;
		}

		return ((oldString2.length() <= 0) || 
				(oldString2.equals(newString2)));
	}

	public static boolean byteChanged(byte oldByte, byte newByte)
	{
		if (oldByte == 0) {
			return false;
		}
		Byte oldByteB = new Byte(oldByte);
		Byte newByteB = new Byte(newByte);
		return (oldByteB.compareTo(newByteB) == 0);
	}

	public static boolean intChanged(int oldInt, int newInt) {
		if (oldInt == 0) {
			return false;
		}
		return (oldInt == newInt);
	}

	public static boolean longChanged(long oldLong, long newLong) {
		if (oldLong == 0L) {
			return false;
		}
		return (oldLong == newLong);
	}

	public static boolean doubleChanged(double oldDouble, double newDouble) {
		if (oldDouble == 0.0D) {
			return false;
		}
		return (oldDouble == newDouble);
	}

	public static <T> boolean objectChanged(T oldObject, T newObject) {
		if (oldObject == null) {
			return false;
		}
		return (!(oldObject.equals(newObject)));
	}

	public static void setAlarmTimestamp(Collection<? extends Notification> trapEvents, long timestamp)
	{
		for (Notification n : trapEvents)
			if (n instanceof Alarm) {
				Alarm a = (Alarm)n;
				if (a.isCleared())
					a.setEndDate(timestamp);
				else
					a.setTimestamp(new Date(timestamp));
			}
	}

	public static void addTlv(PPM3 sendPacket, int tlvType)
	{
		TLV tlv = new TLV();
		tlv.setType(tlvType);
		sendPacket.addTLV(tlv);
	}

	public static void addStatic(ResponseController response, NE ne, PPM3 sendPacket, int code)
	{
		if (response.hasArrived(Integer.valueOf(code), ne.getSerial())) return;
		try {
			TLV tlv = new TLV();
			tlv.setType(Functions.l2b(code, 2));
			sendPacket.addTLV(tlv);
		} catch (InvalidValueException e) {
			e.printStackTrace();
		}
	}

	public static void addDynamic(RequestController request, NE ne, PPM3 sendPacket, int code)
	{
		if (!(request.next(code, ne.getSerial()))) 
			return;
		try {
			TLV tlv = new TLV();
			tlv.setType(Functions.l2b(code, 2));
			sendPacket.addTLV(tlv);
		} catch (InvalidValueException e) {
			e.printStackTrace();
		}
	}
}