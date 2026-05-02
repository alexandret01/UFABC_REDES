package br.com.padtec.v3.data.ne;

import br.com.padtec.v3.data.Temperature;


public abstract interface Transponder extends NE, Sloted {
  public abstract boolean isFail();

  public abstract boolean isLos();

  public abstract double getPin();

  public abstract double getPout();

  public abstract boolean isN3db();

  public abstract String getChannel();

  public abstract double getNominalLambda();

  public abstract int getStyle();

  public abstract Temperature getLaserTemperature();
}