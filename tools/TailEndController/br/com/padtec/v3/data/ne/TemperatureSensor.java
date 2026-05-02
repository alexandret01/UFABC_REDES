package br.com.padtec.v3.data.ne;


public abstract interface TemperatureSensor {
  public abstract long getTemperatureThreshold();

  public abstract long getTemperature();

  public abstract boolean isOverHeat();
}