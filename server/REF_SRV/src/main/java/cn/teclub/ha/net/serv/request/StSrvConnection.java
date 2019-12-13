package cn.teclub.ha.net.serv.request;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.lib.StConst;
import cn.teclub.ha.lib.StEvent;
import cn.teclub.ha.lib.StEventListener;
import cn.teclub.ha.lib.StEvent.HeartBeat;
import cn.teclub.ha.lib.StEvent.SystemShutdown;
import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.net.StExpConnectionLoss;
import cn.teclub.ha.net.StMessage;
import cn.teclub.ha.net.serv.StDBObject;
import cn.teclub.ha.net.serv.StExpServer;
import cn.teclub.ha.net.serv.StModelClient;
import cn.teclub.ha.net.serv.StModelMessage;
import cn.teclub.ha.net.serv.StSrvGlobal;
import cn.teclub.ha.request.StEvtStartRequest;
import cn.teclub.ha.request.StNetPacket;
import cn.teclub.ha.request.StPktSrvUpdateClientB;
import cn.teclub.ha.request.StRequest;
import cn.teclub.ha.request.StRequestID;
import cn.teclub.ha.request.StRequestMgr;
import cn.teclub.ha.request.StSocket4Pkt;
import cn.teclub.ha.request.StNetPacket.Command;
import cn.teclub.ha.request.StNetPacket.Flow;

public interface StSrvConnection {
	public String getCltName();
	public StModelClient getModelClient();
	public StClientID getClientID();
	public long getOfflineMS();
	public void addNewEvent(StEvent e);
	public void addDelayEvent(StEvent e, int delay_ms);
	public void updateClientInfo();
	public void sendPacketSafe(StNetPacket pkt);
	public void sendMessage(StMessage msg0);
	public void checkRemote() ;
	public StringBuffer debug_getStatistics();
}



abstract class StExpConn extends StExpServer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}


class StExpConnSendFailure extends StExpConn {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}


class StExpConnSendReponse extends StExpConn {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
}



