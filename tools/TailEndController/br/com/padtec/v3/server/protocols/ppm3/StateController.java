package br.com.padtec.v3.server.protocols.ppm3;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.database.TableSpvlHistory;
import br.com.padtec.v3.server.protocols.ppm3.packet.EnumHistoryType;
import br.com.padtec.v3.util.DateUtils;
import br.com.padtec.v3.util.Functions;

public final class StateController
{
  private static TableSpvlHistory table;
  private final Map<Integer, SiteState> siteState = new TreeMap<Integer, SiteState>();

  private final Map<SerialNumber, BoardState> boardState = new TreeMap<SerialNumber, BoardState>();

  private static TableSpvlHistory getTable()
  {
    if (table == null) {
      synchronized (StateController.class) {
        if (table == null) {
          table = TableSpvlHistory.getService();
        }
      }
    }
    return table;
  }

  public synchronized SiteState getSiteState(int idColector, int address)
  {
    
    SiteState state = (SiteState)this.siteState.get(address);
    if (state == null) {
      state = new SiteState(idColector, address);
      this.siteState.put(address, state);
    }
    return state;
  }

  public synchronized void removeSiteState() {
    Iterator<SiteState> iter = this.siteState.values().iterator();
    while (iter.hasNext()) {
      SiteState state = (SiteState)iter.next();
      iter.remove();
      state.remove();
    }
  }

  public synchronized BoardState getBoardState(SerialNumber serial)
  {
    BoardState state = (BoardState)this.boardState.get(serial);
    if (state == null) {
      state = new BoardState(serial);
      this.boardState.put(serial, state);
    }
    return state;
  }

  public synchronized void removeBoardState(SerialNumber serial)
  {
    if (serial != null) {
      BoardState state = (BoardState)this.boardState.remove(serial);
      if (state != null)
        state.remove();
    }
  }

  public synchronized void resetHistoryRequest()
  {
    for (BoardState state : this.boardState.values()) {
      state.requestAlarmHistory = true;
      state.requestMetricHistory = true;
    }
  }

  public static class BoardState  {
    private final SerialNumber serial;
    public boolean requestAlarmHistory = true;
    public boolean requestMetricHistory = true;

    public boolean lastAlarmsSynchronized = false;
    private long lastAlarmHistoryReceived = DateUtils.lastWeek().getTime();
    private long lastLastAlarmHistoryReceived = 0L;
    private long lastMetricHistoryReceived = this.lastAlarmHistoryReceived;

    public BoardState(SerialNumber serial) {
      this.serial = serial;
      TableSpvlHistory table = StateController.getTable();

      Long newVal = table.getSyncTime("SpvlBoardHistory", serial.getPart(), serial.getSeq(), 
        EnumHistoryType.READ_ALARMS.getResponseCode());
      if ((newVal != null) && (newVal.longValue() > this.lastAlarmHistoryReceived)) {
        this.lastAlarmHistoryReceived = newVal.longValue();
      }

      newVal = table.getSyncTime("SpvlBoardHistory",  serial.getPart(), serial.getSeq(), 
        EnumHistoryType.READ_METRICS.getResponseCode());
      if ((newVal != null) && (newVal.longValue() > this.lastMetricHistoryReceived))
        this.lastMetricHistoryReceived = newVal.longValue();
    }

    public long getLastAlarmHistoryReceived()
    {
      return this.lastAlarmHistoryReceived;
    }

    public long getLastLastAlarmHistoryReceived()
    {
      return this.lastLastAlarmHistoryReceived;
    }

    public long getLastMetricHistoryReceived()
    {
      return this.lastMetricHistoryReceived;
    }

    public void setLastAlarmHistoryReceived(long lastAlarmHistoryReceived)
    {
      this.lastAlarmHistoryReceived = lastAlarmHistoryReceived;

      StateController.getTable().setSyncTime("SpvlBoardHistory", this.serial.getPart(), 
        this.serial.getSeq(), EnumHistoryType.READ_ALARMS.getResponseCode(), lastAlarmHistoryReceived);
    }

    public void setLastLastAlarmHistoryReceived(long lastLastAlarmHistoryReceived)
    {
      this.lastLastAlarmHistoryReceived = lastLastAlarmHistoryReceived;
    }

    public void setLastMetricHistoryReceived(long lastMetricHistoryReceived)
    {
      this.lastMetricHistoryReceived = lastMetricHistoryReceived;

      StateController.getTable().setSyncTime("SpvlBoardHistory", this.serial.getPart(), 
        this.serial.getSeq(), EnumHistoryType.READ_METRICS.getResponseCode(), 
        lastMetricHistoryReceived);
    }

    public void remove()
    {
      StateController.getTable().removeSyncTime("SpvlBoardHistory", 
        this.serial.getPart(), this.serial.getSeq(), 
        EnumHistoryType.READ_METRICS.getResponseCode());
      StateController.getTable().removeSyncTime("SpvlBoardHistory", 
        this.serial.getPart(), this.serial.getSeq(), 
        EnumHistoryType.READ_ALARMS.getResponseCode());
    }
  }

  public static class SiteState {
    private long lastCommandHistoryReceived = (Functions.isLct) ? 
      DateUtils.lastHour().getTime() : DateUtils.lastWeek().getTime();
    private int idColector;
    private int address;

    public SiteState(int idColector, int address)
    {
      this.idColector = idColector;
      this.address = address;
      TableSpvlHistory table = StateController.getTable();

      Long newVal = table.getSyncTime("SpvlNeHistory", idColector, 
        address, EnumHistoryType.READ_COMMANDS.getResponseCode());
      if ((newVal != null) && (newVal.longValue() > this.lastCommandHistoryReceived))
        this.lastCommandHistoryReceived = newVal.longValue();
    }

    public long getLastCommandHistoryReceived()
    {
      return this.lastCommandHistoryReceived;
    }

    public void setLastCommandHistoryReceived(long lastCommandHistoryReceived)
    {
      this.lastCommandHistoryReceived = lastCommandHistoryReceived;

      StateController.getTable().setSyncTime("SpvlNeHistory", this.idColector, this.address, 
        EnumHistoryType.READ_COMMANDS.getResponseCode(), 
        lastCommandHistoryReceived);
    }

    public void remove()
    {
      StateController.getTable().removeSyncTime("SpvlNeHistory", this.idColector, 
        this.address, EnumHistoryType.READ_COMMANDS.getResponseCode());
    }
  }
}