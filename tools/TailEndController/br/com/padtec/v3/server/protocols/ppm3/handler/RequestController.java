package br.com.padtec.v3.server.protocols.ppm3.handler;

import java.util.Map;
import java.util.TreeMap;

import br.com.padtec.v3.data.SerialNumber;

public class RequestController
{
  private Map<Integer, State> database = new TreeMap<Integer, State>();

  public synchronized void setInterval(int code, int interval)
  {
	  if (interval != 1){
		  State state = new State();
		  state.interval = interval;
		  this.database.put(Integer.valueOf(code), state);
	  }
  }

  public synchronized boolean next(int code, SerialNumber serial)
  {
	  boolean result;
	  State state = (State)this.database.get(Integer.valueOf(code));
	  if (state != null) {

		  Integer step = (Integer)state.step.get(serial);
		  if (step == null) {
			  step = Integer.valueOf(0);
		  }

		  if (state.interval <= 0)  {
			  result = step.intValue() == 0;
		  } else {
			  result = step.intValue() % state.interval == 0;
		  }

		  step = Integer.valueOf(step.intValue() + 1);
		  state.step.put(serial, step);
		  return result;
	  }
	  return true;
  
  }

  public synchronized void reset(SerialNumber serial)
  {
    for (State state : this.database.values())
      state.step.remove(serial);
  }

  private static class State
  {
    public int interval;
    public Map<SerialNumber, Integer> step = new TreeMap();
  }
}