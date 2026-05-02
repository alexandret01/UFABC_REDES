package br.com.padtec.v3.data.ne;

import java.math.BigInteger;

public abstract interface FEC {
  public abstract BigInteger getFixedBits();

  public abstract Double getFixedBitsRate();

  public abstract String getFECName();

  public abstract BigInteger getErroredBlocks();

  public abstract boolean isFecTxCorrEnabled();

  public abstract boolean isFecRxStatsEnabled();

  public abstract boolean isFecRxCorrEnabled();

  public abstract FEC_Type getFecType();

  public static enum FEC_Type {
	  
    REED_SOLOMON(1), 
    ENHANCED_FEC(2);

    private final int code;

    private FEC_Type(int code) {
        this.code = code;
    }

    public int getCode()
    {
      return this.code;
    }

    public static FEC_Type getType(int code) {
      for (FEC_Type type : values()) {
        if (type.getCode() == code) {
          return type;
        }
      }
      return null;
    }
  }
}