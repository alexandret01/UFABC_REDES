package br.com.padtec.v3.data.impl;

import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.Temperature;
import br.com.padtec.v3.data.ne.Transponder;


public abstract class Transponder_Impl extends NE_Impl  implements Transponder {
  private double pin = (0.0D / 0.0D);

  private double pout = (0.0D / 0.0D);
  private boolean los;
  private boolean fail;
  private boolean n3db;
  private int slot = 0;

  private String channel = "";
  private double lambda;
  private final Temperature laserTemperature = new Temperature();

  public Transponder_Impl(SerialNumber serial)
  {
    super(serial);
//    setPersistant(objectsPersistant);
//    if (objectsPersistant) {
//      GenericMySQL db = DataBaseFactory.getColectorInstance();
//      if (!(db.isTrpInDB(serial))) {
//        db.createTrp(serial);
//      } else {
//        String c = (String)db.getValue(serial, "Transponder", 
//          "channel");
//        Long s = (Long)db.getValue(serial, "Transponder", "slot");
//        if (c != null)
//          this.channel = c;
//        if (s != null)
//          this.slot = s.intValue();
//      }
//    }
  }

  public boolean isFail()
  {
    return this.fail;
  }

  public void setFail(boolean fail)
  {
    this.fail = fail;
  }

  public boolean isLos()
  {
    return this.los;
  }

  public void setLos(boolean los)
  {
    this.los = los;
  }

  public double getPin()
  {
    return this.pin;
  }

  public void setPin(double pin)
  {
    this.pin = pin;
  }

  public double getPout()
  {
    return this.pout;
  }

  public Temperature getLaserTemperature()
  {
    return this.laserTemperature;
  }

  public void setPout(double pout)
  {
    this.pout = pout;
  }

  public boolean isN3db()
  {
    return this.n3db;
  }

  public void setN3db(boolean n3db)
  {
    this.n3db = n3db;
  }

  public int getSlot()
  {
    return this.slot;
  }

  public void setSlot(int slot)
  {
    Integer oldSlot = new Integer(this.slot);
    this.slot = slot;
//    firePropertyChange(this, "Transponder", "slot", oldSlot, new Integer(slot), false);
  }

  public void setChannel(String channel)
  {
    String oldChannel = this.channel;
    this.channel = channel;
//    firePropertyChange(this, "Transponder", "channel", oldChannel, channel, false);
  }

  public String getChannel()
  {
    return this.channel;
  }

  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append(this.slot);
    buffer.append("-");
    buffer.append(super.toString());
    return buffer.toString();
  }

  public String toExtendedString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append(this.slot);
    buffer.append("-");
    buffer.append(super.toString());
    return buffer.toString();
  }

  public double getNominalLambda()
  {
    return this.lambda;
  }

  public void setNominalLambda(double lambda)
  {
    this.lambda = lambda;
  }

  public int getSubBastidor() {
    if (getSlot() % 10 == 0) {
      return (getSlot() / 10);
    }
    return (getSlot() / 10 + 1);
  }

  public int getSubBastidorSlot()
  {
    if (getSlot() > 10) {
      return (getSlot() - ((getSubBastidor() - 1) * 10));
    }
    return getSlot();
  }
}