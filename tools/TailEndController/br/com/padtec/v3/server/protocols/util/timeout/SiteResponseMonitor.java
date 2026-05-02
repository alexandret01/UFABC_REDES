package br.com.padtec.v3.server.protocols.util.timeout;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import br.com.padtec.v3.util.Functions;

public class SiteResponseMonitor {
  private long perSiteTimeout = Functions.getProperty("siteTimeout", 60000L);

  private final Map<Integer, Long> siteUpdate = new TreeMap<Integer, Long>();

  private Map<Integer, String> lastSiteUpdateConnection = new TreeMap<Integer, String>();

  public SiteResponseMonitor(Set<Integer> existingSites)
  {
    for (Integer site : existingSites)
      updateSiteReceptionTime(site.intValue(), System.currentTimeMillis());
  }

  private void updateSiteReceptionTime(int site, long time)
  {
    Long lastValue = (Long)this.siteUpdate.put(Integer.valueOf(site), Long.valueOf(time));
    if ((lastValue == null) || 
      (lastValue.longValue() <= time)) return;
    this.siteUpdate.put(Integer.valueOf(site), lastValue);
  }

  public synchronized Map<Integer, Long> getTimedOutSites()
  {
    Map<Integer, Long> result = new TreeMap<Integer, Long>();
    long minimumTime = System.currentTimeMillis() - getTimeout();
    for (Map.Entry<Integer, Long> item : this.siteUpdate.entrySet()) {
      if (((Long)item.getValue()).longValue() < minimumTime) {
        result.put((Integer)item.getKey(), (Long)item.getValue());
      }
    }
    return result;
  }

  public synchronized Long getLastSiteResponse(int site)
  {
    return ((Long)this.siteUpdate.get(Integer.valueOf(site)));
  }

  public synchronized void notifyPacketReceived(int site, String connection)
  {
    this.lastSiteUpdateConnection.put(Integer.valueOf(site), connection);
    updateSiteReceptionTime(site, System.currentTimeMillis());
  }

  public String getLastSiteUpdateConnection(int site)
  {
    return ((String)this.lastSiteUpdateConnection.get(Integer.valueOf(site)));
  }

  public long getTimeout()
  {
    return (this.perSiteTimeout * this.siteUpdate.size());
  }
}