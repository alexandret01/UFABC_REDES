package br.com.padtec.v3.data.ne;


import java.math.BigInteger;

public abstract interface ODUk {

	public abstract BigInteger getBip8();

	public abstract Double getBIP8Rate();

	public abstract BigInteger getBei();

	public abstract Double getBEIRate();

	public abstract int getStat();

	public abstract String getStatDesc();

	public abstract boolean isBdi();

	public abstract String getSAPI();

	public abstract String getDAPI();

	public abstract boolean isTim();

	public abstract boolean isAis();

	public abstract String getDapiRefRx();

	public abstract String getDapiRefTx();

	public abstract String getSapiRefRx();

	public abstract String getSapiRefTx();
}