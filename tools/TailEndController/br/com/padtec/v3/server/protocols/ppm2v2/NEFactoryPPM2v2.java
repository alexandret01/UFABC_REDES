package br.com.padtec.v3.server.protocols.ppm2v2;

import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.padtec.v3.data.Notification;
import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.impl.NE_Impl;
import br.com.padtec.v3.server.protocols.ppm2v2.handler.HandlerInterface;
import br.com.padtec.v3.util.PartNumber;
import br.com.padtec.v3.util.log.Log;

public class NEFactoryPPM2v2 extends NEFactory {
  private static NEFactoryPPM2v2 factory = null;
  public static final int POS_PIN = 1;
  public static final int POS_POUT = 6;
  private static Logger log = Log.getInstance();

  /*private static int getIfB(byte b) {
    return Functions.b2i(b);
  }*/

  public static synchronized NEFactoryPPM2v2 getInstance()  {
    if (factory == null) {
      factory = new NEFactoryPPM2v2();
    }
    return factory;
  }

 /* private SerialNumber getSerial(byte[] origin)  {
    int part = (getIfB(origin[1]) << 8) + getIfB(origin[2]);
    int ser = (getIfB(origin[3]) << 8) + getIfB(origin[4]);
    return new SerialNumber(part, ser);
  }*/

//  private void setOpticalProtection(OpticalProtection_Impl op, PPM2v2 pacote)
//  {
//    byte[] data = pacote.getDataArray();
//
//    if (op.getSerial().equals(getSerial(pacote.getSource()))) {
//      op.setSignal1((data[0] & 0x1) == 1);
//      op.setSignal2((data[0] & 0x2) == 2);
//      op.setPosition(((data[0] & 0x4) == 4) ? 2 : 1);
//      op.setAuto((data[0] & 0x8) == 8);
//      op.setLocked((data[0] & 0x10) == 16);
//      op.setSlot(getIfB(data[1]));
//      op.setVersion(Integer.toString((getIfB(data[2]) & 0xF0) >> 4) + "." + 
//        Integer.toString(getIfB(data[2]) & 0xF));
//    }
//  }

//  private void setMediaConverter(MediaConverter_Impl med, PPM2v2 pacote)
//  {
//    byte[] data = pacote.getDataArray();
//
//    if (med.getSerial().equals(getSerial(pacote.getSource()))) {
//      med.setTX((data[0] & 0x1) != 1);
//      med.setFX((data[0] & 0x2) != 2);
//      if (data.length < 3) {
//        med.setVersion("1.0");
//      } else {
//        med.setSlot(Functions.b2i(data[1]));
//        med.setVersion(String.valueOf((getIfB(data[2]) & 0xF0) >> 4) + "." + 
//          String.valueOf(getIfB(data[2]) & 0xF));
//      }
//    }
//  }

//  private void setSHK(SHK_Impl elem, PPM2v2 pacote) {
//    byte[] data = pacote.getDataArray();
//
//    boolean[] telecommands = new boolean[elem.MAXCOMMAND];
//
//    boolean[] telesignals = new boolean[40];
//
//    if (elem.getSerial().equals(getSerial(pacote.getSource()))) {
//      elem.setVersion(String.valueOf((getIfB(data[7]) & 0xF0) >> 4) + "." + 
//        String.valueOf(getIfB(data[7]) & 0xF));
//
//      int comp = 1;
//      for (int i = 0; i < 8; ++i) {
//        if ((getIfB(data[5]) & comp) == comp)
//          telecommands[i] = true;
//        else {
//          telecommands[i] = false;
//        }
//        comp *= 2;
//      }
//      elem.setCommands(telecommands);
//
//      int loop = 0;
//      for (int i = 0; i < 5; ++i) {
//        comp = 1;
//        for (int j = 0; j < 8; ++j) {
//          if ((getIfB(data[i]) & comp) == comp)
//            telesignals[(j + loop)] = true;
//          else {
//            telesignals[(j + loop)] = false;
//          }
//          comp *= 2;
//        }
//        loop += 8;
//      }
//      elem.setSignals(telesignals);
//
//      elem.setVersion(Integer.toString((getIfB(data[7]) & 0xF0) >> 4) + "." + 
//        Integer.toString(getIfB(data[7]) & 0xF));
//    }
//  }

