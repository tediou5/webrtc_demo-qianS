package cn.teclub.ha.client.session;

import cn.teclub.ha.client.StcEvtClient;
import cn.teclub.ha.lib.StEvent;


public class StcEvtSessionExecution extends StcEvtClient {

	public StcEvtSessionExecution() {
		super("Session Execution", null);
	}


	/**
	 * <h1> Execution Event: Tell core pulse to connect server. <h1>
	 * 
	 * <p> Triggered by:  Session Object
	 * <p> Handled by: 	  Core Pulse
	 * 
	 * @author mancook
	 *
	 */
	public static class Connect extends StcEvtSession{ }

	/**
	 * <h1> Execution Event: Tell core pulse to disconnect server. <h1>
	 * This event is sent to core pulse, when user (RPR main pulse) want to disconnect with server.
     * After disconnecting, core pulse will not sent InfoDisconnect event to its user pulse.
	 * 
	 * <p> Triggered by:  Session Object
	 * <p> Handled by: 	  Core Pulse
	 * 
	 * @author mancook
	 *
	 */
	public static class UserDisconnect extends StcEvtSession{ }

	/**
	 * <h1> Execution Event: Tell session layer of LOGIN SUCCESS. <h1>
	 * 
	 * <p> Triggered by:  Session Object
	 * <p> Handled by: 	  Core Pulse
	 * 
	 * 
	 * @author mancook
	 * 
	 * @deprecated
	 * 
	 */
	public static class LoginSuccess extends StcEvtSession  {}

	
	/**
	 * <h1> Execution Event: Tell session layer of Logout Server  <h1>
	 * 
	 * <p> Triggered by:  Session Object
	 * <p> Handled by: 	  Core Pulse
	 * 
	 * 
	 * @author mancook
	 * 
	 * @deprecated
	 * 
	 */
	public static class Logout extends StEvent { }

}

/**
 * <h1> Session Event: Tell core pulse to disconnect with server. <h1>
 * 
 * <p> Triggered by:  Recv-Thread
 * <p> Handled by: 	  Core Pulse
 * 
 * @author mancook
 *
 */
class StcEvtRecvThreadStop extends StcEvtSession{ }
