package br.com.padtec.v3.data.ne;


public abstract interface TrpOTNTerminal extends TransponderOTN {
  public abstract OpticalInterface getOpticalWDMInterface();

  public abstract OpticalInterface getOpticalClientInterface();

  public abstract OTNInterface getOTN_WDMInterface();

  public abstract ClientInterface getClientInterface();

  public abstract ODUk getODUk();

  public abstract OTUk getOTUk();

  public abstract FEC getFEC();

  public abstract byte getPT();

  public abstract ODP getODP();
}