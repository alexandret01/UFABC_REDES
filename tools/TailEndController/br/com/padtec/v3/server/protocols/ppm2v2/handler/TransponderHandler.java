package br.com.padtec.v3.server.protocols.ppm2v2.handler;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import br.com.padtec.v3.data.LocalHistory;
import br.com.padtec.v3.data.Notification;
import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.impl.NE_Impl;
import br.com.padtec.v3.data.impl.Transponder_Impl;
import br.com.padtec.v3.data.impl.TrpBiDWDMRate_Impl;
import br.com.padtec.v3.data.impl.TrpDWDM_Impl;
import br.com.padtec.v3.data.impl.TrpGBEthD_Impl;
import br.com.padtec.v3.data.ne.LaserOff;
import br.com.padtec.v3.data.ne.SupSPVJ;
import br.com.padtec.v3.data.ne.TransponderOTN;
import br.com.padtec.v3.server.protocols.ppm2v2.ColetorPPM2v2;
import br.com.padtec.v3.server.protocols.ppm2v2.PPM2v2;
import br.com.padtec.v3.util.Functions;
import br.com.padtec.v3.util.TrpTables;
import br.com.padtec.v3.util.log.Log;

public class TransponderHandler  implements HandlerInterface<PPM2v2> {
//  private static final boolean debug = false;
  private static Logger log = Log.getInstance();
  private static final int POS_PIN = 1;
  private static final int POS_POUT = 6;
  private ConcurrentHashMap<SerialNumber, LocalHistory> localHistory = new ConcurrentHashMap<SerialNumber, LocalHistory>();

  public boolean canHandle(NE_Impl ne) {
    return ne instanceof Transponder_Impl;
  }

  public List<PPM2v2> getUpdatePacketList(NE_Impl ne) {
    return null;
  }

  public boolean prepareFullUpdate(NE_Impl ne) {
    return false;
  }

  public boolean onReceiveTrap(NE_Impl ne, PPM2v2 trap, List<PPM2v2> packetToSend, List<Notification> event)  {
    return false;
  }

  private SerialNumber getSerial(byte[] origin)
  {
    int part = (Functions.b2i(origin[1]) << 8) + Functions.b2i(origin[2]);
    int ser = (Functions.b2i(origin[3]) << 8) + Functions.b2i(origin[4]);
    return new SerialNumber(part, ser);
  }

