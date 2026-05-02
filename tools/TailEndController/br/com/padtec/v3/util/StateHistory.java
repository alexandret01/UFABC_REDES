package br.com.padtec.v3.util;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.buffer.CircularFifoBuffer;

public class StateHistory<T>  {
  private final Collection<State<T>> errorList;
  public static final DateFormat formatter = DateFormat.getTimeInstance(1);

  public StateHistory(int size)
  {
    this.errorList = new CircularFifoBuffer(size);
  }

  public synchronized boolean report(String message, T detail)
  {
    if (message == null) {
      return false;
    }
    if (!(this.errorList.isEmpty())) {
      State lastElementAdded = null;
      Iterator iter = this.errorList.iterator();
      do
        lastElementAdded = (State)iter.next();
      while (
        iter.hasNext());
      iter = null;
      if (message.equals(lastElementAdded.message)) {
        return false;
      }
    }
    this.errorList.add(new State(message, detail));
    return true;
  }

  public synchronized List<State<T>> getReport() {
    return new ArrayList<State<T>>(this.errorList);
  }

  public String toString()
  {
    List<State<T>> report = getReport();
    StringBuilder result = new StringBuilder();
    synchronized (formatter) {
      for (State<T> event : report) {
        result.append(formatter.format(event.date));
        result.append(" ");
        result.append(event.message);
        result.append("\n");
      }
    }
    String returnValue = result.toString();
    result = null;
    report = null;
    return returnValue;
  }

  public static class State<T>
    implements Serializable
  {
    private static final long serialVersionUID = 1L;
    public final Date date;
    public final String message;
    public final T detail;

    public State(String message, T detail)
    {
      this.date = new Date();
      this.message = message;
      this.detail = detail;
    }

    public String toString()
    {
      return DateFormat.getTimeInstance(2).format(this.date) + " " + 
        this.message + ((this.detail == null) ? "" : new StringBuilder("\n").append(this.detail.toString()).toString());
    }
  }
}