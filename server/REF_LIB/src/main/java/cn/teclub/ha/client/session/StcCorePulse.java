package cn.teclub.ha.client.session;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.client.StcParams;
import cn.teclub.ha.lib.StErrUserError;
import cn.teclub.ha.lib.StEvent;
import cn.teclub.ha.lib.StEventListener;
import cn.teclub.ha.lib.StEventPulse;
import cn.teclub.ha.lib.StLoopThread;
import cn.teclub.ha.lib.StTask;
import cn.teclub.ha.lib.StLoopThread.ExpLoopTimeout;
import cn.teclub.ha.request.StSocket;


/**
 * <h1> Event Pulse: Session Core </h1>
 * 
 * @author mancook
 *
 */
class StcCorePulse extends StEventPulse
{
	static final int HB_INIT_MS 		= 1000;
	static final int HB_PERIOD_MS 	= 500;

	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructor
	 * 
	 * <p> This is an event handler. 
	 * The event pumping thread starts, in constructor of event handler.
	 */
	StcCorePulse() {
		super("Session-Core-Pulse", HB_INIT_MS, HB_PERIOD_MS);
		stLog.info("Session Core-Pulse Constructed!");
	}
}



class StcCorePulseLis extends ChuyuObj implements StEventListener
{
	private final StcSessionComp 		ssComp;
	private final StcParams 			params;
	private final SSLSocketFactory 		sockFac;
	private final StTask.TaskMgr		taskMgr; 
	private final StLoopThread.LoopMgr  loopMgr;

	private StSocket					sockToSrv;
	private StcRecvThread				recvThread;   // Loop to receive from server
	
