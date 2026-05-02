package br.com.padtec.v3.data.ne;

import br.com.padtec.v3.data.SerialNumber;


public abstract interface NE {
	
  public abstract String getName();

  public abstract int getSlot();

  public abstract String getDescription();

  public abstract SerialNumber getSerial();

  public abstract String getModel();

  public abstract String getVersion();

  public abstract long getUpdate();

  public abstract boolean isUp();

  public abstract boolean isAlarmsDisabled();

  public abstract int getSupAddress();

  public abstract boolean isFullSync();

  public abstract String getHardwareVersion();
}