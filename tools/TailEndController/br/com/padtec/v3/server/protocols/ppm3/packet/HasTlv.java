package br.com.padtec.v3.server.protocols.ppm3.packet;

public abstract interface HasTlv<T extends TLV> {
  public abstract void addTLV(T paramT);

  public abstract T getTLV(int paramInt);

  public abstract int getTLVCount();
}