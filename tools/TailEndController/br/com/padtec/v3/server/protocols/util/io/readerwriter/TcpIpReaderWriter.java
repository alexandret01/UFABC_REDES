package br.com.padtec.v3.server.protocols.util.io.readerwriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import br.com.padtec.v3.server.protocols.util.io.AbstractConnectionAlarmManager;
import br.com.padtec.v3.server.protocols.util.io.SocketFactory;
import br.com.padtec.v3.server.protocols.util.io.queue.ReceiveQueue;
import br.com.padtec.v3.server.protocols.util.io.queue.SendQueue;

public class TcpIpReaderWriter extends AbstractReaderWriter {
  private String ip;
  private int port;
  private Socket socket;
  private InputStream in;
  private OutputStream out;
  private TcpEventHandler handler;
  private static int SOTIMEOUT = 30000;

  public TcpIpReaderWriter(String connection, String ip, int port, ReceiveQueue inputQueue, SendQueue outputQueue, AbstractConnectionAlarmManager connectionAlarmManager)
  {
    super(connection, inputQueue, outputQueue, connectionAlarmManager);
    this.ip = ip;
    this.port = port;
  }

  protected void connect() throws Exception {
//	  System.out.println("TcpIpReaderWriter.connect()" + ip + ":" + port);
//    this.socket = SocketFactory.getInstance().newSocket(Functions.getInetAddress(this.ip), this.port);
	if(socket == null)
	  this.socket =  new Socket(ip, port);
		
	this.socket.setSoTimeout(SOTIMEOUT);
    this.in = this.socket.getInputStream();
    this.out = this.socket.getOutputStream();
    if (this.handler != null)
      this.handler.onConnect(this);
  }

  protected void disconnect()
    throws IOException
  {
    SocketFactory.getInstance().cancel(this);
    SocketFactory.getInstance().cancel(this.readT);
    if (this.socket != null) {
      this.socket.close();
      this.socket = null;
      this.in = null;
      this.out = null;
    }
  }

  protected int read()  throws IOException
  {
    int data = this.in.read();
    if (this.handler != null) {
      return this.handler.onRead(data);
    }
    return data;
  }

  protected void write(byte[] data) throws IOException {
    this.out.write(data);
  }

  protected boolean handleReadException(Exception e) {
    if (this.handler != null) {
      return this.handler.handleReadException(e);
    }
    return false;
  }

  public TcpEventHandler getHandler() {
    return this.handler;
  }

  public void setHandler(TcpEventHandler handler)
  {
    this.handler = handler;
  }

  public static abstract interface TcpEventHandler {
    public abstract int onRead(int paramInt);

    public abstract boolean handleReadException(Exception paramException);

    public abstract void onConnect(TcpIpReaderWriter paramTcpIpReaderWriter);
  }
}