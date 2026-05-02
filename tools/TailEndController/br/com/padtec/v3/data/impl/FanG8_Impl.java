package br.com.padtec.v3.data.impl;
import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.ne.Cooler;
import br.com.padtec.v3.data.ne.FanG8;
import br.com.padtec.v3.data.ne.Sloted;
import br.com.padtec.v3.data.ne.TemperatureSensor;

public class FanG8_Impl extends NE_Impl implements FanG8, Sloted
{
  private static final long serialVersionUID = 3L;
  public static final int TOTAL_COOLERS = 8;
  public static final int TOTAL_TEMP_SENSORS = 2;
  public static final String HW_VERSION_WITHOUT_TEMPERATURE = "1.1A";
  private Cooler_Impl[] coolers;
  private TemperatureSensor_Impl[] temperatureSensors;
  private boolean off;

  public FanG8_Impl(SerialNumber serial)
  {
    super(serial);
    this.temperatureSensors = new TemperatureSensor_Impl[TOTAL_TEMP_SENSORS];
    this.coolers = new Cooler_Impl[TOTAL_COOLERS];
    for (int i = 0; i < TOTAL_TEMP_SENSORS; ++i) {
      this.temperatureSensors[i] = new TemperatureSensor_Impl();
    }
    for (int i = 0; i < TOTAL_COOLERS; ++i) {
      this.coolers[i] = new Cooler_Impl();
    }
    this.off = false;
  }

  public Cooler[] getCoolers() {
    return this.coolers;
  }

  public Cooler getCooler(int id) {
    return this.coolers[id];
  }

  public Cooler_Impl getCooler_Impl(int id)
  {
    return this.coolers[id];
  }

  public TemperatureSensor[] getTemperatureSensors() {
    return this.temperatureSensors;
  }

  public TemperatureSensor getTemperatureSensor(int id) {
    return this.temperatureSensors[id];
  }

  public TemperatureSensor_Impl getTemperatureSensor_Impl(int id)
  {
    return this.temperatureSensors[id];
  }

  public int getSubBastidor() {
    return 1;
  }

  public int getSubBastidorSlot() {
    return 1;
  }

  public boolean haveTemperatureSensors()
  {
    return (getHardwareVersion().compareToIgnoreCase(HW_VERSION_WITHOUT_TEMPERATURE) == 0);
  }

  public boolean isOff()
  {
    return this.off;
  }

  public void setOff(boolean off) {
    this.off = off;
  }
}