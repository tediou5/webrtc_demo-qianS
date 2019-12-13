package cn.teclub.ha.client.rpr;

import java.nio.ByteBuffer;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.client.StcParams;
import cn.teclub.ha.client.StcException.ExpLocalClientOffline;
import cn.teclub.ha.client.StcException.ExpRemoteClientNoFound;
import cn.teclub.ha.client.session.StcEvtRecvFromSrv;
import cn.teclub.ha.client.session.StcEvtRecvP2p;
import cn.teclub.ha.client.session.StcEvtSession;
import cn.teclub.ha.lib.StErrUserError;
import cn.teclub.ha.lib.StEvent;
import cn.teclub.ha.lib.StEventListener;
import cn.teclub.ha.lib.StEventPulse;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.request.StNetPacket;



/**
 * <h1> Event Pulse: Representation Main Pulse </h1>
 * 
 * @author mancook
 *
 */
public class StcRprMainPulse extends StEventPulse 
{
	public static final int HB_INIT_MS 		= 1000;
	public static final int HB_PERIOD_MS 	=  500;
	
	
	/**
	 * Construct RPR Main Pulse. 
	 */
	StcRprMainPulse() {
		super("RPR-Main-Pulse", HB_INIT_MS, HB_PERIOD_MS);
	}
}


class StcRprMainPulseLis extends ChuyuObj implements StEventListener
{
	private final StcParams 		params = StcParams.getInstance();
	private final StcRprObject 	rprObject = StcRprObject.getInstance();
	private int heartBeatCount = 0;
	
	
	@Override
	public String getEvtLisName() {
		return "RPR-Main";
	}

	
	private void prcRequestResponse(final StNetPacket pkt){
		final StClientRequest req = rprObject.getRequest(pkt.getRequestId());
		if(null != req){
			req.onFinish(pkt);
		}else{
			stLog.warn("No Request Found for Packet: " + pkt.dump());
		}
	}
	
	
	private void processEvtRecvFromSrv(StcEvtRecvFromSrv evt){
		final StNetPacket pkt = evt.packet;
		util.assertTrue(pkt.isTypeRequest() || pkt.isTypeResponseAllow() || pkt.isTypeResponseDeny(), "Packet is NOT REQUEST/ALLOW/DENY !");
		util.assertTrue(pkt.isTypeFlowFromSrvToClient(), "Packet Flow NOT SERVER-TO-CLIENT !");
		
		stLog.debug(pkt.makeLogicFlow(false, false));
		
		// 2017-3-16: Logic Error? 
		// Because during a login request, RPR state is LOGING, NOT OFFLINE!
		if(rprObject.sharedVar.getStat() == StcRprState.OFFLINE ){
			// only process following non-request packets:
			// 1. LOGIN ;
			// 2. SignUp;
			if( ( pkt.getCmd().isPreLoginReq() ) 
				&&  ! pkt.isTypeRequest() )
			{
				prcRequestResponse(pkt);
			}else{
				stLog.error("UnExpected Packet When OFFLINE: " + pkt.dump() );
			}
			return;
		}
		
		if(rprObject.info.isOnline()){
			rprObject.sharedVar.setRefreshTime(System.currentTimeMillis());
		}
		
		
		if(pkt.isTypeRequest()){
			final StcService4Srv clt_service = rprObject.getClientService(pkt.getCmd());
			util.assertNotNull(clt_service, "NO Service for SrvRequest: " + pkt.getCmd());
			clt_service.onRecvRequest(null, pkt);
		}
		else {
			prcRequestResponse(pkt);
		}
	}
	
	
	private void processEvtRecvP2p(StcEvtRecvP2p evt){
		final StNetPacket pkt = evt.packet;
		util.assertTrue(pkt.isTypeRequest() || pkt.isTypeResponseAllow() || pkt.isTypeResponseDeny(), "Packet is NOT REQUEST/ALLOW/DENY !");
		util.assertTrue(pkt.isTypeFlowFromClientToClient(), "Packet Flow NOT CLIENT-TO-CLIENT !");
		
		stLog.debug(pkt.makeLogicFlow(false, false));
		
		if(rprObject.sharedVar.getStat() == StcRprState.OFFLINE ){
			stLog.error("UnExpected P2P Packet When OFFLINE: " + pkt.dump() );
			return;
		}
		
		if(pkt.isTypeRequest()){
			final StcServiceP2p p2p_service = rprObject.getP2pService(pkt.getCmd());

			if(p2p_service == null){
				stLog.debug("process p2p-request in app layer...");
				rprObject.rprBridge.addNewEvent(new StcEvtAppRequest(pkt));
			}else{
				try {
					final StClientInfo r_ci = rprObject.sharedVar.getRemoteClientInfo(pkt.getSrcClientId());
					p2p_service.onRecvRequest(r_ci, pkt);
				} catch (ExpRemoteClientNoFound e) {
					e.printStackTrace();
					stLog.warn("No Client Info Found for P2P Request: " + pkt.dump());
				}
			}
		}
		
		else {
			final StClientRequest req = rprObject.getRequest(pkt.getRequestId());
			if(null != req){
				req.onFinish(pkt);
			}else{
				stLog.warn("No Request Found for Packet: " + pkt.dump());
			}
		}
	}
	
	
	
