package br.com.padtec.v3.util;


import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ContainerListener;
import java.awt.event.KeyListener;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;


public final class Functions {
  public static boolean isLct = true;

  public static boolean isServer = false;

  public static double getDbm(double miliwatts)
  {
    return (10.0D * Math.log10(miliwatts));
  }

  public static double getMiliwatts(double dbm)
  {
    return Math.pow(10.0D, dbm / 10.0D);
  }

  public static int b2i(byte b)
  {
    return (b & 0xFF);
  }

  public static int b2i(byte[] b)
  {
    return (int)b2l(b);
  }

  public static int b2i(byte[] b, int startIndex, int length)
  {
    return (int)b2l(b, startIndex, length);
  }

  public static int b2si(byte b)
  {
    return b;
  }

  public static long b2l(byte[] b)
  {
    long result = 0L;
    for (byte x : b) {
      result = result << 8 | b2i(x);
    }
    return result;
  }

  public static int b2i(byte b1, byte b2, byte b3, byte b4)
  {
    return (b2i(b1) << 24 | 
      b2i(b2) << 16 | 
      b2i(b3) << 8 | 
      b2i(b4));
  }

  public static long b2sl(byte[] b)
  {
    switch (b.length)
    {
    case 0:
      return 0L;
    case 1:
      return b2si(b[0]);
    }
    long result = b2si(b[0]);
    for (int i = 1; i < b.length; ++i) {
      result = result << 8 | b2i(b[i]);
    }
    return result;
  }

  public static long b2l(byte[] b, int startIndex, int length)
  {
    return b2l(getSubarray(b, startIndex, length));
  }

  public static long b2sl(byte[] b, int startIndex, int length)
  {
    return b2sl(getSubarray(b, startIndex, length));
  }

  public static byte i2b(int i)
  {
    return (byte)i;
  }

  @Deprecated
  public static long getLongFromBytes(byte[] data)
  {
    return (((data[0] & 0xFF) << 0) + ((data[1] & 0xFF) << 8) + 
      ((data[2] & 0xFF) << 16) + 
      ((data[3] & 0xFF) << 24) + 
      ((data[4] & 0xFF) << 32) + 
      ((data[5] & 0xFF) << 40) + 
      ((data[6] & 0xFF) << 48) + 
      ((data[7] & 0xFF) << 56));
  }

  public static byte[] l2b(long valor, int bytes)
  {
    byte[] result = new byte[bytes];
    int i = bytes;
    while (true) {
      --i;
      if (i < 0) {
        return result;
      }
      result[i] = (byte)(int)(valor & 0xFF);
      valor >>= 8;
    }
  }

  public static void setBytes(byte[] buffer, int pos, long valor, int bytes)
  {
    byte[] data = l2b(valor, bytes);
    System.arraycopy(data, 0, buffer, pos, bytes);
  }

  public static boolean i2bo(int i)
  {
    return (i == 0);
  }

  public static int bo2i(boolean b)
  {
    return ((b) ? 1 : 0);
  }

  public static void centerWindow(Component c)
  {
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    int left = 0;
    int top = 0;
    if (screen.getWidth() > c.getWidth())
      left = (int)(screen.getWidth() / 2.0D) - (c.getWidth() / 2);
    if (screen.getHeight() > c.getHeight()) {
      top = (int)(screen.getHeight() / 2.0D) - (c.getHeight() / 2);
    }
    c.setLocation(left, top);
  }

  public static void centerWindow(Component c, Component ref)
  {
    Point p = ref.getLocation();
    double x = p.getX() + ref.getWidth() / 2;
    double y = p.getY() + ref.getHeight() / 2;
    c.setLocation(new Point((int)(x - (c.getWidth() / 2)), (int)(y - (
      c.getHeight() / 2))));
  }

  public static void showError(Component pai, String msg)
  {
    JOptionPane.showMessageDialog(pai, msg, "Erro", 0);
  }

  public static void showWarning(Component pai, String msg)
  {
    JOptionPane.showMessageDialog(pai, msg, "Aviso", 
      2);
  }

