package br.com.padtec.v3.server.protocols.util.collection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.impl.NE_Impl;
import br.com.padtec.v3.server.protocols.util.BoardManager;

public final class UpdateScheduler
{
  private final BoardManager manager = new BoardManager();
  private final Map<SerialNumber, Long> getAllAlarmCreation = new ConcurrentHashMap<SerialNumber, Long>();
  private final Map<SerialNumber, Integer> getAllSent = new ConcurrentHashMap<SerialNumber, Integer>();

  public NE_Impl remove(SerialNumber serial) {
    if (serial != null) {
      this.getAllAlarmCreation.remove(serial);
      this.getAllSent.remove(serial);
    }
    return this.manager.removeNe(serial);
  }

  public SerialNumber getNextToUpdate()
  {
    return this.manager.next();
  }

  public void setAsNextToUpdate(SerialNumber serial)
  {
    this.manager.setNext(serial);
  }

  public void addNe(NE_Impl ne)
  {
    this.manager.addNe(ne);
  }

  public Map<SerialNumber, NE_Impl> getAllNe()
  {
    return this.manager.getNe();
  }

  public NE_Impl getNe(SerialNumber serial)
  {
    return this.manager.getNe(serial);
  }

  public void notifyAlarmTrapReceived(SerialNumber serial)
  {
    if (serial == null) {
      return;
    }
    Long value = Long.valueOf(System.currentTimeMillis() + 60000L);
    this.getAllAlarmCreation.put(serial, value);
  }

  public boolean createAlarmFromGetAll(SerialNumber serial)
  {
    if (serial == null) {
      return false;
    }
    Long value = (Long)this.getAllAlarmCreation.get(serial);
    return ((value != null) && (value.longValue() >= System.currentTimeMillis()));
  }

  public void setCreateAlarmFromGetAll(SerialNumber serial)
  {
    if (serial == null) {
      return;
    }
    this.getAllAlarmCreation.remove(serial);
  }

  public int getGetAllSent(SerialNumber serial)
  {
    if (serial == null) {
      return 0;
    }
    Integer counter = (Integer)this.getAllSent.get(serial);
    if (counter == null) {
      return 0;
    }
    return counter.intValue();
  }

  public void incGetAllSent(SerialNumber serial)
  {
    if (serial == null) {
      return;
    }
    Integer counter = (Integer)this.getAllSent.put(serial, Integer.valueOf(1));
    if ((counter != null) && (counter.intValue() != 0))
      this.getAllSent.put(serial, Integer.valueOf(counter.intValue() + 1));
  }

  public void clearGetAllSent(SerialNumber serial)
  {
    if (serial == null) {
      return;
    }
    this.getAllSent.put(serial, Integer.valueOf(0));
  }

  public int size()
  {
    return this.manager.size();
  }
}