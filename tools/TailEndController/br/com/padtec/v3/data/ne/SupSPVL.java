package br.com.padtec.v3.data.ne;

public abstract interface SupSPVL extends NE {
  public abstract Integer getMaxMux();

  public abstract Integer getMaxRoadm();

  public abstract Integer getMaxMuxDemux();

  public abstract Integer getMaxMediaConverter();

  public abstract Integer getSegmentation();

  public abstract boolean isControleExclusivo();

  public abstract boolean isControleExclusivoSupported();

  public abstract boolean isControleExclusivoUpdated();

  public abstract Boolean isControleExclusivoObrigatorio();

  public abstract String getLockedLogin();

  public abstract Long getClockDelta();

  public abstract boolean isBoardDiscoveryEnabled();
}