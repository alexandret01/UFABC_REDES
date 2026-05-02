package br.com.padtec.v3.data;

import java.io.Serializable;
import java.util.List;
import java.util.TreeMap;

import br.com.padtec.v3.util.StateHistory.State;


public class ColectorConfig implements Serializable, Comparable<ColectorConfig> {
	private static final long serialVersionUID = 5L;
	private Integer id;
//	private Vector<String> ip;
//	private Vector<Integer> port;
	private Integer type;
//	private Vector<Integer> sites;
	private int port = 0;
	private int site = 0;
	private String ip = "";
	private String typeName;
	private String className;
	private String addresses;
	private Status status = Status.STATUS_STOP;
	private boolean isRunning = false;
	private int totalElements = 0;
	private TreeMap<String, List<State<Exception>>> networkState;
	private String name;

	public ColectorConfig()
	{
//		this.sites = new Vector<Integer>();
//		this.ip = new Vector<String>();
//		this.port = new Vector<Integer>();
	}

	public String getClassName() {
		return this.className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getTypeName() {
		return this.typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

//	public List<Integer> getPort()
//	{
//		return this.port;
//	}
	
	public int getPort(){
		return this.port;
	}
//
//	public void addPort(Integer port)
//	{
//		this.port.add(port);
//	}
	public void setPort(int port){
		this.port = port;
	}

//	public List<String> getIP()
//	{
//		return this.ip;
//	}
	
	public String getIp() {
		return ip;
	}

//	public void addIP(String ip)
//	{
//		this.ip.add(ip);
//	}
	public void setIp(String ip) {
		this.ip = ip;
	}

//	public List<Integer> getSites()
//	{
//		return this.sites;
//	}
//
//	public void addSite(Integer site)
//	{
//		this.sites.add(site);
//	}
	
	public int getSite() {
		return site;
	}

	public Integer getType()
	{
		return this.type;
	}

	public void setType(Integer type)
	{
		this.type = type;
	}

	public boolean equals(Object o)
	{
		if (o instanceof ColectorConfig) {
			if ((this.id == null) || (((ColectorConfig)o).getId() == null)) {
				return false;
			}
			return this.id.equals(((ColectorConfig)o).getId());
		}
		return false;
	}

	public Integer getId()
	{
		return this.id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

//	public void setIP(Vector<String> ip) {
//		this.ip = ip;
//	}
//
//	public void setPort(Vector<Integer> port) {
//		this.port = port;
//	}

//	public void setSites(Vector<Integer> sites) {
//		this.sites = sites;
//	}
	
	public void setSite(int site) {
		this.site = site;
	}

	public boolean isRunning() {
		return this.isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public Status getStatus()
	{
		return this.status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public int getTotalElements()
	{
		return this.totalElements;
	}

	public void setTotalElements(int totalElements) {
		this.totalElements = totalElements;
	}

	public String getAddresses()
	{
		return this.addresses;
	}

	public void setAddresses(String addresses) {
		this.addresses = addresses;
	}

//	public void sort()
//	{
//		if ((this.ip.size() != this.port.size()) || (this.ip.size() != this.sites.size())) {
//			throw new IllegalStateException(
//					"O tamanho dos vetores {ip, port, sites} são diferentes");
//		}
//		if (this.ip.size() <= 1) {
//			return;
//		}
//		TreeMap<String, PortSite> map = new TreeMap<String, PortSite>();
//		for (int i = 0; i < this.ip.size(); ++i) {
//			map.put((String)this.ip.get(i), new PortSite((Integer)this.port.get(i), (Integer)this.sites.get(i)));
//		}
//		this.ip.clear();
//		this.port.clear();
//		this.sites.clear();
//		for (Entry<String, PortSite> item : map.entrySet()) {
//			this.ip.add((String)item.getKey());
//			this.port.add(((PortSite)item.getValue()).porta);
//			this.sites.add(((PortSite)item.getValue()).site);
//		}
//		map = null;
//	}

	public void setNetworkState(TreeMap<String, List<State<Exception>>> ns)
	{
		this.networkState = ns;
	}

	public TreeMap<String, List<State<Exception>>> getNetworkState()
	{
		return this.networkState;
	}

	public String toString()
	{
		StringBuilder buf = new StringBuilder();
		if ((this.name != null) && (!this.name.equals(this.ip))) {
			buf.append(this.name);
			buf.append(' ');
		}
		buf.append('(');
		buf.append(this.ip);
		buf.append(':');
		buf.append(this.port);
		buf.append(')');
		if (getStatus() != Status.STATUS_OK) {
			buf.append(" [");
			buf.append(getStatus().status);
			buf.append("]");
		}
		return buf.toString();
	}

	public int compareTo(ColectorConfig o)
	{
		return (this.getIp()).compareTo(o.getIp());
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

//	private class PortSite
//	{
//		public final Integer porta;
//		public final Integer site;
//
//		public PortSite(Integer porta, Integer site)
//		{
//			this.porta = porta;
//			this.site = site;
//		}
//	}

	public static enum Status  {

		STATUS_OK("Coletando Dados"), 
		STATUS_STOP("Parado"), 
		STATUS_ERROR("Com erro");

		private String status;

		private Status(String status) {
			this.status = status;
		}

		public String toString() {
			return this.status;
		}
	}
}