package br.com.padtec.v3.server.protocols.ppm3.packet;


/**
 * Improves the types of payloads supported on a PPM3 Packet
 * 
 * */
public abstract interface PPM3Payload {
	
	public abstract Type getType();

	public abstract byte[] getBytes();

	public abstract int getSize();

	public abstract void set(byte[] paramArrayOfByte) throws BadPackageException;

	public static enum Type
	{ 

		TYPE_GET(1), 
		TYPE_SET(2), 
		TYPE_RESPONSE(3), 
		TYPE_TRAP(4), 
		TYPE_HISTORY_GET(6), 
		TYPE_HISTORY_RESPONSE(7), 
		TYPE_ERROR(8), 
		TYPE_ACK(9); 

		private byte code;

		private Type(int code){
			
			this.code = (byte)code;
			
		}
		public byte getCode(){
			
			return this.code;
			
		}

		public static Type getType(byte code) {
			
			for (Type t : values()) {
				if (t.code == code) {
					return t;
				}
			}
			
			return null;
			
		}
	}
}