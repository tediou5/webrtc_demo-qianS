package cn.teclub.ha.request;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.lib.StEventPulse;


public class StRequestMgr extends ChuyuObj
{
    ////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS AND METHODS
	////////////////////////////////////////////////////////////////////////////
	private static long _lastID = 0xE000E000L;
	
    private static StRequestMgr _ins;
    
    /**
     * ONLY called in main thread!!!!
     * Otherwise, there is thread safty issue!
     * @param pulse
     */
    public static void initialize(final StEventPulse pulse){
    	util.assertTrue(_ins == null, "DO NOT Initialize, Again!");
    	_ins = new StRequestMgr(pulse);
    }
    
    public static StRequestMgr getInstance(){
    	util.assertTrue(_ins != null, "Initialize before Use!");
        return _ins;
    }
    
	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	
    private ConcurrentHashMap<StRequestID, StRequest>  	reqList;
    
    private int addCount, delCount, timeoutCount, finishCount;
    
    final StEventPulse 	pulse ;

    
    
    /**
     * Constructor
     */
    private StRequestMgr(final StEventPulse p){
    	this.reqList = new ConcurrentHashMap<StRequestID, StRequest> ();
    	this.pulse  = p;
    	stLog.info("Request Manager is Constructed!");
    }
    
    
    public String toString(){
    	StringBuffer sbuf = new StringBuffer(128);
    	sbuf.append("{RequestMgr} Added=" + addCount + 
    			", Del=" + delCount + "(" + finishCount + "+" + timeoutCount + 
    			"), Running=" + reqList.size());
    	return sbuf.toString();
    }
    
    
    synchronized public StRequestID makeId(){
    	StRequestID ss_id = null;
    	StRequest ss =null;
    	while(true){
    		ss_id = new StRequestID(_lastID++);
    		ss = this.reqList.get(ss_id);
    		if(ss == null){
    			// current Request ID is NOT used! 
    			break;
    		}
    	}
    	return ss_id;
    }
    
    
    /**
     * <p> When a response is received,  the mapping request is gotten from MANAGER, and its onFinish() method is called.
     *  
     * <p> However, the request may be expired in onTick() in Pulse Thread, as soon as onFinish() starts.
     * In this case, onFinish() method does nothing. 
     * 
     * @param id
     * @return
     */
    synchronized  public StRequest get(StRequestID id){
    	if(id == null) {
    		return null;
    	}
    	return reqList.get(id);
    }
    
    
    synchronized public void add(final StRequest req){
    	Object obj = reqList.put(req.getId(), req);
    	util.assertTrue(obj == null, "Request Exists: " + req.getId());
    	addCount++;
    }
    
    
    synchronized public StRequest delete(StRequestID id){
    	if(id == null) {
    		return null;
    	}
    	StRequest req= this.reqList.remove(id);
    	if(req != null) {
    		delCount++;
    		if(req.isTimeout()){
    			timeoutCount++;
    		}else{
    			finishCount++;
    		}
    	}
    	stLog.trace("DELETE Request: \n\t" + req);
    	return req;
    }
    
    
    /**
     * Called in manager pulse. 
     * 
     * <pre>
     * 
     * Check all requests: 
     * 1) Tick wait time of requests;
     * 2) Delete TIMEOUT requests;
     * 
     * NOTE: END request is NOT delete by this method. 
     * It is deleted by request pulse in method funishRequest();
     *
     * </pre>
     */
    synchronized public void checkTimeout(){
    	final long MS_START = System.currentTimeMillis();
    	final int count_old  = reqList.size();
    	stLog.trace("Request List (before checking) " + dumpAll() );

    	Collection<StRequest> values = reqList.values();
    	for(StRequest req: values){
    		req.tickRequest();
    	}
    	stLog.trace("Request List (after checking) " + dumpAll() );
    	stLog.debug("Cost: " + util.getCostStr(MS_START) 
    			+ " -- Request Count: " + count_old + "/" + reqList.size());
    }
    
    
    synchronized public StringBuffer dumpAll(){
    	StringBuffer sbuf = new StringBuffer(512);
    	int i=0;
    	util.dumpFunc.addDumpStartLine(sbuf, " Dump All Requests. Count: " + reqList.size() + " " );
    	for(StRequest req: reqList.values()){
    		sbuf.append("\n\t Request [ "+ i++ + "]");
    		sbuf.append(req.dump());
    	}
    	util.dumpFunc.addDumpEndLine(sbuf);
    	return sbuf;
    }
}