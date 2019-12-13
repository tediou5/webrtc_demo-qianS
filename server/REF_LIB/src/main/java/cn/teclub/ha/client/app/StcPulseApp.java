package cn.teclub.ha.client.app;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.lib.StConst;
import cn.teclub.ha.lib.StEvent;
import cn.teclub.ha.lib.StEventListener;
import cn.teclub.ha.lib.StEventPulse;


final class StcPulseApp extends StEventPulse 
{
	public static final int HB_INIT_MS 		= StConst.CLT_PULSE_PERIOD_MS * 2;
	public static final int HB_PERIOD_MS 	= StConst.CLT_PULSE_PERIOD_MS;

	StcPulseApp() {
		super("App-Pulse", HB_INIT_MS, HB_PERIOD_MS);
	}
}



class StcPulseAppLis extends ChuyuObj implements StEventListener
{
	private int heartBeatCount = 0;


	@Override
	public String getEvtLisName() {
		return "app-lis";
	}


	@Override
	public void handleEvent(StEvent event) {
		if(event instanceof StEvent.HeartBeat){
			if(util.mode(heartBeatCount++, 2*30) == 0){
				stLog.debug("app-pulse is alive");
			}
		}
		
		if(event instanceof StEvent.SystemShutdown){
			stLog.info("app-pulse is shutting down ...");
		}

		if(event instanceof StcAppExec){
			stLog.debug("process app execution: " + event );
			((StcAppExec)event).prc(this);
		}
	}
}