  public void setNE(ColetorPPM2v2 coletor, NE_Impl ne, PPM2v2 pacote, byte parametro, 
		  Collection<Notification> notificationList) {
      byte origin[] = pacote.getSource();
      ne.setSupAddress(origin[0]);
      if(origin[0] == 0)
          log.config((new StringBuilder("Collector ")).append(coletor.getConection()).append(": supervisor with address==0").toString());
      try {
          ne.setIsUp(true);
          ne.update();
          for(Iterator<HandlerInterface<PPM2v2>> iterator = ColetorPPM2v2.handlerList.iterator(); 
          iterator.hasNext();) {
              HandlerInterface<PPM2v2> handler = iterator.next();
              if(handler.canHandle(ne))
                   if(handler.onReceiveResponse(coletor, ne, pacote, parametro, notificationList))
                          return;
                
          }

//          if(ne instanceof RateMeter_Impl)
//              setRateMeter((RateMeter_Impl)ne, pacote, parametro);
//          else
//          if(ne instanceof OpticalProtection_Impl)
//              setOpticalProtection((OpticalProtection_Impl)ne, pacote);
//          else
//          if(ne instanceof SHK_Impl)
//              setSHK((SHK_Impl)ne, pacote);
//          else
//          if(ne instanceof MediaConverter_Impl)
//              setMediaConverter((MediaConverter_Impl)ne, pacote);
//          else
//          if(ne instanceof OpticalSwitch8x1_Impl)
//              setSwitch8x1((OpticalSwitch8x1_Impl)ne, pacote);
//          else
//          if(ne instanceof Fan_Impl)
//              setFan((Fan_Impl)ne, pacote);
//          else
//          if(ne instanceof PowerSupply_Impl)
//              setPowerSupply((PowerSupply_Impl)ne, pacote);
//          else
//          if(ne instanceof AmplifierPowerSupply_Impl)
//              setAmpPowerSupply((AmplifierPowerSupply_Impl)ne, pacote);
//          else
//          if(ne instanceof SupSPVS_Impl)
//              setSupFilho((SupSPVS_Impl)ne, pacote);
      } catch(RuntimeException e)  {
              Log.getInstance(1).log(Level.WARNING, "Exception parsing PPM2v2 packet for " + ne.getSerial().toShortString()+ ": " + 
            		  pacote.toString().toString(), e);
       }      
  }


//  private void setSupFilho(SupSPVS_Impl impl, PPM2v2 pacote) {
//    byte[] data = pacote.getDataArray();
//    impl.setVersion(Integer.toString((data[0] & 0xF0) >> 4) + "." + 
//      Integer.toString(data[0] & 0xF));
//    impl.setSlot(getIfB(data[1]) - 1);
//  }

//  private void setFan(Fan_Impl impl, PPM2v2 pacote) {
//    byte[] data = pacote.getDataArray();
//    if (data.length > 0) {
//      impl.setVersion(Integer.toString((data[0] & 0xF0) >> 4) + "." + 
//        Integer.toString(data[0] & 0xF));
//      impl.setSlot(getIfB(data[1]));
//      impl
//        .setMaxTemperature(((getIfB(data[2]) << 4) + ((data[3] & 0xF0) >> 4)) * 0.0625D);
//      impl.setTemperature((((data[3] & 0xF) << 8) + getIfB(data[4])) * 0.0625D);
//      impl.setVelocityControl((data[5] & 0x10) == 16);
//      impl.setOverHeat((data[5] & 0x8) == 8);
//      impl.setFanOk(1, (data[5] & 0x4) == 4);
//      impl.setFanOk(2, (data[5] & 0x2) == 2);
//      impl.setFanOk(3, (data[5] & 0x1) == 1);
//      impl.setVelocityPercentual(getIfB(data[6]));
//    }
//  }

//  private void setPowerSupply(PowerSupply_Impl impl, PPM2v2 pacote) {
//    byte[] data = pacote.getDataArray();
//    impl.setVersion(Integer.toString((data[0] & 0xF0) >> 4) + "." + 
//      Integer.toString(data[0] & 0xF));
//    impl.setSlot(getIfB(data[1]) + 1);
//
//    impl.setVoltage(getIfB(data[2]) / 10.0D);
//    impl.setUpperBound(getIfB(data[3]) / 10);
//    impl.setLowerBound(getIfB(data[4]) / 10);
//    impl
//      .setFail48B(((data[5] & 0x4) == 4) && ((data[5] & 0x2) != 2));
//    impl.setFail48A((data[5] & 0x2) == 2);
//    impl.setOutRange((data[5] & 0x1) == 1);
//  }

//  private void setAmpPowerSupply(AmplifierPowerSupply_Impl impl, PPM2v2 pacote) {
//    byte[] data = pacote.getDataArray();
//    impl.setVersion(Integer.toString((data[0] & 0xF0) >> 4) + "." + 
//      Integer.toString(data[0] & 0xF));
//    impl.setSlot(getIfB(data[1]) + 1);
//
//    impl.setFailA((data[2] & 0x2) == 2);
//    impl.setFailB((data[2] & 0x1) == 1);
//  }

//  private void setSwitch8x1(OpticalSwitch8x1_Impl ops, PPM2v2 pacote)  {
//    byte[] data = pacote.getDataArray();
//    switch (Functions.b2i(data[0]))
//    {
//    case 0:
//    default:
//      ops.setPosition(0);
//      break;
//    case 1:
//      ops.setPosition(1);
//      break;
//    case 2:
//      ops.setPosition(2);
//      break;
//    case 4:
//      ops.setPosition(3);
//      break;
//    case 8:
//      ops.setPosition(4);
//      break;
//    case 16:
//      ops.setPosition(5);
//      break;
//    case 32:
//      ops.setPosition(6);
//      break;
//    case 64:
//      ops.setPosition(7);
//      break;
//    case 128:
//      ops.setPosition(8);
//    }
//
//    ops.setFail((data[1] & 0x1) == 1);
//    ops.setSwitched((data[1] & 0x2) == 2);
//    ops.setChannelLost((data[1] & 0x4) == 4);
//    ops.setDisabled((data[1] & 0x8) == 8);
//    for (int i = 0; i < 10; ++i) {
//      ops.setChannelInactive(i + 1, (data[(i + 2)] & 0x1) == 1);
//      ops.setChannelLos(i + 1, (data[(i + 2)] & 0x2) == 2);
//      ops.setChannelSwitched(i + 1, (data[(i + 2)] & 0x4) == 4);
//      ops.setChannelLost(i + 1, (data[(i + 2)] & 0x8) == 8);
//      ops.setChannelProtection(i + 1, (data[(i + 2)] & 0x10) == 16);
//    }
//    ops.setVersion(Integer.toString((getIfB(data[12]) & 0xF0) >> 4) + "." + 
//      Integer.toString(getIfB(data[12]) & 0xF));
//  }

