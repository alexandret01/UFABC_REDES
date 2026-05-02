package br.com.padtec.v3.server.protocols.util.timeout;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

public class SiteTimeoutController {
  private SiteResponseMonitor monitor;
  private SiteTimeoutHandler handler;
  private Set<Integer> sitesNotResponding = new TreeSet<Integer>();

  public void check() {
    if ((this.monitor == null) || (this.handler == null)) {
      return;
    }

    Map<Integer, Long> notRespondingNow = this.monitor.getTimedOutSites();

    for (Entry<Integer,Long> item : notRespondingNow.entrySet()) {
      Integer site = (Integer)item.getKey();
      if (this.sitesNotResponding.add(site))
    	  this.handler.onSiteTimeout(site.intValue(), ((Long)item.getValue()).longValue());
    }

    Iterator<Integer> i = this.sitesNotResponding.iterator();
    while (i.hasNext()) {
    	Integer site = (Integer)i.next();
    	if (notRespondingNow.remove(site) == null) {
    		i.remove();
    		Long time = this.monitor.getLastSiteResponse(site.intValue());
    		if (time == null) {
    			time = System.currentTimeMillis();
    		}
    		this.handler.onSiteResume(site.intValue(), time.longValue());
      }
    }

    i = null;
    notRespondingNow = null;
  }

  public SiteResponseMonitor getCounter()
  {
    return this.monitor;
  }

  public void setCounter(SiteResponseMonitor counter)
  {
    this.monitor = counter;
  }

  public SiteTimeoutHandler getHandler()
  {
    return this.handler;
  }

  public void setHandler(SiteTimeoutHandler handler)
  {
    this.handler = handler;
  }

  public static abstract interface SiteTimeoutHandler
  {
    public abstract void onSiteTimeout(int paramInt, long paramLong);

    public abstract void onSiteResume(int paramInt, long paramLong);
  }
}