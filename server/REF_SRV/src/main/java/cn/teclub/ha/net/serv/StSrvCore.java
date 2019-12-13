package cn.teclub.ha.net.serv;


import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.lib.StConst;
import cn.teclub.ha.lib.StEvent;
import cn.teclub.ha.lib.StEventListener;
import cn.teclub.ha.lib.StEventPulse;
import cn.teclub.ha.lib.StEventPulsePool;
import cn.teclub.ha.lib.StEvent.SystemShutdown;



/**
 * <h1> Server Core Listener. </h1>
 * 
 * <pre> 
 * 
 * [2016-10-21] Core listeners handle events on:
 * 1) client connection:
 *   e.g. add/delete a connection;
 *    
 * 2) [????] DB Update/Save/Delete Operation;
 *    e.g. ClientOnline, ClientOffline, Add/Delete a client, add/delete a friendship, etc.
 *    
 * 
 * 
 * [2016-11-2] Core listener Does:
 * - check loop, task managers;
 * - pass requests to dest connections;
 * - check timeout requests;
 * 
 * 
 * </pre>
 * 
 * @author mancook
 */
class StSrvCoreListener extends ChuyuObj implements StEventListener 
{
	private final StSrvGlobal 	global 		= StSrvGlobal.getInstance();
	
	private final int 			CHECK_MGR_HB 	= global.cfg.checkMgrTime * StConst.SRV_CORE_PULSE_RATE;
    private final StringBuffer 	sbufDebug 		= new StringBuffer(1024);
    
	private int	hbCount = 0;
	private StDBObject	dbObj;
     
    
    private void prcHeartBeat(final StEvent.HeartBeat evt) 
    {
    	hbCount++;
    	
    	if(hbCount % CHECK_MGR_HB == 0 ){
    		//final long MS_START = System.currentTimeMillis();
    		global.reqMgr.checkTimeout();
    		global.loopMgr.checkFinished();
    		global.taskMgr.checkFinished();
    		//stLog.debug("Req  Mgr: " + global.reqMgr);
    		//stLog.debug("Loop Mgr: " + global.loopMgr);
    		//stLog.debug("Task Mgr: " + global.taskMgr);
    		//stLog.debug("Check-Managers Cost: " + util.getCostStr(MS_START));
    	}
    	
    	if(hbCount % ( CHECK_MGR_HB * 10) == 0 ){
    		sbufDebug.setLength(0);
    		stLog.debug("Req  Mgr: " + global.reqMgr);
    		stLog.debug("Loop Mgr: " + global.loopMgr);
    		stLog.debug("Task Mgr: " + global.taskMgr);
    		stLog.debug(StSrvComp.getInstance().debug_showCount(sbufDebug, dbObj));
    	}
    } 
	
	
	
	
    private void prcEvtServerDebug(StEvtServerDebug evt){
    	sbufDebug.setLength(0);
    	if(evt instanceof StEvtServerDebug.DumpClient){
    		System.out.println("[Deprecated] Show all clients in DB...");
    		System.out.println("             ---- TOO MUCH! ");
    		
			//			sbuf.append("DEBUG: DUMP HA Clients");
			//			sbuf.append("\n\t ###############################################################################");
			//			sbuf.append("\n\t ####  HA Client Manager (in RAM of HA server)  ####");
			//			sbuf.append("\n\t ###############################################################################");
			//			sbuf.append("\n\t #");
			//	    	sbuf.append(clientMgr.dump());
			//	    	
			//	    	sbuf.append("\n");
			//	    	sbuf.append("\n\t ###############################################################################");
			//	    	sbuf.append("\n\t ####  Online HA Clients (from HA DB) ####");
			//	    	sbuf.append("\n\t ###############################################################################");
			//			sbuf.append("\n\t #");
			//	    	List<StModelClient> list  = dbObj.queryOnlineAll();
			//	    	for(StModelClient clt: list){
			//	    		sbuf.append(clt.dump());
			//	    	}
			//	    	
			//	    	sbuf.append("\n");
			//			sbuf.append("\n\t #### Finish Dumping! #### ");
			//			System.out.println(sbuf);
	    	return;
    	}

    	if(evt instanceof StEvtServerDebug.ShowPulsePool){
    		sbufDebug.append(StEventPulsePool.getInstance().checkAllPulse());
    		System.out.println(sbufDebug);
    		return;
    	}
    	
    	if(evt instanceof StEvtServerDebug.ShowPulsePoolStatus){
    		util.dumpFunc.addDumpHeaderLine(sbufDebug, "  Pulse Pool (Last Status) ");
    		sbufDebug.append( StEventPulsePool.getInstance().getLastStatus() );
    		System.out.println(sbufDebug);
    		return;
    	}
    	
    	if(evt instanceof StEvtServerDebug.DumpSummary){
    		dbObj.debug_summary(sbufDebug);
    		System.out.println(sbufDebug);
    		return;
    	}
    	
    	if(evt instanceof StEvtServerDebug.ShowCount){
    		StSrvComp.getInstance().debug_showCount(sbufDebug, dbObj);
    		System.out.println(sbufDebug);
    		return;
    	}
    }
    
    
	//    private StringBuffer showCount(StringBuffer sbuf) {
	//    	if(sbuf == null){
	//    		sbuf = new StringBuffer(256);
	//    	}
	//    	
	//		util.dumpFunc.addDumpHeaderLine(sbuf, " Show Count ");
	//		util.dumpFunc.addDumpLine(sbuf, ">> MAX DB Object: " + StDBObject.ObjectMgr.OBJ_COUNT);
	//		util.dumpFunc.addDumpLine(sbuf, ">> ONLINE in DB : " + dbObj.debug_getOnlineCount());
	//		util.dumpFunc.addDumpLine(sbuf, ">> Request Count: " + StRequestMgr.getInstance().toString());
	//		util.dumpFunc.addDumpLine(sbuf, ">> Accept Count : " + StSrvComp.getInstance().getAcceptCount());
	//		
	//		util.dumpFunc.addDumpLine(sbuf, ">> ... ");
	//		util.dumpFunc.addDumpLine(sbuf, ">> PreProc Pool : " + StSrvComp.getInstance().getPoolSize());
	//		StEventPulsePool.getInstance().debug_getCount(sbuf);
	//		global.connMgr.debug_getCount(sbuf);
	//		util.dumpFunc.addDumpEndLine(sbuf);
	//		
	//		util.dumpFunc.addDumpLine(sbuf, "");
	//		global.hiberMgr.debug_statistics(sbuf);
	//		return sbuf;
	//    }
    
    
	@Override
	public String getEvtLisName() {
		return "[Lis]Srv-Core";
	}
	
