package br.com.padtec.v3.data.ne;

public abstract interface PBAmp extends Amplifier {
  public abstract double getPin();

  public abstract boolean isLos();
  
  public String toStringDetalhed();
}