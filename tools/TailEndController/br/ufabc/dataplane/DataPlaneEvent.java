package br.ufabc.dataplane;

public enum DataPlaneEvent {

	NORMAL(0, "NORMAL: Signal Normal"),
	
	LOS(1, "LOS: Loss of signal"), 
	SIGNAL_FAIL(2, "SIGNAL_FAIL: Signal fail"), 
	LASER_OFF(3, "LASER_OFF: Laser wdm off"), 
	LOF(4, "LOF: Loss of frame"), 
	BIP8_ERROR_RATE(5, "BIP8_ERROR_RATE: Bip-8 rate above of limite"), 
	BEI_REMOTO (7, "Alarm BEI: errors of BIP-8 on remote host") ,
	POWER_IN_BELOW_LIMITES (8, "Power below of supported limite"),
	POWER_IN_ABOVE_LIMITES (9, "Power above of supported limite"),
	DISCONNECT(6, "DESCONECTAR");

	int code;
	String description;
	DataPlaneEvent(int code, String description){
		this.code = code;
		this.description = description;

	}
	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(int code) {
		this.code = code;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append(getDescription());
		buffer.append(" (");
		buffer.append(getCode());
		buffer.append(" )");
		return buffer.toString();
	}

}
