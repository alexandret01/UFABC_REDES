
package br.com.padtec.v3.server.protocols.ppm2v2;


import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;

import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.util.Functions;

public class PPM2v2  implements Serializable {
  private static final long serialVersionUID = 1L;
  public static final byte[] ADDR_NULL = new byte[5];
  public static final int TAMPACKET = 270;
  public static final int HEADER = 15;
  public static final int ADDR_SIZE = 5;
  public static final int MAX_DATA_SIZE = 255;
  public static byte version = 37;
  private byte command;
  private byte parameter;
  private byte[] source = new byte[ADDR_SIZE];
  private byte[] destiny = new byte[ADDR_SIZE];
  private byte[] data;
  private int dataSize;
  public static final byte CMD_GET = 1;
  public static final byte CMD_SET = 2;
  public static final byte CMD_RESPONSE = 3;
  public static final byte CMD_TRAP = 4;
  public static final byte CMD_NOTIFICATION = 5;
  public static final byte CMD_INVALID = -2;
  public static final byte PARAM_INVALID = -2;
  public static final byte TRAP_NEW = 0;
  public static final byte TRAP_DEL = -128;
  public static final byte GET_ALL = 0;
  public static final byte GET_TAXA = 1;
  public static final byte GET_TAXA2 = 37;
  public static final byte GET_AMPLICONF = 2;
  public static final byte GET_SUP_DEBUG = 3;
  public static final byte GET_UPTIME = 4;
  public static final byte GET_OTN = 5;
  public static final byte GET_ODU_SAPI_DAPI = 6;
  public static final byte GET_J0 = 7;
  public static final byte GET_PT = 8;
  public static final byte GET_ODU_SAPI_DAPI_REF_TX = 9;
  public static final byte GET_ODU_SAPI_DAPI_REF_RX = 10;
  public static final byte GET_LOOPBACK = 11;
  public static final byte GET_OTN_TERM = 12;
  public static final byte GET_OTU_SAPI_DAPI = 13;
  public static final byte GET_OTU_SAPI_DAPI_REF_TX = 14;
  public static final byte GET_OTU_SAPI_DAPI_REF_RX = 15;
  public static final byte GET_STAT = 16;
  public static final byte GET_OTN_REGEN = 17;
  public static final byte GET_OTU_SAPI_DAPI2 = 18;
  public static final byte GET_OTU_SAPI_DAPI_REF_TX2 = 19;
  public static final byte GET_OTU_SAPI_DAPI_REF_RX2 = 20;
  public static final byte GET_ODU_OP_SPEC = 21;
  public static final byte GET_ODU_OP_SPEC_REF_TX = 22;
  public static final byte GET_OTU_OP_SPEC = 23;
  public static final byte GET_OTU_OP_SPEC_REF_TX = 24;
  public static final byte GET_J0_SDH1 = 25;
  public static final byte GET_J0_SDH2 = 26;
  public static final byte GET_J0_SDH3 = 27;
  public static final byte GET_J0_SDH4 = 28;
  public static final byte GET_DEBUG = 64;
  public static final byte GET_VENDOR_SFP1 = 29;
  public static final byte GET_VENDOR_SFP2 = 30;
  public static final byte GET_VENDOR_SFP3 = 31;
  public static final byte GET_VENDOR_SFP4 = 32;
  public static final byte GET_SUP_IP = 33;
  public static final byte GET_SUP_CONNECTION = 34;
  public static final byte GET_SUP_CONF = 35;
  public static final byte GET_OTU_OP_SPEC2 = 52;
  public static final byte GET_OTU_OP_SPEC_REF_TX2 = 53;
  public static final byte GET_SFP = 57;
  public static final byte GET_STATIC_INFO_TLV = 38;
  public static final byte GET_ALARMS_TLV = 39;
  public static final byte GET_ALL_TLV = 40;
  public static final byte GET_OTN_TLV = 41;
  public static final byte GET_PERF_HISTORY_TLV = 48;
  public static final byte GET_TRP_CLIENT_DATA = 36;
  public static final byte GET_SUP_RACK_CONF = 50;
  public static final byte GET_CMD_HISTORY_TLV = 51;
  public static final byte GET_TEMP_ALARM = 54;
  public static final byte SET_REGTRAPS = 0;
  public static final byte SET_SUP_CONN_CLOSE = 20;
  public static final byte SET_SUP_CONT_RESET = 21;
  public static final byte SET_SUP_TRAP_HIST = 21;
  public static final byte SET_SUP_CONF = 22;
  public static final byte SET_SUP_RACK_CONF = 23;
  public static final byte SET_SUP_RACK_CONF_SPVL = 38;
  public static final byte SET_TRP_AUTOLASEROFF_ON = -87;
  public static final byte SET_TRP_AUTOLASEROFF_OFF = -86;
  public static final byte SET_TRP_AUTOLASEROFF2_ON = -85;
  public static final byte SET_TRP_AUTOLASEROFF2_OFF = -84;
  public static final byte SET_TRP_FEC_TX_OFF = -91;
  public static final byte SET_TRP_FEC_TX_ON = -90;
  public static final byte SET_TRP_FEC_RX_OFF = -89;
  public static final byte SET_TRP_FEC_RX_ON = -88;
  public static final byte SET_TRP_ODU_SAPI_TX = -79;
  public static final byte SET_TRP_ODU_DAPI_TX = -78;
  public static final byte SET_TRP_ODU_SAPI_RX = -77;
  public static final byte SET_TRP_ODU_DAPI_RX = -76;
  public static final byte SET_TRP_TIM_ACT_ON = -73;
  public static final byte SET_TRP_TIM_ACT_OFF = -72;
  public static final byte SET_TRP_OTU_SAPI_TX = -70;
  public static final byte SET_TRP_OTU_DAPI_TX = -69;
  public static final byte SET_TRP_OTU_SAPI_RX = -68;
  public static final byte SET_TRP_OTU_DAPI_RX = -67;
  public static final byte SET_TRP_OTU_SAPI2_TX = -55;
  public static final byte SET_TRP_OTU_DAPI2_TX = -54;
  public static final byte SET_TRP_OTU_SAPI2_RX = -53;
  public static final byte SET_TRP_OTU_DAPI2_RX = -52;
  public static final byte SET_TRP_TAXA_MODE = -61;
  public static final byte SET_TRP_FEC2_TX_OFF = -59;
  public static final byte SET_TRP_FEC2_TX_ON = -58;
  public static final byte SET_TRP_FEC2_RX_OFF = -57;
  public static final byte SET_TRP_FEC2_RX_ON = -56;
  public static final byte SET_SW8_AUTO_MANUAL = 2;
  public static final byte SET_SW8_SET_CHANNEL = 4;
  public static final byte SET_SW8_SWITCH = 6;
  public static final byte SET_EXTENDED = -1;
  public static final byte RESP_OK = 0;
  public static final byte RESP_NA = 1;
  public static final byte RESP_CMD_FAIL = 2;
  public static final byte RESP_NOT_FOUND = 3;
  public static final byte RESP_INVALID_PAR = 4;
  public static final byte RESP_GL_LOCKED = 5;
  public static final byte RESP_EXTENDED = -1;
  public static final byte RESP_TLV = -2;
  public static final int ADDR_SUP = 0;
  public static final int ADDR_CP1 = 1;
  public static final int ADDR_CP2 = 2;
  public static final int ADDR_NS1 = 3;
  public static final int ADDR_NS2 = 4;
  public static final byte RESERVED_BYTE = -1;
  public static final int RESPONSE_EXTEND_SIZE = 5;
  public static final byte TRAP_LOS = 1;
  public static final byte TRAP_N3DB_OR_LOF = 2;
  public static final byte TRAP_TRP_FAIL = 3;
  public static final byte TRAP_TRP_LASEROFF = 4;
  public static final byte TRAP_LOS2 = 5;
  public static final byte TRAP_LASEROFF2 = 6;
  public static final byte TRAP_TAXA_OR_LOF2 = 7;
  public static final byte TRAP_TRP_FAIL2 = 21;
  public static final byte TRAP_TRPOTN_ODU_TIM = 8;
  public static final byte TRAP_TRPOTN_ODU_BDI = 9;
  public static final byte TRAP_TRPOTN_ODU_AIS = 10;
  public static final byte TRAP_TRPOTN_LOS_SYNC = 11;
  public static final byte TRAP_TRPOTN_LOS2_SYNC = 12;
  public static final byte TRAP_TRPOTN_J0 = 13;
  public static final byte TRAP_TRPOTN_ODU_SAPI_DAPI = 14;
  public static final byte TRAP_TRPOTN_OTU_SAPI_DAPI = 15;
  public static final byte TRAP_TRPOTN_PT = 16;
  public static final byte TRAP_TRPOTN_LOF = 70;
  public static final byte TRAP_TRPOTN_OTU_TIM = 17;
  public static final byte TRAP_TRPOTN_OTU_BDI = 18;
  public static final byte TRAP_TRPOTN_LOM = 19;
  public static final byte TRAP_TRPOTN_STAT = 20;
  public static final byte TRAP_TRPOTN_ENCAISOFF = 22;
  public static final byte TRAP_TRPOTN_ODU_TTI_REF_TX = 23;
  public static final byte TRAP_TRPOTN_ODU_TTI_REF_RX = 24;
  public static final byte TRAP_TRPOTN_OTU_TTI_REF_TX = 25;
  public static final byte TRAP_TRPOTN_OTU_TTI_REF_RX = 26;
  public static final byte TRAP_TRPOTN_OTU_TTI_REF_TX2 = 27;
  public static final byte TRAP_TRPOTN_OTU_TTI_REF_RX2 = 28;
  public static final byte TRAP_TRPOTN_OTU_TIM2 = 29;
  public static final byte TRAP_TRPOTN_OTU_BDI2 = 30;
  public static final byte TRAP_TRPOTN_LOM2 = 31;
  public static final byte TRAP_TRPOTN_OTU_SAPI_DAPI2 = 32;
  public static final byte TRAP_TRPOTN_ODU_OP_SPEC = 33;
  public static final byte TRAP_TRPOTN_OTU_OP_SPEC = 34;
  public static final byte TRAP_TRPOTN_ODU_OP_SPEC_REF_TX = 35;
  public static final byte TRAP_TRPOTN_OTU_OP_SPEC_REF_TX = 36;
  public static final byte TRAP_MUX_LOS_SDH1 = 37;
  public static final byte TRAP_MUX_LOF_SDH1 = 38;
  public static final byte TRAP_MUX_FAIL_SDH1 = 39;
  public static final byte TRAP_MUX_LASEROFF_SDH1 = 40;
  public static final byte TRAP_MUX_LOS_SDH2 = 41;
  public static final byte TRAP_MUX_LOF_SDH2 = 42;
  public static final byte TRAP_MUX_FAIL_SDH2 = 43;
  public static final byte TRAP_MUX_LASEROFF_SDH2 = 44;
  public static final byte TRAP_MUX_LOS_SDH3 = 45;
  public static final byte TRAP_MUX_LOF_SDH3 = 46;
  public static final byte TRAP_MUX_FAIL_SDH3 = 47;
  public static final byte TRAP_MUX_LASEROFF_SDH3 = 48;
  public static final byte TRAP_MUX_LOS_SDH4 = 49;
  public static final byte TRAP_MUX_LOF_SDH4 = 50;
  public static final byte TRAP_MUX_FAIL_SDH4 = 51;
  public static final byte TRAP_MUX_LASEROFF_SDH4 = 52;
  public static final byte TRAP_MUX_LOS_SYNC_SDH1 = 53;
  public static final byte TRAP_MUX_LOS_SYNC_SDH2 = 54;
  public static final byte TRAP_MUX_LOS_SYNC_SDH3 = 55;
  public static final byte TRAP_MUX_LOS_SYNC_SDH4 = 56;
  public static final byte TRAP_MUX_J0_SDH1 = 57;
  public static final byte TRAP_MUX_J0_SDH2 = 58;
  public static final byte TRAP_MUX_J0_SDH3 = 59;
  public static final byte TRAP_MUX_J0_SDH4 = 60;
  public static final byte TRAP_MUX_REMOVED_SFP1 = 61;
  public static final byte TRAP_MUX_CHANGED_SFP1 = 62;
  public static final byte TRAP_MUX_REMOVED_SFP2 = 63;
  public static final byte TRAP_MUX_CHANGED_SFP2 = 64;
  public static final byte TRAP_MUX_REMOVED_SFP3 = 65;
  public static final byte TRAP_MUX_CHANGED_SFP3 = 66;
  public static final byte TRAP_MUX_REMOVED_SFP4 = 67;
  public static final byte TRAP_MUX_CHANGED_SFP4 = 68;
  public static final byte TRAP_COMB_SFP_REMOVED = 37;
  public static final byte TRAP_COMB_SFP_CHANGED = 45;
  public static final byte TRAP_TRPOTN_PLM = 69;
  public static final byte TRAP_SUP_UNLOCK = 1;
  public static final byte TRAP_SUP_LCT = 2;
  public static final byte TRAP_SUP_CS_LOS1 = 3;
  public static final byte TRAP_SUP_CS_LOS2 = 4;
  public static final byte TRAP_SUP_COMMAND_RX = 5;
  public static final byte TRAP_SUP_RESTART = 6;
  public static final byte TRAP_AMP_FAIL = 2;
  public static final byte TRAP_AMP_LASEROFF = 3;
  public static final byte TRAP_AMP_ALS = 4;
  public static final byte TRAP_AMP_MCS_TEMPERATURE_ALARM = 5;
  public static final byte TRAP_AMP_CURRENT_ALARM = 6;
  public static final byte TRAP_AMP_TEMPERATURE_ALARM = 7;
  public static final byte TRAP_MCO_TX = 1;
  public static final byte TRAP_MCO_FX = 2;
  public static final byte TRAP_OP_LOS1 = 1;
  public static final byte TRAP_OP_LOS2 = 2;
  public static final byte TRAP_OP_COMMUTE = 3;
  public static final byte TRAP_OP_AUTOOFF = 4;
  public static final byte TRAP_OP_BLOCK = 5;
  public static final byte TRAP_SW8_CH_LOST = 1;
  public static final byte TRAP_SW8_MOD_FAIL = 2;
  public static final byte TRAP_SW8_MOD_DISABLE = 3;
  public static final byte TRAP_SW8_MOD_SWITCH = 4;
  public static final byte TRAP_FAN_OVERHEAT = 1;
  public static final byte TRAP_FAN_FAIL1 = 2;
  public static final byte TRAP_FAN_FAIL2 = 3;
  public static final byte TRAP_FAN_FAIL3 = 4;
  public static final byte TRAP_PST_OUT_OF_RANGE = 1;
  public static final byte TRAP_PST_FAIL_48A = 2;
  public static final byte TRAP_PST_FAIL_48B = 3;
  public static final byte TRAP_PSA_FAIL_A = 2;
  public static final byte TRAP_PSA_FAIL_B = 1;
  public static final byte TRAP_CMB_LOS_SFP1 = 37;
  public static final byte TRAP_CMB_LOF_SFP1 = 38;
  public static final byte TRAP_CMB_FAIL_SFP1 = 38;
  public static final byte TRAP_CMB_LASEROFF_SFP1 = 40;
  public static final byte TRAP_CMB_LOS_SFP2 = 41;
  public static final byte TRAP_CMB_LOF_SFP2 = 42;
  public static final byte TRAP_CMB_FAIL_SFP2 = 42;
  public static final byte TRAP_CMB_LASEROFF_SFP2 = 44;
  public static final byte TRAP_CMB_LOS_SFP3 = 45;
  public static final byte TRAP_CMB_LOF_SFP3 = 46;
  public static final byte TRAP_CMB_FAIL_SFP3 = 46;
  public static final byte TRAP_CMB_LASEROFF_SFP3 = 48;
  public static final byte TRAP_CMB_LOS_SFP4 = 49;
  public static final byte TRAP_CMB_LOF_SFP4 = 50;
  public static final byte TRAP_CMB_FAIL_SFP4 = 50;
  public static final byte TRAP_CMB_LASEROFF_SFP4 = 52;
  public static final byte TRAP_CMB_LOS_SFP5 = 72;
  public static final byte TRAP_CMB_LOF_SFP5 = 73;
  public static final byte TRAP_CMB_FAIL_SFP5 = 74;
  public static final byte TRAP_CMB_LASEROFF_SFP5 = 75;
  public static final byte TRAP_CMB_LOS_SFP6 = 76;
  public static final byte TRAP_CMB_LOF_SFP6 = 77;
  public static final byte TRAP_CMB_FAIL_SFP6 = 78;
  public static final byte TRAP_CMB_LASEROFF_SFP6 = 79;
  public static final byte TRAP_CMB_LOS_SFP7 = 80;
  public static final byte TRAP_CMB_LOF_SFP7 = 81;
  public static final byte TRAP_CMB_FAIL_SFP7 = 82;
  public static final byte TRAP_CMB_LASEROFF_SFP7 = 83;
  public static final byte TRAP_CMB_LOS_SFP8 = 84;
  public static final byte TRAP_CMB_LOF_SFP8 = 85;
  public static final byte TRAP_CMB_FAIL_SFP8 = 86;
  public static final byte TRAP_CMB_LASEROFF_SFP8 = 87;
  public static final byte TRAP_CMB_LOS_SYNC_SFP1 = 53;
  public static final byte TRAP_CMB_LOS_SYNC_SFP2 = 54;
  public static final byte TRAP_CMB_LOS_SYNC_SFP3 = 55;
  public static final byte TRAP_CMB_LOS_SYNC_SFP4 = 56;
  public static final byte TRAP_CMB_LOS_SYNC_SFP5 = 88;
  public static final byte TRAP_CMB_LOS_SYNC_SFP6 = 89;
  public static final byte TRAP_CMB_LOS_SYNC_SFP7 = 90;
  public static final byte TRAP_CMB_LOS_SYNC_SFP8 = 91;
  public static final byte TRAP_CMB_REMOVED_SFP1 = 61;
  public static final byte TRAP_CMB_CHANGED_SFP1 = 62;
  public static final byte TRAP_CMB_REMOVED_SFP2 = 63;
  public static final byte TRAP_CMB_CHANGED_SFP2 = 64;
  public static final byte TRAP_CMB_REMOVED_SFP3 = 65;
  public static final byte TRAP_CMB_CHANGED_SFP3 = 66;
  public static final byte TRAP_CMB_REMOVED_SFP4 = 67;
  public static final byte TRAP_CMB_CHANGED_SFP4 = 68;
  public static final byte TRAP_CMB_REMOVED_SFP5 = 92;
  public static final byte TRAP_CMB_CHANGED_SFP5 = 93;
  public static final byte TRAP_CMB_REMOVED_SFP6 = 94;
  public static final byte TRAP_CMB_CHANGED_SFP6 = 95;
  public static final byte TRAP_CMB_REMOVED_SFP7 = 96;
  public static final byte TRAP_CMB_CHANGED_SFP7 = 97;
  public static final byte TRAP_CMB_REMOVED_SFP8 = 98;
  public static final byte TRAP_CMB_CHANGED_SFP8 = 99;

