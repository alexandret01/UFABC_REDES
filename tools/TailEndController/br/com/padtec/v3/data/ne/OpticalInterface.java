package br.com.padtec.v3.data.ne;

import br.com.padtec.v3.data.Temperature;

public abstract interface OpticalInterface {
	public abstract String getChannel();

	public abstract boolean isDense();

	public abstract double getLambdaNominal();

	public abstract double getLambdaReal();

	public abstract double getPin();

	public abstract double getPout();

	public abstract boolean isFail();

	public abstract boolean isLos();

	public abstract boolean isLaserOff();

	public abstract boolean isAutoLaserOff();

	public abstract Temperature getLaserTemperature();

	public abstract double getModuleTemperature();

	public abstract boolean isLaserShutdown();
}