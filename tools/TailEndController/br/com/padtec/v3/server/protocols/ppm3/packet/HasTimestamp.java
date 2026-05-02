package br.com.padtec.v3.server.protocols.ppm3.packet;

public abstract interface HasTimestamp {
  public abstract void setTimestamp(long paramLong);

  public abstract long getTimestamp();
}