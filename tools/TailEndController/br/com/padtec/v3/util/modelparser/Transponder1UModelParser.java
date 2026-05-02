package br.com.padtec.v3.util.modelparser;

import java.util.LinkedHashMap;
import java.util.Map;

public class Transponder1UModelParser{
  public static boolean isTranponder1U(String model)  {
    Map<String,String> data = parse(model);
    return data.containsKey("tipo");
  }

  public static Map<String, String> getParsedModel(String model) {
    Map<String,String> data = parse(model);
    if (data.containsKey("tipo")) {
      return data;
    }
    return null;
  }

  public static boolean isExtendedInputPowerMeasure(String model) {
    Map<String,String> data = parse(model);
    String outros = (String)data.get("outros");
    if (outros == null) {
      return false;
    }
    return ("FGYX".indexOf(outros) < 0);
  }

  public static Map<String, String> parse(String model) {
    Map<String,String> result = new LinkedHashMap<String, String>();

    model = ModelParserUtils.select(model, result, "tipo", 
      new String[] { "TS" });

    model = ModelParserUtils.select(model, result, "taxa", new String[] { "01", 
      "06", "25", "100", "GE", "20" });

    model = ModelParserUtils.select(model, result, "wdm", new String[] { "C", 
      "D" });

    model = ModelParserUtils.select(model, result, "banda", new String[] { "C", 
      "S" });

    model = ModelParserUtils.select(model, result, "canal", 2);

    model = ModelParserUtils.select(model, result, "direcao", new String[] { 
      "B", "U" });

    model = ModelParserUtils.select(model, result, "outros", new String[] { 
      "D", "E", "F", "G", "R", "S", "T", "V", "Y", "X", "W", "Z" });

    model = ModelParserUtils.select(model, result, "interface cliente", 
      new String[] { "1", "2", "S" });

    model = ModelParserUtils.select(model, result, "interface enlace", 
      new String[] { "S", "L" });

    model = ModelParserUtils.select(model, result, "alimentacao", new String[] { 
      "A", "D", "F" });

    model = ModelParserUtils.select(model, result, "interface ethernet", 
      new String[] { "1", "2" });

    return result;
  }
}