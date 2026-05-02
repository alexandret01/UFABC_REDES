package br.com.padtec.v3.server.protocols.ppm3;




import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.impl.NE_Impl;
import br.com.padtec.v3.data.impl.SPVL4_Impl;
import br.com.padtec.v3.server.protocols.codegenerator.Generator;
import br.com.padtec.v3.server.protocols.ppm3.handler.GenericHandler;
import br.com.padtec.v3.server.protocols.ppm3.handler.GenericHandlerFactory;
import br.com.padtec.v3.server.protocols.ppm3.packet.InvalidValueException;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3Factory;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3Get;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3HistoryGet;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3HistoryResponse;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3Payload;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3Response;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3Trap;
import br.com.padtec.v3.server.protocols.ppm3.packet.TLV;
import br.com.padtec.v3.server.protocols.ppm3.packet.TimeTLV;
import br.com.padtec.v3.util.DateUtils;
import br.com.padtec.v3.util.PartNumber;
import br.com.padtec.v3.util.io.DelayedOutputStream;
import br.com.padtec.v3.util.math.RandomUtils;

public class SimpleSpvlSimulator extends Thread {
		private final List<InetAddress> ip;
		private final Map<Integer, Map<SerialNumber, NE_Impl>> boardMap;
		private boolean sigTerm;

		public SimpleSpvlSimulator(List<InetAddress> ip, Map<Integer, Map<SerialNumber, NE_Impl>> boardMap) {
			setName("SPVL Simulator " + ip);
			this.ip = ip;
			this.boardMap = boardMap;
		}
		public void run() {
			StringBuilder msg = new StringBuilder();
			msg.append("Starting SPVL simulation on " + this.ip + ":8886\n");
			System.out.print(msg);

			for (int i = 0; i < this.ip.size(); ++i) {
				int conn = i;
				runInterface(conn);
				setName("if " + ((InetAddress)this.ip.get(conn)).getHostAddress());
				start();
			}
		}

