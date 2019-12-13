package cn.teclub.ha.net.serv;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.List;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;


import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.lib.StConst;
import cn.teclub.ha.lib.StErrUserError;
import cn.teclub.ha.lib.StEvent;
import cn.teclub.ha.lib.StEventPulsePool;
import cn.teclub.ha.lib.StExpBreak;
import cn.teclub.ha.lib.StExpFamily;
import cn.teclub.ha.lib.StLoopThread;
import cn.teclub.ha.lib.StLoopThread.ExpLoopTimeout;
import cn.teclub.ha.lib.StTask;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.request.StRequestMgr;
import cn.teclub.ha.request.StSocketChannel;



/**
 * Server Component.
 * 
 * @author mancook
 *
 */
public class StSrvComp extends ChuyuObj
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	
	
    private static StSrvComp _inst = new StSrvComp();
    public static StSrvComp getInstance(){
        return _inst;
    }
    
	////////////////////////////////////////////////////////////////////////////
    // Inner Classes
	////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Members
	////////////////////////////////////////////////////////////////////////////
    
	private final StSrvGlobal 		global; 		// = StServerSSLGlobal.getInstance();
    private final StStunUdpSrv		udpStunSrv; 	// simple UDP STUN server
    private final StSrvCore 		modCore;
    
    private final SSLServerSocket			sslSock;
    
    /**
     * @deprecated
     */
	private final StSrvWaitThread 			sslWaitThraed;
    private final ServerSocketChannel		plainSockCh;
	private final StSrvChannelWaitThread 	plainWaitThraed;
    
	
    /**
     * Constructor
     * @throws IOException 
     */
    private StSrvComp(){
    	final StSrvConfig cfg = StSrvConfig.getInstance();
    	
    	try {
    		stLog.info(util.testMilestoneLog("[1] Create Core Pulse ...") );
	    	this.modCore = StSrvCore.getInstance();
	    	
	    	
	    	stLog.info(util.testMilestoneLog("[2] Init Managers, Global & Core Pulse ...") );
	        StTask.TaskMgr.initialize(modCore);
	        StRequestMgr.initialize(modCore);
	        StSrvSmsCodeMgr.initialize(modCore);
	        StLoopThread.LoopMgr.initialize();
	        // [Theodore: 2016-07-10] As global has managers, 
	        // DO NOT create global until all manager are initialized
	        this.global = StSrvGlobal.getInstance();
	        modCore.addDefaultListener();
	        
	        
	        stLog.info(util.testMilestoneLog("[3] Correct ONLINE clients..."));
	        correctOnlineClients();
	        
	        
	        stLog.info(util.testMilestoneLog("[4] Start STUN on UDP Port: " + cfg.getStunSrvPort() ));
	    	this.udpStunSrv = new StStunUdpSrv(cfg.getStunSrvPort());
	    	this.udpStunSrv.start();
	    	
	    	
	    	stLog.info(util.testMilestoneLog("[5] Start Listening Sockets...") );
	    	// [2016-7-14] [Android 6.0] javax.net.ssl.SSLHandshakeException: no cipher suites in common 
	    	// 
	    	//
	    	// [2018-1-11] StServer needs a NIO connection with each client. 
	    	// - SocketChannel is used for plain TCP connection. 
	    	// - However, Java (until 1.8) does not provide a class like SSLSocketChannel for SSL NIO. 
	    	//   But it seems not very difficult to implement such one. 
	    	//
	    	//
	    	// System.out.println("#### set keyStore & trustStore ");
	        // System.setProperty("javax.net.ssl.keyStore",	  	 cfg.keyStore );
	        // System.setProperty("javax.net.ssl.keyStorePassword", cfg.keyStorePass);
	        // System.setProperty("javax.net.ssl.trustStore",	  	  cfg.keyStore );
	        // System.setProperty("javax.net.ssl.trustStorePassword", cfg.keyStorePass);
	        // 
	    	// stLog.info("#### SSL with: '" + cfg.keyStore + "/" + cfg.keyStorePass + "' ...");
	    	stLog.warn("todo: implement SSL NIO Socket...");
	        System.setProperty("javax.net.ssl.keyStore",	  	 cfg.keyStore );
	        System.setProperty("javax.net.ssl.keyStorePassword", cfg.keyStorePass);
	        
	        SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
	        this.sslSock =  (SSLServerSocket) ssf.createServerSocket(cfg.getSrvPort1());
	        
	        this.plainSockCh = ServerSocketChannel.open();
	        this.plainSockCh.socket().bind(new InetSocketAddress(cfg.getSrvPort2() ));
	        
	        stLog.info(util.testMilestoneLog("[SSL  ] Listening Port: "+ cfg.getSrvPort1()  ));
	        stLog.info(util.testMilestoneLog("[PLAIN] Listening Port: "+ cfg.getSrvPort2()  ));
	        
	        
	        stLog.info(util.testMilestoneLog("Start Waiting Threads ...") );
	        // [2016-10-31] TODO: user NON-BLOCKING SSL socket
	        //		this.sslWaitThraed = new StSrvWaitThread(sslSock, true);
	        //		this.sslWaitThraed.start();
	        this.sslWaitThraed = null;
	        this.plainWaitThraed = new StSrvChannelWaitThread(plainSockCh, false, cfg.preprocPoolSize);
	        this.plainWaitThraed.start();
	        
	        util.sleep(200);  // wait for all threads have started up
	        stLog.info(util.testMilestoneLog("DONE! [6/6] Server Compoment Created!") );
	        stLog.info("==== Server Version: " + StConst.getVersionInfo() + " ====");
    	}catch(IOException | InterruptedException e){
    		throw new StErrUserError("Constuctor Server Compoment Failure!");
    	}
    }
    
    
    private void correctOnlineClients() throws InterruptedException {
    	final StDBObject db_obj = global.dbObjMgr.getNextObject();
		
		stLog.debug("[1] Correct ONLINE clients...");
		List<StModelClient> list = db_obj.queryOnlineAll();  
    	if(list.size() < 1){
    		stLog.info("NO ONLINE clinets ^_^ ");
    	}else{
        	for(StModelClient mc: list){
        		if(! mc.isFlag_Online() ){
        			continue;
        		}
        		stLog.warn("!!!! Find ONLINE Client: " + mc );
        		int flag = mc.getFlag();
        		flag = StClientInfo.Util.setFlag_Online(flag, false);
        		mc.setFlag(flag);
        		// [2016-11-3] update in ONE SQL!
        		//dbObj.updateRecord(mc);
        	}
        	db_obj.updateRecords(list.toArray(new StModelClient[0]));
        	stLog.info("==== Correct " + list.size() + " clients: ONLINE --> OFFLINE");
    	}
    	db_obj.close();
    	stLog.debug("[2] TODO: Check DB tables. e.g. assigned SIP records...");
    }


    public void sendEventToCore(StEvent e){
    	modCore.addNewEvent(e);
    }
    
    
    
    public StringBuffer debug_showCount(StringBuffer sbuf, StDBObject db_obj) {
    	if(sbuf == null){
    		sbuf = new StringBuffer(256);
    	}
    	
		util.dumpFunc.addDumpHeaderLine(sbuf, " Show Count ");
		util.dumpFunc.addDumpLine(sbuf, ">> MAX DB Object: " + StDBObject.ObjectMgr.OBJ_COUNT);
		util.dumpFunc.addDumpLine(sbuf, ">> ONLINE in DB : " + db_obj.debug_getOnlineCount());
		util.dumpFunc.addDumpLine(sbuf, ">> Request Count: " + StRequestMgr.getInstance().toString());
		util.dumpFunc.addDumpLine(sbuf, ">> Accept Count : " + StSrvComp.getInstance().getAcceptCount());
		
		util.dumpFunc.addDumpLine(sbuf, ">> ... ");
		util.dumpFunc.addDumpLine(sbuf, ">> PreProc Pool : " + StSrvComp.getInstance().getPoolSize());
		StEventPulsePool.getInstance().debug_getCount(sbuf);
		global.connMgr.debug_getCount(sbuf);
		util.dumpFunc.addDumpEndLine(sbuf);
		
		util.dumpFunc.addDumpLine(sbuf, "");
		global.hiberMgr.debug_statistics(sbuf);
		return sbuf;
    }
    
    
    

    int getAcceptCount(){
    	return plainWaitThraed.getAcceptCount();
    }
    
    
    int getPoolSize(){
    	return plainWaitThraed.getPoolSize();
    }
    
    
    void clearup() {
		stLog.debug(">>>>");
        // 2016-8-8: close listening socket before closing existing connection
		// 
		stLog.info("[1] Close Listening Sockets...");
        try {
			sslSock.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			stLog.error(util.getExceptionDetails(e1, ""));
		} 						
        try {
			plainSockCh.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			stLog.error(util.getExceptionDetails(e1, ""));
		}
        
        stLog.info("[2] Delete all connections");
        global.connMgr.deleteAll();
        
		stLog.info("[3] Stop wait-threads...");
		try {
			if(sslWaitThraed != null) 	sslWaitThraed.stop();
			if(plainWaitThraed != null) plainWaitThraed.stop();
		} catch (ExpLoopTimeout e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "End-Loop Timeout"));
		}
        
        stLog.info("[4] Stop STUN Server");
        udpStunSrv.clear();
        
        stLog.info("[5] Stop Core Module");
        modCore.stop();
        StEventPulsePool.getInstance().stop();
        
        global.taskMgr.checkFinished();
        System.out.println("#### Running Tasks: " + global.taskMgr.dumpTasks());
        
        StLoopThread.LoopMgr loop_mgr =  StLoopThread.LoopMgr.getInstance();
        loop_mgr.checkFinished();
        System.out.println("#### Running Loops: " + loop_mgr.dump());
        
        stLog.info(util.testMilestoneLog("[5/5] Clearup Server Compoment ! "));
	}

}







