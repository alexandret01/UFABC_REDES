package br.ufabc.dataplane;

import java.util.LinkedList;
import java.util.PriorityQueue;

import br.ufabc.dataplane.alarms.AlarmGmpls;


public class DataPlaneHistory {
	
	private static final int MAX_ELEMENTS_LIST = 10;
	private double acceptableMinGainIn = 0.0;
	private double acceptableMaxGainIn = 35.0;
	private double acceptableMinGainOut = 0.0;
	private double acceptableMaxGainOut = 34.0;
	private long transmittedBytesIn;
	private long transmittedBytesOut;
	private long initTime;
	private long endTime;
	
	/*Armazenam as informações das conexões estabelecidas com sucesso*/
	private LinkedList<Double> powerInList;
	private LinkedList<Double> gainList;
	private LinkedList<Double> powerOutList;
	
	PriorityQueue<AlarmGmpls> listAlarmsGMPLS;
	
	public DataPlaneHistory(){
		this.powerInList = new LinkedList<Double>();
		this.gainList = new LinkedList<Double>();
		this.powerOutList = new LinkedList<Double>();
		listAlarmsGMPLS = new PriorityQueue<AlarmGmpls>() ;
	}
	
	public double getAveragePin(){
		double sum = 0;
		
		for (double d: powerInList){
			sum+=d;
		}
		return sum/powerInList.size();
	}
	
	public void addPout(double power){
		if (powerInList.size() > MAX_ELEMENTS_LIST){
			powerInList.pollFirst();
			powerInList.add(power);
		}
	}
	
	public double getAverageGain(){
		double sum = 0;
		
		for (double d: gainList){
			sum+=d;
		}
		return sum/gainList.size();
	}
	
	public void addGainList	(double power){
		if (gainList.size() > MAX_ELEMENTS_LIST){
			gainList.pollFirst();
			gainList.add(power);
		}
	}
	
	public double getAveragePout(){
		double sum = 0;
		
		for (double d: powerOutList){
			sum+=d;
		}
		return sum/powerOutList.size();
	}
	
	public void addPin(double power){
		if (powerOutList.size() > MAX_ELEMENTS_LIST){
			powerOutList.pollFirst();
			powerOutList.add(power);
		}
	}
	
	
	
	
	
	/**
	 * @return the acceptableMinGainIn
	 */
	public double getAcceptableMinGainIn() {
		return acceptableMinGainIn;
	}
	/**
	 * @param acceptableMinGainIn the acceptableMinGainIn to set
	 */
	public void setAcceptableMinGainIn(double acceptableMinGainIn) {
		this.acceptableMinGainIn = acceptableMinGainIn;
	}
	/**
	 * @return the acceptableMaxGainIn
	 */
	public double getAcceptableMaxGainIn() {
		return acceptableMaxGainIn;
	}
	/**
	 * @param acceptableMaxGainIn the acceptableMaxGainIn to set
	 */
	public void setAcceptableMaxGainIn(double acceptableMaxGainIn) {
		this.acceptableMaxGainIn = acceptableMaxGainIn;
	}
	
	/**
	 * @return the acceptableMinGainOut
	 */
	public double getAcceptableMinGainOut() {
		return acceptableMinGainOut;
	}
	/**
	 * @param acceptableMinGainOut the acceptableMinGainOut to set
	 */
	public void setAcceptableMinGainOut(double acceptableMinGainOut) {
		this.acceptableMinGainOut = acceptableMinGainOut;
	}
	/**
	 * @return the acceptableMaxGainOut
	 */
	public double getAcceptableMaxGainOut() {
		return acceptableMaxGainOut;
	}
	/**
	 * @param acceptableMaxGainOut the acceptableMaxGainOut to set
	 */
	public void setAcceptableMaxGainOut(double acceptableMaxGainOut) {
		this.acceptableMaxGainOut = acceptableMaxGainOut;
	}
	/**
	 * @return the recommentableGainOut
	 */
	public long getTransmittedBytesIn() {
		return transmittedBytesIn;
	}
	public void setTransmittedBytesIn(long transmittedBytesIn) {
		this.transmittedBytesIn = transmittedBytesIn;
	}
	public long getTransmittedBytesOut() {
		return transmittedBytesOut;
	}
	public void setTransmittedBytesOut(long transmittedBytesOut) {
		this.transmittedBytesOut = transmittedBytesOut;
	}
	public long getInitTime() {
		return initTime;
	}
	public void setInitTime(long initTime) {
		this.initTime = initTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
}
