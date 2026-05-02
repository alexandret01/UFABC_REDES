package br.com.padtec.v3.util.modelparser;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import br.com.padtec.v3.util.CustomResourceBundle;
import br.com.padtec.v3.util.text.TextUtils;



public class TransponderModelParser {
  public static boolean isTransponder(String model)
  {
    Map<String, String> data = parse(model);
    return data.containsKey("taxa");
  }

  public static Map<String, String> getParsedModel(String model) {
    Map<String, String> data = parse(model);
    if (data.containsKey("taxa")) {
      return data;
    }
    return null;
  }

  public static boolean isShowPowerSide2(String model) {
    Map<String, String> data = parse(model);
    String caracteristica = (String)data.get("caracteristica");
    if ((caracteristica == null) || (!(caracteristica.contains("S")))) {
      return true;
    }
    String interfaceCliente = (String)data.get("interface cliente");
    if ((interfaceCliente == null) || (!(interfaceCliente.contains("S")))) {
      return true;
    }
    String alcance = (String)data.get("alcance");

    return ((alcance != null) && (alcance.contains("S")));
  }

  public static String getSide2Channel(Map<String, String> parsed)
  {
    String canal = (String)parsed.get("interface cliente");

    if ((canal != null) && (canal.length() == 2) && 
      (TextUtils.filter(canal, "0123456789").length() == 2)) {
      String banda = (String)parsed.get("banda");
      if (banda != null) {
        canal = banda + canal;
      }
    }
    return canal;
  }

  public static Map<String, String> parse(String model) {
    Map<String, String> result = new LinkedHashMap<String, String>();

    model = 
      ModelParserUtils.select(model, result, "tipo", new String[] { "T" });

    model = ModelParserUtils.select(model, result, "taxa", new String[] { "01", 
      "06", "GE", "20", "25", "100" });

    model = ModelParserUtils.select(model, result, "wdm", new String[] { "C", 
      "D" });

    String modelTemp = ModelParserUtils.select(model, result, "banda", 
      new String[] { "O", "E", "S", "C", "L" });
    if (modelTemp != null) {
      model = modelTemp;
      modelTemp = ModelParserUtils.select(model, result, "canal", new String[] { 
        "TC", "TH", "T" });
      if (modelTemp != null)
        model = modelTemp;
      else {
        model = ModelParserUtils.selectNumber(model, result, "canal", 2);
      }
    }

    model = ModelParserUtils.select(model, result, "-", new String[] { "-" });

    model = ModelParserUtils.select(model, result, "altura", new String[] { 
      "1", "3", "4" });

    modelTemp = ModelParserUtils.select(model, result, "familia", new String[] { 
      "F", "G", "P" });
    if (modelTemp != null) {
      model = modelTemp;
      if ("F".equals(result.get("familia")))
      {
        model = ModelParserUtils.select(model, result, "familia-tipo", 
          new String[] { "R", "T" });
      }
      else {
        model = ModelParserUtils.select(model, result, "familia-tipo", 
          new String[] { "R", "T" });
      }
    }
    else
    {
      modelTemp = ModelParserUtils.select(model, result, "familia-tipo", 
        new String[] { "B", "O" });
      if (modelTemp != null) {
        model = modelTemp;
      }

      modelTemp = ModelParserUtils.select(model, result, "familia-tipo", 
        new String[] { "D" });
      if (modelTemp != null) {
        model = modelTemp;
      }
    }

    modelTemp = ModelParserUtils.selectMany(model, result, "caracteristica", 
      new String[] { "R", "E", "F", "L", "S", "T" });
    if (modelTemp != null) {
      model = modelTemp;
    }

    modelTemp = ModelParserUtils.selectNumber(model, result, 
      "interface cliente", 2);
    if (modelTemp != null) {
      model = modelTemp;
    }
    else {
      modelTemp = ModelParserUtils.select(model, result, "interface cliente", 
        new String[] { "CTC", "CTH", "CT", "LT" });
      if (modelTemp != null) {
        model = modelTemp;
      }
      else {
        modelTemp = ModelParserUtils.select(model, result, "interface cliente", 
          new String[] { "1", "2", "3", "4", "5", 
          "S" });
        if (modelTemp != null) {
          model = modelTemp;
        }
      }
    }

    modelTemp = ModelParserUtils.select(model, result, "alcance", new String[] { 
      "S", "L" });
    if (modelTemp != null) {
      model = modelTemp;
    }

    modelTemp = ModelParserUtils.select(model, result, "modulacao", 
      new String[] { "E" });
    if (modelTemp != null) {
      model = modelTemp;
    }

    if ((model != null) && (model.length() >= 1) && (model.charAt(0) == '-')) {
      model = model.substring(1);
    }

    modelTemp = ModelParserUtils.select(model, result, "customizacao", 
      new String[] { "12V" });
    if (modelTemp != null) {
      model = modelTemp;
    }

    return result;
  }

  public static void main(String[] args) {
    CustomResourceBundle rb = CustomResourceBundle.getInstance();
    Collection<String> models = rb.getModels().values();
    
    for (String model : models){
//    	System.out.println("model: " +model);
      if (isTransponder(model)) {
        Map<String, String> res = parse(model);
        if (res!= null)
        System.out.println(rb.getPart(model) + "\t" + model + "\t" + res + "\t" + 
          isShowPowerSide2(model));
      } else {
    	  System.out.println("não é um transponder");
      }
    }
  }
}