/*    */ package br.com.padtec.v3.server.protocols.util.io;
/*    */ 
/*    */ public final class SerialConnectionConfig
/*    */   implements ConnectionConfig
/*    */ {
/*    */   private String port;
/*    */   private int initTimeout;
/*    */   private int baudrate;
/*    */ 
/*    */   public SerialConnectionConfig(String port, int initTimeout, int baudrate)
/*    */   {
/* 21 */     this.port = port;
/* 22 */     this.initTimeout = initTimeout;
/* 23 */     this.baudrate = baudrate;
/*    */   }
/*    */ 
/*    */   public int getBaudrate()
/*    */   {
/* 30 */     return this.baudrate;
/*    */   }
/*    */ 
/*    */   public int getInitTimeout()
/*    */   {
/* 37 */     return this.initTimeout;
/*    */   }
/*    */ 
/*    */   public String getPort()
/*    */   {
/* 44 */     return this.port;
/*    */   }
/*    */ }