package br.com.padtec.v3.server.protocols.ppm3.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.com.padtec.v3.data.Notification;
import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.impl.NE_Impl;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3;

public class HandlerInterfaceFactory
{
  private static List<HandlerInterface<PPM3>> handlerList = new ArrayList<HandlerInterface<PPM3>>();
  private static HandlerInterface<PPM3> nullHandler;

  static  {
//    handlerList.add(new Transponder10GRegeneratorHandler());
    handlerList.add(new Transponder10GTerminalHandler());
//    handlerList.add(new Transponder25OTNHandler());
//    handlerList.add(new Transponder25FecRxHandler());
//    handlerList.add(new CombinerHandler());
//    handlerList.add(new MuxponderHandler());
//    handlerList.add(new AmplifierHandler());
//    handlerList.add(new SupSpvjFilhoHandler());
    for (GenericHandler generic : GenericHandlerFactory.getHandlerList()) {
      handlerList.add(new HandlerInterfaceAdapter(generic));
    }
//    handlerList.add(new TransponderDWDMHandler());
//    handlerList.add(new FanHandler());
//    handlerList.add(new OpticalProtectionHandler());
//    handlerList.add(new MediaConverterHandler());

    nullHandler = new HandlerInterface<PPM3>()   {
      public boolean canHandle(NE_Impl ne) {
        return true;
      }

      public List<PPM3> getUpdatePacketList(NE_Impl ne) {
        return new ArrayList<PPM3>(0);
      }

      public boolean onReceiveResponse(NE_Impl ne, PPM3 pacote, Collection<Notification> alarmList)
      {
        return true;
      }

      public boolean onReceiveTrap(NE_Impl ne, PPM3 trap, List<PPM3> packetToSend, List<Notification> event, boolean history)
      {
        return true;
      }

      public boolean prepareFullUpdate(NE_Impl ne) {
        return true;
      }

      public boolean isFullUpdated(SerialNumber serial) {
        return true;
      }
    };
  }

  public static HandlerInterface<PPM3> getHandler(NE_Impl ne) {
    if (ne != null) {
      for (HandlerInterface<PPM3> handler : handlerList) {
        if (handler.canHandle(ne)) {
          return handler;
        }
      }
    }
    return nullHandler;
  }
}