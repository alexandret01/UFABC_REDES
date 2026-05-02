package br.com.padtec.v3.util;


public final class Debug
{
	static final boolean $assertionsDisabled = false;
  public static void println(String x)
  {
    if (($assertionsDisabled) || (_println(x))) 
    	return; 
    throw new AssertionError();
  }

  private static boolean _println(String x)
  {
    System.out.println(x);
    return true;
  }

  public static String getMessage(Exception e)
  {
    StackTraceElement[] stack = e.getStackTrace();
    int i = 0;
    for (; i < stack.length; ++i) {
      if (stack[i].getClassName().startsWith("br.com.padtec")) {
        break;
      }
    }
    String result = stack[i].toString() + ": " + e.toString();

    int idx = result.indexOf(10);
    if (idx > 0) {
      result = result.substring(0, idx);
    }
    return result;
  }
}