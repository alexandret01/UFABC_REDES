package br.com.padtec.v3.server.protocols.ppm3.packet;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;

import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.util.Functions;
import br.com.padtec.v3.util.math.Counter;



public final class PPM3Factory {
	private static Counter counter = new Counter(0L, 255L, true);

	public static PPM3 getTrapPPM3(int ne, SerialNumber s, int event, long time) {
		PPM3Trap payload = new PPM3Trap(s, time, event);
		PPM3 trap = PPM3.newPpm3(ne, 0, 0L, payload);
		return trap;
	}

	public static PPM3 getErrorPPM3(int ne, SerialNumber s, int type) {
		PPM3Error payload = new PPM3Error();
		payload.setSerial(s);
		payload.setTimestamp(System.currentTimeMillis());
		PPM3 error = PPM3.newPpm3(ne, 0, 0L, payload);
		return error;
	}

	public static PPM3 getSetPPM3(int ne, SerialNumber s) {
		PPM3Set payload = new PPM3Set();
		payload.setSerial(s);
		PPM3 set = PPM3.newPpm3(0, ne, 0L, payload);
		return set;
	}
	/**
	 * @param ne the address of destination
	 * @param s is the Serial Number
	 * */
	public static PPM3 getGetPPM3(int ne, SerialNumber s) {
		PPM3Get payload = new PPM3Get();
		
		payload.setSerial(s);
		PPM3 packet = PPM3.newPpm3(0, ne, 0L, payload);
		
		return packet;
	}

	public static PPM3 getResponsePPM3(int ne, SerialNumber s) {
		PPM3Response payload = new PPM3Response();
		payload.setSerial(s);
		payload.setTimestamp(System.currentTimeMillis());
		PPM3 resp = PPM3.newPpm3(ne, 0, 0L, payload);
		return resp;
	}

	public static void addTLV(HasTlv<TLV> r, byte[] cmd, byte[] data) throws InvalidValueException
	{
		TLV t = new TLV(cmd, data);
		r.addTLV(t);
	}

	public static PPM3 getDummyPPM3() {
		try {
			PPM3Get get = new PPM3Get(new SerialNumber(12, 12), 67, new byte[] { 1, 
				2, 3 });
			PPM3 ppm3 = PPM3.newPpm3(0, 3, counter.next(), get);
			return ppm3;
		} catch (InvalidValueException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static PPM3 getDummyPPM3Trap() {
		try {
			PPM3Trap ppm3 = new PPM3Trap(new SerialNumber(12, 12), 
					System.currentTimeMillis(), -2, new byte[] { 1, 2, 3 });
			return PPM3.newPpm3(1, 1, counter.next(), ppm3);
		} catch (InvalidValueException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static PPM3 getDummyPPM3HistoryGet()
	{
		return null;
	}

	public static void main(String[] args) {
		PPM3 ppm3 = getDummyPPM3();
		System.err.println(Functions.getHexa(ppm3.getBytes(), 0, ppm3.getSize()));
		try {
			Socket s = new Socket(InetAddress.getByName("10.2.22.5"), 8886);
			s.getOutputStream().write(ppm3.getBytes());
			s.getOutputStream().flush();
			while (s.isConnected()){
				InputStream in =  s.getInputStream();
				in.read();
			}
		}
		
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}