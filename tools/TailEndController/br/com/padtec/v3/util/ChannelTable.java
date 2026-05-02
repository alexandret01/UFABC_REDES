package br.com.padtec.v3.util;

import java.util.ArrayList;
import java.util.List;

public final class ChannelTable {
  public static String[] cwdmChannels = { 
    "O1", 
    "O2", 
    "O3", 
    "O4", 
    "O5", 
    "E1", 
    "E2", 
    "E3", 
    "E4", 
    "E5", 
    "S1", 
    "S2", 
    "S3", 
    "C1", 
    "C2", 
    "L1", 
    "L2", 
    "L3" };
  private static final long SPEED_OF_LIGHT = 299792458;

  public static double channel2Wavelength(String channel) throws NumberFormatException {
	  double result = 0.0;
	  if (channel.length() == 2)   {
		  char band = channel.charAt(0);
		  double ch = Integer.parseInt(channel.substring(1));
		  switch (band)   {
		  case 'O':
			  ch += 62.5D;
			  break;
		  case 'E':
			  ch += 67.5D;
			  break;
		  case 'S':
			  ch += 72.5D;
			  break;
		  case 'C':
			  ch += 75.5D;
			  break;
		  case 'L':
			  ch += 77.5D;
		  }

		  result = (ch * 2.0E-008D); }
	  if (channel.length() == 3)   {
		  long frequency = channel2Frequency(channel);
		  result = frequency2Wavelength(frequency);
	  }
	  return result;
  }

  public static long channel2Frequency(String channel)  throws NumberFormatException {
    if (channel.length() == 2)   {
      return wavelength2Frequency(channel2Wavelength(channel)); }
    if (channel.length() == 3)  {
      char band = channel.charAt(0);
      double ch = Double.parseDouble(channel.substring(1));
      if (ch == 0.0D)   {
        ch = 100.0D;
      }
      switch (band)
      {
      case 'H':
        ch += 1900.5D;
        break;
      case 'C':
        ch += 1900.0D;
        break;
      case 'Q':
        ch += 1800.5D;
        break;
      case 'L':
        ch += 1800.0D;
      }
      long result = Math.round(ch * 100000000000.0); 
      return result;
    }
    return 0 ;
  }

  public static double frequency2Wavelength(long frqev) {
	  double wavelength = ((double)SPEED_OF_LIGHT / (double)frqev); 
	  return wavelength;
  }

  public static long wavelength2Frequency(double wavelength) {
    return Math.round(SPEED_OF_LIGHT / wavelength);
  }

  public static String nextChannel(String channel)
  {
    if (channel.length() == 2)
    {
      String ch = Character.toString(channel.charAt(0));
      switch (channel.charAt(0))
      {
      case 'O':
        int oVal = Integer.parseInt(channel.substring(1));
        if (oVal == 5) {
          return "E1";
        }
        return ch + (oVal + 1);
      case 'E':
        int eVal = Integer.parseInt(channel.substring(1));
        if (eVal == 5) {
          return "S1";
        }
        return ch + (eVal + 1);
      case 'S':
        int sVal = Integer.parseInt(channel.substring(1));
        if (sVal == 3) {
          return "C1";
        }
        return ch + (sVal + 1);
      case 'C':
        int cVal = Integer.parseInt(channel.substring(1));
        if (cVal == 2) {
          return "L1";
        }
        return ch + (cVal + 1);
      case 'L':
        int lVal = Integer.parseInt(channel.substring(1));
        if (lVal == 3) {
          return "unknown";
        }
        return ch + (lVal + 1);
      }
    } else if (channel.length() == 3)
    {
      switch (channel.charAt(0))
      {
      case 'L':
        return "Q" + channel.substring(1);
      case 'Q':
        int qVal = Integer.parseInt(channel.substring(1));
        if (qVal == 99) {
          return "L00";
        }
        if (qVal == 0) {
          return "C01";
        }
        return "L" + (qVal + 1);
      case 'C':
        return "H" + channel.substring(1);
      case 'H':
        int hVal = Integer.parseInt(channel.substring(1));
        if (hVal == 99) {
          return "unknown";
        }
        ++hVal;
        return "C" + ((hVal < 10) ? "0" : "") + hVal;
      }
    }
    return "unknown";
  }

