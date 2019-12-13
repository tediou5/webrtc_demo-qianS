package cn.teclub.ha.client.session;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.lib.StEvent;
import cn.teclub.ha.lib.StEventPulse;
import cn.teclub.ha.lib.StLoopThread;
import cn.teclub.ha.lib.StTask;


/**
 * <h1> The Session Global Object </h1>
 * 
 * @author mancook
 *
 */
class StcSessionComp extends ChuyuObj 
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////
	private static final Object lock = new Object();
	private static StcSessionComp _ins = null;

	static StcSessionComp getInstance(){
		synchronized (lock){
			assert _ins != null;
			return _ins;
		}
	}


	////////////////////////////////////////////////////////////////////////////
    // Instance Members
	////////////////////////////////////////////////////////////////////////////	
	private final   StcCorePulse 	corePulse;
	private final 	StEventPulse	epUser;

	private StcNetState		netState = StcNetState.Disconnected;
	private boolean			closing = false;


	//private final StLoopThread.LoopMgr loopMgr;  
	//private final StTask.TaskMgr taskMgr;
	
	StcSessionComp(final StEventPulse ep_user){
		stLog.warn("#### construct session-comp...");
		synchronized (lock){
			util.assertTrue(_ins == null);
			this.epUser = ep_user;
			this.corePulse = new StcCorePulse();
			_ins = this;
			stLog.info("core pulse is created.");
		}

		// NOTE: core-pulse-lis uses task-mgr and loop-mgr,
		// create it after these managers are initialized!
		StTask.TaskMgr.initialize(corePulse);
		StLoopThread.LoopMgr.initialize();
		corePulse.addListener(new StcCorePulseLis(this));
		stLog.info("Session Component Constructed!");
	}

	public void destroy() {
		stLog.debug("destroy session-comp...");
		synchronized (lock) {
			util.assertTrue(_ins != null);
			corePulse.addNewEvent(new StcEvtSessionExecution.UserDisconnect());
			corePulse.stop();
			StLoopThread.LoopMgr.getInstance().destroy();
			StTask.TaskMgr.getInstance().destroy();
			_ins = null;
			stLog.info("Session Component Destroyed!");
		}
	}

	void sendEventToUse(StEvent e){
		epUser.addNewEvent(e);
	}

	void addNewEvent(StEvent e) {
		corePulse.addNewEvent(e);
	}

	synchronized boolean isConnected(){
    	return  netState == StcNetState.Connected;
    }
    
	synchronized StcNetState getNetState() {
		return netState;
	}
	
	synchronized void setNetState(final StcNetState state) {
        stLog.debug("[Session State] " + this.netState + " ~~~~> " + state);
        this.netState = state;
	}
	
	synchronized boolean isClosing() {
		return closing;
	}

	synchronized void setClosing(boolean closing) {
		this.closing = closing;
	}


}
