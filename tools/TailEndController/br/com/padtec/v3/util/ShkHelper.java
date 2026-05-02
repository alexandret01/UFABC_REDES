/*    */ package br.com.padtec.v3.util;
/*    */ 
/*    */ public class ShkHelper
/*    */ {
/*    */   public static int getContactFromCommand(int c)
/*    */   {
/* 23 */     switch (c)
/*    */     {
/*    */     case 1:
/*    */     case 80:
/*    */     case 254:
/* 27 */       return 41;
/*    */     case 2:
/*    */     case 81:
/*    */     case 253:
/* 31 */       return 42;
/*    */     case 4:
/*    */     case 82:
/*    */     case 251:
/* 35 */       return 43;
/*    */     case 8:
/*    */     case 83:
/*    */     case 247:
/* 39 */       return 44;
/*    */     case 16:
/*    */     case 84:
/*    */     case 239:
/* 43 */       return 45;
/*    */     case 32:
/*    */     case 85:
/*    */     case 223:
/* 47 */       return 46;
/*    */     case 64:
/*    */     case 86:
/*    */     case 191:
/* 51 */       return 47;
/*    */     case 87:
/*    */     case 127:
/*    */     case 128:
/* 55 */       return 48;
/*    */     }
/* 57 */     return -1;
/*    */   }
/*    */ }