package br.com.padtec.v3.server.protocols.ppm3.packet;


public class InvalidValueException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public InvalidValueException(String message) {
		super(message);
	}

	public InvalidValueException(String message, Exception cause) {
		super(message, cause);
	}
}