package br.com.padtec.v3.data.impl;

import java.io.Serializable;

import br.com.padtec.v3.data.ne.OTNInterface;



public class OTNInterface_Impl implements OTNInterface, Serializable
{
	private static final long serialVersionUID = 2L;
	boolean lom;
	boolean lof;
	boolean losSync;

	public boolean isLom() {
		return this.lom;
	}

	public boolean isLof() {
		return this.lof;
	}

	public boolean isLosSync() {
		return this.losSync;
	}

	public void setLof(boolean lof) {
		this.lof = lof;
	}

	public void setLom(boolean lom) {
		this.lom = lom;
	}

	public void setLosSync(boolean losSync) {
		this.losSync = losSync;
	}
}