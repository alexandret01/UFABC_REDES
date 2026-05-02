package br.com.padtec.v3.util.timezone;

import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public final class TimeZoneUtils
{
  public static void updateDefaultTimezone()
  {
    int rawOffset;
    int startMonth;
    int startDay;
    int startDayOfWeek;
    int startTime;
    int startTimeMode;
    int endMonth;
    int endDay;
    int endDayOfWeek;
    int endTime;
    int endTimeMode;
    int dstSavings;
    String id = TimeZone.getDefault().getID();
    if ("America/Sao_Paulo".equals(id)) {
      rawOffset = -10800000;
      startMonth = 9;
      startDay = 15;
      startDayOfWeek = -1;
      startTime = 0;
      startTimeMode = 0;
      endMonth = 1;
      endDay = 15;
      endDayOfWeek = -1;
      endTime = 0;
      endTimeMode = 0;
      dstSavings = 3600000;
      TimeZone.setDefault(new SimpleTimeZone(rawOffset, id, startMonth, 
        startDay, startDayOfWeek, startTime, startTimeMode, endMonth, endDay, 
        endDayOfWeek, endTime, endTimeMode, dstSavings));
    } else if ("America/Campo_Grande".equals(id)) {
      rawOffset = -14400000;
      startMonth = 9;
      startDay = 15;
      startDayOfWeek = -1;
      startTime = 0;
      startTimeMode = 0;
      endMonth = 1;
      endDay = 15;
      endDayOfWeek = -1;
      endTime = 0;
      endTimeMode = 0;
      dstSavings = 3600000;
      TimeZone.setDefault(new SimpleTimeZone(rawOffset, id, startMonth, 
        startDay, startDayOfWeek, startTime, startTimeMode, endMonth, endDay, 
        endDayOfWeek, endTime, endTimeMode, dstSavings));
    }
  }

  public static void main(String[] args)
  {
    Formatter f = new Formatter(System.out);

    System.out.println();
    System.out.println();
    System.out.println();

    System.out.println("This program shows the current state of the java VM");
    System.out.println("regarding the timezone and daylight saving time.");
    System.out.println();
    System.out
      .println("It is important that this program is run under the same virtual machine");
    System.out
      .println("and using the same VM parameters (such as -Duser.timezone=\"America/Anywhere\")");
    System.out.println("of other programs.");
    System.out.println();
    System.out
      .println("This program is provided \"as is\" and the use of the results");
    System.out
      .println("provided by the application is of solely responsibility of the user.");

    System.out.println();
    System.out.println();
    System.out.println();

    TimeZone dtz = TimeZone.getDefault();
    System.out.print("Current JVM default timezone: ");
    System.out.println(dtz.getDisplayName(false, 1));
    System.out.print("Current time for this JVM (year-month-day): ");
    f.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %1$tZ\n", new Object[] { new Date() });
    System.out.print("UTC offset (hours): ");
    int utcOffset = dtz.getRawOffset() / 3600000;
    if (utcOffset > 0) {
      System.out.print("+");
    }
    System.out.println(utcOffset);
    System.out.print("DST: ");
    System.out.println(dtz.useDaylightTime());
    System.out.print("DST (hours): ");
    int dstHour = dtz.getDSTSavings() / 3600000;
    if (dstHour > 0) {
      System.out.print("+");
    }
    System.out.println(dstHour);
    System.out.print("Details: ");
    System.out.println(dtz);

    System.out.println();
    System.out.println();
    System.out.println();

    System.out.print("DST preview with one hour increments: ");
    boolean hasDst = false;
    Calendar cal = Calendar.getInstance();
    Date date = cal.getTime();
    long HOUR = 3600000L;
    long DAY = 86400000L;
    int lastHour = -1;
    for (int i = 0; i < 365; ++i) {
      cal.setTime(date);
      if ((lastHour != -1) && (lastHour != cal.get(10))) {
        hasDst = true;
        date.setTime(date.getTime() - 86400000L);
        System.out.println();
        for (int j = 0; j < 24; ++j) {
          f.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM %1$tZ\n", new Object[] { date });
          date.setTime(date.getTime() + 3600000L);
        }
      }
      lastHour = cal.get(10);
      date.setTime(date.getTime() + 86400000L);
    }
    if (!(hasDst))
      System.out.println("This timezone does not have DST.");
  }
}