class StConnStatistics 
		extends ChuyuObj
		implements ChuyuObj.DumpAttribute
{
	class CountItem{
		final String key;
		final int	keyWidth;
		int 	count = 0;
		long 	msCost = 0;
		CountItem(String count_key, int width){
			this.key = count_key;
			this.keyWidth = width;
		}
		
		public String toString(){
			return util.stringFunc.format(key, keyWidth) + " = " + count + "/" + msCost + "ms";
		}
	}
	
	
	
	private final HashMap<String, CountItem> eventMap;
	private final HashMap<StNetPacket.Command, CountItem> recvMap, recvP2pMap, recvRequestMap, recvResMap;
	private final HashMap<StNetPacket.Command, CountItem> sendMap, sendP2pMap, sendRequestMap, sendResMap;
	
	private long msEventCost, msRecvCost, msSendCost;
	private int nEvent, nRecv, nSend;
	
	
	StConnStatistics() {
		this.eventMap = new  HashMap<String, CountItem>();
		this.recvMap 		= new  HashMap<StNetPacket.Command, CountItem>();
		this.recvP2pMap   	= new  HashMap<StNetPacket.Command, CountItem>();
		this.recvRequestMap = new  HashMap<StNetPacket.Command, CountItem>();
		this.recvResMap 	= new  HashMap<StNetPacket.Command, CountItem>();
		
		this.sendMap 		= new  HashMap<StNetPacket.Command, CountItem>();
		this.sendP2pMap   	= new  HashMap<StNetPacket.Command, CountItem>();
		this.sendRequestMap	= new  HashMap<StNetPacket.Command, CountItem>();
		this.sendResMap 	= new  HashMap<StNetPacket.Command, CountItem>();
	}
	
	
	
	synchronized private void addStringItem(HashMap<String, CountItem> map, String key, long ms_cost, int width){
		CountItem i = map.get(key);
		if(i == null){
			i = new CountItem(key, width);
			map.put(key, i);
		}
		i.count++;
		i.msCost+=ms_cost;
	}
	
	synchronized private void addNetPacket(HashMap<StNetPacket.Command, CountItem> map, StNetPacket.Command cmd, long ms_cost){
		CountItem i = map.get(cmd);
		if(i == null){
			i = new CountItem(cmd.toString(), 32);
			map.put(cmd, i);
		}
		i.count++;
		i.msCost+=ms_cost;
	}
	
	
	
	void addConnEvent(StEvent evt_conn, long ms_cost){
		final String key = evt_conn.getClass().getName();
		addStringItem(eventMap, key, ms_cost, 64);
		msEventCost += ms_cost; 
		nEvent++;
	}
	
	
	void addRecvPkt(StNetPacket pkt, long ms_cost ){
		final StClientID dst_id = pkt.getDstClientId();
		if(dst_id.equalWith(StClientID.GEN_ID) ){
			if(pkt.isTypeRequest()){
				addNetPacket(recvRequestMap, pkt.getCmd(), ms_cost);
			}else{
				addNetPacket(recvResMap, pkt.getCmd(), ms_cost);
			}
		}
		else {
			addNetPacket(recvP2pMap, pkt.getCmd(), ms_cost);
		}
		addNetPacket(recvMap, pkt.getCmd(), ms_cost);
		msRecvCost += ms_cost;
		nRecv++;
	}
	
	
	void addSendPkt(StNetPacket pkt, long ms_cost ){
		final StClientID src_id = pkt.getSrcClientId();
		if(src_id.equalWith(StClientID.GEN_ID) ){
			if(pkt.isTypeRequest()){
				addNetPacket(sendRequestMap, pkt.getCmd(), ms_cost);
			}else{
				addNetPacket(sendResMap, pkt.getCmd(), ms_cost);
			}
		}
		else {
			addNetPacket(sendP2pMap, pkt.getCmd(), ms_cost);
		}
		addNetPacket(sendMap, pkt.getCmd(), ms_cost);
		msSendCost += ms_cost;
		nSend++;
	}
	
	
	@Override
	synchronized public void dumpSetup() {
		dumpAddLine("## All Events: " + nEvent  + "/" + msEventCost + "ms");
		for(CountItem i: eventMap.values()){
			dumpAddLine("   " + i);
		}
		dumpAddLine("");
		
		dumpAddLine("## All Recv Packets: " + nRecv + "/" + msRecvCost + "ms");
		for(CountItem i: recvMap.values()){
			dumpAddLine("   " + i);
		}
		dumpAddLine("");
		
		dumpAddLine(">> Recv Requests: " + recvRequestMap.size());
		for(CountItem i: recvRequestMap.values()){
			dumpAddLine("   " + i);
		}
		dumpAddLine(">> Recv Response: " + recvResMap.size());
		for(CountItem i: recvResMap.values()){
			dumpAddLine("   " + i);
		}
		dumpAddLine(">> Recv P2P: " + recvP2pMap.size());
		for(CountItem i: recvP2pMap.values()){
			dumpAddLine("   " + i);
		}
		dumpAddLine("");
		
		dumpAddLine("## Send Packets ( All ? ) : " + nSend + "/" + msSendCost + "ms");
		for(CountItem i: sendMap.values()){
			dumpAddLine("   " + i);
		}
		dumpAddLine("");
		
		dumpAddLine(">> Send Requests: " + sendRequestMap.size());
		for(CountItem i: sendRequestMap.values()){
			dumpAddLine("   " + i);
		}
		dumpAddLine(">> Send Response: " + sendResMap.size());
		for(CountItem i: sendResMap.values()){
			dumpAddLine("   " + i);
		}
		dumpAddLine(">> Send P2P: " + sendP2pMap.size());
		for(CountItem i: sendP2pMap.values()){
			dumpAddLine("   " + i);
		}
	}
}



/**
 * 
 * <pre>
 *  <strong>Avoid associate object with TWO open sessions. </strong>
 *  e.g. if remote client closes the socket when processing Login Request, 
 *  socket exception occurs and the previous hibernate session is NOT closed.
 *  In this case, DO NOT create another session to mark the client OFFLINE, with the client object
 *  queried from the session created for a login request.
 *  
 *  Otherwise following error occurs:
 *  	org.hibernate.HibernateException: Illegal attempt to associate a collection with two open sessions
 *  
 * </pre>
 *  
 * @author mancook
 *
 */
