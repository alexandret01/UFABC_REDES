package br.com.padtec.v3.data.ne;

public abstract interface Supervisor extends NE  {
	
  public abstract int getAddress();

  public abstract boolean isLct();
}