    /**
     * <pre>
     * Check client ONLINE/OFFLINE state
     * - send CMD_CLT_STATUS packet if client is online;
     * - re-login into CS if disconnected;
     * 
     * [Theodore: 2015-04-14]
     * 
     * Although server checks client status, client has to send CMD_CLT_STATUS packet!
     * Reason: to detect if connection to server is lost, client has to send a packet and check if an exception occurs! And vice verse.
     * e.g when network cable is un-plugin, recv-thread can not detect the lost connection at once! 
     * 
     * </pre>
     *
     */
    private void reportOnlineStatus()
    { 
		//		if( rprObject.info.isLogin() ){
		//			try {
		//				final StcReqSrv req = new StcReqClientStatus();
		//				req.send();
		//				return;
		//			} catch (ExpLocalClientOffline e) {
		//				e.printStackTrace();
		//				stLog.error(util.getExceptionDetails(e, "Fail to send clt-status report!"));
		//			}
		//		}
		
		(new StcExCheckAndLogin()).trigger();
    }
    
    
	final int HB_CheckSessionList 	= params.msRequestCheckPeriod / StcRprMainPulse.HB_PERIOD_MS;
 	final int HB_ReportOnlineStatus = params.msConnectionCheckPeriod / StcRprMainPulse.HB_PERIOD_MS;

 	
    private void prcHeartBeat()
    {
    	heartBeatCount++;
    	
    	if(rprObject.info.getRprState() == StcRprState.UPDATE_FRD){
    		(new StcExCheckFriends()).trigger();
    	}
    	
    	if(heartBeatCount % HB_CheckSessionList == 0 ){
    		StClientRequest.MANAGER.checkAll();
    	}
    	
    	if(heartBeatCount % HB_ReportOnlineStatus == 0 ){
			final long t_start = System.currentTimeMillis();
			reportOnlineStatus();
    		final long t_cost = System.currentTimeMillis() - t_start;
    		stLog.debug("Cost of report-online-status: " + t_cost + " ms");
    	}
    }//prcHeartBeat
    
    
    private void clientOffline(){
		stLog.info("client is offline");
    	if(rprObject.sharedVar.getStat() == StcRprState.OFFLINE) {
    		return;
    	}
    	
    	stLog.warn("Timeout all pending requests, before setting State to OFFLINE!");
    	StClientRequest.MANAGER.timeoutAll();
    	rprObject.sharedVar.setStat(StcRprState.OFFLINE);
    	rprObject.sharedVar.setSendStatus(false);
    }
    
    
    private void processEvtSession(StcEvtSession evt){
    	if(evt instanceof StcEvtSession.InfoConnected){
    		StcEvtSession.InfoConnected event = (StcEvtSession.InfoConnected)evt;
    		stLog.info("Connect Success!");
    		rprObject.sharedVar.setLocalIP(event.LocalAddr);
    	}
    	
    	else if (evt instanceof StcEvtSession.InfoDisConnected){
    		stLog.warn("DisConnect with Server!");
    		clientOffline();
    		rprObject.sendEventToApp(new StcEvtRpr.InfoOffline());
			//rprObject.sendMessage(StMessageToGui.OFFLINE);
    	}
    	
    	else if (evt instanceof StcEvtSession.InfoConnectFailure){
    		stLog.warn("Fail to Connect Server!");
    		clientOffline();
    	}
    	
    	else {
    		stLog.error("Unknown Event: " + evt.dump() );
    	}
    }


