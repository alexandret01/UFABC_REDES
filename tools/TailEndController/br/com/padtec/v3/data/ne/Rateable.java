package br.com.padtec.v3.data.ne;

public abstract interface Rateable {
  public abstract boolean isOverRate();

  public abstract double getRate();

  public abstract double getLimiar();
}