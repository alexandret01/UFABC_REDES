package br.com.padtec.v3.data.impl;

import java.io.Serializable;

import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.ne.ODP;



public class ODP_Impl implements ODP, Serializable
{
  private static final long serialVersionUID = 3655187008379442160L;
  public Boolean odpEnabled;
  public Boolean waitToRestoreEnabled;
  public Boolean signalDegradeEnabled;
  public Boolean workingPath;
  public Boolean configError;
  public Boolean cableFail;
  public Boolean commLOS;
  public Boolean neighbourCableFail;
  public Boolean neighbourCommLOS;
  public Boolean laserOffODP;
  public ODP.PathState pathState;
  public Integer waitToRestoreTime;
  public Integer holdOffTime;
  public SerialNumber neighbourBoard;

  public Boolean isODPEnabled()
  {
    return this.odpEnabled;
  }

  public void setOdpEnabled(Boolean odpEnabled) {
    this.odpEnabled = odpEnabled;
  }

  public Boolean isWaitToRestoreEnabled() {
    return this.waitToRestoreEnabled;
  }

  public void setWaitToRestoreEnabled(Boolean waitToRestoreEnabled) {
    this.waitToRestoreEnabled = waitToRestoreEnabled;
  }

  public Boolean isWorkingPath() {
    return this.workingPath;
  }

  public void setWorkingPath(Boolean workingPath) {
    this.workingPath = workingPath;
  }

  public Boolean isConfigError() {
    return this.configError;
  }

  public void setConfigError(Boolean configError) {
    this.configError = configError;
  }

  public Boolean isCableFail() {
    return this.cableFail;
  }

  public void setCableFail(Boolean cableFail) {
    this.cableFail = cableFail;
  }

  public Boolean isCommLOS() {
    return this.commLOS;
  }

  public void setCommLOS(Boolean commLOS) {
    this.commLOS = commLOS;
  }

  public ODP.PathState getPathState() {
    return this.pathState;
  }

  public void setPathState(ODP.PathState pathState) {
    this.pathState = pathState;
  }

  public Integer getWaitToRestoreTime() {
    return this.waitToRestoreTime;
  }

  public void setWaitToRestoreTime(Integer waitToRestore) {
    this.waitToRestoreTime = waitToRestore;
  }

  public Integer getHoldOffTime() {
    return this.holdOffTime;
  }

  public void setHoldOffTime(Integer holdOff) {
    this.holdOffTime = holdOff;
  }

  public SerialNumber getNeighbourBoard() {
    return this.neighbourBoard;
  }

  public void setNeighbourBoard(SerialNumber neighbourBoard) {
    this.neighbourBoard = neighbourBoard;
  }

  public Boolean isNeighbourCableFail() {
    return this.neighbourCableFail;
  }

  public void setNeighbourCableFail(Boolean neighbourCableFail) {
    this.neighbourCableFail = neighbourCableFail;
  }

  public Boolean isNeighbourCommLOS() {
    return this.neighbourCommLOS;
  }

  public void setNeighbourCommLOS(Boolean neighbourCommLOS) {
    this.neighbourCommLOS = neighbourCommLOS;
  }

  public Boolean isSignalDegradeEnabled() {
    return this.signalDegradeEnabled;
  }

  public void setSignalDegradeEnabled(Boolean signalDegradeEnabled) {
    this.signalDegradeEnabled = signalDegradeEnabled;
  }

  public Boolean isLaserOffODP() {
    return this.laserOffODP;
  }

  public void setLaserOffODP(Boolean laserOffODP) {
    this.laserOffODP = laserOffODP;
  }
}