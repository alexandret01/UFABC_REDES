package br.com.padtec.v3.database;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import br.com.padtec.v3.util.log.Log;

public class TableSpvlHistoryLct extends TableSpvlHistory
{
  private final ConcurrentHashMap<PrimaryKey, Long> db = new ConcurrentHashMap<PrimaryKey, Long>();

  public Long getSyncTime(String table, int id1, int id2, byte historyTypeResponseCode)
  {
    try
    {
      return ((Long)this.db.get(new PrimaryKey(table, id1, id2, historyTypeResponseCode)));
    } catch (Exception e) {
      Log.getInstance(1).log(Level.SEVERE, 
        "Fail reading SpvlHistory table", e); }
    return null;
  }

  public void setSyncTime(String table, int id1, int id2, byte historyTypeResponseCode, long syncTime)
  {
    try
    {
      this.db.put(new PrimaryKey(table, id1, id2, historyTypeResponseCode), 
        new Long(syncTime));
    } catch (Exception e) {
      Log.getInstance(1).log(Level.SEVERE, 
        "Fail updating SpvlHistory table", e);
    }
  }

  public Integer removeSyncTime(String table, int id1, int id2, byte historyTypeResponseCode)
  {
    try
    {
      return Integer.valueOf(((Long)this.db.remove(
        new PrimaryKey(table, id1, id2, historyTypeResponseCode))).intValue());
    } catch (Exception e) {
      Log.getInstance(1).log(Level.SEVERE, 
        "Fail removing from SpvlHistory table", e); }
    return null;
  }

  private static final class PrimaryKey
  {
    private String table;
    private final int id1;
    private final int id2;
    private final byte historyType;

    public PrimaryKey(String table, int id1, int id2, byte historyType)
    {
      this.table = table;
      this.id1 = id1;
      this.id2 = id2;
      this.historyType = historyType;
    }

    public int hashCode()
    {
      int prime = 31;
      int result = 1;
      result = prime * result + this.id1;
      result = prime * result + this.id2;
      result = prime * result + ((this.table == null) ? 0 : this.table.hashCode());
      return result;
    }

    public boolean equals(Object obj)
    {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (!(obj instanceof PrimaryKey))
        return false;
      PrimaryKey other = (PrimaryKey)obj;
      if (this.historyType != other.historyType)
        return false;
      if (this.id1 != other.id1)
        return false;
      if (this.id2 != other.id2)
        return false;
      if (this.table == null) {
        if (other.table != null) 
        	return false;
      }
      return (!(this.table.equals(other.table)));
    }
  }
}