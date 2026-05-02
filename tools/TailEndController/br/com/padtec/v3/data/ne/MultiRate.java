package br.com.padtec.v3.data.ne;

public abstract interface MultiRate  {
	
  public abstract Rate getRate();

  public static enum Rate  {
	  NONE(-1), 
	  AUTO(0), 
	  STM_64(1), 
	  GBE_10_WAN(2), 
	  GBE_10_LAN(3), 
	  STM_16(4), 
	  GBE_10(5);
//    NONE, AUTO, STM_64, GBE_10_WAN, GBE_10_LAN, STM_16, GBE_10;

    private byte code;
    
    private Rate(int code){
    	this.code = (byte)code;
    }

    public byte getCode()
    {
      return this.code;
    }

    public String toString()
    {
      switch (ordinal())
      {
      case 0:
        return "N/A";
      case 1:
        return "Auto";
      case 2:
        return "STM-64";
      case 3:
        return "10GbE WAN";
      case 4:
        return "10GbE LAN";
      case 5:
        return "STM-16";
      case 6:
        return "10GbE";
      }
      return super.toString();
    }

    public static Rate getRate(byte code)
    {
      for (Rate r : values()) {
        if (r.getCode() == code) {
          return r;
        }
      }
      return NONE;
    }
  }
}