  public boolean equals(Object o)
  {
    if (!(o instanceof PPM2v2)) {
      return false;
    }
    PPM2v2 o2 = (PPM2v2)o;

    return ((this.command != o2.command) || (this.parameter != o2.parameter) || 
      (this.dataSize != o2.dataSize) || 
      (!(Arrays.equals(this.source, o2.source))) || 
      (!(Arrays.equals(this.destiny, o2.destiny))) || 
      (!(Arrays.equals(this.data, o2.data))));
  }

  public PPM2v2(byte comando, byte parametro, byte[] origem, byte[] destino, byte[] dados)
  {
    if (dados == null) {
      this.dataSize = 0;
      this.data = null;
    } else {
      this.dataSize = dados.length;
      this.data = new byte[this.dataSize];
      System.arraycopy(dados, 0, this.data, 0, this.dataSize);
    }
    this.command = comando;
    this.parameter = parametro;
    if (origem != null) {
      System.arraycopy(origem, 0, this.source, 0, ADDR_SIZE);
    }

    if (destino != null)
      System.arraycopy(destino, 0, this.destiny, 0, ADDR_SIZE);
  }

  public PPM2v2(byte comando, byte parametro, byte[] origem, byte[] destino, byte[] dados, byte cmd_original, byte param_original)
    throws BadPackageException
  {
    if (comando != 3) {
      throw new BadPackageException(
        "Tentativa de Comando Extendido diferente de Response!");
    }
    if (dados == null) {
      this.dataSize = 5; } else {
      if (dados.length > 255) {
        throw new BadPackageException("Oversized Data");
      }
      this.dataSize = (dados.length + 5);
    }
    this.data = new byte[this.dataSize];
    this.data[0] = parametro;
    this.data[1] = cmd_original;
    this.data[2] = param_original;
    for (int i = 3; i < 5; ++i) {
      this.data[i] = -1;
    }
    if (dados != null) {
      System.arraycopy(dados, 0, this.data, 5, dados.length);
    }
    this.command = 3;
    this.parameter = -1;
    if (origem != null) {
      if (origem.length != 5) {
        throw new BadPackageException("Wrong Origin Size");
      }
      System.arraycopy(origem, 0, this.source, 0, 5);
    }

    if (destino != null) {
      if (destino.length != 5) {
        throw new BadPackageException("Wrong Destiny Size");
      }
      System.arraycopy(destino, 0, this.destiny, 0, 5);
    }
  }

