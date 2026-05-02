package br.com.padtec.v3.data.ne;

public abstract interface TrpBiDWDMRate extends TrpBidirecional, Rateable, TrpDWDM {
  public abstract boolean isOverRate2();

  public abstract double getRate2();

  public abstract double getLimiar2();
}