  public boolean onReceiveResponse(ColetorPPM2v2 coletor, NE_Impl ne, PPM2v2 pacote, byte parametro, 
		  Collection<Notification> alarmList)  {
    byte[] data = pacote.getDataArray();
    if (parametro == PPM2v2.GET_PERF_HISTORY_TLV) {
      Transponder_Impl transponder = (Transponder_Impl)ne;
      byte[] tlv = pacote.getDataArray();
      LocalHistory auxHist = (LocalHistory)this.localHistory.get(transponder.getSerial());
      if (auxHist == null) {
        auxHist = new LocalHistory();
        auxHist.setSerialNumber(transponder.getSerial());
        this.localHistory.put(transponder.getSerial(), auxHist);
      }
      if (Functions.b2i(tlv[0]) == 48) {
        double pin;
        double pout;
        int idx = Functions.b2i(tlv[1]);
        byte[] pot = new byte[10];
        System.arraycopy(tlv, 3, pot, 0, 10);

        if ((transponder instanceof TransponderOTN)/* || (transponder instanceof LogPower)*/) {
          pin = TrpTables.getPot10G(true, pot);
          pout = TrpTables.getPot10G(false, pot);
        } else {
          pin = TrpTables.getPower(Functions.b2i(pot[0]), POS_PIN, pot, TrpTables.isPinLog(ne));
          pout = TrpTables.getPower(Functions.b2i(pot[5]), POS_POUT, pot, true);
        }
        if (transponder instanceof LaserOff) {
          LaserOff t = (LaserOff)transponder;
          if (t.isLaserOff())
            pout = (0.0D / 0.0D);
        }
        if (transponder.isLos())
          pin = (0.0D / 0.0D);
        auxHist.addTempPoint(idx, pin, pout);
      } else if (Functions.b2i(tlv[0]) == 0) {
        auxHist.finishPoints(Functions.b2i(tlv[1]), Functions.b2l(tlv, 3, 8));
      }

    }

    if (ne.getSerial().equals(getSerial(pacote.getSource()))) {
      /*if (ne instanceof TrpGBEthC_Impl)
        updateTrpGBEthC_Impl((TrpGBEthC_Impl)ne, parametro, data);
      else */
    	if (ne instanceof TrpBiDWDMRate_Impl)
        updateTrpBiDWDMRate_Impl(coletor, (TrpBiDWDMRate_Impl)ne, parametro, 
          data);
      else if (ne instanceof TrpGBEthD_Impl)
        updateTrpGBEthD_Impl((TrpGBEthD_Impl)ne, parametro, data);
      /*else if (ne instanceof TrpCWDM_Impl)
        updateTrpCWDM_Impl((TrpCWDM_Impl)ne, parametro, data);
      else if (ne instanceof TrpCWDMRate_Impl)
        updateTrpCWDMRate_Impl((TrpCWDMRate_Impl)ne, parametro, data);
      else if (ne instanceof TrpDWDM10_Impl)
        updateTrpDWDM10_Impl((TrpDWDM10_Impl)ne, parametro, data);
      else if (ne instanceof TrpDWDM25Otn_Impl)
        updateTrpDWDM25Otn_Impl((TrpDWDM25Otn_Impl)ne, parametro, data);*/
      else if (ne instanceof TrpDWDM_Impl)
        updateTrpDWDM_Impl((TrpDWDM_Impl)ne, parametro, data);
      /*else if (ne instanceof TrpFECRX_Impl)
        updateTrpFECRX_Impl((TrpFECRX_Impl)ne, parametro, data);
      else if (ne instanceof TrpTrS25_Impl)
        updateTrpTrS25_Impl((TrpTrS25_Impl)ne, parametro, data);
      else if (ne instanceof T100D_GC_Impl)
        updateT100D_GC_Impl((T100D_GC_Impl)ne, parametro, data);*/
    } else {
      log.severe("Serial Number mismatch. Internal: " + ne.getSerial().toShortString() + " PPM2v2: " +
    		  getSerial(pacote.getSource()).toShortString());
    }
    return true;
  }

