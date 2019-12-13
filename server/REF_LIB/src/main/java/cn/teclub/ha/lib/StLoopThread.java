package cn.teclub.ha.lib;

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import cn.teclub.common.ChuyuObj;


/**
 * <h1> Loop Thread </h1>
 * 
 * <pre>
 * Loop ends if such exception is thrown by loopThread(): StExpBreak | StExpUserError.
 * 
 * </pre>
 * 
 * @author mancook
 *
 */
public abstract class StLoopThread 
		extends ChuyuObj 
		implements Runnable 
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	
	private static LoopMgr loopMgr = LoopMgr.getInstance();
	
	public static class ExpLoopTimeout extends StExpFamily {
		private static final long serialVersionUID = 6902144068741634601L;
		ExpLoopTimeout() { }
	}
	
	public static class State {
		private static final Vector<State>  values = new Vector<>();
		public  static final State LoopInit 		= new State(1, "LoopInit");
		public  static final State LoopStarted 		= new State(2, "LoopStarted");
		public  static final State LoopRunning 		= new State(3, "LoopRunning");
		public  static final State LoopStopped 		= new State(4, "LoopStopped");
		public  static final State LoopFinished 	= new State(5, "LoopFinished");
		
		public static State fromInt(int v){
			for (int i=0; i<values.size();i++) {
				State s = values.get(i);
				if(s.value == v) return s;
			}
			throw new RuntimeException("value [" + v + "] is NOT found !");
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
	
	
	/**
	 * LoopThread Manager
	 * 
	 * @author mancook
	 *
	 */
	public static class LoopMgr 
			extends ChuyuObj  
			implements ChuyuObj.DumpAttribute 
	{
		private static LoopMgr _ins;
		
		public static void initialize(){
			util.assertTrue(_ins == null);
			_ins = new LoopMgr();
		}
		public  static LoopMgr getInstance(){
			util.assertTrue(_ins != null);
			return _ins;
		}
		
		/////////////////////////////////////////////////////////////////////////////
		private final ConcurrentLinkedQueue<StLoopThread> list;
	    
	    private int addCount = 0;
	    private int delCount = 0;
	    
	    
		/**
		 * Constructor
		 */
		private LoopMgr(){
			list = new ConcurrentLinkedQueue<>();
		}
		
		synchronized void addLoop(StLoopThread loop){
			list.add(loop);
			addCount++;
		}
		
		
		public synchronized void stopAll(){
			stLog.trace(">>>>");
			long t_start = System.currentTimeMillis();
			for(StLoopThread loop : list){
				try {
					stLog.info("Stopping Loop: " + loop.toString());
					loop.stop();
					stLog.info("Stopped Loop:  " + loop.toString());
				} catch (ExpLoopTimeout e) {
					stLog.error(util.getExceptionDetails(e, "Timeout when stopping loop"));
				}
			}
			long t_cost = System.currentTimeMillis() - t_start;
			stLog.info("Stop All Cost: " + t_cost + " ms");
			stLog.trace("<<<<");
		}
		

		public synchronized void checkFinished(){
			final long MS_START = System.currentTimeMillis();
			
			// stLog.trace("Loops (Before Checking): " + dump());
			ArrayList<StLoopThread> del_list = new ArrayList<>();
			for(StLoopThread loop : list){
				if(loop.getState() == State.LoopFinished ){
					try {
						loop.stop();
					} catch (ExpLoopTimeout e) {
						stLog.error(util.getExceptionDetails(e, "Timeout when stopping loop"));
					}
					del_list.add(loop);
				}
			}
			for(StLoopThread loop : del_list){
				list.remove(loop);
				stLog.info("Removed Loop: " + loop);
				delCount++;
			}
			// stLog.trace("Loops (After Checking): " + dump());
			
	    	stLog.debug("Cost: " + util.getCostStr(MS_START) 
	    			+ " -- Deleted Count: " + del_list.size() + ", Left Count:" + list.size());
		}
		
		
		private synchronized void setStopFlag(){
			for(StLoopThread loop : list){
				loop.setStopFlag();
				loop.self.interrupt();
			}
		}
		
		
	    public void destroy(){
	    	util.assertTrue(_ins != null);
	    	// adding a loop causes runtime error!
	    	_ins = null;
	    	
	    	stLog.debug("Stopping all loops: " + dump());
	    	setStopFlag();
	    	final int MS_SLEEP = 500;
	    	final int MS_MAX_WAIT = 5000;
	    	for(int i=0; i<MS_MAX_WAIT; i+=MS_SLEEP){
	    		util.sleep(MS_SLEEP);
	    		checkFinished();
	    		if(list.size() == 0){
	    			break;
	    		}
	    	}
	    	
	    	if(list.size() != 0){
	    		stLog.error("there are un-stopped loops: " + dump());
	    		util.assertTrue(false);
	    	}
	    	stLog.info("All Loops Stopped!");
	    }
	    
	    
		@Override
		public void dumpSetup() {
			this.dumpSetTitle(" Dump All Loops , Count: " + list.size() + " ");
			this.dumpSetBufferSize(512);
			int  i = 0;
	    	for(StLoopThread loop : list){
	    		this.dumpAddLine("[" + i++ + "]" + loop.toString() );
	    	}
		}
		
		
	    public String toString(){
			return ("{LoopMgr} Added=" + addCount + ", Del=" + delCount + ", Running=" + list.size());
	    }
	}
	
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Members
	////////////////////////////////////////////////////////////////////////////	

	private final Thread 	self;
	private boolean     	isLogLoop ;
	private State			state;
	private long			lastLoopBeginTime;
	

	/**
	 * Constructor.
	 * 
	 */
	public StLoopThread(){
		this.self = new Thread(this);
		this.state  =State.LoopInit;
		this.isLogLoop = false;
		loopMgr.addLoop(this);
	}
	
	
	public String toString(){
		return "{" + getClass().getSimpleName() + "}" +
				getState() + "," +
				getCurrntLoopCost() + "ms";
	}
	
	
	synchronized public void start(){
		util.assertTrue(state != State.LoopStarted, "Thread has started!");
		state = State.LoopStarted;
		self.start();
		stLog.trace("starting loop thread: " + this);
	}

	
	/**
	 * <h2>Set the stop-flag.</h2>
	 * 
	 * <p> NOTE: the thread does not stop immediately! 
	 * It ends after current loopOnce() returns.
	 * 
	 */
	synchronized public void setStopFlag(){
		stLog.trace(">>>>");
		if(state == State.LoopRunning){
			state = State.LoopStopped;
		}
		stLog.debug("Loop State: " + state);
		stLog.trace("<<<<");
	}
	
	
	synchronized public State getState() {
		return this.state;
	}
	
	
	synchronized public long getCurrntLoopCost() {
		return (System.currentTimeMillis() - this.lastLoopBeginTime);
	}


	synchronized private void setLastLoopBeginTime(long last_loop_begin_ms) {
		this.lastLoopBeginTime = last_loop_begin_ms;
	}
	
	
	synchronized private void setState(State s){
		this.state = s;
	}
	

	/**
	 * <h2>Set the stop-flag and wait the thread ends.</h2>
	 * 
	 * <p> This is called in another thread. 
	 * ATTENTION: As this method waits, DO NOT use synchronized !!!!
	 * 
	 */
	public void stop() throws ExpLoopTimeout{
		try {
			stLog.trace(">>>>");
			setStopFlag();
			
			//stLog.info("#### Interrupt Loop Thread");
			//self.interrupt();
			
			stLog.debug("Waiting for loop finishing...");
			int timeout = 10*1000;
	    	int wait_ms = 0;
	    	for(int i=0; ; i++){
		    	if(wait_ms > timeout){
					stLog.error("Loop timeout -- " + this.dump() );
					stLog.error("waited " + wait_ms + " ms for its finishing");
					throw new ExpLoopTimeout();
				}
		    	if( getState() == State.LoopFinished ){
		    		stLog.debug("Loop finished. Waited " + i * StConst.WAIT_SLEEP_MS + " ms" );
		    		break;
		    	}
		    	util.sleep(StConst.WAIT_SLEEP_MS ); 
		    	wait_ms += StConst.WAIT_SLEEP_MS;
	    	}
			
			self.join();
			stLog.debug("Loop thread dies -- " + this.getClass());
		} catch (InterruptedException e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "InterruptedException when joining thread."));
		}finally{
			stLog.trace("<<<<");
		}
	}
	
	
	public void run(){
		stLog.debug(">>>> Loop thread begins -- " + this.getClass().toString());
		setState(State.LoopRunning);
		if(loopStart()) 
			while(this.getState() == State.LoopRunning){
				try {
					setLastLoopBeginTime(System.currentTimeMillis());
					loopOnce();
					if(this.isLogLoop) {
						stLog.trace("Loop once -- " + this.getClass().getName());
					}
				} catch (StExpBreak e) {
					setStopFlag();
				} catch (StExpUserError e) {
					stLog.info(util.getExceptionDetails(e, "User Error -- End the loop"));
					setStopFlag();
				} catch (StExpFamily e) {
					// NON-UserError Exception thread loop! Log the exception and continue!
					e.printStackTrace();
					stLog.error(util.getExceptionDetails(e, "S.T. Exception during the loop"));
				}catch(Exception e){
					e.printStackTrace();
					throw new StErrUserError(util.getExceptionDetails(e, "Unexpected Exception during the loop"));
				}
			}
		loopEnd();
		setState(State.LoopFinished);
		stLog.debug("<<<< Loop Thread ends -- " + this.getClass().toString());
	}


	public long getThreadId(){
		return self.getId();
	}
	
	
	/**
	 * 
	 * @param is_log false to disable logging
	 * 
	 * @deprecated use ChuyuObj.setLogLevel()
	 */
	protected void setLogLoop(boolean is_log){
		this.isLogLoop = is_log;
	}

	protected boolean loopStart() { 
		return true;
	}
	
	protected void loopEnd(){ }
	
	protected abstract void loopOnce() throws StExpFamily;

}