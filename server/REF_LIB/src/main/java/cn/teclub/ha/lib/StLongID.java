package cn.teclub.ha.lib;

import java.nio.ByteBuffer;

import cn.teclub.common.ChuyuLongID;


public class StLongID extends ChuyuLongID{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5578213568131497192L;
	
	public static final long 	   NULL_VALUE = 0x00l;
	public static final StLongID   NULL_ID = new StLongID(0x00l);
	
	
	public StLongID(ByteBuffer buf ) {
		super(buf.getLong());
	}
	
	public StLongID(long id) {
		super(id);
	}
	
	public StLongID(StLongID id) {
		super(id.getId());
	}
	
	public StLongID(){
		super(NULL_VALUE);
	}
	
}// end of class: StLongID
