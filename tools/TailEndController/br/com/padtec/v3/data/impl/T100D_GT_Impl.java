package br.com.padtec.v3.data.impl;


import java.math.BigInteger;
import java.util.Date;
import java.util.Map;

import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.ne.Bidirectional;
import br.com.padtec.v3.data.ne.ClientInterface;
import br.com.padtec.v3.data.ne.FEC;
import br.com.padtec.v3.data.ne.LaserOff;
import br.com.padtec.v3.data.ne.MultiRate;
import br.com.padtec.v3.data.ne.ODP;
import br.com.padtec.v3.data.ne.ODUk;
import br.com.padtec.v3.data.ne.OTNInterface;
import br.com.padtec.v3.data.ne.OTUk;
import br.com.padtec.v3.data.ne.OpticalInterface;
import br.com.padtec.v3.data.ne.TrpOTNTerminal;
import br.com.padtec.v3.util.modelparser.TransponderModelParser;

public class T100D_GT_Impl extends Transponder_Impl implements TrpOTNTerminal, LaserOff, Bidirectional, MultiRate {
  private static final long serialVersionUID = 4L;
  private ODUk_Impl odu;
  private OTUk_Impl otu;
  private OpticalInterface_Impl oiCliente;
  private OpticalInterface_Impl oiWdm;
  private ClientInterface_Impl interCliente;
  private OTNInterface_Impl otnWdm;
  private FEC_Impl fec;
  private Date lastReset;
  private byte pt;
  private MultiRate.Rate rate;
  private boolean encAIS;
  private ODP_Impl odp;

  public T100D_GT_Impl(SerialNumber serial)  {
    super(serial);

    this.odu = new ODUk_Impl(getK());
    this.otu = new OTUk_Impl(getK());
    this.oiCliente = new OpticalInterface_Impl(false);
    this.oiWdm = new OpticalInterface_Impl(true);
    this.interCliente = new ClientInterface_Impl();
    this.otnWdm = new OTNInterface_Impl();

    this.fec = new FEC_Impl(FEC.FEC_Type.REED_SOLOMON);
    this.rate = MultiRate.Rate.NONE;

    Map<String,String> parsed = TransponderModelParser.parse(getModel());

    if ((parsed.get("familia") != null) && (((String)parsed.get("familia")).compareTo("P") == 0))
      this.odp = new ODP_Impl();
  }

  public OpticalInterface getOpticalWDMInterface()  {
    return this.oiWdm;
  }

  public OpticalInterface_Impl getOpticalWDMInterface_Impl() {
    return this.oiWdm;
  }

  public OpticalInterface getOpticalClientInterface() {
    return this.oiCliente;
  }

  public OpticalInterface_Impl getOpticalClientInterface_Impl() {
    return this.oiCliente;
  }

  public ODUk getODUk() {
    return this.odu;
  }

  public ODUk_Impl getODUk_Impl() {
    return this.odu;
  }

  public OTUk getOTUk() {
    return this.otu;
  }

  public OTUk_Impl getOTUk_Impl() {
    return this.otu;
  }

  public FEC getFEC() {
    return this.fec;
  }

  public byte getPT() {
    return this.pt;
  }
  /**
   * Returns the value of index k. This index represents a supported bit rate and the 
   * different versions of OPUk, ODUk and OTUk. k = 1 represents an approximate bit 
   * rate of 2.5 Gbit/s, k = 2 represents an approximate bit rate of 10 Gbit/s and 
   * k = 3 represents an approximate bit rate of 40 Gbit/s   
   * of bit rates supported on the interface*/
  public int getK()
  {
    return 2;
  }

  public int getStyle() {
    return 5;
  }

  public OTNInterface getOTN_WDMInterface() {
    return this.otnWdm;
  }

  public ClientInterface getClientInterface() {
    return this.interCliente;
  }

  public void resetCounters() {
    this.fec.framesOTN = BigInteger.valueOf(-1L);
    this.fec.fixedBits = BigInteger.valueOf(-1L);
    this.fec.erroredBlocks = BigInteger.valueOf(-1L);
    this.odu.framesOTN = BigInteger.valueOf(-1L);
    this.odu.bip8_ODUk = BigInteger.valueOf(-1L);
    this.odu.bei_ODUk = BigInteger.valueOf(-1L);
    this.otu.framesOTN = BigInteger.valueOf(-1L);
    this.otu.bip8_OTUk = BigInteger.valueOf(-1L);
    this.otu.bei_OTUk = BigInteger.valueOf(-1L);
    this.lastReset = new Date();
  }

