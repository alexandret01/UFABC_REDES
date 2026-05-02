package br.ufabc.controlplane.metropad;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import br.com.padtec.v3.server.Colector;
import br.com.padtec.v3.server.protocols.ppm2v2.ColetorPPM2v2;
import br.com.padtec.v3.server.protocols.ppm3.PPM3Collector;
import br.ufabc.controlplane.metropad.Servidor.TypeSupervisor;


public class BoardColector{

	protected Map<Integer, Colector> colectores;
	protected Map<TypeSupervisor, Integer> types;
	public BoardColector(){
		colectores = new HashMap<Integer, Colector>();
		types = new HashMap<TypeSupervisor, Integer>();
	}

	public void addColector(int site, TypeSupervisor type, Colector colector){
		colectores.put(site, colector);
		types.put(type, site);
	}

	public Colector getColector(int id){
		return colectores.get(id);
	}
	
	public void updateColector(Colector colector, int site){
		if(colectores.containsValue(colector)){
			int oldtype = 0;
			TypeSupervisor type = null;
//			System.out.println("atualizando site para: "+ site);
			if (colector instanceof PPM3Collector){
//				System.out.println("BoardColector: Atualizando ColetorPPM3, oldSite:" + types.get(TypeSupervisor.SPVL));
//				System.out.println("site: "+ site);
				oldtype = types.get(TypeSupervisor.SPVL);
				type = TypeSupervisor.SPVL;
				
			} else if ( colector instanceof ColetorPPM2v2 ){
//				System.out.println("BoardColector: Atualizando ColetorPPM3, oldSite:" + types.get(TypeSupervisor.SPVJ));
				
				oldtype = types.get(TypeSupervisor.SPVJ);
				type = TypeSupervisor.SPVJ;
			}
			
			colectores.remove(colector);
			colectores.put(site,colector);
			types.remove(type);
			types.put(type, site);
//			System.out.println("Coletor Adiconado: " + colectores.get(site) );
		}
			
	}
	
	public Colector getColector(TypeSupervisor type){
		return getColector(types.get(type));
	}

	public Collection<Colector> getAllColector(){
		return colectores.values();
	}

	public Map<Integer, Colector> getColectores() {
		return colectores;
	}
	
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("ID - Type\t- Colector\'s Class");
		for (Iterator<Entry<Integer,Colector>> iterator = colectores.entrySet().iterator(); iterator.hasNext() ; ){
			Entry<Integer,Colector> item = iterator.next();
			builder.append(item.getKey());
			builder.append("  -  ");
			builder.append(types.get(item.getKey()));
			builder.append("\t- ");
			if (item.getValue() instanceof PPM3Collector)
				builder.append("PPM3Collecor");
			else if (item.getValue() instanceof ColetorPPM2v2)
				builder.append("ColecorPPM2v2");
			if (iterator.hasNext())
				builder.append("\n");
			
		}
		
		return builder.toString();
		
	}

}