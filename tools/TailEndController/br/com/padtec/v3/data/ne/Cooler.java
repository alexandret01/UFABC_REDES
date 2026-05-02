package br.com.padtec.v3.data.ne;

public abstract interface Cooler {
  public abstract long getSpeedPercentage();

  public abstract long getMaxSpeedPercentage();

  public abstract long getMinSpeedPercentage();

  public abstract boolean isFail();
}