  private void updateTrpBiDWDMRate_Impl(ColetorPPM2v2 coletor, TrpBiDWDMRate_Impl transponder, 
		  byte parametro, byte[] data) {
    if (parametro == PPM2v2.GET_ALL) {
      updateTrpGBEthD_Impl(transponder, parametro, data);
      transponder.setOverRate((data[12] & 0x40) == 64);
      SupSPVJ sup = coletor.getSiteSpvj(transponder.getSupAddress());
      String spvjVerTaxa2 = Functions.getProperty("spvjVerTaxa2", null);
      if ((spvjVerTaxa2 != null) && (sup != null) &&  (Functions.compareVersions(sup.getVersion(), spvjVerTaxa2) >= 0)) 
    	  transponder.setOverRate2((data[12] & 0x80) == 128);
    }
    else if (parametro == PPM2v2.GET_TAXA) {
      int taxa = Functions.b2i(data[0]) * 256 + Functions.b2i(data[1]);
      int limiar = Functions.b2i(data[2]) * 256 + Functions.b2i(data[3]);
      transponder.setRate(TrpTables.getFrequency(taxa));
      transponder.setLimiar(TrpTables.getFrequency(limiar));
    }
  }

/*  private void updateTrpTrS25_Impl(TrpTrS25_Impl transponder, byte parametro, byte[] data)
  {
    if (parametro == 0)
      if ((data != null) && (data.length == 15)) {
        updateBasic(transponder, data);
        updatePinPout(transponder, data);
        if (transponder.isLos()) {
          transponder.setPin((0.0D / 0.0D));
        }
        transponder
          .setRealLambda(TrpTables.getLambda(Functions.b2i(data[10])) + data[11] / 100.0D);
        transponder.setChannel(
          TrpTables.getChannelDWDM(Functions.b2i(data[10])));
      } else {
        log.warning("Response GET_ALL with bad data for TrpTrS25_Impl");
      }
  }*/

/*  private void updateTrpGBEthC_Impl(TrpGBEthC_Impl transponder, byte parametro, byte[] data)
  {
    if (parametro == 0)
      if ((data != null) && (data.length == 15)) {
        updateTrpCWDM_Impl(transponder, parametro, data);
        transponder.setLos2((data[12] & 0x10) == 16);
        transponder.setLaserOff2((data[12] & 0x20) == 32);
      } else {
        log.warning("Response GET_ALL with bad data for TrpGBEthC_Impl");
      }
  }*/

/*  private void updateTrpCWDMRate_Impl(TrpCWDMRate_Impl transponder, byte parametro, byte[] data)
  {
    if (data != null)
      if (parametro == 0) {
        updateBasic(transponder, data);
        updatePinPout(transponder, data);
        boolean loff = (data[12] & 0x8) == 8;
        transponder.setLaserOff(loff);
        if (loff) {
          transponder.setPout((0.0D / 0.0D));
        }
        if (transponder.isLos()) {
          transponder.setPin((0.0D / 0.0D));
        }
        transponder.setChannel(
          TrpTables.getChannelCWDM(Functions.b2i(data[10])));
        transponder.setOver((data[12] & 0x40) == 64);
      } else if (parametro == 1) {
        int taxa = Functions.b2i(data[0]) * 256 + Functions.b2i(data[1]);
        int limiar = Functions.b2i(data[2]) * 256 + Functions.b2i(data[3]);
        transponder.setRate(TrpCWDMRate_Impl.getFrequency(taxa));
        transponder.setLimiar(TrpCWDMRate_Impl.getFrequency(limiar));
      }
    else
      log.warning("Response GET_ALL with bad data for TrpCWDMRate_Impl");
  }*/

/*  private void updateTrpCWDM_Impl(TrpCWDM_Impl transponder, byte parametro, byte[] data)
  {
    if (parametro == 0)
      if ((data != null) && (data.length == 15)) {
        updateBasic(transponder, data);
        updatePinPout(transponder, data);
        boolean loff = (data[12] & 0x8) == 8;
        transponder.setLaserOff(loff);
        if (loff) {
          transponder.setPout((0.0D / 0.0D));
        }
        if (transponder.isLos()) {
          transponder.setPin((0.0D / 0.0D));
        }
        transponder.setChannel(
          TrpTables.getChannelCWDM(Functions.b2i(data[10])));
      } else {
        log.warning("Response GET_ALL with bad data for TrpCWDM_Impl");
      }
  }*/

