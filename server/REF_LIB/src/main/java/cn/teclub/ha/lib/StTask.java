package cn.teclub.ha.lib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import cn.teclub.common.ChuyuLongID;
import cn.teclub.common.ChuyuObj;


/**
 * <h1> Task runs as a thread. </h1>
 * 
 * <p> Task (including its sub-class) instance uses event handler in task-manager.
 * By using an existing event handler, event can be sent during the execution 
 * of the task thread.
 * 
 * <p> Usage of a task:
 * <ol>
 *  <li> Create a StTask class;
 *  <li> Implement the abstract methods;
 *  <li> start the task; [DONE]
 * </ol>
 * 
 * <p> NOTE: You do not have to clean the task. Core-listener does it in heart-beat event.
 * <pre> 
 * Example: 
 * StTask task = new StTask("Merge videos in time lapse"){
 *   protected void taskRun() {
 *     mergeTimeLapseVideos(evt.tl);
 *   }
 *   public void onFinish() {
 *     stLog.info("TL-Merge task finishes.");
 *   }
 * };
 * task.start();
 * </pre>
 * 
 * @author mancook
 *
 */
abstract public class StTask extends ChuyuObj implements Runnable, ChuyuObj.DumpAttribute
{
	////////////////////////////////////////////////////////////////////////////
    // Inner Class
	////////////////////////////////////////////////////////////////////////////	
	
	public static class ExpTaskTimeout extends StExpFamily {
		private static final long serialVersionUID = -617864508171820534L;
	}
	
	public static class StTaskID extends ChuyuLongID {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2549644563739525599L;
		public final static StTaskID NULL_ID = new StTaskID(0);
		
		public StTaskID(long id) {
			super(id);
		}
	}

	
	public static class TaskMgr extends ChuyuObj{
	    ////////////////////////////////////////////////////////////////////////////
	    // STATIC MEMBERS AND METHODS
		////////////////////////////////////////////////////////////////////////////
		private static long lastID = new Random().nextLong() % 0xFFFF+ 0xFF;
		private static TaskMgr _ins;
		
		
		/**
		 * 
		 * @param evt_hdl - Currently, NOT used!
		 */
		public static void initialize(final StEventHandler evt_hdl){
			if(_ins != null){
				throw new RuntimeException("DO NOT initialize StTaskMgr, twice!");
			}
			_ins = new TaskMgr(evt_hdl);
		}
		
		
		public static TaskMgr getInstance(){
			if(_ins == null){
				throw new RuntimeException("StTaskMgr must be initialized before using! ");
			}
			return _ins;
		}
		
		
		////////////////////////////////////////////////////////////////////////////
	    // Instance Attributes
		////////////////////////////////////////////////////////////////////////////	
		private final ConcurrentHashMap<StTaskID, StTask>  taskList;
	    
		@SuppressWarnings("unused")
		private final StEventHandler 	evtHdl;
	    
	    private int addCount = 0;
	    private int delCount = 0;
	    
	    
	    /**
	     * Constructor
	     */
	    private TaskMgr(final StEventHandler evt_hdl){
	    	this.taskList = new ConcurrentHashMap<>();
	    	this.evtHdl = evt_hdl;
	    	stLog.info("Task Manager Constructed!");
	    }
	    

	    synchronized void addTask(StTask task){
	    	taskList.put(task.getTaskId(), task);
	    	addCount++;
	    }
	    
	    synchronized void delTask(StTaskID task_id){
	    	StTask task = taskList.remove(task_id);
	    	if(task != null) {
	    		stLog.info("Removed Task: " + task);
	    		delCount++;
	    	}
	    }
	    
	    
	    synchronized StTaskID makeTaskId(){
	    	StTaskID task_id;
	    	while(true){
	    		task_id = new StTaskID(lastID++);
	    		StTask task = this.taskList.get(task_id);
	    		if(task == null){
	    			// current task ID is NOT used! 
	    			break;
	    		}
	    	}
	    	return task_id;
	    }
	    
	    
	    /**
	     * Time Consuming! ONLY for debug!
	     * @return string buffer
	     */
	    public synchronized StringBuffer dumpTasks(){
	    	StringBuffer sbuf = new StringBuffer(512);
	    	int  i = 0;
	    	util.dumpFunc.addDumpHeaderLine(sbuf, " Dump Tasks. Count: " + taskList.size() + " ",  "*" );
	    	for(StTask t: taskList.values()){
	    		sbuf.append("\n\t Task [ ").append(i++).append("]");
	    		sbuf.append(t.dump());
	    	}
	    	util.dumpFunc.addDumpHeaderLine(sbuf, null, "*");
	    	return sbuf;
	    }
	    
	    
	  
