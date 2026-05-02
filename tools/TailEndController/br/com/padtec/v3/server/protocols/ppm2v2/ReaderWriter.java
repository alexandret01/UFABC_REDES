package br.com.padtec.v3.server.protocols.ppm2v2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.padtec.v3.data.impl.SupSPVJ_Impl;
import br.com.padtec.v3.server.protocols.util.io.AbstractConnectionAlarmManager;
import br.com.padtec.v3.server.protocols.util.io.SocketFactory;
import br.com.padtec.v3.util.Functions;
import br.com.padtec.v3.util.StateHistory;
import br.com.padtec.v3.util.log.Log;

public class ReaderWriter extends Thread {
  public static final Map<String, StateHistory<Exception>> connectionStatePool = Collections.synchronizedMap(new TreeMap<String, StateHistory<Exception>>());
  private final int connectionStateSize = 32;

  private boolean printPacket = Functions.getProperty("printPacket", false);

  private boolean printStackTrace = Functions.getProperty("printStackTrace", 
    false);

  private long connectFailureTimewait = 10000L;
  private Socket socket;
  private String ip;
  private int port;
//  private SerialDealer serialDealer;
  private InputStream in;
  private OutputStream out;
  private List<PPM2v2> inboundPackets;
  private boolean continua = true;
  private boolean isConnected;
  private boolean serial;
  private Logger log = Log.getInstance();
  private static final int TCP_READ_TIMEOUT = 30000;
  public static final int STATUS_OFF = 0;
  public static final int STATUS_ON = 1;
  private int status = 0;
  private String serialPort;
  private int initTimeout;
  private int baudRate;
  private AbstractConnectionAlarmManager connectionAlarmManager;
  public SupSPVJ_Impl supervisor;

  public ReaderWriter(String addrIP, int port)
  {
    this.inboundPackets = Collections.synchronizedList(new LinkedList<PPM2v2>());
    this.ip = addrIP;
    this.port = port;

    this.serial = false;
    init();
  }

  public ReaderWriter(String porta, int initTimeout, int baudRate)
  {
    this.inboundPackets = Collections.synchronizedList(new LinkedList<PPM2v2>());
    this.serial = true;
    this.serialPort = porta;
    this.initTimeout = initTimeout;
    this.baudRate = baudRate;
    init();
  }

  private void init() {
    if (!(connectionStatePool.containsKey(getConection())))
      connectionStatePool.put(getConection(), new StateHistory<Exception>(connectionStateSize));
  }

  private void tcpIpConnect() {
    try   {
//      if (NSimulators.nSimulators) {
//        this.log.warning("Simulation mode. To turn off change NSimulators.nSimulators value to false.");
//
//        this.socket = SocketFactory.getInstance().newSocket(Functions.getInetAddress(NSimulators.simulatorIP), this.port);
//      } else {
//        this.socket = SocketFactory.getInstance().newSocket(Functions.getInetAddress(this.ip), this.port);
//      }
    	
    	if(socket == null)
    		this.socket =  new Socket(ip, port);
    	this.socket.setSoTimeout(TCP_READ_TIMEOUT);
    	this.isConnected = true;
    	this.log.config("RW " + getConection() + " connected");

    	this.out = this.socket.getOutputStream();
    	this.in = this.socket.getInputStream();

    	this.out.write( new PPM2v2((byte)2, (byte)0, null, null, null).getRawBytes());
    	this.out.flush();
    	connectionStatePool.get(getConection()).report("Connection OK", null);
    	this.connectionAlarmManager.setConnectionUp();
    } catch (Exception e) {
//      if (this.printStackTrace) {
        e.printStackTrace();
//      }
      this.isConnected = false;
      TreeMap<String, Object> supConnection = SupjUdpService.getSupConnection(this.ip);
      if (supConnection != null) {
        String connectedIp = (String)supConnection.get("CONNECTED_IP");
        if ((connectedIp != null) && (!("0.0.0.0".equals(connectedIp)))) {
          e = new ConnectException("SPVJ connected with " + connectedIp);
        }
      }
      if (connectionStatePool.get(getConection()).report(e.toString(), e)) {
        this.log.warning("RW " + getConection() + " problem in tcpIpConnect: " + e.toString());
      }
      this.connectionAlarmManager.setConnectionDown(e);
      try {
        Thread.sleep(this.connectFailureTimewait);
      }
      catch (InterruptedException localInterruptedException) {
      }
    }
  }