  public PPM2v2()
  {
  }

  public PPM2v2(byte[] pacote) throws BadPackageException
  {
    if (pacote == null)
      throw new BadPackageException("Null Pointer: packet null");
    if (pacote.length < 15)
      throw new BadPackageException("Packet Too Small, packet's length: " + pacote.length);
    if (pacote[0] != version)
      throw new BadPackageException("Bad Version");
    this.dataSize = Functions.b2i(pacote[2]);
    if (this.dataSize > 255) {
      throw new BadPackageException("Oversized Data");
    }
    this.command = pacote[3];
    this.parameter = pacote[4];
    System.arraycopy(pacote, 5, this.source, 0, 5);
    System.arraycopy(pacote, 10, this.destiny, 0, 5);
    if (this.dataSize > 0) {
      this.data = new byte[this.dataSize];
      System.arraycopy(pacote, 15, this.data, 0, this.dataSize);
    } else {
      this.data = null;
    }
  }

  public int getPacketSize()
  {
    return (getDataSize() + 15);
  }

  public int getDataSize()
  {
    return ((this.data == null) ? 0 : this.dataSize);
  }

  public byte[] getDataArray()
  {
    if (this.data == null) {
      return new byte[0];
    }
    return ((byte[])this.data.clone());
  }

  private byte[] getDataBytes()
  {
    return this.data;
  }

