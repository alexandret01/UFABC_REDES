package br.com.padtec.v3.server.protocols.util.io.readerwriter;

import java.util.logging.Level;

import br.com.padtec.v3.data.impl.NE_Impl;
import br.com.padtec.v3.data.impl.SPVL4_Impl;
import br.com.padtec.v3.server.protocols.ppm3.PPM3Collector;
import br.com.padtec.v3.server.protocols.util.io.readerwriter.TcpIpReaderWriter.TcpEventHandler;
import br.com.padtec.v3.util.Functions;

public class TcpIpEventHandlerImpl implements TcpEventHandler{
	
	private boolean dataReceived;
	private AbstractReaderWriter connection;
	PPM3Collector coletor;
	
	public TcpIpEventHandlerImpl(PPM3Collector coletor, AbstractReaderWriter connection){
		this.connection = connection;
		this.coletor = coletor;
	}




	public boolean handleReadException(Exception e)
	{
		if (("Read timed out".equalsIgnoreCase(e.getMessage())) && 
				(this.dataReceived))
		{
			for (NE_Impl ne : coletor.getNeDb().getNe().values()) {
				if (ne instanceof SPVL4_Impl) {
					SPVL4_Impl spvl = (SPVL4_Impl)ne;
					if (!(Functions.equals(connection.getConnection(), spvl.getIP()))) 
						coletor.getNeDb().setNext(ne.getSerial());
					coletor.log(Level.INFO, "Sending keep-alive to " +	connection.getConnection());
					this.dataReceived = false;
					return true;
				}
			}
		}

		return false;
	}

	public void onConnect(TcpIpReaderWriter writer) {
	}

	public int onRead(int data) {
		this.dataReceived = true;
		return data;
	}

}
