package br.com.padtec.v3.server.protocols.util.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.apache.commons.collections.buffer.CircularFifoBuffer;

import br.com.padtec.v3.data.Data3;
import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.util.log.Log;

public abstract class AbstractCommunicationMonitor<P>
{
  private final Map<SerialNumber, Collection<Data3<Long, String, P>>> database = new ConcurrentHashMap<SerialNumber, Collection<Data3<Long,String,P>>>();

  public Collection<Data3<Long, String, P>> getBoardData(SerialNumber serial) {
    if (serial == null) {
      return null;
    }
    return this.database.get(serial);
  }

  public List<String> getBoardDataAsString(SerialNumber serial) {
    return boardData2String(getBoardData(serial));
  }

  private List<String> boardData2String(Collection<Data3<Long, String, P>> boardData)
  {
    if (boardData == null) {
      return null;
    }
    List<String> text = new ArrayList<String>(boardData.size());
    try {
      for (Data3<Long, String, P> event : boardData)
        text.add(
          String.format("%1$tT:%1$tL %2$s %3$s", new Object[] { 
          new Date(((Long)event.v1).longValue()), event.v2, event.v3 }));
    }
    catch (RuntimeException e) {
      e.printStackTrace();
    }
    return text;
  }

  public void log(String rwId, SerialNumber serial, P packet)
  {
    try
    {
      if (serial == null) {
        return;
      }
      Collection<Data3<Long, String, P>> list = this.database.get(serial);
      if (list == null) {
        synchronized (this.database) {
          list = this.database.get(serial);
          if (list == null) {
            list = Collections.synchronizedCollection(new CircularFifoBuffer(30));
            this.database.put(serial, list);
          }
        }
      }
      list.add(
        new Data3<Long, String, P>(new Long(System.currentTimeMillis()), 
        rwId, packet));
    } catch (RuntimeException e) {
      Log.getInstance(1).log(Level.SEVERE, 
        "AbstractCommunicationMonitor exception", e);
    }
  }
}