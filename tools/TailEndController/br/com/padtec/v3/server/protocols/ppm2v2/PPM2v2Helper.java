
package br.com.padtec.v3.server.protocols.ppm2v2;

import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.ne.NE;
import br.com.padtec.v3.util.Functions;

public final class PPM2v2Helper {
  public static final byte[] getBytes(NE ne)
  {
    if (ne == null) {
      return null;
    }
    return getBytes((byte)ne.getSupAddress(), ne.getSerial());
  }

  public static final byte[] getBytes(byte supAddress, SerialNumber serial)
  {
    if (serial == null) {
      return null;
    }
    int p = serial.getPart();
    int sn = serial.getSeq();
    byte[] destino = new byte[5];
    destino[0] = supAddress;
    destino[1] = (byte)(p >> 8 & 0xFF);
    destino[2] = (byte)(p & 0xFF);
    destino[3] = (byte)(sn >> 8 & 0xFF);
    destino[4] = (byte)(sn & 0xFF);
    return destino;
  }

  public static final SerialNumber getSerial(byte[] data)
  {
    int p = (Functions.b2i(data[1]) << 8) + Functions.b2i(data[2]);
    int s = (Functions.b2i(data[3]) << 8) + Functions.b2i(data[4]);
    SerialNumber serial = new SerialNumber(p, s);
    return serial;
  }

  public static String parseData(byte[] data, int start, int end) {
    StringBuilder text = new StringBuilder();
    text.append("data: ");
    int i = start;
    if (end < start) {
      end = data.length;
    }

    while (i < end)
    {
      text.append(Functions.getHexa(data[i]));
      ++i;
      text.append(' ');
    }
    String result = text.toString();
    text = null;
    return result;
  }

  private static String parseDataTlv(byte[] data, int start) {
    StringBuilder text = new StringBuilder();
    int i = start;
    while (i < data.length) {
      text.append("code,");
      text.append((char)Functions.b2i(data[i]));
      text.append(",");
      text.append(Functions.getHexa(data[i]));
      text.append(" b1:");
      ++i;
      text.append(Functions.b2i(data[i]));
      text.append(",");
      text.append(Functions.getHexa(data[i]));
      ++i;
      int size = Functions.b2i(data[i]);
      text.append(" ");
      text.append(size);
      text.append("bytes ");
      ++i;
      text.append(parseData(data, i, i + size));
      i += size;
      if (i < data.length) {
        text.append(' ');
      }
    }
    String result = text.toString();
    text = null;
    return result;
  }

  public static String parseResponseParam(byte param) {
    StringBuilder text = new StringBuilder();
    switch (param)
    {
    case 2:
      text.append("RESP_CMD_FAIL");
      break;
    case -1:
      text.append("RESP_EXTENDED");
      break;
    case 5:
      text.append("RESP_GL_LOCKED");
      break;
    case 4:
      text.append("RESP_INVALID_PAR");
      break;
    case 1:
      text.append("RESP_NA");
      break;
    case 3:
      text.append("RESP_NOT_FOUND");
      break;
    case 0:
      text.append("RESP_OK");
      break;
    case -2:
      text.append("RESP_TLV");
      break;
    default:
      text.append("CMD_RESPONSE:");
      text.append(Functions.getHexa(param));
    }

    String result = text.toString();
    text = null;
    return result;
  }

  public static String parseCommandParam(byte command, byte param) {
    StringBuilder text = new StringBuilder();
    switch (command)
    {
    case 1:
      text.append("CMD_GET");
      break;
    case -2:
      text.append("CMD_INVALID");
      break;
    case 5:
      text.append("CMD_NOTIFICATION");
      break;
    case 3:
      break;
    case 2:
      text.append("CMD_SET");
      break;
    case 4:
      text.append("CMD_TRAP");
      break;
    case -1:
    case 0:
    default:
      text.append("command:");
      text.append(Functions.getHexa(command));
    }

    if (command == 3) {
      text.append(parseResponseParam(param));
    } else {
      text.append(" ");
      text.append(Functions.getHexa(param));
    }
    String result = text.toString();
    text = null;
    return result;
  }

  public static String parsePacket(byte[] data) {
    StringBuilder text = new StringBuilder();
    try {
      text.append(Functions.getHexa(data[0]));
      text.append(" ");
      text.append(Functions.b2i(data[2]));
      text.append("bytes ");
      text.append(parseCommandParam(data[3], data[4]));
      text.append(" [");
      text.append(Functions.b2i(data[5]));
      text.append(',');
      text.append(Functions.b2l(data, 6, 2));
      text.append(',');
      text.append(Functions.b2l(data, 8, 2));
      text.append("][");
      text.append(Functions.b2i(data[10]));
      text.append(',');
      text.append(Functions.b2l(data, 11, 2));
      text.append(',');
      text.append(Functions.b2l(data, 13, 2));
      text.append(']');
      if (data.length > 15) {
        text.append(" ");
        if ((data[3] == 3) && (data[4] == -1)) {
          text.append(parseResponseParam(data[15]));
          text.append(" Orig[");
          text.append(parseCommandParam(data[16], data[17]));
          text.append("] reservado ");
          text.append(Functions.b2i(data[18]));
          text.append(' ');
          text.append(Functions.b2i(data[19]));
          text.append(" ");
          if (data[15] == -2)
            text.append(parseDataTlv(data, 20));
          else
            text.append(parseData(data, 20, -1));
        }
        else {
          text.append(parseData(data, 15, -1));
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return text.toString();
  }
}