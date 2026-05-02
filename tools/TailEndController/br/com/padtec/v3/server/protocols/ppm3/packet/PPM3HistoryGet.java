package br.com.padtec.v3.server.protocols.ppm3.packet;


import java.text.DateFormat;
import java.util.Date;

import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.util.DateUtils;
import br.com.padtec.v3.util.Functions;


public class PPM3HistoryGet
  implements PPM3Payload, HasSerialNumber
{
  private EnumHistoryType historyType;
  private SerialNumber serial;
  private Long startDate;
  private Long endDate;

  public PPM3HistoryGet() {
  }

  public PPM3HistoryGet(EnumHistoryType historyType, SerialNumber serial, Long startDate, Long endDate)
  {
    setHistoryType(historyType);
    setSerial(serial);
    setStartDate(startDate);
    setEndDate(endDate);
  }

  public EnumHistoryType getHistoryType()
  {
    return this.historyType;
  }

  public void setHistoryType(EnumHistoryType historyType)
  {
    this.historyType = historyType;
  }

  public SerialNumber getSerial()
  {
    return this.serial;
  }

  public void setSerial(SerialNumber serial)
  {
    this.serial = serial;
  }

  public Long getStartDate()
  {
    return this.startDate;
  }

  public void setStartDate(Long startDate)
  {
    this.startDate = startDate;
  }

  public Long getEndDate()
  {
    return this.endDate;
  }

  public void setEndDate(Long endDate)
  {
    this.endDate = endDate;
  }

  public PPM3Payload.Type getType()
  {
    return PPM3Payload.Type.TYPE_HISTORY_GET;
  }

  public byte[] getBytes()
  {
    if (this.historyType == null) {
      return null;
    }

    byte[] result = new byte[30];
    int pos = 4;
    Functions.setBytes(result, pos, getHistoryType().getRequestCode1(), 2);
    pos += 2;

    Functions.setBytes(result, pos, 22L, 2);
    pos += 2;

    Functions.setBytes(result, pos, getHistoryType().getRequestCode2(), 2);
    pos += 2;

    if (getSerial() == null) {
      pos += 4;
    } else {
      Functions.setBytes(result, pos, getSerial().getPart(), 2);
      pos += 2;
      Functions.setBytes(result, pos, getSerial().getSeq(), 2);
      pos += 2;
    }

    if (getStartDate() != null) {
      Functions.setBytes(result, pos, getStartDate().longValue(), 8);
    }
    pos += 8;
    if (getEndDate() != null) {
      Functions.setBytes(result, pos, getEndDate().longValue(), 8);
    }
    pos += 8;

    return result;
  }

  public void set(byte[] payload)
    throws BadPackageException
  {
    try
    {
      int pos = 4;
      setHistoryType(
        EnumHistoryType.getRequestType(Functions.b2i(payload, pos, 
        2)));
      pos += 2;

      pos += 2;

      pos += 2;

      int part = Functions.b2i(payload, pos, 2);
      pos += 2;
      int serial = Functions.b2i(payload, pos, 2);
      pos += 2;
      if ((part != 0) || (serial != 0)) {
        setSerial(new SerialNumber(part, serial));
      }

      long startDate = Functions.b2l(payload, pos, 8);
      pos += 8;
      if (startDate > 0L) {
        setStartDate(Long.valueOf(startDate));
      }

      long endDate = Functions.b2l(payload, pos, 8);
      pos += 8;
      if (endDate > 0L)
        setEndDate(Long.valueOf(endDate));
    }
    catch (RuntimeException e)
    {
      throw e;
    }
  }

  public int getSize()
  {
    return 30;
  }

  public String toString()
  {
    Date date;
    StringBuilder result = new StringBuilder();
    if (this.historyType == null)
      result.append("HISTORY_TYPE_NOT_SET");
    else {
      result.append(this.historyType.toString());
    }
    if (this.serial == null)
      result.append(" [0#0]");
    else {
      result.append(" ").append(this.serial.toShortString());
    }
    if (this.startDate == null) {
      result.append(" start=none");
    } else {
      result.append(" start=");
      date = new Date(this.startDate.longValue());
      result.append(DateFormat.getDateInstance(2).format(date));
      result.append("_");
      result.append(DateUtils.getTimeAll(date));
    }
    if (this.endDate == null) {
      result.append(" end=none");
    } else {
      result.append(" end=");
      date = new Date(this.endDate.longValue());
      result.append(DateFormat.getDateInstance(2).format(date));
      result.append("_");
      result.append(DateUtils.getTimeAll(date));
    }
    String returnString = result.toString();
    result = null;
    return returnString;
  }

  public boolean equals(Object obj)
  {
    if (obj instanceof PPM3HistoryGet) {
      PPM3HistoryGet o = (PPM3HistoryGet)obj;

      return ((!(Functions.equals(this.startDate, o.startDate))) || 
        (!(Functions.equals(this.historyType, o.historyType))) || 
        (!(Functions.equals(this.endDate, o.endDate))) || 
        (!(Functions.equals(this.serial, o.serial))));
    }
    return false;
  }
}