package br.com.padtec.v3.server.protocols.ppm2v2;

import java.util.Set;
import java.util.TreeMap;

public class IoSystem{
  private static final IoSystem instance = new IoSystem();

  private final TreeMap<String, ReaderWriter> connectionPool = new TreeMap<String, ReaderWriter>();

  public static IoSystem getInstance()
  {
    return instance;
  }

  public synchronized ReaderWriter createConnection(String ip, int port)
  {
    String id = ip + ":" + port;

    ReaderWriter result = new ReaderWriter(ip, port);
    this.connectionPool.put(id, result);
    return result;
  }

  public synchronized Set<String> getIdList() {
    return this.connectionPool.keySet();
  }

  public synchronized ReaderWriter getConnection(String id) {
    return ((ReaderWriter)this.connectionPool.get(id));
  }
}