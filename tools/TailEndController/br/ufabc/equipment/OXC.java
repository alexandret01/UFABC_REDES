package br.ufabc.equipment;

import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.ne.NE;
import br.ufabc.polatis.PolatisOXC;
import java.util.Vector;

public class OXC implements NE {
	private PolatisOXC polatis;
	private String ip;
	
	
	
	public OXC(String ip) {
		polatis = new PolatisOXC(ip);
		this.ip=ip;
	}
	
	
	
	
	@Override
	public String getName() {
		return polatis.getModel().concat("#").concat(polatis.getSerial());
	}

	@Override
	public int getSlot() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SerialNumber getSerial() {
		System.out.println("Serial: "+polatis.getSerial());
		return null;
	}

	@Override
	public String getModel() {
		return polatis.getModel();
	}

	@Override
	public String getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getUpdate() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isUp() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAlarmsDisabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getSupAddress() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isFullSync() {
		return false;
	}

	@Override
	public String getHardwareVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	public PolatisOXC getPolatis() {
		return this.polatis;
	}

	public void setActivedIgressPorts(){
		polatis.setActivedIgressPorts();		
	}
	public void setActivedEgressPorts(){
		polatis.setActivedEgressPorts();		
	}
	public Vector<String> getActivedIgressPorts(){
		return polatis.getActivedIgressPorts();
	}
	public Vector<String> getActivedEgressPorts(){
		return polatis.getActivedEgressPorts();
	}
	

}