  public void setAis_ODUk(boolean status) {
    this.odu.ais_ODUk = status;
  }

  public void setBdi_ODUk(boolean bdi_ODUk) {
    this.odu.bdi_ODUk = bdi_ODUk;
  }

  public void setBei_ODUk(BigInteger bei_ODUk) {
    this.odu.bei_ODUk = bei_ODUk;
  }

  public BigInteger getBei_ODUk() {
    return this.odu.bei_ODUk;
  }

  public void setBip8_ODUk(BigInteger bip8_ODUk) {
    this.odu.bip8_ODUk = bip8_ODUk;
  }

  public BigInteger getBip8_ODUk() {
    return this.odu.bip8_ODUk;
  }

  public Double getBIP8Rate_ODUk() {
    return this.odu.getBIP8Rate();
  }

  public void setDapi_ODUk(String dapi_ODUk) {
    this.odu.dapi_ODUk = dapi_ODUk;
  }

  public void setSapi_ODUk(String sapi_ODUk) {
    this.odu.sapi_ODUk = sapi_ODUk;
  }

  public void setStat_ODUk(int stat_ODUk) {
    this.odu.stat_ODUk = stat_ODUk;
  }

  public int getStat_ODUk() {
    return this.odu.stat_ODUk;
  }

  public void setTim_ODUk(boolean status) {
    this.odu.tim_ODUk = status;
  }

  public void setAis_OTUk(boolean status) {
    this.otu.ais_OTUk = status;
  }

  public void setBdi_OTUk(boolean bdi_OTUk) {
    this.otu.bdi_OTUk = bdi_OTUk;
  }

  public void setBei_OTUk(BigInteger bei_OTUk) {
    this.otu.bei_OTUk = bei_OTUk;
  }

  public BigInteger getBei_OTUk() {
    return this.otu.bei_OTUk;
  }

  public void setBip8_OTUk(BigInteger bip8_OTUk) {
    this.otu.bip8_OTUk = bip8_OTUk;
  }

  public BigInteger getBip8_OTUk() {
    return this.otu.bip8_OTUk;
  }

  public Double getBIP8Rate_OTUk() {
    return this.otu.getBIP8Rate();
  }

  public void setDapi_OTUk(String dapi_OTUk) {
    this.otu.dapi_OTUk = dapi_OTUk;
  }

  public void setSapi_OTUk(String sapi_OTUk) {
    this.otu.sapi_OTUk = sapi_OTUk;
  }

  public void setTim_OTUk(boolean status) {
    this.otu.tim_OTUk = status;
  }

  /**
   * Sets the laser off status in optical interface WDM
   * @param laserOff
   * */
  public void setLaserOff(boolean laserOff) {
    this.oiWdm.setLaserOff(laserOff);
  }

  /**
   * Sets the laser off status in optical interface client
   * @param laserOff2
   * */
  public void setLaserOff2(boolean laserOff2) {
    this.oiCliente.setLaserOff(laserOff2);
  }

  /**
   * Sets the Loss of Signal (LoS) status in optical interface WDM
   * and Transponder_impl
   * @param autoLaserOff
   * */
  public void setLos(boolean los)
  {
    super.setLos(los);
    this.oiWdm.setLos(los);
  }

  /**
   * Returns Loss of Signal (LoS) the Optical Interface WDM 
   * */
  public boolean isLos() {
    return this.oiWdm.isLos();
  }

  /**
   * Returns the Optical Interface WDM Laser OFF
   * */
  public boolean isLaserOff() {
    return this.oiWdm.isLaserOff();
  }

  /**
   * Returns the Optical Interface WDM Fail
   * */
  public boolean isFail() {
    return this.oiWdm.isFail();
  }

  /**
   * Sets the fail status in optical interface WDM
   * and Transponder_impl
   * @param fail
   * */
  public void setFail(boolean fail) {
    super.setFail(fail);
    this.oiWdm.setFail(fail);
  }

  /**
   * Sets the fail status in optical interface client
   * @param fail2
   * */
  public void setFail2(boolean fail2) {
    this.oiCliente.setFail(fail2);
  }

  /**
   * Sets the loss of signal status in optical interface client
   * @param los2
   * */
  public void setLos2(boolean los2) {
    this.oiCliente.setLos(los2);
  }

  /**
   * Sets the Pin in optical interface WDM
   * and Transponder_impl
   * @param pin
   * */
  public void setPin(double pin) {
    super.setPin(pin);
    this.oiWdm.setPin(pin);
  }