  public static int h2i(String hexa)
  {
    return Integer.parseInt(hexa, 16);
  }

  public static String getHexa(byte b)
  {
    String n = Integer.toHexString(b2i(b)).toUpperCase();
    if (n.length() == 1)
      n = "0x0" + n;
    else {
      n = "0x" + n;
    }
    return n;
  }

  public static String getHexa(byte[] data)
  {
    if (data == null) {
      return "";
    }
    return getHexa(data, 0, data.length);
  }

  public static String getHexa(byte[] frame, int offset, int size)
  {
    StringBuffer buf = new StringBuffer();
    for (int i = offset; i < offset + size; ++i) {
      buf.append(getHexa(frame[i]));
      buf.append(" ");
    }
    return buf.toString();
  }

  public static void showError(Component pai, String title, String msg)
  {
    JOptionPane.showMessageDialog(pai, msg, title, 0);
  }

  private static String getPlural(long i) {
    if ((i > 1L) || (i == 0L))
      return "s";
    return "";
  }

  public static String getUpTime(long seconds)
  {
    long minutes = seconds / 60L;
    long hours = minutes / 60L;
    long days = hours / 24L;
    String result = "";
    if (days != 0L)
      result = days + " dia" + getPlural(days);
    if ((days != 0L) && (hours != 0L))
      result = result + ", ";
    if (hours != 0L)
      result = result + (hours % 24L) + " hora" + getPlural(hours % 24L);
    if ((hours != 0L) && (minutes != 0L))
      result = result + ", ";
    if (minutes != 0L)
      result = result + (minutes % 60L) + " minuto" + getPlural(minutes % 60L);
    if ((minutes != 0L) && (seconds % 60L != 0L))
      result = result + " e ";
    if (seconds % 60L != 0L)
      result = result + (seconds % 60L) + " segundo" + getPlural(seconds % 60L);
    return result;
  }

  public static void addKeyAndContainerListenerRecursively(Component c, KeyListener kl, ContainerListener cl)
  {
    c.addKeyListener(kl);
    if (c instanceof Container) {
      Container cont = (Container)c;
      cont.addContainerListener(cl);
      Component[] children = cont.getComponents();
      for (int i = 0; i < children.length; ++i)
        addKeyAndContainerListenerRecursively(children[i], kl, cl);
    }
  }

  public static void removeKeyAndContainerListenerRecursively(Component c, KeyListener kl, ContainerListener cl)
  {
    synchronized (c) {
      c.removeKeyListener(kl);
    }
    if (c instanceof Container) {
      Container cont = (Container)c;
      cont.removeContainerListener(cl);
      Component[] children = cont.getComponents();
      for (int i = 0; i < children.length; ++i)
        removeKeyAndContainerListenerRecursively(children[i], kl, cl);
    }
  }

  public static double mean(double[] array)
  {
    double mean = 0.0D;
    if (array.length > 0) {
      double sum = 0.0D;
      for (int i = 0; i < array.length; ++i) {
        sum += array[i];
      }
      mean = sum / array.length;
    }
    return mean;
  }

  public static double stdDev(double[] array)
  {
    double stdDev = 0.0D;
    if (array.length > 1) {
      double meanValue = mean(array);
      double sum = 0.0D;
      for (int i = 0; i < array.length; ++i) {
        double diff = array[i] - meanValue;
        sum += diff * diff;
      }
      stdDev = Math.sqrt(sum / (array.length - 1));
    }
    return stdDev;
  }

  public static boolean testBit(byte b, int i)
  {
    int mask = 1 << i;
    boolean result = ((b & mask) == 0); 
    return result;
  }

  public static byte setBit(byte b, int i)
  {
    int mask = 1 << i;
    return (byte)(b | mask);
  }

  public static byte[] getSubarray(byte[] src, int startIndex, int length)
  {
    byte[] result = new byte[length];
    System.arraycopy(src, startIndex, result, 0, length);
    return result;
  }

