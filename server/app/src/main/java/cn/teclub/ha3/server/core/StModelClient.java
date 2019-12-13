package cn.teclub.ha3.server.core;

import cn.teclub.ha3.net.StClientHas;
import cn.teclub.ha3.net.StClientID;
import cn.teclub.ha3.net.StClientInfo;
import cn.teclub.ha3.net.StClientType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * Hibernate Mapping Class: tb_client
 * 
 * @author mancook
 *
 */
@SuppressWarnings("ALL")
public class StModelClient
		extends StClientInfo
		implements StDbTable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static ArrayList<StClientInfo> toClientInfoList(ArrayList<StModelClient> mc_list){
		ArrayList<StClientInfo> ci_list = new  ArrayList<StClientInfo>();
		for(StModelClient mc :mc_list){
			ci_list.add(mc);
		}
		return ci_list;
	}
	
	// Following fields are NOT serialized when sending client-info from server to client.
	// ONLY used by server to statistics.
	private String		passwd;
	private String		localIP;
	private int			localPort;
	
	private Timestamp	createTime;
	private Timestamp 	lastLogin;
	private Timestamp 	lastLogoff;
	private int			onlineTime;
	
	private StModelSipAcct 			sipAcct;
	
	private Set<StModelClientHas> relationList1, relationList2;
	
	
	
	/**
	 * Constructor <br/>
	 * 
	 * Used when adding a new client at signup
	 */
	public StModelClient(final StClientType clt_type){
		super(clt_type);
	}
	
	/**
	 * Constructor <br/>
	 * 
	 * Used by hibernate
	 */
	public StModelClient(){ }
	
	// [Theodore: 2016-11-06] DO NOT call access relationList1/2! 
	// Accessing either cause Hibernate lazy SQL for tb_client_has.
	// 
	//	public String toString(){
	//		String s0 = super.toString();
	//		return s0 + "," + (relationList1 == null ? "<>": relationList1.size()) + "/" + (relationList2 == null ? "<>": relationList2.size());
	//	}
	
	public void dumpSetup(){
		super.dumpSetup();
		dumpAddLine(">> Local Addr : " + localIP + "/" + localPort);
		dumpAddLine(">> Created At : " + createTime);
		dumpAddLine(">> Online Time: " + onlineTime);
		dumpAddLine(">> Last Login/Logoff: " + lastLogin + "/" + lastLogoff);
		dumpAddLine(">> RelationList1: " + (relationList1 == null ? "<null>": relationList1.size() ));
		dumpAddLine(">> RelationList1: " + (relationList2 == null ? "<null>": relationList2.size() ));
	}
	
	
	public void setFlag_Online(boolean online){
		setFlag(Util.setFlag_Online(getFlag(), online));
	}
	
	
	/**
	 * Called when deleting a friendship
	 * 
	 * @param f_id
	 * @return
	 */
	public StModelClientHas getFriendship(final StClientID f_id){
		for(StModelClientHas f: getRelationList1()){
			if(f.getCltB().equalWith(f_id)){
				return f;
			}
		}
		for(StModelClientHas f: getRelationList2()){
			if(f.getCltA().equalWith(f_id)){
				return f;
			}
		}
		return null;
	}
	
	
	public ArrayList<StClientID> loadRelation() {
		//stLog.info("#### create list1...");
		this.list1 = new HashSet<StClientHas>();
		for(StModelClientHas mch: relationList1){
			list1.add(mch);
		}
	
		//stLog.info("#### create list2...");
		this.list2 = new HashSet<StClientHas>();
		for(StModelClientHas mch: relationList2){
			list2.add(mch);
		}
		return super.getFriendList();
	}
	
	
	// ------------------------------------------------------------------------
	// Properties
	// ------------------------------------------------------------------------

	
	public long getRawId() {
		return getClientID().getId();
	}
	public void setRawId(long id) {
		setClientID(new StClientID(id));
	}
	
	
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
		
	
	public String getLocalIP() {
		return localIP;
	}
	public void setLocalIP(String localIP) {
		this.localIP = localIP;
	}

	
	public int getLocalPort() {
		return localPort;
	}
	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}
	
	
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(final Timestamp ts){
		this.createTime = ts;
	}
	public Timestamp getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(final Timestamp ts){
		this.lastLogin = ts;
	}
	public Timestamp getLastLogoff() {
		return lastLogoff;
	}
	public void setLastLogoff(final Timestamp ts){
		this.lastLogoff = ts;
	}
	public int getOnlineTime() {
		return onlineTime;
	}
	public void setOnlineTime(final int online_time){
		this.onlineTime = online_time;
	}
	
	
	// ------------------------------------------------------------------------
	// Relationship 
	// ------------------------------------------------------------------------
	

	/**
	 * Client-B is friend. <br/>
	 * Called when hibernate deleting this client.
	 * @return
	 */
	public Set<StModelClientHas> getRelationList1() {
		return relationList1;
	}
	public void setRelationList1(Set<StModelClientHas> relationList1) {
		this.relationList1 = relationList1;
	}


	/**
	 * Client-A is friend. <br/>
	 * Called when hibernate deleting this client.
	 * @return
	 */
	public Set<StModelClientHas> getRelationList2() {
		return relationList2;
	}
	public void setRelationList2(Set<StModelClientHas> relationList2) {
		this.relationList2 = relationList2;
	}


	public void setSipAcct(StModelSipAcct sip_acct){
		this.sipAcct = sip_acct;
		setSipId(sip_acct.getSipId());
		setSipPasswd(sip_acct.getSipPasswd());
		setSipDomain(sip_acct.getSipDomain());
	}
	
	
	public StModelSipAcct getSipAcct(){
		return sipAcct;
	}
}



