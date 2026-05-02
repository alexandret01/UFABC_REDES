package br.com.padtec.v3.server.protocols.ppm3.requestcontrol;

import br.com.padtec.v3.server.protocols.ppm3.PPM3Collector;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3;

public class RequestEventHandler_Impl implements RequestEventHandler<PPM3>{
	protected PPM3 request;
	protected PPM3Collector collector;
	public RequestEventHandler_Impl(PPM3Collector collector, PPM3 request) {
		this.request = request;
		this.collector = collector;
	}
    public boolean onReceiveResponse(String connection, PPM3 response) {
      if (response.getId() == request.getId()) {
    	collector.analyzePacket(response, connection);
        return true;
      }
      return false;
    }


    public void onRequestFail()
    {
    }
  
}