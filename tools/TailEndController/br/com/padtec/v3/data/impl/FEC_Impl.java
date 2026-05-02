package br.com.padtec.v3.data.impl;

import java.io.Serializable;
import java.math.BigInteger;

import br.com.padtec.v3.data.ne.FEC;
import br.com.padtec.v3.util.math.MathUtils;




public class FEC_Impl extends Number implements FEC, Serializable, Cloneable {
  private static final long serialVersionUID = 3L;
  private static final Integer BPF_ReedSolomon = Integer.valueOf(130560);

  private static final Integer BPF_UltraFEC = Integer.valueOf(152064);

  private static final Double UltraFEC_C1 = Double.valueOf(0.0055D);

  private static final Double UltraFEC_C2 = Double.valueOf(0.004D);

  private static final Integer UltraFEC_Factor = Integer.valueOf(884);

  BigInteger framesOTN = BigInteger.ZERO;

  BigInteger fixedBits = BigInteger.ZERO;
  BigInteger erroredBlocks = BigInteger.ZERO;
  boolean fec_rx_stats_enabled;
  boolean fec_rx_correction_enabled;
  boolean fec_tx_correction_enabled;
  FEC_Type fecType;

  public FEC_Impl(FEC_Type fecType)
  {
    this.fecType = fecType;
  }

  public BigInteger getFixedBits() {
    return this.fixedBits;
  }

  public BigInteger getErroredBlocks() {
    return this.erroredBlocks;
  }

  public boolean isFecTxCorrEnabled() {
    return this.fec_tx_correction_enabled;
  }

  public boolean isFecRxStatsEnabled() {
    return this.fec_rx_stats_enabled;
  }

  public boolean isFecRxCorrEnabled() {
    return this.fec_rx_correction_enabled;
  }

  public void setErroredBlocks(BigInteger erroredBlocks) {
    this.erroredBlocks = erroredBlocks;
  }

  public void setFecRxCorrEnabled(boolean fec_rx_correction_enabled) {
    this.fec_rx_correction_enabled = fec_rx_correction_enabled;
  }

  public void setFecRxStatsEnabled(boolean fec_rx_stats_enabled) {
    this.fec_rx_stats_enabled = fec_rx_stats_enabled;
  }

  public void setFecTxCorrEnabled(boolean fec_tx_correction_enabled) {
    this.fec_tx_correction_enabled = fec_tx_correction_enabled;
  }

  public void setFixedBits(BigInteger fixedBits) {
    this.fixedBits = fixedBits;
  }

  public Double getFixedBitsRate()
  {
    Number result;
    if (this.framesOTN == null) {
      return Double.valueOf(0.0D);
    }
    if ((this.framesOTN.compareTo(BigInteger.ZERO) == 0) || 
      (this.fixedBits.compareTo(BigInteger.ZERO) == 0))
      return Double.valueOf(0.0D);
    if ((this.framesOTN.compareTo(BigInteger.valueOf(-1L)) == 0) || 
      (this.fixedBits.compareTo(BigInteger.valueOf(-1L)) == 0)) {
      return Double.valueOf(-1.0D);
    }

    switch (fecType.getCode())
    {
    case 2:
      Number transmittedBits = MathUtils.multiply(this.framesOTN, 
        Double.valueOf(BPF_UltraFEC.doubleValue()));
      Number u = MathUtils.divide(MathUtils.multiply(this.erroredBlocks, 
        UltraFEC_Factor), transmittedBits);

      Number linearTerm = MathUtils.multiply(u, UltraFEC_C1);
      Number quadraticTerm = MathUtils.multiply(MathUtils.multiply(u, u), 
        UltraFEC_C2);

      result = MathUtils.add(MathUtils.add(this.fixedBits, linearTerm), 
        quadraticTerm);
      break;
    case 1:
    default:
      Number value = MathUtils.multiply(this.framesOTN, 
        Double.valueOf(BPF_ReedSolomon.doubleValue()));
      result = MathUtils.divide(this.fixedBits, value);
    }

    return Double.valueOf(result.doubleValue());
  }

  public void setFecType(FEC.FEC_Type fecType) {
    this.fecType = fecType;
  }

  public FEC.FEC_Type getFecType() {
    return this.fecType;
  }

  public String getFECName() {
    switch (this.fecType.getCode())
    {
    case 1:
      return "Reed-Solomon";
    case 2:
      return "Enhanced FEC";
    }
    return "Invalid FEC Type";
  }

  public void setFramesOTN(BigInteger framesOTN)
  {
    this.framesOTN = framesOTN;
  }

  public BigInteger getFramesOTN() {
    return this.framesOTN;
  }

  public double doubleValue()
  {
    return getFixedBitsRate().doubleValue();
  }

  public float floatValue()
  {
    return getFixedBitsRate().floatValue();
  }

  public int intValue()
  {
    return getFixedBitsRate().intValue();
  }

  public long longValue()
  {
    return getFixedBitsRate().longValue();
  }

  public Object clone()
  {
    FEC_Impl copia = new FEC_Impl(this.fecType);
    copia.setFixedBits(getFixedBits());
    copia.setErroredBlocks(getErroredBlocks());
    copia.setFramesOTN(getFramesOTN());
    return copia;
  }

  public String toString()
  {
    StringBuilder result = new StringBuilder("[FEC_Impl] " + this.fecType);
    result.append(" FramesOTN: " + getFramesOTN());
    result.append(" FixedBits: " + getFixedBits());
    result.append(" ErroredBlocks: " + getErroredBlocks());
    result.append(" BitsRate: " + getFixedBitsRate());
    return result.toString();
  }
}