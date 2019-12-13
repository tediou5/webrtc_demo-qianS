package cn.teclub.ha.net.serv.request;

import java.io.IOException;

import org.hibernate.HibernateException;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.lib.StErrUserError;
import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.serv.StDBObject;
import cn.teclub.ha.net.serv.StModelClient;
import cn.teclub.ha.net.serv.StSrvGlobal;
import cn.teclub.ha.request.StNetPacket;
import cn.teclub.ha.request.StSocket4Pkt;
import cn.teclub.ha.request.StNetPacket.Command;


/**
 * Client Service: for request from server
 * 
 * @author mancook
 *
 */
public abstract class StSrvService 
		extends ChuyuObj 
{
	
	/**
	 * ID in service map in StcRprObject.
	 */
	protected final StNetPacket.Command 	cmd;
	protected final StSrvGlobal 			global = StSrvGlobal.getInstance(); 
	
	
	protected StSrvConnection 		conn = null;
	protected StSocket4Pkt 			sockService = null;
	
	protected StSrvConnLis			connLis = null;
	
	
	/**
	 * Response Packet
	 */
	protected StNetPacket 	resPkt = null;
	protected boolean		closeSock = false;
	
	
	
	/**
	 * Constructor
	 * @param cmd
	 */
	protected StSrvService(final StNetPacket.Command cmd){
		this.cmd = cmd;
	}	
	
	final int MS_MAX_REQUEST = 1000;
	
	public long processRequest(
			final StSrvConnLis conn_lis,
			final StModelClient mc_self, 
			final StNetPacket pkt )
	{
		final long ms_start = System.currentTimeMillis();
		
		this.connLis = conn_lis;
		this.conn = (StSrvConnection) conn_lis.getServiceParams()[0];
		this.sockService = (StSocket4Pkt) conn_lis.getServiceParams()[1];
		try{
			handleRequest(conn_lis, mc_self, pkt);
		} catch (StExpConnSendReponse e) {
		}
		
		// [Theodore: 2016-11-17]
		// [Deprecated] SIGN-UP response is SENT in its own service
		// 
		if(resPkt != null){
			sendPkt(resPkt);
		}
		if(closeSock){
			sockService.close();
		}
		
		final long ms_cost = util.getCostMillis(ms_start);
		if(resPkt != null){
			connLis.debug_addSendPacket(resPkt, ms_cost);
		}
		
		if(ms_cost > MS_MAX_REQUEST/2){
			final String msg = "==== Cost: " + ms_cost + "ms for Req: " + cmd + " IN " + conn_lis;
			if(ms_cost > MS_MAX_REQUEST * 10){
				stLog.error(msg);
			}else if(ms_cost > MS_MAX_REQUEST){
				stLog.warn(msg);
			}else{
				stLog.debug(msg);
			}
		}
		return ms_cost;
	}
	
	
	public StSrvService copy(){
		try {
			return (StSrvService)getClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "Fail to copy server-service object!"));
			throw new StErrUserError();
		}
	}
	
	
	/**
	 * Set the response packet.
	 * 
	 * TODO_In_Future: throw conn-end-request exception.
	 * 
	 * @param pkt
	 */
	protected void finishRequest(final StNetPacket pkt){
		//		util.assertTrue(pkt.isTypeResponseAllow() || pkt.isTypeResponseDeny());
		//		util.assertTrue(resPkt == null);
		//		resPkt = pkt;
		finishRequest(pkt, false);
	}
	
	protected void finishRequest(final StNetPacket pkt, boolean close_sock){
		util.assertTrue(pkt.isTypeResponseAllow() || pkt.isTypeResponseDeny());
		util.assertTrue(resPkt == null);
		resPkt = pkt;
		closeSock = close_sock;
	}
	
	
	protected void sendPkt(StNetPacket pkt){
		try {
			if(!pkt.isStatusChecking()){
				stLog.debug(connLis.getConnStr() + " --> " + pkt.makeLogicFlow(true, false));
			}
			sockService.sendPacket(pkt);
			return;
		} catch (IOException e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "Fail to send packet: " + connLis ) );
		}
		
		stLog.warn("fail to send response packet: " + pkt);
		
		// [Theodore: 2016-11-10] 
		// For LOGIN & SIGN_UP service, a new socket is coming. Just close this socket.
		// DO NOT close the connection! 
		if(!connLis.isRemoteSocket(sockService)){
			sockService.close();
			return;
		}
		
		stLog.warn("Close Connection: " + connLis);
		StDBObject dbObj = getDbObj();
		if(dbObj != null){
			connLis.close(dbObj);
			return;
		}
		try{
			dbObj = global.dbObjMgr.getNextObject();
			connLis.close(dbObj);
		} catch (InterruptedException e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "Fail to get DB-Object!" ) );
		}
		finally{
			global.dbObjMgr.putObject(dbObj);
			dbObj = null;
		}
	}
	
	/**
	 * Inform friend connection to send SRV_UPDATE_CLIENT.
	 * 
	 * @param mc
	 */
	protected void updateFriendClientInfo(StClientID id2){
		StSrvConnection conn2 = global.connMgr.getConnection(id2);
		if(conn2 == null){
			return;
		}
		conn2.updateClientInfo();
	}
	
	
	protected boolean isReqFinish(){
		return resPkt != null;
	}
	
	
	protected void handleRequest(
			final StSrvConnLis conn_lis, 
			final StModelClient mc_self, 
			final StNetPacket pkt) throws StExpConnSendReponse 
	{ 
		onRequest(conn_lis, null, mc_self, pkt);
	}
	
	
	protected StDBObject getDbObj(){ return null; };
	
	
	protected abstract void onRequest(
			final StSrvConnLis conn_lis, 
			final StDBObject db_obj, 
			final StModelClient mc_self, 
			final StNetPacket pkt) ;
}



/**
 * Service which needs DB access.
 * 
 * @author mancook
 *
 */
abstract class StSrvDbService extends StSrvService
{
	private StDBObject dbObj;
	
	
	/**
	 * Constructor
	 * @param cmd
	 */
	protected StSrvDbService(Command cmd) {
		super(cmd);
	}
	
	
	protected void handleRequest(
			final StSrvConnLis conn_lis, 
			final StModelClient mc_self, 
			final StNetPacket pkt) throws StExpConnSendReponse
	{ 
		try {
			dbObj = global.dbObjMgr.getNextObject();
			onRequest(conn_lis, dbObj, mc_self, pkt);
		} catch (InterruptedException e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "Fail to get db-object"));
			finishRequest(pkt.buildDny(StNetPacket.Code.DENY_DB_BUSY));
		} catch (HibernateException e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "Hibernate Error!"));
			if(!isReqFinish()){
				finishRequest(pkt.buildDny(StNetPacket.Code.DENY_DB_ERROR));
			}
		}finally{
			global.dbObjMgr.putObject(dbObj);
			dbObj = null;
		}
		throw new StExpConnSendReponse();
	}
	
	
	protected StDBObject getDbObj(){ 
		return dbObj; 
	}
	
}
