
package br.com.padtec.v3.data;

import java.io.Serializable;

public class Threshold  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private SerialNumber serial;
  private Integer performanceType;
  private Integer alarmType;
  private Double max;
  private Double min;

  public Threshold(SerialNumber serial, Integer performanceType, Double max, Double min)
  {
    this(serial, performanceType);
    this.max = max;
    this.min = min;
  }

  public Threshold(SerialNumber serial, Integer performanceType) {
    this.serial = serial;
    this.performanceType = performanceType;
    this.alarmType = new Integer(Alarm.getThresholdType(
      performanceType.intValue()));
  }

  public Integer getAlarmType() {
    return this.alarmType;
  }

  public Double getMax() {
    return this.max;
  }

  public Double getMin() {
    return this.min;
  }

  public Integer getPerformanceType() {
    return this.performanceType;
  }

  public SerialNumber getSerial() {
    return this.serial;
  }

  public void setMax(Double max)
  {
    this.max = max;
  }

  public void setMin(Double min)
  {
    this.min = min;
  }

  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("Threshold ");
    buffer.append(this.performanceType);
    buffer.append(" for ");
    buffer.append(this.serial.toShortString());

    if (this.max != null) {
      buffer.append(" max: ");
      buffer.append(this.max);
    }
    if (this.min != null) {
      buffer.append(" min: ");
      buffer.append(this.min);
    }
    return buffer.toString();
  }
}