		public synchronized StTask getTask(StTaskID id){
	    	if(id == null) {
	    		return null;
	    	}
	    	return this.taskList.get(id);
	    }
	    
	    
	    /**
	     * <p> This method is called in heart-beat event handler of client listener, 
	     * so that finished tasks will be deleted from the list.
	     * 
	     */
	    public synchronized void checkFinished(){
	    	final long MS_START = System.currentTimeMillis();
	    	//stLog.debug("Running Tasks (before checking): " + dumpTasks());
	    	
	    	// stop and delete finished tasks
	    	ArrayList<StTaskID> id_list = new ArrayList<>();
	    	Collection<StTask> values = taskList.values();
	    	for(StTask task: values){
	    		if(task.isFinished()){
	    			task.stop();
	    			id_list.add(task.getTaskId());
	    		}
	    	}
	    	for(final StTaskID id: id_list){
	    		delTask(id);
	    	}
	    	//stLog.debug("Running Tasks (after checking): " + dumpTasks());
	    	
	    	stLog.debug("Cost: " + util.getCostStr(MS_START) 
	    			+ " -- Deleted Count: " + id_list.size() + ", Left Count: " + taskList.size());
	    }
	    
	    
	    private synchronized void stopAll(){
	    	Collection<StTask> values = this.taskList.values();
	    	for(StTask task: values){
	    		task.selfThread.interrupt();
	    	}
	    }
	    
	    
	    public void destroy(){
	    	stLog.debug("Stopping all tasks: " + dumpTasks());
	    	
	    	// adding a task causes runtime error!
	    	_ins = null;
	    	
	    	stopAll();
	    	final int MS_SLEEP = 500;
	    	final int MS_MAX_WAIT = 5000;
	    	for(int i=0; i<MS_MAX_WAIT; i+=MS_SLEEP){
	    		util.sleep(MS_SLEEP);
	    		checkFinished();
	    		if(taskList.size() == 0){
	    			break;
	    		}
	    	}
	    	
	    	if(taskList.size() != 0){
	    		stLog.error("un-stopped tasks: " + dumpTasks());
	    		util.assertTrue(false);
	    	}
	    	
	    	stLog.info("All tasks stopped!");
	    }
	    
	    
	    public String toString(){
			return ("{TaskMgr} Added=" + addCount + ", Del=" + delCount + ", Running=" + taskList.size());
	    }
	}
	
	
	
	/**
	 * <p> Based on org.linphone.core.LinphoneCall.State 
	 * 
	 * @author mancook
	 *
	 */
	public static class State {
		private static final Vector<State>  values = new Vector<>();
		public  static final State TaskInit 		= new State(1, "TaskInit");
		public  static final State TaskRunning 		= new State(2, "TaskRunning");
		public  static final State TaskFinished 	= new State(3, "TaskFinished");
		
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
	
	
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	

	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	

	private final StTaskID	id;
	private final Thread 	selfThread;
	private final String	dscp;

	private   State			state;
	private   long 			startTime, threadCost;

	// set in the implementation of taskRun() in sub-class
	protected int			endCode;
	protected String		endMessage;
	
	

	public StTask(String dscp){
		//super(TaskMgr.getInstance().getEvtHdl());
		this.id = TaskMgr.getInstance().makeTaskId();
		this.selfThread = new Thread(this);
		this.dscp = dscp;
		this.endCode = 0;
		this.endMessage = "Task finishes!";
		this.state = State.TaskInit;
	}
	
