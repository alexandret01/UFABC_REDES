package br.com.padtec.v3.data.ne;

public abstract interface Fan extends NE, Sloted {
	public abstract double getTemperature();

	public abstract double getMaxTemperature();

	public abstract boolean isOverHeat();

	public abstract boolean isVelocityControl();

	public abstract boolean isFanOk(int paramInt);

	public abstract int getVelocityPercentual();
}