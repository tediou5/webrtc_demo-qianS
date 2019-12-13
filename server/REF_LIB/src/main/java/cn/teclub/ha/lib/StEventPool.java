package cn.teclub.ha.lib;

import java.util.ArrayList;
import java.util.LinkedList;

import cn.teclub.common.ChuyuObj;


/**
 * Event Pool <br/>
 * 
 * Integrated in multi-thread system. Usage of event-pool: <br/>
 * - register event handlers; <br/>
 * - start pump thread; <br/>
 * 
 * @deprecated
 * This class has Thread Safety Issue!
 * 
 * @author mancook
 *
 */
public class StEventPool extends ChuyuObj implements Runnable
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS
	////////////////////////////////////////////////////////////////////////////
	
    private static StEventPool _Instance = null;
    
    public static StEventPool getInstance(){
        if(null == _Instance ){
            _Instance = new StEventPool();
        }
        return _Instance;
    }
    
    
	////////////////////////////////////////////////////////////////////////////
    // Instance Members
	////////////////////////////////////////////////////////////////////////////
	protected ArrayList<StEventHandler> 	handlerList = new ArrayList<StEventHandler>();
	protected LinkedList<StEvent> 			eventPool = new LinkedList<StEvent>();
	protected Thread pumpThread		 		= null;
	
    /**
     * Constructor: as soon as this event pool object is created, 
     * a new pump thread starts running, sending events in pool to 
     * registered handlers.  
     */
	private StEventPool() {
		this.stLog.info("#### Create Event Pool to transport all events in system-wide ####");
	}

	
	
	/**
     * A new pump thread starts running, sending events in pool to 
     * registered handlers.
     * 
	 */
	public void startPump() throws StExpUserError{
		if(this.pumpThread != null){
			throw new StExpUserError("DO NOT start event pool pump thread, again! ");
		}
		pumpThread = new Thread(this);
		pumpThread.start();		
		util.testMilestoneLog("[EVT-POOL] Thread Starts. ");
	}
	

	public void stopPump(){
		this.eventPool.add(new StEvent.SystemShutdown());
		this.stLog.debug("Wait for thread dying");
		try {
			this.pumpThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.stLog.debug("Thread is dead!");
		util.testMilestoneLog("[EVT-POOL] Thread Starts Ends. ");
	}
	
	
	/**
	 * Add a general event listener <br/>
	 * 
	 * @param listener
	 * @throws StExpUserError 
	 */
	public synchronized void registerHanlder(StEventHandler hdl) 
			throws StExpUserError{
		if(this.pumpThread != null){
			throw new StExpUserError("CANNOT register event handler to a running event pool.");
		}
		this.handlerList.add(hdl);
		this.stLog.info("Register: Add an event handler, successfully! ");
	}
	
//	public synchronized void unregisterHanlder(StEventHandler hdl){
//		boolean ret = this.handlerList.remove(hdl);
//		if(ret == false){
//			this.stLog.warn("Did not find specified event handler! Removing operation has no effect! ");
//		}
//		this.stLog.info("UnRegiester: Remove a handler, successfully! ");
//	}
	

	/**
	 * Only used by private method, which sends events to registered handlers and runs in StEventPool' thread.
	 * As it accesses 'eventPool', which is accessed in other threads, which call 'addEvent' method 
	 * to add a new event into event pool. It must be a __synchronized__ method! 
	 * 
	 * @return
	 */
	private synchronized StEvent getEvent(){
		return this.eventPool.poll();
	}
	
	
	/**
	 * See: comments for getEvent
	 * 
	 * @param event
	 */
	public synchronized void broadcastEvent(StEvent event){
		this.eventPool.add(event);
	}
	
	

	

	/**
	 * Thread Function: send event to all registered handlers
	 *
	 */ 
	public void run(){
		while(true){
			StEvent event = this.getEvent();
			if(event == null){
				 util.sleep(10);
				 continue;
			}
			
			this.stLog.debug("Deliver Event to All Handlers: " + event.getEventName());
			
			for(StEventHandler hdl: this.handlerList){
				hdl.addNewEvent(event);
			}
			
			// if SYSTEM-SHUTDOWN event occurs, end the event pump. 
			//
			// NOTE: Other listeners may do cleaning on this event. You should 
			// end this loop after they finish their job. 
			if(event instanceof StEvent.SystemShutdown ){
				this.stLog.info("[EVT-POOL] Event SystemShutdown is received! Stop Event Pooling Thread");
				break;
			}
		}//while
	}//end of: run
	
}// end of class: StEventPool
