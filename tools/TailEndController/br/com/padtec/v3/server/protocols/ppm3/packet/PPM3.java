package br.com.padtec.v3.server.protocols.ppm3.packet;

import java.io.Serializable;

import br.com.padtec.v3.util.Functions;





/**
 * This class represents a PPM3 packet.
 * 
 *
               0             1        
        +-------------+-------------+
        |   Version   |   Length    |
        +-------------+-------------+
        |   Length    |    Source   |
		+-------------+-------------+
        | Destination |    ID Con   |
		+-------------+-------------+
        |  Msg Type   |      ID     |
        +-------------+-------------+
        |  ID (cont)  |Payload (Var)|
        +-------------+-------------+
        |Payload (Var)|  Checksum   |
 		+-------------+-------------+
*/
public class PPM3 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3919296549610655261L;
	/** Offset into the PPM3 packet of the protocol version number. */
	public static final int OFFSET_VERSION = 0;
	/** Offset into the PPM3 packet of the total length of this message in bytes. */
	public static final int OFFSET_LENGTH = 1;
	/** Offset into the PPM3 packet of the source address.*/
	public static final int OFFSET_SOURCE = 3;
	/** Offset into the PPM3 packet of the destination address.*/
	public static final int OFFSET_DESTINATION = 4;
	/** Offset into the PPM3 packet of the identifier of connection.*/
	public static final int OFFSET_ID_CONNECTION = 5;
	/** Offset into the PPM3 packet of the type of message.*/
	public static final int OFFSET_MESSAGE_TYPE = 6;
	/** Offset into the PPM3 packet of the sequential identifier.*/
	public static final int OFFSET_ID = 7;
	/** Offset into the PPM3 packet of the payload.*/
	public static final int OFFSET_PAYLOAD = 9;
	
	public static final int HEADER_CRC_SIZE = 10;

	/** Byte array to storage the data*/
	protected byte[] _data_;
	/** The protocol version*/
	public static final int VERSION = 48;
	/** The source address*/
	private int source;
	/** The destination address*/
	private int destination;
	/** The identifier of connection*/
	private int idConnection;
	/** The sequential identifier */
	private long id;
	/** The payload of PPM3 packet*/
	private PPM3Payload payload;
	

	/**
	 * Creates a new PPM3 Packet object.
	 * @param size The size, in bytes, of the packet.
	 */
	private PPM3 () {
	}
	
	/**
	 * Returns the identifier of the connection.
	 *
	 * @return The idConnection value.
	 */
	  public int getIdConnection() {
		    return this.idConnection;
		  }
	  
		/**
		 * Sets the identifier of the connection.
		 * @param value An 8-bit unsigned integer.
		 */
	  public void setIdConnection(int idConn) {
	    this.idConnection = idConn;
	  }
	  
		/**
		 * Returns the PPM3 source address value.
		 *
		 * @return The PPM3 source address value.
		 */
		public int getSource() {
			return this.source;
		}

		/**
		 * Sets the PPM3 source address value.
		 * @param source An 8-bit unsigned integer.
		 */
		  public void setSource(int source)	{
		    this.source = source;
		  }
			/**
			 * Returns the PPM3 destination address value.
			 *
			 * @return The PPM3 destination address value.
			 */
		  public int getDestination() {
		    return this.destination;
		  }
			/**
			 * Sets the PPM3 source address value.
			 * @param destination An 8-bit unsigned integer.
			 */
		  public void setDestination(int destination) {
		    this.destination = destination;
		  }

			/**
			 * @return the ID.
			 */
		  public long getId() {
		    return this.id;
		  }
			/**
			 * Sets the PPM3 message total length header value.
			 *
			 * @param sequence The total PPM3 message length in bytes.
			 */
		  public void setId(long id)  {
		    this.id = id;
		  }
		  
			/**
			 * @return the payload
			 */
		  public PPM3Payload getPayload()  {
			    return this.payload;
			  }
			
			
			/**
			 * @param payload the payload to set
			 */
		  public void setPayload(PPM3Payload payload)  {
		    this.payload = payload;
		  }

		  public int getSize()  {

		    if (this.payload == null) {
		      return HEADER_CRC_SIZE;
		    }
		    return (HEADER_CRC_SIZE + this.payload.getSize());
		  }
		  
		  public byte[] getBytes()  {
			    int size = getSize();
			    long id = getId();
			    byte[] buffer = new byte[size];
			    int pos = 0;
			    buffer[(pos++)] = 48;
			    Functions.setBytes(buffer, pos, size, 2);
			    pos += 2;
			    buffer[(pos++)] = Functions.i2b(getSource());
			    buffer[(pos++)] = Functions.i2b(getDestination());
			    ++pos;
			    if (this.payload != null) {
			      buffer[(pos++)] = this.payload.getType().getCode();
			      Functions.setBytes(buffer, pos, id, 2);
			      pos += 2;
			      byte[] payloadBytes = this.payload.getBytes();
			      System.arraycopy(payloadBytes, 0, buffer, pos, payloadBytes.length);
			      pos += payloadBytes.length;
			    } else {
			      buffer[(pos++)] = 0;
			      Functions.setBytes(buffer, pos, id, 2);
			      pos += 2;
			    }
			    buffer[(buffer.length - 1)] = CRC8.calcCRC8(buffer, buffer.length - 1);
			    return buffer;
			  }

			  public static PPM3 getPPM3(byte[] buffer) throws BadPackageException  {
			    try  {
			      int pos = 0;
			      if (buffer[(pos++)] != 48) {
			        throw new BadPackageException("First byte not 0x30:" + 
			          Functions.getHexa(buffer[0]));
			      }
			      PPM3 ppm3 = newPpm3();
			      int size = (int)Functions.b2l(buffer, pos, 2);
			      pos += 2;
			      ppm3.setSource(Functions.b2i(buffer[(pos++)]));
			      ppm3.setDestination(Functions.b2i(buffer[(pos++)]));
			      ppm3.setIdConnection(Functions.b2i(buffer[(pos++)]));
			      ppm3.payload = PPM3PayloadFactory.getInstance(buffer[(pos++)]);
			      ppm3.setId((int)Functions.b2l(buffer, pos, 2));
			      pos += 2;
			      ppm3.payload.set(Functions.getSubarray(buffer, pos, size - pos - 1));
			      if (buffer[(buffer.length - 1)] != CRC8.calcCRC8(buffer, buffer.length - 1)) {
			        throw new BadPackageException("Crc check fail.");
			      }
			      return ppm3;
			    } catch (Exception e) {
			      if (e instanceof BadPackageException) {
			        throw ((BadPackageException)e);
			      }
			      throw new BadPackageException(e);
			    }
			  }



			  public String toString() {
			    StringBuilder sb = new StringBuilder();
			    sb.append("PPM3 SIZE=").append(getSize());
			    sb.append(" SRC=").append(getSource());
			    sb.append(" DST=").append(getDestination());
			    sb.append(" ID_CON=").append(getIdConnection());
			    sb.append(" ID=").append(getId());
			    if (this.payload == null) {
			      sb.append(" no_payload");
			    } else {
			      sb.append(" ").append(this.payload.getType());
			      sb.append(" PAYLOAD[").append(this.payload.toString()).append("]");
			    }
			    String result = sb.toString();
			    sb = null;
			    return result;
			  }

			  
			public void addTLV(TLV tlv) {
			    if (this.payload instanceof HasTlv)
			      ((HasTlv)this.payload).addTLV(tlv);
			  }

			  public int getTLVCount() {
			    if (this.payload instanceof HasTlv) {
			      return ((HasTlv)this.payload).getTLVCount();
			    }
			    return 0;
			  }

			  public static PPM3 newPpm3() {
			    return new PPM3();
			  }

			  public static PPM3 newPpm3(int source, int destination, long id, PPM3Payload payload)
			  {
			    PPM3 instance = new PPM3();
			    instance.setSource(source);
			    instance.setDestination(destination);
			    instance.setId(id);
			    instance.setPayload(payload);
			    return instance;
			  }
			
			

		}

		  