class StSrvChannelWaitThread extends StLoopThread
{
	private final int POOL_SIZE;
	
	private final StSrvPreprocess[]	 	preprocessPool;
	private final ServerSocketChannel	sockCh;
	private final String				connType;
	private int acceptCount = 0;
	
	
	/**
	 * Constructor
	 * @throws IOException
	 */
	StSrvChannelWaitThread(final ServerSocketChannel sock_ch, final boolean use_ssl, final int pool_size)
	{
		this.sockCh = sock_ch;
		this.connType = use_ssl ? "[ SSL ]":"[PLAIN]";
		this.POOL_SIZE = pool_size;
		this.preprocessPool = new StSrvPreprocess[POOL_SIZE];
	}
	
	
	int getAcceptCount(){
		return acceptCount;
	}
	
	
	int getPoolSize(){
		return POOL_SIZE;
	}
	
	
	protected boolean loopStart() { 
		for(int i=0; i<POOL_SIZE; i++){
			preprocessPool[i] = new StSrvPreprocess("PreProcess-Pulse-" + i);
		}
		util.sleep(200);
		stLog.info("Created PreProcess Pool");
		return true;
	}
	
	
	@Override
	protected void loopOnce() throws StExpFamily {
		stLog.debug(connType + " waiting for connection... ");
		try {
			preprocessPool[acceptCount++ % POOL_SIZE].onNewSocket(new StSocketChannel( sockCh.accept()));
		} catch (IOException e) {
			// e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, connType + " Wait Thread Abort!"));
			throw new StExpBreak();
		}
	}
}





/**
 * <h1>Wait for new connection. </h1>
 * 
 * <p> Used by both SSL and Plain sockets. 
 * 
 * @author mancook
 * 
 * @deprecated DO NOT use BLOCKING socket!
 *
 */
class StSrvWaitThread extends StLoopThread
{
//	private final ServerSocket	ss;
//	private final String		connType;
//	
//	/**
//	 * Constructor
//	 * @throws IOException
//	 */
//	StSrvWaitThread(final ServerSocket sock, final boolean use_ssl)
//	{
//		this.ss = sock;
//		this.connType = use_ssl ? "[ SSL ]":"[PLAIN]";
//	}
	
	
	@Override
	protected void loopOnce() throws StExpFamily {
		//		stLog.trace(connType + " waiting for connection... ");
		//		try {
		//			StSocket st_sock = new StSocket( ss.accept());
		//			stLog.info(connType + " Got a connection !!!!");
		//			StSrvPreprocess p = new StSrvPreprocess(st_sock); 
		//			p.start();
		//		} catch (IOException e) {
		//			// e.printStackTrace();
		//			stLog.error(util.getExceptionDetails(e, connType + " Wait Thread Abort!"));
		//			throw new StExpBreak();
		//		}
	}
}



