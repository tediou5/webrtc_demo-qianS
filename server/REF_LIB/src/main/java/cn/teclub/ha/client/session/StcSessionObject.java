package cn.teclub.ha.client.session;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.lib.StEventPulse;
import cn.teclub.ha.request.StNetPacket;


/**
 * <h1> Session Object </h1>
 * 
 * API for upper layer (representation layer)
 * 
 * @author mancook
 *
 */
public class StcSessionObject extends ChuyuObj 
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	
	private static final Object lock = new Object();
	private static StcSessionObject _ins;


	////////////////////////////////////////////////////////////////////////////
    // Instance Members
	////////////////////////////////////////////////////////////////////////////	
	
	final StcSessionComp 	ssComp;


	/**
	 * Constructor
	 */
	public StcSessionObject(StEventPulse ep_user){
		synchronized (lock){
			ssComp = new StcSessionComp(ep_user);
			_ins = this;
		}
		stLog.info("Session Object Constructed!");
	}
	
	
	public boolean isConnected(){
		final StcNetState state = ssComp.getNetState();
    	return  state == StcNetState.Connected ;
    }
    
	
    /**
     * @deprecated used?
     */
    public StcNetState getNetState() {
		return ssComp.getNetState();
	}


	public void sendToSrv(final StNetPacket pkt ) {
		ssComp.addNewEvent(new StcEvtSendToSrv(pkt));
	}
	
	
	public void connect(){
		stLog.info("Send Connect Event to Core Pulse ...");
		ssComp.addNewEvent(new StcEvtSessionExecution.Connect());
	}


	/**
     * <pre>
	 * Called by RPR mail pulse.
     * This method sends an event to core pulse.
     * Upon this event, the core pulse disconnects with server.
     * But it does not send InfoDisConnected event to RPR main pulse, as RPR want to disconnect deliberately.
	 *
     * </pre>
     *
	 */
	public void disconnect(){
		stLog.info("Send UserDisconnect Event to Core Pulse ...");
		ssComp.addNewEvent(new StcEvtSessionExecution.UserDisconnect());
	}
	
	
	public void destroy() {
		synchronized (lock) {
			assert  _ins != null;
			ssComp.destroy();
			_ins = null;
		}
	}
}
