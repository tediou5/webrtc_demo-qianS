package cn.teclub.ha.lib;

import cn.teclub.common.ChuyuObj;




/**
 * <h1>Event super class </h1>
 * 
 * <p> all known sub-classes: StEvtNet, 
 * 
 * @author mancook
 *
 */
public abstract class StEvent 
	extends ChuyuObj  
	implements ChuyuObj.DumpAttribute
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	
	private static long curID = 0xEA0000;
	private synchronized static long makeID(){
		return curID++;
	}
	
	
	// --------------------
	// static inner class
	// --------------------
	
	public abstract static class EvtSystem extends StEvent { 
		public EvtSystem(String name, String dscp){
			super(name, dscp);
		}
		
		public EvtSystem(){
		}
	}
	
	public static class DebugEvtSys extends EvtSystem {  }
	
	public static class HeartBeat extends EvtSystem { }
	
	public static class SystemShutdown extends EvtSystem {
		static String name = "EVENT_SYSTEM_SHUTDOWN";
		static String dscp = "Stop Event handler or pool thread.";
		
		public SystemShutdown() {
			super(name, dscp);
		}
	}

	public static class DebugCrashHandler extends EvtSystem {  }
	

	////////////////////////////////////////////////////////////////////////////
    // Inner Classes
	////////////////////////////////////////////////////////////////////////////

	public final long TS = System.currentTimeMillis();
	public final long id;				// Reserved for future [Theodore: 2014-11-13]
	public final String eventName;  	// fixed in each event. set when initializing an event
	public final String eventDscp;  	// detailed description about the event; 
	private final String OBJ_STR;
	
	/**
	 *  runtime message, set by event creator to carry more information about the event
	 */
	private String eventMsg;  
	
	/**
	 * Set before sending this event to a listener
	 */
	private long msProcessStart = 0;
	
	
	////////////////////////////////////////////////////////////////////////////
	// Constructors
	////////////////////////////////////////////////////////////////////////////
	

	public StEvent(final String evt_name, final String evt_dscp) {
		this.id = makeID();	
		if(evt_name == null){
			this.eventName = getClass().getSimpleName();
		}else{
			this.eventName = evt_name ;
		}
		this.eventDscp  = evt_dscp;
		OBJ_STR = 	"[" + eventName + "]0x" + Long.toHexString(id); 
	}
	
	
	/**
	 * <h2>Constructor</h2>
	 * 
	 *  <p> [2015-3-1] used by simple event, which has no specified name and dscp. 
	 */
	public StEvent() {
		this(null, "<NONE>");
	}
	
	
	////////////////////////////////////////////////////////////////////////////
	// Methods
	////////////////////////////////////////////////////////////////////////////
	
	public String getEventMsg() {
		return eventMsg;
	}

	
	public void setEventMsg(String eventMsg) {
		this.eventMsg = eventMsg;
	}
	
	private static final String EVENT_START = ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>";
	private static final String EVENT_END   = "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<[][]";


	/**
	 * call this method in event handler. DO NOT call listener in handler! 
	 * So that, more event info can be tracked. e.g. 'msProcessStart'.
	 * 
	 * @param lis event listener
	 * @return cost in ms
	 */
	synchronized int process(final StEventListener lis){
		msProcessStart = System.currentTimeMillis();
		boolean traceEvent = !(this instanceof HeartBeat);
		if(traceEvent){
			stLog.trace(EVENT_START);
			stLog.trace(getEventName() + " --> [Lis]" + lis.getEvtLisName());
		}
		
		lis.handleEvent(this);
		final long ms_cost = System.currentTimeMillis() - msProcessStart;
		final String msg_evt_cost = getEventName() + " Cost: " + ms_cost + "ms IN [Lis]" + lis.getEvtLisName();

		if(traceEvent){
			stLog.trace(msg_evt_cost);
			stLog.trace(EVENT_END);
		}
		
		if(this instanceof DebugCrashHandler){
			throw new StErrUserError("[Test] Crash Handler!");
		}
		msProcessStart = 0;
		return (int) ms_cost;
	}

	
	/**
	 * <pre>
	 * NOT synchronized! 
	 * 
	 * Cause: EventHandler may checking an processing event, 
	 * whose process() method is executing. 
	 * </pre>
	 * 
	 * @deprecated [2016-11-9] NOT Thread Safe! 
	 * 
	 * @return start time of processing
	 */
	long getProcessStart(){
		return msProcessStart;
	}

	
	public String getEventName() {
		// [2015-3-11] Just use the class name of event, to KISS;
		//    return eventName;
		// 	  return getClass().getName() + "(0x" + util.to16CharHex(id) + ")";
		//
		//return (  eventName + " [" + getClass().getSimpleName() + ", ID:0x" + util.to16CharHex(id) + "]");
		return toString();
	}

	public long getId() {
		return id;
	}

	
	public String toString(){
		return OBJ_STR;
	}
	
	
	@Override
	public void dumpSetup() {
		this.dumpSetTitle(" { " + getClass() + " } ");
		this.dumpAddLine("  ID:          0x" + util.to16CharHex(id) );
		this.dumpAddLine("  Name:        " + eventName );
		this.dumpAddLine("  Description: " + eventDscp );
		this.dumpAddLine("  - Processing:" + (msProcessStart > 0 ? "YES" : "NO") );
		this.dumpAddLine("  - Message:   " + eventMsg );
		this.dumpAddLine("  - Prc Start: " + util.getTimeStamp(msProcessStart));
	}
}