class StSrvConnLis  
	extends ChuyuObj 
	implements StEventListener
{
	private final StSrvConnection 	conn;
	private final String			CLT_NAME;		
	private final StSrvGlobal		global = StSrvGlobal.getInstance();	
	private final int				CHECK_CLIENT_HB = global.cfg.checkClientTime * StConst.SRV_CORE_PULSE_RATE;
	private final long 				MS_SKIP_CHECK = (global.cfg.checkClientTime * 1000) * 3/4;;
	private final String			LIS_NAME;
	
	/**
	 * Must be NON-BLOCKCING socket!!!!
	 */
	private StSocket4Pkt 	sockRemote;
	private StModelClient 	mcSelf;
	
	/**
	 * Set when close current connection.
	 * Unset (set to 0), when a client logs in or signs up.
	 */
	private long 	tsOffline = System.currentTimeMillis(); // OFFLINE Timestamp
	
	private long 	lastRecvMS = 0;
	private int  	hbCount = 0;
	private Object[] serviceParams;
	
	private final StConnStatistics statistics = new StConnStatistics();
	
	
	/**
	 * Constructor
	 * 
	 * @param sock
	 * @param hdl
	 */
	StSrvConnLis(StSocket4Pkt sock, StSrvConnection connection){
		this.sockRemote = sock;
		this.conn = connection;
		this.CLT_NAME = conn.getCltName();
		this.LIS_NAME = "Lis__Conn_" + CLT_NAME;
	}
	
	
	Object[] getServiceParams(){
		return this.serviceParams;
	}
	
	private void setServiceParams(Object[] param){
		this.serviceParams = param;
	}
	
	
	StModelClient getModelClient(){
		return mcSelf;
	}
	
	
	/**
	 * <pre>
	 * Called by Login/SignUp Service.
	 * 
	 * 1) Update DB with:
	 * - online flag;
	 * - last login;
	 * - public IP/Port
	 * 
	 * 2) close previous socket;
	 * 
	 * 3) Load friends and inform them;
	 * 
	 * </pre>
	 * 
	 * 
	 * @param mc0
	 * @param db_obj
	 * @param sock2
	 * @return
	 */
	StModelClient setOnline(StModelClient mc0, StDBObject db_obj, StSocket4Pkt sock2) {
		final String pub_addr = sock2.getSrcAddress().getHostAddress();
		final int pub_port = sock2.getSrcPort();
		final StClientID id = mc0.getClientID();
		mc0.setFlag_Online(true);
		mc0.setLastLogin(new Timestamp(new java.util.Date().getTime()));
		mc0.setPublicIP(pub_addr);
		mc0.setPublicPort(pub_port);	
		db_obj.updateRecord(mc0);
		
		if(sockRemote != null){
			final StNetPacket pkt_bye = 
					StNetPacket.buildReq(
						Command.YouLogout, 
						Flow.SERVER_TO_CLIENT, 
						StNetPacket.Code.NONE, 
						null, 
						null, id,
						null);
			stLog.info("[Server ---->>>> Client] (FINAL PACKET) " + pkt_bye.dumpSimple() );
			try {
				sendPacket(pkt_bye);
			} catch (StExpConnSendFailure e) {
				stLog.error("Fail to Send Final Packet!!!!");
			}
		}
		
		if(mcSelf == null){
			stLog.trace("First Online. Load friends...");
			util.assertTrue(sockRemote == null);
			mcSelf = db_obj.loadClient(id, true);
			global.connMgr.addConnection(id, conn);
		}else{
			util.assertTrue(mcSelf == mc0);
		}
		
		informOnlineFriends();
		sockRemote = sock2;
		tsOffline = 0; // connection is ONLINE
		return mcSelf;
	}
	
	
	void die(){
		global.connMgr.deleteConnection(conn);
	}
	
	
	void setModelClient(StModelClient mc){
		if(this.mcSelf == mc){
			stLog.warn("#### set with same mcSelf object !");
		}
		this.mcSelf = mc;
	}
	
	
	/**
	 * <pre>
	 * Called when:
	 * - a new client is online;
	 * - close current connection;
	 * </pre>
	 * 
	 * @return
	 */
	boolean informOnlineFriends()  {
		final ArrayList<StClientInfo> ci_list = new ArrayList<StClientInfo>();
		ci_list.add(mcSelf);
		
		ArrayList<StSrvConnection> conn_list = global.connMgr.getConnection(mcSelf.getFriendList());
		for(StSrvConnection conn: conn_list){
			// [2016-10-20] only update ONLINE friend. 
			// DO NOT use request! As SrvUpdateClientB has not result!
			stLog.debug("Info ONLINE friend : " + conn.getCltName());
			final StNetPacket update_pkt = StPktSrvUpdateClientB.buildSrvReq(null, conn.getClientID(), ci_list);
			conn.sendPacketSafe(update_pkt);
		}
		stLog.debug("Send client-info to friends of: " + mcSelf );
		return conn_list.size() > 0;
    }
	
	
	boolean isRemoteSocket(StSocket4Pkt sock){
		return sock == sockRemote;
	}
	
	void debug_addSendPacket(StNetPacket pkt, final long ms_cost){
		statistics.addSendPkt(pkt, ms_cost);
	}

	StringBuffer debug_getStatistics(){
		StringBuffer sbuf = statistics.dump();
		
		// NOT thread-safe!
		sbuf.append(mcSelf == null ? "<NO Model Client>" : mcSelf.dump());
		util.dumpFunc.addDumpLine(sbuf, getConnStr());
		util.dumpFunc.addDumpLine(sbuf, "Time: " + util.getTimeStamp());
		return sbuf;
    }
	
	

	/**
	 * <pre>
	 * Called when:
	 * - fail to send/recv from the socket;
	 * - SRV_CHECK_CLIENT Timeout;
	 * </pre>
	 * 
	 * 
	 * Create ONLY ONE db-object in each event!
	 * 
	 * @param dbObj
	 */
	void close(StDBObject db_obj) {
		if(sockRemote != null){
			sockRemote.close();
			sockRemote = null;
			stLog.info("Closed Socket!");
		}
		
		// if(mcSelf != null && mcSelf.isFlag_Online()){
		 if(mcSelf != null){
			mcSelf.setFlag_Online(false);
			mcSelf.setLastLogoff(new Timestamp(new java.util.Date().getTime()));
			db_obj.updateRecord(mcSelf);
			informOnlineFriends();
			stLog.info("Mark OFFLINE: " + mcSelf.getName());
		}
		 
		 tsOffline = System.currentTimeMillis();
	}
	
	
	
	@Override
	public String getEvtLisName() {
		return LIS_NAME;
	}

	        
	@Override
	public void handleEvent(StEvent evt) {
		final long MS_START = System.currentTimeMillis();
		
		try{
            if(evt instanceof StEvent.HeartBeat){
            	prcHeartBeat((StEvent.HeartBeat) evt);
            	return;
            }	
            
            if(evt instanceof StEvent.SystemShutdown){
            	prcSystemShutdown((StEvent.SystemShutdown) evt);
            	return;
            }
            
			if(evt instanceof StEvtStartRequest){
				prcStartRequest((StEvtStartRequest)evt);
				return;
			}
			
			// --------------------------------------------------------------
            // error checking
            if(! (evt instanceof StEvtConn) ){
            	stLog.warn("UnExpected Event: " + evt.dump());
            	return;
            }
            
            StEvtConn event = (StEvtConn) evt;
            if(!event.cltName.equals(CLT_NAME)){
            	stLog.trace("Ignore Event for '" + event.cltName +"' -- " + getConnStr());
            	return;
            }
            // --------------------------------------------------------------
            
            stLog.debug("[Conn-Lis] >>>> " + event );
            if(event instanceof StEvtConnNewSocket){
            	prcConnNewSocket((StEvtConnNewSocket) event);
            	return;
            }
            
            
            if(event instanceof StEvtConnRecv){
            	prcConnRecv((StEvtConnRecv)event);
            	return;
            }
            
            if(event instanceof StEvtConnSend){
            	prcConnSend((StEvtConnSend)event);
            	return;
            }
			
			if(event instanceof StEvtConnLoss) {
				 prcConnLoss((StEvtConnLoss)event);
	             return;
			}
            
            if(event instanceof StEvtConnSendMessage){
            	prcConnSendMessage((StEvtConnSendMessage)event);
            	return;
            }
            
            if(event instanceof StEvtConnCheck){
            	prcConnCheck((StEvtConnCheck)event);
            	return;
            }
            
            if(event instanceof StEvtConnUpdateClientInfo){
            	prcConnUpdateClientInfo((StEvtConnUpdateClientInfo)event);
            	return;
            }
            return;
		} 
		catch(RuntimeException e){
			e.printStackTrace();
			stLog.fatal(util.getExceptionDetails(e, "Runtime Exception in: " + toString()));
			stLog.fatal("Trouble Event: " + evt.dump() );
			stLog.fatal("==== dump conn-mgr ==== " + global.connMgr.dump() );
			// [2016-11-7] NOTE: Client may be ONLINE in database, after dying
			die();
			throw e;
		} catch (StExpConnSendFailure e) {
			stLog.debug(util.getExceptionDetails(e, ""));
		}finally{
			statistics.addConnEvent(evt, util.getCostMillis(MS_START));
		}
		
		stLog.warn("Close this connection...");
		StDBObject db_obj = null;
		try {
			db_obj = global.dbObjMgr.getNextObject();
			close(db_obj);
		} catch (InterruptedException e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "Fail to Close: " + this ));
		}finally{
			global.dbObjMgr.putObject(db_obj);
		}
	}
	
	
	private boolean isOnline(){
		return sockRemote != null  && mcSelf != null && mcSelf.isFlag_Online();
	}

	
	private boolean isOffline(){
		return sockRemote == null && (mcSelf == null ||  !mcSelf.isFlag_Online());
	}
	
	
	
	/**
	 * <pre>
	 * ATTENTION: ONLY call this method in listener thread!!!! <br/>
	 * Including the service!
	 * </pre>
	 * 
	 * @param pkt
	 * 
	 * @throws StExpConnSendFailure 
	 */
	private void sendPacket(final StNetPacket pkt) throws StExpConnSendFailure {
		// [2016-11-3] ATTENTION: DO NOT call send SRV_CHECK_CLIENT request here!
		// Reason: a request is sent as a packet. This method will be called when sending a request! 
		// As a result, dead-loop occurs! 
		// 
		//		checkRemoteClient();
		
		if(sockRemote == null || sockRemote.isClosed()) {
			stLog.trace("Socket is NULL/Closed -- Drop Packet: " + pkt);
			return;
		}
		
		if(!pkt.isStatusChecking()){
			stLog.debug(getConnStr() + " --> " + pkt.makeLogicFlow(true, false));
		}
		
		final long ms_start = System.currentTimeMillis();
		try {
			sockRemote.sendPacket(pkt);
		} catch (IOException e) {
			// [2016-11-9] In most cases, the remote client close this socket.
			// This is NOT an error!
			stLog.warn("Fail to send: " + pkt + " -- " + this );
			stLog.debug(util.getExceptionDetails(e, "Exception in " + this ));
			throw new StExpConnSendFailure();
		}finally{
			statistics.addSendPkt(pkt, util.getCostMillis(ms_start));
		}
	}
	
	
	
    private void prcConnLoss(StEvtConnLoss event) {
    	if(isOffline()){
    		stLog.trace("Connecton is OFFLINE! Do nothing!");
    		return;
    	}
    	
		StDBObject db_obj = null;
		try {
			db_obj = global.dbObjMgr.getNextObject();
			close(db_obj);
		} catch (InterruptedException e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "Fail to close connection: " + this));
		}finally{
			global.dbObjMgr.putObject(db_obj);
		}
	}


	private void prcConnNewSocket(StEvtConnNewSocket event) {
		final StNetPacket.Command cmd = event.firstPkt.getCmd();
		util.assertTrue(cmd == StNetPacket.Command.Login || cmd == StNetPacket.Command.Signup);
		setServiceParams(new Object[]{conn, event.sock});
		StSrvService service = global.getServiceMgr().getService(cmd).copy();
		final long ms_cost = service.processRequest(this, mcSelf, event.firstPkt);
		setServiceParams(null);
		
		statistics.addRecvPkt(event.firstPkt, ms_cost);
		// [2017-3-17] three costs: since accept, event creating, event process;
		stLog.info(	"==== Cost:" + event  + 
					util.getCostMillis(event.tsAccept) + "/" + util.getCostMillis(event.TS) + "/" + ms_cost + 
					"ms -- " + (mcSelf == null ? "<null>" : mcSelf.getName() )
					);
	}

    
	private void prcStartRequest(final StEvtStartRequest evt) throws StExpConnSendFailure{
    	util.assertTrue(evt.request instanceof StSrvRequest, "NOT a Server Request!" );
    	final StSrvRequest req = (StSrvRequest)evt.request;
		if(req.onPreSend()){
			sendPacket(req.buildOutPacket());
		}else{
			stLog.error("Abort Request: " + req.dump() );
		}
    }
    
    
	private void prcConnSendMessage(StEvtConnSendMessage event) throws StExpConnSendFailure {
		StDBObject db_obj = null;
		try {
			db_obj = global.dbObjMgr.getNextObject();
			
			ArrayList<StMessage> msg_list = new ArrayList<StMessage>();
			msg_list.add(event.msg);
			final StNetPacket msg_pkt = StNetPacket.buildReq(
					StNetPacket.Command.SrvMessageToClt, Flow.SERVER_TO_CLIENT, (byte)0, 
					null, null, mcSelf.getClientID(), StMessage.toBuffer(msg_list));
			sendPacket(msg_pkt);
			
			event.msg.setFlagSent(true);
			db_obj.updateRecord(new StModelMessage( event.msg));
		} catch (InterruptedException e) {
			e.printStackTrace();
			stLog.info(util.getExceptionDetails(e, "Bypass Event: " + event + " IN " + this));
		}finally{
			global.dbObjMgr.putObject(db_obj);
		}
	}


	private void prcConnCheck(StEvtConnCheck event) {
		checkRemoteClient();
	}


	private void prcConnSend(StEvtConnSend event) throws StExpConnSendFailure {
		sendPacket(event.packet);
	}
	
	
	private void prcConnUpdateClientInfo(StEvtConnUpdateClientInfo event) {
		if(!isOnline()){
			return;
		}
		
		StDBObject db_obj = null;
		try {
			db_obj = global.dbObjMgr.getNextObject();
			mcSelf = db_obj.loadClient(mcSelf.getClientID(), true);
			(new StSrvReqUpdateClient(conn)).startRequest();
		} catch (InterruptedException e) {
			e.printStackTrace();
			stLog.info(util.getExceptionDetails(e, "Bypass Event: " + event + " IN " + this));
		}finally{
			global.dbObjMgr.putObject(db_obj);
		}
	}


	/**
	 * 
	 * @param event
	 * @throws StExpConnSendFailure 
	 */
	private void prcConnRecv(StEvtConnRecv event) throws StExpConnSendFailure {
		if(event.sock != sockRemote){
			stLog.warn("Packet may from previous socket! Ignore: " + event.packet.dump());
			return;
		}
		
		final long ms_start = System.currentTimeMillis();
		final StNetPacket recv_pkt = event.packet;
		final StClientID dst_id = recv_pkt.getDstClientId();
		if(dst_id.equalWith(StClientID.GEN_ID) ){
			prcConnRecv_fromClient(recv_pkt);
		}else{
			stLog.debug("Relay P2P Packet " + recv_pkt.getCmd() + "...");
			StSrvConnection conn = global.connMgr.getConnection(dst_id); 
			if(conn != null){
				conn.checkRemote();
				conn.sendPacketSafe(recv_pkt);
			}else{
				stLog.warn("TODO: send relay-failure result!");
			}
		}
		statistics.addRecvPkt(recv_pkt, util.getCostMillis(ms_start));
	}
	


	private void prcConnRecv_fromClient(StNetPacket pkt) throws StExpConnSendFailure  {
		if(!pkt.isStatusChecking()){
			stLog.debug(getConnStr() + " <-- " + pkt.makeLogicFlow(false, false));
		}
		
		final Command pkt_cmd = pkt.getCmd();
		if(pkt.isTypeRequest()){
			if(pkt_cmd == Command.Signup || pkt_cmd == Command.Login){
				stLog.error("Error Packet for Online Client: " + pkt.dump());
				
				// [2016-11-12] This occurs when client login/signup times out.
				// And it re-send the Login/Signup request!
				//
				stLog.error("Client May Timeout: " + mcSelf);
				sendPacket(pkt.buildAlw(mcSelf.toBuffer(true)));
				
				return;
				// just ignore it!
				//sendPacket(pkt.buildDny(StNetPacket.Code.NONE));
			}
			StSrvService service = global.getServiceMgr().getService(pkt_cmd).copy();
			setServiceParams(new Object[]{conn, sockRemote});
			service.processRequest(this, mcSelf, pkt);
			setServiceParams(null);
		}
		else {
			final StRequestID id = pkt.getRequestId();
			final StRequest req = StRequestMgr.getInstance().get(id);
			if(req == null){
				stLog.warn("No Request by ID: " + id);
				return;
			}
			util.assertTrue(req instanceof StSrvRequest, "UnExpected: " + req.dump());
			req.finishRequest(pkt);
		}
	}


	private void prcSystemShutdown(SystemShutdown event) {
		if(sockRemote != null) {
			sockRemote.close();
			sockRemote = null;
		}
		stLog.info("Shut down: " + toString());
	}

	
	private void prcHeartBeat(HeartBeat event) {
		hbCount++;
		prcHeartBeat_recvPacket();
		
		if(hbCount % CHECK_CLIENT_HB == 0){
			checkRemoteClient();
		}
		
		if(tsOffline > 0){
			if(util.getCostMillis(tsOffline)/1000 > global.cfg.connMaxOffline){
				stLog.info("Exceed Max OFFLINE Time: " + util.getCostStr(tsOffline) + " -- " + this);
				die();
			}
		}
	}
	
	
	private void prcHeartBeat_recvPacket() {
		if(sockRemote == null || sockRemote.isClosed()) {
			return;
		}
		try {
			StNetPacket pkt =null;
			for(int i=0; ; i++){
				pkt = sockRemote.recvPacket();
				if(pkt == null){
					if(i>0) stLog.debug("Recv Packets: " + i);
					break;
				}
				conn.addNewEvent(new StEvtConnRecv(null, CLT_NAME, pkt, sockRemote));
				lastRecvMS = System.currentTimeMillis();
			}
			return;
		} catch (StExpConnectionLoss | IOException e) {
			// [2016-11-3] In most cases, this is NOT an error.
			// It happens when remote closes the connection. 
			stLog.debug(util.getExceptionDetails(e, "Exception when recv from: " + toString() ));
			sockRemote.close();
			conn.addNewEvent(new StEvtConnLoss(null, CLT_NAME, sockRemote));
		}
	
		// exception occurs! close this connection.
		
		StDBObject db_obj = null;
		try {
			db_obj = global.dbObjMgr.getNextObject();
			close(db_obj);
		} catch (InterruptedException e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "Fail to Mark OFFLINE!"));
		}finally{
			global.dbObjMgr.putObject(db_obj);
		}
	}
	
	
	private void checkRemoteClient() {
		if(sockRemote == null){
			stLog.trace("Skip NULL socket");
			return;
		}
		final long ms_check_period = System.currentTimeMillis() - lastRecvMS ; 
		if( sockRemote == null || ms_check_period < MS_SKIP_CHECK ){
			stLog.trace("Skip Check (" + ms_check_period + "<" + MS_SKIP_CHECK + " ms): " + getConnStr());
			return; 
		}
		stLog.debug("Checking (" + ms_check_period + "/" + MS_SKIP_CHECK + " ms): " + getConnStr() );
		(new StSrvReqCheckClient(conn, sockRemote)).startRequest();
	}


	
	public String getConnStr(){
		return toString();
	}
	
	
	public long getOffLineMS(){
		if(tsOffline == 0) return 0;
		return (System.currentTimeMillis() - tsOffline);
	}
	
	
	public String toString(){
		final StringBuffer sbuf = new StringBuffer(128);
		sbuf.append("{[]--[]")
			.append(conn.getCltName())
			.append("," + getOffLineMS() + "ms")
			.append("," + (sockRemote == null ? 
							"<null> " : 
							sockRemote.getSrcAddress().getHostAddress() + ":" + sockRemote.getSrcPort() + "}" 
							) );
		return sbuf.toString();
	}
	
}




