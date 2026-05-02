/*    */ package br.com.padtec.v3.server.protocols.util.io;
/*    */ 
import java.util.Set;
import java.util.TreeMap;

import br.com.padtec.v3.server.protocols.util.io.queue.ReceiveQueue;
import br.com.padtec.v3.server.protocols.util.io.queue.SendQueue;
import br.com.padtec.v3.server.protocols.util.io.readerwriter.AbstractReaderWriter;
import br.com.padtec.v3.server.protocols.util.io.readerwriter.TcpIpReaderWriter;

/*    */ 
/*    */ public class IoSystem
/*    */ {
/* 19 */   private static final IoSystem instance = new IoSystem();
/*    */ 
/* 25 */   private final TreeMap<String, AbstractReaderWriter> connectionPool = new TreeMap<String, AbstractReaderWriter>();
/*    */ 
/*    */   public static IoSystem getInstance()
/*    */   {
/* 22 */     return instance;
/*    */   }
/*    */ 
/*    */   public synchronized AbstractReaderWriter createConnection(ConnectionConfig conf, AbstractConnectionAlarmManager connectionAlarmManager, ReceiveQueue inputQueue, SendQueue outputQueue)
/*    */   {
/*    */     String id;
/* 39 */     if (conf instanceof TcpConnectionConfig) {
/* 40 */       TcpConnectionConfig config = (TcpConnectionConfig)conf;
/* 41 */       id = config.getIp() + ":" + config.getPort();
/* 42 */       TcpIpReaderWriter result = new TcpIpReaderWriter(config.getIp(), 
/* 43 */         config.getIp(), config.getPort(), inputQueue, outputQueue, 
/* 44 */         connectionAlarmManager);
/* 45 */       this.connectionPool.put(id, result);
/* 46 */       return result; }
///* 47 */     if (conf instanceof SerialConnectionConfig) {
///* 48 */       SerialConnectionConfig config = (SerialConnectionConfig)conf;
///* 49 */       id = config.getPort() + " " + config.getBaudrate();
///* 50 */       SerialReaderWriter result = new SerialReaderWriter(config.getPort(), 
///* 51 */         config.getPort(), config.getInitTimeout(), config.getBaudrate(), 
///* 52 */         inputQueue, outputQueue, connectionAlarmManager);
///* 53 */       this.connectionPool.put(id, result);
///* 54 */       return result;
///*    */     }
/* 56 */     return null;
/*    */   }
/*    */ 
/*    */   public synchronized Set<String> getIdList() {
/* 60 */     return this.connectionPool.keySet();
/*    */   }
/*    */ 
/*    */   public synchronized AbstractReaderWriter getConnection(String id) {
/* 64 */     return ((AbstractReaderWriter)this.connectionPool.get(id));
/*    */   }
/*    */ }