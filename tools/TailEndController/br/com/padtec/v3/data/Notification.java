package br.com.padtec.v3.data;


import java.io.Serializable;
import java.util.Date;

import br.com.padtec.v3.util.math.Counter;

public class Notification implements Serializable{ 
  private static final long serialVersionUID = 7L;
  private int id;
  private Integer mapKey;
  private Date timestamp;
  private SerialNumber neOrigin;
  private String slot;
  private String subBastidor;
  private final Long key;
  private Object data;
  public static final int ID_NE_ADDED = 1;
  public static final int ID_NE_REMOVED = 2;
  public static final int ID_NE_CHANGED = 3;
  public static final int ID_NE_MOVED = 4;
  public static final int ID_ALARM = 5;
  public static final int ID_AUTO_ACK_ENABLE = 6;
  public static final int ID_AUTO_ACK_DISABLE = 7;
  public static final int ID_ALARM_REBUILD = 8;
  public static final int ID_MAP_ADDED = 9;
  public static final int ID_MAP_REMOVED = 10;
  public static final int ID_MAP_CHANGED = 11;
  public static final int ID_MAP_MOVED = 12;
  public static final int ID_NE_DISABLE = 13;
  public static final int ID_NE_ENABLE = 14;
  public static final int ID_NE_SILENCED = 15;
  public static final int ID_NE_NOTSILENCED = 16;
  public static final int ID_REGEN_RECEVED = 17;
  public static final int ID_ALARM_REPAINT = 18;
  public static final int ID_OTN_COUNT_RESET = 19;
  public static final int ID_DIAGRAM_SAVED = 20;
  public static final int ID_SERVER_TICK = 21;
  public static final int ID_NETWORK_ELEMENT_UPDATED = 22;
  public static final int ID_RACK_SAVED = 25;
  public static final int ID_NE_RENAMED = 30;
  public static final int ID_NE_RACK_SAVED = 31;
  public static final int ID_DATA_TABLE_CHANGED = 33;
  public static final int ID_RELATION_CHANGED = 35;
  public static final int ID_RELATION_REMOVED = 36;
  public static final int ID_NETWORKLINK_CHANGED = 37;
  public static final int ID_NETWORKLINK_REMOVED = 38;
  public static final int ID_CIRCUIT_CHANGED = 39;
  public static final int ID_CIRCUIT_REMOVED = 40;
  public static final int ID_SHK_TELECOMMAND_CHANGED = 41;
  public static final int ID_ALARM_DISABLE = 42;
  public static final int ID_PICTURE_REMOVED = 43;
  private static Counter alarmId = new Counter(0L, 9223372036854775807L, 
    System.currentTimeMillis());

  public Notification(int id, SerialNumber serial)
  {
    this(id);
    this.neOrigin = serial;
  }

  public Notification(int id, SerialNumber serial, Integer mapKey)
  {
    this(id);
    this.neOrigin = serial;
    this.mapKey = mapKey;
  }

  public Notification(int id, Integer mapKey)
  {
    this(id);
    this.mapKey = mapKey;
  }

  public Notification(int id)
  {
    this.mapKey = null;

    this.id = id;
    this.timestamp = new Date();
    this.key = Long.valueOf(alarmId.next());
  }

  public Integer getMapKey()
  {
    return this.mapKey;
  }

  public void setMapKey(Integer mapKey)
  {
    this.mapKey = mapKey;
  }

  public int getId()
  {
    return this.id;
  }

  public SerialNumber getNeOrigin()
  {
    return this.neOrigin;
  }

  public void setNeOrigin(SerialNumber serial)
  {
    this.neOrigin = serial;
  }

  public Date getTimestamp()
  {
    return this.timestamp;
  }

  public void setTimestamp(Date timestamp)
  {
    this.timestamp = timestamp;
  }

  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("Notification ");
    buffer.append(getString(this.id));
    buffer.append(":");
    buffer.append(this.key);
    if (this.neOrigin != null) {
      buffer.append(" ");
      buffer.append(this.neOrigin.toShortString());
    }
    return buffer.toString();
  }

  private static String getString(int id) {
    switch (id) { 
    case 5:
      return "Alarm";
    case 8:
      return "Alarm Rebuild";
    case 7:
      return "Auto Ack Disable";
    case 6:
      return "Auto Ack Enable";
    case 1:
      return "NE Added";
    case 3:
      return "NE Changed";
    case 2:
      return "NE Removed";
    case 9:
      return "Map Added";
    case 11:
      return "Map Changed";
    case 10:
      return "Map Removed";
    case 12:
      return "Map Moved";
    case 13:
      return "NE Disabled";
    case 14:
      return "NE Enabled";
    case 15:
      return "NE Silenced";
    case 16:
      return "NE Unsilenced";
    case 17:
      return "Regen Received";
    case 18:
      return "Alarm Repaint";
    case 19:
      return "OTN Count Reset";
    case 20:
      return "Diagram Saved";
    case 21:
      return "Server Tick";
    case 36:
      return "OTS Trail Removed";
    case 35:
      return "OTS Trail Changed";
    case 38:
      return "OMS Trail Removed";
    case 37:
      return "OMS Trail Changed";
    case 40:
      return "Circuit Removed";
    case 39:
      return "Circuit Changed";
    case 41:
      return "SHK Telecommand Configuration Changed";
    case 4:
      return "Moved NE";
    case 22:
      return "NE updated";
    case 25:
      return "Rack Saved";
    case 30:
      return "NE Renamed";
    case 31:
      return "NE Rack Saved";
    case 33:
      return "Data Table Changed";
    case 43:
      return "Picture Removed";
    case 23:
    case 24:
    case 26:
    case 27:
    case 28:
    case 29:
    case 32:
    case 34:
    case 42: } return "Unknown";
  }

  public Long getKey()
  {
    return this.key;
  }

  public Object getData()
  {
    return this.data;
  }

  public void setData(Object data)
  {
    this.data = data;
  }

  public void setSlot(String slot)
  {
    this.slot = slot;
  }

  public String getSlot()
  {
    return this.slot;
  }

  public void setSubBastidor(String subBastidor)
  {
    this.subBastidor = subBastidor;
  }

  public String getSubBastidor()
  {
    return this.subBastidor;
  }
}