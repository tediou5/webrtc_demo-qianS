package cn.teclub.ha.net;

import java.nio.ByteBuffer;

import cn.teclub.common.ChuyuObj;
import cn.teclub.common.ChuyuObjSer;



public class StClientHas  
	extends ChuyuObjSer
	implements ChuyuObj.DumpAttribute
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	

	private StClientID  	cltA;
	private StClientID  	cltB;
	private int				flag;
	
	/**
	 * Constructor
	 */
	public StClientHas(){
	}
	
	
	/**
	 * Constructor
	 * 
	 * @param clt_a
	 * @param clt_b
	 * @param flag
	 */
	public StClientHas(StClientID clt_a, StClientID clt_b, int flag){
		this.cltA = clt_a;
		this.cltB = clt_b;
		this.flag = flag;
	}
	
	
	/**
	 * Constructor
	 * 
	 * @param buf
	 */
	public StClientHas(final ByteBuffer buf){
		this.cltA = new StClientID(buf.getLong());
		this.cltB = new StClientID(buf.getLong());
		this.flag = buf.getInt();
	}
	
	
	public ByteBuffer toBuffer(){
		ByteBuffer buf = ByteBuffer.allocate(20);
		buf.putLong(cltA.getId());
		buf.putLong(cltB.getId());
		buf.putInt(flag);
		buf.rewind();
		return buf;
	}
	
	
	
	public boolean isFlag_clientAAdmin(){
		return StClientInfo.UtilHas.isFlag_OwnerIsAdmin(flag);
	}
	
	public boolean isFlag_clientBAdmin(){
		return StClientInfo.UtilHas.isFlag_FriendIsAdmin(flag);
	}
	
	public StClientType getFlag_clientAType(){
		return StClientInfo.UtilHas.getFlag_OwenerClientType(flag);
	}
	public StClientType getFlag_clientBType(){
		return StClientInfo.UtilHas.getFlag_FriendClientType(flag);
	}
	

	public void dumpSetup() {
		//dumpAddLine("  ID:   " + util.to16CharHex(id));
		dumpAddLine("Flag:      0b" + Integer.toBinaryString(flag));
		dumpAddLine("  -- (str): " + StClientInfo.UtilHas.flagStrLine(flag) );
		dumpAddLine("Client A (Owner) :    " + cltA);
		dumpAddLine("Client B (Friend):    " + cltB);
	}
	
	
	public String toString(){
		StringBuffer sbuf = new StringBuffer(64);
		sbuf.append("[Friendship]")
			.append(" " + cltA).append("/" + getFlag_clientAType()).append("/" + (isFlag_clientAAdmin() ? "ADM":"---") )
			.append("," + cltB).append("/" + getFlag_clientBType()).append("/" + (isFlag_clientBAdmin() ? "ADM":"---") )
			.append(",0b" + Integer.toBinaryString(flag))
			.append(",0x" + util.to8CharHex(flag));
		return sbuf.toString();
	}
	
	
	// --------------------------------------------------------------------------
	// Properties
	// --------------------------------------------------------------------------
	public  int getFlag() {
		return flag;
	}
	public  void setFlag(int flag) {
		this.flag = flag;
	}
	
	public StClientID getCltA() {
		return cltA;
	}
	public void setCltA(StClientID cltA) {
		this.cltA = cltA;
	}

	public StClientID getCltB() {
		return cltB;
	}
	public void setCltB(StClientID cltB) {
		this.cltB = cltB;
	}



}
