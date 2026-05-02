package br.com.padtec.v3.data.impl;

import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.ne.AutoLaserOff;
import br.com.padtec.v3.data.ne.LaserOff;
import br.com.padtec.v3.data.ne.TrpDWDM;

public class TrpDWDM_Impl extends Transponder_Impl  implements TrpDWDM, LaserOff, AutoLaserOff {
  private static final long serialVersionUID = 3L;
  private double realLambda;
  private boolean laserOff;
  private Boolean autoLaserOff;
  private byte[] tablePin = new byte[5];
  private byte[] tablePout = new byte[5];
  private byte[] tableLambda = new byte[5];
  private double desvioLambda;

  public TrpDWDM_Impl(SerialNumber serial)
  {
    super(serial);
  }

  public boolean isLaserOff()
  {
    return this.laserOff;
  }

  public void setLaserOff(boolean laserOff)
  {
    this.laserOff = laserOff;
  }

  public double getRealLambda()
  {
    return this.realLambda;
  }

  public void setRealLambda(double realLambda)
  {
    this.realLambda = realLambda;
  }

  public String toExtendedString()
  {
    return super.toString() + "\n" + "Pin: " + getPin() + " Pout: " + getPout() + 
      " Lambda: " + this.realLambda + "(" + getNominalLambda() + ")";
  }

  public int getStyle() {
    return 2;
  }

  public Boolean getAutoLaserOff() {
    return this.autoLaserOff;
  }

  public void setAutoLaserOff(Boolean autoLaserOff) {
    this.autoLaserOff = autoLaserOff;
  }

  public byte[] getTablePin() {
    return this.tablePin;
  }

  public void setTablePin(byte pin) {
    this.tablePin[0] = pin;
  }

  public void setTablePin(byte[] table) {
    this.tablePin = table;
  }

  public void fillTablePin(byte[] table) {
    System.arraycopy(table, 0, this.tablePin, 1, 4);
  }

  public byte[] getTablePout() {
    return this.tablePout;
  }

  public void setTablePout(byte pout) {
    this.tablePout[0] = pout;
  }

  public void setTablePout(byte[] table) {
    System.arraycopy(table, 0, this.tablePout, 1, 4);
  }

  public void setTableLamda(byte[] array) {
    System.arraycopy(array, 0, this.tableLambda, 0, 5);
  }

  public byte[] getTableLambda() {
    return this.tableLambda;
  }

  public double getDesvioLambda() {
    return this.desvioLambda;
  }

  public void setDesvioLambda(double desvioLambda) {
    this.desvioLambda = desvioLambda;
  }
}