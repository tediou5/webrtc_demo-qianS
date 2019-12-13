package cn.teclub.ha.lib;

import java.util.Timer;

import cn.teclub.ha.lib.StEvent.HeartBeat;


/**
 * <h1>Event Handler with Heart Beat. </h1>
 * 
 * <pre> 
 * Known sub-classes:
 * - StEventPulse:     A pulse used by app;
 * - StEventPulsePool: A singleton which manages all StEventPulse instances;
 * 
 * NOTE: As long as this instance is not stopped, heart-beat and event-pump are running.
 * 
 * Update Logs:
 * ~~~~~~~~~~~~
 * [Theodore: 2014-01-14] Created. 
 * 
 * [Theodore: 2016-07-08] Rename: ChuyuEventSubsystem --> StEvtModule 
 * TOOD: process heart beat and check event cost. 
 * 
 * [Theodore: 2016-09-15] 
 * 
 * </pre>
 * 
 * 
 * @author mancook
 *
 */
class StEventHBHandler extends StEventHandler 
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS
	////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////
    // Instance Members
	////////////////////////////////////////////////////////////////////////////
	public final long    msPeriod;
	private Timer 		 heartbeatTimer;


	public StEventHBHandler (
			final String evt_hdl_name, 
			final int delay, 
			final int period,
			final StEventListener hb_lis)
	{
		super(evt_hdl_name, period > 100? period/2 : 50);
		this.msPeriod = period;
		
		stLog.debug("[1] Start heart-beat timer ");
		this.heartbeatTimer = new Timer(true);
		heartbeatTimer.schedule(
					new java.util.TimerTask() {
						@Override
						public void run() {
							// [2016-9-17] a new thread is created for each timer.
							// stLog.info("======== HB Timer Task: "+ HDL_NAME);
							addNewEvent(new StEvent.HeartBeat());
						}
					},
					delay,
					period ); 
		
		
		if(hb_lis != null){
			stLog.debug("[2] Add build-in heart-beat listener! ");
			addListener(hb_lis);
		}
		stLog.debug("[2/2] DONE! Event Pulse Constructed!");
	}
	
	
	/**
	 * Stop heart-beat timer & event handler.
	 */
	public void stop(){
		if(heartbeatTimer == null){
			stLog.debug("HB-Handler has already stopped. Do nothing!");
			return;
		}
		heartbeatTimer.cancel();
		heartbeatTimer = null;
		stLog.debug("Stop HB-Handler     "+ HDL_NAME +" ...");
		super.stop();
		stLog.info ("HB-Handler Stopped: "+ HDL_NAME +" [][]");
	}
	
	
	public String toString(){
		return super.toString() + ", [P]" + msPeriod + "ms"; 
	}
}



/**
 * Event Pulse is registered to Pulse Pool when constructed.
 * It is un-registered from pool, in Pool's heart beat event.
 * 
 * @author mancook
 */
public class StEventPulse extends StEventHBHandler 
{
	public StEventPulse(String evt_hdl_name, int delay, int period) {
		super(evt_hdl_name, delay, period, new StEventPulseLis(period, evt_hdl_name));
		StEventPulsePool.getInstance().register(this);
	}
}


/**
 * <h1> Pulse HB Listener </h1>
 *
 * <pre>
 * This listener only handles HeartBeat event.
 *
 * In Event Pump thread, only HB event is sent to StEventHBLis listener.
 * As a result, this listener will NOT receive any other event, except for HB event.
 * (see: StEventHandler)
 *
 * </pre>
 *
 * @author mancook
 *
 */
class StEventPulseLis extends StEventHBLis 
{
	/**
	 * Min time interval between two ALIVE logs.
	 * 
	 * <pre>
	 * The HB rates of pulses are different. 
	 * Most pulses has a HB rate of 500 ms, e.g. session core pulse, rpr main pulse. 
	 * Some background pulses have a longer rate of 60*1000 ms, e.g. Laberor pulse. 
	 * 
	 * For a fast beating pulse, there is NO need to log alive message in each heart beat. 
	 * Just log every MIN_LOG_PERIOD ms. For a slow pulse, MIN_LOG_PERIOD may by ignored. 
	 * <pre>
	 */
	private static final long MIN_LOG_PERIOD  = 30*1000; //  1*60*1000 -- log heart-beat every 1 min
	
	private final String 	HB_HDL_NAME;
	private final long 		MS_PERIOD;
	private long 			tmLastLog = 0;
	private int 			preGap;
	

	StEventPulseLis(final long period, final String hdl_name){
		this.MS_PERIOD = period;
		this.HB_HDL_NAME = hdl_name;
	}
	
	
	@Override
	public String getEvtLisName() {
		return "HB-Lis[" + HB_HDL_NAME + "]";
	}
	

	@Override
	public void handleEvent(StEvent event) {
		if(event instanceof StEvent.HeartBeat){
			prcHeartBeat((StEvent.HeartBeat)event);
		}
	}


	private void prcHeartBeat(HeartBeat e) {
		// Log an ALIVE message every LOG_PERIOD ms
		final long TS_CURRENT = System.currentTimeMillis();
		if( (TS_CURRENT - tmLastLog) > MIN_LOG_PERIOD){
			//stLog.debug("---- Pulse is ALIVE: {"+ HB_HDL_NAME +"}");
			tmLastLog = TS_CURRENT;
		}
		
		final long TS_GAP = TS_CURRENT - e.TS;
		try{
			if(TS_GAP < MS_PERIOD){
				// previous events are handled in one heart-beat!
				return;
			}
			
			if(TS_GAP < preGap){
				// GAP is going down
				return;
			}
			
			final int frame_count = (int) (TS_GAP / MS_PERIOD);
			final String msg  = "{"+ HB_HDL_NAME +"} Dropped " + frame_count + " Frames! ";
			if( frame_count > StConst.SYS_PULSE_SKIP_FRAME_TOO_MUCH){
				stLog.error(msg);
			}else{
				stLog.warn(msg);
			}
			stLog.warn("Check Previous Events for Details!");
		}
		finally{
			preGap = (int) TS_GAP;
		}
	}
}