		private void runInterface(int conn) {
			try {
				ServerSocket server = new ServerSocket(8886, 0, (InetAddress)this.ip.get(conn));
				while (!(this.sigTerm)) {
					Socket socket = null;
					try
					{
						socket = server.accept();
						socket.setSoTimeout(1);
						log(System.out, DateUtils.getTimeAll(new Date()) + " New connection " + 
								server.getInetAddress() + "/" + socket.getRemoteSocketAddress());
						OutputStream out = new DelayedOutputStream( socket.getOutputStream(), RandomUtils.getLong(500L, 1500L) );
						InputStream in = socket.getInputStream();
						byte[] buf = new byte[16];
						PPM3ReceiveQueue rQueue = new PPM3ReceiveQueue();
						while (!(this.sigTerm)) {
							PPM3 packet;
//							PPM3 resp;
							List<PPM3> result = new ArrayList<PPM3>();
							try {
								int len = in.read(buf);
								if (len == -1) {
									throw new SocketException("Connection reset");
								}
								for (int i = 0; i < len; ++i) {
									rQueue.addByte(null, buf[i]);
								}
								if (!(rQueue.isEmpty())) {
									packet = rQueue.nextPacket();

									doPacket(packet, result);

									for (Iterator<PPM3> localIterator = result.iterator(); localIterator.hasNext(); ) { 
										PPM3 resp = (PPM3)localIterator.next();

									if (RandomUtils.getInt(100) < 5)
									{
										continue;
									}

									out.write(resp.getBytes());
									resp = null;
									}
									result.clear();
								}
							}
							catch (SocketTimeoutException localSocketTimeoutException)
							{
							}

							List<PPM3> packetToSend = doLoopEvent();
							if (packetToSend != null) {
								for (Iterator<PPM3> it = packetToSend.iterator(); it.hasNext(); ) { 
									packet = (PPM3)it.next();

									out.write(packet.getBytes());
								}
							}
						}

						log(System.out, DateUtils.getTimeAll(new Date()) + 
						" Connection closed by peer");

						in.close();
						out.flush();
						out.close();
					} catch (SocketException e) {
						String message = null;
						if (e.getMessage() != null) {
							if (e.getMessage().startsWith("Software caused connection abort"))
								message = "Fail sending data through socket";
							else if (e.getMessage().startsWith("Connection reset")) {
								message = "Connection closed by peer";
							}
						}
						if (message != null)
							log(System.out, DateUtils.getTimeAll(new Date()) + " " + message);
						else
							e.printStackTrace();
					}
					catch (Exception e) {
						e.printStackTrace();
					} finally {
						if (socket != null) {
							socket.close();
							log(System.out, DateUtils.getTimeAll(new Date()) + 
									" Connection closed");
						}
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		protected List<PPM3> doLoopEvent()
		{
			return null;
		}

		private void doPacket(PPM3 requestPacket, List<PPM3> result)
		{
			PPM3Payload payload = requestPacket.getPayload();
			if (payload instanceof PPM3Get) {
				PPM3Get get = (PPM3Get)payload;
				result.addAll(
						doGet(requestPacket.getDestination(), get.getSerial(), 
								requestPacket.getId(), get));
			} else if (payload instanceof PPM3HistoryGet) {
				PPM3HistoryGet get = (PPM3HistoryGet)payload;
				result.addAll(
						doHistoryGet(requestPacket.getDestination(), 
								get.getSerial(), requestPacket.getId(), get));
			}
		}
  private List<PPM3> doGet(int siteAddress, SerialNumber serial, long id, PPM3Get get)
  {
    List<PPM3> result = new ArrayList<PPM3>(1);
    for (int i = 0; i < get.getTLVCount(); ++i) {
      PPM3 packet;
      TLV tlv = get.getTLV(i);
      if ((serial.getPart() == 0) && (serial.getSeq() == 0) && 
        (tlv.getTypeAsInt() == 0))
      {
        Iterator<Entry<Integer, Map<SerialNumber, NE_Impl>>> localIterator1 = this.boardMap.entrySet().iterator();

        while (localIterator1.hasNext()) {
          Entry<Integer, Map<SerialNumber, NE_Impl>> site = localIterator1.next();
          for (NE_Impl board : site.getValue().values())
            if (board instanceof SPVL4_Impl) {
              packet = PPM3Factory.getTrapPPM3(site.getKey().intValue(), board.getSerial(), 0,  System.currentTimeMillis());
              PPM3Trap trap = (PPM3Trap)packet.getPayload();
              byte[] data = createGetDiscoveryPayload(board);
              try {
                trap.getTLV(0).setValue(data);
              } catch (InvalidValueException e) {
                e.printStackTrace();
              }
              result.add(packet);
            }
        }
      }
      else {
        Map<SerialNumber, NE_Impl> siteBoards = this.boardMap.get( siteAddress);
        if (siteBoards != null) {
          NE_Impl board = (NE_Impl)siteBoards.get(serial);
          if (board != null) {
            GenericHandler handler = GenericHandlerFactory.getHandler(board);

            byte[] res = (byte[])null;
            try {
              res = handler.analyzeGet(board, tlv.getTypeAsInt(), tlv.getValue());
              
            } catch (UnsupportedOperationException localUnsupportedOperationException) {
            	
            } catch (RuntimeException e) {
              e.printStackTrace();
            }

            if ((res == null) || (res.length == 0)) {
              res = new byte[512];
              RandomUtils.setRandomData(res);
            }

            if (result.isEmpty()) {
              packet = PPM3Factory.getResponsePPM3(siteAddress, serial);
              packet.setId(id);
              result.add(packet);
            } else {
              packet = (PPM3)result.get(0);
            }
            PPM3Response resp = (PPM3Response)packet.getPayload();
            try
            {
              resp.addTLV(new TLV(tlv.getType(), res));
            } catch (InvalidValueException e) {
              e.printStackTrace();
            }
          } else {
            log(System.out, "doGet() board not found: " + 
              serial.toShortString());
          }
        } else {
          log(System.out, "doGet() site not found: " + siteAddress);
        }
      }
    }
    return result;
  }

  private Collection<? extends PPM3> doHistoryGet(int siteAddress, SerialNumber serial, long id, PPM3HistoryGet get)
  {
    List<PPM3> result = new ArrayList<PPM3>(1);
    Map<SerialNumber, NE_Impl> siteBoards = this.boardMap.get(siteAddress);
    if (siteBoards != null) {
      Map<Integer,Boolean> trapList;
      PPM3HistoryResponse resp = new PPM3HistoryResponse();
      resp.setHistoryType(get.getHistoryType());

      switch (get.getHistoryType())
      {
      case READ_ALARMS:
        break;
      case READ_COMMANDS:
        break;
      case READ_LAST_ALARMS:
        NE_Impl board = siteBoards.get(get.getSerial());
        if (board != null) {
          try {
            GenericHandler handler =  GenericHandlerFactory.getHandler(board);
            trapList = handler.getTrapsFromBean(board);
            if (trapList != null) {
              PPM3Trap trap = new PPM3Trap();
              trap.setSerial(board.getSerial());
              resp.addBlock(trap);
              for (Entry<Integer, Boolean> item : trapList.entrySet()) {
                int val = ((Integer)item.getKey()).intValue();
                if (!(((Boolean)item.getValue()).booleanValue())) {
                  val |= 32768;
                }
                trap.addTLV(new TimeTLV(System.currentTimeMillis(), val));
              }
            }
          } catch (Exception e) {
            log(System.err, "Fail creating " + get.getHistoryType() + 
              " response for board " + board.getClass());
            e.printStackTrace();
          }
        }
        break;
      case READ_METRICS:
        break;
      case READ_TRAP_NEW_AND_DEL:
        Iterator<Entry<SerialNumber,NE_Impl>> it = siteBoards.entrySet().iterator();

        while (it.hasNext()) {
          Entry<SerialNumber,NE_Impl> item = it.next();
          byte[] data = createGetDiscoveryPayload(item.getValue());
          try {
            resp.addBlock( new PPM3Trap((SerialNumber)item.getKey(),item.getValue().getUpdate(), 0, data));
          } catch (InvalidValueException e) {
            e.printStackTrace();
          }
        }

      }

      PPM3 packet = PPM3.newPpm3(siteAddress, 0, id, resp);
      result.add(packet);
    } else {
      log(System.out, "doHistoryGet() site not found: " + siteAddress);
    }

    return result;
  }

  public void sigTerm()
  {
    this.sigTerm = true;
  }

  private static final byte[] createGetDiscoveryPayload(NE_Impl board) {
    byte[] data = new byte[56];
    Generator._getVersion(data, 24, board.getVersion());
    Generator._getSlot(data, 22, board.getSlot());
    Generator._getVersion(data, 40, board.getHardwareVersion());
    return data;
  }

  public static void main(String[] args)
    throws Exception
  {
    Map<Integer, Map<SerialNumber, NE_Impl>> boardMap = new TreeMap<Integer, Map<SerialNumber,NE_Impl>>();

    System.out.print("Pode-se passar como parâmetro vários códigos de produto de várias placas, ");
    System.out.println("os quais aparecerão no primeiro NE do enlace.");

    System.out.println("Sugere-se para esta simulação o uso de demux/mux 917/918 DXDC21201E22/MXDC21201E22.");

    int[][] partList = { 
      { 
      1316, 
      1585, 
      1532, 1532, 
      1533, 1533, 
      1023, 
      930, 
      1133, 
      1203, 
      718, 
      1445, 
      1552, 
      415, 
      44, 
      1525 }, 
      { 
      1316, 
      1532, 1532, 
      1533, 1533, 
      1586, 1586, 
      1023, 
      930, 
      1133 }, 
      { 
      1316, 
      1586, 1586, 
      1532, 1532, 
      1533, 1533, 
      1200, 
      1282, 
      1365 }, 
      { 
      1316, 
      1532, 1532, 
      1533, 1533, 
      1585, 
      1203, 
      718, 
      1445, 
      1552 } };

    for (int site = 1; site <= partList.length; ++site) {
      Map<SerialNumber, NE_Impl> siteBoards = new TreeMap<SerialNumber, NE_Impl>();
      boardMap.put(site, siteBoards);

      int seq = 100 * site;
      int slot = 0;
      for (int part : partList[(site - 1)]) {
        SerialNumber serial = new SerialNumber(part, seq);
        NE_Impl ne = PartNumber.getInstance(serial, false);
        if (ne != null) {
          setNeRandomData(site, ne, slot++);
          siteBoards.put(serial, ne);
          ++seq;
        }
      }

      if (site == 1) {
        for (String part : args)
          try {
            int partN = Integer.parseInt(part);
            SerialNumber serial = new SerialNumber(partN, seq);
            NE_Impl ne = PartNumber.getInstance(serial, false);
            if (ne != null) {
              setNeRandomData(site, ne, slot++);
              siteBoards.put(serial, ne);
              ++seq;
            }
          }
          catch (Exception localException)
          {
          }
      }
    }
    List<InetAddress> ipList = new ArrayList<InetAddress>();
    Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
    while (interfaces.hasMoreElements()) {
      NetworkInterface interf = interfaces.nextElement();
      Enumeration<InetAddress> addrs = interf.getInetAddresses();
      while (addrs.hasMoreElements()) {
        ipList.add(addrs.nextElement());
      }
    }
    SimpleSpvlSimulator sim = new SimpleSpvlSimulator(ipList, boardMap) {
      private long nextRun = System.currentTimeMillis() + 1000L;
      private int step = 0;

      protected final List<PPM3> doLoopEvent()
      {
        if (this.nextRun <= System.currentTimeMillis()) {
          PPM3 packet;
          this.nextRun = (System.currentTimeMillis() + 5000L);
          List<PPM3> result = new ArrayList<PPM3>();

          switch (this.step)
          {
          case 0:
          default:
            this.step = 0;
            packet = PPM3Factory.getTrapPPM3(1, new SerialNumber(1316, 100), 2, 
              new GregorianCalendar(2008, 8, 16, 10, 0, 0).getTimeInMillis());
            result.add(packet);
            break;
          case 1:
            packet = PPM3Factory.getTrapPPM3(1, new SerialNumber(1316, 100), 32770, 
              new GregorianCalendar(2008, 8, 16, 10, 15, 0).getTimeInMillis());
            result.add(packet);
            break;
          case 2:
            packet = PPM3Factory.getTrapPPM3(1, new SerialNumber(1316, 100), 2, 
              new GregorianCalendar(2008, 8, 16, 10, 0, 0).getTimeInMillis());
            result.add(packet);
            break;
          case 3:
            packet = PPM3Factory.getTrapPPM3(1, new SerialNumber(1316, 100),  32770, 
              new GregorianCalendar(2008, 8, 16, 10, 15, 0).getTimeInMillis());
            result.add(packet);
            break;
          case 4:
            packet = PPM3Factory.getTrapPPM3(1, new SerialNumber(1316, 100),  2, 
              new GregorianCalendar(2008, 8, 16, 11, 0, 0).getTimeInMillis());
            result.add(packet);
            break;
          case 5:
            packet = PPM3Factory.getTrapPPM3(1, new SerialNumber(1316, 100),  2, 
              new GregorianCalendar(2008, 8, 16, 9, 0, 0).getTimeInMillis());
            result.add(packet);
            break;
          case 6:
            packet = PPM3Factory.getTrapPPM3(1, new SerialNumber(1316, 100),  32770, 
              new GregorianCalendar(2008, 8, 16, 9, 15, 0).getTimeInMillis());
            result.add(packet);
            break;
          case 7:
            packet = PPM3Factory.getTrapPPM3(1, new SerialNumber(1316, 100),  32770, 
              new GregorianCalendar(2008, 8, 16, 11, 15, 0).getTimeInMillis());
            result.add(packet);
          }
          step++;

          return result;
        }
        return null;
      }
    };
    sim.start();
  }

  private static void setNeRandomData(int site, NE_Impl ne, int slot) {
    RandomUtils.setRandomData(ne);

    ne.setSupAddress(site);
    ne.setSlot(slot);
    ne.setVersion(RandomUtils.getInt(1, 3) + "." + RandomUtils.getInt(0, 9) + 
      ".5_alpha");
    ne.setHardwareVersion(RandomUtils.getInt(1, 3) + "." + 
      RandomUtils.getInt(0, 9) + ".12a");

    if (ne instanceof SPVL4_Impl) {
      SPVL4_Impl sup = (SPVL4_Impl)ne;
      sup.setName("NE " + site + " (simulador)");
      sup.setIP("192.168.0." + sup.getAddress());
      sup.setMask("255.255.255.0");
      sup.setGateway("192.168.0.254");
    }
  }

  private static void log(PrintStream out, String message) {
    out.println(Thread.currentThread().getName() + "\t" + message);
  }
}














