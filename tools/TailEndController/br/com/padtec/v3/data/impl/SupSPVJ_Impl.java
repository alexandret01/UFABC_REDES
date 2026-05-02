package br.com.padtec.v3.data.impl;

import java.util.List;

import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.RackAddress.RackAddress;
import br.com.padtec.v3.data.ne.SupSPVJ;
import br.com.padtec.v3.util.ArrayUtils;
import br.com.padtec.v3.util.Functions;


public class SupSPVJ_Impl  extends Supervisor_Impl implements SupSPVJ {
  private static final long serialVersionUID = 3L;
  private boolean isBlocked = true;
  private boolean isLOS1;
  private boolean isLOS2;
  private String ip;
  private byte[] supConf;
  private byte[] rackConf = new byte[BUFFER_SIZE];
  private List<RackAddress> racks;
  private String gateway;
  private String mask;
  private int slot;
  private int canError = 0;
  private int tintError = 0;
  private int crcError = 0;
  private int canTotal = 0;
  private int tintTotal = 0;
  private int ppm2v2Total = 0;
  private int tokenTimeouts = 0;
  private int roBuffer = 0;
  protected String neName;

  public SupSPVJ_Impl(SerialNumber serial)
  {
    super(serial);
  }

  public void setBlocked(boolean status) {
    this.isBlocked = status;
  }

  public void setLOS1(boolean status) {
    this.isLOS1 = status;
  }

  public void setLOS2(boolean status) {
    this.isLOS2 = status;
  }

  public void setIP(String ip) {
    this.ip = ip;
  }

  public boolean isBlocked() {
    return this.isBlocked;
  }

  public boolean isLOS1() {
    return this.isLOS1;
  }

  public boolean isLOS2() {
    return this.isLOS2;
  }

  public String getIP() {
    return this.ip;
  }

  public boolean setSupConf(byte[] data, String newName)
  {
    this.supConf = data;
    String oldName = this.neName;
    this.neName = newName;
    return (!(Functions.equals(oldName, newName)));
  }

  public boolean setSupConf(byte[] data)
  {
    this.supConf = data;
    return false;
  }

  public String getNEName()
  {
    return this.neName;
  }

  public void setNeName(String neName)
  {
    this.neName = neName;
  }

  public boolean isRackSync() {
    return (this.racks == null);
  }

  public int getNENameLimit()
  {
    if (Functions.compareVersions(getVersion(), "1.1.20") <= 0) {
      return 20;
    }
    return 64;
  }

  public Integer getMaxsites()
  {
    return new Integer(this.supConf[7]);
  }

  public Integer getMaxTrp()
  {
    return ((this.supConf == null) ? null : Integer.valueOf(this.supConf[8]));
  }

  public Integer getMaxAmp()
  {
    return ((this.supConf == null) ? null : Integer.valueOf(this.supConf[9]));
  }

  public Integer getMaxCho()
  {
    return ((this.supConf == null) ? null : Integer.valueOf(this.supConf[10]));
  }

  public Integer getMaxShk()
  {
    return ((this.supConf == null) ? null : Integer.valueOf(this.supConf[11]));
  }

  public Boolean isTokenTimeOut()
  {
    return ((this.supConf == null) ? null : Boolean.valueOf(this.supConf[12] != 0));
  }

  public Boolean isMasterSlave()
  {
    return ((this.supConf == null) ? null : Boolean.valueOf(this.supConf[13] != 0));
  }

  public Boolean isAgc()
  {
    return ((this.supConf == null) ? null : Boolean.valueOf(this.supConf[15] != 0));
  }

  public Boolean isOTN()
  {
    return ((this.supConf == null) ? null : Boolean.valueOf(this.supConf[16] != 0));
  }

  public Integer getMaxFan()
  {
    return ((this.supConf == null) ? null : Integer.valueOf(this.supConf[17]));
  }

  public Integer getMaxPst()
  {
    return ((this.supConf == null) ? null : Integer.valueOf(this.supConf[18]));
  }

  public Boolean isAmplifierAls()
  {
    return ((this.supConf == null) ? null : Boolean.valueOf(this.supConf[19] != 0));
  }

  public Integer getMaxMux()
  {
    return ((this.supConf == null) ? null : Integer.valueOf(this.supConf[20]));
  }

  public void setMask(String string) {
    this.mask = string;
  }

  public void setGateway(String string) {
    this.gateway = string;
  }

