package br.ufabc.equipment;

import java.util.Vector;
import java.util.logging.Logger;

import br.com.padtec.v3.data.Command;
import br.com.padtec.v3.data.impl.PBAmp_Impl;
import br.com.padtec.v3.data.ne.Amplifier;
import br.com.padtec.v3.data.ne.NE;
import br.com.padtec.v3.util.log.Log;

public class Amplifiers {
	/** The associated supervisor. */
	protected Supervisor supervisor;
	/** The amplifiers. */
	PBAmp_Impl amplifier;
	/** Logger. */
	private static Logger log = Log.getInstance();

	/**
	 * Creates a new Amplifiers object.
	 * 
	 * @param amp
	 *            An Amplifier object.
	 */
	public Amplifiers(Supervisor sup, Amplifier amp) {
		this.supervisor = sup;
		this.amplifier = (PBAmp_Impl) amp;
	}

	/**
	 * Returns the total power input at the amplifier.
	 * 
	 * @return The total power input at the amplifier.
	 */
	public double getPowerInput() {
		return amplifier.getPin();
	}

	/**
	 * Returns the total power output at the amplifier.
	 * 
	 * @return The total power output at the amplifier.
	 */
	public double getPowerOutput() {
		return amplifier.getPout();
	}

	/**
	 * Returns the gain (in dB) of the amplifier.
	 * 
	 * @return The gain (in dB) of the amplifier.
	 */
	public double getGain() {
		return amplifier.getAGCGain();
	}

	/**
	 * Sets a new gain value for this amplifier.
	 * 
	 * @param gain
	 *            The new gain value (in dB).
	 */
	public void setGain(double gain) {
		log.info("Setting gain of " + amplifier.getName() + " to " + gain + " dB.");
		Command cmd = AmplifierCommands.getCommandSetGain(supervisor, amplifier.getSerial(), gain);
		supervisor.doCommand(cmd, amplifier.getSupAddress());
	}

	/**
	 * Returns true, if it is in AGC (Automatic Gain Control) mode. False,
	 * otherwise.
	 * 
	 * @return True, if it is in AGC (Automatic Gain Control) mode. False,
	 *         otherwise.
	 */
	public boolean isAGC() {
		return amplifier.isAGC();
	}

	/**
	 * Returns true, if it is in a LOS condition. False, otherwise.
	 * 
	 * @return True, if it is in a LOS condition. False, otherwise.
	 */
	public boolean isLOS() {
		return amplifier.isLos();
	}

	/**
	 * Sets the Automatic Gain Control (AGC) status.
	 * 
	 * @param on
	 *            True, if enabled. False, otherwise.
	 */
	public void setAGC(boolean on) {
		amplifier.setAGCStatus(on);
	}

	/**
	 * Returns a String representation of the object.
	 */
	public String toString() {
		return amplifier.toString();
	}

	/**
	 * Returns all amplifiers of the specified supervisor.
	 * 
	 * @param server
	 *            The specified supervisor.
	 * @return All amplifiers of the specified supervisor.
	 */
	public static Vector<Amplifier> getAmplifiers(Supervisor server) {
		Vector<Amplifier> amplifiers = new Vector<Amplifier>();
		for (NE ne : server.getAllNeIntoColector()) {
			if (ne instanceof Amplifier) {
				amplifiers.add((Amplifier)ne);
			}
		}
		return amplifiers;
	}
}
