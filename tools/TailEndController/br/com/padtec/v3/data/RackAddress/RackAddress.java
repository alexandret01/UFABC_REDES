package br.com.padtec.v3.data.RackAddress;

import java.io.Serializable;

import br.com.padtec.v3.data.SerialNumber;





public class RackAddress
  implements Comparable<RackAddress>, Serializable
{
  private static final long serialVersionUID = 1L;
  public static int TRP = 0;
  public static int AMP = 1;
  public static int AMP1U = 2;
  public static int TRP1U = 3;
  public static int SHK = 4;
  public static int MCO = 5;
  public static int OPS = 6;
  public static int S8X1 = 7;
  public static int FAN = 8;
  public static int MUX = 9;
  public static int DEMUX = 10;
  public static int SOMSOD = 11;
  public static int OADM = 12;
  public static int FANNOTMGMT = 13;
  public static int DCM = 14;
  public static int SUP = 15;
  public static int MUXDEMUX = 16;
  public static int OADMWDM = 17;
  public static int FANG8 = 18;
  public static int MuxGRNoVoa = 19;
  public static int DemuxGRNoVoa = 20;
  public static int ROADMPADTEC = 21;
  public static int MuxVOA = 22;
  public static int DemuxVOA = 23;
  public static int PPM = 24;
  public static int SPLITER = 25;
  private Integer typeElement;
  private int rackKey;
  private int serial;
  private int part;

  public RackAddress(int typeElement, int rackKey, SerialNumber localRef)
  {
    this.typeElement = Integer.valueOf(typeElement);
    this.rackKey = rackKey;
    this.serial = localRef.getSeq();
    this.part = localRef.getPart();
  }

  public int getRackKey() {
    return this.rackKey;
  }

  public void setRackPosition(int rackPosition) {
    this.rackKey = rackPosition;
  }

  public int getSerial() {
    return this.serial;
  }

  public int getPart() {
    return this.part;
  }

  public int compareTo(RackAddress o)
  {
    if (o == null) {
      return 0;
    }
    int typeCompare = this.typeElement.compareTo(o.typeElement);
    if (typeCompare != 0) {
      return typeCompare;
    }
    return new Integer(this.rackKey).compareTo(Integer.valueOf(o.rackKey));
  }

  public int getTypeElement() {
    return this.typeElement.intValue();
  }

  public void setTypeElement(int typeElement) {
    this.typeElement = Integer.valueOf(typeElement);
  }

  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    result = prime * result + this.part;
    result = prime * result + this.rackKey;
    result = prime * result + this.serial;
    result = prime * result + 
      ((this.typeElement == null) ? 0 : this.typeElement.hashCode());
    return result;
  }

  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (super.getClass() != obj.getClass())
      return false;
    RackAddress other = (RackAddress)obj;
    if (this.part != other.part)
      return false;
    if (this.rackKey != other.rackKey)
      return false;
    if (this.serial != other.serial)
      return false;
    if (this.typeElement == null) {
      if (other.typeElement == null)
    	  return false;
    }
    return (!(this.typeElement.equals(other.typeElement)));
  }
}