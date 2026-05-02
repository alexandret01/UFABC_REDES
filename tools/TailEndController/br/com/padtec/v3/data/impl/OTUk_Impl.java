package br.com.padtec.v3.data.impl;



import java.io.Serializable;
import java.math.BigInteger;

import br.com.padtec.v3.data.ne.OTUk;

public class OTUk_Impl implements OTUk, Serializable {
	private static final long serialVersionUID = 4L;
	BigInteger bip8_OTUk = BigInteger.ZERO;
	BigInteger bei_OTUk = BigInteger.ZERO;
	boolean bdi_OTUk;
	boolean tim_OTUk;
	boolean ais_OTUk;
	boolean iae_OTUk;
	String sapi_OTUk;
	String dapi_OTUk;
	private String sapiRefTx_OTUk;
	private String dapiRefTx_OTUk;
	private String sapiRefRx_OTUk;
	private String dapiRefRx_OTUk;
	private String opSpRefTx_OTUk;
	private String opSpRefRx_OTUk;
	BigInteger framesOTN = BigInteger.ZERO;
	private int k;
	private static final int OPU_COLS = 3810;
	private static final int BitsPerFrameOPU = 121920;

	public OTUk_Impl(int k)
	{
		this.k = k;
	}

	public String getDAPI() {
		return this.dapi_OTUk;
	}

	public String getSAPI() {
		return this.sapi_OTUk;
	}

	public boolean isBdi() {
		return this.bdi_OTUk;
	}

	public BigInteger getBei() {
		return this.bei_OTUk;
	}

	public BigInteger getBip8() {
		return this.bip8_OTUk;
	}

	public boolean isTim() {
		return this.tim_OTUk;
	}

	public boolean isAIS() {
		return this.ais_OTUk;
	}

	public Double getBIP8Rate() {
		if ((this.framesOTN.compareTo(BigInteger.valueOf(-1L)) == 0) || 
				(this.bip8_OTUk.compareTo(BigInteger.valueOf(-1L)) == 0))
			return Double.valueOf(-1.0D);
		if ((this.framesOTN.compareTo(BigInteger.ZERO) == 0) || 
				(this.bip8_OTUk.compareTo(BigInteger.ZERO) == 0)) {
			return Double.valueOf(0.0D);
		}

		double div = this.framesOTN.doubleValue() * 121920.0D;
		double ret = this.bip8_OTUk.doubleValue() / div;
		return Double.valueOf(ret);
	}

	public Double getBEIRate() {
		if ((this.framesOTN.compareTo(BigInteger.valueOf(-1L)) == 0) || 
				(this.bei_OTUk.compareTo(BigInteger.valueOf(-1L)) == 0))
			return Double.valueOf(-1.0D);
		if ((this.framesOTN.compareTo(BigInteger.ZERO) == 0) || 
				(this.bei_OTUk.compareTo(BigInteger.ZERO) == 0)) {
			return Double.valueOf(0.0D);
		}

		double div = this.framesOTN.doubleValue() * 121920.0D;
		double ret = this.bei_OTUk.doubleValue() / div;
		return Double.valueOf(ret);
	}

	public boolean isIAE() {
		return this.iae_OTUk;
	}

	public void setAis(boolean ais_OTUk) {
		this.ais_OTUk = ais_OTUk;
	}

	public void setBdi(boolean bdi_OTUk) {
		this.bdi_OTUk = bdi_OTUk;
	}

	public void setBei(BigInteger bei_OTUk) {
		this.bei_OTUk = bei_OTUk;
	}

	public void setBip8(BigInteger bip8_OTUk) {
		this.bip8_OTUk = bip8_OTUk;
	}

	public void setDapi(String dapi_OTUk) {
		this.dapi_OTUk = dapi_OTUk;
	}

	public void setFramesOTN(BigInteger framesOTN) {
		this.framesOTN = framesOTN;
	}

	public void setIae(boolean iae_OTUk) {
		this.iae_OTUk = iae_OTUk;
	}

	public void setSapi(String sapi_OTUk) {
		this.sapi_OTUk = sapi_OTUk;
	}

	public void setTim(boolean tim_OTUk) {
		this.tim_OTUk = tim_OTUk;
	}

	public BigInteger getFramesOTN() {
		return this.framesOTN;
	}

	public void setSapiRefTx(String sapi) {
		this.sapiRefTx_OTUk = sapi;
	}

	public String getSapiRefTx() {
		if (this.sapiRefTx_OTUk != null) {
			return this.sapiRefTx_OTUk;
		}
		return "";
	}

	public void setDapiRefTx(String dapi) {
		this.dapiRefTx_OTUk = dapi;
	}

	public String getDapiRefTx() {
		if (this.dapiRefTx_OTUk != null) {
			return this.dapiRefTx_OTUk;
		}
		return "";
	}

	public void setSapiRefRx(String sapi) {
		this.sapiRefRx_OTUk = sapi;
	}

	public String getSapiRefRx() {
		if (this.sapiRefRx_OTUk != null) {
			return this.sapiRefRx_OTUk;
		}
		return "";
	}

	public void setDapiRefRx(String dapi) {
		this.dapiRefRx_OTUk = dapi;
	}

	public String getDapiRefRx() {
		if (this.dapiRefRx_OTUk != null) {
			return this.dapiRefRx_OTUk;
		}
		return "";
	}

	public String getOpSpRefRx() {
		if (this.opSpRefRx_OTUk != null) {
			return this.opSpRefRx_OTUk;
		}
		return "";
	}

	public void setOpSpRefRx(String opSpRefRx_OTUk) {
		this.opSpRefRx_OTUk = opSpRefRx_OTUk;
	}

	public String getOpSpRefTx() {
		if (this.opSpRefTx_OTUk != null) {
			return this.opSpRefTx_OTUk;
		}
		return "";
	}

	public void setOpSpRefTx(String opSpRefTx_OTUk) {
		this.opSpRefTx_OTUk = opSpRefTx_OTUk;
	}
}