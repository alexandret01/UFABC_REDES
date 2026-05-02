package br.ufabc.controlplane.rsvp.state;

import java.util.EventListener;

public interface StateListener extends EventListener{
	void stateUpdated(StateEvent e);
}
