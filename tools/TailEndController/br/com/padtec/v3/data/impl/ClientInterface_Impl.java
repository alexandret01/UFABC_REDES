package br.com.padtec.v3.data.impl;

import java.io.Serializable;

import br.com.padtec.v3.data.ne.ClientInterface;


public class ClientInterface_Impl  implements ClientInterface, Serializable {
  private static final long serialVersionUID = 2L;
  boolean lof;
  boolean losSync;

  public boolean isLof()
  {
    return this.lof;
  }

  public boolean isLosSync() {
    return this.losSync;
  }

  public void setLof(boolean lof) {
    this.lof = lof;
  }

  public void setLosSync(boolean losSync) {
    this.losSync = losSync;
  }
}