
package br.com.padtec.v3.server.protocols.ppm2v2.handler;

import java.util.Collection;
import java.util.List;

import br.com.padtec.v3.data.Notification;
import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.impl.NE_Impl;
import br.com.padtec.v3.server.protocols.ppm2v2.ColetorPPM2v2;

public abstract interface HandlerInterface<T> {
  public abstract boolean canHandle(NE_Impl paramNE_Impl);

  public abstract List<T> getUpdatePacketList(NE_Impl paramNE_Impl);

  public abstract boolean prepareFullUpdate(NE_Impl paramNE_Impl);

  public abstract boolean onReceiveTrap(NE_Impl paramNE_Impl, T paramT, List<T> paramList, List<Notification> paramList1);

  public abstract boolean onReceiveResponse(ColetorPPM2v2 paramColetorPPM2v2, NE_Impl paramNE_Impl, T paramT, byte paramByte, Collection<Notification> paramCollection);

  public abstract List<Notification> getAlarmList(NE_Impl paramNE_Impl);

  public abstract boolean isFullUpdated(SerialNumber paramSerialNumber);
}