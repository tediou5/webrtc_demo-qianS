package cn.teclub.ha.net.serv;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.lib.StLoopThread;
import cn.teclub.ha.lib.StTask;
import cn.teclub.ha.net.serv.request.StSrvConnMgr;
import cn.teclub.ha.net.serv.request.StSrvConnMgrFactory;
import cn.teclub.ha.net.serv.request.StSrvServiceMgr;
import cn.teclub.ha.request.StRequestMgr;



/**
 * <h1>Server Global </h1>
 * 
 * <pre>
 * Store variables, used globally in this application. 
 * 
 * Accessibility
 * ~~~~~~~~~~~~~
 * - Configuration is ALWAYS accessible;
 * - Others are accessible, as soon as initialization finishes;
 * 
 * 
 * Variable Members
 * ~~~~~~~~~~~~~~~~
 * - Configuration;
 * - Parameters;
 * - Object Managers;
 * - Event Modules (NOT NOW);
 *
 *
 * DESIGN CONSIDERATION
 * ~~~~~~~~~~~~~~~~~~~~
 * Q: Put event module in global? 
 * A: NOT A GOOD IDEA! That means global is created after event modules. 
 * However, listeners uaually use global, and an event module creates its listeners when constructed, 
 * which requires global be created before listener. 
 *
 * </pre>
 * 
 * @author mancook
 *
 */
public  class StSrvGlobal extends ChuyuObj
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	

    private static StSrvGlobal _ins = new StSrvGlobal();;
    
    /**
     * ATTENTION: Initialize managers before constructing global object. 
     * 
     * @return
     */
    public static StSrvGlobal getInstance(){
        return _ins;
    }
    
    
	////////////////////////////////////////////////////////////////////////////
    // Instance Membersq
	////////////////////////////////////////////////////////////////////////////	
    public final StSrvConfig			cfg = StSrvConfig.getInstance();
    public final StTask.TaskMgr			taskMgr = StTask.TaskMgr.getInstance();
    public final StLoopThread.LoopMgr 	loopMgr = StLoopThread.LoopMgr.getInstance();
   
    public final StRequestMgr 			reqMgr = StRequestMgr.getInstance();
    public final StDbHiberMgr			hiberMgr = StDbHiberMgr.getInstance();
    public final StDBObject.ObjectMgr	dbObjMgr = StDBObject.ObjectMgr.getInstance();
    public final StSrvConnMgr			connMgr ;
    
    
    
    
    /**
     * ATTENTION: Initialize managers before constructing global object. 
     * 
     * Constructor.
     */
    private StSrvGlobal(){
    	System.out.println("####[StSrvGlobal] Config: \n" + cfg.toString());
    	if(cfg.usePulseConn()){
    		connMgr = StSrvConnMgrFactory.getInstance().createConnMgr(StSrvConnMgrFactory.Type.PULSE_CONN);
    	}else {
    		connMgr = StSrvConnMgrFactory.getInstance().createConnMgr(StSrvConnMgrFactory.Type.LIGHT_CONN);
    	}
    }
    
    
    
    /**
     * <pre>
     * DO NOT use a final reference to serviceManager!!!! 
     * Reason: global is used by service objects, 
     * which is created when constructing service-manager.
     * </pre>
     * @return
     */
    public StSrvServiceMgr getServiceMgr(){
    	return StSrvServiceMgr.getInstance();
    }

    
   public StSrvSmsCode getSmsCode(String sms_code){
	   return StSrvSmsCodeMgr.getInstance().get(sms_code);
   }
    

    public  interface DebugRoutine{
    	void execute();
    }

	public void debugCode(DebugRoutine dr ) {
		if(! cfg.isDebug()){
			return;
		}
		stLog.debug("-------------------------------------------------------");
		final long ms_start = System.currentTimeMillis();
		dr.execute();
		stLog.info("#### Debug Cost: " + util.getCostStr(ms_start));
		stLog.debug("---------------------------------------------------[][]");
	}
}
