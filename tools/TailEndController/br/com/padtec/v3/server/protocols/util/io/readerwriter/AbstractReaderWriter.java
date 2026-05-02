package br.com.padtec.v3.server.protocols.util.io.readerwriter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import br.com.padtec.v3.server.protocols.util.io.AbstractConnectionAlarmManager;
import br.com.padtec.v3.server.protocols.util.io.queue.ReceiveQueue;
import br.com.padtec.v3.server.protocols.util.io.queue.SendQueue;
import br.com.padtec.v3.util.Functions;
import br.com.padtec.v3.util.StateHistory;
import br.com.padtec.v3.util.log.Log;

public abstract class AbstractReaderWriter extends Thread  {

	private boolean printPacket = Functions.getProperty("printPacket", false);

	private boolean printStackTrace = Functions.getProperty("printStackTrace",  false);

	   private static final Map<String, StateHistory<Exception>> connectionStatePool = Collections.synchronizedMap(new TreeMap<String, StateHistory<Exception>>());
	private final int connectionStateSize = 32;
	private ReceiveQueue receiveQueue;
	private SendQueue sendQueue;
	   private final AbstractConnectionAlarmManager connectionAlarmManager;
	private boolean shutdown = false;
	private boolean connected = false;

	private Logger log = Log.getInstance();
	private final String connection;
	private String lastProblem;
	protected Thread readT;
	protected Thread writeT;

	protected AbstractReaderWriter(String connection, ReceiveQueue receiveQueue, SendQueue sendQueue, 
			AbstractConnectionAlarmManager connectionAlarmManager){
		this.connection = connection;
		this.receiveQueue = receiveQueue;
		this.sendQueue = sendQueue;
		readT = new Thread(new ReaderRunnable(this));
		this.readT.setName(Thread.currentThread().getName() + " reader");
		this.readT.setDaemon(true);
		this.readT.start();
		writeT = new Thread(new WriterRunnable(this)); 
		this.writeT.setName(Thread.currentThread().getName() + " write");
		this.writeT.setDaemon(true);
		this.writeT.start();

		     this.connectionAlarmManager = connectionAlarmManager;
		     if(connectionAlarmManager != null)
		    	 this.connectionAlarmManager.setConnectionName(this.connection);

		     if (!(connectionStatePool.containsKey(this.connection))) 
		    	 connectionStatePool.put(this.connection, new StateHistory<Exception>(32));
	}

	   public List<StateHistory.State<Exception>> getReport()  {
	     return (connectionStatePool.get(this.connection)).getReport();
	   }

	public void startup()
	{
		setName("RW " + this.connection);
		setDaemon(false);
		start();
	}

	public void shutdown()
	{
		this.shutdown = true;
	}

	public void run()   {
		try   {
						
			_connect();


			while ((this.readT.isAlive()) || (this.writeT.isAlive())) {
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			_disconnect();

			       this.connectionAlarmManager.setNe(null);
			this.log.info("RW " + this.connection + " shutdown");
		} catch (Exception e) {
			//			if (this.printStackTrace) {
			e.printStackTrace();
			//			}
			this.log.warning("RW " + this.connection + " problem in start/stop: " + e.toString());
		}
	}

	private void _connect()  {
		try {
			this.receiveQueue.resetState();
			this.sendQueue.clear();
			connect();
			this.connected = true;
			       (connectionStatePool.get(this.connection)).report("Connection OK", null);
			       this.connectionAlarmManager.setConnectionUp();
			this.log.info("RW " + this.connection + " connected");
		} catch (Exception e) {
			//       if (this.printStackTrace) {
			e.printStackTrace();
			//       }
			       (connectionStatePool.get(this.connection)).report(e.toString(), e);
			       this.connectionAlarmManager.setConnectionDown(e);
			this.lastProblem = e.toString();
			if (!(e.toString().equals(this.lastProblem)))
				this.log.warning("RW " + this.connection + " problem while connecting: " +    e.toString());
		}
	}

	private void _disconnect()
	{
		try {
			this.connected = false;
			disconnect();
		}
		catch (IOException e) {
			if (this.printStackTrace)
				e.printStackTrace();
		}
	}

	protected abstract boolean handleReadException(Exception paramException);

	protected abstract void write(byte[] paramArrayOfByte)   throws IOException;

	protected abstract int read() throws IOException;

	protected abstract void connect() throws Exception;

	protected abstract void disconnect() throws IOException;

	public String getConnection()  {
		return this.connection;
	}

	   public AbstractConnectionAlarmManager getConnectionAlarmManager()
	   {
	     return this.connectionAlarmManager;
	   }

	public ReceiveQueue getReceiveQueue()
	{
		return this.receiveQueue;
	}

	public SendQueue getSendQueue()
	{
		return this.sendQueue;
	}

	public boolean isConnected()
	{
		return this.connected;
	}

	public boolean isPrintPacket() {
		return this.printPacket;
	}

	public boolean isPrintStackTrace() {
		return this.printStackTrace;
	}

	public void setPrintPacket(boolean value) {
		this.printPacket = value;
	}

	public void setPrintStackTrace(boolean value) {
		this.printStackTrace = value;
	}

	class ReaderRunnable implements Runnable {
		private AbstractReaderWriter rw;
		public ReaderRunnable(AbstractReaderWriter rw) {
			this.rw = rw;
		}
		public void run() {
			while (!rw.shutdown)
				try {
					if (rw.connected) {
						int data = rw.read();
//						if (data == -1){
//							rw._disconnect();
//							throw new IOException("Connection closed by peer");
//						}
						try {
							rw.receiveQueue.addByte(rw, data);
						} catch (RuntimeException e) {
							rw.log.severe("Error parsing packet " + e.toString()); 
						} 
					}

//					rw._disconnect();
//					try {
//						Thread.sleep(10000L);
//					} catch (InterruptedException ie) {
//						ie.printStackTrace();
//					}
//					if (!shutdown) {
//						rw._connect();
//					}

				}catch (Exception e) {
					//				if (rw.printStackTrace) {
					e.printStackTrace();
					//				}

					if (!(rw.handleReadException(e)))	{
						rw.log.warning("RW " + rw.connection +" problem in read: " +  e.toString());
						rw._disconnect();
						//					try {
						//						Thread.sleep(10000L);
						//					} catch (InterruptedException ie) {
						//						ie.printStackTrace();
						//					}

						//					rw._connect();
					}
				}
//				rw._disconnect();
		}
	}


	class WriterRunnable implements Runnable {
		private AbstractReaderWriter rw;
		public WriterRunnable(AbstractReaderWriter rw) {
			this.rw = rw;
		}
		public void run() {
			while (!(rw.shutdown)) {
				try {
//					if(!rw.connected)
//						rw._connect();
//						System.out.println("socket não conectado");
					if (rw.connected && rw.sendQueue.hasNextPacket()) {
						synchronized (rw.sendQueue) {
							byte[] data = rw.sendQueue.nextPacket();
							if(data != null && data.length > 0 ){
								rw.write(data);
								rw.sendQueue.notifyNextPacketSent(rw);
							} else {
								System.out.println("WriterRunnable: pacote vazio");
							}
						}
					}
					Thread.sleep(100L);

				}
				catch (Exception e) {
					if (rw.printStackTrace) {
						e.printStackTrace();
					}
					if(!shutdown){
						log.warning("RW " + AbstractReaderWriter.this.connection + " problem in writer: " + 
			                e.toString());
			            _disconnect();
					}
					
				}
			}

		}
	}


}