package cn.teclub.ha.client.rpr;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.client.StcException;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.request.StNetPacket;
import cn.teclub.ha.request.StRequestID;
import cn.teclub.ha.client.StcException.ExpLocalClientOffline;


/**
 * <h1>Network Request<h1>
 * 
 * @author mancook
 */
public abstract class StClientRequest
	extends  	ChuyuObj  
	implements 	ChuyuObj.DumpAttribute
{
	
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	
	
	/**
	 * Request Manager
	 * 
	 * @author mancook
	 */
	public static class Manager extends ChuyuObj
	{
	    ////////////////////////////////////////////////////////////////////////////
	    // STATIC MEMBERS AND METHODS
		////////////////////////////////////////////////////////////////////////////
		private static long lastID = new Random().nextLong() % 0xFFFF+ 0xFF;
		
	    private static Manager _Instance = null;
	    
	    public static Manager getInstance(){
	        if(null == _Instance ){
	            _Instance = new Manager();
	        }
	        return _Instance;
	    }
	    
		////////////////////////////////////////////////////////////////////////////
	    // Instance Attributes
		////////////////////////////////////////////////////////////////////////////	
	    private ConcurrentHashMap<StRequestID, StClientRequest>  	reqList;
	    
	    
	    /**
	     * Constructor
	     */
	    private Manager(){
	    	this.reqList = new ConcurrentHashMap<>();
	    	stLog.info("Request Manager is Constructed!");
	    }
	    
	    private StRequestID makeId(){
	    	StRequestID 		ss_id;
	    	StClientRequest 	ss;
	    	while(true){
	    		ss_id = new StRequestID(lastID++);
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
	     * @param id - request ID
		 *
	     * @return	a client request
	     */
	    public StClientRequest get(StRequestID id){
	    	if(id == null) {
	    		return null;
	    	}
	    	return reqList.get(id);
	    }
	    
	    
	    public void add(final StClientRequest req){
	    	Object obj = reqList.put(req.getId(), req);
	    	util.assertTrue(obj == null, "Request Exists: " + req.getId());
	    }
	    
	    
	    public StClientRequest delete(StRequestID id){
	    	if(id == null) {
	    		return null;
	    	}
	    	StClientRequest req= this.reqList.remove(id);
	    	stLog.debug("Delete Request: \n\t" + req);
	    	return req;
	    }
	    
	    
	    /**
	     * <pre>
	     * 
	     * Check all sessions: 
	     * 1) tick wait time of session;
	     * 2) delete FINISH and TIMEOUT sessions;
	     * 
	     * NOTE: Called in client-core-module
	     *
	     * </pre>
	     */
	    public void checkAll(){
	    	//stLog.debug("Before Checking: " + dumpAll() );

	    	Collection<StClientRequest> values = reqList.values();
	    	for(StClientRequest req: values){
				req.onTick();
	    	}
	    	
	    	//stLog.debug("After Checking: " +  this.dumpAll().toString() );
	    }
	    
	    
	    
	    /**
	     * Called when:
	     * - destroy RPR object;
	     */
		public void clear() {
			reqList.clear();
		}
		
		
	    /**
	     * Called when:
	     * - client is offline; 
	     */
		public void timeoutAll(){
			stLog.warn("force timeout all requests!");
			Collection<StClientRequest> values = reqList.values();
	    	for(StClientRequest ss: values){
	    		ss.stat = State.TIMEOUT;
	    		ss.onTick();
	    	}
		}
		
	    
	    synchronized StringBuffer dumpAll(){
	    	StringBuffer sbuf = new StringBuffer(512);
	    	int i=0;
	    	util.dumpFunc.addDumpStartLine(sbuf, " Dump All Requests. Count: " + reqList.size() + " " );
	    	for(StClientRequest req: reqList.values()){
	    		sbuf.append("\n\t Request [ "+ i++ + "]");
	    		sbuf.append(req.dump());
	    	}
	    	util.dumpFunc.addDumpEndLine(sbuf);
	    	return sbuf;
	    }


	}
	
	
	////////////////////////////////////////////////////////////////////////////
    // Inner Classes
	////////////////////////////////////////////////////////////////////////////


	
	/**
	 * Request State.
	 * 
	 * @author mancook
	 *
	 * @deprecated  TODO: use enum
	 */
	public static class State 
	{
		private static final  Vector<State>  values = new Vector<>();
		private static  final State WAITING 	= new State(1, "WAITING ");
		private static  final State FINISH		= new State(2, "FINISH  ");
		public  static  final State TIMEOUT		= new State(3, "TIMEOUT  ");
		public  static  final State END			= new State(4, "END  ");
		
		public static State fromInt(int v){
			for (int i=0; i<values.size();i++) {
				State s = values.get(i);
				if(s.value == v) return s;
			}
			throw new RuntimeException("Request state with value [" + v + "] is NOT found !");
		}

		// ---------------------------------------------
		// non-static members
		// ---------------------------------------------
		
		private final int value;
		private final String name;
		
		private State(int v, String n){
			value =v;
			name = n;
			values.addElement(this);
		}

		public String toString(){
			return name;
		}

	}
	
	
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////
	
	protected static final Manager   		MANAGER  = Manager.getInstance();
	protected static final StcSharedVar 	sharedVar = StcSharedVar.getInstance();

	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	
	protected final StcRprObject 	 	rprObject = StcRprObject.getInstance();
	protected final StcRprMainPulse 	mainPulse = rprObject.mainPulse;

	protected final StRequestID  		id; 
	
	/**
	 * local is  NULL, if it is SIGNUP / LOGIN request;
	 */
	protected final StClientInfo		local;
	
	/**
	 * remote is
	 * <li> NULL, 		if it is a server request;
	 * <li> NON_NULL, 	if it is a P2P request;
	 */
	protected final StClientInfo 		remote;
	
	protected final StNetPacket.Command cmd;
	protected final byte 				reqCode;
	protected final StNetPacket.Flow	reqFlow;
	protected ByteBuffer				reqData;
	
	/**
	 * Set in onFinish(). 
	 * May be modified in onResAllow() or onResDeny() of sub-class.
	 * 
	 */
	protected Object resResult;
	
	/**
	 * Set in onFinish()
	 */
	private byte resCode;
	private boolean resAllowed; 
	
	public    final long 				TS_CREATE;	// ms
	public	  final int 				msTimeout; 	// ms
	public 	  final String 				dscp; 
	
	private  State 	stat;
	private  long  	msWaitTime;  	// MS
	
	
	/**
	 * <h2> Constructor </h2>
	 * 
	 * @param remote - NULL for request to server
	 *
	 * @param cmd 	- request command
	 * @param code 	- request code
	 * @param flow	- request flow
	 * @param data	- request data
	 * @param timeout	- timeout value (ms)
	 * @param dscp	- request description
	 * 
	 */
	protected StClientRequest(
			final StClientInfo remote,
			final StNetPacket.Command cmd,
			final byte code, 
			final StNetPacket.Flow flow,
			final ByteBuffer data,
			final int timeout, 
			final String dscp )
	{
		this.id = MANAGER.makeId();
		this.stat = State.WAITING;
		this.msTimeout = timeout;
		this.TS_CREATE = System.currentTimeMillis();
		this.cmd = cmd;
		this.reqFlow = flow;
		this.reqCode = code;
		this.reqData = data;
		
		this.remote = remote;
		this.local = sharedVar.getLocalCopy();
		
		this.dscp = dscp;
		this.msWaitTime = 0;
		MANAGER.add(this);
	}
	
	public boolean isResAllowed() {
		return resAllowed;
	}


	public boolean isPreLoginRequest(){
		return cmd.isPreLoginReq();
	}
	

	public boolean isTimeout(){
		return stat == State.TIMEOUT;
	}
	
	

	/**
	 * <h2> Wait until request END or TIMEOUT. </h2>
	 *  
	 * DO NOTE rely on rpr-main-pulse! It can be dead anytime. 
	 * To avoid wait for ever when rpr-main-pulse is dead, call ticks() in each loop.
	 *
	 *
	 * @return  - the result object, if state is END <br/>
	 * @throws StcExpRpr.ExpReqTimeout  - state is TIMEOUT
	 */
	public Object waitForResult() throws StcExpRpr.ExpReqTimeout {
		for(int i=0;;i++){
			ticks();
			final State s = getState();
			if(s == State.END ){
				return resResult;
			}
			if(s == State.TIMEOUT){
				// return null;
				throw new StcExpRpr.ExpReqTimeout();
			}
			if(i % 2 ==0) { stLog.trace("Wait for result of requst: " + this); }
			util.sleep(500);
		}//for
	}
	
	
	
	public byte getResCode(){
		final State s = getState();
		util.assertTrue(s == State.END, "DO NOT get Response Code until the request ends");
		return resCode;
	}

	
	/**
	 * NOTE: call this method on a FINISH request.
	 */
	public synchronized int getWaitTime(){
		return (int)this.msWaitTime;
	}
	
	
	public synchronized State getState(){
		return this.stat;
	}


	public synchronized StRequestID getId(){
		return this.id;
	}
	
	StNetPacket buildOutPacket(){
		return StNetPacket.buildReq (
				cmd, reqFlow, reqCode, id,
				local == null ? null: local.getClientID(), 
				remote == null? null: remote.getClientID(),
				reqData );
	}



	/**
	 * @throws ExpLocalClientOffline
	 * NOTE: some requests do not throw this exception!
	 */
	public void startRequest() throws ExpLocalClientOffline {
		//
		// 8-13: DO NOT send the request packet here!
		// Trigger an event to main pulse, which does pre-processing and startRequest the request packet.
		// rprObject.sendPacket(pkt);
		//
		if( local == null
				&& cmd != StNetPacket.Command.ResetPasswd
				&& cmd != StNetPacket.Command.PreLoginQuery
				&& cmd != StNetPacket.Command.SmsVerifyCode
				&& cmd != StNetPacket.Command.Signup
				&& cmd != StNetPacket.Command.Login )
		{
			throw new StcException.ExpLocalClientOffline();
		}
		mainPulse.addNewEvent(new StcEvtStartRequest(this));
	}

	
	/**
	 * Update the wait_time, if state is WAITING.
	 * 
	 * @return
	 * - true: state is TIMEOUT or changes to TIMEOUT <br/>
	 * - false: others <br/>
	 */
	private synchronized boolean ticks(){
		if(stat == State.WAITING){
			final long ts_now = System.currentTimeMillis();
			msWaitTime = ts_now - TS_CREATE;
			
			if(0 <= msWaitTime && msWaitTime <= msTimeout){
				//stLog.info("#### update request wait-time: " + msWaitTime + " ms");
				return false;
			}
			
			if(msWaitTime < 0 ){
				stLog.error("System Time is reset to PREVIOUS!");
				stLog.error("-- Request Create Time: " + util.getTimeStampMS(TS_CREATE));
				stLog.error("-- Current Time:        " + util.getTimeStampMS(ts_now));
				stLog.error("Cannot determine wait-time. Just TIMEOUT this request!");
			}
			stat = State.TIMEOUT;
			return true;
		}
		else if(stat == State.TIMEOUT){
			return true;
		}
		else{
			stLog.debug("Request is not WAITING/TIMEOUT: " + stat );
			return false;
		}
	}
	
	
	/**
	 * 
	 * @return
	 * - true: state changes WAITING --> FINISH
	 * - false: others
	 */
	private synchronized boolean finish(){
		if(stat == State.WAITING){
			stat = State.FINISH;
			return true;
		}else{
			stLog.warn("!!!! Request is not WAITING !!!!");
			return false;
		}
	}
	
	
	private synchronized void end(){
		util.assertTrue(stat == State.FINISH, "Incorrect State: " + stat);
		stat = State.END;
	}



	////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//
	// called in main-pulse
	//
	//
	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Called by req-manager, which runs in main-pulse
	 *
	 * - Tick WAITING request;
	 * - If TIMEOUT occurs, call timeout runnable & delete it;
	 * -
	 */
	public void onTick(){
		if(ticks()){
			stLog.warn("Request TIMEOUT: " + this.toString());
			onTimeout();
			rprObject.sendEventToApp(new StcEvtRpr.InfoReqTimeout(cmd));
			//rprObject.sendMessage(StMessageToGui.REQ_TIMEOUT, cmd);
			MANAGER.delete(id) ;
		}
	}


	/**
	 * Called in core/user module when a response is received.
	 * @param pkt - response packet
	 */
	public void onFinish(final StNetPacket pkt){
		ticks();
		if(finish()){
			this.resAllowed = pkt.isTypeResponseAllow();
			this.resCode = pkt.getCode();
			this.resResult = pkt.getDataBuffer();
			onResponse(pkt);
			end();
			MANAGER.delete(id);
		}
	}
	
	
	/**
	 * <p> Overwrite this method, if you want to process the raw ALLOW/DENY packet.
	 * 
	 * @param pkt - the response packet
	 */
	protected void onResponse(final StNetPacket pkt){
		if(pkt.isTypeResponseAllow()){
			stLog.debug("Request ALLOW: " + cmd);
			onResAllow(resCode, (ByteBuffer)resResult);
		}else{
			stLog.warn ("Request DENY : " + cmd);
			if(resCode == StNetPacket.Code.DENY_P2P_Service_BUSY){
				stLog.warn("P2P-Service BUSY");
			}
			if(resCode == StNetPacket.Code.DENY_SRV_Service_BUSY){
				stLog.warn("Service4Srv BUSY");
			}
			if(resCode == StNetPacket.Code.DENY_P2P_RUNNING_OPT){
				stLog.warn("Remote is running an operation!");
			}
			onResDeny(resCode, (ByteBuffer)resResult);
		}
	}
	
	
	/**
	 * <pre>
	 * Called in RPR Main Pulse.
	 * Overwrite this method, to do something just before building & sending the request.
	 * NOTE: Request data can be changed in this method.
	 *
	 * [Theodore: 2017-10-25] main-pulse will finish & end this request, if it returns false.
	 *
	 *
	 * </pre>
	 * 
	 * @return
	 * <li> true  -- request will be sent
	 * <li> false -- request will NOT be sent
	 */
	protected boolean onPreSend(){ 
		return true;
	}


	protected void abort(){
		finish();
		end();
		MANAGER.delete(id);
		stLog.warn("Abort Request: " + dump() );
	}
	
	protected abstract void onTimeout();
	protected abstract void onResAllow(final byte code, final ByteBuffer data);
	protected abstract void onResDeny(final byte code, final ByteBuffer data);
	
	
	
	////////////////////////////////////////////////////////////////////////////
	
	public String toString(){
		return ("[" + cmd + "] ") +
				id + "," +
				stat + "," +
				msWaitTime + "/" +
				msTimeout + "ms -- " +
				dscp;
	}
	
	
	@Override
	public void dumpSetup() {
		this.dumpSetTitle(" { " + cmd + " } ");
		
		this.dumpAddLine("    Cmd      :" + cmd );
		this.dumpAddLine("    ID       :" + id.toString() );
		this.dumpAddLine("    State    :" + stat );
		this.dumpAddLine("    Timeout  :" + msTimeout );
		this.dumpAddLine("    WaitTime :" + msWaitTime );
		this.dumpAddLine(" Class:" + getClass());  
		this.dumpAddLine(" Description:" + this.dscp);  
	}
}
