package br.com.padtec.v3.data.ne;

import java.util.Date;

public abstract interface CounterReset {
  public abstract Date lastReset();
}