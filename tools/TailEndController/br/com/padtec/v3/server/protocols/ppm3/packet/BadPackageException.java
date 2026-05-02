package br.com.padtec.v3.server.protocols.ppm3.packet;

public class BadPackageException extends Exception {
	private static final long serialVersionUID = 1L;

	public BadPackageException(String message) {
		super(message);
	}

	public BadPackageException(Exception cause) {
		super(cause);
	}
}
