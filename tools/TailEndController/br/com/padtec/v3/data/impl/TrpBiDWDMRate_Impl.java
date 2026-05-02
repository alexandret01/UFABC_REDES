package br.com.padtec.v3.data.impl;

import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.ne.TrpBiDWDMRate;

public class TrpBiDWDMRate_Impl extends TrpGBEthD_Impl implements TrpBiDWDMRate {
  private static final long serialVersionUID = 3L;
  private double rate;
  private double rate2;
  private double limiar;
  private double limiar2;
  private boolean overRate;
  private boolean overRate2;

  public TrpBiDWDMRate_Impl(SerialNumber serial)
  {
    super(serial);
  }

  public boolean isOverRate() {
    return this.overRate;
  }

  public double getRate() {
    return this.rate;
  }

  public double getLimiar() {
    return this.limiar;
  }

  public void setLimiar(double limiar) {
    this.limiar = limiar;
  }

  public void setOverRate(boolean overRate) {
    this.overRate = overRate;
  }

  public void setRate(double rate) {
    this.rate = rate;
  }

  public boolean isOverRate2()
  {
    return this.overRate2;
  }

  public void setOverRate2(boolean overRate2)
  {
    this.overRate2 = overRate2;
  }

  public double getLimiar2()
  {
    return this.limiar2;
  }

  public double getRate2()
  {
    return this.rate2;
  }

  public void setLimiar2(double limiar2)
  {
    this.limiar2 = limiar2;
  }

  public void setRate2(double rate2)
  {
    this.rate2 = rate2;
  }
}