	/**
	 * <h2>Wait until the task is FINISHED.</h2>
	 * 
	 * <p> This method does NOT call Thread.join() to stop the thread! 
	 * 
	 * <p> As this method blocks, DO NOT 'synchronized' it !!! 
	 * 
	 * 
	 * @param timeout ms
	 * @throws ExpTaskTimeout timeout occurs
	 */
	public void waitUntilTimeoutOrFinish(final int timeout) throws ExpTaskTimeout {
		if(isFinished()){
			stLog.warn("Task has finished!  -- " + this);
			return;
		}
    	int wait_ms = 0;
    	for(int i=0; ; i++){
	    	if(wait_ms > timeout){
				stLog.error("Task timeout -- " + this.dump() );
				stLog.warn("Waited " + timeout + " ms for this task");
				stLog.warn("Interrupt the task thread.");
				this.selfThread.interrupt();
				throw new ExpTaskTimeout();
			}
	    	if(isFinished()){
	    		stLog.debug("Waited " + i * StConst.WAIT_SLEEP_MS + " ms for task finishing" );
	    		break;
	    	}
	    	util.sleep(StConst.WAIT_SLEEP_MS ); 
	    	wait_ms += StConst.WAIT_SLEEP_MS;
    	}
	}
	
	synchronized public StTaskID getTaskId() {
		return id;
	}

	
	synchronized public State getState(){
		return this.state;
	}
	
	synchronized public int getEndCode(){
		return this.endCode;
	}
	
	synchronized public void start(){
		selfThread.start();
		TaskMgr._ins.addTask(this);
		stLog.debug(">>>>>>>>>>>>>>>> Task starts... \n\t---- " + this );
	}
	
	
	synchronized public boolean isFinished(){
		return (this.state == State.TaskFinished);
	}
	
	synchronized private void setState(State s){
		this.state = s;
	}
	
	
	/**
	 * <h2> Wait for the finished task thread to die. </h2>
	 * 
	 * <p> Used by Task Manager.
	 */
	synchronized private void stop(){
		if(!this.isFinished()){
			throw new StErrUserError("Cannot stop an un-finished task!");
		}
		try {
			selfThread.join();
			stLog.debug("Task thread has died -- " + this);
		} catch (InterruptedException e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "InterruptedException when joining thread."));
		}
	}
	
	
	synchronized public long getCost(){
		if(state == State.TaskRunning){
			return System.currentTimeMillis() - this.startTime;
		}
		if(state == State.TaskFinished){
			return this.threadCost;
		}
		return 0;
	}
	
	
	
    @Override
    public void run() {
    	this.setState(State.TaskRunning);
    	this.startTime = System.currentTimeMillis();

    	try{
    		this.taskRun();
    		this.onFinish();
		}catch(RuntimeException e){
			e.printStackTrace();
			stLog.fatal(util.getExceptionDetails(e, "Runtime Exception in task: " + dscp ));
			throw e;
		}
    	this.threadCost = System.currentTimeMillis() - this.startTime;
    	this.setState(State.TaskFinished);
    	
    	if(getCost() > 3000){
    		stLog.warn("Time-consuming Task (Cost>3s): " + this );
    	}
    	stLog.debug("<<<<<<<<<<<<<<<< Task Thread Ends: " + this );
    }
    
    
	public String toString(){
		return "{" + getClass().getSimpleName() + "}" + id + "," +
				getState() + "," +
				getCost() + "ms" +
				"--" + dscp;
	}
	
	
    
	@Override
	public void dumpSetup() {
		this.dumpSetTitle(" {Task} ");
		this.dumpAddLine("    ID    :" + id );
		this.dumpAddLine("    State :" + state );
		this.dumpAddLine("    Start :" + util.getTimeStamp(startTime));
		this.dumpAddLine("    Cost  :" + getCost() );
		this.dumpAddLine("  Class:" + getClass().toString());  
		this.dumpAddLine("  Description:" + this.dscp);    
	}
    
    
    /**
     * <p> The main job of the task.
     */
    abstract protected void taskRun();
    
    
    /**
     * <p> Used for pre-defined tasks. e.g. FtpSend and FtpRecv tasks. 
     * So that some action can be done when the task is finished.
     * 
     */
    abstract protected void onFinish();
}