  public static String nextChannel(String channel, Space space)
  {
    if (channel.length() == 2)
    {
      if (space != Space.S20nm)
    	  return nextChannel(channel);
    }
    if (channel.length() == 3)
    {
      int count = 0;
      switch (space.ordinal())
      {
      case 1:
        count = 1;
        break;
      case 2:
        count = 2;
        break;
      case 3:
        count = 4;
        break;
      case 4:
        return "unknown";
      }
      String ch = channel;
      int i = 0; 
      while (true) { ch = nextChannel(ch);
        ++i; if (i >= count)
        {
          return ch; } }
    }
    return "unknown";
  }

  public static List<String> getChannels(ChannelGroup group)
  {
    List<String> result = new ArrayList<String>(group.getChannelCount());
    String ch = group.getInitialChannel();
    for (int i = 0; i < group.getChannelCount(); ++i) {
      result.add(ch);
      ch = nextChannel(ch, group.getSpace());
    }
    return result;
  }

  public static String wavelength2String(double wavelength)
  {
    return String.format("%1$.2f nm", new Object[] { Double.valueOf(wavelength * 1000000000.0D) });
  }

  public static String frequency2DwdmChannel(long frequency)
  {
    String[][] arrayOfString;;
    long lastDifference = 9223372036854775807L;
    String lastChannel = null;

    int j = (arrayOfString = new String[][] { { "L", "Q" }, 
      { "C", "H" } }).length; 
    int k = 0;

    for (; k < j; ++k) {
      String[] bandas = arrayOfString[k];
      for (int i = 1; i <= 100; ++i) {
        for (String bandaAtual : bandas) {
          String channel = bandaAtual + 
            ((i == 100) ? "00" : (i < 10) ? "0" + i : Integer.valueOf(i));
          long difference = channel2Frequency(channel) - frequency;
          if (Math.abs(difference) > Math.abs(lastDifference)) {
            return lastChannel;
          }
          lastDifference = difference;
          lastChannel = channel;
        }
      }
    }
    return lastChannel;
  }

  public static String wavelength2CwdmChannel(double wavelength)
  {
    double lastDifference = 9.223372036854776E+018D;
    String lastChannel = null;
    for (String channel : cwdmChannels) {
      double difference = channel2Wavelength(channel) - wavelength;
      if (Math.abs(difference) > Math.abs(lastDifference)) {
        return lastChannel;
      }
      lastDifference = difference;
      lastChannel = channel;
    }
    return lastChannel;
  }

  public static float convertITUChannel(String channel) {
    float freq = (float)(channel2Frequency(channel) / 1000000L);
    float dist = 1.0F;
    if (freq > 1.915625E+008F)
      dist += (1.961E+008F - freq) / 16.0F / 3.125F / 1000.0F;
    else {
      dist = (1.915594E+008F - freq) / 16.0F / 3.125F / 1000.0F + 91.8125F;
    }
    return dist;
  }

  public static class ChannelGroup
  {
    private String initialChannel;
    private ChannelTable.Space space;
    private int channelCount;

    public ChannelGroup(String initialChannel, ChannelTable.Space space, int channelCount)
    {
      this.initialChannel = initialChannel;
      this.space = space;
      this.channelCount = channelCount;
    }

    public int getChannelCount()
    {
      return this.channelCount;
    }

    public void setChannelCount(int channelCount)
    {
      this.channelCount = channelCount;
    }

    public String getInitialChannel()
    {
      return this.initialChannel;
    }

    public void setInitialChannel(String initialChannel)
    {
      this.initialChannel = initialChannel;
    }

    public ChannelTable.Space getSpace()
    {
      return this.space;
    }

    public void setSpace(ChannelTable.Space space)
    {
      this.space = space;
    }
  }

  public static enum Space
  {
    S50G, S100G, S200G, S20nm;

    public String toString()
    {
      switch (ordinal())
      {
      case 1:
        return "50 GHz";
      case 2:
        return "100 GHz";
      case 3:
        return "200 GHz";
      case 4:
        return "20 nm";
      }
      return super.toString();
    }
  }
}