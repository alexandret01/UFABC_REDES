package br.com.padtec.v3.server.protocols.ppm3;


import java.util.LinkedList;
import java.util.List;

import br.com.padtec.v3.data.Command;
import br.com.padtec.v3.data.ne.NE;
import br.com.padtec.v3.data.ne.Ne16Bit;
import br.com.padtec.v3.server.CommandFactory;
import br.com.padtec.v3.server.protocols.ppm3.packet.HasTlv;
import br.com.padtec.v3.server.protocols.ppm3.packet.InvalidValueException;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3Response;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3Trap;
import br.com.padtec.v3.server.protocols.ppm3.packet.TimeTLV;
import br.com.padtec.v3.util.Functions;

public final class Ppm3Helper {
//  public static List<Alarm> alarmFilter(List<Notification> oldList, List<Notification> newList)
//  {
//    Notification n;
//    List result = new LinkedList();
//    for (Iterator localIterator = newList.iterator(); localIterator.hasNext(); ) { n = (Notification)localIterator.next();
//      if (n instanceof Alarm) {
//        result.add((Alarm)n);
//      }
//    }
//    for (localIterator = oldList.iterator(); localIterator.hasNext(); ) { n = (Notification)localIterator.next();
//      if (n instanceof Alarm) {
//        Alarm item = (Alarm)n;
//        for (int i = 0; i < result.size(); ++i) {
//          Alarm item2 = (Alarm)result.get(i);
//          if ((item.getAlType() != item2.getAlType()) || 
//            (item.isCleared() != item2.isCleared()) || 
//            (!(Functions.equals(item.getContact(), item2.getContact()))) || 
//            (!(Functions.equals(item.getNeOrigin(), item2.getNeOrigin())))) continue;
//          result.remove(i);
//          break;
//        }
//      }
//    }
//
//    return result;
//  }

  public static List<PPM3Response> trapToResponse(PPM3Trap trap) {
    List<PPM3Response> result = new LinkedList<PPM3Response>();
    for (int i = 0; i < trap.getTLVCount(); ++i) {
      TimeTLV tlv = trap.getTLV(i);
      try {
        PPM3Response response = new PPM3Response(trap.getSerial(), 
          tlv.getTimestamp(), tlv.getTypeAsInt(), tlv.getValue());
        result.add(response);
      }
      catch (InvalidValueException e) {
        e.printStackTrace();
      }
    }
    return result;
  }

  public static Command ppm32Command(NE ne, byte[] type, byte[] data) {
    if (!(ne instanceof Ne16Bit)) {
      return CommandFactory.createOriginalCommand(ne, Functions.b2i(type[1]), 
        data);
    }
    return CommandFactory.createOriginalCommand(ne, 
      (int)Functions.b2l(type), data);
  }

  public static TimeTLV getNewestTlv(HasTlv<TimeTLV> payload)
  {
    if (payload.getTLVCount() == 0) {
      return null;
    }
    TimeTLV newestTlv = (TimeTLV)payload.getTLV(0);
    for (int i = 1; i < payload.getTLVCount(); ++i) {
      TimeTLV currentTlv = (TimeTLV)payload.getTLV(i);
      if (newestTlv.getTimestamp() < currentTlv.getTimestamp()) {
        newestTlv = currentTlv;
      }
    }
    return newestTlv;
  }
}