package br.com.padtec.v3.database;


public abstract class TableSpvlHistory
{
  public static final String BOARD_HISTORY = "SpvlBoardHistory";
  public static final String NE_HISTORY = "SpvlNeHistory";

  public static TableSpvlHistory getService() {
//    if (Functions.isLct) {
      return new TableSpvlHistoryLct();
//    }
//    return new TableSpvlHistoryDb();
  }

  public abstract Long getSyncTime(String paramString, int paramInt1, int paramInt2, byte paramByte);

  public abstract void setSyncTime(String paramString, int paramInt1, int paramInt2, byte paramByte, long paramLong);

  public abstract Integer removeSyncTime(String paramString, int paramInt1, int paramInt2, byte paramByte);
}