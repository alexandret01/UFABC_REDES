package br.com.padtec.v3.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.UIManager;

public final class Msg {
  private static final String bundle_pt_BR = "br.com.padtec.v3.util.strings_pt_BR";
  private static final String bundle_en_US = "br.com.padtec.v3.util.strings_en_US";
  private static final String bundle_es_ES = "br.com.padtec.v3.util.strings_es_ES";
  private static ResourceBundle bundle;
  private static boolean localeSet;

  public static void setLocale(Locale l)
  {
    localeSet = true;
    if (l.getLanguage().equals("en")) {
      bundle = ResourceBundle.getBundle("br.com.padtec.v3.util.strings_en_US");
    }
    else if (l.getLanguage().equals("es")) {
      bundle = ResourceBundle.getBundle("br.com.padtec.v3.util.strings_es_ES");

      if (!(Functions.isServer)) {
        UIManager.put("OptionPane.yesButtonText", "Sí");
        UIManager.put("OptionPane.noButtonText", "No");
        UIManager.put("OptionPane.cancelButtonText", "Cancelar");
        UIManager.put("OptionPane.okButtonText", "Ok");

        UIManager.put("FileChooser.lookInLabelText", "Buscar en:");
        UIManager.put("FileChooser.fileNameLabelText", "Archivo:");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Tipos de Archivos:");
        UIManager.put("FileChooser.upFolderToolTipText", "Carpetas");
        UIManager.put("FileChooser.upFolderAccessibleName", "Carpetas");
        UIManager.put("FileChooser.homeFolderToolTipText", "Mis Documentos");
        UIManager.put("FileChooser.homeFolderAccessibleName", "Mis Documentos");

        UIManager.put("FileChooser.newFolderToolTipText", "Crear Nueva Carpeta");
        UIManager.put("FileChooser.newFolderAccessibleNam", "Nueva Carpeta");
        UIManager.put("FileChooser.listViewButtonToolTipText", "Lista");
        UIManager.put("FileChooser.listViewButtonAccessibleName", "Lista");
        UIManager.put("FileChooser.detailsViewButtonToolTipText", "Detalles");

        UIManager.put("FileChooser.detailsViewButtonAccessibleName", "Detalles");
      }
    } else {
      bundle = ResourceBundle.getBundle("br.com.padtec.v3.util.strings_pt_BR");

      if (!(Functions.isServer)) {
        UIManager.put("OptionPane.yesButtonText", "Sim");
        UIManager.put("OptionPane.noButtonText", "Não");
        UIManager.put("OptionPane.cancelButtonText", "Cancelar");
        UIManager.put("OptionPane.okButtonText", "Ok");

        UIManager.put("FileChooser.lookInLabelText", "Procurar em:");
        UIManager.put("FileChooser.fileNameLabelText", "Arquivo:");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Tipos de Arquivos:");
        UIManager.put("FileChooser.upFolderToolTipText", "Pastas");
        UIManager.put("FileChooser.upFolderAccessibleName", "Pastas");
        UIManager.put("FileChooser.homeFolderToolTipText", "Meus Documentos");

        UIManager.put("FileChooser.homeFolderAccessibleName", "Meus Documentos");
        UIManager.put("FileChooser.newFolderToolTipText", "Cria Nova Pasta");
        UIManager.put("FileChooser.newFolderAccessibleNam", "Nova Pasta");
        UIManager.put("FileChooser.listViewButtonToolTipText", "Lista");
        UIManager.put("FileChooser.listViewButtonAccessibleName", "Lista");
        UIManager.put("FileChooser.detailsViewButtonToolTipText", "Detalhes");

        UIManager.put("FileChooser.detailsViewButtonAccessibleName", "Detalhes");
      }
    }
    Locale.setDefault(l);
  }

  public static String getString(String key)
  {
    if (!(localeSet))
      setLocale(Locale.getDefault());
    try
    {
      return bundle.getString(key); } catch (MissingResourceException e) {
    }
    return '!' + key + '!';
  }
}