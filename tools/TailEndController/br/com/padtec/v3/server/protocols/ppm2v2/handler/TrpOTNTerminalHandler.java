package br.com.padtec.v3.server.protocols.ppm2v2.handler;

import java.math.BigInteger;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import br.com.padtec.v3.data.LocalHistory;
import br.com.padtec.v3.data.Notification;
import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.impl.FEC_Impl;
import br.com.padtec.v3.data.impl.NE_Impl;
import br.com.padtec.v3.data.impl.T100D_GT_Impl;
import br.com.padtec.v3.data.ne.FEC;
import br.com.padtec.v3.data.ne.TrpOTNTerminal;
import br.com.padtec.v3.data.ne.MultiRate.Rate;
import br.com.padtec.v3.server.protocols.ppm2v2.ColetorPPM2v2;
import br.com.padtec.v3.server.protocols.ppm2v2.PPM2v2;
import br.com.padtec.v3.server.protocols.ppm2v2.PPM2v2Helper;
import br.com.padtec.v3.util.Functions;
import br.com.padtec.v3.util.TrpTables;
import br.com.padtec.v3.util.log.Log;
import br.com.padtec.v3.util.math.OverflowChk;

public class TrpOTNTerminalHandler  implements HandlerInterface<PPM2v2> {
//  private static final boolean debug = false;
  private boolean debugTerminal = false;
  private static final Logger log = Log.getInstance();

  private final ConcurrentHashMap<SerialNumber, Integer> stepOtn = new ConcurrentHashMap<SerialNumber, Integer>();
  private final ConcurrentHashMap<SerialNumber, Integer> stepSapiDapi = new ConcurrentHashMap<SerialNumber, Integer>();

  public boolean canHandle(NE_Impl ne)
  {
    return ne instanceof TrpOTNTerminal;
  }

  public static final int getStep(Map<SerialNumber, Integer> map, SerialNumber serial)
  {
    Integer value = (Integer)map.get(serial);
    if (value == null)
      value = Integer.valueOf(-1);
    else {
      value = Integer.valueOf(value.intValue() + 1);
    }
    map.put(serial, value);
    return value.intValue();
  }

  public List<PPM2v2> getUpdatePacketList(NE_Impl ne)
  {
    List<PPM2v2> result = new LinkedList<PPM2v2>();
    byte[] destino = PPM2v2Helper.getBytes(ne);
    result.add(new PPM2v2(PPM2v2.CMD_GET, PPM2v2.GET_ALL, null, destino, null));
    if (this.debugTerminal) {
      result.add( new PPM2v2(PPM2v2.CMD_GET, PPM2v2.GET_DEBUG, null, destino,  null));
    }
    result.add(  new PPM2v2(PPM2v2.CMD_GET, PPM2v2.GET_OTN_TERM, null, destino, null));
    if (getStep(this.stepOtn, ne.getSerial()) % 3 == 0) {
      result.add(  new PPM2v2(PPM2v2.CMD_GET , PPM2v2.GET_STAT, null, destino, null));
      result.add( new PPM2v2(PPM2v2.CMD_GET, PPM2v2.GET_PT, null, destino, null));
//      if (ne instanceof TrpOTNTerminalSDH) {
//        result.add( new PPM2v2(PPM2v2.CMD_GET, PPM2v2.GET_J0, null, destino, null));
//      }
    }
    if (getStep(this.stepSapiDapi, ne.getSerial()) % 7 == 0) {
      result.add( new PPM2v2(PPM2v2.CMD_GET, PPM2v2.GET_ODU_SAPI_DAPI, null, destino, null));
      result.add( new PPM2v2(PPM2v2.CMD_GET, PPM2v2.GET_OTU_SAPI_DAPI, null, destino, null));
    }
    return result;
  }

  public boolean prepareFullUpdate(NE_Impl ne)
  {
    this.stepOtn.put(ne.getSerial(), Integer.valueOf(-1));
    this.stepSapiDapi.put(ne.getSerial(), Integer.valueOf(-1));
    return true;
  }

  public List<Notification> getAlarmList(NE_Impl ne)
  {
    return null;
  }

