package cn.teclub.ha.net.serv.request;


import java.util.concurrent.ConcurrentHashMap;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.lib.StConst;
import cn.teclub.ha.lib.StEvent;
import cn.teclub.ha.lib.StEventPulse;
import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.StMessage;
import cn.teclub.ha.net.serv.StModelClient;
import cn.teclub.ha.net.serv.StSrvConfig;
import cn.teclub.ha.request.StNetPacket;


class StSrvLightConnGroup extends  ChuyuObj
{
	private static final StSrvConfig 	cfg = StSrvConfig.getInstance();
	private static final int 	MAX_CONN_NUM = cfg.getGrpMaxConn();
	
	private final StEventPulse 	pulse;
	private final ConcurrentHashMap<String, StSrvLightConnection> connMap;
	
	
	StSrvLightConnGroup(String pulse_name){
		this.pulse = new StEventPulse(
				pulse_name, 
				StConst.SRV_CORE_PULSE_PERIOD_MS/2, 
				StConst.SRV_CORE_PULSE_PERIOD_MS);
		this.connMap = new ConcurrentHashMap<String, StSrvLightConnection>();
		stLog.debug("Created Light Conn Group: " + pulse_name);
	}
	
	
	int getConnCount(){
		final int n = connMap.size();
		util.assertTrue(n <= MAX_CONN_NUM);
		return n;
	}
	
	
	/**
	 * Create a light-connection in this group.
	 * 
	 * @param clt_name
	 * @return 
	 * - Newly created light-connection.
	 * - NULL if no space for additional connection
	 */
	StSrvLightConnection putLightConnection( String clt_name ){
		if(connMap.size() > MAX_CONN_NUM){
			return null;
		}
		StSrvLightConnection conn = new StSrvLightConnection(clt_name, pulse);
		connMap.put(clt_name, conn);
		return conn;
	}

	
	StSrvLightConnection removeLightConnection(String clt_name ){
		StSrvLightConnection conn =  connMap.remove(clt_name);
		if(conn == null){
			return null;
		}
		conn.delFromPulse();
		return conn;
	}
	
	
	
	void addNewEvent(StEvent e) {
		pulse.addNewEvent(e);
	}
}




/**
 * Connection Pulse
 * 
 * @author mancook
 *
 */
public class StSrvLightConnection 
		extends ChuyuObj 
		implements StSrvConnection
{
	private final StEventPulse		pulse;
	private final StSrvConnLis 		connLis;
	private final String 			CLT_NAME;
	
	
	/**
	 * Construtor
	 * 
	 */
	StSrvLightConnection(String clt_name, StEventPulse grp_pulse) {
		this.CLT_NAME = clt_name;
		this.pulse = grp_pulse;
		//this.pulse = new StSrvConnPulse("LightConn__" + clt_name + "__" + util.getTimeStampMS());
		this.connLis = new StSrvConnLis(null, this);
		pulse.addListener(connLis);
	}
	
	void delFromPulse(){
		pulse.delListener(connLis);
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
		if(e instanceof StEvent.SystemShutdown){
			stLog.debug("Ingore SystemShutdown in light-connection");
			return;
		}
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




