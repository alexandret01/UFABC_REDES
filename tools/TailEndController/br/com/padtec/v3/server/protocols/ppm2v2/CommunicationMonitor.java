package br.com.padtec.v3.server.protocols.ppm2v2;

import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.server.protocols.util.io.AbstractCommunicationMonitor;

public final class CommunicationMonitor extends AbstractCommunicationMonitor<PPM2v2>
{
  private static final CommunicationMonitor instance = new CommunicationMonitor();

  public static CommunicationMonitor getInstance() {
    return instance;
  }

  public void notifyPacket(String rwId, PPM2v2 packet, SerialNumber serial)
  {
    super.log(rwId, serial, packet);
  }
}