package br.com.padtec.v3.server.protocols.ppm3;


import java.lang.reflect.Method;
import java.util.logging.Level;

import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3HistoryGet;
import br.com.padtec.v3.server.protocols.util.io.AbstractCommunicationMonitor;
import br.com.padtec.v3.util.log.Log;


public final class CommunicationMonitor extends AbstractCommunicationMonitor<PPM3> {
	private static final CommunicationMonitor instance = new CommunicationMonitor();

	public static CommunicationMonitor getInstance() {
		return instance;
	}

	public void notifyPacket(String rwId, PPM3 packet)  {
		try    {
			Method metodos = packet.getPayload().getClass().getMethod("getSerial", null);
			SerialNumber serial = (SerialNumber)metodos.invoke(packet.getPayload(), new Object[0]);
			log(rwId, serial, packet);
		} catch (NoSuchMethodException localNoSuchMethodException) {
		}
		catch (Exception e) {
			Log.getInstance(1).log(Level.SEVERE, "CommunicationMonitor exception", e);
		}
	}

	public void notifyHistoryResponse(String rwId, PPM3HistoryGet get, PPM3 response) {
		log(rwId, get.getSerial(), response);
	}
}