  public boolean onReceiveResponse(ColetorPPM2v2 coletor, NE_Impl ne, PPM2v2 pacote, byte parametro, Collection<Notification> alarmList)
  {
    T100D_GT_Impl impl = (T100D_GT_Impl)ne;

    byte[] data = pacote.getDataArray();
    if (parametro == PPM2v2.GET_ALL) {
      if ((data != null) && (data.length == 15)) {
        impl.setLos((data[12] & 0x1) == 1);
        impl.setFail((data[12] & 0x2) == 2);
        impl.setLof((data[12] & 0x4) == 4);
        impl.setLaserOff((data[12] & 0x8) == 8);
        impl.setLos2((data[12] & 0x10) == 16);
        impl.setLaserOff2((data[12] & 0x20) == 32);
        impl.setLof2((data[12] & 0x40) == 64);
        impl.setFail2((data[12] & 0x80) == 128);
        impl.setVersion(Integer.toString((Functions.b2i(data[14]) & 0xF0) >> 4) + 
          "." + Integer.toString(Functions.b2i(data[14]) & 0xF));
        impl.setSlot(Functions.b2i(data[13]) + 1);
        impl.setNominalLambda(TrpTables.getLambda(Functions.b2i(data[10])));
        impl
          .setRealLambda(TrpTables.getLambda(Functions.b2i(data[10])) + data[11] / 100.0D);
        impl.setChannel(TrpTables.getChannelDWDM(Functions.b2i(data[10])));
        if (impl.getOpticalWDMInterface().isLaserOff())
          impl.setPout((0.0D / 0.0D));
        else {
          impl.setPout(TrpTables.getPot10G(false, data));
        }
        if (impl.getOpticalWDMInterface().isLos())
          impl.setPin((0.0D / 0.0D));
        else {
          impl.setPin(TrpTables.getPot10G(true, data));
        }
        if (impl.getOpticalClientInterface().isLaserOff()) {
          impl.setPout2((0.0D / 0.0D));
        }
        if (impl.getOpticalClientInterface().isLos())
          impl.setPin2((0.0D / 0.0D));
      }
      else {
        log.warning("TrpOTNTerminalHandler:Response GET_ALL with bad data for T100D_GT_Impl");
      }
    } else if (parametro == PPM2v2.GET_DEBUG) {
      if (this.debugTerminal) {
        System.out.println("--> Muxponder " + ne.getModel() + " - " + 
          ne.getSerial().toShortString() + " <--");
        System.out
          .println("Pacote: " + Functions.getHexa(data, 0, data.length));
        System.out.println("num Of Reboots : " + 
          (int)Functions.b2l(data, 0, 2));
        System.out.println("last Errored PC: " + 
          (int)Functions.b2l(data, 2, 1));
        System.out.println("errored PC #0  : " + Functions.getHexa(data, 3, 2));
        System.out.println("errored PC #1  : " + Functions.getHexa(data, 5, 2));
        System.out.println("errored PC #2  : " + Functions.getHexa(data, 7, 2));
        System.out.println("errored PC #3  : " + Functions.getHexa(data, 9, 2));
        System.out
          .println("errored PC #4  : " + Functions.getHexa(data, 11, 2));
        System.out
          .println("errored PC #5  : " + Functions.getHexa(data, 13, 2));
        System.out
          .println("errored PC #6  : " + Functions.getHexa(data, 15, 2));
        System.out
          .println("errored PC #7  : " + Functions.getHexa(data, 17, 2));
        System.out
          .println("errored PC #8  : " + Functions.getHexa(data, 19, 2));
        System.out
          .println("errored PC #9  : " + Functions.getHexa(data, 21, 2));
        System.out
          .println("errored PC #10 : " + Functions.getHexa(data, 23, 2));
        System.out
          .println("errored PC #11 : " + Functions.getHexa(data, 25, 2));
        System.out
          .println("errored PC #12 : " + Functions.getHexa(data, 27, 2));
      }
    } else if (parametro == PPM2v2.GET_OTN_TERM) {
      if ((data != null) && (data.length == 50))  {
//        BigInteger total_sdh = BigInteger.ZERO;
//        BigInteger b1 = BigInteger.ZERO;

        BigInteger bip8_odu = OverflowChk.getCorrectValue(data, 
          impl.getBip8_ODUk(), 24, 27, ne);
        BigInteger bei_odu = OverflowChk.getCorrectValue(data, 
          impl.getBei_ODUk(), 28, 31, ne);
        BigInteger bip8_otu = OverflowChk.getCorrectValue(data, 
          impl.getBip8_OTUk(), 32, 35, ne);
        BigInteger bei_otu = OverflowChk.getCorrectValue(data, 
          impl.getBei_OTUk(), 36, 39, ne);

        boolean bdi_otu = (data[40] & 0x1) == 1;
        boolean bdi_odu = (data[40] & 0x2) == 2;
        boolean tim_otu = (data[40] & 0x4) == 4;
        boolean tim_odu = (data[40] & 0x8) == 8;
        boolean ais_odu = (data[40] & 0x10) == 16;
        boolean lossync = (data[40] & 0x20) == 32;
        boolean lossync2 = (data[40] & 0x40) == 64;
        boolean lom = (data[40] & 0x80) == 128;
        boolean fec_tx_correction_enabled = (data[41] & 0x1) == 1;
        boolean fec_rx_stats_enabled = (data[41] & 0x2) == 2;
        boolean fec_rx_correction_enabled = (data[41] & 0x4) == 4;

        int rate = (data[41] & 0x18) >> 3;
        boolean encAIS = (data[41] & 0x20) == 32;

        boolean autoLaserOff = (data[41] & 0x40) == 64;
        boolean autoLaserOff2 = (data[41] & 0x80) == 128;

        byte[] power2 = { -1, 0, 0, 0, 0, -1 };
        System.arraycopy(data, 42, power2, 1, 4);
        System.arraycopy(data, 46, power2, 6, 4);

        /*if (impl instanceof TrpOTNTerminalSDH) {
          SDH_Impl sdh_impl = null;
          if (impl instanceof T100D_GT_SDH_Impl)
            sdh_impl = ((T100D_GT_SDH_Impl)impl).getSDHClientInterface_Impl();
          else if (impl instanceof T100D_GT_Rate_Impl) {
            sdh_impl = ((T100D_GT_Rate_Impl)impl).getSDHClientInterface_Impl();
          }

          b1 = OverflowChk.getCorrectValue(data, sdh_impl.getB1(), 0, 3, ne);
          total_sdh = OverflowChk.getCorrectValue(data, 
            sdh_impl.getFramesSDH(), 10, 15, ne);
          sdh_impl.setFramesSDH(total_sdh);
          sdh_impl.setB1(b1);
        }*/

        FEC fec = impl.getFEC();

        if (fec instanceof FEC_Impl) {
          FEC_Impl rsFec = (FEC_Impl)fec;
          BigInteger total_otn = OverflowChk.getCorrectValue(data, 
            rsFec.getFramesOTN(), 4, 9, ne);
          BigInteger bit = OverflowChk.getCorrectValue(data, rsFec.getFixedBits(), 
            16, 19, ne);
          BigInteger errored = OverflowChk.getCorrectValue(data, 
            rsFec.getErroredBlocks(), 20, 23, ne);
          rsFec.setFramesOTN(total_otn);
          rsFec.setFixedBits(bit);
          rsFec.setErroredBlocks(errored);
          rsFec.setFecTxCorrEnabled(fec_tx_correction_enabled);
          rsFec.setFecRxStatsEnabled(fec_rx_stats_enabled);
          rsFec.setFecRxCorrEnabled(fec_rx_correction_enabled);
        }

        Rate r = Rate.getRate((byte)rate);

        impl.setRate(r);
        impl.setEncAIS(encAIS);
        impl.setAutoLaserOff(autoLaserOff);
        impl.setAutoLaserOff2(autoLaserOff2);
        impl.setLosSync(lossync);
        impl.setLosSync2(lossync2);
        impl.setLom(lom);
        impl.setAis_ODUk(ais_odu);
        impl.setTim_ODUk(tim_odu);
        impl.setBip8_ODUk(bip8_odu);
        impl.setBei_ODUk(bei_odu);
        impl.setBdi_ODUk(bdi_odu);
        impl.setTim_OTUk(tim_otu);
        impl.setBip8_OTUk(bip8_otu);
        impl.setBei_OTUk(bei_otu);
        impl.setBdi_OTUk(bdi_otu);
        if (impl.getOpticalClientInterface().isLaserOff()) {
          impl.setPout2((0.0D / 0.0D));
        }
        else {
          impl.setPout2(TrpTables.getPot10G(false, power2));
        }
        if (impl.getOpticalClientInterface().isLos()) {
          impl.setPin2((0.0D / 0.0D));
        }
        else {
          impl.setPin2(TrpTables.getPot10G(true, power2));
        }

      }

    }
    else if (parametro == PPM2v2.GET_ODU_SAPI_DAPI) {
      if ((data != null) && (data.length == 30)) {
        impl.setSapi_ODUk(new String(data, 0, 15));
        impl.setDapi_ODUk(new String(data, 15, 15));
      }

    }
    else if (parametro == PPM2v2.GET_OTU_SAPI_DAPI) {
      if ((data != null) && (data.length == 30)) {
        impl.setSapi_OTUk(new String(data, 0, 15));
        impl.setDapi_OTUk(new String(data, 15, 15));
      }

    }
    else if (parametro == PPM2v2.GET_PT) {
      if ((data != null) && (data.length == 1)) {
        impl.setPT(data[0]);
      }

    }
    else if (parametro == PPM2v2.GET_STAT) {
      if ((data != null) && (data.length == 1)) {
        impl.setStat_ODUk(data[0]);
      }

    }
    /*else if ((parametro == PPM2v2.GET_J0) && (data != null) && (data.length == 15) &&  (impl instanceof TrpOTNTerminalSDH)) {
      SDH_Impl sdh_impl = null;
      if (impl instanceof T100D_GT_SDH_Impl)
        sdh_impl = ((T100D_GT_SDH_Impl)impl).getSDHClientInterface_Impl();
      else if (impl instanceof T100D_GT_Rate_Impl) {
        sdh_impl = ((T100D_GT_Rate_Impl)impl).getSDHClientInterface_Impl();
      }
      sdh_impl.setJ0(new String(data, 0, 15));
    }
*/
    return true;
  }

  public boolean onReceiveTrap(NE_Impl ne, PPM2v2 trap, List<PPM2v2> packetToSend, List<Notification> event)
  {
    return false;
  }

  public LocalHistory getLocalHistory(NE_Impl ne)
  {
    return null;
  }

  public boolean isFullUpdated(SerialNumber serial)
  {
    return true;
  }
}