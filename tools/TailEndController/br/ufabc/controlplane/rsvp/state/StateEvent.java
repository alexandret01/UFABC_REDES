package br.ufabc.controlplane.rsvp.state;

import java.util.EventObject;

public class StateEvent extends EventObject{
	
	private Type type;

	public StateEvent(Object source) {
		super(source);
		
	}
	enum Type{
		CONTINUE,
		ALERT,
		RELEASE
	}
	
	public Type getType() {
		return type;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