  public byte getCommand()
  {
    return this.command;
  }

  public byte getParameter()
  {
    return this.parameter;
  }

  public int getSource(byte[] arrayTo)
  {
    if (arrayTo.length != this.source.length) {
      return 0;
    }
    System.arraycopy(this.source, 0, arrayTo, 0, this.source.length);
    return this.source.length;
  }

  public byte[] getSource()
  {
    return this.source;
  }

  public int getDestiny(byte[] arrayTo)
  {
    if (arrayTo.length != this.destiny.length) {
      return 0;
    }
    System.arraycopy(this.source, 0, arrayTo, 0, this.destiny.length);
    return this.destiny.length;
  }

  public byte[] getDestiny()
  {
    return this.destiny;
  }

  public static int getSiteFromAddress(byte[] dest)
  {
    return dest[0];
  }

  public static SerialNumber getSerialFromAddress(byte[] dest)
  {
    int p = Functions.b2i(dest[1]) * 256 + Functions.b2i(dest[2]);
    int s = Functions.b2i(dest[3]) * 256 + Functions.b2i(dest[4]);
    return new SerialNumber(p, s);
  }

  static byte[] getAddress(int site, SerialNumber serial)
  {
    byte[] buf = new byte[5];
    buf[0] = (byte)site;
    buf[1] = (byte)(serial.getPart() / 256);
    buf[2] = (byte)(serial.getPart() % 256);
    buf[3] = (byte)(serial.getSeq() / 256);
    buf[4] = (byte)(serial.getSeq() % 256);
    return buf;
  }

