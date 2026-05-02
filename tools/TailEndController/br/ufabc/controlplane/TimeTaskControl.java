package br.ufabc.controlplane;

import gmpls.signaling.RSVPPacket;

import java.util.TimerTask;

public class TimeTaskControl extends TimerTask{

	private RSVPPacket packet;
	
	public TimeTaskControl(RSVPPacket packet) {
		this.packet = packet;
	}
	
	public void run() {
		ControlPlane controller = ControlPlane.getInstance();
		controller.send(packet);	
	}

	public void setPacket(RSVPPacket packet) {
		this.packet = packet;
	}
}
