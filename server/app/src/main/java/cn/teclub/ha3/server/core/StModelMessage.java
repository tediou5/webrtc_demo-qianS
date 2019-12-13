package cn.teclub.ha3.server.core;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha3.net.StMessage;


/**
 * <h1>DB table: Message</h1>
 * 
 * <pre>
 * flag:
 * 
 * 
 * </pre>
 * 
 * @author mancook
 */
@SuppressWarnings("ALL")
public class StModelMessage
		extends StMessage 
		implements ChuyuObj.DumpAttribute, StDbTable
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	

	public StModelMessage(){
		super();
	}
	
	public StModelMessage(StMessage msg){
		super(msg);
	}
	
	/*
	private long  			id;
	private StClientID  	cltA;
	private StClientID  	cltB;
	private int				flag;
	private int 			dataLen;
	private Timestamp		startTime;
	private Timestamp		endTime;
	private byte[] 			data;
	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public int getDataLen() {
		return dataLen;
	}
	public void setDataLen(int dataLen) {
		this.dataLen = dataLen;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public Timestamp getStartTime() {
		return startTime;
	}
	
	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}
	

	@Override
	public void dumpSetup() {
		dumpAddLine(">> ID:    0x" + util.to16CharHex(id));
		dumpAddLine(">> Client A (Owner) :    " + cltA);
		dumpAddLine(">> Client B (Friend):    " + cltB);
		dumpAddLine(">> Flag: 0x" + util.to8CharHex(flag));
		dumpAddLine(">> Start Time : " + startTime );
		dumpAddLine(">> End Time   : " + endTime );
		dumpAddLine(">> data length: " + data.length );
	}
	
	
	public String toString(){
		StringBuffer sbuf = new StringBuffer(128);
		sbuf.append("[0x" + Long.toHexString(id) + "]")
			.append(cltA.toString())
			.append("->" + cltB)
			.append("," + Util.flagToString(flag))
			.append("," + startTime)
			.append("/" + endTime)
			.append("");
		return sbuf.toString();
	}
	*/
}


