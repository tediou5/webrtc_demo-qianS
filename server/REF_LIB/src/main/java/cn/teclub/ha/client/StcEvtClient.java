package cn.teclub.ha.client;

import cn.teclub.ha.lib.StEvent;

public abstract class StcEvtClient extends StEvent {
	public StcEvtClient(final String evt_name, final String evt_dscp){
		super(evt_name, evt_dscp);
	}

	/*
	 * [Theodore: 2018-06-12] track event states?  
	 * 
	private boolean triggered = false;
	private boolean processed = false;
	private boolean finished 	= false;
	 */

}


