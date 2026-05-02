package br.com.padtec.v3.data.impl;



import java.io.Serializable;
import java.math.BigInteger;

import br.com.padtec.v3.data.ne.ODUk;
import br.com.padtec.v3.util.Msg;


public class ODUk_Impl implements ODUk, Serializable {
  private static final long serialVersionUID = 4L;
  private static final int OPU_COLS = 3810;
  private static final int BitsPerFrameOPU = 121920;
  BigInteger bip8_ODUk = BigInteger.ZERO;
  BigInteger bei_ODUk = BigInteger.ZERO;
  /*status bits for indication of maintenance signal*/
  int stat_ODUk;
  /*Backward Error Indication*/
  boolean bdi_ODUk;
  /**/
  boolean tim_ODUk;
  boolean ais_ODUk;
  /*Source Access Point Identifier*/
  String sapi_ODUk;
  /*Destination Access Point Identifier*/
  String dapi_ODUk;
  private String sapiRefTx_ODUk;
  private String dapiRefTx_ODUk;
  private String sapiRefRx_ODUk;
  private String dapiRefRx_ODUk;
  private String opSpRefTx_ODUk;
  private String opSpRefRx_ODUk;
  BigInteger framesOTN = BigInteger.ZERO;
  /*The index of bit rate supported*/
  private int k;

  public ODUk_Impl(int k)  {
    this.k = k;
  }

  public BigInteger getBip8() {
    return this.bip8_ODUk;
  }

  public Double getBIP8Rate() {
    if ((this.framesOTN.compareTo(BigInteger.valueOf(-1L)) == 0) || 
      (this.bip8_ODUk.compareTo(BigInteger.valueOf(-1L)) == 0))
      return Double.valueOf(-1.0D);
    if ((this.framesOTN.compareTo(BigInteger.ZERO) == 0) || 
      (this.bip8_ODUk.compareTo(BigInteger.ZERO) == 0)) {
      return Double.valueOf(0.0D);
    }

    double div = this.framesOTN.doubleValue() * BitsPerFrameOPU;
    double ret = this.bip8_ODUk.doubleValue() / div;
    return Double.valueOf(ret);
  }

  public BigInteger getBei() {
    return this.bei_ODUk;
  }

  public Double getBEIRate() {
    if ((this.framesOTN.compareTo(BigInteger.valueOf(-1L)) == 0) || 
      (this.bei_ODUk.compareTo(BigInteger.valueOf(-1L)) == 0))
      return Double.valueOf(-1.0D);
    if ((this.framesOTN.compareTo(BigInteger.ZERO) == 0) || 
      (this.bei_ODUk.compareTo(BigInteger.ZERO) == 0)) {
      return Double.valueOf(0.0D);
    }

    double div = this.framesOTN.doubleValue() * BitsPerFrameOPU;
    double ret = this.bei_ODUk.doubleValue() / div;
    return Double.valueOf(ret);
  }

  public int getStat() {
    return this.stat_ODUk;
  }
  /**
   * Returns the status in the Optical Data Unit
   * */
  public String getStatDesc() {
    if (this.stat_ODUk == 1)
      return Msg.getString("ODUk.1");
    if (this.stat_ODUk == 5)
      return Msg.getString("ODUk.5");
    if (this.stat_ODUk == 6)
      return Msg.getString("ODUk.6");
    if (this.stat_ODUk == 7) {
      return Msg.getString("ODUk.7");
    }
    return Msg.getString("ODUk.0");
  }

  public boolean isBdi()
  {
    return this.bdi_ODUk;
  }

  public String getSAPI() {
    return this.sapi_ODUk;
  }

  public String getDAPI() {
    return this.dapi_ODUk;
  }

  public boolean isTim() {
    return this.tim_ODUk;
  }

  public boolean isAis() {
    return this.ais_ODUk;
  }

  public void setAis(boolean ais_ODUk) {
    this.ais_ODUk = ais_ODUk;
  }

  public void setBdi(boolean bdi_ODUk) {
    this.bdi_ODUk = bdi_ODUk;
  }

  public void setBei(BigInteger bei_ODUk) {
    this.bei_ODUk = bei_ODUk;
  }

  public void setBip8(BigInteger bip8_ODUk) {
    this.bip8_ODUk = bip8_ODUk;
  }

  public void setDapi(String dapi_ODUk) {
    this.dapi_ODUk = dapi_ODUk;
  }

  public void setFramesOTN(BigInteger framesOTN) {
    this.framesOTN = framesOTN;
  }

  public BigInteger getFramesOTN() {
    return this.framesOTN;
  }

  public void setSapi(String sapi_ODUk) {
    this.sapi_ODUk = sapi_ODUk;
  }

  public void setStat(int stat_ODUk) {
    this.stat_ODUk = stat_ODUk;
  }

  public void setTim(boolean tim_ODUk) {
    this.tim_ODUk = tim_ODUk;
  }

  public void setSapiRefTx(String sapi) {
    this.sapiRefTx_ODUk = sapi;
  }

  public void setDapiRefTx(String dapi) {
    this.dapiRefTx_ODUk = dapi;
  }

  public void setSapiRefRx(String sapi) {
    this.sapiRefRx_ODUk = sapi;
  }

  public void setDapiRefRx(String dapi) {
    this.dapiRefRx_ODUk = dapi;
  }

  public String getOpSpRefRx() {
    return this.opSpRefRx_ODUk;
  }

  public void setOpSpRefRx(String opSpRefRx_ODUk) {
    this.opSpRefRx_ODUk = opSpRefRx_ODUk;
  }

  public String getOpSpRefTx() {
    return this.opSpRefTx_ODUk;
  }

  public void setOpSpRefTx(String opSpRefTx_ODUk) {
    this.opSpRefTx_ODUk = opSpRefTx_ODUk;
  }

  public String getDapiRefRx() {
    if (this.dapiRefRx_ODUk != null) {
      return this.dapiRefRx_ODUk;
    }
    return "";
  }

  public String getDapiRefTx() {
    if (this.dapiRefTx_ODUk != null) {
      return this.dapiRefTx_ODUk;
    }
    return "";
  }

  public String getSapiRefRx() {
    if (this.sapiRefRx_ODUk != null) {
      return this.sapiRefRx_ODUk;
    }
    return "";
  }

  public String getSapiRefTx() {
    if (this.sapiRefTx_ODUk != null) {
      return this.sapiRefTx_ODUk;
    }
    return "";
  }
}