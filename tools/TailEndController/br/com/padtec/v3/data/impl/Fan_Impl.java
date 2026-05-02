package br.com.padtec.v3.data.impl;

import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.ne.Fan;

public class Fan_Impl extends NE_Impl  implements Fan {
  private static final long serialVersionUID = 3L;
  private double temperature;
  private double maxTemperature;
  private boolean velocityControl;
  private boolean overHeat;
  private boolean fan1Ok = true;
  private boolean fan2Ok = true;
  private boolean fan3Ok = true;
  private int velocityPercentual;

  public Fan_Impl(SerialNumber serial)
  {
    super(serial);
  }

  public boolean isVelocityControl() {
    return this.velocityControl;
  }

  public void setVelocityControl(boolean velocityControl) {
    this.velocityControl = velocityControl;
  }

  public boolean isOverHeat() {
    return this.overHeat;
  }

  public void setOverHeat(boolean overHeat) {
    this.overHeat = overHeat;
  }

  public double getTemperature() {
    return this.temperature;
  }

  public void setTemperature(double temperature) {
    this.temperature = temperature;
  }

  public double getMaxTemperature() {
    return this.maxTemperature;
  }

  public void setMaxTemperature(double maxTemperature) {
    this.maxTemperature = maxTemperature;
  }

  public int getVelocityPercentual() {
    return this.velocityPercentual;
  }

  public void setVelocityPercentual(int velocityPercentual) {
    this.velocityPercentual = velocityPercentual;
  }

  public boolean isFanOk(int index) {
    if (index == 1)
      return this.fan1Ok;
    if (index == 2)
      return this.fan2Ok;
    if (index == 3) {
      return this.fan3Ok;
    }
    throw new UnsupportedOperationException("Fan " + index + " not supported");
  }

  public void setFanOk(int index, boolean value) {
    if (index == 1)
      this.fan1Ok = value;
    else if (index == 2)
      this.fan2Ok = value;
    else if (index == 3)
      this.fan3Ok = value;
    else
      throw new UnsupportedOperationException("Fan " + index + " not supported");
  }

  public int getSubBastidor() {
    return 1;
  }

  public int getSubBastidorSlot() {
    return 1;
  }
}