package cn.teclub.ha.net;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.ArrayList;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.net.StClientID;


/**
 * <h1> Message</h1>
 * 
 * <pre>
 * [2016-11-21]
 * For all messages:
 * client-A: the client who creates this message.
 * client-B: the client who will receive this message.
 * 
 * For an APPLY message, applicant and target client-info are stored in data. 
 * client-A: applicant or its master;
 * client-B: target or its master;
 * 
 *  
 * flag: see berry02 doc;
 * </pre>
 * 
 * @author mancook
 */
public class StMessage
		extends ChuyuObj 
		implements ChuyuObj.DumpAttribute
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	
	public static int HEADER_LEN = 8+8+8+4+4+8+8; // 48 bytes
	public static int DATA_MAX = 1024*32;
	
	public static ArrayList<StMessage> fromBuffer(final ByteBuffer buf){
		util.assertNotNull(buf);
		util.assertTrue(buf.remaining() >= HEADER_LEN, "Buffer Remaining " + buf.remaining() + " < " + HEADER_LEN);
		ArrayList<StMessage> list = new ArrayList<StMessage>();
		while(buf.remaining() > 0){
			list.add(new StMessage(buf));
		}
		return list;
	}
	
	
	public static ByteBuffer toBuffer(ArrayList<StMessage> msg_list){
		ArrayList<ByteBuffer> buf_list = new ArrayList<ByteBuffer>();
		for(StMessage msg: msg_list){
			buf_list.add(msg.toBuffer());
		}
		int buf_total = 0;
		for(ByteBuffer bb: buf_list){
			buf_total += bb.limit();
		}
		
		ByteBuffer buf = ByteBuffer.allocate(buf_total);
		for(ByteBuffer bb: buf_list){
			buf.put(bb);
		}
		buf.rewind();
		return buf;
	}
	
	
	public static class Util 
	{
		public static int flagSetApply(int flag){
			flag &= ~0x03;
			flag |=  0x01;
			return flag;
		}
		
		public static int flagSetApplyApproved(int flag){
			flag &= ~0x03;
			flag |=  0x02;
			return flag;
		}
		
		public static int flagSetApplyRejected(int flag){
			flag &= ~0x03;
			flag |=  0x03;
			return flag;
		}
		
		public static int flagSetData(int flag){
			flag |=  0x04;
			return flag;
		}
		
		public static int flagSetSent(int flag, boolean send){
			if(send){
				flag |=  0x08;
			}else{
				flag &= ~0x08;
			}
			return flag;
		}
		
		public static boolean isFlagApply(int flag){
			flag &= 0x03;
			return (flag == 0x01);
		}
		public static boolean isFlagApplyApproved(int flag){
			flag &= 0x03;
			return (flag == 0x02);
		}
		public static boolean isFlagApplyRejected(int flag){
			flag &= 0x03;
			return (flag == 0x03);
		}
		
		public static boolean isFlagData(int flag){
			flag &= 0x04;
			return flag > 0;
		}
		
		public static boolean isFlagSent(int flag){
			flag &= 0x08;
			return flag > 0;
		}
		
		public static String flagToString(final int flag){
			StringBuffer sbuf = new StringBuffer(128);
			int c = flag & 0x03;
			if(c == 0x00){
				sbuf.append("----,");
			}else if(c == 0x01){
				sbuf.append("Apply,");
			}else if(c == 0x02){
				sbuf.append("ApArv,");
			}else{
				sbuf.append("ApRej,");
			}
			
			if(isFlagSent(flag)){
				sbuf.append("Sent,");
			}else{
				sbuf.append("----,");
			}
			
			if(isFlagData(flag)){
				sbuf.append("Data");
			}else{
				sbuf.append("----");
			}

			return sbuf.toString();
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	

	
	private long  			id;
	private StClientID  	cltA;
	private StClientID  	cltB;
	private int				flag;
	private int 			dataLen;
	private Timestamp		startTime;
	private Timestamp		endTime;
	private byte[] 			data;
	
	
	/**
	 * Constructor
	 */
	public StMessage() { }
	
	public StMessage(StMessage msg) { 
		this.id = msg.id;
		this.cltA = msg.cltA;
		this.cltB = msg.cltB;
		this.flag = msg.flag;
		this.dataLen = msg.dataLen;
		this.startTime = msg.startTime;
		this.endTime = msg.endTime;
		this.data = msg.data;
	}
	
	
	public StMessage(ByteBuffer buf){
		util.assertNotNull(buf);
		util.assertTrue(buf.remaining() >= HEADER_LEN, "Buffer Remaining " + buf.remaining() + " < " + HEADER_LEN);
		this.id = buf.getLong();
		this.cltA = new StClientID(buf.getLong());
		this.cltB = new StClientID(buf.getLong());
		this.flag = buf.getInt();
		this.dataLen = buf.getInt();
		this.startTime  = new Timestamp(buf.getLong());
		this.endTime    = new Timestamp(buf.getLong());
		this.data 		= new byte[dataLen];
		buf.get(data);
	}
	
	
	public ByteBuffer toBuffer(){
		final ByteBuffer buffer = ByteBuffer.allocate(HEADER_LEN + dataLen);
		buffer.putLong(id);
		buffer.putLong(cltA.getId());
		buffer.putLong(cltB.getId());
		buffer.putInt(flag);
		buffer.putInt(dataLen);
		buffer.putLong(startTime == null ? 0 : startTime.getTime());
		buffer.putLong(endTime   == null ? 0 : endTime.getTime());
		buffer.put(data);
		buffer.rewind();
		return buffer;
	}
	
	
	public boolean isFlagApply(){
		return Util.isFlagApply(flag);
	}
	public boolean isFlagApplyRejected(){
		return Util.isFlagApplyRejected(flag);
	}
	public boolean isFlagApplyApproved(){
		return Util.isFlagApplyApproved(flag);
	}
	public boolean isFlagData(){
		return Util.isFlagData(flag);
	}
	public boolean isFlagSent(){
		return Util.isFlagSent(flag);
	}
	public void setFlagApplyApproved(){
		flag = Util.flagSetApplyApproved(flag);
	}
	public void setFlagApplyRejected(){
		flag = Util.flagSetApplyRejected(flag);
	}
	public void setFlagSent(boolean is_sent){
		flag = Util.flagSetSent(flag, is_sent);
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public int getDataLen() {
		return dataLen;
	}
	public void setDataLen(int len) {
		this.dataLen = len;
	}

	public long getCltARawId() {
		return getCltA().getId();
	}
	public void setCltARawId(long id) {
		setCltA(new StClientID(id));
	}
	public long getCltBRawId() {
		return getCltB().getId();
	}
	public void setCltBRawId(long id) {
		setCltB( new StClientID(id) );
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
	
	private StClientInfo ciApplicant, ciContact;
	public StClientInfo getApplicantInfo(){
		util.assertTrue(isFlagApply() || isFlagApplyApproved() || isFlagApplyRejected());
		if(ciApplicant == null){
			final StClientInfo[] array = StClientInfo.Util.fromBuffer(ByteBuffer.wrap(data));
			this.ciApplicant = array[0];
			this.ciContact = array[1];
		}
		return ciApplicant;
	}
	
	public StClientInfo getContactInfo(){
		util.assertTrue(isFlagApply() || isFlagApplyApproved() || isFlagApplyRejected());
		if(ciContact == null){
			final StClientInfo[] array = StClientInfo.Util.fromBuffer(ByteBuffer.wrap(data));
			this.ciApplicant = array[0];
			this.ciContact = array[1];
		}
		return ciContact;
	}
	

	@Override
	public void dumpSetup() {
		dumpAddLine(">> ID: 0x" + Long.toHexString(id));
		dumpAddLine(">> Client A (Owner) :    " + cltA);
		dumpAddLine(">> Client B (Friend):    " + cltB);
		dumpAddLine(">> Flag: 0x" + util.to8CharHex(flag) + " / " + Util.flagToString(flag));
		dumpAddLine(">> data-len :   " + dataLen);
		dumpAddLine(">> Start Time:  " + startTime );
		dumpAddLine(">> End Time :   " + endTime );
		dumpAddLine(">> Msg Data:    " + (data==null ? 0: data.length) + " B" );
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
}
