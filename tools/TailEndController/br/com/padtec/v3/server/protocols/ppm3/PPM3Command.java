package br.com.padtec.v3.server.protocols.ppm3;

public enum PPM3Command  {
	REGEN(0);

	private int code;

	private PPM3Command(int code) {
		this.code = code;
	}
	public int code() {
		return this.code;
	}
}