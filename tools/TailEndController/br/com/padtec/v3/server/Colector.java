package br.com.padtec.v3.server;

import java.util.Collection;

import br.com.padtec.v3.data.ColectorConfig;
import br.com.padtec.v3.data.Command;
import br.com.padtec.v3.data.NotificationListener;
import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.impl.NE_Impl;
import br.com.padtec.v3.data.ne.NE;


public abstract interface Colector extends Runnable  {

	/**
	 * Returns a collection of Network Elements from Board Manage Class 
	 * */
	public abstract Collection<? extends NE> getAllNE();
	/**
	 * Returns a network element implementation from Board Manage 
	 * by serial number
	 * 
	 * @param serial The serial Number of NE
	 * */
	public abstract NE_Impl getNE(SerialNumber paramSerialNumber);

	/**
	 * Removes a network element from data base
	 * */
	public abstract void removeNE(SerialNumber paramSerialNumber);

	/**
	 * Additions a notification listener
	 * */
	public abstract void addNotificationListener(NotificationListener paramNotificationListener);

	/**
	 * Removes a notification listener
	 * */
	public abstract void removeNotificationListener(NotificationListener paramNotificationListener);

	/**
	 * Additions a command to be sent to supervisor
	 * */
	public abstract void addCommand(Command paramCommand);

	/**
	 * Shuts down this application
	 * */
	public abstract void shutdown();

	/**
	 * Verify if this application is alive
	 * */
	public abstract boolean isAlive();

	/**
	 * Returns a String of IP address
	 * */
	public abstract String getConection();

	/**
	 * The same as <code>getConnection()</code>
	 * */
	public abstract String getIName();

	@Deprecated
	public abstract boolean addSupervisor(int paramInt1, String paramString, int paramInt2);

	/**
	 * Sets a serial connection
	 * */
	public abstract boolean setConnection(String porta, int initTimeout, int baudRate);

	/**
	  * Sets the TCP connection
	  * @param ip The destination's IP
	  * @param port The destination's port
	  * */
	public abstract boolean setConnection(String ip, int port);

	/**
	  * Sets a TCP connection of backup
	  * @param ip The destination's IP
	  * @param port The destination's port
	  * */
	public abstract boolean setBackUpConnection(String ip, int port);

	public abstract void reSyncAlarms(SerialNumber paramSerialNumber);

	
	public abstract ColectorConfig getColectorConfig();

	/**
	 * Sets up a ColectorConfig object
	 * @param paramColectorConfig the ColectorConfig object
	 * */
	public abstract void setColectorConfig(ColectorConfig paramColectorConfig);
	
	/**
	 * Sends a command to unlock the supervisor
	 * @param part The part number of serial number
	 * @param address the internal address of supervisor
	 * */
	public abstract void unlockSupervisor(int part, int address);

	
	/**
	 * Sets the supervisor's IP
	 *  
	 * */
	@Deprecated
	public abstract void setSupIp(Integer site, String ip);

	/**
	  * Sends a packet to regenerate traps from supervisor
	  * */
	public abstract void sendRegen();

	/**
	 * Does the update in a network element
	 * */
	public abstract void update(SerialNumber paramSerialNumber);
}