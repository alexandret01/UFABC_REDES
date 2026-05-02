package br.com.padtec.v3.data.ne;

import br.com.padtec.v3.data.SerialNumber;

public abstract interface ODP {
  public abstract Boolean isODPEnabled();

  public abstract Boolean isWaitToRestoreEnabled();

  public abstract Boolean isWorkingPath();

  public abstract Boolean isConfigError();

  public abstract Boolean isCableFail();

  public abstract Boolean isCommLOS();

  public abstract Boolean isNeighbourCableFail();

  public abstract Boolean isNeighbourCommLOS();

  public abstract Boolean isSignalDegradeEnabled();

  public abstract PathState getPathState();

  public abstract Integer getWaitToRestoreTime();

  public abstract Integer getHoldOffTime();

  public abstract SerialNumber getNeighbourBoard();

  public abstract Boolean isLaserOffODP();

  public static enum PathState
  {
//    SIGNAL_FAIL, SIGNAL_DEGRADE, NO_DEFECT;
	  SIGNAL_FAIL(1, "Signal Fail"), 
	  SIGNAL_DEGRADE(3, "Signal Degrade"), 
	  NO_DEFECT(5, "No defect");

    private final int code;
    private final String stateName;

    private PathState(int code , String stateName){
    	this.code = code;
    	this.stateName = stateName;
    }
    
    public int getCode()
    {
      return this.code;
    }

    public String getStateName() {
      return this.stateName;
    }

    public static PathState getType(int code) {
      for (PathState type : values()) {
        if (type.getCode() == code) {
          return type;
        }
      }
      return null;
    }
  }
}