package cn.teclub.ha.lib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;

import cn.teclub.common.ChuyuObj;



/**
 * <pre>
 * Update: [Theodore: 2014-11-11]
 * 
 * System Event Handler, which is the Event Source in observer pattern. 
 * 
 * Handle an event <==> Call Listener Method
 * 
 * It is the source object's responsibility to handle events, properly.
 * That is, calling methods in all registered listener interface. 
 * 
 * </pre>
 * 
 * @author mancook
 *
 */
public class StEventHandler extends ChuyuObj implements Runnable
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS
	////////////////////////////////////////////////////////////////////////////
	static final int PUMP_SLEEP_MS = 40; //ms
	
	private static class DelayEvent{
		final StEvent 	event;
		final long 		msSend;
		
		public DelayEvent(StEvent e, long ms_send) {
			this.event = e;
			this.msSend = ms_send;
		}
	}
	
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Members
	////////////////////////////////////////////////////////////////////////////
	public final String HDL_NAME;
	public final int 	MAX_EVENT_COST;
	
	private final ConcurrentSkipListSet<DelayEvent>  delaySet 
					= new ConcurrentSkipListSet<>(new Comparator<DelayEvent>(){
						@Override
						public int compare(DelayEvent o1, DelayEvent o2) {
							return (int) (o1.msSend - o2.msSend);
						}
					});
	
	private final ArrayList<StEventListener> 				listenerList = new ArrayList<>();
	private final ConcurrentLinkedQueue<StEventListener> 	addingListenerList = new ConcurrentLinkedQueue<>();
	private final ConcurrentLinkedQueue<StEventListener> 	removingListenerList = new ConcurrentLinkedQueue<>();
	private final ConcurrentLinkedQueue<StEvent> 			pendingQueue = new ConcurrentLinkedQueue<>();
	
	private final Thread 	pumpThread;
	private boolean			running 	= true; 	// unset when pump thread ends
	private boolean 		pumpSleep 	= false;	
	private StEvent 		curEvent 	= null;
	private StEventListener	curLis 		= null;
	
	private final long		MS_CREATED = System.currentTimeMillis();
	private int				eventCount = 0;
	private int				eventNetCount = 0;
	private long			eventCost = 0;
	private long			eventNetCost = 0;
	private final StringBuffer sbufDebug = new StringBuffer(256);
	
	
	
	/**
	 * Constructor
	 * 
	 * @param hdl_name - handler name 
	 * @param max_event_cost - unit: ms
	 */
	public StEventHandler(final String hdl_name, final int max_event_cost) {
		this.HDL_NAME = hdl_name;
		this.MAX_EVENT_COST = max_event_cost;
		this.pumpThread = new Thread(this);
		pumpThread.start();
		util.testMilestoneLog("[EVT-HDL] " + this.HDL_NAME + " -- Thread starts.");
		stLog.debug(" Event Handler Starts! [" + HDL_NAME + "]");
	}


	/**
	 * [Theodore: 2017-11-08] use it carefully.  <br/>
	 * Current Usage: assert in core/sip-pulse when setting variable.
	 */
	public Thread getPumpThread(){
		return pumpThread;
	}

	public void addNewEvent(StEvent e){
		stLog.trace("[Event] " + e + " --> " + toString() );
		pendingQueue.add(e);
		
		// [Theodore: 2016-07-22] process the event at once, 
		// if pump thread is sleeping
		wakePumpSleep();
	}


	/**
	 *
	 * @return count of all handled event
	 */
	public int getCountEvent(){
		return eventCount;
	}


	/**
	 *
	 * @return count of handled event, excluding HeartBeat.
	 */
	public int getCountNetEvent(){
		return eventNetCount;
	}


	/**
	 *
	 * @return count of all pending events
	 */
	public int getCountPendingEvent(){
		return pendingQueue.size();
	}



	public void addNewEvents(StEvent[] events){
		ArrayList<StEvent> list = new ArrayList<>();
		Collections.addAll(list, events);
		pendingQueue.addAll(list);
		
		// [Theodore: 2016-07-22] process the event at once, 
		// if pump thread is sleeping
		wakePumpSleep();
	}
	
	
	public void addDelayEvent(StEvent e, int delay_ms){
		delaySet.add(new DelayEvent(e, delay_ms + System.currentTimeMillis()));
	}
	
	
	public synchronized boolean isPumpRunning(){
		return running;
	}
	
	public synchronized boolean isPumpSleep() {
		return pumpSleep;
	}
	
	void interruptPump(){
		pumpThread.interrupt();
	}
	
	
	/**
	 * Called by another thread to get cost of current event.
	 * 
	 * <pre>
	 * Return current event cost. 
	 * NOTE: current event is still being processed by a listener!
	 * <pre>
	 * 
	 * @param sbuf string buffer
	 * @return cost of current event
	 */
	public int debug_eventCost(StringBuffer sbuf){
		final long ms_cur_cost = getCurrentEventCost();
		
		// [2016-11-9] current event and listener are NOT thread-safe, and may change now!
		// Data from them are just for DEBUG purpose.
		StEvent cur_evt = getCurrentEvent();
		StEventListener cur_lis = getCurrentEventLis();
		
		if(sbuf == null){
			sbuf = new StringBuffer(128);
		}
		util.dumpFunc.addDumpLine(sbuf, "Event Handler: " + this);
		util.dumpFunc.addDumpLine(sbuf, "-- Total: " + 
					util.getCostMillis(MS_CREATED)/1000 + "s, Event:" + 
					eventCost +"ms/" + eventCount + 
				    "," + eventNetCost +"ms/" + eventNetCount);
		
		if(ms_cur_cost < 0){
			util.dumpFunc.addDumpLine(sbuf,"-- <No Current Event>");
		}else{
			util.dumpFunc.addDumpLine(sbuf,
					"-- Current Event: " + cur_evt +
					"(" + ms_cur_cost + "ms) IN " + (cur_lis == null ? "<?>" : cur_lis.getEvtLisName()) );
		}
		
		/*
		final long ms_cur_cost;
		if(cur_evt == null || cur_evt.getProcessStart() == 0){
			ms_cur_cost = 0 ;
			util.dumpFunc.addDumpLine(sbuf,"-- <No Current Event>");
		}else{
			ms_cur_cost = util.getCostMillis(cur_evt.getProcessStart());
			util.dumpFunc.addDumpLine(sbuf,
					"-- Current Event: " + cur_evt +
					"(" + ms_cur_cost + "ms) IN " + (cur_lis == null ? "<?>" : cur_lis.getEvtLisName()) );
		}
		*/
		return (int) ms_cur_cost;
	}
	
	
	/**
	 * Stop Pump Thread
	 */
	public void stop(){
		this.addNewEvent(new StEvent.SystemShutdown());
		
		for(int i=0;; i++){
			try {
				stLog.trace("Wait "+ i +" loops, for thread dying...");
				pumpThread.join();
				break;
			} catch (InterruptedException e) {
				e.printStackTrace();
				stLog.error(util.getExceptionDetails(e, "Interrupted when Join Thread"));
			}
		}
		stLog.trace(util.testMilestoneLog("[" + HDL_NAME + "] Event Handler Stopped!"));
	}
	
	

	public void addListener(StEventListener listener){
		stLog.debug("Adding: " + listener.getEvtLisName() + " --> " + this);
		addingListenerList.add(listener);
		addNewEvent(new StEvent.DebugEvtSys());
	}
	
	

	public void delListener(StEventListener listener){
		if(listener == null){
			return;
		}
		stLog.debug("Removing: " + listener.getEvtLisName() + " <-- " + this);
		removingListenerList.add(listener);
		addNewEvent(new StEvent.DebugEvtSys());
	}
	
	
	
	/**
	 * <h2> Thread Function: inform all event listeners </h2>
	 * 
	 * <p> This thread does the real work of processing a new event. 
	 * However, it does not determine how to deal with an event.
	 * It calls methods in all registered event listeners.
	 * 
	 * 
	 * <p> ATTENTION: DO NOT use 'synchronized'!!! DO NOT lock the event pending queue
	 * while processing an event. Otherwise, other threads can not put new event
	 * into the pending queue. Handling an event may take a long time. Its code 
	 * is in the event listener, implemented later by user. 
	 * 
	 */
	public void run(){
		try{
			stLog.debug("["+ HDL_NAME +"] Pump Thread Start...");
			while(true){
				while(delaySet.size() > 0){
					final long MS_NOW = System.currentTimeMillis();
					if( MS_NOW < delaySet.first().msSend ){
						break;
					}
					DelayEvent de = delaySet.pollFirst();
					pendingQueue.add(de.event);
				}
				final StEvent e = pendingQueue.poll();
	
				processEvent(e);
				
				// if SYSTEM-SHUTDOWN event occurs, end the event pump. 
				//
				// NOTE: Other listeners may do cleaning on this event. You should 
				// end this loop after they finish their job. 
				if(e instanceof StEvent.SystemShutdown ){
					break;
				}
			}
		} catch(Exception e){
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "Unexpected exception in event handler"));
			throw e;
		}finally{
			setPumpRunning(false);
			// [2016-9-17] KEEP AN INFO message when a thread ends.
			stLog.info("Pump Thread End --  "+ this.toString() );
		}
	}
	
	
	public String toString(){
		return "{" + HDL_NAME + "}" + (running ? "RUNNING" : "STOPPED" ) + 
				",MAX:" +  MAX_EVENT_COST + "ms,ID:0x" + Long.toHexString(pumpThread.getId())
				;
	}
	
	
	private long curEventStart  = 0;
	private synchronized void setCurEvent(final StEvent cur_event, final StEventListener cur_lis) {
		this.curEventStart = System.currentTimeMillis();
		this.curEvent = cur_event;
		this.curLis = cur_lis;
	}
	
	private synchronized StEvent getCurrentEvent(){
		return this.curEvent;
	}
	
	
	private synchronized long getCurrentEventCost(){
		if(curEvent == null){
			return -1;
		}
		return (System.currentTimeMillis() - curEventStart);
	}
	
	private synchronized StEventListener getCurrentEventLis(){
		return this.curLis;
	}
	

	/**
	 * Make sure interrupt the sleeping pump! 
	 * 
	 * If the pump thread is running, NEVER interrupt it!
	 * 
	 * @return true if pump thread is sleeping
	 */
	private synchronized boolean wakePumpSleep() {
		if(pumpSleep){
			//stLog.debug("Interrupt Sleep-Pump: " + HDL_NAME + "..." );
			pumpThread.interrupt();
		}
		return pumpSleep;
	}

	
	private synchronized void setPumpSleep(boolean pump_sleep) {
		pumpSleep = pump_sleep;
	}
	
	
	/**
	 * ONLY called in pump-thread!
	 *
	 */
	private synchronized void setPumpRunning(boolean running){
		this.running = running;
	}
	
	
	
	private void updateListenerList(){
		StEventListener lis;
		while((lis = this.addingListenerList.poll()) != null){
			listenerList.add(lis);
			stLog.debug("Added: " + lis.getEvtLisName() + " ====> " + this);
		}
		while((lis = this.removingListenerList.poll()) != null){
			listenerList.remove(lis);
			stLog.debug("Removed: " + lis.getEvtLisName() + " <==== " + this);
		}
	}
	
	/**
	 * [Q] clone an event object for each event handler ????
	 * 
	 * @param e event
	 */
	private void processEvent(final StEvent e){
		if(e == null){
			setPumpSleep(true);
			try {
				//stLog.trace("No Event. Sleep " + PUMP_SLEEP_MS + " ms ...");
				Thread.sleep(PUMP_SLEEP_MS);
			} catch (InterruptedException e1) {
				// [Theodore: 2016-07-26] If Thread.sleep() throws InterruptedException, 
				// the interrupted status of current thread is cleared.
				//
				// stLog.debug("Sleep-Pump Interrupted: " + HDL_NAME);
			}
			setPumpSleep(false); 
			return;
		}

		if(Thread.interrupted()){
			stLog.debug("[EVT-HDL] '"+ HDL_NAME +"' is interrupted");
		}
		
		updateListenerList();
		
		// NOTE: system-debug event is NOT sent to listener!
		if(e instanceof StEvent.DebugEvtSys){
			StringBuffer sbuf = new StringBuffer(512);
			util.dumpFunc.addDumpHeaderLine(sbuf, " Dump Event-Handler '" + HDL_NAME + "' ", "*");
			int i = 0;
			for(StEventListener l: this.listenerList){
				util.dumpFunc.addDumpLine(sbuf, "  Attached Listener["+ i++ +"]: " + l.getEvtLisName());
			}
			util.dumpFunc.addDumpHeaderLine(sbuf, null, "*");
			stLog.debug("Show All Listeners:" + sbuf);
			return;
		}
		
		// Send event to registered listeners. 
		// -- HB-Lis:    ONLY receives HB event;
		// -- Other Lis: receives all,  HB & other events;
		for(StEventListener lis: this.listenerList){
			if(lis instanceof StEventHBLis){
				if(!(e instanceof StEvent.HeartBeat)){
					continue;
				}
			}

			
			// [Theodore: 2016-09-15] DO NOT call listener.handleEvent()! 
			// Call StEvent.process(), so that same event can handed by different handlers, safely! 
			//		lis.handleEvent(e);
			setCurEvent(e, lis);
			final int ms_cost = e.process(lis);
			setCurEvent(null, null);
			
			eventCost += ms_cost;
			if(! (e instanceof StEvent.HeartBeat) ){
				eventNetCost += ms_cost;
			}
			

			// -------------------------------------------------------------------------------------
			if(ms_cost > MAX_EVENT_COST){
				sbufDebug.setLength(0);
				sbufDebug.append("==== Cost: ").append(ms_cost).append("ms, ").append("[BAD PERF] Event ").append(e).append(" --> ").append(lis.getEvtLisName());
				
				if(ms_cost > MAX_EVENT_COST * 100 ){
					sbufDebug.append("\n\t !!!! Serious Problem in Your Application !!!! ");
					sbufDebug.append("\n\t Trouble Event: ").append(e.dump());
					stLog.fatal(sbufDebug.toString());
				}
				else if(ms_cost > MAX_EVENT_COST * 10 ){
					sbufDebug.append("\n\t !! VERY BAD Performance !! ");
					stLog.error(sbufDebug.toString());
				}else{
					stLog.warn(sbufDebug.toString());
				}
				sbufDebug.setLength(0);
			}
			// -------------------------------------------------------------------------------------
		}//for
		eventCount++;
		if(! (e instanceof StEvent.HeartBeat) ){
			eventNetCount++;
		}
	}

}//EOF StEventHandler
