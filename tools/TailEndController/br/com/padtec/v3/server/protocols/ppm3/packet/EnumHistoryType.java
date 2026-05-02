package br.com.padtec.v3.server.protocols.ppm3.packet;


public enum EnumHistoryType{
//	READ_ALARMS, 
//	READ_LAST_ALARMS, 
//	READ_COMMANDS, 
//	READ_TRAP_NEW_AND_DEL, 
//	READ_METRICS;

	READ_ALARMS(41, 0, 50),
	READ_LAST_ALARMS(48, 0, 57),
	READ_COMMANDS(49, 1, 51),
	READ_TRAP_NEW_AND_DEL(55, 1, 56),
	READ_METRICS(53, 2, 54);
	
	
	
	private byte requestCode1;
	private byte requestCode2;
	private byte responseCode;

	private EnumHistoryType (int rqCode1, int rqCode2, int rpCode){
		requestCode1 = (byte)rqCode1;
		requestCode2 = (byte)rqCode2;
		responseCode = (byte)rpCode;
	}
	public int getRequestCode1() {
		return this.requestCode1;
	}

	public int getRequestCode2() {
		return this.requestCode2;
	}

	public byte getResponseCode()
	{
		return this.responseCode;
	}

	public static EnumHistoryType getRequestType(int requestCode1) {
		for (EnumHistoryType type : values()) {
			if (type.getRequestCode1() == requestCode1) {
				return type;
			}
		}
		return null;
	}

	public static EnumHistoryType getResponseType(int responseCode) {
		for (EnumHistoryType type : values()) {
			if (type.getResponseCode() == responseCode) {
				return type;
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		for (EnumHistoryType type : EnumHistoryType.values()) {
//			if (type.getResponseCode() == responseCode) {
//				return type;
//			}
			System.out.println(type.name() + "( " + type.getRequestCode1() + ", " + type.getRequestCode2() + 
					", " + type.getResponseCode() + ")");
			
		}
	}
}