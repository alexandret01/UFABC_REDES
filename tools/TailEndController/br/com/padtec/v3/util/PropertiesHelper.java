/*    */ package br.com.padtec.v3.util;
/*    */ 
/*    */ import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;
/*    */ 
/*    */ public final class PropertiesHelper
/*    */ {
/*    */   public static void loadSystemDefaultProperties(File file)
/*    */     throws IOException
/*    */   {
/* 29 */     Properties curProp = System.getProperties();
/* 30 */     Properties newProp = loadProperties(file, curProp);
/* 31 */     System.setProperties(newProp);
/*    */   }
/*    */ 
/*    */   public static Properties asProperties(ResourceBundle bundle, Properties def)
/*    */   {
/* 43 */     Properties result = new Properties(def);
/* 44 */     Enumeration keyList = bundle.getKeys();
/* 45 */     while (keyList.hasMoreElements()) {
/* 46 */       String key = (String)keyList.nextElement();
/* 47 */       String value = bundle.getString(key);
/* 48 */       result.setProperty(key, value);
/*    */     }
/* 50 */     return result;
/*    */   }
/*    */ 
/*    */   public static Properties loadProperties(File file, Properties def)
/*    */     throws IOException
/*    */   {
/* 66 */     InputStream fileInputStream = new FileInputStream(file);
/* 67 */     Properties result = new Properties(def);
/* 68 */     result.load(fileInputStream);
/* 69 */     return result;
/*    */   }
/*    */ }