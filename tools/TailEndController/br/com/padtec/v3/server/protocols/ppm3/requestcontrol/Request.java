package br.com.padtec.v3.server.protocols.ppm3.requestcontrol;

import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;

public class Request<T>{
  private T packet;
  private long timeout;
  private RequestEventHandler<T> handler;
  private SortedMap<Long, String> sendHistory = new TreeMap<Long, String>();

  private SortedMap<Long, String> queueHistory = new TreeMap<Long, String>();
  private final long creationTime;

  public Request(T packet, RequestEventHandler<T> handler, long timeout)  {
    this.packet = packet;
    this.handler = handler;
    this.timeout = timeout;
    this.creationTime = System.currentTimeMillis();
  }

  public T getPacket()  {
    return this.packet;
  }

  public SortedMap<Long, String> getSendHistory()
  {
    return this.sendHistory;
  }

  public Long getLastSend() {
    try {
      return ((Long)this.sendHistory.lastKey()); } catch (NoSuchElementException e) {
    }
    return null;
  }

  public SortedMap<Long, String> getQueueHistory()
  {
    return this.queueHistory;
  }

  public Long getLastEnqueue() {
    if (this.queueHistory.isEmpty()) {
      return null;
    }
    return ((Long)this.queueHistory.lastKey());
  }

  public boolean isTimedOut() {
    if (this.sendHistory.isEmpty()) {
      return false;
    }
    return 
      (((Long)this.sendHistory.lastKey()).longValue() + this.timeout >= System.currentTimeMillis());
  }

  public void notifyPacketEnqueued(String rwId) {
    this.queueHistory.put(Long.valueOf(System.currentTimeMillis()), rwId);
  }

  public void notifyPacketSent(String rwId)
  {
    this.sendHistory.put(Long.valueOf(System.currentTimeMillis()), rwId);
  }

  public RequestEventHandler<T> getHandler()
  {
    return this.handler;
  }

  public String toString()
  {
    return "Request[packet=" + this.packet;
  }

  public long getCreationTime()
  {
    return this.creationTime;
  }

  public long getTimeout()
  {
    return this.timeout;
  }
}