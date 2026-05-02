package br.com.padtec.v3.data.ne;

import java.math.BigInteger;

public abstract interface OTUk {
	
  public abstract BigInteger getBip8();

  public abstract Double getBIP8Rate();

  public abstract BigInteger getBei();

  public abstract Double getBEIRate();

  public abstract boolean isBdi();

  public abstract String getSAPI();

  public abstract String getDAPI();

  public abstract boolean isTim();

  public abstract boolean isAIS();

  public abstract boolean isIAE();

  public abstract String getDapiRefRx();

  public abstract String getDapiRefTx();

  public abstract String getSapiRefRx();

  public abstract String getSapiRefTx();
  
}