package br.com.padtec.v3.data.impl;

import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.ne.Bidirectional;
import br.com.padtec.v3.data.ne.TrpBidirecional;

public class TrpGBEthD_Impl extends TrpDWDM_Impl  implements TrpBidirecional, Bidirectional {
  private static final long serialVersionUID = 3L;
  private boolean los2;
  private boolean laserOff2;
  private Boolean autoLaserOff2;

  public TrpGBEthD_Impl(SerialNumber serial)
  {
    super(serial);
  }

  public boolean isLaserOff2()
  {
    return this.laserOff2;
  }

  public void setLaserOff2(boolean laserOff2)
  {
    this.laserOff2 = laserOff2;
  }

  public boolean isLos2()
  {
    return this.los2;
  }

  public void setLos2(boolean los2)
  {
    this.los2 = los2;
  }

  public int getStyle()
  {
    return 3;
  }

  public Boolean getAutoLaserOff2() {
    return this.autoLaserOff2;
  }

  public void setAutoLaserOff2(Boolean autoLaserOff2) {
    this.autoLaserOff2 = autoLaserOff2;
  }
}