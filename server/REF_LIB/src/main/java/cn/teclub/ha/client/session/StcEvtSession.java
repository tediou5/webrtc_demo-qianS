package cn.teclub.ha.client.session;

import cn.teclub.ha.client.StcEvtClient;


/**
 * NOTE: getClasSimple() fails on inner class. 
 * Add event-name for each inner class!
 * 
 * @author mancook
 *
 */
public class StcEvtSession extends StcEvtClient 
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBER
	////////////////////////////////////////////////////////////////////////////	
	

	/**
	 * <h1> Session Event: Tell upper layer that net-state is connected. <h1>
	 * 
	 * <p> Triggered by:  Core Pulse
	 * <p> Handled by: 	  Upper Layer
	 * 
	 * 
	 * @author mancook
	 *
	 */
	public static class InfoConnected extends StcEvtSession  {
		public final String LocalAddr;
		public InfoConnected(String loc_addr){
			super("InfoConnected", null);
			this.LocalAddr = loc_addr;
		}
	};
	
	
	/**
	 * <h1> Session Event: Tell upper layer of connection failure. <h1>
	 * 
	 * <p> Triggered by:  Core Pulse
	 * <p> Handled by: 	  Upper Layer
	 * 
	 * 
	 * @author mancook
	 *
	 */
	public static class InfoConnectFailure extends StcEvtSession  {
		public InfoConnectFailure(){
			super("InfoConnectFailure", null);
		}
	};

	
	/**
	 * <h1> Session Event: Tell upper layer that net-state is disconnected. <h1>
	 * 
	 * <p> Triggered by:  Core Pulse
	 * <p> Handled by: 	  Upper Layer
	 * 
	 * 
	 * @author mancook
	 *
	 */
	public static class InfoDisConnected extends StcEvtSession  {
		public InfoDisConnected() {
			super("InfoDisConnected", null);
		}
	};

	
	////////////////////////////////////////////////////////////////////////////
    // Inner Class
	////////////////////////////////////////////////////////////////////////////
	
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Member
	////////////////////////////////////////////////////////////////////////////

	public StcEvtSession(final String evt_name, final String evt_dscp){
		super(evt_name, evt_dscp);
	}
	
	
	public StcEvtSession() {
		super(null, null);
	}
}


