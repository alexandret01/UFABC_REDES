package br.com.padtec.v3.data;

import java.util.Date;

public abstract interface ExtendedAlarm
{
  public abstract boolean equals(Object paramObject);

  public abstract Date getAckDate();

  public abstract String getAlarmName();

  public abstract int getAlType();

  public abstract Integer getContact();

  public abstract String getDescription();

  public abstract String getDetail();

  public abstract Date getEndDate();

  public abstract String getMapName();

  public abstract String getNeName();

  public abstract int getPriority();

  public abstract String getSlot();

  public abstract Date getStartDate();

  public abstract int hashCode();

  public abstract boolean isAck();

  public abstract boolean isCleared();

  public abstract boolean isEmail();

  public abstract boolean isInst();

  public abstract void setAckDate(long paramLong);

  public abstract void setAlarmName(String paramString);

  public abstract void setContact(Integer paramInteger);

  public abstract void setDescription(String paramString);

  public abstract void setDetail(String paramString);

  public abstract void setEmail(boolean paramBoolean);

  public abstract void setEndDate(long paramLong);

  public abstract void setMapName(String paramString);

  public abstract void setNeName(String paramString);

  public abstract void setPriority(int paramInt);

  public abstract void setSlot(String paramString);

  public abstract String toExtendedString();

  public abstract String toString();

  public abstract SerialNumber getNeOrigin();

  public abstract Object getAckDescription();
}