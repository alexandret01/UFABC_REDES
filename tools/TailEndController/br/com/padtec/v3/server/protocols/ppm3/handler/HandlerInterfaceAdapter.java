package br.com.padtec.v3.server.protocols.ppm3.handler;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

import br.com.padtec.v3.data.Notification;
import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.impl.NE_Impl;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3Factory;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3Response;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3Trap;
import br.com.padtec.v3.server.protocols.ppm3.packet.TLV;
import br.com.padtec.v3.server.protocols.ppm3.packet.TimeTLV;
import br.com.padtec.v3.util.log.Log;

public class HandlerInterfaceAdapter implements HandlerInterface<PPM3> {
	private final GenericHandler handler;
	private final Map<SerialNumber, Integer> request = new TreeMap<SerialNumber, Integer>();
	private final ResponseController response = new ResponseController();
	private final Map<SerialNumber, TreeMap<Integer, byte[]>> tlvHistory = new TreeMap<SerialNumber, TreeMap<Integer,byte[]>>();

	public HandlerInterfaceAdapter(GenericHandler handler) {
		this.handler = handler;
	}

	public boolean canHandle(NE_Impl ne)
	{
		return this.handler.canHandle(ne);
	}

	public List<PPM3> getUpdatePacketList(NE_Impl ne)
	{
		List<Integer> toSend = this.handler.getRequestCode(ne, GenericHandler.CodeType.DYNAMIC);

		List<Integer> stat = this.handler.getRequestCode(ne, GenericHandler.CodeType.STATIC);
		for (Integer code : stat) {
			if (!(this.response.hasArrived(code, ne.getSerial()))) {
				toSend.add(code);
			}
		}
		stat = null;

		Integer step = (Integer)this.request.get(ne.getSerial());
		if (step == null) {
			step = Integer.valueOf(0);
		}
		if (step.intValue() % 3 == 0) {
			toSend.addAll(this.handler.getRequestCode(ne, GenericHandler.CodeType.OCCASIONAL));
		}
		step = Integer.valueOf(step.intValue() + 1);
		this.request.put(ne.getSerial(), step);

		List<PPM3> result = new LinkedList<PPM3>();
		int responseSize = 2147483647;
		PPM3 sendPacket = null;
		for (Integer code : toSend)
		{
			this.response.registerCode(code);

			if (responseSize > 450) {
				sendPacket = PPM3Factory.getGetPPM3(ne.getSupAddress(), ne.getSerial());
				result.add(sendPacket);
				responseSize = 0;
			}

			HandlerHelper.addTlv(sendPacket, code.intValue());
			responseSize += this.handler.getExpectedResponseSize(ne, code.intValue());
		}

		toSend = null;

		return ((List<PPM3>)result);
	}

	public boolean isFullUpdated(SerialNumber serial)
	{
		return this.response.hasAllArrivals(serial);
	}

	public boolean onReceiveResponse(NE_Impl ne, PPM3 pacote, Collection<Notification> notifications)
	{
		if (pacote.getPayload() instanceof PPM3Response) {
			PPM3Response response = (PPM3Response)pacote.getPayload();

			Map<Integer,byte[]> tlvs = new TreeMap<Integer, byte[]>();
			for (int i = 0; i < response.getTLVCount(); ++i) {
				TLV tlv = response.getTLV(i);
				tlvs.put(Integer.valueOf(tlv.getTypeAsInt()), tlv.getValue());
				this.response.registerArrival(Integer.valueOf(tlv.getTypeAsInt()), ne.getSerial());
			}

			this.handler.analyzeResponse(ne, tlvs, this.tlvHistory.get(ne.getSerial()), 
					notifications, new TreeMap<Integer,Double>());

			return true;
		}
		return false;
	}

	public boolean onReceiveTrap(NE_Impl ne, PPM3 pacote, List<PPM3> packetToSend, List<Notification> event, boolean history)
	{
		if (pacote.getPayload() instanceof PPM3Trap) {
			PPM3Trap trapPacket = (PPM3Trap)pacote.getPayload();
			List<Notification> alarms = new ArrayList<Notification>();
			Iterator<TimeTLV> localIterator = trapPacket.getEvents().iterator(); 

			while (true) { 
				TimeTLV tlv = localIterator.next();
				try {
					alarms.clear();
					if ((handler.analyzeTrap(ne, tlv.getTypeAsInt(), tlv.getValue(), alarms)) && 
							(!(history))) {
						prepareFullUpdate(ne);
					}

					HandlerHelper.setAlarmTimestamp(alarms, tlv.getTimestamp());
					event.addAll(alarms);
				} catch (RuntimeException e) {
					Log.getInstance(1).log(Level.SEVERE, 
							"Handler:fail parsing trap:" + e.toString(), e);
				}
				if (!(localIterator.hasNext()))
				{
					return true; 
				} 
			}
		}
		return false;
	}

	public boolean prepareFullUpdate(NE_Impl ne)
	{
		this.request.put(ne.getSerial(), Integer.valueOf(0));
		this.response.clearArrival(ne.getSerial());
		return true;
	}
}