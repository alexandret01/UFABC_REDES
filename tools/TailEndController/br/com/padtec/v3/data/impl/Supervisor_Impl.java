package br.com.padtec.v3.data.impl;

import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.ne.Supervisor;

public abstract class Supervisor_Impl  extends NE_Impl implements Supervisor {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7898397385679038359L;
	private boolean lct;

	public Supervisor_Impl(SerialNumber serial) {
		super(serial);
	}

	public int getAddress()
	{
		return getSupAddress();
	}

	public void setAddress(int addr)
	{
		setSupAddress(addr);
	}

	public boolean isLct()
	{
		return this.lct;
	}

	public void setLct(boolean lct)
	{
		this.lct = lct;
	}
}
