/*     */ package br.com.padtec.v3.server.protocols.util.io.readerwriter;
/*     */ 
/*     */ import java.io.IOException;

import br.com.padtec.v3.server.protocols.util.io.AbstractConnectionAlarmManager;
import br.com.padtec.v3.server.protocols.util.io.queue.ReceiveQueue;
import br.com.padtec.v3.server.protocols.util.io.queue.SendQueue;

/*     */ 
/*     */ public class SerialReaderWriter extends AbstractReaderWriter
/*     */ {

	protected SerialReaderWriter(String connection, ReceiveQueue receiveQueue,
			SendQueue sendQueue,
			AbstractConnectionAlarmManager connectionAlarmManager) {
		super(connection, receiveQueue, sendQueue, connectionAlarmManager);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void connect() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void disconnect() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean handleReadException(Exception paramException) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int read() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void write(byte[] paramArrayOfByte) throws IOException {
		// TODO Auto-generated method stub
		
	}
/*     */  }