  private void updateTrpGBEthD_Impl(TrpGBEthD_Impl transponder, byte parametro, byte[] data)
  {
    if (parametro == PPM2v2.GET_ALL)
      if ((data != null) && (data.length == 15)) {
        updateTrpDWDM_Impl(transponder, parametro, data);
        transponder.setLos2((data[12] & 0x10) == 16);
        transponder.setLaserOff2((data[12] & 0x20) == 32);
      } else {
        log.warning("Response GET_ALL with bad data for TrpGBEthD_Impl");
      }
  }

/*  private void updateTrpDWDM10_Impl(TrpDWDM10_Impl transponder, byte parametro, byte[] data) {
    if (parametro == 0)
      if ((data != null) && (data.length == 15)) {
        updateBasic(transponder, data);
        updatePinPoutAlt(transponder, data);
        boolean loff = (data[12] & 0x8) == 8;
        transponder.setLaserOff(loff);
        if (loff) {
          transponder.setPout((0.0D / 0.0D));
        }
        if (transponder.isLos()) {
          transponder.setPin((0.0D / 0.0D));
        }
        transponder
          .setRealLambda(TrpTables.getLambda(Functions.b2i(data[10])) + data[11] / 100.0D);
        transponder.setChannel(
          TrpTables.getChannelDWDM(Functions.b2i(data[10])));
      } else {
        log.warning("Response GET_ALL with bad data for TrpDWDM10_Impl");
      }
  }*/

/*  private void updateTrpFECRX_Impl(TrpFECRX_Impl transponder, byte parametro, byte[] data)
  {
    if (parametro == 0)
      if ((data != null) && (data.length == 15)) {
        updateBasic(transponder, data);
        updatePinPout(transponder, data);
        boolean loff = (data[12] & 0x8) == 8;
        transponder.setLaserOff(loff);
        if (loff) {
          transponder.setPout((0.0D / 0.0D));
        }
        if (transponder.isLos()) {
          transponder.setPin((0.0D / 0.0D));
        }
        transponder.setChannel("N/D");
      } else {
        log.warning("Response GET_ALL with bad data for TrpFECRX_Impl");
      }
  }*/

/*  private void updateTrpDWDM25Otn_Impl(TrpDWDM25Otn_Impl impl, byte parametro, byte[] data)
  {
    if (parametro == 0) {
      if ((data != null) && (data.length == 15)) {
        updateTrpDWDM_Impl(impl, parametro, data);
        impl.setLof((data[12] & 0x4) == 4);
        impl.setLos2((data[12] & 0x10) == 16);
        impl.setLaserOff2((data[12] & 0x20) == 32);
        impl.setLof2((data[12] & 0x40) == 64);
      }
    } else if (parametro == 5) {
      if ((data == null) || (data.length != 34))
        return;
      BigInteger b1 = OverflowChk.getCorrectValue(data, impl.getB1(), 0, 3, 
        impl);
      BigInteger total_otn = OverflowChk.getCorrectValue(data, 
        impl.getFramesOTN(), 4, 9, impl);
      BigInteger bit = OverflowChk.getCorrectValue(data, impl.getFixedBits(), 
        10, 13, impl);
      BigInteger bip8_odu = OverflowChk.getCorrectValue(data, 
        impl.getBip8_ODUk(), 14, 17, impl);
      BigInteger bei_odu = OverflowChk.getCorrectValue(data, 
        impl.getBei_ODUk(), 18, 21, impl);

      int stat = data[22] & 0x7;
      boolean bdi_odu = (data[22] & 0x8) == 8;
      boolean ais_odu = (data[22] & 0x10) == 16;
      boolean lossync = (data[22] & 0x20) == 32;
      boolean lossync2 = (data[22] & 0x40) == 64;
      boolean tim_odu = (data[22] & 0x80) == 128;

      BigInteger errored = OverflowChk.getCorrectValue(data, 
        impl.getErroredBlocks(), 23, 26, impl);

      boolean fec_tx_correction_enabled = (data[27] & 0x1) == 1;
      boolean fec_rx_stats_enabled = (data[27] & 0x2) == 2;
      boolean fec_rx_correction_enabled = (data[27] & 0x4) == 4;
      boolean fec_tx2_correction_enabled = (data[27] & 0x8) == 8;
      boolean fec_rx2_stats_enabled = (data[27] & 0x10) == 16;
      boolean fec_rx2_correction_enabled = (data[27] & 0x20) == 32;
      boolean autoLaserOff = (data[27] & 0x40) == 64;
      boolean autoLaserOff2 = (data[27] & 0x80) == 128;

      BigInteger total_sdh = OverflowChk.getCorrectValue(data, 
        impl.getFramesSDH(), 28, 33, impl);

      impl.setB1(b1);
      impl.setFramesOTN(total_otn);
      impl.setFramesSDH(total_sdh);
      impl.setFixedBits(bit);
      impl.setErroredBlocks(errored);
      impl.setFecTxCorrEnabled(fec_tx_correction_enabled);
      impl.setFecRxStatsEnabled(fec_rx_stats_enabled);
      impl.setFecRxCorrEnabled(fec_rx_correction_enabled);
      impl.setFecTx2CorrEnabled(fec_tx2_correction_enabled);
      impl.setFecRx2StatsEnabled(fec_rx2_stats_enabled);
      impl.setFecRx2CorrEnabled(fec_rx2_correction_enabled);
      impl.setLosSync(lossync);
      impl.setLosSync2(lossync2);
      impl.setAis_ODUk(ais_odu);
      impl.setAutoLaserOff(Boolean.valueOf(autoLaserOff));
      impl.setAutoLaserOff2(Boolean.valueOf(autoLaserOff2));
      impl.setTim_ODUk(tim_odu);
      impl.setBip8_ODUk(bip8_odu);
      impl.setBei_ODUk(bei_odu);
      impl.setStat_ODUk(stat);
      impl.setBdi_ODUk(bdi_odu);
    }
    else if (parametro == 6) {
      if ((data != null) && (data.length == 30)) {
        impl.setSapi_ODUk(new String(data, 0, 15));
        impl.setDapi_ODUk(new String(data, 15, 15));
      }

    }
    else if (parametro == 7) {
      if ((data == null) || (data.length != 15)) {
        return;
      }

      impl.setJ0(new String(data, 0, 15));
    } else {
      if ((parametro != 8) || 
        (data == null) || (data.length != 1))
      {
        return;
      }

      impl.setPT(data[0]);
    }
  }*/

