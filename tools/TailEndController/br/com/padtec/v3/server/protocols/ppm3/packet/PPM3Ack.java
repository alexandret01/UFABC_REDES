package br.com.padtec.v3.server.protocols.ppm3.packet;

/**
 * 
 * Improves a structure to the payload type Ack
 * 
 *                0             1        
        +-------------+-------------+
        |        Serial(part)       |
        +-------------+-------------+
		|        Serial(Seq)        |
        +-------------+-------------+
        | 			    			|
        |        Time Stamp         |
        |         (8 bytes)         |
        |                           |
		+-------------+-------------+
        |             .             |
        |         TLV List          |
        |             .             |
        +-------------+-------------+
 *  
 * */
public class PPM3Ack extends PPM3Response {
	
	public PPM3Payload.Type getType() { 
		
		return PPM3Payload.Type.TYPE_ACK;
		
	}
	
}