
package br.com.padtec.v3.server.protocols.ppm2v2;

import java.io.IOException;

public class BadPackageException extends IOException {
  private static final long serialVersionUID = 1L;

  public BadPackageException()  {
    super("Bad PPM2v2 Package");
  }

  public BadPackageException(String message)
  {
    super(message);
  }
}