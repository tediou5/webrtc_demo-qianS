package cn.teclub.ha.client.app;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.client.StcParams;
import cn.teclub.ha.lib.StErrUserError;
import cn.teclub.ha.lib.StEvent;
import cn.teclub.ha.lib.StEventListener;
import cn.teclub.ha.lib.StEventPulse;



/**
 * 
 * @author mancook
 * 
 * @deprecated  UnImplemented
 */
public class StcPulseOperation extends StEventPulse 
{
	public static final int HB_INIT_MS 		= 1000;
	public static final int HB_PERIOD_MS 	= 5000;
	
	private static StcPulseOperation _ins = new StcPulseOperation();
	public static StcPulseOperation getInstance(){
		return _ins;
	}
	
	
	////////////////////////////////////////////////////////////////////////////

	private StcPulseOperation() {
		super("App-Pulse", HB_INIT_MS, HB_PERIOD_MS);
	}
}



@SuppressWarnings("unused")
class StcPulseOperationLis extends ChuyuObj implements StEventListener
{
	private final StcParams  	params = StcParams.getInstance();
	private final StcAppComp 	clientApp = StcAppComp.getInstance();
	private int heartBeatCount = 0;
	
	
	@Override
	public String getEvtLisName() {
		return "OPT-Pulse-Lis";
	}

    private void prcHeartBeat(final StEvent.HeartBeat evt) 
    {
    	heartBeatCount++;
    }
    

	
	@SuppressWarnings("deprecation")
	@Override
	public void handleEvent(StEvent event) {
		if(event instanceof StEvent.HeartBeat){
			prcHeartBeat((StEvent.HeartBeat)event);
			return;
		}
		
		if(event instanceof StEvent.SystemShutdown){
			stLog.info("[App] App Pulse is shutting down ...");
			return;
		}
		
		stLog.debug(">>>> " + event.getEventName() );
		if( event instanceof StcEvtOperation ){
			StcEvtOperation evt = (StcEvtOperation)event;
			evt.prc();
		}
		else{
			//stLog.error("UnExpected Event:" + event.getEventName() );
			throw new StErrUserError("Unknown Event: " + event);
		}
	}
}
