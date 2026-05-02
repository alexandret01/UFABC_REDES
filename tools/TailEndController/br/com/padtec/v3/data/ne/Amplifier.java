package br.com.padtec.v3.data.ne;

public abstract interface Amplifier extends NE, Sloted, LaserOff {
	
  public abstract boolean isFail();

  public abstract double getPout();

  public abstract boolean isAGC();

  public abstract boolean getEyeProtection();

  public abstract boolean getAGCStatus();

  public abstract boolean getLaserFail();

  public abstract boolean getCurrentAlarm();

  public abstract boolean getTemperatureAlarm();

  public abstract double getAGCGain();

  public abstract boolean getMCS();

  public abstract boolean isUsedBackupLaser();

  public abstract boolean isFailSPVJ();

  public abstract boolean hasIntermediateStage();
}