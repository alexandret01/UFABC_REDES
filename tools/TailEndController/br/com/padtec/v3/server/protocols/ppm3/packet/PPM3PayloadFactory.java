package br.com.padtec.v3.server.protocols.ppm3.packet;



public class PPM3PayloadFactory {
  public static PPM3Payload getInstance(byte code)
    throws BadPackageException
  {
    PPM3Payload.Type type = PPM3Payload.Type.getType(code);
    return getInstance(type);
  }

  public static PPM3Payload getInstance(PPM3Payload.Type type)
    throws BadPackageException
  {
    if (type != null) {
      switch (type)
      {
      case TYPE_GET:
        return new PPM3Get();
      case TYPE_SET:
        return new PPM3Set();
      case TYPE_RESPONSE:
        return new PPM3Response();
      case TYPE_TRAP:
        return new PPM3Trap();
      case TYPE_HISTORY_GET:
        return new PPM3HistoryGet();
      case TYPE_HISTORY_RESPONSE:
        return new PPM3HistoryResponse();
      case TYPE_ERROR:
        return new PPM3Error();
      case TYPE_ACK:
        return new PPM3Ack();
      }
    }
    throw new BadPackageException("Unknown payload type.");
  }
}