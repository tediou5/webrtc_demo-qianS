package cn.teclub.ha.net;

import cn.teclub.common.ChuyuObj;

public class StFriend extends ChuyuObj {
	public final StClientInfo ci;
	public final boolean isSlave;
	
	public StFriend(StClientInfo ci, boolean is_slave){
		this.ci = ci;
		this.isSlave = is_slave;
	}
	
	public StClientID getClientID(){
		return ci == null? null: ci.getClientID();
	}
	
	public String getName(){
		return ci == null ? null : ci.getName();
	}
	
	public String getLabel(){
		return ci == null ? null: ci.getLabel();
	}
	
	
	/**
	 * [2016-11-18] ONLY return label! 
	 * So that Gateway listview in StSDFrgHome can use ArrayAdapter<StFriend>.
	 */
	public String toString(){
		return ci.getLabel();
	}
}
