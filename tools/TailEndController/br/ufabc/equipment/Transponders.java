package br.ufabc.equipment;

import java.util.Vector;

import br.com.padtec.v3.data.ne.NE;
import br.com.padtec.v3.data.ne.Transponder;

public class Transponders {
	/** The associated supervisor. */
	protected Supervisor supervisor;
	/** The transponder reference. */
	protected Transponder transponder;
	
	public Transponders(Supervisor sup, Transponder transp) {
		this.supervisor = sup;
		this.transponder = transp;
	}
	
	/**
	 * Returns the name of the transponder.
	 * @return The name of the transponder.
	 */
	public String getName() {
		return transponder.getName();
	}
	
	/**
	 * Returns the input power at the server interface.
	 * @return The input power at the server interface.
	 */
	public double getInputPower() {
		return transponder.getPin();
	}
	
	/**
	 * Returns the output power at the server interface.
	 * @return The output power at the server interface.
	 */
	public double getOutputPower() {
		return transponder.getPout();
	}
		
	/**
	 * Returns true if the server interface is in LOS state. False, otherwise.
	 * @return True if the server interface is in LOS state. False, otherwise.
	 */
	public boolean isLOS() {
		return transponder.isLos();
	}

	/**
	 * Returns the server interface channel.
	 * @return The server interface channel.
	 */
	public String getChannel() {
		return transponder.getChannel();
	}
	
	/**
	 * Returns the wavelength frequency of the server interface channel (in nm).
	 * @return The wavelength frequency of the server interface channel (in nm).
	 */
	public double getLambda() {
		return transponder.getNominalLambda();
	}
	
	/**
	 * Returns all transponders of the specified supervisor.
	 * @param server The specified supervisor.
	 * @return All transponders of the specified supervisor.
	 */
	public static Vector<Transponder> getTransponders(Supervisor server) {
		Vector<Transponder> transponders = new Vector<Transponder>(); 
		for (NE ne : server.getAllNeIntoColector()) {			
			if (ne instanceof Transponder) {
				transponders.add((Transponder) ne);
			}
		}
		return transponders;
	}
	
}
