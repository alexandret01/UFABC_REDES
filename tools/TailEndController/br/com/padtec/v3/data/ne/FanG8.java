package br.com.padtec.v3.data.ne;

public abstract interface FanG8 extends NE, Sloted, Ne16Bit {
	
  public abstract Cooler[] getCoolers();

  public abstract TemperatureSensor[] getTemperatureSensors();

  public abstract Cooler getCooler(int paramInt);

  public abstract TemperatureSensor getTemperatureSensor(int paramInt);

  public abstract boolean haveTemperatureSensors();

  public abstract boolean isOff();
}