  private void tcpIpDisconnect() {
    if (this.socket == null)
      return;
    if (this.supervisor != null)
      try {
        byte[] destino = PPM2v2Helper.getBytes(this.supervisor);
        PPM2v2 close = new PPM2v2((byte)2, (byte)20, null, destino, null);
        this.out.write(close.getRawBytes());
      }
      catch (Exception localException) {
      }
    try {
      this.socket.close();
    } catch (IOException e) {
      this.log.warning("RW " + getConection() + " problem in tcpIpDisconnect: " + 
        e.toString());
    }
    this.socket = null;
  }

//  private void serialConnect() {
//    try
//    {
//      this.serialDealer = new SerialDealer(this.serialPort, this.initTimeout, this.baudRate);
//      this.isConnected = true;
//      this.log.config("RW " + getConection() + " connected");
//
//      SerialInputStream sis = new SerialInputStream(
//        this.serialDealer.getInputStream());
//      this.in = sis;
//      this.out = new SerialOutputStream(this.serialDealer.getOutputStream(), sis);
//
//      this.out.write(
//        new PPM2v2(2, 0, null, null, null).getRawBytes());
//      ((StateHistory)connectionStatePool.get(getConection()))
//        .report("Connection OK", null);
//      this.connectionAlarmManager.setConnectionUp();
//
//      Thread.sleep(ColetorPPM2v2.HIGH_PRIORITY_INTERVAL);
//    } catch (Exception e) {
//      this.isConnected = false;
//      if (((StateHistory)connectionStatePool.get(getConection())).report(e.toString(), e)) {
//        this.log.warning("RW " + getConection() + " problem in serialConnect: " + 
//          e.toString());
//      }
//      this.connectionAlarmManager.setConnectionDown(e);
//    }
//  }

//  private void serialDisconnect() {
//    if (this.serialDealer != null)
//      this.serialDealer.close();
//  }

  public void shutdown()
  {
    this.continua = false;
    SocketFactory.getInstance().cancel(this);
  }

  public List<PPM2v2> getInboundPackets()
  {
    return this.inboundPackets;
  }

  public synchronized boolean write(byte[] dataToWrite)  {
    try {
      this.out.write(dataToWrite);
      if (this.printPacket) {
        this.log.finest(new SimpleDateFormat(".SSS").format(new Date()) + " RW " + 
          getConection() + " write " + 
          PPM2v2Helper.parsePacket(dataToWrite));
      }
      return true;
    } catch (Exception e) {
      this.log.warning("RW " + getConection() + " problem in write " + e.toString());
      if (this.printStackTrace) {
        e.printStackTrace();
      }
      this.isConnected = false; }
    return false;
  }

  private void read(InputStream is, byte[] buffer, int off, int len) throws IOException  {
    while (true)  {
      int bytesRead = is.read(buffer, off, len);
      if (bytesRead == len)  {
        return;
      }
      if (bytesRead < 0) {
        throw new IOException("Not enough data - end of stream");
      }
      if (!(this.continua)) {
        throw new IOException("Not enough data - shutdown");
      }
      try {
        Thread.sleep(100L);
      } catch (InterruptedException localInterruptedException) {
      }
      off += bytesRead;
      len -= bytesRead;
    }
  }