//	public void setPPM3Source(int source) {
//		_data_[OFFSET_SOURCE] = (byte)(source & 0xff);
//	}
	
	
//	public void setSource(int source) {
//		setPPM3Source(source);
//	}
	
	
//	public void setMsgType(PPM3Payload.Type type) {
//		_data_[OFFSET_MESSAGE_TYPE] = (byte)(type.getCode() & 0xff);
//	}
	

//	public int getIdConnection() {
//		return (_data_[OFFSET_ID_CONNECTION] & 0xff);
//	}
	

//	public void setIdConnection(int value) {
//		_data_[OFFSET_ID_CONNECTION] = (byte)(value & 0xff);
//	}
	

//	public int getPPM3Destination() {
//		return (_data_[OFFSET_DESTINATION] & 0xff);
//	}
	
//	public int getDestination(){
//		return getPPM3Destination();
//	}
	

//	public void setPPM3Destination(int destination) {
//		_data_[OFFSET_DESTINATION] = (byte)(destination & 0xff);
//	}
	
	
//	public void setDestination(int destination) {
//		setPPM3Destination(destination);
//	}
	
//	public final void setPPM3Length(int length) {
//	    _data_[OFFSET_LENGTH]     = (byte)((length >> 8) & 0xff);
//	    _data_[OFFSET_LENGTH + 1] = (byte)(length & 0xff);
//	}

