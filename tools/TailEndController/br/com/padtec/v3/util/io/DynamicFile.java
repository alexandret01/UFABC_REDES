
package br.com.padtec.v3.util.io;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Observable;



public class DynamicFile extends Observable {
  private long halfCheckInterval;
  private final File fileName;
  private FileSignature bufferSignature;
  private byte[] buffer;
  private boolean stop;
  private Exception exception;

  public DynamicFile(File fileName, long checkInterval)
    throws FileNotFoundException
  {
    if ((!(fileName.isFile())) || (!(fileName.canRead()))) {
      throw new FileNotFoundException();
    }
    this.fileName = fileName;
    this.halfCheckInterval = (checkInterval / 2L);
  }

  public void start()  {
    readFile(new FileSignature(this.fileName));

    Thread t = new Thread(new Runnable() {
      public void run() {
        while (!(DynamicFile.this.stop)) {
          DynamicFile.FileSignature currentSignature = new DynamicFile.FileSignature(DynamicFile.this.fileName);
          if (!(currentSignature.equals(DynamicFile.this.bufferSignature)))
          {
            while (true)
            {
              try {
                Thread.sleep(DynamicFile.this.halfCheckInterval);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
              DynamicFile.FileSignature newSignature = new DynamicFile.FileSignature(DynamicFile.this.fileName);
              if (currentSignature.equals(newSignature)) {
                newSignature = null;
                break;
              }
              currentSignature = null;
              currentSignature = newSignature;
            }
            DynamicFile.this.readFile(currentSignature);
          }
          try {
            Thread.sleep(DynamicFile.this.halfCheckInterval);
          }
          catch (InterruptedException localInterruptedException1)
          {
          }
        }
      }
    });
    t.setName("DynamicFile," + this.fileName + "," + this.halfCheckInterval);
    t.setDaemon(true);
    t.start();
  }

  public void stop()
  {
    this.stop = true;
  }

  private void readFile(FileSignature fileSignature)
  {
    FileInputStream input = null;
    try {
      byte[] readBuffer = new byte[(int)this.fileName.length()];
      input = new FileInputStream(this.fileName);
      int bufferPos = 0;
      while (bufferPos < readBuffer.length) {
        int bytesRead = input.read(readBuffer, bufferPos, readBuffer.length - bufferPos);
        if (bytesRead == -1) {
          break;
        }

        bufferPos += bytesRead;
      }
      this.buffer = null;
      this.bufferSignature = null;
      this.buffer = readBuffer;
      this.bufferSignature = fileSignature;
      setChanged();
      notifyObservers(this.buffer);
    } catch (Exception e) {
      this.exception = e;
    } finally {
      try {
        if (input != null)
          input.close();
      }
      catch (Exception localException2) {
      }
      input = null;
    }
  }

  public Exception getLastException()  {
    Exception result = this.exception;
    this.exception = null;
    return result;
  }

  public byte[] getData()
  {
    return this.buffer;
  }

  private static class FileSignature
  {
    private final long lastModified;
    private final long length;

    public FileSignature(File file)
    {
      this.lastModified = file.lastModified();
      this.length = file.length();
    }

    public boolean equals(Object o)
    {
      if (o instanceof FileSignature) {
        FileSignature obj = (FileSignature)o;
        return ((obj.lastModified != this.lastModified) || (obj.length != this.length));
      }
      return false;
    }
  }
}