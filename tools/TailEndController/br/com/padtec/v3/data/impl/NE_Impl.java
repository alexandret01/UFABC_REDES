package br.com.padtec.v3.data.impl;

import java.io.Serializable;
import java.util.logging.Logger;

import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.ne.NE;
import br.com.padtec.v3.util.CustomResourceBundle;
import br.com.padtec.v3.util.log.Log;


public abstract class NE_Impl implements Serializable, NE
{
  private static final long serialVersionUID = 2L;
  
  private String description = "";
  private int supAddress;
  private SerialNumber serial;
  private String name;
  private String modelName;
  private String version = "";
  private String hardwareVersion = "";
  private long update = System.currentTimeMillis();
  private boolean isUp = true;
  private boolean alarmsDisabled = false;
  private int slot = -1;
  private boolean fullSync = true;
//  private static transient NotificationServer notifServer;
//  public static transient boolean objectsPersistant = true;

  private boolean persistant = true;

  private transient Logger log = Log.getInstance();

  public NE_Impl(SerialNumber serial)
  {
    this.serial = serial;
    String model = CustomResourceBundle.getInstance().getModel( serial.getPart());
    this.modelName = model;
//    setPersistant(objectsPersistant);
//    if (objectsPersistant) {
//      GenericMySQL db = DataBaseFactory.getColectorInstance();
//      NE_Data ne = db.getNE(serial);
//      if (ne == null) {
//        this.name = this.modelName + "#" + serial.getSeq();
//        this.log.fine("Creating a new NE " + this.name);
//        db.createNE(serial, this.name, this.modelName);
//      } else {
//        this.name = ne.getName();
//        this.modelName = ne.getModel();
//        this.version = ne.getVersion();
//        this.description = ne.getDescription();
//        this.alarmsDisabled = ne.isAlarmsDisabled();
//        setModelName(model);
//      }
//    } else {
      this.name = this.modelName + "#" + serial.getSeq();
//    }
  }

  public String getName()
  {
    return this.name;
  }

  public void setName(String name)
  {
    if (name == null) {
      throw new NullPointerException("Setting NE name with null value");
    }
    if (name.equals("")) {
      throw new IllegalArgumentException("Empty NE name");
    }
    String oldName = this.name;
    this.name = name;
//    firePropertyChange(this, "NE", "name", oldName, name, true);
  }

  public SerialNumber getSerial()
  {
    return this.serial;
  }

  public boolean equals(Object obj)
  {
    if (obj instanceof NE_Impl) {
      return this.serial.equals(((NE_Impl)obj).getSerial());
    }
    return false;
  }

  public int hashCode()
  {
    return this.serial.hashCode();
  }

  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append(this.modelName);
    buffer.append("#");
    buffer.append(this.serial.getSeq());
    return buffer.toString();
  }

  public String toExtendedString()
  {
    return toString();
  }

  public String getModel() {
    return this.modelName;
  }

//  protected static final void firePropertyChange(NE_Impl n, String table, String atribute, Object oldValue, Object newValue, boolean notify)
//  {
//    if ((!(n.isPersistant())) || (
//      (oldValue != null) && (oldValue.equals(newValue)))) return;
//    GenericMySQL db = DataBaseFactory.getColectorInstance();
//    db.setNeValue(n, table, atribute, newValue);
//
//    if ((notifServer != null) && (notify)) {
//      Notification notif = new Notification(
//        3, n.getSerial());
//      notif.setData(n);
//      notifServer.notify(notif);
//    }
//  }

  public int getSupAddress()
  {
    return this.supAddress;
  }

  public void setSupAddress(int supAddress)
  {
    if (supAddress <= 0) {
      throw new IllegalArgumentException(
        "Supervisor Address can not be zero or negative");
    }
    this.supAddress = supAddress;
  }

  public void setDescription(String desc)
  {
    String oldDesc = this.description;
    this.description = desc;
//    firePropertyChange(this, "NE", "description", oldDesc, desc, false);
  }

  public String getDescription()
  {
    return this.description;
  }

  public boolean isPersistant()
  {
    return this.persistant;
  }

  public void setPersistant(boolean p)
  {
    this.persistant = p;
  }

  public String getVersion() {
    return this.version;
  }

  public void setVersion(String version)
  {
    if (version == null) {
      throw new NullPointerException("Set NE version as null is not permited");
    }
    String oldName = this.version;
    this.version = version;
//    firePropertyChange(this, "NE", "version", oldName, version, false);
  }

  public long getUpdate() {
    return this.update;
  }

  public boolean isUp() {
    return this.isUp;
  }

  public void update()
  {
    update(System.currentTimeMillis());
  }

  public void update(long time)
  {
    this.update = Math.max(this.update, time);
  }

  public void setIsUp(boolean up)
  {
    this.isUp = up;
  }

  public boolean isAlarmsDisabled()
  {
    return this.alarmsDisabled;
  }

  public void setAlarmsDisabled(boolean b)
  {
    boolean oldValue = this.alarmsDisabled;
    this.alarmsDisabled = b;
//    firePropertyChange(this, "NE", "alarmsDisabled", new Boolean(oldValue), new Boolean(this.alarmsDisabled), false);
  }

//  public static void setNotifServer(NotificationServer notifServer) {
//    notifServer = notifServer;
//  }

  public int getSlot() {
    return this.slot;
  }

  public void setSlot(int slot) {
    this.slot = slot;
  }

  public String getModelName() {
    return this.modelName;
  }

  public void setModelName(String modelName) {
    if (modelName == null) {
      throw new NullPointerException("Set NE model as null is not permitted");
    }
    String old = this.modelName;
    this.modelName = modelName;
//    firePropertyChange(this, "NE", "model", old, modelName,false);
  }

  public boolean isFullSync() {
    return this.fullSync;
  }

  public void setFullSync(boolean status) {
    this.fullSync = status;
  }

  public String getHardwareVersion() {
    return this.hardwareVersion;
  }

  public void setHardwareVersion(String hardwareVersion)
  {
    this.hardwareVersion = hardwareVersion;
  }
  
  public static void main(String[] args){
//	  NE_Impl ne = new NE_Impl(new SerialNumber(1316, 684));
//	  System.out.println(ne);
  }
}