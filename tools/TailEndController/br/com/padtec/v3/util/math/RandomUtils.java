/*     */ package br.com.padtec.v3.util.math;
/*     */ 
/*     */ import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Random;

import javax.swing.ImageIcon;

import br.com.padtec.v3.data.Alarm;
import br.com.padtec.v3.data.SerialNumber;
/*     */ 
/*     */ public final class RandomUtils
/*     */ {
/*  25 */   public static Random rnd = new Random();
/*     */ 
/*     */   public static Alarm getRandomAlarm()
/*     */   {
/*     */     String nome;
/*     */     String mapa;
/*     */     String ne;
/*     */     int priority;
/*  35 */     Date d = new Date();
/*     */ 
/*  38 */     switch (rnd.nextInt(5))
/*     */     {
/*     */     case 0:
/*  40 */       nome = "LOS";
/*  41 */       priority = 60;
/*  42 */       break;
/*     */     case 1:
/*  44 */       nome = "LaserOff";
/*  45 */       priority = 50;
/*  46 */       break;
/*     */     case 2:
/*  48 */       nome = "Unidade não responde";
/*  49 */       priority = 40;
/*  50 */       break;
/*     */     case 3:
/*  52 */       nome = "Telecomando Enviado";
/*  53 */       priority = 30;
/*  54 */       break;
/*     */     default:
/*  56 */       nome = "Unknown";
/*  57 */       priority = 20;
/*     */     }
/*     */ 
/*  60 */     switch (rnd.nextInt(5))
/*     */     {
/*     */     case 0:
/*  62 */       mapa = "Eunápolis";
/*  63 */       break;
/*     */     case 1:
/*  65 */       mapa = "Rio de Janeiro";
/*  66 */       break;
/*     */     case 2:
/*  68 */       mapa = "Brasília";
/*  69 */       break;
/*     */     case 3:
/*  71 */       mapa = "Banco do Brasil";
/*  72 */       break;
/*     */     default:
/*  74 */       mapa = "Unknown";
/*     */     }
/*     */ 
/*  77 */     switch (rnd.nextInt(5))
/*     */     {
/*     */     case 0:
/*  79 */       ne = "TrS25GVD-4";
/*  80 */       break;
/*     */     case 1:
/*  82 */       ne = "T25DC55-4E";
/*  83 */       break;
/*     */     case 2:
/*  85 */       ne = "SPVJ-4";
/*  86 */       break;
/*     */     case 3:
/*  88 */       ne = "PAmpC24";
/*  89 */       break;
/*     */     default:
/*  91 */       ne = "Unknown";
/*     */     }
/*     */ 
/*  94 */     boolean ack = rnd.nextInt(2) == 1;
/*  95 */     Alarm al = new Alarm(new SerialNumber(rnd.nextInt(300), rnd.nextInt(1000)), 
/*  96 */       1, new Integer(0));
/*  97 */     al.setAlarmName(nome);
/*  98 */     al.setMapName(mapa);
/*  99 */     al.setPriority(priority);
/* 100 */     al.setNeName(ne);
/* 101 */     al.setTimestamp(d);
/* 102 */     if (ack)
/* 103 */       al.setEndDate(System.currentTimeMillis());
/* 104 */     return al;
/*     */   }
/*     */ 
/*     */   public static boolean getBoolean()
/*     */   {
/* 113 */     return rnd.nextBoolean();
/*     */   }
/*     */ 
/*     */   public static int getInt(int max)
/*     */   {
/* 124 */     return rnd.nextInt(max);
/*     */   }
/*     */ 
/*     */   public static int getInt(int min, int max)
/*     */   {
/* 133 */     return (min + rnd.nextInt(1 + max - min));
/*     */   }
/*     */ 
/*     */   public static Double[] setRandomData(Double[] list, double min, double max)
/*     */   {
/* 145 */     Random rand = new Random();
/* 146 */     for (int i = 0; i < list.length; ++i) {
/* 147 */       list[i] = Double.valueOf(min + rand.nextDouble() * (max - min));
/*     */     }
/* 149 */     return list;
/*     */   }
/*     */ 
/*     */   public static <T> T[] setRandomData(T[] list, T[] values) {
/* 153 */     for (int i = 0; i < list.length; ++i) {
/* 154 */       list[i] = getRandomData(values);
/*     */     }
/* 156 */     return list;
/*     */   }
/*     */ 
/*     */   public static byte[] setRandomData(byte[] list, byte[] values) {
/* 160 */     for (int i = 0; i < list.length; ++i) {
/* 161 */       list[i] = getRandomData(values);
/*     */     }
/* 163 */     return list;
/*     */   }
/*     */ 
/*     */   public static <T> T getRandomData(T[] values) {
/* 167 */     Random rand = new Random();
/* 168 */     return values[rand.nextInt(values.length)];
/*     */   }
/*     */ 
/*     */   public static byte getRandomData(byte[] values) {
/* 172 */     Random rand = new Random();
/* 173 */     return values[rand.nextInt(values.length)];
/*     */   }
/*     */ 
/*     */   public static Boolean[] setRandomData(Boolean[] list) {
/* 177 */     Random rand = new Random();
/* 178 */     for (int i = 0; i < list.length; ++i) {
/* 179 */       list[i] = Boolean.valueOf(rand.nextBoolean());
/*     */     }
/* 181 */     return list;
/*     */   }
/*     */ 
/*     */   public static void setRandomData(Object bean)
/*     */   {
/*     */     int length;
/*     */     int i;
/* 185 */     Random rand = new Random();
/* 186 */     if (bean.getClass().isArray()) {
/* 187 */       Class arrayClass = bean.getClass().getComponentType();
/* 188 */       length = Array.getLength(bean);
/* 189 */       for (i = 0; i < length; ++i)
/*     */         try {
/* 191 */           if (arrayClass == Byte.TYPE) {
/* 192 */             	Array.set(bean, i, Byte.valueOf((byte)rand.nextInt()));
						break;
					}
/* 193 */           if (arrayClass == Double.class) {
/* 194 */             	Array.set(bean, i, Double.valueOf(rand.nextDouble())); 
						break;}
/* 195 */           if (arrayClass == Boolean.class) {
/* 196 */             	Array.set(bean, i, Boolean.valueOf(rand.nextBoolean())); 
						break; 
					}
/* 197 */           if (arrayClass == String.class)
/* 198 */            	Array.set(bean, i, "***pos[" + i + "]" + rand.nextInt(10) + "***");
/*     */         }
/*     */         catch (Exception e) {
/* 201 */           System.out.println("class:" + bean.getClass().toString());
/* 202 */           System.out.println("bean:" + bean.toString());
/* 203 */           e.printStackTrace();
/*     */         }
/*     */     }
/*     */     else {
/* 207 */       for (Method m : bean.getClass().getMethods())
/*     */         try {
/* 209 */           if (m.getName().startsWith("set")) {
/* 210 */             if (m.getParameterTypes().length == 1) {
/* 211 */               Class parameterClass = m.getParameterTypes()[0];
/* 212 */               if (parameterClass == Integer.class)
/* 213 */                 m.invoke(bean, new Object[] { 
/* 214 */                   Integer.valueOf(rand.nextInt() * 100 - 50) });
/* 215 */               else if (parameterClass == Double.class)
/* 216 */                 m.invoke(bean, new Object[] { 
/* 217 */                   Double.valueOf(rand.nextDouble() * 100.0D - 50.0D) });
/* 218 */               else if (parameterClass == Boolean.class)
/* 219 */                 m.invoke(bean, new Object[] { Boolean.valueOf(
/* 220 */                   rand.nextBoolean()) });
/* 221 */               else if (parameterClass == Character.class)
/* 222 */                 m.invoke(bean, new Object[] { 
/* 223 */                   new Character((char)rand.nextInt()) });
/* 224 */               else if (parameterClass == String.class)
/* 225 */                 m.invoke(bean, new Object[] { "***" + m.getName().substring(3) + 
/* 226 */                   rand.nextInt(10) + "***" });
/*     */             }
/*     */           }
/* 229 */           if ((m.getName().startsWith("get")) && 
/* 230 */             (m.getParameterTypes().length == 0) && 
/* 231 */             (m.getReturnType().isArray())) {
/* 232 */             Object result = m.invoke(bean, null);
/* 233 */             if (result != null)
/* 234 */               setRandomData(result);
/*     */           }
/*     */         }
/*     */         catch (UnsupportedOperationException localUnsupportedOperationException)
/*     */         {
/*     */         }
/*     */         catch (Exception e) {
/* 241 */           e.printStackTrace();
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static ImageIcon getRandomIcon()
/*     */   {
/*     */     String url;
/* 249 */     int n = rnd.nextInt(8);
/* 250 */     if (n < 7)
/* 251 */       url = "/br/com/padtec/v3/viewer/maps/images/ico_mapa_" + n + ".png";
/*     */     else
/* 253 */       url = "/br/com/padtec/v3/viewer/maps/images/ico_mapa_disable.png";
/* 254 */     return new ImageIcon(RandomUtils.class.getResource(url));
/*     */   }
/*     */ 
/*     */   public static double getDouble(double min, double max)
/*     */   {
/* 263 */     return (min + rnd.nextDouble() * (max - min));
/*     */   }
/*     */ 
/*     */   public static long getLong(long min, long max) {
/* 267 */     return (min + Math.round(rnd.nextDouble() * (max - min)));
/*     */   }
/*     */ }