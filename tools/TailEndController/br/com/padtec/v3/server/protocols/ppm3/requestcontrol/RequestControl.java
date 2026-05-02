package br.com.padtec.v3.server.protocols.ppm3.requestcontrol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import br.com.padtec.v3.server.protocols.ppm3.PPM3SendQueue;
import br.com.padtec.v3.server.protocols.ppm3.packet.PPM3;
import br.com.padtec.v3.server.protocols.util.io.readerwriter.AbstractReaderWriter;
import br.com.padtec.v3.util.math.Average;
import br.com.padtec.v3.util.math.Ratio;

public class RequestControl
{
  private final LinkedList<Request<PPM3>> database = new LinkedList<Request<PPM3>>();
  private final AbstractReaderWriter[] rws;
  private final Map<Integer, Average> averageResponseTime = new TreeMap<Integer, Average>();
  private final Map<Integer, Ratio> failureRatio = new TreeMap<Integer, Ratio>();

  public RequestControl(AbstractReaderWriter[] rws) {
    this.rws = rws;
    for (AbstractReaderWriter item : rws)
      ((PPM3SendQueue)item.getSendQueue()).setController(this);
  }

  private Average getAverageResponseTime(int site)
  {
    Integer siteKey = Integer.valueOf(site);
    Average result = (Average)this.averageResponseTime.get(siteKey);
    if (result == null) {
      result = new Average();
      this.averageResponseTime.put(siteKey, result);
    }
    return result;
  }

  private Ratio getFailureRatio(int site) {

    Ratio result = (Ratio)this.failureRatio.get(site);
    if (result == null) {
      result = new Ratio();
      this.failureRatio.put(site, result);
    }
    return result;
  }

  public synchronized void send(PPM3 requestpacket, long responseTimeout, RequestEventHandler<PPM3> analyzer)
  {
	  if (requestpacket != null) {
		  Request<PPM3> event = new Request<PPM3>(requestpacket, analyzer, responseTimeout);

		  Iterator<Request<PPM3>> i = this.database.iterator();
		  while (i.hasNext()) {
			  Request<PPM3> item = i.next();
			  if (item.getPacket().getId() == requestpacket.getId()) {
				  i.remove();
				  getFailureRatio(item.getPacket().getDestination()).x();
				  item.getHandler().onRequestFail();
				  break;
			  }
		  }
		  this.database.add(event);
		  getFailureRatio(requestpacket.getDestination()).y();
		  _send(event);
	  }
  }

  private void _send(Request<PPM3> event)
  {
    Collection<String> usedRws = event.getSendHistory().values();

    List<AbstractReaderWriter> selectedRw = new ArrayList<AbstractReaderWriter>(this.rws.length);
    for (int i = 0; i < this.rws.length; ++i)
    {
      if (this.rws[i].isConnected()) {
        selectedRw.add(this.rws[i]);
      }

    }

    if (!(selectedRw.isEmpty()))
    {
      for (AbstractReaderWriter rw : selectedRw) {
//        ((PPM3SendQueue)rw.getSendQueue()).addPacket((PPM3)event.getPacket(), false);
        ((PPM3SendQueue)rw.getSendQueue()).addPacket((PPM3)event.getPacket(), true); //Teste High priority
        event.notifyPacketEnqueued(rw.getConnection());
      }
    } else {
      this.database.remove(event);
      _fail(event);
    }
  }

  private final void _fail(Request<PPM3> event)
  {
    getFailureRatio(((PPM3)event.getPacket()).getDestination()).x();
    event.getHandler().onRequestFail();
  }

  public void notifyPacketSent(String rwConnection, PPM3 packet)
  {
    for (Request<PPM3> item : this.database)
      if (item.getPacket() == packet) {
        item.notifyPacketSent(rwConnection);
        return;
      }
  }

  public int getPending()
  {
    return this.database.size();
  }

  public synchronized void check()
  {
    
    long limit = System.currentTimeMillis() - 800000L;
    Iterator<Request<PPM3>> i = this.database.iterator();
    while (i.hasNext()) {
      Request<PPM3> r = i.next();
      if (r.getCreationTime() < limit) {
        i.remove();
        _fail(r);
      }
    }

    List<Request<PPM3>> sendList = new LinkedList<Request<PPM3>>();
    for (Iterator<Request<PPM3>> localIterator1 = this.database.iterator(); localIterator1.hasNext(); ) { 
    	Request<PPM3> r = localIterator1.next();
      if (r.isTimedOut()) {
        sendList.add(r);
      }
    }

    for (Iterator<Request<PPM3>>localIterator1 = sendList.iterator(); localIterator1.hasNext(); ) { 
    	Request<PPM3> r = localIterator1.next();

      this.database.remove(r);
      _fail(r);
    }
    sendList = null;
  }

  public synchronized boolean onReceiveResponse(String connection, PPM3 packet)
  {
    for (Request<PPM3> r : this.database)  {
      Long sendTime = r.getLastSend();
      RequestEventHandler<PPM3> handler = r.getHandler(); 
      boolean handled = handler.onReceiveResponse(connection, packet);
      if ((sendTime != null) && handled){

    	  this.database.remove(r);
    	  for (AbstractReaderWriter rw : this.rws)  {
    		  rw.getSendQueue().removePacket(r.getPacket());
    	  }
    	  long responseTime = System.currentTimeMillis() - sendTime.longValue();

    	  getAverageResponseTime(((PPM3)r.getPacket()).getDestination()).add(responseTime);
    	  return true;
      }
    }

    return false;
  }

  public Map<Integer, Average> getAverageResponseTime()  {
    return this.averageResponseTime;
  }

  public Map<Integer, Ratio> getFailureRatio()
  {
    return this.failureRatio;
  }

  public void resetStatistics()
  {
    this.averageResponseTime.clear();
    this.failureRatio.clear();
  }
}