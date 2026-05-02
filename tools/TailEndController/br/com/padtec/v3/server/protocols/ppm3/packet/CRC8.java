package br.com.padtec.v3.server.protocols.ppm3.packet;

import br.com.padtec.v3.util.Functions;



public final class CRC8	{
	
	private static final char CRCtbl[] = {
        '\0', '^', '\274', '\342', 'a', '?', '\335', '\203', '\302', '\234', 
        '~', ' ', '\243', '\375', '\037', 'A', '\235', '\303', '!', '\177', 
        '\374', '\242', '@', '\036', '_', '\001', '\343', '\275', '>', '`', 
        '\202', '\334', '#', '}', '\237', '\301', 'B', '\034', '\376', '\240', 
        '\341', '\277', ']', '\003', '\200', '\336', '<', 'b', '\276', '\340', 
        '\002', '\\', '\337', '\201', 'c', '=', '|', '"', '\300', '\236', 
        '\035', 'C', '\241', '\377', 'F', '\030', '\372', '\244', '\'', 'y', 
        '\233', '\305', '\204', '\332', '8', 'f', '\345', '\273', 'Y', '\007', 
        '\333', '\205', 'g', '9', '\272', '\344', '\006', 'X', '\031', 'G', 
        '\245', '\373', 'x', '&', '\304', '\232', 'e', ';', '\331', '\207', 
        '\004', 'Z', '\270', '\346', '\247', '\371', '\033', 'E', '\306', '\230', 
        'z', '$', '\370', '\246', 'D', '\032', '\231', '\307', '%', '{', 
        ':', 'd', '\206', '\330', '[', '\005', '\347', '\271', '\214', '\322', 
        '0', 'n', '\355', '\263', 'Q', '\017', 'N', '\020', '\362', '\254', 
        '/', 'q', '\223', '\315', '\021', 'O', '\255', '\363', 'p', '.', 
        '\314', '\222', '\323', '\215', 'o', '1', '\262', '\354', '\016', 'P', 
        '\257', '\361', '\023', 'M', '\316', '\220', 'r', ',', 'm', '3', 
        '\321', '\217', '\f', 'R', '\260', '\356', '2', 'l', '\216', '\320', 
        'S', '\r', '\357', '\261', '\360', '\256', 'L', '\022', '\221', '\317', 
        '-', 's', '\312', '\224', 'v', '(', '\253', '\365', '\027', 'I', 
        '\b', 'V', '\264', '\352', 'i', '7', '\325', '\213', 'W', '\t', 
        '\353', '\265', '6', 'h', '\212', '\324', '\225', '\313', ')', 'w', 
        '\364', '\252', 'H', '\026', '\351', '\267', 'U', '\013', '\210', '\326', 
        '4', 'j', '+', 'u', '\227', '\311', 'J', '\024', '\366', '\250', 
        't', '*', '\310', '\226', '\025', 'K', '\251', '\367', '\266', '\350', 
        '\n', 'T', '\327', '\211', 'k', '5'
    };

	  public static final byte calcCRC8(byte[] data, int size)
	  {
	    int crc = 0;
	    int i = 0;
	    while (size != 0)
	    {
	      crc = CRCtbl[(crc ^ Functions.b2i(data[(i++)]))];
	      --size;
	    }

	    return Functions.i2b(crc);
	  }
	  
	  public static final byte calcChecksum (byte[] data){
		  int crc = 0;
		    int i = 0;
		    int size = data.length-1;
		    while (size != 0)
		    {
		      crc = CRCtbl[(crc ^ Functions.b2i(data[(i++)]))];
		      --size;
		    }
		    
		    return Functions.i2b(crc);
	  }
	  
	  public static boolean verifyChecksum(byte[] buffer) {
		  boolean result = true;
		  
		  if (buffer[(buffer.length - 1)] != CRC8.calcChecksum(buffer)) {
			  result = false;
//			  	throw new BadPackageException("Crc check fail.");
		  }
		  
		  return result;
	  }
	  
	
	  
	  public static void main(String[] args){
		  
		  PPM3 packet = PPM3.newPpm3();
		  packet.setIdConnection(0);
		  packet.setId(1);
		  packet.setDestination(3);
//		  packet.setLength(packet._data_.length);
		  packet.setSource(0);
		  packet.setPayload(null);
//		  byte[] buffer = packet._data_;
//		  packet.setChecksum(Functions.b2i(CRC8.calcChecksum(buffer)));
		  
		  try {
			  System.out.println(packet.getSize() +" = tamanho ");
			  if (!CRC8.verifyChecksum(packet.getBytes())) {
			  	throw new BadPackageException("Crc check fail.");
			  } else 
				  System.out.println(packet);
		  } catch (BadPackageException e) {
			  e.printStackTrace();
		  }
		  
		  
//		  
//		  for (int i = 0; i < CRCtbl.length ; i++){
//			  String s = new String(CRCtbl);
//			  String ss = new String(s.getBytes(), Charset.forName("UTF-8"));
//			  System.out.print(ss);
//			  
//		  }
	  }
}

