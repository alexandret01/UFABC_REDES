package br.com.padtec.v3.util;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.logging.Level;

import br.com.padtec.v3.data.SerialNumber;
import br.com.padtec.v3.data.impl.NE_Impl;
import br.com.padtec.v3.data.impl.Unknown_Impl;
import br.com.padtec.v3.util.log.Log;
import br.com.padtec.v3.util.modelparser.TransponderModelParser;

public final class PartNumber
{
  private static final String num = "0123456789";

  public static NE_Impl getInstance(SerialNumber s)   {
    return getInstance(s, true);
  }

  public static NE_Impl getUnknown(SerialNumber sn, boolean persistant)
  {
    NE_Impl n;
    synchronized (PartNumber.class) {
      try {
//        NE_Impl.objectsPersistant = persistant;
        n = new Unknown_Impl(sn);
      } finally {
//        NE_Impl.objectsPersistant = true;
      }
    }
    return n;
  }

  public static Class<?> getClass(int partNumber) throws ClassNotFoundException
  {
    String className = CustomResourceBundle.getInstance().getClassName(
      partNumber);
    return Class.forName(className);
  }

  public static NE_Impl getInstance(SerialNumber s, boolean persistant)
  {
    try {
      NE_Impl n;
      Class<?> c = getClass(s.getPart());
      Constructor<?> ctr = c.getConstructor(new Class[] { SerialNumber.class });

      synchronized (PartNumber.class) {
        try {
//          NE_Impl.objectsPersistant = persistant;
          n = (NE_Impl)ctr.newInstance(new Object[] { s });
        } finally {
//          NE_Impl.objectsPersistant = true;
        }
      }
      return n;
    } catch (MissingResourceException e) {
      if (Functions.isLct) {
        return getUnknown(s, persistant);
      }
      return null;
    }
    catch (ClassNotFoundException e) {
      if (Functions.isLct) {
        return getUnknown(s, persistant);
      }
      return null;
    }
    catch (Exception e) {
      Log.getInstance(1).log(Level.SEVERE, 
        "Fail in Partnumber.getInstance: " + s + " " + persistant, e); }
    return null;
  }

  public static void main(String[] args)
    throws ClassNotFoundException
  {

    Map<String,Set<String>> modelList = new TreeMap<String, Set<String>>();

    CustomResourceBundle rb = CustomResourceBundle.getInstance();
    Map<Integer,String> partList = rb.getModels();
    for (Iterator<Entry<Integer,String>> localIterator = partList.entrySet().iterator(); localIterator.hasNext(); ) { 
        Entry<Integer, String> item = localIterator.next();
      String model = generalize(item.getValue());
      Set<String> modelParts = (Set<String>)modelList.get(model);
      if (modelParts == null) {
        modelParts = new TreeSet<String>();
        modelList.put(model, modelParts);
      }
      modelParts.add(rb.getClassName((item.getKey()).intValue()).substring(27));
    }

    for (Iterator<Entry<String,Set<String>>> localIterator = modelList.entrySet().iterator(); localIterator.hasNext(); ) { 
        Entry<String,Set<String>> item = localIterator.next();
      System.out.print(item.getKey());
      System.out.print('\t');
      if ((item.getValue()).size() > 1) {
        System.out.print((item.getValue()).toString());
        System.out.print(" <<< WARNING\n");
      } else {
        System.out.print(item.getValue().iterator().next());
        System.out.println();
      }
    }
  }

  private static String generalize(String value)
  {
    Map<String,String> parsed;
    if ((parsed = TransponderModelParser.getParsedModel(value)) != null) {
      generalizeNumberField(parsed, "canal", "cc");
      generalizeNumberField(parsed, "interface cliente", "cc");
      return parsedModelToString(parsed); }
//    if ((parsed = Transponder1UModelParser.getParsedModel(value)) != null) {
//      generalizeNumberField(parsed, "canal", "cc");
//      return parsedModelToString(parsed); }
//    if ((parsed = MuxponderModelParser.getParsedModel(value)) != null) {
//      generalizeNumberField(parsed, "canal", "cc");
//      return parsedModelToString(parsed); }
//    if ((parsed = CombinerModelParser.getParsedModel(value)) != null) {
//      generalizeNumberField(parsed, "canal", "cc");
//      return parsedModelToString(parsed); }
//    if ((parsed = OadmModelParser.getParsedModel(value)) != null) {
//      generalizeNumberField(parsed, "canal inicial", "ii");
//      generalizeField(parsed, "espacamento", "e");
//      generalizeNumberField(parsed, "canais", "nn");
//      return parsedModelToString(parsed);
//    }
    return value;
  }

  private static void generalizeField(Map<String, String> parsedModel, String fieldName, String newValue)
  {
    String value = (String)parsedModel.get(fieldName);
    if (value != null)
      parsedModel.put(fieldName, newValue);
  }

  private static void generalizeNumberField(Map<String, String> parsedModel, String fieldName, String newValue)
  {
    String value = (String)parsedModel.get(fieldName);
    if ((value == null) || (value.length() != 2) || 
      ("0123456789".indexOf(value.charAt(0)) < 0) || 
      ("0123456789".indexOf(value.charAt(1)) < 0)) return;
    parsedModel.put(fieldName, newValue);
  }

  private static String parsedModelToString(Map<String, String> parsedModel)
  {
    StringBuilder sb = new StringBuilder();
    for (Entry<String,String> item : parsedModel.entrySet()) {
      sb.append(item.getValue());
    }
    String result = sb.toString();
    sb = null;
    return result;
  }
}