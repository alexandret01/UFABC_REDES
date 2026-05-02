package br.com.padtec.v3.server.protocols.ppm3;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;

import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3;
import br.com.padtec.v3.server.protocols.util.io.queue.ReceiveQueue;
import br.com.padtec.v3.server.protocols.util.io.readerwriter.AbstractReaderWriter;
import br.com.padtec.v3.server.protocols.util.timeout.SiteResponseMonitor;
import br.com.padtec.v3.util.Functions;
import br.com.padtec.v3.util.log.Log;


public final class PPM3ReceiveQueue implements ReceiveQueue
{
	private SiteResponseMonitor siteResponseMonitor;
	private int step = 0;
	private byte size1;
	private byte[] buffer;
	private int bufferPos;
	private final Queue<PPM3> queue = new ArrayBlockingQueue<PPM3>(1000);

	public void addByte(AbstractReaderWriter rw, int data)  {
		try
		{
			switch (this.step)
			{
			case 0:
				if (data == 48)
				{
					this.step = 1;
				}
				return;
			case 1:
				this.size1 = Functions.i2b(data);
				this.step = 2;
				return;
			case 2:
				byte size2 = Functions.i2b(data);
				int size = Functions.b2i((byte)0, (byte)0, this.size1, size2);

				if ((this.buffer == null) || (this.buffer.length != size)) {
					this.buffer = null;
					this.buffer = new byte[size];
				}
				this.buffer[0] = 48;
				this.buffer[1] = this.size1;
				this.buffer[2] = size2;
				this.bufferPos = 3;
				this.step = 3;
				return;
			case 3:
				this.buffer[(this.bufferPos++)] = Functions.i2b(data);
				if (this.bufferPos >= this.buffer.length) {
					PPM3 packet = PPM3.getPPM3(this.buffer);
					this.queue.offer(packet);
					this.step = 0;
					if (this.siteResponseMonitor != null) {
						this.siteResponseMonitor.notifyPacketReceived(packet.getSource(), rw.getConnection());
					}
					if (rw != null) {
						CommunicationMonitor.getInstance().notifyPacket(rw.getConnection(), 
								packet);
						if (rw.isPrintPacket())
						{
							Log.getInstance().finest(
									new SimpleDateFormat(".SSS").format(new Date()) + " RW " + 
									rw.getConnection() + " read " + packet.toString());
						}
					}
				}
				return;
			}
		} catch (Exception e) {
			this.step = 0;
			if (rw != null) {
				String msg = "PPM3 RW:" + rw.getConnection() + " " + e.toString() + 
				": " + Functions.getHexa(this.buffer);
				Log.getInstance(2).log(Level.WARNING, msg, e);
			}
		}
	}

	public void resetState() {
		this.step = 0;
		this.buffer = null;
		this.bufferPos = 0;
		this.size1 = 0;
	}

	public boolean isEmpty()
	{
		return this.queue.isEmpty();
	}

	public PPM3 nextPacket()
	{
		//	  System.out.println("PPM3ReceivedQueue Lendo pacote: " + this.queue.peek());
		return ((PPM3)this.queue.poll());
	}

	public SiteResponseMonitor getSiteResponseMonitor()
	{
		return this.siteResponseMonitor;
	}

	public void setSiteResponseMonitor(SiteResponseMonitor siteResponseMonitor)
	{
		this.siteResponseMonitor = siteResponseMonitor;
	}
}