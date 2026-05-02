package br.ufabc.controlplane.util.log;

import br.ufabc.controlplane.ControlPlane;
import br.ufabc.dataplane.DataPlane;

public class LogTxtThread extends Thread{
	NewLogTXT txt;
	DataPlane dataPlane;
	ControlPlane controller;
	boolean run = true;
	public LogTxtThread(DataPlane dataPlane, ControlPlane controller) {
		//txt = LogTXT.getInstance();
		this.dataPlane = dataPlane;
		this.controller = controller;
		txt= NewLogTXT.getInstance(dataPlane, controller);
		txt.headerRecorder();
//		setDaemon(true);
	}
	public void run(){
		try {
			while(run){
				//txt.writeDataPlane(dataPlane);
				txt.writeDataPlane();
				sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace(); 
		}
		
	}
	
	public void close(){
		txt.flush();
		txt.close();
		run=false;
	}
}
