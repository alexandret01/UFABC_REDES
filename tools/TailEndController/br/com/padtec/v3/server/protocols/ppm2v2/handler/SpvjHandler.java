
package br.com.padtec.v3.server.protocols.ppm2v2.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import br.com.padtec.v3.data.LocalHistory;
import br.com.padtec.v3.data.Notification;
import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.impl.NE_Impl;
import br.com.padtec.v3.data.impl.SupSPVJ_Impl;
import br.com.padtec.v3.server.AlarmFactory;
import br.com.padtec.v3.server.protocols.ppm2v2.ColetorPPM2v2;
import br.com.padtec.v3.server.protocols.ppm2v2.PPM2v2;
import br.com.padtec.v3.server.protocols.ppm2v2.PPM2v2Helper;
import br.com.padtec.v3.util.Functions;


public final class SpvjHandler extends AbstractHandler {
	
  private final ConcurrentHashMap<SerialNumber, SerialNumber> sendGetSupConf = new ConcurrentHashMap<SerialNumber, SerialNumber>();
  private final ConcurrentHashMap<SerialNumber, SerialNumber> sendGetSupRackConf = new ConcurrentHashMap<SerialNumber, SerialNumber>();
  private final ConcurrentHashMap<SerialNumber, SerialNumber> sendGetCmdHistory = new ConcurrentHashMap<SerialNumber, SerialNumber>();

  public boolean canHandle(NE_Impl ne)  {
    return ne instanceof SupSPVJ_Impl;
  }

  public List<PPM2v2> getUpdatePacketList(NE_Impl ne)  {
    List<PPM2v2> result = super.getUpdatePacketList(ne);
    byte[] destino = PPM2v2Helper.getBytes(ne);
    if (this.sendGetSupConf.contains(ne.getSerial())) {
      result.add( new PPM2v2(PPM2v2.CMD_GET, PPM2v2.GET_SUP_CONF, PPM2v2.ADDR_NULL, destino, null));
      result.add( new PPM2v2(PPM2v2.CMD_GET, PPM2v2.GET_SUP_IP, PPM2v2.ADDR_NULL, destino, null));
      result.add( new PPM2v2(PPM2v2.CMD_GET, PPM2v2.GET_SUP_DEBUG, PPM2v2.ADDR_NULL, destino, null));
    }
    if (this.sendGetSupRackConf.contains(ne.getSerial())) {
      result.add( new PPM2v2(PPM2v2.CMD_GET, PPM2v2.GET_SUP_RACK_CONF, PPM2v2.ADDR_NULL, destino, null));
    }
    if (this.sendGetCmdHistory.contains(ne.getSerial())) {
      result.add( new PPM2v2(PPM2v2.CMD_GET, PPM2v2.GET_CMD_HISTORY_TLV, PPM2v2.ADDR_NULL, destino, null));
    }
    return result;
  }

  public boolean prepareFullUpdate(NE_Impl ne)
  {
    String version = ne.getVersion();
    if (version != null) {
      int dotIdx = version.indexOf(46);
      if (dotIdx != -1) {
        int ver1 = Integer.parseInt(version.substring(0, dotIdx));
        int ver2 = Integer.parseInt(version.substring(dotIdx + 1));
        if ((ver1 > 2) || ((ver1 == 2) && (ver2 >= 11))) {
          this.sendGetSupConf.put(ne.getSerial(), ne.getSerial());
        }
        if (ver1 > 2) {
          this.sendGetSupRackConf.put(ne.getSerial(), ne.getSerial());
          this.sendGetCmdHistory.put(ne.getSerial(), ne.getSerial());
        }
      }
    }
    return true;
  }

  public boolean onReceiveTrap(NE_Impl ne, PPM2v2 pacote, List<PPM2v2> packetToSend, List<Notification> event)
  {
    SupSPVJ_Impl supervisor = (SupSPVJ_Impl)ne;
    boolean isStart = pacote.getParameter() > 0;

    int trap = (isStart) ? pacote.getParameter() : pacote.getParameter() - 128;
    switch (trap)  {
    case 0:
      break;
    case 1:
      isStart = !(isStart);
      supervisor.setBlocked(!(isStart));
      event.add( AlarmFactory.createGenericAlarm(supervisor, null, 11, null, isStart, null));
      break;
    case 2:
      supervisor.setLct(isStart);
      event.add( AlarmFactory.createGenericAlarm(supervisor, null, 12, null, isStart, null));
      break;
    case 3:
      supervisor.setLOS1(isStart);
      event.add( AlarmFactory.createGenericAlarm(supervisor, null, 13, null, isStart, null));
      break;
    case 4:
      supervisor.setLOS2(isStart);
      event.add( AlarmFactory.createGenericAlarm(supervisor, null, 14, null, isStart, null));
      break;
    case 5:
      event.add( AlarmFactory.createGenericAlarm(supervisor, null, 15, null, isStart, null));
      break;
    case 6:
      event.add( AlarmFactory.createGenericAlarm(supervisor, null, 16, null, isStart, null));
      prepareFullUpdate(ne);
    }

    return true;
  }

