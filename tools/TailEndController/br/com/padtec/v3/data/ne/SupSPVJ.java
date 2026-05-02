package br.com.padtec.v3.data.ne;

import java.util.List;

import br.com.padtec.v3.data.RackAddress.RackAddress;




public abstract interface SupSPVJ extends Supervisor, Sloted {
  public static final int BUFFER_SIZE = 150;

  public abstract byte[] getSupConf();

  public abstract boolean isBlocked();

  public abstract boolean isLOS1();

  public abstract boolean isLOS2();

  public abstract String getIP();

  public abstract String getGateway();

  public abstract String getMask();

  public abstract Integer getMaxsites();

  public abstract Integer getMaxTrp();

  public abstract Integer getMaxAmp();

  public abstract Integer getMaxCho();

  public abstract Integer getMaxMco();

  public abstract Integer getMaxShk();

  public abstract Boolean isTokenTimeOut();

  public abstract Boolean isMasterSlave();

  public abstract Boolean isAgc();

  public abstract Boolean isOTN();

  public abstract Integer getMaxFan();

  public abstract Integer getMaxPst();

  public abstract Boolean isAmplifierAls();

  public abstract Integer getMaxMux();

  public abstract String getNEName();

  public abstract List<RackAddress> getRackAddress();

  public abstract boolean isRackSync();

  public abstract int getCanError();

  public abstract int getCanTotal();

  public abstract int getCrcError();

  public abstract int getPPM2v2Total();

  public abstract int getRoBuffer();

  public abstract int getTintError();

  public abstract int getTintTotal();

  public abstract int getTokenTimeouts();

  public abstract int getNENameLimit();
}