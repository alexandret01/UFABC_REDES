package br.ufabc.controlplane;

import br.com.padtec.v3.data.NotificationListener;
import br.ufabc.dataplane.DataPlane;

public interface GmplsListener extends NotificationListener{

	public DataPlane getDataPlane(int id);
	public void setDataPlane(int id, DataPlane dataPlane);
	
	
}
