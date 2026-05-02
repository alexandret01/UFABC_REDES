package br.com.padtec.v3.server.protocols.ppm2v2.handler;

import java.util.LinkedList;
import java.util.List;

import br.com.padtec.v3.data.impl.NE_Impl;
import br.com.padtec.v3.server.protocols.ppm2v2.PPM2v2;
import br.com.padtec.v3.server.protocols.ppm2v2.PPM2v2Helper;

public abstract class AbstractHandler implements HandlerInterface<PPM2v2> {
  public List<PPM2v2> getUpdatePacketList(NE_Impl ne)
  {
    List<PPM2v2> result = new LinkedList<PPM2v2>();
    byte[] destino = PPM2v2Helper.getBytes(ne);
    PPM2v2 sendPacket = new PPM2v2((byte)1, (byte)0, null, destino, null);
    result.add(sendPacket);
    return result;
  }
}