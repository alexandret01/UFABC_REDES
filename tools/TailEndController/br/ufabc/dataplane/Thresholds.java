package br.ufabc.dataplane;

public class Thresholds {
	
	private static double bytesPerFrame = 15240;
	private static double maxBitsPerFrame = 8*bytesPerFrame;
	
	/**
	 * Bip-8 Max Rate per Frame = 6.56167979e-5
	 * */
	public static double getBip8MaxRate(){
		// Bip-8 Max Rate per Frame = 6.56167979e-5
		return (double) 8 / maxBitsPerFrame; 
	}
	
	public static void main(String[] args){
		System.out.println(maxBitsPerFrame);
		System.out.println(getBip8MaxRate());
	}
}
