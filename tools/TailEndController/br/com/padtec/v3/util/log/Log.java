package br.com.padtec.v3.util.log;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.padtec.v3.util.Functions;



public final class Log
{
  public static final int TYPE_DEFAULT = 0;
  public static final int TYPE_EXCEPTION = 1;
  public static final int TYPE_PACKET = 2;
  public static final int TYPE_MONITORING = 3;
  private static final String[] fileName = { "metropad3.log", 
    "exception.log", "packet.log", "monitoring.log" };

  private static final Logger[] loggerArray = new Logger[4];

  private static final String location = "." + 
    System.getProperty("file.separator") + "logs";

  private static boolean console = true;
  private static boolean off;

  public static void turnOff()
  {
    off = true;
  }

  public static Logger getInstance(int fileReference)
  {
	  
    if (loggerArray[fileReference] != null) {
      return loggerArray[fileReference];
    }

    synchronized (loggerArray)
    {
      if (loggerArray[fileReference] != null) {
        return loggerArray[fileReference];
      }

      String logKey = Log.class.getName() + ":" + fileReference;

      Logger log = Logger.getLogger(logKey);

      Level level = (off) ? Level.OFF : Level.ALL;
      log.setLevel(level);

      log.setUseParentHandlers(false);
      if (Functions.getProperty("console", console)) {
        ConsoleHandler console = new ConsoleHandler();
        console.setFormatter(new TxtFormatter());
        console.setLevel(level);
        log.addHandler(console);
      }
      FileHandler file = null;
      try {
        File folder = new File(getLocation());
        if ((folder.exists()) && (folder.isDirectory()))
          file = new FileLog(getLocation() + 
            System.getProperty("file.separator") + fileName[fileReference]);
        else {
          file = new FileLog(fileName[fileReference]);
        }
        log.addHandler(file);
        folder = null;
      } catch (IOException e) {
        e.printStackTrace();
      }

      loggerArray[fileReference] = log;
      return log;
    }
  }

  public static Logger getInstance()
  {
    return getInstance(0);
  }

  public static String getLocation()
  {
    return location;
  }

  public static boolean isConsole()
  {
    return console;
  }

  public static void setConsole(boolean console)
  {
    console = console;
  }

  public static void resetTimezone()
  {
    for (Logger l : loggerArray)
      if (l != null)
        for (Handler h : l.getHandlers()) {
          Formatter f = h.getFormatter();
          if (f instanceof TxtFormatter) {
            TxtFormatter txtFormatter = (TxtFormatter)f;
            txtFormatter.initFormatterConfig();
          }
        }
  }
}