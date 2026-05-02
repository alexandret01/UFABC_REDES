package br.com.padtec.v3.server.protocols.ppm3.packet;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.util.Functions;



/**
 * This class represents the payload ERROR.
 * It has a Serial Number (2 bytes) to each 
 * Serial Part and Seq, the Date (8 bytes) , and a TLV List.
 *    
 *    The TLV_Type field refer to type of error
 * 
 *               0             1        
        +-------------+-------------+
        |        Serial(part)       |
        +-------------+-------------+
		|        Serial(Seq)        |
        +-------------+-------------+
        | 			    			|
        |        Time Stamp         |
        |         (8 bytes)         |
        |                           |
        +-------------+-------------+
        |             .             |
        |         TLV List          |
        |             .             |
        +-------------+-------------+
 * */
public class PPM3Error implements PPM3Payload, HasSerialNumber, HasTimestamp, HasTlv<TLV>
{
  private SerialNumber serial;
  private long timestamp;
  private List<TLV> tlvList = new ArrayList<TLV>(1);

  public PPM3Payload.Type getType()
  {
    return PPM3Payload.Type.TYPE_ERROR;
  }

  public SerialNumber getSerial()
  {
    return this.serial;
  }

  public void setSerial(SerialNumber serialNumber)
  {
    this.serial = serialNumber;
  }

  public long getTimestamp()
  {
    return this.timestamp;
  }

  public void setTimestamp(long timestamp)
  {
    this.timestamp = timestamp;
  }

  public void addTLV(TLV tlv)
  {
    this.tlvList.add(tlv);
  }

  public TLV getTLV(int idx)
  {
    return ((TLV)this.tlvList.get(idx));
  }

  public int getTLVCount()
  {
    return this.tlvList.size();
  }

  public byte[] getBytes()
  {
    byte[] result = new byte[getSize()];
    if (this.serial != null) {
      Functions.setBytes(result, 0, this.serial.getPart(), 2);
      Functions.setBytes(result, 2, this.serial.getSeq(), 2);
    }
    Functions.setBytes(result, 4, this.timestamp, 8);
    int pos = 12;
    for (TLV tlv : this.tlvList) {
      System.arraycopy(tlv.getType(), 0, result, pos, 2);
      pos += 2;
      System.arraycopy(Functions.l2b(tlv.getLength(), 2), 0, result, pos, 2);
      pos += 2;
      if (tlv.getValue() != null) {
        System.arraycopy(tlv.getValue(), 0, result, pos, tlv.getLength());
        pos += tlv.getLength();
      }
    }
    return result;
  }

  public int getSize() {
    int size = 12;
    for (TLV tlv : this.tlvList) {
      size += tlv.getLength() + 4;
    }
    return size;
  }

  public void set(byte[] payload) throws BadPackageException {
    try {
      int part = (int)Functions.b2l(payload, 0, 2);
      int serial = (int)Functions.b2l(payload, 2, 2);
      this.serial = new SerialNumber(part, serial);
      this.timestamp = Functions.b2l(payload, 4, 8);

      GregorianCalendar calendar = new GregorianCalendar();
      calendar.setTimeInMillis(this.timestamp);
      int packetYear = calendar.get(1);
      int currentYear = new GregorianCalendar().get(1);
      if ((packetYear < currentYear - 1) || (packetYear > currentYear)) {
        throw new InvalidValueException("invalid year");
      }

      int pos = 12;
      while (pos < payload.length) {
        TLV tlv = new TLV();
        tlv.setType(Functions.getSubarray(payload, pos, 2));
        pos += 2;
        int length = (int)Functions.b2l(payload, pos, 2);
        pos += 2;
        if (length > 0) {
          tlv.setValue(Functions.getSubarray(payload, pos, length));
          pos += length;
        }
        this.tlvList.add(tlv);
      }
    } catch (RuntimeException e) {
      throw e;
    } catch (InvalidValueException e) {
      throw new BadPackageException(e);
    }
  }

  public String toString()
  {
    StringBuilder tlvStr = new StringBuilder();
    for (TLV tlv : this.tlvList) {
      tlvStr.append("[type:");
      tlvStr.append(Functions.getHexa(tlv.getType()));

      ErrorType type = ErrorType.getType(tlv.getTypeAsInt());
      if (type != null)
        tlvStr.append("(" + type.toString() + ")");
      else {
        tlvStr.append("(Unknown_ErrorType)");
      }

      tlvStr.append(" len:");
      tlvStr.append(tlv.getLength());
      tlvStr.append(" val:");
      tlvStr.append(Functions.getHexa(tlv.getValue()));
      tlvStr.append("] ");
    }
    String tlvString = tlvStr.toString();
    tlvStr = null;
    return this.serial.toShortString() + 
      ":" + 
      DateFormat.getDateTimeInstance(2, 2)
      .format(new Date(this.timestamp)) + 
      ":" + tlvString;
  }

  public static enum ErrorType
  {
//    UNKNOWN_SUP, FAIL, ELEMENT_NOT_FOUND, INVALID_PAR, GET_NOT_FOUND, DISCARD_PACKET, SET_COMMAND_FAILED, HIST_NOT_FOUND, FULLY_BLOCKED;
	  UNKNOWN_SUP(1), 
	  FAIL(2), 
	  ELEMENT_NOT_FOUND(3), 
	  INVALID_PAR(4), 
	  GET_NOT_FOUND(5), 
	  DISCARD_PACKET(6), 
	  SET_COMMAND_FAILED(7), 
	  HIST_NOT_FOUND(8), 
	  FULLY_BLOCKED(9);
	  
    private int code;

    private ErrorType(int code) {
    	this.code = code;
    }
    public int getCode()
    {
      return this.code;
    }

    public static ErrorType getType(int code) {
      for (ErrorType t : values()) {
        if (t.code == code) {
          return t;
        }
      }
      return null;
    }
  }
}