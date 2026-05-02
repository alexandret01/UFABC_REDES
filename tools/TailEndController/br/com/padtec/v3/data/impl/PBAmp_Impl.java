package br.com.padtec.v3.data.impl;

import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.ne.PBAmp;

public class PBAmp_Impl extends Amplifier_Impl  implements PBAmp {

	private static final long serialVersionUID = 2L;
	private double pin;
	private boolean los;

	public PBAmp_Impl(SerialNumber serial)	{
		super(serial);
	}

	public double getPin()	{
		if (this.pin == -100.0D) {
			return (0.0D / 0.0D);
		}

		return this.pin;
	}

	public void setPin(double pin)	{
		this.pin = pin;
	}

	public boolean isLos()	{
		return this.los;
	}

	public void setLos(boolean los)	{
		this.los = los;
	}
	
	public String toStringDetalhed(){
		  StringBuilder builder = new StringBuilder();
		  builder.append(this.toString());
		  builder.append(":\n");
		  builder.append("\tSlot: ");
		  builder.append(getSlot());
		  builder.append("\n\tPin: ");
		  builder.append(this.getPin());
		  builder.append("\n\tPout: ");
		  builder.append(this.getPout());
		  builder.append("\n\tGain: ");
		  builder.append(getAGCGain());
		  builder.append("\n\tMCS: ");
		  builder.append((getMCS()? Boolean.TRUE.toString() : Boolean.FALSE.toString()));
		 
		  
		  return builder.toString();
	}
}