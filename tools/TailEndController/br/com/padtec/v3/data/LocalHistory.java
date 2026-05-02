package br.com.padtec.v3.data;

import java.io.Serializable;
import java.util.Date;

import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeriesDataItem;

public class LocalHistory implements Serializable {
  private static final long serialVersionUID = 1L;
  TimeSeriesDataItem[] tablePin = new TimeSeriesDataItem[100];
  TimeSeriesDataItem[] tablePout = new TimeSeriesDataItem[100];

  double[] auxTablePout = new double[100];
  double[] auxTablePin = new double[100];

  boolean ready = false;
  private SerialNumber serialNumber;

  public void addTempPoint(int idx, double pin, double pout)
  {
    this.auxTablePin[idx] = pin;
    this.auxTablePout[idx] = pout;
    this.ready = false;
  }

  public void finishPoints(int refIdx, long refTimestamp) {
    int auxIdx = refIdx;
    long auxTime = refTimestamp;
    for (int i = 0; i < 100; ++i) {
      addPoint(i, this.auxTablePin[auxIdx], this.auxTablePout[auxIdx], auxTime);
      --auxIdx;
      if (auxIdx < 0) auxIdx = 99;
      auxTime -= 900000L;
    }
    this.ready = true;
  }

  private void addPoint(int idx, double pin, double pout, long timestamp) {
    RegularTimePeriod p = new Minute(new Date(timestamp));
    this.tablePin[idx] = new TimeSeriesDataItem(p, pin);
    this.tablePout[idx] = new TimeSeriesDataItem(p, pout);
  }

  public TimeSeriesDataItem[] getPinTable() {
    return this.tablePin;
  }

  public TimeSeriesDataItem[] getPoutTable() {
    return this.tablePout;
  }

  public boolean isReady() {
    return this.ready;
  }

  public String toString()
  {
    StringBuffer b = new StringBuffer();
    b.append("Tabela ");
    b.append(this.serialNumber.toShortString());
    b.append('\n');
    for (int i = 0; i < this.tablePin.length; ++i) {
      b.append(this.tablePin[i].getPeriod().getStart().toString());
      b.append(": Pin: ");
      b.append(this.tablePin[i].getValue());
      b.append(" Pout: ");
      b.append(this.tablePout[i].getValue());
      b.append('\n');
    }
    return b.toString();
  }

  public SerialNumber getSerialNumber() {
    return this.serialNumber;
  }

  public void setSerialNumber(SerialNumber serialNumber) {
    this.serialNumber = serialNumber;
  }
}