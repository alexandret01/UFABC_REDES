package br.com.padtec.v3.server.protocols.util.io;

import br.com.padtec.v3.data.impl.NE_Impl;
import br.com.padtec.v3.util.Functions;

public abstract class AbstractConnectionAlarmManager {
	protected Boolean connectionUp;
	protected NE_Impl ne;
	protected String connectionName;
	protected String lastErrorMessage;

	public boolean setConnectionUp() {
		if ((this.connectionUp != null) && (this.connectionUp.booleanValue())) {
			return false;
		}
		this.connectionUp = Boolean.valueOf(true);
		this.lastErrorMessage = null;
		if (this.ne == null)
			sendAlarm(null, this.connectionName, 53, true, null);
		else {
			reSyncAlarm();
		}
		return true;
	}

	public boolean setConnectionDown(Exception e)
	{
		if ((this.connectionUp != null) && (!(this.connectionUp.booleanValue()))) {
			return false;
		}
		this.connectionUp = Boolean.valueOf(false);
		String msg = e.getMessage();
		if (msg == null) {
			msg = e.toString();
		}
		this.lastErrorMessage = msg;
		if (this.ne == null)
			sendAlarm(null, this.connectionName, 52, true, 	msg);
		else {
			reSyncAlarm();
		}
		return true;
	}

	public void reSyncAlarm() {
		if ((this.ne != null) && (this.connectionUp != null))
			sendAlarm(this.ne, null, 51, this.connectionUp.booleanValue() ? false : true, this.lastErrorMessage);
	}

	public abstract void sendAlarm(NE_Impl paramNE_Impl, String paramString1, int paramInt, boolean paramBoolean, String paramString2);

	public void setConnectionName(String connectionName){
		this.connectionName = connectionName;
	}

	public void setNe(NE_Impl newNe){
		if (Functions.equals((this.ne == null) ? null : this.ne.getSerial(), 
				(newNe == null) ? null : newNe.getSerial()))	{
			return;
		}
		if (this.ne != null){
			sendAlarm(this.ne, null, 51, false, this.lastErrorMessage);
		}
		this.ne = newNe;
		reSyncAlarm();
	}

	public NE_Impl getNe() {
		return this.ne;
	}
}