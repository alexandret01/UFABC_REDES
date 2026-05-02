
package br.com.padtec.v3.util.modelparser;

import java.util.Collection;
import java.util.Map;

import br.com.padtec.v3.util.CustomResourceBundle;


public class ModelParserUtils
{
  private static String match(String remainingModel, String[] options)
  {
    for (String option : options) {
      if ((option != null) && (remainingModel.startsWith(option))) {
        return option;
      }
    }
    return null;
  }

  public static String select(String model, Map<String, String> map, String key, String[] values)
  {
    if (model == null) {
      return null;
    }
    String match = match(model, values);
    if (match == null)
    {
      return null;
    }
    String oldMatch = (String)map.put(key, match);
    if (oldMatch != null) {
      map.put(key, oldMatch + match);
    }
    model = model.substring(match.length());
    return model;
  }

  public static String selectMany(String model, Map<String, String> map, String key, String[] values)
  {
    String lastModel;
    if (model == null) {
      return null;
    }
    do
    {
      lastModel = model;

      String match = match(model, values);
      if (match == null)
      {
        return lastModel;
      }
      for (int i = 0; i < values.length; ++i) {
        if (match.equals(values[i])) {
          values[i] = null;
        }
      }
      String oldMatch = (String)map.put(key, match);
      if (oldMatch != null) {
        map.put(key, oldMatch + match);
      }
      model = model.substring(match.length());
    }
    while (model != null);
    return lastModel;
  }

  public static String select(String model, Map<String, String> map, String key, int len)
  {
    if (model == null) {
      return null;
    }
    if (model.length() >= len) {
      String match = model.substring(0, len);
      map.put(key, match);
      model = model.substring(match.length());
    }
    return model;
  }

  public static String selectNumber(String model, Map<String, String> map, String key, int len)
  {
    if (model == null) {
      return null;
    }
    if (model.length() < len) {
      return null;
    }
    String number = "0123456789";
    String match = model.substring(0, len);
    for (int i = 0; i < match.length(); ++i) {
      if ("0123456789".indexOf(match.charAt(i)) < 0)
      {
        return null;
      }
    }

    map.put(key, match);
    model = model.substring(match.length());
    return model;
  }

  public static void main(String[] args) throws ClassNotFoundException {
    CustomResourceBundle rb = CustomResourceBundle.getInstance();
    Collection<String> models = rb.getModels().values();
    for (String model : models) {
      Integer partNumber = rb.getPart(model);
      System.out.println("insert into PartNumber values (" + partNumber + ",'" + 
        model + "','" + 
        rb.getClassName(partNumber.intValue()).substring(27) + "');");
    }
  }
}