  /*private void updatePinPoutAlt(Transponder_Impl trp, byte[] data)
  {
    trp.setPin(TrpTables.getPot10G(true, data));

    trp.setPout(TrpTables.getPot10G(false, data));
  }*/

  private void updateTrpDWDM_Impl(TrpDWDM_Impl transponder, byte parametro, byte[] data)
  {
    if (parametro == PPM2v2.GET_ALL)
      if ((data != null) && (data.length == 15)) {
        updateBasic(transponder, data);
        updatePinPout(transponder, data);
        boolean loff = (data[12] & 0x8) == 8;
        transponder.setLaserOff(loff);
        if (loff) {
          transponder.setPout((0.0D / 0.0D));
        }
        if (transponder.isLos()) {
          transponder.setPin((0.0D / 0.0D));
        }
        transponder
          .setRealLambda(TrpTables.getLambda(Functions.b2i(data[10])) + data[11] / 100.0D);
        transponder.setChannel(
          TrpTables.getChannelDWDM(Functions.b2i(data[10])));
      } else {
        log.warning("Response GET_ALL with bad data for TrpDWDM_Impl");
      }
  }

 /* private void printArray(byte[] t, int off, int size) {
    System.err.print(size);
    System.err.print(':');
    System.err.print(' ');
    for (int i = off; i < size; ++i) {
      System.err.print(Integer.toHexString(Functions.b2i(t[i])));
      System.err.print(' ');
    }
    System.err.println();
  }*/

  private void updateBasic(Transponder_Impl trp, byte[] data)
  {
    trp.setLos((data[12] & 0x1) == 1);

    trp.setFail((data[12] & 0x2) == 2);

    trp.setN3db((data[12] & 0x4) == 4);

    trp.setVersion(Integer.toString((Functions.b2i(data[14]) & 0xF0) >> 4) + 
      "." + Integer.toString(Functions.b2i(data[14]) & 0xF));

    trp.setSlot(Functions.b2i(data[13]) + 1);

    trp.setNominalLambda(TrpTables.getLambda(Functions.b2i(data[10])));
  }