  public NE_Impl createNE(SerialNumber serial)
  {
    NE_Impl ne = PartNumber.getInstance(serial);

    if (ne != null) {
      ne.setFullSync(true);
    }
    return ne;
  }

//  private void setRateMeter(RateMeter_Impl rater, PPM2v2 pacote, byte parametro) {
//    byte[] data = pacote.getDataArray();
//
//    if (rater.getSerial().equals(getSerial(pacote.getSource())))
//      if (parametro == 0)
//      {
//        rater.setPin(
//          TrpTables.getPower(getIfB(data[0]), 1, data, 
//          TrpTables.isPinLog(rater)));
//
//        rater.setLos((data[12] & 0x1) == 1);
//
//        rater.setVersion(Integer.toString((getIfB(data[14]) & 0xF0) >> 4) + "." + 
//          Integer.toString(getIfB(data[14]) & 0xF));
//
//        rater.setSlot(getIfB(data[13]) + 1);
//
//        rater.setOver((data[12] & 0x40) == 64);
//        if (rater.isLos())
//          rater.setPin((0.0D / 0.0D));
//      }
//      else {
//        int taxa = getIfB(data[0]) * 256 + getIfB(data[1]);
//        int limiar = getIfB(data[2]) * 256 + getIfB(data[3]);
//        rater.setRate(TrpTables.getFrequency(taxa));
//        rater.setLimiar(TrpTables.getFrequency(limiar));
//      }
//  }
}