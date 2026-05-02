package br.com.padtec.v3.util;

import br.com.padtec.v3.data.SerialNumber;

public class BoardSerialControl {
  private static final int BOARD_SERIAL = 3000;

  public static boolean isValidBoardSerial(SerialNumber board)
  {
    return (board.getSeq() < BOARD_SERIAL);
  }
}