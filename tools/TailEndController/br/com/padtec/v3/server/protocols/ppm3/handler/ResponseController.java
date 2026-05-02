package br.com.padtec.v3.server.protocols.ppm3.handler;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import br.com.padtec.v3.data.SerialNumber;

public class ResponseController
{
  private Map<Integer, Set<SerialNumber>> controller = new TreeMap<Integer, Set<SerialNumber>>();

  public synchronized void registerCode(Integer code) {
    Set<SerialNumber> codeBoards = this.controller.get(code);
    if (codeBoards == null)
      this.controller.put(code, new TreeSet<SerialNumber>());
  }

  public synchronized void registerArrival(Integer responseCode, SerialNumber serial)
  {
    Set<SerialNumber> codeBoards = this.controller.get(responseCode);
    if (codeBoards == null) {
      codeBoards = new TreeSet<SerialNumber>();
      this.controller.put(responseCode, codeBoards);
    }
    codeBoards.add(serial);
  }

  public synchronized boolean hasArrived(Integer responseCode, SerialNumber serial)
  {
    Set<SerialNumber> codeBoards = this.controller.get(responseCode);
    if (codeBoards == null) {
      return false;
    }
    return codeBoards.contains(serial);
  }

  public synchronized boolean hasAllArrivals(SerialNumber serial)
  {
    for (Set<SerialNumber> codeBoards : this.controller.values()) {
      if (!(codeBoards.contains(serial))) {
        return false;
      }
    }
    return true;
  }

  public synchronized void clearArrival(SerialNumber serial)
  {
    for (Set<SerialNumber> codeBoards : this.controller.values())
      codeBoards.remove(serial);
  }
}