  private void updatePinPout(Transponder_Impl trp, byte[] data)
  {
    trp.setPin(
      TrpTables.getPower(Functions.b2i(data[0]), 1, data, 
      TrpTables.isPinLog(trp)));

    trp.setPout(
      TrpTables.getPower(Functions.b2i(data[5]), 6, data, true));
  }

/*  private void updateT100D_GC_Impl(T100D_GC_Impl impl, byte parametro, byte[] data)
  {
    if (parametro == 0) {
      if ((data != null) && (data.length == 15))
      {
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
        impl.setLambda_A(TrpTables.getLambda(Functions.b2i(data[10])));
        impl
          .setRealLambda_A(TrpTables.getLambda(Functions.b2i(data[10])) + data[11] / 100.0D);
        impl.setChannel(TrpTables.getChannelDWDM(Functions.b2i(data[10])));
        if (impl.getInterfaceA().isLaserOff())
          impl.setPout((0.0D / 0.0D));
        else {
          impl.setPout(TrpTables.getPot10G(false, data));
        }
        if (impl.getInterfaceA().isLos())
          impl.setPin((0.0D / 0.0D));
        else {
          impl.setPin(TrpTables.getPot10G(true, data));
        }
        if (impl.getInterfaceB().isLaserOff()) {
          impl.setPout2((0.0D / 0.0D));
        }
        if (impl.getInterfaceB().isLos())
          impl.setPin2((0.0D / 0.0D));
      }
      else {
        log.warning("Response GET_ALL with bad data for T100D_GT_Impl");
      }
    } else if (parametro == 17) {
      if ((data == null) || (data.length != 56))
      {
        return;
      }

      BigInteger total_otn_a = OverflowChk.getCorrectValue(data, 
        impl.getFec_A().getFramesOTN(), 0, 5, impl);
      BigInteger total_otn_b = OverflowChk.getCorrectValue(data, 
        impl.getFec_B().getFramesOTN(), 6, 11, impl);
      BigInteger bit_a = OverflowChk.getCorrectValue(data, 
        impl.getFec_A().getFixedBits(), 12, 15, impl);
      BigInteger bit_b = OverflowChk.getCorrectValue(data, 
        impl.getFec_B().getFixedBits(), 20, 23, impl);
      BigInteger errored_a = OverflowChk.getCorrectValue(data, 
        impl.getFecA().getErroredBlocks(), 16, 19, impl);
      BigInteger errored_b = OverflowChk.getCorrectValue(data, 
        impl.getFecB().getErroredBlocks(), 24, 27, impl);
      BigInteger bip8_a = OverflowChk.getCorrectValue(data, 
        impl.getOTUkA().getBip8(), 28, 31, impl);
      BigInteger bei_a = OverflowChk.getCorrectValue(data, 
        impl.getOTUkA().getBei(), 32, 35, impl);
      BigInteger bip8_b = OverflowChk.getCorrectValue(data, 
        impl.getOTUkB().getBip8(), 36, 39, impl);
      BigInteger bei_b = OverflowChk.getCorrectValue(data, 
        impl.getOTUkB().getBei(), 40, 43, impl);

      boolean bdi_a = (data[44] & 0x1) == 1;
      boolean bdi_b = (data[44] & 0x2) == 2;
      boolean tim_a = (data[44] & 0x4) == 4;
      boolean tim_b = (data[44] & 0x8) == 8;
      boolean lom_a = (data[44] & 0x10) == 16;
      boolean lom_b = (data[44] & 0x20) == 32;
      boolean lossync_a = (data[44] & 0x40) == 64;
      boolean lossync_b = (data[44] & 0x80) == 128;

      boolean fec_tx_correction_enabled_a = (data[45] & 0x1) == 1;
      boolean fec_rx_stats_enabled_a = (data[45] & 0x2) == 2;
      boolean fec_rx_correction_enabled_a = (data[45] & 0x4) == 4;
      boolean fec_tx_correction_enabled_b = (data[45] & 0x8) == 8;
      boolean fec_rx_stats_enabled_b = (data[45] & 0x10) == 16;
      boolean fec_rx_correction_enabled_b = (data[45] & 0x20) == 32;
      boolean autoLaserOff = (data[45] & 0x40) == 64;
      boolean autoLaserOff2 = (data[45] & 0x80) == 128;

      int rate = data[46] & 0x3;
      boolean encAIS = (data[46] & 0x4) == 4;

      int channel2 = Functions.b2i(data[47]);

      byte[] power2 = { -1, 0, 0, 0, 0, -1 };
      System.arraycopy(data, 48, power2, 1, 4);
      System.arraycopy(data, 52, power2, 6, 4);

      impl.setFramesOTN_A(total_otn_a);
      impl.setFixedBits_A(bit_a);
      impl.setErroredBlocks_A(errored_a);

      impl.setFramesOTN_B(total_otn_b);
      impl.setFixedBits_B(bit_b);
      impl.setErroredBlocks_B(errored_b);

      impl.setFecTxCorrEnabled_A(fec_tx_correction_enabled_a);
      impl.setFecRxStatsEnabled_A(fec_rx_stats_enabled_a);
      impl.setFecRxCorrEnabled_A(fec_rx_correction_enabled_a);
      impl.setFecTxCorrEnabled_B(fec_tx_correction_enabled_b);
      impl.setFecRxStatsEnabled_B(fec_rx_stats_enabled_b);
      impl.setFecRxCorrEnabled_B(fec_rx_correction_enabled_b);
      impl.setAutoLaserOff(autoLaserOff);
      impl.setAutoLaserOff2(autoLaserOff2);

      MultiRate.Rate r = MultiRate.Rate.getRate((byte)rate);

      impl.setRate(r);
      impl.setEncAIS(encAIS);
      impl.setAutoLaserOff(autoLaserOff);
      impl.setAutoLaserOff2(autoLaserOff2);
      impl.setLosSync(lossync_a);
      impl.setLosSync2(lossync_b);
      impl.setLom_A(lom_a);
      impl.setLom_B(lom_b);
      impl.setTim_OTUk_A(tim_a);
      impl.setBip8_OTUk_A(bip8_a);
      impl.setBei_OTUk_A(bei_a);
      impl.setBdi_OTUk_A(bdi_a);
      impl.setTim_OTUk_B(tim_b);
      impl.setBip8_OTUk_B(bip8_b);
      impl.setBei_OTUk_B(bei_b);
      impl.setBdi_OTUk_B(bdi_b);
      if (impl.getInterfaceB().isLaserOff()) {
        impl.setPout2((0.0D / 0.0D));
      }
      else {
        impl.setPout2(TrpTables.getPot10G(false, power2));
      }
      if (impl.getInterfaceB().isLos()) {
        impl.setPin2((0.0D / 0.0D));
      }
      else {
        impl.setPin2(TrpTables.getPot10G(true, power2));
      }

      impl.setChannel2(TrpTables.getChannelDWDM(channel2));
      impl.setLambda_B(TrpTables.getLambda(channel2));
    }
    else if (parametro == 13) {
      if ((data != null) && (data.length == 30)) {
        impl.setSapi_OTUk_A(new String(data, 0, 15));
        impl.setDapi_OTUk_A(new String(data, 15, 15));
      }

    }
    else
    {
      if ((parametro != 18) || 
        (data == null) || (data.length != 30)) return;
      impl.setSapi_OTUk_B(new String(data, 0, 15));
      impl.setDapi_OTUk_B(new String(data, 15, 15));
    }
  }*/

  public List<Notification> getAlarmList(NE_Impl ne)
  {
    return null;
  }

  public boolean isFullUpdated(SerialNumber serial) {
    return true;
  }
}