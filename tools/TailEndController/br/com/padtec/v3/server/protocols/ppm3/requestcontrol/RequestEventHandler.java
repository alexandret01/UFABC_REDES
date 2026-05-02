package br.com.padtec.v3.server.protocols.ppm3.requestcontrol;

public abstract interface RequestEventHandler<T>{
  public abstract void onRequestFail();

  public abstract boolean onReceiveResponse(String paramString, T paramT);
}