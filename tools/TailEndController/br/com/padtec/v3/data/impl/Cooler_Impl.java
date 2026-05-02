package br.com.padtec.v3.data.impl;

import java.io.Serializable;

import br.com.padtec.v3.data.ne.Cooler;


public class Cooler_Impl implements Cooler, Serializable
{
  private static final long serialVersionUID = 3L;
  private long speedPercentage;
  private long maxSpeedPercentage;
  private long minSpeedPercentage;
  private boolean fail;

  public Cooler_Impl()
  {
    this.fail = false;
    this.speedPercentage = 100L;
  }

  public long getSpeedPercentage() {
    return this.speedPercentage;
  }

  public void setSpeedPercentage(long percentage) {
    this.speedPercentage = percentage;
  }

  public long getMaxSpeedPercentage() {
    return this.maxSpeedPercentage;
  }

  public void setMaxSpeedPercentage(long maxPercentage) {
    this.maxSpeedPercentage = maxPercentage;
  }

  public long getMinSpeedPercentage() {
    return this.minSpeedPercentage;
  }

  public void setMinSpeedPercentage(long minPercentage) {
    this.minSpeedPercentage = minPercentage;
  }

  public boolean isFail()
  {
    return this.fail;
  }

  public void setFail(boolean fail)
  {
    this.fail = fail;
  }
}