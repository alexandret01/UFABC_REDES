package br.com.padtec.v3.data.ne;

public abstract interface TransponderOTN extends Transponder, CounterReset {
  public abstract int getK();

  public abstract boolean isEncAIS();
}