  /**
   * Sets the Pout in optical interface WDM
   * and Transponder_impl
   * @param pout
   * */
  public void setPout(double pout) {
    super.setPout(pout);
    this.oiWdm.setPout(pout);
  }

  /**
   * Sets the Pin in optical interface client
   * @param pin2
   * */
  public void setPin2(double pin2) {
    this.oiCliente.setPin(pin2);
  }
  
  /**
   * Sets the Pout in optical interface client
   * @param pout2
   * */
  public void setPout2(double pout2) {
    this.oiCliente.setPout(pout2);
  }

  /**
  * Sets the Nominal Lambda in optical interface WDM
  * and Transponder_impl
  * @param lambda
  * */
  public void setNominalLambda(double lambda) {
    super.setNominalLambda(lambda);
    this.oiWdm.setLambdaNominal(lambda);
  }

  /**
   * Sets the Real Lambda in optical interface WDM
   * @param lambda
   * */
  public void setRealLambda(double lambda) {
    this.oiWdm.setLambdaReal(lambda);
  }

  /**
   * Sets the channel in optical interface WDM
   * and Transponder_impl
   * @param channel
   * */
  public void setChannel(String channel) {
    super.setChannel(channel);
    this.oiWdm.setChannel(channel);
  }

  /**
   * Sets the channel in optical interface client
   * @param channel
   * */
  public void setChannel2(String channel) {
    this.oiCliente.setChannel(channel);
  }

  /**
   * Sets the Nominal Lambda in optical interface client
   * @param lambda
   * */
  public void setLambdaNominal2(double lambda) {
    this.oiCliente.setLambdaNominal(lambda);
  }

  /**
   * Sets the Real Lambda in optical interface clent
   * @param lambda
   * */
  public void setLambdaReal2(double lambda) {
    this.oiCliente.setLambdaReal(lambda);
  }

  /**
   * Sets the status Loss of Frame  (LoF) in OTNInterface_Impl object
   * @param lof The indication of Loss of Frame
   * */
  public void setLof(boolean lof) {
    this.otnWdm.lof = lof;
  }

  /**
   * Sets the status Loss of Frame (LoF) in ClientInterface_Impl object
   * @param lof2 The indication of Loss of Frame in the client interface
   * */
  public void setLof2(boolean lof2) {
    this.interCliente.lof = lof2;
  }

  /**
   * Sets the Loss of Multi-frame Alarm (LoM) in optical transport network (OTN) interface
   * @param lom
   * */
  public void setLom(boolean lom) {
    this.otnWdm.lom = lom;
  }

  /**
   * Sets the LosSync in optical transport network (OTN) interface
   * @param status
   * */
  public void setLosSync(boolean status) {
    this.otnWdm.losSync = status;
  }

  /**
   * Sets the LosSync in interface client
   * @param status
   * */
  public void setLosSync2(boolean status) {
    this.interCliente.losSync = status;
  }

  @Deprecated
  public void setN3db(boolean n3db) {
  }

  public void setPT(byte pt) {
    this.pt = pt;
  }

  public void setRate(MultiRate.Rate r) {
    this.rate = r;
  }

  public MultiRate.Rate getRate() {
    return this.rate;
  }
  /***/
  public boolean isEncAIS() {
    return this.encAIS;
  }
  /***/
  public void setEncAIS(boolean encAIS)  {
    this.encAIS = encAIS;
  }

  public Date lastReset() {
    return this.lastReset;
  }

  /**
   * Sets the auto laser off status in optical interface WDM
   * @param autoLaserOff
   * */
  public void setAutoLaserOff(boolean autoLaserOff) {
    this.oiWdm.setAutoLaserOff(autoLaserOff);
  }

  /**
   * Sets the auto laser off status in optical interface client
   * @param autoLaserOff2
   * */
  public void setAutoLaserOff2(boolean autoLaserOff2) {
    this.oiCliente.setAutoLaserOff(autoLaserOff2);
  }

  public int getSubBastidor()
  {
    if (getSlot() % 10 == 0) {
      return (getSlot() / 10);
    }
    return (int)(Math.floor(getSlot() / 10) + 1.0D);
  }

  public int getSubBastidorSlot()
  {
    if (getSlot() > 10) {
      return (getSlot() - ((getSubBastidor() - 1) * 10));
    }
    return getSlot();
  }

  public ODP getODP()
  {
    return this.odp;
  }

  public ODP_Impl getODP_Impl() {
    return this.odp;
  }
}