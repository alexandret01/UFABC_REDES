package br.com.padtec.v3.server.protocols.ppm3.requestcontrol;

import java.util.logging.Level;

import br.com.padtec.v3.server.protocols.ppm3.CommunicationMonitor;
import br.com.padtec.v3.server.protocols.ppm3.PPM3Collector;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3HistoryGet;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3HistoryResponse;

public class RequestEventHandlerHistory extends RequestEventHandler_Impl{

	public RequestEventHandlerHistory(PPM3Collector collector, PPM3 request) {
		super(collector, request);
		
	}
	public boolean onReceiveResponse(String connection, PPM3 response) {
	      if (response.getId() == request.getId()) {
	    	  if (response.getPayload() instanceof PPM3HistoryResponse){
	    		  PPM3HistoryGet getPayload = (PPM3HistoryGet)request.getPayload();
	    		  PPM3HistoryResponse responsePayload = (PPM3HistoryResponse)response.getPayload();

	    		  CommunicationMonitor.getInstance().notifyHistoryResponse(connection, getPayload, response);

	    		  if (getPayload.getHistoryType() == responsePayload.getHistoryType()){
	    			  collector.onReceiveHistory(request, response, connection);

	    			  return true;
	    		  }
	    	  }
	    	  collector.log(Level.WARNING, "PPM3: HistoryGet [" + request.toString() +	
	    			  "] received a response [" + response.toString() + "]");
	      }
	      return false;
	}

}
