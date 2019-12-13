package cn.teclub.ha.net;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;

import cn.teclub.common.ChuyuObj;
import cn.teclub.common.ChuyuObjSer;
import cn.teclub.common.ChuyuUtil;
import cn.teclub.ha.lib.StConst;



/**
 * <h1>Describe a registered client.  </h1>
 * 
 * - Client Info is used by HA server (CS) to monitor online clients. <br/>
 * - It is also used by a client to monitor all remote clients. <br/>
 * - It is mapped to a record in database. <br/><br/>
 * 
 * @author mancook
 *
 */
public class StClientInfo
	extends ChuyuObjSer 
	implements ChuyuObj.DumpAttribute
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1598591563195751828L;

	
	/**
	 * Util is used by StModelClient in server package.
	 * 
	 * @author mancook
	 *
	 */
	public static class Util 
	{
		
		/**
		 * Convert a client-info array into a byte-buffer, excluding friends.
		 * 
		 * 
    	 * <pre> 
    	 * ATTENTION: This method NEVER returns null!
    	 * 
    	 * Converted byte[] array:
    	 * 
    	 * 	[0] [1] [2] [3] [4] [5] .... ....  [n] 	
    	 * 	|-----|<-----  bytes array    ------>|	
    	 *  | \   |                              |
    	 *     \										
    	 *      \-----> number of client-info objects				
    	 * 
    	 * </pre>
		 * 
		 * @param ci_arr
		 * @return
		 */
		public static ByteBuffer toBuffer(StClientInfo[] ci_arr){
			util.assertTrue(ci_arr.length <= 32, "ClientInfo Array Number > 32");
			util.stringFunc.toBuffer("");
			ByteBuffer max_buffer = ByteBuffer.allocate(StConst.PKT_MAX_LENGTH);
			max_buffer.putShort((short) ci_arr.length);
			for(StClientInfo ci: ci_arr){
				max_buffer.put(ci.toBuffer(false));
			}
			max_buffer.limit(max_buffer.position());
			max_buffer.rewind();
			
			final ByteBuffer obj_buf = ByteBuffer.allocate(max_buffer.limit());
			obj_buf.put(max_buffer);
			obj_buf.rewind();
			return obj_buf;
		}
		
		
		/**
		 * Convert a client-info list into a byte-buffer, excluding friends.
		 *  
		 * @param ci_list
		 * @return
		 * 
		 */
		public static ByteBuffer toBuffer(ArrayList<StClientInfo> ci_list){
			return toBuffer(ci_list.toArray(new StClientInfo[0]));
		}
		
		
		public static StClientInfo[] fromBuffer(final ByteBuffer buffer){
			util.assertTrue(buffer.remaining() >=2);
			final int NUM = buffer.getShort();
			final StClientInfo[] array = new StClientInfo[NUM];
			for(int i=0; i<NUM; i++){
				array[i] = new StClientInfo(buffer);
			}
			return array;
		}
		
		
		public static int setFlag_ClientType(int flag, StClientType clt_type){
			flag &= ~0x07;
			flag |= (clt_type.ordinal() & 0x07);
			return flag;
		}
		public static StClientType getFlag_ClientType(int flag){
			int oridnal = (flag  & 0x07 );
			return StClientType.values()[oridnal];
		}
		
		public static boolean isFlag_Online(int flag){
			if( (flag &  0x0008) == 0x0008 ){
				return true;
			}
			return false;
		}
		public static int setFlag_Online(int flag, boolean b){
			if(b){
				flag |= 0x0008;
			}else{
				flag &= ~0x0008;
			}
			return flag;
		}
		
		public static int setFlag_NatType(int flag, StJniNatType nat_type){
			flag &= ~0xF0;
			flag |= ((nat_type.ordinal() <<4) & 0xF0 );
			return flag;
		}
		public static StJniNatType getFlag_NatType(int flag){
			int oridnal = ((flag >>4) & 0x0F );
			return StJniNatType.values()[oridnal];
		}


		public static boolean isFlag_CustomerService(int flag){
			if( (flag &  0x0100) == 0x0100 ){
				return true;
			}
			return false;
		}
		public static int setFlag_CustomerServvice(int flag, boolean b){
			if(b){
				flag |= 0x0100;
			}else{
				flag &= ~0x0100;
			}
			return flag;
		}

		

		public static String flagStrLine(int flag){
			StringBuffer sbuf = new StringBuffer();
			sbuf.append("[");
			sbuf.append((isFlag_Online(flag) ? " ONLINE" : "OFFLINE" ));
			sbuf.append("," + getFlag_ClientType(flag));
			sbuf.append("," + getFlag_NatType(flag));
			sbuf.append("," + (isFlag_CustomerService(flag) ? "CusSrv":"-" ));
			sbuf.append("]");
			return sbuf.toString();
		}
		
		
		/**
		 * @deprecated
		 */
		public static String flagValueStr(int flag){
			ChuyuUtil util = ChuyuUtil.getInstance();
			String s = "0x" + util.to8CharHex(flag) + " (" + util.toBinaryString(flag) + ")";
			return s;
		}
		
		
		/**
		 * @deprecated
		 */
		public static String flagStr(int flag){
			StringBuffer sbuf = new StringBuffer();
			ArrayList<String> str_list = flagStrList(flag);
			for(String s: str_list){
				sbuf.append("\n\t");  sbuf.append(s);
			}
			return sbuf.toString();
		}
		

		/**
		 * @deprecated
		 */
		public static ArrayList<String> flagStrList(int flag){
			ArrayList<String> str_list = new ArrayList<String>();
			str_list.add("    ==== Client Flag ====");
			str_list.add("    [Flag Value:" + flagValueStr(flag) + "]");
			str_list.add("    Is online  : " + (isFlag_Online(flag) ? "Yes" : "No" ));
			str_list.add("    Client Type: " + getFlag_ClientType(flag));
			str_list.add("    NAT    Type: " + getFlag_NatType(flag));
			//str_list.add("    Is assigned: " + (isAssigned(flag) ? "Yes" : "No" ));
			//str_list.add("    Is gateway : " + (isGateway(flag) ? "Yes" : "No" ));
			//str_list.add("    Is user    : " + (isUser(flag) ? "Yes" : "No" ));
			return str_list;
		}
	}//EOF Util
	
	
	
	/**
	 * For flags in StClientHas table.
	 * @author mancook
	 *
	 */
	public static class UtilHas extends ChuyuObj 
	{
		public static int setFlag_OwnerClientType(int flag, StClientType clt_type){
			flag &= ~0x07;
			flag |= (clt_type.ordinal() & 0x07);
			return flag;
		}
		public static StClientType getFlag_OwenerClientType(int flag){
			int oridnal = (flag  & 0x07 );
			return StClientType.values()[oridnal];
		}
		
		
		public static boolean isFlag_OwnerIsAdmin(int flag){
			if( (flag &  0x0008) == 0x0008 ){
				return true;
			}
			return false;
		}
		public static int setFlag_OwnerAdmin(int flag, boolean b){
			if(b){
				flag |= 0x0008;
			}else{
				flag &= ~0x0008;
			}
			return flag;
		}
		
		
		public static int setFlag_FriendClientType(int flag, StClientType clt_type){
			flag &= ~0x0700;
			flag |= (clt_type.ordinal() << 8 ) & 0x0700;
			return flag;
		}
		public static StClientType getFlag_FriendClientType(int flag){
			int oridnal = (flag >> 8)  & 0x07;
			return StClientType.values()[oridnal];
		}
		
		
		
		public static boolean isFlag_FriendIsAdmin(int flag){
			if( (flag &  0x0800) == 0x0800 ){
				return true;
			}
			return false;
		}
		public static int setFlag_FriendAdmin(int flag, boolean b){
			if(b){
				flag |= 0x0800;
			}else{
				flag &= ~0x0800;
			}
			return flag;
		}
		
		
		public static String flagStrLine(int flag){
			StringBuffer sbuf = new StringBuffer();
			sbuf.append("[" + getFlag_OwenerClientType(flag));
			sbuf.append((isFlag_OwnerIsAdmin(flag) ? ",Adm" : ",---" ));
			sbuf.append("," + getFlag_FriendClientType(flag));
			sbuf.append((isFlag_FriendIsAdmin(flag) ? ",Adm" : ",---" ));
			sbuf.append("]");
			return sbuf.toString();
		}
		
		
//		/**
//		 * @deprecated
//		 * @param flag
//		 * @return
//		 */
//		public static String flagValueStr(int flag){
//			ChuyuUtil util = ChuyuUtil.getInstance();
//			String s = "0x" + util.to8CharHex(flag) + " (" + util.toBinaryString(flag) + ")";
//			return s;
//		}
//		
//		
//		/**
//		 * @deprecated
//		 */
//		public static String flagStr(int flag){
//			StringBuffer sbuf = new StringBuffer();
//			ArrayList<String> str_list = flagStrList(flag);
//			for(String s: str_list){
//				sbuf.append("\n\t");  sbuf.append(s);
//			}
//			return sbuf.toString();
//		}
//		
//		
//		/**
//		 * @deprecated
//		 */
//		public static ArrayList<String> flagStrList(int flag){
//			ArrayList<String> str_list = new ArrayList<String>();
//			str_list.add("    ==== Client Flag ====");
//			str_list.add("    [Flag Value:" + flagValueStr(flag) + "]");
//			str_list.add("    Owner : " + (flagIsOwnerGateway(flag) ?  "Gateway" : "User" ));
//			str_list.add("    Friend: " + (flagIsFriendGateway(flag) ? "Gateway" : "User" ));
//			str_list.add("    Is admin: " + (flagIsAdmin(flag) ? "Yes" : "No" ));
//			return str_list;
//		}
		
		
		
		/*
		public static boolean flagIsOwnerUser(int flag){
			if( (flag &  0x0001) == 0x0000 ){
				return true;
			}
			return false;
		}
		public static boolean flagIsOwnerGateway(int flag){
			if( (flag &  0x0001) == 0x0001 ){
				return true;
			}
			return false;
		}
		public static boolean flagIsFriendUser(int flag){
			if( (flag &  0x0002) == 0x0000 ){
				return true;
			}
			return false;
		}
		public static boolean flagIsFriendGateway(int flag){
			if( (flag &  0x0002) == 0x0002 ){
				return true;
			}
			return false;
		}
		public static boolean flagIsAdmin(int flag){
			if( (flag &  0x0004) == 0x0004 ){
				return true;
			}
			return false;
		}
		
		public static int flagSetOwnerGateway(int flag, boolean b){
			if(b){
				flag |= 0x0001;
			}else{
				flag &= ~0x0001;
			}
			return flag;
		}
		public static int flagSetFriendGateway(int flag, boolean b){
			if(b){
				flag |= 0x0002;
			}else{
				flag &= ~0x0002;
			}
			return flag;
		}
		public static int flagSetAdmin(int flag, boolean b){
			if(b){
				flag |= 0x0004;
			}else{
				flag &= ~0x0004;
			}
			return flag;
		}
		*/
	}
	
	
	// client info's object size is NOT fixed!
	static final int MAX_OBJLEN = 1024 *2 ;
	static final int MIN_OBJLEN = 8;
	static final int MAX_FRIEND = 64;
	static final int MAX_OWNER 	= 64;
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	
	private StClientID 	clientID;
	private String 		name, label, dscp;
	private int			flag;
	private long 		iconTS;
	private String phone, macAddr;
	
	private String 		publicIP;
	private int 		publicPort;
	
	private String		sipId, sipPasswd, sipDomain;
	
	protected HashSet<StClientHas> 	list1, list2;
	
	
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Methods
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructor
	 */
	public StClientInfo (final StClientType clt_type) {
		this.flag = Util.setFlag_ClientType(0, clt_type);
	}
	
	public StClientInfo(){ }
	
	public StClientInfo(StClientInfo ci){
		this.clientID = ci.getClientID();
		this.name = ci.getName();
		this.label = ci.getLabel();
		this.dscp = ci.getDscp();
		this.flag = ci.getFlag();
		this.iconTS = ci.iconTS;

		this.phone = ci.phone;
		this.macAddr = ci.macAddr;
		this.publicIP = ci.publicIP;
		this.publicPort = ci.publicPort;

		this.sipDomain = ci.sipDomain;
		this.sipId = ci.sipId;
		this.sipPasswd = ci.sipPasswd;

	}
	
	
	
	/**
	 * <h2> Constructor. </h2>
	 * 
	 * <p> Construct an object from the remaining of the input byte buffer.  
	 * <li> bytebuffer must have enough remaining bytes;  
	 * <li> build from the current position of byte buffer;  
	 * <li> after building, bytebuffer position is NOT reset;   
	 * </p>
	 * 
	 * @param buffer
	 */
	public StClientInfo (final ByteBuffer buffer) {
		util.assertNotNull(buffer, "input buffer is null!");
		util.assertTrue(buffer.remaining() > MIN_OBJLEN , "input buffer is too small! buffer remaining: " + buffer.remaining());
		
		int pos0 = buffer.position();
		final short obj_size = buffer.getShort();
		
		this.clientID = new StClientID(buffer.getLong());
		this.flag = buffer.getInt();
		this.publicPort = buffer.getInt();
		this.iconTS = buffer.getLong();
		
		final String[] str_arr = util.stringFunc.fromBufferToArray(buffer, 9);
		this.name 		= str_arr[0];
		this.label 		= str_arr[1];
		this.dscp 		= str_arr[2];
		this.publicIP 	= str_arr[3];
		this.sipId		= str_arr[4];
		this.sipPasswd 	= str_arr[5];
		this.sipDomain 	= str_arr[6];
		this.phone  	= str_arr[7];
		this.macAddr	= str_arr[8];
		
		int pos1 = buffer.position();
		util.assertTrue(obj_size <= MAX_OBJLEN,    "object size is too big!");
		util.assertTrue(obj_size >= (pos1 - pos0), "object size is too small!");
		if(obj_size == (pos1 - pos0)){
			return;
		}
		
		stLog.debug("Get Friend List...");
		this.list1 = new HashSet<StClientHas>();
		this.list2 = new HashSet<StClientHas>();
		int friend_num = 0;
		
		friend_num = buffer.getShort();
		util.assertTrue(friend_num <= MAX_FRIEND, "Too many friends!");
		for(int i=0; i<friend_num; i++){
			list1.add(new StClientHas(buffer));
		}
		
		friend_num = buffer.getShort();
		util.assertTrue(friend_num <= MAX_FRIEND, "Too many friends!");
		for(int i=0; i<friend_num; i++){
			list2.add(new StClientHas(buffer));
		}
	}
	
	
	
	/**
	 * <h2> Serialize client-info object into a byte array. </h2>
	 * 
	 * @param all  TRUE to serialize all members, including friend and owner ID list
	 * @return
	 */
	public ByteBuffer toBuffer(boolean all) {
		final ByteBuffer buffer = ByteBuffer.allocate(MAX_OBJLEN);
		
		buffer.putShort((short)0);		// allocate 2 bytes for the object size
		buffer.putLong(clientID.getId());
		buffer.putInt (flag);
		buffer.putInt (publicPort);
		buffer.putLong(iconTS);
		
		final String[] str_arr = new String[]{
			name, label, dscp,  (publicIP==null? "x.x.x.x":publicIP),
			sipId, sipPasswd, sipDomain, phone, 
			macAddr,
		};
		buffer.put(util.stringFunc.toBuffer(str_arr));
		
		if(all){
			util.assertTrue(list1.size() <= MAX_FRIEND, "Too many friends!");
			util.assertTrue(list2.size() <= MAX_FRIEND, "Too many friends!");
			stLog.debug("serialize friend lists");
			
			buffer.putShort((short)list1.size());
			for(StClientHas f: list1){
				buffer.put(f.toBuffer());
			}
			
			buffer.putShort((short)list2.size());
			for(StClientHas f: list2){
				buffer.put(f.toBuffer());
			}
		}
		buffer.limit(buffer.position());
		buffer.position(0); 	buffer.putShort((short)buffer.limit());  // write object size
		buffer.rewind();
		
		final ByteBuffer objbuf = ByteBuffer.allocate(buffer.limit());
		objbuf.put(buffer);
		objbuf.rewind();
		stLog.trace("Object Buffer ---- limit: " + objbuf.limit() + ", capacity: " + objbuf.capacity());
		return objbuf;
	}
	
	
	
	/**
	 * Get admin-user of a Gateway
	 * @return
	 * 
	 * @deprecated used in future
	 */
	public StClientID getAdminUser(){
		util.assertTrue(isFlag_Gateway());
		return getMaster();
	}
	
	
	/**
	 * Get admin-GW of a Monitor
	 * @return
	 * 
	 * @deprecated used in future
	 */
	public StClientID getAdminGateway(){
		util.assertTrue(isFlag_Monitor());
		return getMaster();
	}
	
	
	/**
	 * ONLY GW/Monitor has a master. User has NO master! <br/>
	 * 
	 * The master must be a user!
	 * 
	 * @return
	 */
	public StClientID getMaster(){
		/*
		final StClientType master_type;
		if(isFlag_Gateway()){
			master_type = StClientType.USER;
		}else if(isFlag_Monitor()){
			master_type =  StClientType.GATEWAY;
		}else{
			throw new StErrUserError("FATAL");
		}
		
		// master only in list2!
		for(StClientHas ch : list2){
			if(ch.getFlag_clientAType() == master_type && ch.isFlag_clientAAdmin()){
				return ch.getCltA();
			}
		}
		return null;
		*/
		
		// master only in list2!
		util.assertTrue(isFlag_Monitor() || isFlag_Gateway(), "Must be Monitor/GW");
		for(StClientHas ch : list2){
			if( ch.isFlag_clientAAdmin()){
				return ch.getCltA();
			}
		}
		return null;
	}
	
	
	public ArrayList<StClientID> getSlave() {
		/**
		 * [2016-11-7] TODO: make gateway admin monitor. 
		 * - User: slave is a gateway
		 * - GW:   slave is a monitor
		 * - Monitor: ERROR!
		 * 
		final ArrayList<StClientID> list = new ArrayList<StClientID>();
		final StClientType slave_type;
		
		if(isFlag_User()){
			slave_type =  StClientType.GATEWAY;
		}
		else if(isFlag_Gateway()){
			slave_type = StClientType.MONITOR;
		} else{
			throw new StErrUserError("FATAL: current client must be User/GW");
		}
		
		// slave only in list1!
		for(StClientHas ch : list1){
			//stLog.info("#### check client-has: " + ch);
			if(ch.getFlag_clientBType() == slave_type && ch.isFlag_clientAAdmin()){
				list.add(ch.getCltB());
			}
		}
		return list;
		*/
		
		util.assertTrue(isFlag_User() || isFlag_Gateway(), "Must be User/GW");
		final ArrayList<StClientID> list = new ArrayList<StClientID>();
		// slave only in list1!
		for(StClientHas ch : list1){
			if( ch.isFlag_clientAAdmin()){
				list.add(ch.getCltB());
			}
		}
		return list;
	}
	

	@Override
	public void dumpSetup() {
		dumpAddLine("Client ID : " + this.clientID);
		dumpAddLine("Flag : " + getFlagStrLine() );
		dumpAddLine("  -- " + StNetUtil.flagValueStr(flag) );
		
		dumpAddLine("Name : " + this.name);
		dumpAddLine("Label: " + this.label);
		dumpAddLine("Dscp : " + this.dscp);
		
		dumpAddLine("SIP ID : " + this.sipId);
		dumpAddLine("SIP Passwd: " + this.sipPasswd);
		dumpAddLine("SIP Domain: " + this.sipDomain);
		dumpAddLine("Phone Num : " + phone);
		dumpAddLine("MAC Addr  : " + macAddr);
		dumpAddLine("ICON TS   : 0x"+ Long.toHexString(iconTS));
		
		if(list1 == null && list2 == null){
			return;
		}
		dumpAddLine("Friend ID List-1: " + (list1 == null ? "<null>" : list1.size()) );
		if(list1 != null)
			for(StClientHas f: list1){
				dumpAddLine("  -- " + f );
			}
		
		dumpAddLine("Friend ID List-2: "+ (list2 == null ? "<null>" : list2.size()) );
		if(list2 != null)
			for(StClientHas f: list2){
				dumpAddLine("  -- " + f );
			}
	}
	
	
	public StringBuffer dumpSimple(){
		StringBuffer sbuf = new StringBuffer(256);
		util.dumpFunc.addDumpLine(sbuf, "ID:" + clientID + ", " + name + "/" + label + "/" + sipId );
		util.dumpFunc.addDumpLine(sbuf, StNetUtil.flagValueStr(flag)  + ", " + getFlagStrLine() );
		return sbuf;
	}
	
	public boolean isFlag_Online(){
		return Util.isFlag_Online(this.flag);
	}
	
	public StClientType getFlag_ClientType(){
		return Util.getFlag_ClientType(flag);
	}
	
	public boolean isFlag_Gateway(){
		return Util.getFlag_ClientType(flag) == StClientType.GATEWAY;
	}
	public boolean isFlag_User(){
		return Util.getFlag_ClientType(flag) == StClientType.USER;
	}
	public boolean isFlag_Monitor(){
		return Util.getFlag_ClientType(flag) == StClientType.MONITOR;
	}
	
	synchronized public StJniNatType getFlag_NatType(){
		return Util.getFlag_NatType(flag);
	}
	synchronized public void setFlag_NatType(StJniNatType nat_type){
		flag = Util.setFlag_NatType(flag, nat_type);
	}


	synchronized public boolean isFlag_CustomerService(){
		return Util.isFlag_CustomerService(flag);
	}
	synchronized public void setFlag_CustomerServvice(boolean b){
		flag = Util.setFlag_CustomerServvice(flag, b);
	}


	public String getFlagStrLine(){
		return Util.flagStrLine(this.flag);
	}
	
	
	/**
	 * <pre>
	 * NOTE: DO NOT call this method on a friend, at client-side! 
	 * Reason: friend lists of a friend is NOT sent to client.
	 * </pre>
	 * 
	 * @param f_id
	 * @return
	 */
	public boolean hasFriend(final StClientID f_id){
		for(StClientHas f: list1){
			if(f.getCltB().equalWith(f_id)){
				return true;
			}
		}
		for(StClientHas f: list2){
			if(f.getCltA().equalWith(f_id)){
				return true;
			}
		}
		return false;
	}
	
	
	public String toString(){
		StringBuffer sbuf = new StringBuffer(128);
		sbuf
			.append(name + "/" + label + "/" + (clientID == null ? "NULL" : clientID.getHex()))
			.append(getFlagStrLine())
			.append(",").append(this.sipId)
			.append(",").append(this.getPublicIP())
			//.append(":").append(this.getPublicPort())
			.append("," + (list1 == null ? "<>" : list1.size() ))
			.append("/" + (list2 == null ? "<>" : list2.size() ))
			.append("");
		return sbuf.toString();
		
	}
	
	
	////////////////////////////////////////////////////////////////////////////
	// Properties
	////////////////////////////////////////////////////////////////////////////
	
	public StClientID getClientID() {
		return clientID == null ? StClientID.GEN_ID : clientID;
	}
	public void setClientID(final StClientID id){
		this.clientID = id;
	}
	
	public int getFlag(){
		return this.flag;
	}
	public void setFlag(final int flag){
		this.flag = flag;
	}
	
	public String getName() {
		return name;
	}
	public void setName(final String name){
		this.name = name;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(final String label) {
		this.label = label;
	}
	public String getDscp() {
		return dscp;
	}
	public void setDscp(final String dscp) {
		this.dscp = dscp;
	}
	
	
	public String getSipId() {
		return sipId;
	}
	public void setSipId(final String sip_id){
		this.sipId = sip_id;
	}
	public String getSipAddress() {
		return sipId + "@" + sipDomain;
	}
	public String getSipPasswd() {
		return sipPasswd;
	}
	public void setSipPasswd(final String sip_pass){
		this.sipPasswd = sip_pass;
	}
	public String getSipDomain() {
		return sipDomain;
	}
	public void setSipDomain(final String sip_domain){
		this.sipDomain = sip_domain;
	}
	
	
	public int getPublicPort() {
		return publicPort;
	}
	public void setPublicPort(int publicPort) {
		this.publicPort = publicPort;
	}
	public String getPhone(){
		return phone;
	}
	public void setPhone(String phone_num) {
		this.phone = phone_num;
	}
	public String getMacAddr() {
		return macAddr;
	}
	public void setMacAddr(String macAddr) {
		this.macAddr = macAddr;
	}
	public long getIconTS() {
		return iconTS;
	}
	public void setIconTS(long iconTS) {
		this.iconTS = iconTS;
	}
	
	public String getPublicIP() {
		return publicIP == null ? "0.0.0.0" : publicIP;
	}

	public void setPublicIP(String publicIP) {
		this.publicIP = publicIP;
	}
	
	
	public ArrayList<StClientID> getFriendList() {
		ArrayList<StClientID> fid_list = new ArrayList<StClientID>();
		for(StClientHas ch : list1){
			fid_list.add(ch.getCltB());
		}
		for(StClientHas ch : list2){
			fid_list.add(ch.getCltA());
		}
		return fid_list;
	}
}
