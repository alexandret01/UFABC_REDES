package br.ufabc.equipment;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import br.com.padtec.v3.server.Colector;
import br.com.padtec.v3.server.protocols.ppm2v2.ColetorPPM2v2;
import br.com.padtec.v3.server.protocols.ppm3.PPM3Collector;

public class BoardCollector{

	protected Map<Integer, Colector> collectors;
	protected Map<Supervisor.TypeSupervisor, Integer> types;
	
	public BoardCollector(){
		collectors = new HashMap<Integer, Colector>();
		types = new HashMap<Supervisor.TypeSupervisor, Integer>();
	}

	public void addCollector(int site, Supervisor.TypeSupervisor type, Colector colector){
		collectors.put(site, colector);
		types.put(type, site);
	}

	public Colector getCollector(int id){
		return collectors.get(id);
	}
	
	public void updateCollector(Colector colector, int site){
		if(collectors.containsValue(colector)){
			Supervisor.TypeSupervisor type = null;
			if (colector instanceof PPM3Collector){
				type = Supervisor.TypeSupervisor.SPVL;
			} else if (colector instanceof ColetorPPM2v2){
				type = Supervisor.TypeSupervisor.SPVJ;
			}			
			collectors.remove(colector);
			collectors.put(site,colector);
			types.remove(type);
			types.put(type, site);
		}			
	}
	
	public Colector getCollector(Supervisor.TypeSupervisor type){
		return getCollector(types.get(type));
	}

	public Collection<Colector> getAllCollector(){
		return collectors.values();
	}

	public Map<Integer, Colector> getCollectors() {
		return collectors;
	}
	
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("ID - Type\t- Colector\'s Class");
		for (Iterator<Entry<Integer,Colector>> iterator = collectors.entrySet().iterator(); iterator.hasNext() ; ){
			Entry<Integer,Colector> item = iterator.next();
			builder.append(item.getKey());
			builder.append("  -  ");
			builder.append(types.get(item.getKey()));
			builder.append("\t- ");
			if (item.getValue() instanceof PPM3Collector)
				builder.append("PPM3Collector");
			else if (item.getValue() instanceof ColetorPPM2v2)
				builder.append("ColetorPPM2v2");
			if (iterator.hasNext())
				builder.append("\n");		
		}		
		return builder.toString();		
	}
}