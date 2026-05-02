/*    */ package br.com.padtec.v3.server.protocols.util.io;
/*    */ 
/*    */ public final class TcpConnectionConfig implements ConnectionConfig
/*    */ {
/*    */   private String ip;
/*    */   private int port;
/*    */ 
/*    */   public TcpConnectionConfig(String ip, int port)
/*    */   {
/* 19 */     this.ip = ip;
/* 20 */     this.port = port;
/*    */   }
/*    */ 
/*    */   public String getIp()
/*    */   {
/* 27 */     return this.ip;
/*    */   }
/*    */ 
/*    */   public int getPort()
/*    */   {
/* 34 */     return this.port;
/*    */   }
/*    */ }