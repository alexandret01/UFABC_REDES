package br.com.padtec.v3.util.math;

public class Ratio
{
	private Average average = new Average();

	public void x()
	{
		this.average.add(1.0D);
	}

	public void y()
	{
		this.average.add(0.0D);
	}

	public double getRatio()
	{
		return this.average.getAverage();
	}

	public void reset()
	{
		this.average.reset();
	}

	public String toString()
	{
		return Double.toString(getRatio());
	}
}