package cn.teclub.ha.lib;

import java.nio.ByteBuffer;

import cn.teclub.common.ChuyuLongID;
import cn.teclub.common.ChuyuObj;



/**
 * </h1> Abstract General packet class. </h1>
 *  
 * Packet buffer is defined in this class.  This class defines general
 * operations for a packet.  <br/>
 * 
 * Packet header, which has a 'length' attribute, is defined in sub-class.  If
 * putter method defined in StGenPkt is used to write data into the packet,
 * 'length' attribute is updated automatically. <br/>
 * 
 * In sub-class, you must: <br/>
 *    1. define a header attribute; <br/>
 *    2. allocate buffer in its constructor; <br/>
 *    3. implement all abstract methods; <br/>
 * 
 * Build a packet: 
 * - When building a packet with given attributes, e.g. header command and
 *   packet data, you must froze it as soon as all the data has been
 *   written to buffer! 
 * - When building a packet from a byte array, you must finalized it at the end
 *   of building. 
 *
 * For a finalized (frozen) packet:
 * - You CANNOT change header and buffer of a frozen packet. 
 * - You CAN move in the buffer of a frozen packet, to get bytes from the
 *   buffer. 
 *    
 * @author mancook
 *
 */
public abstract class StGenPkt extends ChuyuObj {
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	

	
	////////////////////////////////////////////////////////////////////////////
    // Inner Classes
	////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	
	
	protected ByteBuffer 	buffer;
	protected boolean		finalized;

	////////////////////////////////////////////////////////////////////////////
    // Instance Methods
	////////////////////////////////////////////////////////////////////////////	
	
	/**
	 * Constrcutor
	 */
	public StGenPkt() {
		this.finalized = false;
	}
   
	public boolean isFrozen(){
		return this.finalized;
	}
    
	
	/**
	 * <h2> Finalize (froze) the packet. </h2>
	 * 
	 * NOTE: DO NOT call this method when building from a byte array!  <br/>
	 * 
	 * Steps: <br/>
	 *   1. set buffer limit to current position; <br/>
	 *      update header length with position pointer; <br/>
	 *   2. write header to buffer starting block; <br/>
	 *   3. TODO: calculate checksum; <br/>
	 *   4. set position pointer at the beginning of this packet; <br/>
	 *   5. set 'finalized' flag; <br/>
	 * 
	 */
    public void froze() {
    	if(this.finalized){
    		this.stLog.warn("DO NOT finalize packet again!   -- " + this.dump());
    		return;
    	}
    	
    	this.buffer.limit(this.buffer.position());
    	this.updateLength();
    	this.writeHeaderToBuffer();
    	
    	//TODO: calculate the checksum
    	byte checksum = 0;
    	this.buffer.position(0);
    	this.buffer.put(checksum);
    	
    	// at last, position buffer pointer at the beginning of packet 
    	this.buffer.rewind();
    	this.finalized = true;
    }
    
    
    
	// ------------------------------------------------------------------------------
    // Buffer Method: putter and getter 
	// ------------------------------------------------------------------------------
    
    
    /**
     * Clone the packet buffer and return it. <br/>
     * 
     * @return
     * @ 
     */
    public ByteBuffer getClonedBuffer() {
    	util.assertTrue( this.isFrozen(), 
    			"Your are trying to clone a UN-FROZEN packet! ");
    	ByteBuffer ret_buffer = ByteBuffer.allocate(this.buffer.limit());
    	this.buffer.position(0); 
    	ret_buffer.put(this.buffer); 
    	ret_buffer.position(0);
    	return ret_buffer;
    }
    
    
    /**
     * <h2>Get Packet Buffer. </h2>
     * 
     * PAY SPECIAL ATTENTION when using this method! 
     * 
     * @return
     * 
     * @deprecated
     */
    public ByteBuffer getBuffer22(){
    	return this.buffer;
    }
    
    
    protected void put(byte b) throws StExpUserError{
    	if(this.finalized){
    		throw new StExpUserError("DO NOT add into a finalized packet!");
    	}
    	this.buffer.put(b);
    	this.updateLength();
    }
    
    
    
    /**
     * Add a ByteBuffer into packet buffer <br/><br/>
     * 
     * NOTE: if input 'inbuf' is NULL, do nothing. <br/>
     * 
     * @param inbuf
     * @ 
     */
    protected void put(ByteBuffer inbuf)  {
    	if(inbuf == null){
    		return;
    	}
    	util.assertTrue(  !this.isFrozen(), "CANNOT change a frozen (finalized) packet");
    	util.assertTrue( this.buffer.remaining() >= inbuf.remaining(), 
    			"Buffer remaining space ( "+ this.buffer.remaining() + " bytes) " +
				"is NOT enough for input bytebuffer (" + inbuf.remaining() + " bytes)");
    	
    	this.buffer.put(inbuf);
    	this.updateLength();
    }
    
    protected void put(byte[] bb) throws StExpUserError{
    	util.assertTrue(  !this.isFrozen(), "CANNOT change a frozen (finalized) packet");
    	util.assertTrue( this.buffer.remaining() >= bb.length, 
    			"Buffer remaining space ( "+ this.buffer.remaining() + " bytes) " +
				"is NOT enough for input byte[] (" + bb.length + " bytes)");
    	
    	this.buffer.put(bb);
    	this.updateLength();
    }
    
    protected void putShort(short value) throws StExpUserError{
    	util.assertTrue(  !this.isFrozen(), "CANNOT change a frozen (finalized) packet");
    	this.buffer.putShort(value);
    	this.updateLength();
    }
    