	private int heartBeatCount = 0;

	
	/**
	 * Constructor
	 */
	StcCorePulseLis(final StcSessionComp ss_comp){
		//this.epUser = ss_comp.epUser;
		this.ssComp =  ss_comp;
		this.params = StcParams.getInstance();
		this.taskMgr= StTask.TaskMgr.getInstance();
		this.loopMgr= StLoopThread.LoopMgr.getInstance();
		
		if(!params.useSSL){
			stLog.info("[*] SSL is NOT used. Do nothing!");
			this.sockFac = null;
			return;
		}
		stLog.info("[*] load SSL public key");
		if(params.sockFac != null){
			stLog.info("#### Use SSL socket factory from init-parameters!");
			this.sockFac = params.sockFac;
			return;
		}
		try {
			stLog.info("#### Key Path: " + params.keyPath  + ", #### DefAlg: " + KeyManagerFactory.getDefaultAlgorithm() );
			InputStream ins = new FileInputStream(params.keyPath);
			final KeyStore trusted = KeyStore.getInstance("JKS");
			
			final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory
					.getDefaultAlgorithm());
			final TrustManagerFactory tmf = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			final SSLContext sc = SSLContext.getInstance("TLS");
			trusted.load(ins, "abcd1234".toCharArray());
			kmf.init(trusted, "abcd1234".toCharArray());
			tmf.init(trusted);
			ins.close();
			
			sc.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new java.security.SecureRandom());
			this.sockFac = sc.getSocketFactory();
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException | KeyManagementException e) {
			e.printStackTrace();
			throw new StErrUserError("Fail to load SSL public key!");
		}
	}
	
	
	@Override
	public String getEvtLisName() {
		return "Core-Pulse-Lis";
	}

	
	/**
     * Establish Connection to server.
     * 
     * <ol>
     * <li> Connect to server by socket;
     * <li> Start recv-thread;
     * <li> Set net state to CONNECTED;
     * </ol>
     * 
     * @return true: connect success
     */
    private boolean connect() {
    	stLog.trace(">>>>");
    	try{
    		if(sockToSrv != null){
    			stLog.warn("Abort Connect -- Socket to Server is Active!");
    			util.assertTrue(recvThread != null, "Recv-Thread is NULL");
    			return true;
    		}
    		util.assertTrue(recvThread == null, "Recv-Thread is NOT NULL");
    		util.assertTrue(sockToSrv == null, "Socket is NOT NULL");
    		
    		if(params.useSSL){
    			stLog.debug("[1] connect to SSL server: " + params.srvHost +":" + params.srvPort );
    			sockToSrv = new StSocket(sockFac.createSocket(params.srvHost, params.srvPort));
    			stLog.debug("[ SSL ] Socket Created!");
    		}else{
    			final int port =  params.srvPort + 1;
    			stLog.debug("[1] connect to PLAIN server: " + params.srvHost +":" + port);
        		sockToSrv = new StSocket(new Socket(params.srvHost, port));
        		stLog.debug("[PLAIN] Socket Created!");
    		}
    		
    		stLog.debug("[2] start recv-thread");
    		recvThread = new StcRecvThread(sockToSrv);
    		recvThread.start();
    		
    		stLog.debug("[3] set net state");
    		ssComp.setNetState(StcNetState.Connected);
    		stLog.info("[3/3] DONE! Connection Established with Local Address: " + sockToSrv.getDstAddress());
    		
    		
    		ssComp.sendEventToUse(new StcEvtSession.InfoConnected(sockToSrv.getDstAddress().toString()));
    		return true;
    	}catch(IOException e){
    		stLog.error(util.getExceptionDetails(e, "Fail to connect server " + params.srvHost +":" + params.srvPort ));
    		disconnect(false);
			ssComp.sendEventToUse(new StcEvtSession.InfoConnectFailure());
    	}finally{
    		stLog.trace("<<<<");
    	}
    	return false;
    }
    
 
    /**
     * - close socket;
     * - stop recv-thread;
     */
	private void disconnect(boolean inform_user) {
    	stLog.trace(">>>>");
		ssComp.setClosing(true);
    	try{
    		if(sockToSrv != null){
				stLog.debug("[1] close TCP socket to HA server.");
				//util.closeSocket(sockToSrv);
				sockToSrv.close();
				sockToSrv = null;
    		}
    		
			if(recvThread != null){
				stLog.debug("[2] Stop RecvThread");
				try {
					recvThread.stop();
					recvThread = null;
				} catch (ExpLoopTimeout e) {
					stLog.error(util.getExceptionDetails(e, "Timeout when stopping recv-from-srv loop"));
				}
			}
			
			stLog.debug("[3] Check tasks and loops");
	    	taskMgr.checkFinished();
	    	loopMgr.checkFinished();
			
	    	stLog.debug("[4] Set State to DISCONNECTED ");
			ssComp.setNetState(StcNetState.Disconnected);

			if(inform_user) {
				stLog.debug("[5] Inform user that client is Disconnected! ");
				ssComp.sendEventToUse(new StcEvtSession.InfoDisConnected());
			}

			ssComp.setClosing(false);
			stLog.info("[5/5] DONE! Disconnect End!");
    	}finally{
    		stLog.trace("<<<<");
    	}
    }

	
	private void processEvtRecvFromSrv(final StcEvtRecvFromSrv evt){
		// final StNetPacket pkt = evt.packet;
		// TODO: process packets in session layer
		ssComp.sendEventToUse(evt);
	}
	
	
	private void processEvtRecvP2p(final StcEvtRecvP2p evt){
		ssComp.sendEventToUse(evt);
	}
	
	
    private void processHeartBeat()
    {
    	heartBeatCount++;
     	final int HB_CheckTaskList 	= params.msTaskCheckPeriod / StcCorePulse.HB_PERIOD_MS;

    	if(heartBeatCount % HB_CheckTaskList == 0 ){
        	taskMgr.checkFinished();
        	loopMgr.checkFinished();
    	}
    }
    


    private void processEvtSendToSrv(StcEvtSendToSrv evt){
    	// [2016-9-14] Usually for LOGIN & SIGN-UP request.
    	// for other request, connection should be available !
		//    	if(! (ssComp.isConnected() || connect() )){
		//    		stLog.error("No Connection to Server. Fail to Send: " + evt.packet.dumpSimple());
		//    		return;
		//    	}
    	if(! ssComp.isConnected()){
    		stLog.warn("Client is disconnected! Connecting...");
    		if(!connect()){
    			stLog.error("No Connection to Server. Fail to Send: " + evt.packet.dumpSimple());
    			return;
    		}
    	}
    	
    	try {
			sockToSrv.sendPacket(evt.packet);
		} catch (IOException e) {
			e.printStackTrace();
			stLog.error("Fail to Send: " + evt.packet.dump());
			disconnect(true);
		}
    }
    
    
	@Override
	public void handleEvent(StEvent event) {
		if(event instanceof StEvent.HeartBeat){
			processHeartBeat();
			return;
		}
		
		stLog.trace(">>>> " + event );
		if(event instanceof StEvent.SystemShutdown){
			stLog.info("[SS ] Core Pulse is shutting down ...");
		}
		
		else if(event instanceof StcEvtSessionExecution.Connect){
			connect();
		}
		
		else if(event instanceof StcEvtSessionExecution.UserDisconnect){
            //disconnect(true);
			disconnect(false);
		}
		
		else if(event instanceof StcEvtRecvThreadStop){
			disconnect(true);
		}
		
		else if(event instanceof StcEvtSendToSrv){
			processEvtSendToSrv((StcEvtSendToSrv)event);
		}
		
		else if(event instanceof StcEvtRecvFromSrv){
			processEvtRecvFromSrv((StcEvtRecvFromSrv) event);
		}
		
		else if(event instanceof StcEvtRecvP2p){
			processEvtRecvP2p((StcEvtRecvP2p)event);
		}
		
		else{
			stLog.error("Unknown Event: " + event.dump() );
		}
		
		stLog.trace("<<<< " + event);
	}
	
}