package cn.teclub.ha.request;

import java.nio.ByteBuffer;
import java.util.Vector;
import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.lib.StErrUserError;
import cn.teclub.ha.net.StClientInfo;




/**
 * <h1>Network Request<h1>
 * 
 * @author mancook
 */
public abstract class StRequest 
	extends  	ChuyuObj  
	implements 	ChuyuObj.DumpAttribute
{
	
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	
	
	
	////////////////////////////////////////////////////////////////////////////
    // Inner Classes
	////////////////////////////////////////////////////////////////////////////


	/**
	 * Request State.
	 * 
	 * <pre>
	 * 
	 * - WAITING: Initial State
	 * - FINISH:  WAITING FINISH.  Pulse thread will set response properties 
	 * - TIMEOUT: WAITING TIMEOUT. 
	 * - END:  	  Response properties are set.
	 * 
	 * Pulse thread will delete TIMEOUT & END request from manger.
	 * 
	 * </pre>
	 * 
	 * @author mancook
	 */
	public static class State 
	{
		private static final  Vector<State>  values = new Vector<State>();
		private static  final State WAITING 	= new State(1, "WAITING");
		private static  final State FINISH		= new State(2, "FINISH ");  	
		public  static  final State TIMEOUT		= new State(3, "TIMEOUT");	
		public  static  final State END			= new State(4, "END    ");		
															
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
	protected static final StRequestMgr   	_MANAGER  = StRequestMgr.getInstance();

	
	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	
	protected final StRequestID  	id; 
	
	
	/**
	 * <pre>
	 * [Server Request] local is always NULL; 
	 * [Client Request] local is NULL, if it is SIGNUP / LOGIN request;
	 * </pre>
	 */
	protected final StClientInfo		local;
	
	/**
	 * <pre>
	 * [Server Request] NOT NULL
	 * 
	 * [Client Request]
	 * remote is
	 * <li> NULL, 		if it is a request to server;
	 * <li> NOT NULL, 	if it is a P2P request;
	 * 
	 * </pre>
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
	
	public    final long 				tsCreate;	// ms
	public 	  final String 				dscp; 
	public	  final int 				TIMEOUT_MS; 	// MS
	
	private  State 	stat;
	private  long  	msWaitTime;  	// MS
	
	
	/**
	 * <h2> Constructor </h2>
	 * 
	 * TODO: [2016-11-16] NOT Thread Safe!
	 * Reason: Separated calls to _MANAGER.makeId() & _MANAGER.add(this)!
	 * 
	 * 
	 * @param r_clt -- NULL for request to server
	 * @param cmd
	 * @param code
	 * @param flow
	 * @param data
	 * @param timeout
	 * @param dscp
	 * 
	 */
	protected StRequest(
			final StClientInfo local,
			final StClientInfo remote,
			final StNetPacket.Command cmd,
			final byte code, 
			final StNetPacket.Flow flow,
			final ByteBuffer data,
			final int timeout, 
			final String dscp )
	{
		this.id = _MANAGER.makeId();
		this.stat = State.WAITING;
		this.TIMEOUT_MS = timeout;
		this.tsCreate = System.currentTimeMillis();
		this.cmd = cmd;
		this.reqFlow = flow;
		this.reqCode = code;
		this.reqData = data;
		
		this.remote = remote;
		this.local = local;
		
		this.dscp = dscp;
		this.msWaitTime = 0;
		_MANAGER.add(this);
	}
	
	
	////////////////////////////////////////////////////////////////////////////
	// methods for calling thread
	////////////////////////////////////////////////////////////////////////////
	
	synchronized public boolean isResAllowed() {
		util.assertTrue(getState() == State.END, "Request NOT END!");
		return resAllowed;
	}
	
	synchronized public boolean isTimeout(){
		return getState() == State.TIMEOUT;
	}
	
	synchronized public byte getResCode(){
		util.assertTrue(getState() == State.END, "Request NOT END!");
		return resCode;
	}
	
	
	
	/**
	 * Trigger an event to manager-pulse, which does pre-processing and send the request packet.
	 * 
	 * <pre>
	 * 2016-8-13: DO NOT send the request packet here!
	 * </pre>
	 * 
	 * @param loc_id
	 * @param rem_id
	 */
	public void startRequest(){
		_MANAGER.pulse.addNewEvent(new StEvtStartRequest(this));
	}
	

	/**
	 * Wait until request END or TIMEOUT.
	 *  
	 * @return
	 * - the result object, if state is END <br/>
	 * - null, if the state is TIMEOUT <br/>
	 */
	public Object waitForResult(){
		while(true){
			final State s = getState();
			if(s == State.END ){
				return resResult;
			}
			if(s == State.TIMEOUT){
				return null;
			}
			util.sleep(500);
		}
	}
	
	
	/**
	 * NOTE, call this method on a FINISH & END request.
	 * @return
	 */
	public synchronized int getWaitTime(){
		final State s = getState();
		util.assertTrue(s == State.FINISH || s == State.END, "State Error:" + s	);
		return (int)this.msWaitTime;
	}
	
	
	public synchronized State getState(){
		return this.stat;
	}
	
	

	
	
	public synchronized StRequestID getId(){
		return this.id;
	}
	
	
	
	////////////////////////////////////////////////////////////////////////////
	// methods for request & manager pulse
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Called by Request Pulse in event StEvtStartRequest.
	 * @return
	 */
	public StNetPacket buildOutPacket(){
		return StNetPacket.buildReq (
				cmd, reqFlow, reqCode, id,
				local == null ? null: local.getClientID(), 
				remote == null? null: remote.getClientID(),
				reqData );
	}
	
	
	private synchronized void setState(final State s){
		do{
			if(stat == State.WAITING){
				if(s == State.FINISH || s == State.TIMEOUT){
					break;
				}
			}
			else if(stat== State.FINISH){
				if(s == State.END){
					break;
				}
			}
			throw new StErrUserError("INVALID State Change: " + stat + " --X--> " + s);
		}while(false);
		stLog.trace("Set State: " + stat + "-->" + s + " -- " + this);
		this.stat = s;
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
			msWaitTime = ts_now - tsCreate;
			
			if(0 <= msWaitTime && msWaitTime <= TIMEOUT_MS){
				//stLog.info("#### update request wait-time: " + msWaitTime + " ms");
				return false;
			}
			
			if(msWaitTime < 0 ){
				stLog.error("!!!! System Time is reset to PREVIOUS !!!!");
				stLog.error("Cannot determine wait time. Just TIMEOUT this request!");
			}
			stat = State.TIMEOUT;
			return true;
		}
		else if(stat == State.TIMEOUT){
			return true;
		}
		else{
			stLog.warn("!!!! Request is not WAITING !!!!");
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
			setState(State.FINISH);
			return true;
		}else{
			stLog.warn("!!!! Request is not WAITING !!!!");
			return false;
		}
	}
	
	
	/**
	 * State END is necessary, 
	 * so that waitForResult() can get correct resData from a END request.
	 * 
	 */
	private synchronized void end(){
		setState(State.END);
	}
	
	
	/**
	 * <pre>
	 * Called in manger pulse, to
	 * - Tick WAITING request;
	 * - If TIMEOUT, call onTimeout() & delete from manager;
	 * </pre>
	 */
	public void tickRequest(){
		if(ticks()){
			stLog.warn("Request TIMEOUT: " + this.toString());
			onTimeout();
			_MANAGER.delete(id) ;
		}
	}
	

	/**
	 * Called in request pulse when a response is received.
	 * @param pkt
	 */
	public void finishRequest(final StNetPacket pkt){
		ticks();
		if(finish()){
			this.resAllowed = pkt.isTypeResponseAllow();
			this.resCode = pkt.getCode();
			this.resResult = pkt.getDataBuffer();
			onResponse(pkt);
			end();
			_MANAGER.delete(id);
			
			// TODO: Post Response
			/*
			rprObject.epUser.addNewEvent(
				new StcEvtRpr.ProcessInAppPulse(cmd, pkt.isTypeResponseAllow(), resCode, resResult) {
					@Override
					public void process() {
						onPostResponse();
					}
			});
			*/
		}
	}
	
	
	/**
	 * <p> Overwrite this method, if you want to process the raw ALLOW/DENY packet.
	 * 
	 * @param pkt
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
			onResDeny(resCode, (ByteBuffer)resResult);
		}
	}
	
	
	/**
	 * <p> Called in request Pulse. 
	 * Overwrite this method, to do something just before building & sending the request.
	 * 
	 * <p> NOTE: Request data can be changed in this method. 
	 * 
	 * 
	 * @return
	 * <li> true  -- request will be sent
	 * <li> false -- request will NOT be sent
	 */
	public boolean onPreSend(){ 
		return true;
	}
	
	
	
	protected abstract void onTimeout();
	protected abstract void onResAllow(final byte code, final ByteBuffer data);
	protected abstract void onResDeny(final byte code, final ByteBuffer data);
	
	/**
	 * TODO:
	 */
	protected void onPostResponse(){ 
		
	}
	
	////////////////////////////////////////////////////////////////////////////
	
	public String toString(){
		StringBuffer sbuf = new StringBuffer(128);
		sbuf.append("{"+ getClass().getSimpleName() +"}")
			.append(cmd + "-->" + (remote == null ? "<?>" : remote.getName()) + ",")
			.append(id).append(",")
			.append(stat).append(",")
			.append(msWaitTime).append("/")
			.append(TIMEOUT_MS).append("ms -- ")
			.append(dscp);
		return sbuf.toString();
	}
	
	
	@Override
	public void dumpSetup() {
		this.dumpSetTitle(" { " + cmd + " } ");
		this.dumpAddLine("    Cmd      :" + cmd );
		this.dumpAddLine("    ID       :" + id.toString() );
		this.dumpAddLine("    State    :" + stat );
		this.dumpAddLine("    Timeout  :" + TIMEOUT_MS );
		this.dumpAddLine("    WaitTime :" + msWaitTime );
		this.dumpAddLine(" Class:" + getClass());  
		this.dumpAddLine(" Description:" + this.dscp);  
	}
}