  public String getGateway()
  {
    return this.gateway;
  }

  public String getMask()
  {
    return this.mask;
  }

  public byte[] getSupConf() {
    return this.supConf;
  }

  public int getSlot()
  {
    return 11;
  }

  public int getSubBastidor() {
    if (this.slot % 10 == 0) {
      return (this.slot / 10);
    }
    return (int)(Math.floor(this.slot / 10.0D) + 1.0D);
  }

  public int getSubBastidorSlot()
  {
    return 11;
  }

  public boolean setSupRackConf(byte[] data)
  {
    if (!(ArrayUtils.startsWith(this.rackConf, data))) {
      System.arraycopy(data, 0, this.rackConf, 0, data.length);
      return true;
    }
    return false;
  }

  public void setRackAddress(List<RackAddress> racksFromHandler)
  {
    this.racks = racksFromHandler;
  }

  public List<RackAddress> getRackAddress() {
    return this.racks;
  }

  public int getCanError() {
    return this.canError;
  }

  public void setCanError(int canError) {
    this.canError = canError;
  }

  public int getCanTotal() {
    return this.canTotal;
  }

  public void setCanTotal(int canTotal) {
    this.canTotal = canTotal;
  }

  public int getCrcError() {
    return this.crcError;
  }

  public void setCrcError(int crcError) {
    this.crcError = crcError;
  }

  public int getPPM2v2Total() {
    return this.ppm2v2Total;
  }

  public void setPPM2v2Total(int crcTotal) {
    this.ppm2v2Total = crcTotal;
  }

  public int getTintError() {
    return this.tintError;
  }

  public void setTintError(int tintError) {
    this.tintError = tintError;
  }

  public int getTintTotal() {
    return this.tintTotal;
  }

  public void setTintTotal(int tintTotal) {
    this.tintTotal = tintTotal;
  }

  public int getRoBuffer() {
    return this.roBuffer;
  }

  public void setRoBuffer(int roBuffer) {
    this.roBuffer = roBuffer;
  }

  public int getTokenTimeouts() {
    return this.tokenTimeouts;
  }

  public void setTokenTimeouts(int tokenTimeouts) {
    this.tokenTimeouts = tokenTimeouts;
  }

  public void removeElementFromConfig(SerialNumber serial) {
    byte[] rackConfAux = new byte[150];
    int tamanhoAux = 1;
    int tamanho = Functions.b2i(this.rackConf[0]);
    int i = 1;
    if ((tamanho > 0) && (tamanho != 255)) {
      while (i <= tamanho) {
        int type = Functions.b2i(this.rackConf[i]);
        ++i;

        int chave = (int)Functions.b2l(this.rackConf, i, 4);
        i += 4;

        int part = (int)Functions.b2l(this.rackConf, i, 2);
        i += 2;

        int serialN = (int)Functions.b2l(this.rackConf, i, 2);
        i += 2;
        SerialNumber serialTmp = new SerialNumber(part, serialN);
        if (serial.compareTo(serialTmp) != 0) {
          rackConfAux[tamanhoAux] = Functions.i2b(type);
          ++tamanhoAux;
          rackConfAux[tamanhoAux] = Functions.l2b(chave, 4)[0];
          ++tamanhoAux;
          rackConfAux[tamanhoAux] = Functions.l2b(chave, 4)[1];
          ++tamanhoAux;
          rackConfAux[tamanhoAux] = Functions.l2b(chave, 4)[2];
          ++tamanhoAux;
          rackConfAux[tamanhoAux] = Functions.l2b(chave, 4)[3];
          ++tamanhoAux;
          rackConfAux[tamanhoAux] = Functions.l2b(part, 2)[0];
          ++tamanhoAux;
          rackConfAux[tamanhoAux] = Functions.l2b(part, 2)[1];
          ++tamanhoAux;
          rackConfAux[tamanhoAux] = Functions.l2b(serialN, 2)[0];
          ++tamanhoAux;
          rackConfAux[tamanhoAux] = Functions.l2b(serialN, 2)[1];
          ++tamanhoAux;
        }
      }
    }
    --tamanhoAux;
    rackConfAux[0] = Functions.i2b(tamanhoAux);
    setSupRackConf(rackConfAux);
  }

  public Integer getMaxMco() {
    if (Functions.compareVersions(getVersion(), "2.14") >= 0) {
      return ((this.supConf == null) ? null : Integer.valueOf(this.supConf[21]));
    }
    return null;
  }
}