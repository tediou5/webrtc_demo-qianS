package cn.teclub.ha.lib;

import java.util.ArrayList;


/**
 * <h1> Event Handler Pool </h1>
 * 
 * <pre>
 * An app has ONLY ONE Handler Pool instance, which does: 
 * - Keep a reference to each Event Handler;
 * - Check if a handler is alive; 
 * - Shutdown all handlers by System-Shutdown event;
 * 
 * [Deprecated]
 * - Broadcast event to all handler;
 * </pre>
 * 
 * @author mancook
 *
 */
final public class StEventPulsePool 
	extends StEventHBHandler
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS
	////////////////////////////////////////////////////////////////////////////
	public static final int MS_PERIOD = StConst.SYS_PULSE_POOL_PERIOD;  
    
	private static StEventPulsePool _ins = null;
	public static void initialize(){
		util.assertTrue(_ins == null);
		_ins = new StEventPulsePool();
	}
    public static StEventPulsePool getInstance(){
    	util.assertNotNull(_ins);
        return _ins;
    }
    
    
	////////////////////////////////////////////////////////////////////////////
    // Instance Members
	////////////////////////////////////////////////////////////////////////////

	/**
	 * <pre>
	 * In each heart-beat:
	 * - Check if all handler is alive;
	 * 
	 * </pre>
	 * @author mancook
	 *
	 */
	final class HeartBeatLis extends StEventHBLis 
	{
		@Override
		public String getEvtLisName() {
			return "HB_LIS_POOL";
		}
	
		
		@Override
		public void handleEvent(StEvent event) {
			if(! (event instanceof StEvent.HeartBeat)){
				return;
			}
			
			stLog.trace("Check Registered Pulse: " + checkAllPulse());
		}
	}

	
	private final ArrayList<StEventPulse> 	pulseList = new ArrayList<StEventPulse>();
	private String lastStatus;
	
	
	/**
	 * Constructor
	 */
	private StEventPulsePool(){
		super("EVT_HDL_POOL",MS_PERIOD/2, MS_PERIOD, null);
		addListener(new HeartBeatLis());
	}
	
	
	public StringBuffer checkAllPulse(){
		final StringBuffer sbuf = new StringBuffer(512);
		util.dumpFunc.addDumpHeaderLine(sbuf, " Event Pulse Status  ");
		int i = 0;
		for(StEventPulse pulse: copyPulseList()){
			util.dumpFunc.addDumpLine(sbuf, "\n  Pulse [" + i++ + "]");
			final int ms_cur_cost = pulse.debug_eventCost(sbuf);
			
			if(ms_cur_cost <  MAX_EVENT_COST){
			}
			else if(ms_cur_cost >  MAX_EVENT_COST * 200){
				stLog.fatal("\n" + sbuf);
				stLog.fatal("Event Cost >  MAX_EVENT_COST * 200 ---- Interrupt Pump Thread...");
				pulse.interruptPump();
			}
			else if(ms_cur_cost >  MAX_EVENT_COST * 10){
				stLog.error("\n" + sbuf);
				stLog.error("Event Cost >  MAX_EVENT_COST * 10");
			}
			else {
				stLog.warn("\n" + sbuf);
				stLog.warn("Event Cost >  MAX_EVENT_COST");
			}
			
			if(!pulse.isPumpRunning()){
				// make sure HB-timer is stopped!
				pulse.stop();
				unregister(pulse);
			}
		}
		util.dumpFunc.addDumpLine(sbuf, "\n  ==== Time: " + util.getTimeStamp() + ", Count:" + pulseList.size());
		util.dumpFunc.addDumpEndLine(sbuf);
		lastStatus = sbuf.toString();
		return sbuf;
	}
	
	
	@SuppressWarnings("unchecked")
	private synchronized ArrayList<StEventPulse> copyPulseList(){
		return (ArrayList<StEventPulse>) pulseList.clone();
	}
	

	private synchronized void stopAllPulse(){
		stLog.debug("[1.1] Broadcast SystemShutdown Event to all pulses...");
		broadcast(new StEvent.SystemShutdown());
		
		stLog.debug("[1.2] Wait for all stop...");
		stLog.debug("Current Pulse List: " + checkAllPulse());
		boolean has_running = true;
		while(has_running){
			util.sleep(1000);
			has_running = false;
			for(StEventPulse pulse: pulseList){
				if(pulse.isPumpRunning()){
					stLog.warn("Find Running Pulse: " + pulse);
					has_running = true;
					break;
				}
			}//for
		}//while
		stLog.debug("Pulse List After Waiting : " + checkAllPulse());
		stLog.info("All Pulse Stopped! ");
		pulseList.clear();
	}
	
	
	public synchronized void register(final StEventPulse pulse){
		if( pulseList.add(pulse))
			stLog.debug("Registered Pulse: " + pulse.HDL_NAME + " -- Count:" + pulseList.size());
	}
	
	
	public synchronized void unregister(final StEventPulse pulse){
		if(pulseList.remove(pulse))
			stLog.debug("Un-Registered Pulse: " + pulse.HDL_NAME + " -- Count:" + pulseList.size());
	}
	
	
	public synchronized String getLastStatus(){
		return lastStatus;
	}
	
	
	public synchronized int getCount22(){
		return pulseList.size();
	}
	
	
	public StringBuffer debug_getCount(StringBuffer sbuf){
		if(sbuf == null){
			sbuf = new StringBuffer(128);
		}
		
		int run_num=0, stop_num=0, pump_sleep=0; 
		
		ArrayList<StEventPulse> list = copyPulseList();
		for(StEventPulse pulse: list){
			if(pulse.isPumpRunning()){
				run_num++;
				if(pulse.isPumpSleep()) 
					pump_sleep++;
			}else{
				stop_num++;
			}
		}
		
    	util.dumpFunc.addDumpLine(sbuf, 
    				">> {Pulse Pool} Total/Stopped/Running/Sleeping = " +  
    				list.size()  + "/" + stop_num + "/" + run_num + "/" + pump_sleep);
    	return sbuf;
	}
	
	
	/**
	 * @deprecated
	 * 
	 * @param event
	 */
	public synchronized void broadcast(final StEvent event){
		for(StEventHandler hdl: pulseList){
			hdl.addNewEvent(event);
		}
	}
	
	
	/**
	 * <pre>
	 * NOT synchronized! 
	 * Reason: pump thread need to access synchronized method of event-handler, e.g. isPumpSleep().
	 * </pre> 
	 * 
	 * TODO: In a peaceful end, DO NOT stop pulse-pool, just stop the pulses ? 
	 */
	public void stop(){
		util.assertNotNull(_ins);
		stLog.trace("[1] Stop All Pulses...");  
		stopAllPulse();
		
		stLog.trace("[2] Stop Pool...");  
		super.stop();
		
		_ins = null;
		stLog.info("[2/2][" + HDL_NAME + "] Pulse Pool Stopped [][][][] " );
	}
}


