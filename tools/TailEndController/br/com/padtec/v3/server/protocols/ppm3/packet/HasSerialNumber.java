package br.com.padtec.v3.server.protocols.ppm3.packet;

import br.com.padtec.v3.data.SerialNumber;

public abstract interface HasSerialNumber {
  public abstract void setSerial(SerialNumber paramSerialNumber);

  public abstract SerialNumber getSerial();
}