  public boolean onReceiveResponse(ColetorPPM2v2 coletor, NE_Impl ne, PPM2v2 pacote, byte parametro, Collection<Notification> alarmList)
  {
    Notification n;
    SupSPVJ_Impl sup = (SupSPVJ_Impl)ne;
    byte[] data = pacote.getDataArray();
    switch (parametro) {
    case 0:
      if (data.length == 15) {
        sup.setCanError(Functions.b2i(data[0]) + Functions.b2i(data[1]) * 256);
        sup.setTintError(Functions.b2i(data[2]) + Functions.b2i(data[3]) * 256);
        sup.setCrcError(Functions.b2i(data[4]) + Functions.b2i(data[5]) * 256);
        sup.setTintTotal(Functions.b2i(data[6]) + Functions.b2i(data[7]) * 256);
        sup.setPPM2v2Total(Functions.b2i(data[8]) + Functions.b2i(data[9]) * 256);
        sup.setTokenTimeouts(Functions.b2i(data[10]) + Functions.b2i(data[11]) * 256);
        sup.setRoBuffer(Functions.b2i(data[12]) + Functions.b2i(data[13]) * 256); 
        break;
      }

      if (data.length <= 0) 
    	  break;
      sup.setLct((data[0] & 0x1) == 1);
      sup.setBlocked((data[0] & 0x2) == 2);
      sup.setLOS2((data[0] & 0x4) == 4);
      sup.setLOS1((data[0] & 0x8) == 8);
      sup.setAddress(PPM2v2.getSiteFromAddress(pacote.getSource()));
      int ver1 = (Functions.b2i(data[2]) & 0xF0) >> 4;
      int ver2 = Functions.b2i(data[2]) & 0xF;
      String newVersion = ver1 + "." + ver2;
      if (newVersion.equals(sup.getVersion())) 
    	  break;
      sup.setVersion(newVersion);
      onVersionChange(sup);

      break;
    case 33:
      sup.setIP(Functions.b2i(data[0]) + "." + Functions.b2i(data[1]) + "." + 
        Functions.b2i(data[2]) + "." + Functions.b2i(data[3]));
      sup.setMask(Functions.b2i(data[4]) + "." + Functions.b2i(data[5]) + "." + 
        Functions.b2i(data[6]) + "." + Functions.b2i(data[7]));
      sup.setGateway(Functions.b2i(data[8]) + "." + Functions.b2i(data[9]) + 
        "." + Functions.b2i(data[10]) + "." + Functions.b2i(data[11]));
      break;
    case 35:
      this.sendGetSupConf.remove(ne.getSerial());
      if (!(sup.setSupConf(data, new String(data, 24, 20).trim()))) 
    	  break;
      n = new Notification(Notification.ID_NE_RENAMED, sup.getSerial());
      alarmList.add(n);

      break;
    case 50:
      this.sendGetSupRackConf.remove(ne.getSerial());
      if ((Functions.b2i(data[0]) <= 0) || (!(sup.setSupRackConf(data)))) 
    	  break;
      n = new Notification(Notification.ID_NE_RACK_SAVED,sup.getSerial());
      alarmList.add(n);

      break;
    case 51:
      this.sendGetCmdHistory.remove(ne.getSerial());

      /* if using a Server uncomment the code bellow
      
      int telecomando = Functions.b2i(data[0]);
      byte b1 = data[1];

      long part = Functions.b2l(data, 3, 2);
      long serial = Functions.b2l(data, 5, 2);
      int origem = Functions.b2i(data[7]);
  
      SerialNumber sn = new SerialNumber((int)part, (int)serial);
      NE_Impl placa = PartNumber.getInstance(sn, false);
      
      Date time = new Date(Functions.b2l(data, 8, 8));
      Command cmd = CommandFactory.createOriginalCommand(placa, telecomando, new byte[] { b1 });
      String login = "Desconhecido";
      String host = "Desconhecido";
      
      switch (origem)   {
      case 0:
        host = "Via Canal de Supervisão";
        break;
      case 1:
        host = "Ethernet";
        break;
      case 2:
        host = "Serial";
      }

      if (Functions.isLct) 
    	  break;
      /* if using a Server uncomment the line bellow 
      Server.getMapServer().logDoCommand(cmd, time, login, host);*/
      
      break;
    }
    return true;
    
  }

  private void onVersionChange(SupSPVJ_Impl sup)
  {
    prepareFullUpdate(sup);
  }

  public List<Notification> getAlarmList(NE_Impl ne)  {
    SupSPVJ_Impl sup = (SupSPVJ_Impl)ne;
    ArrayList<Notification> alarms = new ArrayList<Notification>();
    alarms.add(AlarmFactory.createAlarm(sup, sup.isLct(), 12));
    alarms.add(
      AlarmFactory.createAlarm(sup, !(sup.isBlocked()), 11));

    return alarms;
  }

  public LocalHistory getLocalHistory(NE_Impl ne) {
    return null;
  }

  public boolean isFullUpdated(SerialNumber serial)
  {
    return true;
  }
}