package br.com.padtec.v3.server.protocols.util.io.queue;

import br.com.padtec.v3.server.protocols.util.io.readerwriter.AbstractReaderWriter;

public abstract interface ReceiveQueue {
	
  public abstract void addByte(AbstractReaderWriter paramAbstractReaderWriter, int paramInt);

  public abstract void resetState();
}