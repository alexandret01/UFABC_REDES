package br.com.padtec.v3.server.protocols.ppm3.handler;

import java.util.Collection;
import java.util.List;

import br.com.padtec.v3.data.Notification;
import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.impl.NE_Impl;



public abstract interface HandlerInterface<T> {
	
  public abstract boolean canHandle(NE_Impl paramNE_Impl);

  public abstract List<T> getUpdatePacketList(NE_Impl paramNE_Impl);

  public abstract boolean prepareFullUpdate(NE_Impl paramNE_Impl);

  public abstract boolean onReceiveTrap(NE_Impl paramNE_Impl, T paramT, List<T> paramList, List<Notification> paramList1, boolean paramBoolean);

  public abstract boolean onReceiveResponse(NE_Impl paramNE_Impl, T paramT, Collection<Notification> paramCollection);

  public abstract boolean isFullUpdated(SerialNumber paramSerialNumber);
}