package cn.teclub.ha.request;

import cn.teclub.ha.lib.StEvent;


/**
 * This event is sent to Request Processing Pulse
 * 
 * @author mancook
 *
 */
public class StEvtStartRequest extends StEvent.EvtSystem{
	public final StRequest request;
	public StEvtStartRequest(final StRequest req){
		this.request = req;
	}
}
