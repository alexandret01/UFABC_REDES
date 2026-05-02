package br.ufabc.controlplane.rsvp.state;

import java.util.Collection;
import java.util.HashMap;

public class StatesList<E> {
	
	private HashMap<Integer, E> list;
	
	public StatesList(){
		list = new HashMap<Integer, E>();
	}
	
	public void add(int key, E state){
		if (state != null)
			list.put(key, state);
	}
	
	public E getState(int key){
		return list.get(key);
	}
	
	public void remove(int key){
		list.remove(key);
	}
	
	public int size(){
		return list.size();
	}
	
	public Collection<E> values(){
		return list.values();
	}
	
	public boolean isEmpty(){
		return list.isEmpty();
	}
	

}
