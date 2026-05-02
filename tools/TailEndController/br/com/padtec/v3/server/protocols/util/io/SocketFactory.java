package br.com.padtec.v3.server.protocols.util.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SocketFactory {
  private static SocketFactory instance = new SocketFactory();

//  private final int MAX_PARALEL = (SoUtils.isWindows()) ? 8 : 100;
  private final int MAX_PARALEL = 100;

  private final List<Thread> waitingQueue = Collections.synchronizedList(new ArrayList<Thread>());

  public static SocketFactory getInstance()
  {
    return instance;
  }

  public Socket newSocket(InetAddress address, int port)
    throws UnknownHostException, IOException  {
    Thread currentThread = Thread.currentThread();
    try    {
      int idx;
      this.waitingQueue.add(currentThread);

      while ((idx = this.waitingQueue.indexOf(currentThread)) >= this.MAX_PARALEL)
        try {
          Thread.sleep(200L);
        }
        catch (InterruptedException localInterruptedException) {
        }
      if (idx == -1)
      {
        throw new IOException("Connection attempt cancelled");
      }
      return new Socket(address, port);
    } finally {
      this.waitingQueue.remove(currentThread);
    }
  }

  public void cancel(Thread newSocketThread) {
    if (this.waitingQueue.remove(newSocketThread))
      newSocketThread.interrupt();
  }
}