//	public final int getPPM3Length() {
//		return (((_data_[OFFSET_LENGTH] & 0xff) << 8) |
//				(_data_[OFFSET_LENGTH + 1] & 0xff)); 
//	}
	

//	public final void setId(long sequence) {
//	    _data_[OFFSET_ID]     = (byte)((sequence >> 8) & 0xff);
//	    _data_[OFFSET_ID + 1] = (byte)(sequence & 0xff);
//	}


//	public final long getId() {
//		return (((_data_[OFFSET_ID] & 0xff) << 8) |
//				(_data_[OFFSET_ID + 1] & 0xff)); 
//	}
	
//	public void setPPM3Checksum(int checksum) {
//		_data_[_data_.length-1] = (byte)(checksum & 0xff);
//	}
	
	/**
	 * Returns the PPM3 checksum value.
	 *
	 * @return The PPM3 checksum value.
	 */
//	public int getPPM3Checsum() {
//		return (_data_[_data_.length-1] & 0xff);
//	}
	
	/**
	 * Computes the PPM3 checksum, optionally updating the checksum value 
	 * into the Packet.
	 *
	 * @param update Specifies whether or not to update the PPM3 checksum
	 * header after computing the checksum.  A value of true indicates
	 * the header should be updated, a value of false indicates it
	 * should not be updated.
	 * @return The computed PPM3 checksum.
	 */	
//	public final int computePPM3Checksum(boolean update){
//		int crc = CRC8.calcChecksum(_data_) & 0xff;
//		if (update)
//			setPPM3Checksum(CRC8.calcChecksum(_data_));
//		return crc;
//	}
	
	/**
	 * Same as <code>computePPM3Checksum(true);</code>
	 *
	 * @return The computed RSVP checksum value.
	 */
//	public final int computePPM3Checksum(){
//		return computePPM3Checksum(true);
//	}
	

//	public byte[] getPayloadInBytes() {
//		return payload.getBytes();
//	}

//	public void setPayload(PPM3Payload payload) {
//		this.payload = payload;
//		int sizeData = _data_.length;
//		int sizePayload = payload.getSize();
//		/*creates a new byte array*/
//		byte[] newData = new byte[sizeData+sizePayload];
//		/*copy from _data_ to newData */
//		System.arraycopy(_data_, 0, newData, 0, sizeData-1);
//		/*gets the bytes from payload*/
//		byte[] buffer = payload.getBytes();
//		/*copy the payload's data to new Data*/
//		System.arraycopy(buffer, 0, newData, sizeData-1, sizePayload);
//		/*sets the new data*/
//		setData(newData);
//		/*update packet's length*/
//		setPPM3Length(sizeData+sizePayload);
//		/*updates packet's crc*/
//		
//		computePPM3Checksum();
//	}
	
	/**
	 * Returns this packet as a String
	 * @return a string
	 * */