  static byte[] getTimeData(long t)
  {
    byte[] time = new byte[10];
    time[7] = (byte)(int)(t >> 0);
    time[6] = (byte)(int)(t >> 8);
    time[5] = (byte)(int)(t >> 16);
    time[4] = (byte)(int)(t >> 24);
    time[3] = (byte)(int)(t >> 32);
    time[2] = (byte)(int)(t >> 40);
    time[1] = (byte)(int)(t >> 48);
    time[0] = (byte)(int)(t >> 56);
    time[8] = 0;
    time[9] = 0;
    return time;
  }

  public byte[] getRawBytes()
  {
    byte[] stream = new byte[this.dataSize + 15];
    stream[0] = version;

    stream[1] = 0;
    stream[2] = (byte)this.dataSize;
    stream[3] = getCommand();
    stream[4] = getParameter();
    byte[] origem = getSource();
    byte[] destino = getDestiny();
    System.arraycopy(origem, 0, stream, 5, 5);
    System.arraycopy(destino, 0, stream, 10, 5);
    byte[] dados = getDataBytes();
    if (this.dataSize > 0) {
      System.arraycopy(dados, 0, stream, 15, this.dataSize);
    }
    return stream;
  }

  public String toString()
  {
    StringBuffer buf = new StringBuffer("PPM2v2 ");
    switch (this.command)
    {
    case 1:
      buf.append("GET");
      break;
    case 2:
      buf.append("SET");
      break;
    case 4:
      buf.append("TRAP");
      break;
    case 3:
      buf.append("RESPONSE");
      break;
    case 5:
      buf.append("NOTIFICATION");
    }

    buf.append(" par:");
    buf.append(Functions.getHexa(this.parameter));
    buf.append(" From [");
    buf.append(Functions.b2i(this.source[0]));
    buf.append(',');
    buf.append(Functions.b2i(this.source[1]) * 256 + Functions.b2i(this.source[2]));
    buf.append(',');
    buf.append(Functions.b2i(this.source[3]) * 256 + Functions.b2i(this.source[4]));
    buf.append("] To [");
    buf.append(Functions.b2i(this.destiny[0]));
    buf.append(',');
    buf.append(Functions.b2i(this.destiny[1]) * 256 + Functions.b2i(this.destiny[2]));
    buf.append(',');
    buf.append(Functions.b2i(this.destiny[3]) * 256 + Functions.b2i(this.destiny[4]));
    buf.append(']');

    buf.append(" data[");
    buf.append(Functions.getHexa(getDataBytes()));
    buf.append("]");

    return buf.toString();
  }

