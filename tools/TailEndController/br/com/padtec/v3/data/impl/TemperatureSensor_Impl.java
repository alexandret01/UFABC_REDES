package br.com.padtec.v3.data.impl;


import java.io.Serializable;

import br.com.padtec.v3.data.ne.TemperatureSensor;

public class TemperatureSensor_Impl implements TemperatureSensor, Serializable {
  private static final long serialVersionUID = 3L;
  private long temperatureThreshold;
  private long temperature;
  private boolean overHeat;

  public TemperatureSensor_Impl()
  {
    this.overHeat = false;
  }

  public long getTemperatureThreshold()
  {
    return this.temperatureThreshold;
  }

  public void setTemperatureThreshold(long temperatureThreshold) {
    this.temperatureThreshold = temperatureThreshold;
  }

  public long getTemperature() {
    return this.temperature;
  }

  public void setTemperature(long temperature) {
    this.temperature = temperature;
  }

  public boolean isOverHeat() {
    return this.overHeat;
  }

  public void setOverHeat(boolean overHeat) {
    this.overHeat = overHeat;
  }
}