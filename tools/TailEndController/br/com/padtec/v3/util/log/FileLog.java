package br.com.padtec.v3.util.log;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;



public final class FileLog extends FileHandler
{
  public FileLog(String filename)
    throws IOException
  {
    super(filename, 91509228, 14);
    super.setFormatter(new TxtFormatter());
    super.setEncoding("US-ASCII");
    setLevel(Level.ALL);
  }
}