package br.com.padtec.v3.server.protocols.ppm2v2.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import br.com.padtec.v3.data.Alarm;
import br.com.padtec.v3.data.LocalHistory;
import br.com.padtec.v3.data.Notification;
import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.impl.Amplifier_Impl;
import br.com.padtec.v3.data.impl.NE_Impl;
import br.com.padtec.v3.data.impl.PBAmp_Impl;
import br.com.padtec.v3.server.AlarmFactory;
import br.com.padtec.v3.server.protocols.ppm2v2.ColetorPPM2v2;
import br.com.padtec.v3.server.protocols.ppm2v2.PPM2v2;
import br.com.padtec.v3.util.Functions;
import br.com.padtec.v3.util.log.Log;

public class AmplifierHandler extends AbstractHandler {
  private transient Logger log = Log.getInstance();

  public boolean canHandle(NE_Impl ne) {
    return ne instanceof Amplifier_Impl;
  }

  public List<Notification> getAlarmList(NE_Impl ne) {
    ArrayList<Notification> alarms = new ArrayList<Notification>();
    Amplifier_Impl amplifier = (Amplifier_Impl)ne;
    alarms.add( AlarmFactory.createAlarm(amplifier, amplifier.getCurrentAlarm(), Alarm.TYPE_CURRENT_ALARM));
    alarms.add( AlarmFactory.createAlarm(amplifier, amplifier.isFail(), Alarm.TYPE_AMP_FAIL));
    alarms.add( AlarmFactory.createAlarm(amplifier, amplifier.isLaserOff(), Alarm.TYPE_AMP_LASEROFF));
    alarms.add( AlarmFactory.createAlarm(amplifier, amplifier.getMCS(), Alarm.TYPE_AMP_MCS_TEMPERATURE));
    alarms.add( AlarmFactory.createAlarm(amplifier, amplifier.getTemperatureAlarm(), Alarm.TYPE_TEMPERATURE_ALARM));
    if (amplifier instanceof PBAmp_Impl) {
      alarms.add( AlarmFactory.createAlarm(amplifier, ((PBAmp_Impl)amplifier).isLos(), Alarm.TYPE_AMP_LOS));
    }

//    if (amplifier instanceof AmpALS) {
//      AmpALS ampALS = (AmpALS)amplifier;
//      alarms.add(
//        AlarmFactory.createAlarm(amplifier, 
//        ampALS.isALS_Acting(), 1525));
//      alarms.add(
//        AlarmFactory.createAlarm(amplifier, 
//        ampALS.isManualRestoreActing(), 1526));
//    }

    return alarms;
  }

  public LocalHistory getLocalHistory(NE_Impl ne) {
    return null;
  }

  public boolean onReceiveResponse(ColetorPPM2v2 coletor, NE_Impl ne, PPM2v2 pacote, byte parametro, Collection<Notification> alarmList)
  {
    setAmplifier((Amplifier_Impl)ne, pacote, parametro);
    return true;
  }

