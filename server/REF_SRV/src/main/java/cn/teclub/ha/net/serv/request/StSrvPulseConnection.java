package cn.teclub.ha.net.serv.request;


import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.lib.StConst;
import cn.teclub.ha.lib.StEvent;
import cn.teclub.ha.lib.StEventPulse;
import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.StMessage;
import cn.teclub.ha.net.serv.StModelClient;
import cn.teclub.ha.request.StNetPacket;



/**
 * Connection Pulse
 * 
 * @author mancook
 *
 */
class StSrvConnPulse extends StEventPulse 
{
	public StSrvConnPulse(String pulse_name){
		super(pulse_name, 200, StConst.SRV_CORE_PULSE_PERIOD_MS);
	}
}




/**
 * Connection Pulse
 * 
 * @author mancook
 *
 */
public class StSrvPulseConnection extends ChuyuObj implements StSrvConnection
{
	private final StSrvConnPulse 	pulse;
	private final StSrvConnLis 		connLis;
	private final String 			CLT_NAME;
	
	/**
	 * Construtor
	 * 
	 */
	public StSrvPulseConnection(String clt_name) {
		this.CLT_NAME = clt_name;
		this.pulse = new StSrvConnPulse("Conn__" + clt_name + "__" + util.getTimeStampMS());
		this.connLis = new StSrvConnLis(null, this);
		pulse.addListener(connLis);
	}
	
	
	public String getCltName(){
		return this.CLT_NAME;
	}
	
	public StModelClient getModelClient(){
		return connLis.getModelClient();
	}
	

	public StClientID getClientID(){
		StModelClient mc_self = getModelClient();
		return mc_self == null ? null : mc_self.getClientID();
	}
	
	
	public long getOfflineMS(){
		return connLis.getOffLineMS();
	}
	
	
	public void addNewEvent(StEvent e){
		pulse.addNewEvent(e);
	}
	
	
	public void addDelayEvent(StEvent e, int delay_ms){
		pulse.addDelayEvent(e, delay_ms);
	}

	
	public String toString(){
		return connLis.getConnStr();
	}


	/**
	 * Send an event to listener, which
	 * loads model-client from DB & updates it to remote client.
	 * @param mc2 
	 * 
	 */
	public void updateClientInfo() {
		pulse.addNewEvent(new StEvtConnUpdateClientInfo(null, CLT_NAME));
	}
	
	
	public void sendPacketSafe(StNetPacket pkt) {
		pulse.addNewEvent(new StEvtConnSend(null, CLT_NAME, pkt));
	}


	public void sendMessage(StMessage msg0) {
		pulse.addNewEvent(new StEvtConnSendMessage(null, CLT_NAME, msg0));
	}
	
	
	public void checkRemote() {
		pulse.addNewEvent(new StEvtConnCheck(null, CLT_NAME));
	}
	
	@Override
	public StringBuffer debug_getStatistics() {
		return connLis.debug_getStatistics();
	}
}




