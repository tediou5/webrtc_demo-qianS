package cn.teclub.ha.client.rpr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import cn.teclub.common.ChuyuObjSer;
import cn.teclub.ha.client.StcException;
import cn.teclub.ha.client.StcException.ExpRemoteClientNoFound;
import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.net.StFriend;
import cn.teclub.ha.net.StJniNatType;



/**
 * <h1>Store RPR variables </h1>
 *
 * <pre>
 * The main RPR var: local client info; friend list; rpr-state;
 *
 * This object is serialized for displaying when client is offline.
 *
 * </pre>
 * 
 * 
 * @author mancook
 *
 */
public class StcSharedVar extends ChuyuObjSer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7271271367770158007L;

	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	
	private static StcSharedVar _ins = new StcSharedVar();
	static StcSharedVar getInstance()  {  
		return _ins; 
	}
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Members
	////////////////////////////////////////////////////////////////////////////	
	
	
	/**
	 * <pre>
	 * Client may lose connection with server, due to various reasons.
	 * In this case, if 'autoLogin' is TRUE, main pulse will re-login by itself. 
	 * </pre>
	 */
	private boolean			autoLogin = true;
	
	
	private StClientInfo 	local = null;
	private String 			passwd, publicIP;
	private String			localIP;

	private final ConcurrentHashMap<StClientID, StClientInfo>    mapRemoteClient = new ConcurrentHashMap<>();
	
	private StcRprState		stat = StcRprState.OFFLINE;
	private boolean			uploading = false;


	/**
	 * TRUE: when a CLT_STATUS is sent and no response is received
	 */
	private boolean			sendStatus= false;
	
	/**
	 * <pre>
	 * [2017-3-16] Help to determine if client is REALLY ONLINE.
	 * 
	 * You cannot determine whether client is REALLY ONLINE, if RPR state is online.
	 * Especially on an Android device, where the app gets less and less CPU time if it runs in background.
	 * In this case, the RPR state is IDEAL, but server has closed the connection. Client has not enough 
	 * CPU time to process this event. 
	 * 
	 * Updated when:
	 * - LOGIN Success;
	 * - Any packet is received, when client is LOGIN;
	 * 
	 * </pre>
	 */
	private long 			refreshTime;
	
	
	/**
	 * Constructor
	 */
	private StcSharedVar(){
		//init();
	}
	
	
	/**
	 * todo: move into constructor ?
	 * Called when: RPR object is created
	 *
	 */
	void init() {
		autoLogin = true;
		
		local = null;
		passwd = null;
		publicIP = null;
		
		mapRemoteClient.clear();
		
		stat = StcRprState.OFFLINE;
		uploading = false;
	}
	
	synchronized public boolean isUploading() {
		return uploading;
	}
	synchronized public void setUploading(boolean uploading) {
		this.uploading = uploading;
	}


	synchronized public StcRprState getStat() {
		return stat;
	}

	synchronized public void setStat(final StcRprState s) {
		stLog.debug("RPR State: " + stat + " ----> " + s);
		this.stat = s;
		StcRprObject.getInstance().sendEventToApp(new StcEvtRpr.InfoStateChange() );
		//StcRprObject.getInstance().sendMessage(StMessageToGui.STATE_CHANGE);
	}
	
	
	/**
	 * @deprecated NAT type is NOT reliable!
	 * 
	 * @return call is relayed
	 */
	synchronized public boolean isCallRelay(){
		util.assertTrue(false, "use deprecated API");
		return false;

		/*
		util.assertNotNull(callRec);
		int nat_sum = 0;
		final StJniNatType loc_nat = getNatType();
		final StJniNatType rem_nat = callRec.getRemoteNatType();
		switch(loc_nat){
		case SYMMETRIC:
		case SYMMETRIC_UDP:
			nat_sum += 3;
			break;
		case PORT_RESTRICTED:
			nat_sum += 1;
			break;
		default:
		}
		switch(rem_nat){
		case SYMMETRIC:
		case SYMMETRIC_UDP:
			nat_sum += 3;
			break;
		case PORT_RESTRICTED:
			nat_sum += 1;
			break;
		default:
		}

		return nat_sum > 3;
		*/
	}

	
	synchronized public StClientID getClientID() {
		if( local != null ){
			return local.getClientID();
		}
		return null;
	}
	
	
	synchronized public String getClientName() {
		if( local != null ){
			return local.getName();
		}
		return null;
	}
	
	synchronized public String getClientLabel() {
		if( local != null ){
			return local.getLabel();
		}
		return null;
	}
	
	synchronized public String getClientDscp() {
		if( local != null ){
			return local.getDscp();
		}
		return null;
	}

	/**
	 * Set when NAT type is discovered by PJ. <br/>
	 *
	 * NAT type is a bit accurate if the STUN server has multiply public IPs.
	 *
	 * @param nat_type nat type from PJ.
	 */
	synchronized public void setNatType(StJniNatType nat_type){
		if(local == null){
			stLog.warn("client is offline");
			return;
		}
		stLog.info("Set Local NAT: " + local.getFlag_NatType() + " --> " + nat_type);
		local.setFlag_NatType(nat_type);
	}
	
	synchronized public StJniNatType getNatType(){
		if(local == null){
			stLog.warn("client is offline");
			return StJniNatType.UNKNOWN_NAT;
		}
		return local.getFlag_NatType();
	}
	
	
	synchronized void setLocal(final StClientInfo local, final String passwd) {
		this.local = local;
		this.passwd = passwd;
	}
	
	
	synchronized void setLocal(final StClientInfo local) {
		this.local = local;
	}
	
	synchronized StClientInfo getLocalCopy(){
		if(null == this.local){
			stLog.warn("client is offline");
			return null;
		}
		return new StClientInfo(this.local);
	}
	
	synchronized StClientInfo getRemoteClientInfo(String name) throws ExpRemoteClientNoFound{
		Collection<StClientInfo> list = mapRemoteClient.values();
		for(StClientInfo f: list){
			if(f.getName().equals(name)){
				return f;
			}
		}
		throw new StcException.ExpRemoteClientNoFound(name);
	}
	
	
	synchronized StClientInfo getRemoteClientInfo(StClientID clt_id) throws ExpRemoteClientNoFound{
		StClientInfo ci =  mapRemoteClient.get(clt_id);
		if(ci == null){
			throw new StcException.ExpRemoteClientNoFound(clt_id);
		}
		return ci;
	}
	
	
	synchronized StFriend getRemoteFriend(StClientID clt_id) throws ExpRemoteClientNoFound {
		StClientInfo rem =  mapRemoteClient.get(clt_id);
		if(rem == null){
			throw new StcException.ExpRemoteClientNoFound(clt_id);
		}
		boolean is_slave = false;
		if(!local.isFlag_Monitor()){
			final ArrayList<StClientID> slave_list = local.getSlave();
			is_slave =slave_list.contains(rem.getClientID());
		}
		return new StFriend(rem, is_slave );
	}
	
	
	
	synchronized ArrayList<StClientInfo> getRemoteClientInfoList(){
		ArrayList<StClientInfo> arr_list = new ArrayList<>();
		Collection<StClientInfo> list = mapRemoteClient.values();
		for(StClientInfo f: list){
			arr_list.add(f);
		}
		return arr_list;
	}
	
	
	synchronized ArrayList<StClientInfo> getRemoteUserList(){
		ArrayList<StClientInfo> arr_list = new ArrayList<>();
		Collection<StClientInfo> list = mapRemoteClient.values();
		for(StClientInfo f: list){
			if(f.isFlag_User()){
				arr_list.add(f);
			}
		}
		return arr_list;
	}
	
	
	synchronized ArrayList<StFriend> getRemoteGatewayList(boolean include_monitor){
		final ArrayList<StFriend> gw_list = new ArrayList<>();
		if(local != null){
			final Collection<StClientInfo> list = mapRemoteClient.values();
			final ArrayList<StClientID> slave_list = local.getSlave();
			for(StClientInfo f: list){
				if(f.isFlag_Gateway() || include_monitor && f.isFlag_Monitor()){
					gw_list.add(new StFriend(f, slave_list.contains(f.getClientID())));
				}
			}
		}
		return gw_list;
	}
	
	
	synchronized boolean hasFriend(final StClientID f_id){
		return local.hasFriend(f_id);
	}
	
	
	synchronized ArrayList<StClientID> getSlave(){
		return local.getSlave();
	}
	
	
	synchronized ArrayList<StClientID> getFriendIDList(){
		// return local.getFriendList();
		ArrayList<StClientID> list = new ArrayList<>();
		if(local == null || local.getFriendList() == null){
			return list;
		}
		return local.getFriendList();
	}
	
	
	synchronized void clearRemoteClients(){
		mapRemoteClient.clear();
	}
	
	
	/**
	 * @param r_clt_id - CAN NOT be null;
	 * @param ci - Remote Client is removed, if NULL;
	 * @return previous client info
	 */
	synchronized StClientInfo updateRemoteClient(StClientID r_clt_id, StClientInfo ci){
		util.assertNotNull(r_clt_id);
		final StClientInfo pre_ci = mapRemoteClient.remove(r_clt_id);
		if(ci != null){
			mapRemoteClient.put(r_clt_id, ci);
		}
		return pre_ci;
	}
	
	
	synchronized StringBuffer debugRemoteClients(){
		StringBuffer sbuf = new StringBuffer(256);
        Iterator<Entry<StClientID, StClientInfo>> iter = mapRemoteClient.entrySet().iterator();
        int i=0;
        for(; iter.hasNext(); i++){
        	util.dumpFunc.addDumpHeaderLine(sbuf, " Remote Client ["+ i +"] ", "+");
        	sbuf.append(iter.next().getValue().dumpSimple()  );
        }
        util.dumpFunc.addDumpHeaderLine(sbuf, " EOF ", "+");
        sbuf.append("\n");
		return sbuf;
	}
	
	synchronized StringBuffer dumpLocal(){
		return local.dump();
	}


	synchronized boolean isAutoLogin() {
		return autoLogin;
	}

	synchronized void setAutoLogin(boolean auto_login) {
		stLog.info("Set auto-login: " + auto_login);
		this.autoLogin = auto_login;
	}

	
	synchronized String getPasswd() {
		return passwd;
	}

	synchronized String getPublicIP() {
		return publicIP;
	}

	synchronized void setPublicIP(String publicIP) {
		this.publicIP = publicIP;
	}


	synchronized public String getLocalIP() {
		return localIP;
	}


	synchronized public void setLocalIP(String localIP) {
		this.localIP = localIP;
	}
	
	synchronized public boolean isPassword(String pass) {
		return this.passwd != null && this.passwd.equals(pass);
	}


	synchronized public boolean isSendStatus() {
		return sendStatus;
	}


	synchronized public void setSendStatus(boolean sendStatus) {
		this.sendStatus = sendStatus;
	}


	synchronized public long getRefreshTime() {
		return refreshTime;
	}


	synchronized public void setRefreshTime(long refreshTime) {
		this.refreshTime = refreshTime;
	}
	
	/**
	 * Called when a client logs off from a device. 
	 * In this case, the client info and friend list must be deleted from the serialized object. <br/>
	 * See: B17MAR1701
	 */
	synchronized public void clear(){
		this.mapRemoteClient.clear();
		this.local = null;
		this.passwd = null;
	}

}//EOF StCoreVar


