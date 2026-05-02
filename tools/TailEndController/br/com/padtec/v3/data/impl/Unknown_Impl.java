package br.com.padtec.v3.data.impl;



import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.ne.Unknown;


public class Unknown_Impl extends NE_Impl implements Unknown
{
  private static final long serialVersionUID = 2L;

  public Unknown_Impl(SerialNumber serial)
  {
    super(serial);
  }
}