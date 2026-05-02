package br.com.padtec.v3.util.log;



import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.AccessController;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

//import sun.security.action.GetPropertyAction;

public final class TxtFormatter extends Formatter
{
  private static final boolean debugMode = false;
  private final Date dat = new Date();
  private DateFormat s;
  private final StringBuilder sb = new StringBuilder();

  //private final String lineSeparator = (String)AccessController.doPrivileged(new GetPropertyAction("line.separator"));
  private final String lineSeparator = System.lineSeparator();

  public TxtFormatter()
  {
    initFormatterConfig();
  }

  private String getLevelName(Level l)
  {
    if (l.equals(Level.WARNING))
      return "WARNIN";
    if (l.equals(Level.INFO))
      return "INFO  ";
    if (l.equals(Level.FINE))
      return "FINE  ";
    if (l.equals(Level.FINER)) {
      return "FINER ";
    }
    return l.getName();
  }

  public synchronized String format(LogRecord record)
  {
    this.dat.setTime(record.getMillis());

    this.sb.append(getLevelName(record.getLevel()));
    this.sb.append(' ');
    this.sb.append(this.s.format(this.dat));
    this.sb.append(' ');

    this.sb.append(formatMessage(record));

    this.sb.append(this.lineSeparator);
    if (record.getThrown() != null) {
      try {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        record.getThrown().printStackTrace(pw);
        pw.close();
        this.sb.append(sw.toString());
        sw = null;
        pw = null;
      } catch (Exception ex) {
        ex.printStackTrace();
        ex = null;
      }
    }
    String result = this.sb.toString();
    this.sb.setLength(0);
    return result;
  }

  public void initFormatterConfig()
  {
    this.s = 
      DateFormat.getDateTimeInstance(3, 2, 
      Locale.getDefault());
  }
}
