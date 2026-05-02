package br.com.padtec.v3.server.protocols.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.impl.NE_Impl;
import br.com.padtec.v3.data.impl.SupSPVJ_Impl;


public final class BoardManager  {
	
	private final Map<SerialNumber, NE_Impl> boards = new ConcurrentHashMap<SerialNumber, NE_Impl>();

	private final Map<Integer, SerialNumber> lastSiteSerial = new TreeMap<Integer, SerialNumber>();

	private int lastSite = -1;

	private final LinkedList<SerialNumber> nextSerial = new LinkedList<SerialNumber>();
	private boolean lastNextFromNextSerial;

	public synchronized void addNe(NE_Impl ne)
	{
		if (ne == null) {
			return;
		}
		SerialNumber serial = ne.getSerial();
		if (serial == null) {
			return;
		}
		this.boards.put(ne.getSerial(), ne);
	}

	public NE_Impl getNe(SerialNumber serial)
	{
		if (serial != null) {
			for (SerialNumber se : boards.keySet()){
				if (se.getPart() == serial.getPart() && se.getSeq() == serial.getSeq())
					return boards.get(se);
			}
		}
		return null;
	}

	public SortedMap<SerialNumber, NE_Impl> getNe(int siteAddress)
	{
		SortedMap<SerialNumber, NE_Impl> result = new TreeMap<SerialNumber, NE_Impl>();
		for (Entry<SerialNumber,NE_Impl>  item : this.boards.entrySet()) {
			if (((NE_Impl)item.getValue()).getSupAddress() == siteAddress) {
				result.put((SerialNumber)item.getKey(), (NE_Impl)item.getValue());
			}
		}
		return result;
	}

	public List<SupSPVJ_Impl> getSupervisor(int siteAddress)
	{
		List<SupSPVJ_Impl> result = new ArrayList<SupSPVJ_Impl>(2);
		for (Entry<SerialNumber,NE_Impl> item : this.boards.entrySet()) {
			if ((item.getValue().getSupAddress() == siteAddress) &&	(item.getValue() instanceof SupSPVJ_Impl))
				result.add((SupSPVJ_Impl)item.getValue());
		}

		return result;
	}

	public int getNeCount(int siteAddress)
	{
		int count = 0;
		for (Entry<SerialNumber,NE_Impl>  item : this.boards.entrySet()) {
			if (((NE_Impl)item.getValue()).getSupAddress() == siteAddress) {
				++count;
			}
		}
		return count;
	}

	public Map<SerialNumber, NE_Impl> getNe()
	{
		return Collections.unmodifiableMap(this.boards);
	}

	public boolean contains(SerialNumber serial)
	{
		if (serial == null) {
			return false;
		}
		return this.boards.containsKey(serial);
	}

	public synchronized NE_Impl removeNe(SerialNumber serial)
	{
		if (serial == null) {
			return null;
		}
		return ((NE_Impl)this.boards.remove(serial));
	}

	public int size() {
		return this.boards.size();
	}

	public SortedSet<Integer> getSites() {
		SortedSet<Integer> result = new TreeSet<Integer>();
		for (NE_Impl ne : this.boards.values()) {
			result.add(Integer.valueOf(ne.getSupAddress()));
		}
		return result;
	}

	private int getNextSite() {
		int smallest = 2147483647;
		int next = 2147483647;
		for (NE_Impl ne : this.boards.values()) {
			if (ne.getSupAddress() == this.lastSite + 1) {
				this.lastSite += 1;
				return this.lastSite;
			}
			smallest = Math.min(smallest, ne.getSupAddress());
			if (ne.getSupAddress() > this.lastSite) {
				next = Math.min(next, ne.getSupAddress());
			}
		}
		if (next != 2147483647)
			this.lastSite = next;
		else {
			this.lastSite = smallest;
		}
		return this.lastSite;
	}

	public synchronized SerialNumber next()
	{
		if (!this.lastNextFromNextSerial && !this.nextSerial.isEmpty()) {
			this.lastNextFromNextSerial = true;
			SerialNumber result = (SerialNumber)this.nextSerial.removeFirst();
			return result;
		}

		this.lastNextFromNextSerial = false;
		int site = getNextSite();

		return next(site);
	}
	

//    public synchronized SerialNumber next()
//    {
//        if(!lastNextFromNextSerial && !nextSerial.isEmpty())
//        {
//            lastNextFromNextSerial = true;
//            SerialNumber result = (SerialNumber)nextSerial.removeFirst();
//            return result;
//        } else
//        {
//            lastNextFromNextSerial = false;
//            int site = getNextSite();
//            return next(site);
//        }
//    }

	public synchronized SerialNumber next(int site)
	{
		

		SerialNumber lastSerial = this.lastSiteSerial.get(site);
		SerialNumber nextSerial = null;
		Iterator<Entry<SerialNumber, NE_Impl>> i = this.boards.entrySet().iterator();
		if (lastSerial != null)
		{
			while (i.hasNext()) {
				Entry<SerialNumber,NE_Impl> item = i.next();
				if (item.getKey().equals(lastSerial)) {
					break;
				}
			}
		}
		while (i.hasNext())
		{
			Entry<SerialNumber,NE_Impl> item = i.next();
			if (item.getValue().getSupAddress() == site) {
				nextSerial = item.getKey();
				break;
			}
		}
		if (nextSerial == null)
		{
			i = null;
			i = this.boards.entrySet().iterator();
			while (i.hasNext()) {
				Entry<SerialNumber,NE_Impl> item = i.next();
				if (((NE_Impl)item.getValue()).getSupAddress() == site) {
					nextSerial = item.getKey();
					break;
				}
			}
		}

		this.lastSiteSerial.put(new Integer(site), nextSerial);
		return nextSerial;
	}

	public synchronized void setNext(SerialNumber serial)
	{
		this.nextSerial.remove(serial);
		this.nextSerial.addFirst(serial);
		if (this.nextSerial.size() > 5)
			this.nextSerial.removeLast();
	}
}