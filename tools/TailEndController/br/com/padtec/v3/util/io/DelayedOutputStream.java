package br.com.padtec.v3.util.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DelayedOutputStream extends OutputStream  {
	
	private static final LinkedBlockingQueue<QueueItem> pool = new LinkedBlockingQueue<QueueItem>(  1000);
	private final long delayTime;
	private boolean closeCalled;
	private ConcurrentLinkedQueue<QueueItem> queue = new ConcurrentLinkedQueue<QueueItem>();
	private IOException exception;

	public DelayedOutputStream(OutputStream out, long delayTime) {
		this.delayTime = delayTime;

		while(queue != null) {
			try{
				QueueItem element = queue.peek();
				if(element!=null){
					if(element.releaseTime < System.currentTimeMillis()){
						queue.poll();
						switch (element.event){
						case WRITE_BYTE:
							out.write(element.intValue);
							break;
						case WRITE_ARRAY:
							out.write(element.arrayValue, element.arrayOff, element.arrayLen);
							break;
						case FLUSH:
							out.flush();
							break;
						case CLOSE:
							out.close();
							break;
						}
						if (!(DelayedOutputStream.pool.offer(element))) {
							element = null; 
							break;
						}
					}
				}
				DelayedOutputStream.pool.clear();
			} catch (IOException e){
				exception = e;
			}
		}


	}



	private QueueItem newQueueItem() {
		QueueItem event = (QueueItem)pool.poll();
		if (event == null) {
			event = new QueueItem();
		}
		return event;
	}

	public void write(int b) throws IOException	{
		checkException();
		QueueItem event = newQueueItem();
		event.event = Event.WRITE_BYTE;
		event.intValue = b;
		add(event);
	}

	public void write(byte[] b, int off, int len) throws IOException
	{
		checkException();
		if (b == null)
			throw new NullPointerException();
		if ((off < 0) || (off > b.length) || (len < 0) || 
				(off + len > b.length) || (off + len < 0))
			throw new IndexOutOfBoundsException();
		if (len == 0) {
			return;
		}
		QueueItem event = newQueueItem();
		event.event = Event.WRITE_ARRAY;
		event.arrayValue = b;
		event.arrayOff = off;
		event.arrayLen = len;
		add(event);
	}

	public void close() throws IOException {
		checkException();
		QueueItem event = newQueueItem();
		event.event = Event.CLOSE;
		add(event);
		this.closeCalled = true;
	}

	public void flush() throws IOException	{
		checkException();
		QueueItem event = newQueueItem();
		event.event = Event.FLUSH;
		add(event);
	}

	private void add(QueueItem event)	{
		if (this.closeCalled) {
			return;
		}
		ConcurrentLinkedQueue<QueueItem> ref = this.queue;
		if (ref != null) {
			event.releaseTime = (System.currentTimeMillis() + this.delayTime);
			ref.add(event);
		}
	}

	private void checkException() throws IOException {
		if (this.exception != null) {
			IOException ref = this.exception;
			this.exception = null;
			throw ref;
		}
	}

	private static enum Event	{
		WRITE_BYTE, WRITE_ARRAY, FLUSH, CLOSE;
	}



	private static final class QueueItem	{
		public long releaseTime;
		public DelayedOutputStream.Event event;
		public int intValue;
		public byte[] arrayValue;
		public int arrayOff;
		public int arrayLen;
	}
}