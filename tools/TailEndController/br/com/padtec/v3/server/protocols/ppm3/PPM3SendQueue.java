package br.com.padtec.v3.server.protocols.ppm3;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3;
import br.com.padtec.v3.server.protocols.ppm3.requestcontrol.RequestControl;
import br.com.padtec.v3.server.protocols.util.io.queue.SendQueue;
import br.com.padtec.v3.server.protocols.util.io.readerwriter.AbstractReaderWriter;
import br.com.padtec.v3.util.Functions;
import br.com.padtec.v3.util.log.Log;


public class PPM3SendQueue implements SendQueue {
	private static final long HIGH_PRIORITY_INTERVAL = 300L;
	private static final long LOW_PRIORITY_INTERVAL = PPM3Collector.REGEN_INTERVAL;

	private Logger log = Log.getInstance();
	private boolean lastGetNextIsHigh;

	private LinkedList<PPM3> highPriorityQueue = new LinkedList<PPM3>();

	private LinkedList<PPM3> lowPriorityQueue = new LinkedList<PPM3>();
	private long nextHPRelease;
	private long nextLPRelease;
	private RequestControl controller;

	public synchronized void addPacket(PPM3 packet, boolean highPriority) {
		if (highPriority)
			this.highPriorityQueue.add(packet);
		else
			this.lowPriorityQueue.add(packet);
	}

	public void clear() {
		this.highPriorityQueue.clear();
		this.lowPriorityQueue.clear();
	}

	public boolean contains(PPM3 packet) {
		return ((!(this.highPriorityQueue.contains(packet))) &&(!(this.lowPriorityQueue.contains(packet))));
	}

	public boolean hasNextPacket() {
		long now = System.currentTimeMillis();

		return (this.nextHPRelease <= now) && (!this.highPriorityQueue.isEmpty()); 
//		&& (( (this.nextLPRelease > now) || (this.lowPriorityQueue.isEmpty()))));
	}

	public byte[] nextPacket() {
		PPM3 packet;
		long now = System.currentTimeMillis();
		PPM3 result = null;
		if ((this.nextHPRelease <= now) && (!(this.highPriorityQueue.isEmpty()))) {
			packet = (PPM3)this.highPriorityQueue.getFirst();
			if (packet != null) {
				this.lastGetNextIsHigh = true;
				result = packet;
			}
		}
		if ((result == null) && (this.nextLPRelease <= now) && 
				(!(this.lowPriorityQueue.isEmpty()))) {
			packet = (PPM3)this.lowPriorityQueue.getFirst();
			if (packet != null) {
				this.lastGetNextIsHigh = false;
				result = packet;
			}
		}
		if (result == null) {
			return null;
		}
//		System.out.println("PPM3SendQueue Enviando pacote: " + result.toString());
		return result.getBytes();
	}

	public void notifyNextPacketSent(AbstractReaderWriter rw) {
		PPM3 packet;
		if (this.lastGetNextIsHigh) {
			packet = (PPM3)this.highPriorityQueue.removeFirst();
			onHighPriorityPacketSent(rw.getConnection(), packet);
			this.nextHPRelease = (System.currentTimeMillis() + HIGH_PRIORITY_INTERVAL);
		} else {
			packet = (PPM3)this.lowPriorityQueue.removeFirst();
			this.nextLPRelease = (System.currentTimeMillis() + LOW_PRIORITY_INTERVAL);
		}
		if (rw != null) {
			CommunicationMonitor.getInstance().notifyPacket(rw.getConnection(), packet);
			this.controller.notifyPacketSent(rw.getConnection(), packet);
			if ((packet != null) && (rw.isPrintPacket())) {

				this.log.finest(new SimpleDateFormat(".SSS").format(new Date()) + " RW " + 
						rw.getConnection() + " write " + packet.toString());
			}
		}
		else if (packet != null) {
			this.log.finest(new SimpleDateFormat(".SSS").format(new Date()) + " write " + 
					Functions.getHexa(packet.getBytes()));
			this.log.finest(new SimpleDateFormat(".SSS").format(new Date()) + " write " + 
					packet.toString());
		}
	}

	protected void onHighPriorityPacketSent(String connection, PPM3 packet) {
	}

	public int size()
	{
		return (this.highPriorityQueue.size() + this.lowPriorityQueue.size());
	}

	public void setController(RequestControl controller)
	{
		this.controller = controller;
	}

	public synchronized void removePacket(Object packet) {
		List<Iterator<PPM3>> queues = new ArrayList<Iterator<PPM3>>();
		queues.add(highPriorityQueue.iterator());
		queues.add(lowPriorityQueue.iterator());

		for (Iterator<PPM3> i : queues) {
			Iterator<PPM3> iter = i;
			while (iter.hasNext())
				if (iter.next() == (PPM3)packet) {
					iter.remove();
					return;
				}
		}
	}
}