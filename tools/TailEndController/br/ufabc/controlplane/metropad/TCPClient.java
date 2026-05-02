package br.ufabc.controlplane.metropad;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.padtec.v3.server.protocols.ppm3.CommunicationMonitor;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3;
import br.com.padtec.v3.server.protocols.util.io.readerwriter.AbstractReaderWriter;
import br.com.padtec.v3.util.Functions;
import br.com.padtec.v3.util.log.Log;


public class TCPClient {
	private String ip;
	private int port;
	private String connectionDesk;
	private Socket socket;
	private int step = 0;
	private byte size1;
	private byte size2;
	private int size;
	private byte[] buffer;
	private int bufferPos;

	private OutputStream out;
	private InputStream in;
	
	private Thread reader;
	
	private Logger log = Log.getInstance();

	private LinkedList<byte[]> bytesToRead = new LinkedList<byte[]>(); 



	public TCPClient(String ip, int port) {
		this.ip = ip;
		this.port = port;
		this.connectionDesk = ip+":"+port;
		try {
			socket = new Socket(ip, port);
			out = socket.getOutputStream();
			in = socket.getInputStream();
			reader = new Thread(new ReaderStream());
			reader.start();
			
		} catch (UnknownHostException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}
	
	public void reconnect () throws UnknownHostException, IOException{
		socket = null;
		reader.interrupt();
		reader = null;
		
		socket = new Socket(ip, port);
		out = socket.getOutputStream();
		in = socket.getInputStream();
		reader = new Thread(new ReaderStream());
		reader.start();

	}

	public boolean isConnected(){
		return this.socket.isConnected();
	}
	
	public synchronized  byte[] getBytesReaded() {
		return bytesToRead.pollFirst();
	}
	
	public LinkedList<byte[]> getBytesToRead() {
		return bytesToRead;
	}

	public void close() throws IOException {
		this.socket.close();
		reader.interrupt();
	}

	public void write(byte[] data) throws IOException{
		this.out.write(data);
		bytesToRead.add(data);
	}

	public byte[] read() throws IOException{
		byte[] buffer = new byte[in.available()];
		if (buffer.length > 0){
			in.read(buffer);
			return buffer;
		}
		return null;
	}

	public String getConnection(){
		return connectionDesk;
	}

	public Socket getSocket(){
		return this.socket;
	}

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
				int bufferPos = 3;
				this.step = 3;
				return;
			case 3:
				this.buffer[(this.bufferPos++)] = Functions.i2b(data);
				if (this.bufferPos >= this.buffer.length) {
					PPM3 packet = PPM3.getPPM3(this.buffer);
					//	          this.queue.offer(packet);
					this.step = 0;
					//	          if (this.siteResponseMonitor != null) {
						//	            this.siteResponseMonitor.notifyPacketReceived(packet.getPPM3Source(), rw.getConnection());
					//	          }
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

	class ReaderStream implements Runnable{
		public ReaderStream() {

		}


		public void run() {
			while(isConnected()){
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				try {
					if(br.ready()){
						byte[] data = read();
						if (data != null)
							bytesToRead.add(data);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}
} 