//	public String toString() {
//		StringBuilder sb = new StringBuilder();
//		sb.append("PPM3 SIZE=").append(getPPM3Length());
//		sb.append(" SRC=").append(getPPM3Source());
//		sb.append(" DST=").append(getPPM3Destination());
//		sb.append(" ID_CON=").append(getIdConnection());
//		sb.append(" ID=").append(getId());
//		if (this.payload == null) {
//			sb.append(" no_payload");
//		} else {
//			sb.append(" ").append(this.payload.getType());
//		sb.append(" PAYLOAD[").append(this.payload.toString()).append("]");
//		}
//		String result = sb.toString();
//		sb = null;
//		return result;
//	}

	 /** Returns the size of the packet
	 * @return <code>int</code> Size of the packet
	 * */
//	public int getSize() {
//
//		return getPPM3Length();
//	}

//	public byte[] getBytes(){
//		int size = getSize();
//		long id = getId();
//		byte[] buffer = new byte[size];
//		int pos = 0;
//		buffer[(pos++)] = 48;
//		Functions.setBytes(buffer, pos, size, 2);
//		pos += 2;
//		buffer[(pos++)] = Functions.i2b(getSource());
//		buffer[(pos++)] = Functions.i2b(getDestination());
//		++pos;
//		if (this.payload != null) {
//			buffer[(pos++)] = this.payload.getType().getCode();
//			Functions.setBytes(buffer, pos, id, 2);
//			pos += 2;
//			byte[] payloadBytes = this.payload.getBytes();
//			System.arraycopy(payloadBytes, 0, buffer, pos, payloadBytes.length);
//			pos += payloadBytes.length;
//		} else {
//			buffer[(pos++)] = 0;
//			Functions.setBytes(buffer, pos, id, 2);
//			pos += 2;
//		}
//		buffer[(buffer.length - 1)] = CRC8.calcCRC8(buffer, buffer.length - 1);
//		return buffer;
//	}
//
//	public static PPM3 getPPM3(byte[] buffer) throws BadPackageException {
//		try	{
//			int pos = 0;
//			if (buffer[(pos++)] != 48) {
//				throw new BadPackageException("First byte not 0x30:" + Functions.getHexa(buffer[0]));
//			}
//			PPM3 ppm3 = newPpm3();
//			int size = (int)Functions.b2l(buffer, pos, 2);
//			pos += 2;
//			ppm3.setPPM3Source(Functions.b2i(buffer[(pos++)]));
//			ppm3.setPPM3Destination(Functions.b2i(buffer[(pos++)]));
//			ppm3.setIdConnection(Functions.b2i(buffer[(pos++)]));
//			PPM3Payload payload = PPM3PayloadFactory.getInstance(buffer[(pos++)]);
//
//			ppm3.setId((int)Functions.b2l(buffer, pos, 2));
//			pos += 2;
//			payload.set(Functions.getSubarray(buffer, pos, size - pos - 1));
//			ppm3.setPayload(payload);
//			if (buffer[(buffer.length - 1)] != CRC8.calcCRC8(buffer, buffer.length - 1)) {
//				throw new BadPackageException("Crc check fail.");
//			}
//			return ppm3;
//		} catch (Exception e) {
//			if (e instanceof BadPackageException) {
//				throw ((BadPackageException)e);
//			}
//			throw new BadPackageException(e);
//		}
//	}
//
//	public void addTLV(TLV tlv)
//	{
//		if (this.payload instanceof HasTlv<?>){
//			((HasTlv<TLV>)this.payload).addTLV(tlv);
//			setPayload(payload);	
//		}
//		
//	}
//
//	public int getTLVCount()
//	{
//		if (this.payload instanceof HasTlv) {
//			return ((HasTlv)this.payload).getTLVCount();
//		}
//		return 0;
//	}
//
//	public static PPM3 newPpm3() {
//		return new PPM3();
//	}
//
//	public static PPM3 newPpm3(int source, int destination, long id, PPM3Payload payload){
//		PPM3 instance = new PPM3();
//		instance.setVersion();
//		instance.setPPM3Source(source);
//		instance.setPPM3Destination(destination);
//		instance.setId(id);
//		instance.setPayload(payload);
//		instance.setMsgType(payload.getType());
//		return instance;
//	}



	 



	  