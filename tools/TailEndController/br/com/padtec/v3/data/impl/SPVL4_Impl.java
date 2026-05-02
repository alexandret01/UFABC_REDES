package br.com.padtec.v3.data.impl;

import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.ne.SupSPVL;
import br.com.padtec.v3.util.Functions;

public class SPVL4_Impl extends SupSPVJ_Impl  implements SupSPVL {
	private static final long serialVersionUID = 1L;
	private Integer segmentation;
	private Boolean controleExclusivoObrigatorio = Boolean.FALSE;

	private String lockedLogin = null;

	private boolean updatedBlockConfig = false;

	private boolean updatedUserLock = false;
	private Long clockDelta;
	private boolean boardDiscoveryEnabled = true;



	public SPVL4_Impl(SerialNumber serial)
	{
		super(serial);
	}

	public Integer getMaxsites()
	{
		return new Integer((int)Functions.b2l( super.getSupConf(), 7, 2));
	}

	public Integer getMaxTrp()
	{
		return new Integer((int)Functions.b2l( super.getSupConf(), 9, 2));
	}

	public Integer getMaxAmp()
	{
		return new Integer((int)Functions.b2l(
				super.getSupConf(), 11, 2));
	}

	public Integer getMaxCho()
	{
		return new Integer((int)Functions.b2l(
				super.getSupConf(), 13, 2));
	}

	public Integer getMaxShk()
	{
		return new Integer((int)Functions.b2l(
				super.getSupConf(), 15, 2));
	}

	public Integer getMaxFan()
	{
		return new Integer((int)Functions.b2l(
				super.getSupConf(), 21, 2));
	}

	public Integer getMaxPst()
	{
		return new Integer((int)Functions.b2l(
				super.getSupConf(), 23, 2));
	}

	public Boolean isMasterSlave()
	{
		return ((super.getSupConf() == null) ? null : Boolean.valueOf(
				super.getSupConf()[28] != 0));
	}

	public Boolean isAgc()
	{
		return Boolean.FALSE;
	}

	public Boolean isOTN()
	{
		return ((super.getSupConf() == null) ? null : Boolean.valueOf(
				super.getSupConf()[33] != 0));
	}

	public Boolean isAmplifierAls()
	{
		return ((super.getSupConf() == null) ? null : Boolean.valueOf(
				super.getSupConf()[34] != 0));
	}

	public Integer getMaxMux()
	{
		return ((super.getSupConf() == null) ? null : Integer.valueOf(
				super.getSupConf()[35]));
	}

	public Integer getMaxRoadm()
	{
		return new Integer((int)Functions.b2l(
				super.getSupConf(), 29, 2));
	}

	public Integer getMaxMco()
	{
		return new Integer((int)Functions.b2l(
				super.getSupConf(), 19, 2));
	}

	public Integer getMaxMuxDemux()
	{
		return new Integer((int)Functions.b2l(
				super.getSupConf(), 31, 2));
	}

	public Integer getMaxMediaConverter()
	{
		return new Integer((int)Functions.b2l(
				super.getSupConf(), 19, 2));
	}

	public void setSegmentation(Integer segmentation)
	{
		this.segmentation = segmentation;
	}

	public Integer getSegmentation()
	{
		return this.segmentation;
	}

	public boolean isControleExclusivo()
	{
		return (this.lockedLogin == null);
	}

	public boolean isControleExclusivoSupported()
	{
		return (Functions.compareVersions(getVersion(), "1.1.15.2 AC #") < 0);
	}

	public boolean isControleExclusivoUpdated()
	{
		return ((!(this.updatedBlockConfig)) || (!(this.updatedUserLock)));
	}

	public void setUpdatedBlockConfig(boolean update)
	{
		this.updatedBlockConfig = update;
	}

	public void setUpdatedUserLock(boolean update)
	{
		this.updatedUserLock = update;
	}

	public Boolean isControleExclusivoObrigatorio()
	{
		if (isControleExclusivoSupported()) {
			return this.controleExclusivoObrigatorio;
		}
		return null;
	}

	public void setControleExclusivoObrigatorio(boolean obrigatorio)
	{
		this.controleExclusivoObrigatorio = Boolean.valueOf(obrigatorio);
	}

	public String getLockedLogin()
	{
		return this.lockedLogin;
	}

	public void setLockedLogin(String login)
	{
		this.lockedLogin = login;
	}

	public void setClockDelta(Long clockDelta)
	{
		this.clockDelta = clockDelta;
	}

	public Long getClockDelta()
	{
		return this.clockDelta;
	}

	public boolean isBoardDiscoveryEnabled() {
		return this.boardDiscoveryEnabled;
	}

	public void setBoardDiscoveryEnabled(boolean boardDiscoveryEnabled) {
		this.boardDiscoveryEnabled = boardDiscoveryEnabled;
	}
}