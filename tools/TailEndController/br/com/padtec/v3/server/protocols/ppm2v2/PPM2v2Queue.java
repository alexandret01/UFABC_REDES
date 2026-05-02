package br.com.padtec.v3.server.protocols.ppm2v2;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

public class PPM2v2Queue{
  private final LinkedBlockingQueue<PPM2v2> queue = new LinkedBlockingQueue<PPM2v2>();

  public synchronized void add(PPM2v2 packet)
  {
    this.queue.offer(packet);
  }

  public synchronized void addAll(Collection<PPM2v2> list)
  {
    for (PPM2v2 item : list)
      add(item);
  }

  public boolean contains(PPM2v2 packet)
  {
    return this.queue.contains(packet);
  }

  public boolean isEmpty()
  {
    return this.queue.isEmpty();
  }

  public PPM2v2 getNext()
  {
    return ((PPM2v2)this.queue.peek());
  }

  public PPM2v2 remove()
  {
    return ((PPM2v2)this.queue.poll());
  }
}