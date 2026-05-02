package br.com.padtec.v3.data.impl;

import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.ne.Amplifier;
import br.com.padtec.v3.data.ne.LaserOff;

public abstract class Amplifier_Impl extends NE_Impl
  implements Amplifier, LaserOff {
  private static final long serialVersionUID = -2956133239890070259L;
  private double pout;
  private boolean fail;
  private boolean fail1;
  private boolean fail2;
  private boolean fail3;
  private boolean fail4;
  private boolean laserOff;
  private boolean AGCStatus;
  private boolean currentAlarm;
  private boolean currentAlarm1;
  private boolean currentAlarm2;
  private boolean currentAlarm3;
  private boolean currentAlarm4;
  private boolean currentAgcAlarm1;
  private boolean currentAgcAlarm2;
  private boolean currentAgcAlarm3;
  private boolean currentAgcAlarm4;
  private boolean eyeProtection;
  private boolean laserFail;
  private boolean temperatureAlarm;
  private boolean temperatureAlarm1;
  private boolean temperatureAlarm2;
  private boolean temperatureAlarm3;
  private boolean temperatureAlarm4;
  private double gainAGC;
  private boolean MCS;
  private boolean usedBackupLaser;

  public Amplifier_Impl(SerialNumber serial)
  {
    super(serial);
//    setPersistant(objectsPersistant);
//    if (objectsPersistant) {
//      GenericMySQL db = DataBaseFactory.getColectorInstance();
//      if (!(db.isAmpInDB(serial))) {
//        db.createAmp(serial);
//      } else {
//        Long s = (Long)db.getValue(serial, "Amplifier", "slot");
//        if (s != null)
//          super.setSlot(s.intValue());
//      }
//    }
  }

  public boolean isAGC()
  {
    String model = getModel();
    String subsLetras = model.substring(1, 3);

    char modelAmplifier = model.charAt(0);

    if (("FR".indexOf(modelAmplifier) == -1) && 
      ("OA".equals(subsLetras))) {
      char filtrosMontagens = model.charAt(7);
      return ("BCEMS".indexOf(filtrosMontagens) != -1);
    }

    return false;
  }

  public boolean hasIntermediateStage()
  {
    String model = getModel();
    String amplifierType = model.substring(0, 3);

    if ("LOA".equals(amplifierType)) {
      char filtrosMontagens = model.charAt(7);

      return ((filtrosMontagens != 'E') && (filtrosMontagens != 'G') && 
        (filtrosMontagens != 'J') && (filtrosMontagens != 'V'));
    }
    return false;
  }

  public boolean isFail()
  {
    return this.fail;
  }

  public void setFail(boolean fail)
  {
    this.fail = fail;
  }

  public void setFail1(boolean fail) {
    this.fail1 = fail;
  }

  public void setFail2(boolean fail) {
    this.fail2 = fail;
  }

  public void setFail3(boolean fail) {
    this.fail3 = fail;
  }

  public void setFail4(boolean fail) {
    this.fail4 = fail;
  }

  public double getPout()
  {
    if (this.pout == -100.0D) {
      return (0.0D / 0.0D);
    }
    return this.pout;
  }

  public void setPout(double pout)
  {
    this.pout = pout;
  }

  public void setSlot(int slot)
  {
    int oldSlot = getSlot();
    super.setSlot(slot);
//    firePropertyChange(this, "Amplifier", "slot", Integer.valueOf(oldSlot), new Integer(slot), false);
  }

  public void setLaserOff(boolean status)
  {
    this.laserOff = status;
  }

  public boolean isLaserOff() {
    return this.laserOff;
  }

  public boolean getAGCStatus() {
    return this.AGCStatus;
  }

  public boolean getCurrentAlarm()
  {
    return ((!(this.currentAlarm1)) && (!(this.currentAlarm2)) && (!(this.currentAlarm3)) && (!(this.currentAlarm4)) && 
      (!(this.currentAgcAlarm1)) && (!(this.currentAgcAlarm2)) && (!(this.currentAgcAlarm3)) && 
      (!(this.currentAgcAlarm4)) && (!(this.currentAlarm)));
  }

  public boolean getEyeProtection() {
    return this.eyeProtection;
  }

  public boolean getLaserFail() {
    return this.laserFail;
  }

  public boolean getTemperatureAlarm()
  {
    return ((!(this.temperatureAlarm1)) && (!(this.temperatureAlarm2)) && (!(this.temperatureAlarm3)) && 
      (!(this.temperatureAlarm4)) && (!(this.temperatureAlarm)));
  }

  public double getAGCGain() {
    return this.gainAGC;
  }

  public void setAGCStatus(boolean status) {
    this.AGCStatus = status;
  }

  public void setCurrentAlarm(boolean status) {
    this.currentAlarm = status;
  }

  public void setCurrentAlarm1(boolean status) {
    this.currentAlarm1 = status;
  }

  public void setCurrentAlarm2(boolean status) {
    this.currentAlarm2 = status;
  }

  public void setCurrentAlarm3(boolean status) {
    this.currentAlarm3 = status;
  }

  public void setCurrentAlarm4(boolean status) {
    this.currentAlarm4 = status;
  }

  public void setCurrentAgcAlarm1(boolean status) {
    this.currentAgcAlarm1 = status;
  }

  public void setCurrentAgcAlarm2(boolean status) {
    this.currentAgcAlarm2 = status;
  }

  public void setCurrentAgcAlarm3(boolean status) {
    this.currentAgcAlarm3 = status;
  }

  public void setCurrentAgcAlarm4(boolean status) {
    this.currentAgcAlarm4 = status;
  }

  public void setEyeProtection(boolean status) {
    this.eyeProtection = status;
  }

  public void setLaserFail(boolean status) {
    this.laserFail = status;
  }

  public void setTemperatureAlarm(boolean status) {
    this.temperatureAlarm = status;
  }

  public void setTemperatureAlarm1(boolean status) {
    this.temperatureAlarm1 = status;
  }

  public void setTemperatureAlarm2(boolean status) {
    this.temperatureAlarm2 = status;
  }

  public void setTemperatureAlarm3(boolean status) {
    this.temperatureAlarm3 = status;
  }

  public void setTemperatureAlarm4(boolean status) {
    this.temperatureAlarm4 = status;
  }

  public void setAGCGain(double gainValue) {
    this.gainAGC = gainValue;
  }

  public boolean getMCS() {
    return this.MCS;
  }

  public void setMCS(boolean status) {
    this.MCS = status;
  }

  public int getSubBastidor() {
    if (getSlot() % 8 == 0) {
      return (getSlot() / 8);
    }
    return (getSlot() / 8 + 1);
  }

  public int getSubBastidorSlot()
  {
    if (getSlot() > 8) {
      return (getSlot() - ((getSubBastidor() - 1) * 8));
    }
    return getSlot();
  }

  public boolean isUsedBackupLaser()
  {
    return this.usedBackupLaser;
  }

  public void setUsedBackupLaser(boolean usedBackupLaser) {
    this.usedBackupLaser = usedBackupLaser;
  }

  public boolean isFailSPVJ() {
    return ((!(this.fail1)) && (!(this.fail2)) && (!(this.fail3)) && (!(this.fail4)) && (!(this.fail)));
  }
}