	@Override
	public void handleEvent(StEvent event) {
		final long MS_START = System.currentTimeMillis();
		try{
            if(event instanceof StEvent.HeartBeat){
            	prcHeartBeat((StEvent.HeartBeat) event);
            	return;
            }	
            
            stLog.debug("[Srv-Core] >>>> " + event );
            
			if(event instanceof StEvtServerInitCore) {
				prcServerInitCore((StEvtServerInitCore)event);
				return;
			}
			
			if(event instanceof StEvent.SystemShutdown) {
				prcSystemShutdown((StEvent.SystemShutdown)event);
				return;
			}
			
			if(event instanceof StEvtServerDebug){
				prcEvtServerDebug((StEvtServerDebug)event);
				return;
			}
		} catch ( InterruptedException e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, ""));
			System.exit(-1);
		}finally{
			if(! (event instanceof StEvent.HeartBeat)){
				stLog.debug("Event Cost: " + util.getCostStr(MS_START) + event);
			}
		}

	}//EOF: handleEvent


	private void prcSystemShutdown(SystemShutdown event) {
		stLog.info("[Core] Close DB Object!");
		dbObj.close();
		dbObj = null;
	}


	private void prcServerInitCore(StEvtServerInitCore evt) throws InterruptedException {
		util.assertTrue(dbObj == null);
		stLog.info("[Core] Create DB Object");
		dbObj = global.dbObjMgr.getNextObject();
	}
}




/**
 * Single Server Core Module
 * 
 * @author mancook
 *
 */
public class StSrvCore extends StEventPulse 
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	
	
    private static StSrvCore _ins = new StSrvCore();;
    static StSrvCore getInstance(){
        return _ins;
    }
    
	////////////////////////////////////////////////////////////////////////////
    // Inner Classes
	////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Members
	////////////////////////////////////////////////////////////////////////////
    private StSrvCoreListener coreLis = null; // = new StServerSSLCoreListener();
    
    
	/**
	 * Constructor
	 */
	private StSrvCore() {
		super(	"Server-Core", 
				StConst.SRV_CORE_PULSE_PERIOD_MS * 2, 
				StConst.SRV_CORE_PULSE_PERIOD_MS      );
	}
	
	
	/**
	 * Listener uses global, which must be created BEFORE listener!
	 */
	void addDefaultListener(){
		util.assertTrue(coreLis == null, "DO NOT call twice!");
		coreLis = new StSrvCoreListener();
		addListener(coreLis);
		addNewEvent(new StEvtServerInitCore());
	}
}

