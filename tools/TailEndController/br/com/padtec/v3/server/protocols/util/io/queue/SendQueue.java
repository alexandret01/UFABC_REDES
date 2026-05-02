package br.com.padtec.v3.server.protocols.util.io.queue;

import br.com.padtec.v3.server.protocols.util.io.readerwriter.AbstractReaderWriter;

public abstract interface SendQueue {
  public abstract boolean hasNextPacket();

  public abstract byte[] nextPacket();

  public abstract void notifyNextPacketSent(AbstractReaderWriter paramAbstractReaderWriter);

  public abstract void clear();

  public abstract void removePacket(Object paramObject);
}