package cn.teclub.ha.request;

import cn.teclub.common.ChuyuLongID;

/**
 * <h2>Session ID</h2>
 * 
 * @author mancook
 *
 */
public  class StRequestID extends ChuyuLongID {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6419261359194106201L;
	public final static StRequestID NULL_ID = new StRequestID(0);
	
	public StRequestID(long id) {
		super(id);
	}
} 