  public byte getNonExtendedParameter()
  {
    if (((getCommand() == 2) && (getParameter() == -1)) || (
      (getCommand() == 3) && (getParameter() == -1))) {
      return getDataArray()[0];
    }
    return -2;
  }

  public byte[] getNonExtendedResponseDataArray()
  {
    if (getDataArray().length > 5) {
      byte[] dados = new byte[getDataArray().length - 5];
      System.arraycopy(getDataArray(), 5, dados, 0, 
        dados.length);
      return dados;
    }
    return new byte[0];
  }

  public byte[] getResponseExtendArray()
  {
    if (getParameter() == -1) {
      byte[] dados = new byte[5];
      System.arraycopy(getDataArray(), 0, dados, 0, 5);
      return dados;
    }
    return new byte[0];
  }

  public static String getGetName(Byte param) {
    Field[] arrayOfField1;
    Field[] fieldList = PPM2v2.class.getFields();
    int j = (arrayOfField1 = fieldList).length; 
    int i = 0; 
    	Field field = arrayOfField1[i];
    if (field.getName().startsWith("GET_"));
    try {
      Object value = field.get(null);
      if (!(param.equals(value)))
    	 return field.getName();
    } catch (Exception e) {
      do {
        System.out.println(e.toString());

        ++i; } while (i < j);
    }

    return param.toString();
  }
}