  public static int compareVersions(String v1, String v2)
  {
    if (v1 == v2) {
      return 0;
    }
    if (v1 == null)
      return -1;
    if (v2 == null) {
      return 1;
    }

    StringTokenizer v1Version = new StringTokenizer(v1, ".");
    StringTokenizer v2Version = new StringTokenizer(v2, ".");

    while ((v1Version.hasMoreTokens()) || (v2Version.hasMoreTokens())) {
      String v1Str = (v1Version.hasMoreTokens()) ? 
        filter(v1Version.nextToken(), 
        "0123456789") : "";
      String v2Str = (v2Version.hasMoreTokens()) ? 
        filter(v2Version.nextToken(), 
        "0123456789") : "";

      int intV1 = (v1Str.length() == 0) ? 0 : Integer.parseInt(v1Str);
      int intV2 = (v2Str.length() == 0) ? 0 : Integer.parseInt(v2Str);
      if (intV1 != intV2) {
        return (intV1 - intV2);
      }
    }

    return (v1Version.countTokens() - v2Version.countTokens());
  }

  public static String filter(String data, String characters) {
    if (data == null) {
      return null;
    }
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < data.length(); ++i) {
      if (characters.indexOf(data.charAt(i)) >= 0) {
        result.append(data.charAt(i));
      }
    }
    String value = result.toString();
    result = null;
    return value;
  }

  public static boolean getProperty(String key, boolean def)
  {
    String property = System.getProperty(key);
    if (property == null) {
      return def;
    }
    return "true".equalsIgnoreCase(property);
  }

  public static String getProperty(String key, String def)
  {
    String property = System.getProperty(key);
    if (property == null) {
      return def;
    }
    return property;
  }

  public static long getProperty(String key, long def)
  {
    String res = getProperty(key, null);
    if (res != null) {
      try {
        return Long.parseLong(res);
      } catch (RuntimeException e) {
        System.err.println("Value " + res + " is not a valid number.");
      }
    }
    return def;
  }

  public static byte[] concatArray(byte d1, byte d2, byte d3, byte[] d4) {
    int size = 3;
    if (d4 != null) {
      size += d4.length;
    }
    byte[] result = new byte[size];
    result[0] = d1;
    result[1] = d2;
    result[2] = d3;
    if (d4 != null) {
      System.arraycopy(d4, 0, result, 3, d4.length);
    }
    return result;
  }

  public static boolean equals(Object obj1, Object obj2)
  {
    if (obj1 == obj2) {
      return true;
    }
    if (obj1 == null) {
      return false;
    }
    return obj1.equals(obj2);
  }

  public static boolean equalsIgnoreCase(String obj1, String obj2)
  {
    if (obj1 == obj2) {
      return true;
    }
    if (obj1 == null) {
      return false;
    }
    return obj1.equalsIgnoreCase(obj2);
  }

  public static Object[] concatArray(Object v1, Object[] v2) {
    Object[] result = new Object[v2.length + 1];
    result[0] = v1;
    System.arraycopy(v2, 0, result, 1, v2.length);
    return result;
  }

  public static byte[] concatArray(byte v1, byte v2, byte[] v3) {
    byte[] result = new byte[v3.length + 2];
    result[0] = v1;
    result[1] = v2;
    System.arraycopy(v3, 0, result, 2, v3.length);
    return result;
  }

  public static byte[] concatArray(byte[] v1, byte[] v2) {
    byte[] result = new byte[v1.length + v2.length];
    System.arraycopy(v1, 0, result, 0, v1.length);
    System.arraycopy(v2, 0, result, v1.length, v2.length);
    return result;
  }

  public static <T> T[] concatArray(T[] v1, T[] v2)
  {
    Object[] result = (Object[])Array.newInstance(v1.getClass()
      .getComponentType(), v1.length + v2.length);
    System.arraycopy(v1, 0, result, 0, v1.length);
    System.arraycopy(v2, 0, result, v1.length, v2.length);
    return (T[])result;
  }

  public static <T> T[] concatArray(T[] v1, T v2)
  {
    Object[] result = (Object[])Array.newInstance(v1.getClass()
      .getComponentType(), v1.length + 1);
    System.arraycopy(v1, 0, result, 0, v1.length);
    result[(result.length - 1)] = v2;
    return (T[])result;
  }

  public static InetAddress getInetAddress(String ip)
    throws UnknownHostException
  {
    return InetAddress.getByAddress(ip2byte(ip));
  }

  public static byte[] ip2byte(String ip)
  {
    String[] ipStrList = ip.split("\\D");
    byte[] ipList = new byte[ipStrList.length];
    for (int i = 0; i < ipStrList.length; ++i) {
      if (ipStrList[i].length() == 0)
        ipList[i] = 0;
      else {
        ipList[i] = (byte)Integer.parseInt(ipStrList[i]);
      }
    }
    return ipList;
  }

  public static String byte2ip(byte[] ip)
  {
    StringBuilder result = new StringBuilder();
    if (ip.length > 0) {
      result.append(b2i(ip[0]));
      for (int i = 1; i < ip.length; ++i) {
        result.append('.');
        result.append(b2i(ip[i]));
      }
    }

    String ret = result.toString();
    result = null;
    return ret;
  }

  public static long miliToSeconds(long miliseconds)
  {
    return (miliseconds / 1000L);
  }

  public static byte[] trim(byte[] data, int maxLen) {
    if (data == null) {
      return new byte[0];
    }
    if (data.length > maxLen) {
      return getSubarray(data, 0, maxLen);
    }
    return data;
  }

  public static byte[] getHexa(String text)
  {
    List result = new ArrayList(text.length() / 5);
    StringTokenizer st = new StringTokenizer(text);
    while (st.hasMoreTokens()) {
      String item = st.nextToken();
      item = item.trim();
      if (item.startsWith("0x")) {
        item = item.substring(2);
      }
      int value = 0;
      for (int i = 0; i < item.length(); ++i) {
        value = value << 4 | 
          Byte.parseByte(String.valueOf(item.charAt(i)), 16);
      }
      result.add(Byte.valueOf((byte)value));
    }
    byte[] resultArray = new byte[result.size()];
    for (int i = 0; i < result.size(); ++i) {
      resultArray[i] = ((Byte)result.get(i)).byteValue();
    }
    return resultArray;
  }

  public static <T> T getNotNull(T[] objectList)
  {
    for (T item : objectList) {
      if (item != null) {
        return item;
      }
    }
    return null;
  }

  public static boolean isPadtecNetwork()
  {
    try
    {
      boolean result = InetAddress.getByName("medusa").toString()
        .contains("172.16.0.20");

      return result; } catch (Exception e) {
    }
    return false;
  }

  public static String semAcento(String txt)
  {
    String s = "";
    for (int i = 0; i < txt.length(); ++i) {
      char c = txt.charAt(i);
      switch (c)
      {
      case 'À':
      case 'Á':
      case 'Ã':
        c = 'A';
        break;
      case 'É':
      case 'Ê':
        c = 'E';
        break;
      case 'Í':
        c = 'I';
        break;
      case 'Ó':
      case 'Ô':
      case 'Õ':
        c = 'O';
        break;
      case 'Ú':
        c = 'U';
        break;
      case 'Ç':
        c = 'C';
        break;
      case 'à':
      case 'á':
      case 'ã':
        c = 'a';
        break;
      case 'é':
      case 'ê':
        c = 'e';
        break;
      case 'í':
        c = 'i';
        break;
      case 'ó':
      case 'ô':
      case 'õ':
        c = 'o';
        break;
      case 'ú':
        c = 'u';
        break;
      case 'ç':
        c = 'c';
      }

      s = s + c;
    }
    return s;
  }

  public static int convertITUChannel(int channel, char type) {
    channel = (61 - channel) * 2;
    if (type == 'C') {
      ++channel;
    }
    channel <<= 4;
    return channel;
  }
}