    protected void putInt(int value) throws StExpUserError{
    	util.assertTrue(  !this.isFrozen(), "CANNOT change a frozen (finalized) packet");
    	this.buffer.putInt(value);
    	this.updateLength();
    }
    
    protected void putLong(long value) {
    	util.assertTrue(  !this.isFrozen(), "CANNOT change a frozen (finalized) packet");
    	this.buffer.putLong(value);
    	this.updateLength();
    }
    
    
    protected byte getByte(){
    	return this.buffer.get();
    }
    
    protected byte[] getBytes(int size){
    	byte[] ret = null;
    	int n_left = this.buffer.remaining();
    	
    	if(size > n_left){
    		this.stLog.warn("Try to get " + size + 
    				" bytes from buffer. But only " + n_left +
    				" bytes remaining in buffer");
    		ret = new byte[n_left];
    	}else{
    		ret = new byte[size];
    	}
    	
    	this.buffer.get(ret);
		return ret;
    }
    
    protected byte[] getFixedBytes(int size){
    	byte[] ret = null;
    	int n_left = this.buffer.remaining();
    	
    	if(size > n_left){
    		throw new StErrUserError("Try to get " + size + 
    				" bytes from buffer. But only " + n_left +
    				" bytes remaining in buffer");
    	}else{
    		ret = new byte[size];
    	}
    	this.buffer.get(ret);
		return ret;
    }
    
    protected short getShort(){
    	return this.buffer.getShort();
    }
    
    protected int getInt(){
    	return this.buffer.getInt();
    }
    
    protected long getLong(){
    	return this.buffer.getLong();
    }
    
    protected void posMove(int offset){
    	int cur_pos = this.buffer.position();
    	this.buffer.position(cur_pos + offset);
    }
    
    
	// ------------------------------------------------------------------------------
    // Data Functions
	// ------------------------------------------------------------------------------
   
    
    /**
     * Get a byte block from data of the packet.<br/><br/>
     * 
     * NOTE: In super class StIceGenPkt, dataGetXXX method accesses DATA in the packet buffer. <br/>
     * 
     * @param offset
     * @param size
     * @return
     * @ 
     */
	protected ByteBuffer dataGetBufferBlock(int offset, int size) {
		util.assertTrue( this.isFrozen(), "DONOT get data from an un-frozen  packet");
		byte[] dst = new byte[size];
		this.positionAtData();
		this.posMove(offset);
		this.buffer.get(dst);
		return ByteBuffer.wrap(dst);
	}
	
	
	/**
	 * Get a StLongID from data of the packet. <br/><br/>
	 * 
	 * NOTE: In super class StIceGenPkt, dataGetXXX method accesses DATA in the packet buffer. <br/>
	 * 
	 * @param offset from the beginning of cloud packet data
	 * @return
	 * @ 
	 */
	protected ChuyuLongID dataGetLongID(int offset) {
		util.assertTrue( this.isFrozen(), "DONOT get data from an un-frozen  packet");
		this.positionAtData();
		this.posMove(offset);
		long id = this.getLong();
		return new ChuyuLongID(id);
	}
	
	
	/**
	 * NOTE: In super class StIceGenPkt, dataGetXXX method accesses DATA in the packet buffer. <br/>
	 * 
	 * @param offset
	 * @return
	 * @
	 */
	protected long dataGetLong(int offset) {
		util.assertTrue( this.isFrozen(), "DONOT get data from an un-frozen  packet");
		this.positionAtData();
		this.posMove(offset);
		return this.getLong();
	}
	
	/**
	 * NOTE: In super class StIceGenPkt, dataGetXXX method accesses DATA in the packet buffer. <br/>
	 * @param offset
	 * @return
	 */
	protected short dataGetShort(int offset){
		this.positionAtData();
		this.posMove(offset);
		return this.getShort();
	}
	
	/**
	 * NOTE: In super class StIceGenPkt, dataGetXXX method accesses DATA in the packet buffer. <br/>
	 * @param offset
	 * @return
	 */
	protected int dataGetInt(int offset){
		this.positionAtData();
		this.posMove(offset);
		return this.getInt();
	}
	
	
	// ------------------------------------------------------------------------
    // Abstract Method
	// ------------------------------------------------------------------------
    
	/**
	 * <h2> Set 'header.length' with current position of packet buffer. </h2>
	 * 
	 * called by froze method. 
	 */
	protected abstract void updateLength();
	 
	
	/**
     * <h2>Write header into the starting of data buffer. </h2>
     * 
     * Called by froze method. Implemented in sub-class. After this call, buffer position
     * is set to the starting of data. 
     *          
     *                   /--------------------------------- header      
     *                  /
     *                 /         
     * [[ HEADER Attributes ]]   
     *     ||					  /---- <position at end> 
     *     ||                    /                  
     *    \||/                  |                   
     *     \/                   V                  
     * [xx xx xx xx xx xx xx xx][xx xx xx xx xx xx xx xx xx xx ... ]
     * |                       ||                                  |
     * |       HEADER          ||           DATA                   |
     * |<---- hdr len  ------->||<------- (data size) ------------>|
     * |                                                           |
     * |----------------- PACKET BUFFER ---------------------------|
     * 
     */
    protected abstract void writeHeaderToBuffer();

    
    
    /**
     * <h2> Position byte buffer pointer at the beginning of data </h2> 
     * 
     * This method is called by dataGetXXX methods. 
     */
    protected abstract void positionAtData() ;
    
	//public abstract String getCommandStr();
    //public abstract int getDataLen();
    //public abstract int getLen();
}