  public boolean onReceiveTrap(NE_Impl ne, PPM2v2 trap, List<PPM2v2> packetToSend, List<Notification> event)
  {
    if (ne instanceof PBAmp_Impl) {
      PBAmp_Impl amplifier = (PBAmp_Impl)ne;
      try {
        switch (trap.getParameter()) {
        case PPM2v2.TRAP_NEW:
          break;
        case PPM2v2.TRAP_LOS:
          amplifier.setLos(true);
          break;
        case PPM2v2.TRAP_AMP_FAIL:
          amplifier.setFail(true);
          break;
        case PPM2v2.TRAP_AMP_LASEROFF:
          amplifier.setLaserOff(true);
          break;
        case PPM2v2.TRAP_AMP_MCS_TEMPERATURE_ALARM:
          amplifier.setMCS(true);
          break;
        case PPM2v2.TRAP_AMP_CURRENT_ALARM:
          amplifier.setCurrentAlarm(true);
          break;
        case PPM2v2.TRAP_AMP_TEMPERATURE_ALARM:
          amplifier.setTemperatureAlarm(true);
          break;
        case -127:
          amplifier.setLos(false);
          break;
        case -126:
          amplifier.setFail(false);
          break;
        case -125:
          amplifier.setLaserOff(false);
          break;
        case -123:
          amplifier.setMCS(false);
          break;
        case -122:
          amplifier.setCurrentAlarm(false);
          break;
        case -121:
          amplifier.setTemperatureAlarm(false);
          break;
        case -124:
        case PPM2v2.TRAP_AMP_ALS:
          break;
        default:
          this.log.warning("Source - Amplifier. Received an alarm code that is not implemented: " +
        		  trap.getParameter());
        }
      }
      catch (Exception e) {
        e.printStackTrace();
      }
//    } else if (ne instanceof FOA_Impl) {
//      FOA_Impl amplifier = (FOA_Impl)ne;
//      try {
//        switch (trap.getParameter())
//        {
//        case 2:
//          amplifier.setFail(true);
//          break;
//        case 3:
//          amplifier.setLaserOff(true);
//          break;
//        case -126:
//          amplifier.setFail(false);
//          break;
//        case -125:
//          amplifier.setLaserOff(false);
//          break;
//        case -124:
//        case 4:
//          break;
//        default:
//          this.log
//            .warning("Source - FOA Received an alarm code that is not implemented: " + 
//            trap.getParameter());
//        }
//      }
//      catch (Exception e) {
//        e.printStackTrace();
//      }
    }
    return false;
  }

  public boolean prepareFullUpdate(NE_Impl ne) {
    return true;
  }

  private void setAmplifier(Amplifier_Impl amplifier, PPM2v2 pacote, byte parametro) {
    if (parametro == PPM2v2.GET_ALL) {
      byte[] data = pacote.getDataArray();
      if ((data.length <= 0) || 
        (!(amplifier.getSerial().equals(getSerial(pacote.getSource()))))) return;
      double pin = ((data[1] & 0x80) == 128) ? (0xFFFF0000 | data[0] & 0xFF | data[1] << 8 & 0xFF00) / 100.0D : 
        (Functions.b2i(data[0]) + Functions.b2i(data[1]) * 256) / 100.0D;
      double pout = ((data[3] & 0x80) == 128) ? (0xFFFF0000 | data[2] & 0xFF | data[3] << 8 & 0xFF00) / 100.0D : 
        (Functions.b2i(data[2]) + Functions.b2i(data[3]) * 256) / 100.0D;
      if (amplifier instanceof PBAmp_Impl) {
        ((PBAmp_Impl)amplifier).setPin(pin);

        ((PBAmp_Impl)amplifier).setLos(Functions.testBit(data[4], 2));
      } 
//      else if (amplifier instanceof RAmpNoPout_Impl) {
//        RAmpNoPout_Impl raman = (RAmpNoPout_Impl)amplifier;
//        raman.setPBombeio(pin);
//      }
      amplifier.setPout(pout);

      amplifier.setLaserOff(Functions.testBit(data[4], 0));
      amplifier.setFail(Functions.testBit(data[4], 1));
      amplifier.setEyeProtection(Functions.testBit(data[4], 3));
      amplifier.setCurrentAlarm(Functions.testBit(data[4], 5));
      amplifier.setTemperatureAlarm(Functions.testBit(data[4], 6));
      amplifier.setMCS(Functions.testBit(data[4], 7));

      amplifier.setAGCStatus(Functions.testBit(data[4], 4));
      double gainValue = (Functions.b2i(data[9]) * 256 + 
        Functions.b2i(data[8])) / 10.0D;
      amplifier.setAGCGain(gainValue);

      int slot = Functions.b2i(data[5]);

      amplifier.setSlot(slot);
      amplifier.setVersion(Integer.toString((Functions.b2i(data[6]) & 0xF0) >> 4) + 
    		  "." + Integer.toString(Functions.b2i(data[6]) & 0xF));
    }
  }

  private SerialNumber getSerial(byte[] origin) {
    int part = (Functions.b2i(origin[1]) << 8) + Functions.b2i(origin[2]);
    int ser = (Functions.b2i(origin[3]) << 8) + Functions.b2i(origin[4]);
    return new SerialNumber(part, ser);
  }

  public boolean isFullUpdated(SerialNumber serial)
  {
    return true;
  }
}