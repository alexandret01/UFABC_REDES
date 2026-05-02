
package br.com.padtec.v3.util.math;

import java.math.BigInteger;
import java.util.Arrays;

import br.com.padtec.v3.data.ne.NE;
import br.com.padtec.v3.util.Functions;

public class OverflowChk
{
  public static boolean debug = false;

  public static BigInteger getCorrectValue(byte[] data, BigInteger lastValueFromBean, int arrayPositionInit, int arrayPositionEnd, NE ne)
  {
    boolean lookForOverFlow = true;

    int counterArraySize = arrayPositionEnd - arrayPositionInit + 1;

    byte[] lastValueFromBeanAsArray = lastValueFromBean.toByteArray();
    byte[] newValueFromHandlerAsArray = Functions.getSubarray(data, 
      arrayPositionInit, counterArraySize);
    BigInteger newValueFromHandler = new BigInteger(1, 
      newValueFromHandlerAsArray);
    byte[] maxValueAllowedArray = new byte[counterArraySize];
    Arrays.fill(maxValueAllowedArray, (byte)-1);
    BigInteger maxValueAllowed = new BigInteger(1, maxValueAllowedArray);

    if (debug)
    {
      System.out.println("");
      System.out
        .println("<=============================================================================================================>");
      System.out.println("");
      System.out.println("* NE DEBUGADO: " + ne.getName() + " - " + 
        ne.getSerial().toShortString() + " - [MAX ARRAY VALUE: " + 
        maxValueAllowed + "]");
      System.out.println("");
      System.out.println("-------- DADOS DO BEAN    --------");
      System.out.println("* Array que tenho    : " + 
        Functions.getHexa(lastValueFromBeanAsArray, 0, 
        lastValueFromBeanAsArray.length));
      System.out.println("* Valor em decimanl  : " + lastValueFromBean);
      System.out.println("-------- DADOS DO HANDLER --------");
      System.out.println("* Array que chegou   : " + 
        Functions.getHexa(newValueFromHandlerAsArray, 0, 
        newValueFromHandlerAsArray.length));
      System.out.println("* Valor em decimal   : " + newValueFromHandler);
    }

    if (counterArraySize == 6) {
      return newValueFromHandler;
    }

    if (newValueFromHandler.compareTo(BigInteger.ZERO) == 0)
    {
      return BigInteger.ZERO;
    }

    if ((newValueFromHandler.compareTo(BigInteger.valueOf(-1L)) == 0) || 
      (newValueFromHandler.compareTo(maxValueAllowed) == 0))
    {
      return BigInteger.valueOf(-1L);
    }

    if (lastValueFromBeanAsArray.length < counterArraySize)
    {
      return newValueFromHandler;
    }

    if (lookForOverFlow) {
      boolean overFlow = false;

      if (newValueFromHandler.compareTo(lastValueFromBean) == -1) {
        overFlow = true;
      }

      if (lastValueFromBean.add(newValueFromHandler)
        .compareTo(maxValueAllowed) >= 0) {
        overFlow = true;
      }

      if (!(overFlow))
      {
        return newValueFromHandler;
      }

      BigInteger lastPktValue = lastValueFromBean.mod(maxValueAllowed);

      int overFlowCount = lastValueFromBean.divide(maxValueAllowed)
        .intValue();
      if (newValueFromHandler.compareTo(lastPktValue) < 0)
      {
        ++overFlowCount;
      }

      BigInteger baseValue = maxValueAllowed.multiply(
        BigInteger.valueOf(overFlowCount));

      return baseValue.add(newValueFromHandler);
    }

    return newValueFromHandler;
  }
}