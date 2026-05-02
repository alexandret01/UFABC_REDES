package br.com.padtec.v3.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
  public static void setTime(Calendar cal, int hour, int minute, int second, int millisecond)
  {
    cal.set(11, hour);
    cal.set(12, minute);
    cal.set(13, second);
    cal.set(14, millisecond);
  }

  public static Date lastMonth()
  {
    Calendar now = Calendar.getInstance();
    now.add(2, -1);
    Date result = now.getTime();
    now = null;
    return result;
  }

  public static Date lastWeek()
  {
    Calendar now = Calendar.getInstance();
    now.add(5, -7);
    Date result = now.getTime();
    now = null;
    return result;
  }

  public static Date lastHour()
  {
    Calendar now = Calendar.getInstance();
    now.add(11, -1);
    Date result = now.getTime();
    now = null;
    return result;
  }

  public static Date min(Date d1, Date d2)
  {
    if (d1 == null) {
      return d2;
    }
    if (d2 == null) {
      return d1;
    }
    if (d1.getTime() < d2.getTime()) {
      return d1;
    }
    return d2;
  }

  public static Date max(Date d1, Date d2)
  {
    if (d1 == null) {
      return d2;
    }
    if (d2 == null) {
      return d1;
    }
    if (d1.getTime() > d2.getTime()) {
      return d1;
    }
    return d2;
  }

  public static int compareTo(Date date1, Date date2)
  {
    long time1 = date1.getTime();
    long time2 = date2.getTime();
    time1 = round(time1);
    time2 = round(time2);
    return (int)(time1 - time2);
  }

  public static long round(long time)
  {
    time /= 1000L;
    return (time * 1000L);
  }

  public static String getDate(Date date)
  {
    return new SimpleDateFormat("yyyyMMdd").format(date);
  }

  public static String getTime(Date date)
  {
    return new SimpleDateFormat("HHmmss").format(date);
  }

  public static String getTimeShort(Date date)
  {
    return new SimpleDateFormat("HH:mm").format(date);
  }

  public static String getTimeMedium(Date date)
  {
    return new SimpleDateFormat("HH:mm:ss").format(date);
  }

  public static String getTimeAll(Date date)
  {
    return new SimpleDateFormat("HH:mm:ss.SSS").format(date);
  }

  public static String getDateTime(Date date)
  {
    return new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);
  }

  public static String getDateMedium(Date date)
  {
    return new SimpleDateFormat("dd/MM/yyyy").format(date);
  }

  public static String getDateMonthDay(Date date)
  {
    return new SimpleDateFormat("dd/MM").format(date);
  }
}