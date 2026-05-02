package br.com.padtec.v3.data;

import java.io.Serializable;

public class Data3<T1, T2, T3>
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public final T1 v1;
  public final T2 v2;
  public final T3 v3;

  public Data3(T1 v1, T2 v2, T3 v3)
  {
    this.v1 = v1;
    this.v2 = v2;
    this.v3 = v3;
  }

  public String toString()
  {
    return this.v1.toString();
  }
}