  public void run() {
    try  {
      connect();
      this.status = 1;

      boolean keepAliveSent = false;
      while (this.continua) {
        byte[] data = (byte[])null;
        try {
          if (this.isConnected) {
            byte[] header = new byte[3];
            do {
              read(this.in, header, 0, 1);
              keepAliveSent = false; }
            while (header[0] != PPM2v2.version);

            synchronized (this) {
              read(this.in, header, 1, 2);
              int size = Functions.b2i(header[2]);

              data = new byte[15 + size];
              System.arraycopy(header, 0, data, 0, 3);
              header = (byte[])null;

              read(this.in, data, 3, data.length - 3);
              PPM2v2 pacote = new PPM2v2(data);
              if (this.printPacket) {
                try {
                  this.log.finest(new SimpleDateFormat(".SSS").format(new Date()) + 
                    " RW " + getConection() + " read " + 
                    PPM2v2Helper.parsePacket(data));
                } catch (Exception e) {
                  e.printStackTrace();
                }
              }
              CommunicationMonitor.getInstance().notifyPacket(getConection(), 
            		  pacote, PPM2v2Helper.getSerial(pacote.getSource()));
              this.inboundPackets.add(pacote);
            }
          }
//          connect();
          keepAliveSent = false;
        }
        catch (BadPackageException e) {
          String msg = "PPM2v2 RW:" + getConection() + " " + e.toString();
          if (data != null) {
            msg = msg + ": " + Functions.getHexa(data);
          }
          Log.getInstance(2).log(Level.WARNING, msg);
        } catch (IOException e) {
          if (this.continua)
            if (("Read timed out".equalsIgnoreCase(e.getMessage())) && 
              (!(keepAliveSent)))    {
              sendSupervisorGetAll();

              keepAliveSent = true;
            } else {
              this.log.warning("RW " + getConection() + " problem in run: " + 
                e.toString());
              if (this.printStackTrace) {
                e.printStackTrace();
              }
              this.isConnected = false;
              try {
                Thread.sleep(5000L);
              }
              catch (InterruptedException localInterruptedException) {
              }
            }
        }
        try {
          Thread.sleep(10L);
        } catch (InterruptedException localInterruptedException1) {
        }
      }
      disconnect();
    } finally {
      this.status = 0;
      this.log.info("RW " + getConection() + " shutdown");
    }
  }

  private synchronized void connect()
  {
    disconnect();
//    if (this.serial)
//      serialConnect();
//    else
      tcpIpConnect();
  }

  private synchronized void disconnect()
  {
//    if (this.serial)
//      serialDisconnect();
//    else
      tcpIpDisconnect();
  }

  public String getConection()
  {
//    if (this.serial) {
//      return this.serialPort;
//    }
    return this.ip;
  }

  public boolean isConnected()
  {
    return this.isConnected;
  }

  public int getRWStatus()
  {
    return this.status;
  }

  public String toString()
  {
//    if (this.serial) {
//      return this.serialDealer.getPort() + " - " + this.serialDealer.getBaudRate();
//    }
    return this.ip + ":" + this.port;
  }

  private void sendSupervisorGetAll() {
    if (this.supervisor == null) {
      return;
    }
    this.log.fine("RW " + getConection() + " sending keep-alive to " + 
      this.supervisor.getSerial().toShortString());
    byte[] destino = PPM2v2Helper.getBytes(this.supervisor);
    PPM2v2 sendPacket = new PPM2v2((byte)1, (byte)0, null,  destino, null);
    CommunicationMonitor.getInstance().notifyPacket(getConection(), sendPacket,
    		PPM2v2Helper.getSerial(sendPacket.getDestiny()));
    write(sendPacket.getRawBytes());
  }

  public boolean isPrintPacket()
  {
    return this.printPacket;
  }

  public boolean isPrintStackTrace()
  {
    return this.printStackTrace;
  }

  public void setPrintPacket(boolean printPacket)  {
    this.printPacket = printPacket;
  }

  public void setPrintStackTrace(boolean printStackTrace)  {
    this.printStackTrace = printStackTrace;
  }

  public void setConnectionAlarmManager(AbstractConnectionAlarmManager connectionAlarmManager)  {
    this.connectionAlarmManager = connectionAlarmManager;
    this.connectionAlarmManager.setConnectionName(getConection());
  }

  public AbstractConnectionAlarmManager getConnectionAlarmManager() {
    return this.connectionAlarmManager;
  }

  public boolean isSerial()
  {
    return this.serial;
  }
}