	/**
	 * @deprecated  by StRprObject.sendRequest()
	 */
	@SuppressWarnings("unused")
	private void sendRequest(final StClientRequest req){
		if(req.onPreSend()){
			StNetPacket pkt = req.buildOutPacket();
			stLog.debug(pkt.makeLogicFlow(true, false));
			rprObject.sendPacket(pkt);
		}else{
			stLog.error("Abort Request: " + req.dump() );
		}
    }
    
    
    private void processStartRequest(StcEvtStartRequest evt){
		final StClientRequest req = evt.request;
		
		if( rprObject.info.isOnline()  || req.isPreLoginRequest())
		{
			// if client is offline, only above requests are sent, e.g. SIGNUP and LOGIN
			rprObject.sendRequest(req);
			return;
		}
		
		if(!rprObject.ssObj.isConnected()){
			stLog.warn("NO Connection to Server ---- connect again...");
			rprObject.ssObj.connect();
		}
		
		stLog.warn("Client is OFFLINE. ReLogin Before Request...");
		final String name   = rprObject.sharedVar.getClientName();
		final String passwd = rprObject.sharedVar.getPasswd();
		if(name == null || passwd == null){
			stLog.error("Stop ReLogin -- name/passwd is NULL: " + name + "/" + passwd);
			stLog.error("Abort Request: " + req.dump() );
			return;
		}
		stLog.info("ReLogin as " + name + "/" + passwd);
		try {
			final StcReqSrvLogin req_login = new StcReqSrvLogin(name, passwd, "Auto ReLogin -- before a NON LOGIN/SIGNUP Request"){
				@Override
				protected void onResAllow(byte code, ByteBuffer data) {
					rprObject.sendRequest(req);
				}
				
				@Override
				protected void onResDeny(byte code, ByteBuffer data) {
					throw new StErrUserError("Abort Request: " + req.dump() );
				}
			};
			// TODO: send at once!
			req_login.startRequest();
		} catch (ExpLocalClientOffline e) {
			throw new StErrUserError("Impossible");
		}
    }
    
    
    private void  processEvtPostPacket(StcEvtPostPacket evt){
		throw new StErrUserError("TODO");
    }
    
    
	@Override
	public void handleEvent(StEvent event) {
		if(event instanceof StEvent.HeartBeat){
			prcHeartBeat();
			return;
		}
		
		if(event instanceof StEvent.SystemShutdown){
			stLog.info("[RPR] Main Pulse is shutting down ...");
			return;
		}
		
		stLog.trace(">>>> " + event);
		if (event instanceof StcEvtStartRequest){
			processStartRequest((StcEvtStartRequest) event);
		}
		
		else if (event instanceof StcEvtPostPacket){
			processEvtPostPacket((StcEvtPostPacket) event);
		}
		
		else if (event instanceof StcEvtRecvFromSrv){
			processEvtRecvFromSrv((StcEvtRecvFromSrv) event);	
		}
		
		else if(event instanceof StcEvtRecvP2p){
			processEvtRecvP2p((StcEvtRecvP2p) event);
		}
		
		else if(event instanceof StcRprExecution){
			((StcRprExecution)event).prc();
		}
		
		else if(event instanceof StcEvtSession){
			processEvtSession((StcEvtSession)event);
		}

		else if(event instanceof StcEvtDisconnect){
            stLog.info("disconnect in main pulse");
            rprObject.ssObj.disconnect();
			clientOffline();
		}

		else{
			throw new StErrUserError("Unknown Event: " + event.dump());
		}
		
		stLog.trace("<<<< " + event);
	}
	
}