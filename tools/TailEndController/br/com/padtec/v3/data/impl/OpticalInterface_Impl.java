package br.com.padtec.v3.data.impl;


import java.io.Serializable;

import br.com.padtec.v3.data.Temperature;
import br.com.padtec.v3.data.ne.OpticalInterface;

public class OpticalInterface_Impl implements OpticalInterface, Serializable
{
  private static final long serialVersionUID = 3L;
  private boolean los;
  private boolean laserOff;
  private boolean fail;
  private boolean laserShutdown;
  private boolean isDWDM;
  private boolean autoLaserOff;
  private double pin;
  private double pout;
  private double lambdaReal = -1.0D;
  private double lambdaNominal;
  private String channel;
  private final Temperature laserTemperature = new Temperature();

  private double moduleTemperature = (0.0D / 0.0D);

  public OpticalInterface_Impl(boolean isDense)
  {
    this.isDWDM = isDense;
  }

  public OpticalInterface_Impl() {
    this.isDWDM = false;
  }

  public String getChannel() {
    return this.channel;
  }

  public void setChannel(String channel) {
    this.channel = channel;
  }

  public boolean isFail() {
    return this.fail;
  }

  public void setFail(boolean fail) {
    this.fail = fail;
  }

  public void setLambdaNominal(double lambdaNominal) {
    this.lambdaNominal = lambdaNominal;
  }

  public void setLambdaReal(double lambdaReal) {
    this.lambdaReal = lambdaReal;
  }

  public boolean isLaserOff() {
    return this.laserOff;
  }

  public void setLaserOff(boolean laserOff) {
    this.laserOff = laserOff;
  }

  public boolean isLos() {
    return this.los;
  }

  public void setLos(boolean los) {
    this.los = los;
  }

  public double getPin() {
    return this.pin;
  }

  public void setPin(double pin) {
    this.pin = pin;
  }

  public double getPout() {
    return this.pout;
  }

  public Temperature getLaserTemperature() {
    return this.laserTemperature;
  }

  public void setPout(double pout) {
    this.pout = pout;
  }

  public boolean isDense() {
    return this.isDWDM;
  }

  public double getLambdaNominal() {
    return this.lambdaNominal;
  }

  public double getLambdaReal() {
    return this.lambdaReal;
  }

  public void setAutoLaserOff(boolean autoLaserOff) {
    this.autoLaserOff = autoLaserOff;
  }

  public boolean isAutoLaserOff() {
    return this.autoLaserOff;
  }

  public double getModuleTemperature()
  {
    return this.moduleTemperature;
  }

  public void setModuleTemperature(double moduleTemperature)
  {
    this.moduleTemperature = moduleTemperature;
  }

  public boolean isLaserShutdown() {
    return this.laserShutdown;
  }

  public void setLaserShutdown(boolean laserShutdown) {
    this.laserShutdown = laserShutdown;
  }
}