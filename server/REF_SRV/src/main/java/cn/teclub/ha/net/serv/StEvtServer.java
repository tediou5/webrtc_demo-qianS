package cn.teclub.ha.net.serv;

import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.StEvtNet;
import cn.teclub.ha.request.StNetPacket;
import cn.teclub.ha.request.StPktClientAQueryB;
import cn.teclub.ha.request.StPktLogin;
import cn.teclub.ha.request.StPktSignup;
import cn.teclub.ha.request.StSocket4Pkt;
import cn.teclub.ha.request.StSocketChannel;


/**
 * @author mancook
 */
public abstract class StEvtServer extends StEvtNet {
	final StClientID 		targetClient; // null if no target client.
	
	public StClientID getTargetClient() {
		return targetClient;
	}

	/**
	 * Constructor
	 */
	StEvtServer(StClientID  target_clt_id ) {
		this.targetClient = target_clt_id;
	}
	
	/**
	 * Constructor
	 */
	public StEvtServer( ) {
		this.targetClient = null;
	}
}


/**
 * <h1> Server Broadcast Event. </h1>
 * 
 * <p> This event will be broadcast to all connection groups, which determine
 * if this event will be processed by its 'targetClient'. 
 * 
 * <p> Target client-ID indicates which client this event is sent to. 
 * 
 * @author mancook
 *
 */
abstract class StEvtServerBroadcast extends StEvtServer {
	public StEvtServerBroadcast(StClientID target_clt_id) {
		super(target_clt_id);
	}
}


/**
 * <h1> Server Network Event: send a packet to remote client. </h1>
 * 
 * @author mancook
 * 
 * @deprecated from old server, which has connection-groups
 *
 */
class StEvtServerSendTo extends StEvtServerBroadcast {
	final StNetPacket 	packet;

	public StNetPacket getPacket(){
		return this.packet;
	}
	
	/**
	 * Constructor. <br/>
	 * @param dst_clt_id
	 * @param pkt
	 */
	public StEvtServerSendTo(StClientID target_clt_id, StNetPacket pkt) {
		super(target_clt_id);
		this.packet = pkt;
	}
} 



/**
 * <p> For both non-ssl and SSL connections.
 * 
 * NOTE: same client may have logged in before! 
 * In this case, delete the previouse connection. 
 * 
 * @author mancook
 *
 */
class StEvtServerClientOnline extends StEvtServer 
{
	final StSocket4Pkt 	sock;
	final StModelClient	mc;
	final StPktLogin	loginPkt;
	
	
	StEvtServerClientOnline(final StSocket4Pkt sock, final StModelClient mc, final StPktLogin login_pkt){
		super(mc.getClientID());
		this.sock = sock;
		this.mc = mc;
		this.loginPkt = login_pkt;
	}
}




class StEvtServerInitCore extends StEvtServer { }



/**
 * <h2> Event: tell system to mark client ONLINE at signup time </h2>
 *  
 * This event is sent by Pre-Processor thread and handled by server's main-listener. 
 * 
 * @author mancook
 *
 * @deprecated SIGNUP and LOGIN should be seperated
 * 
 */
class StEvtServerClientOnlineAtSignup extends StEvtServer {
	private StSocketChannel sockCh;
	private StModelClient	mc;
	private StPktSignup		signupPkt;
	
	public StEvtServerClientOnlineAtSignup(StSocketChannel sock_ch, StModelClient mc, StPktSignup signup_pkt) {
		super(null);
		this.sockCh = sock_ch;
		this.signupPkt = signup_pkt;
		this.mc = mc;
	}
	
	StSocketChannel getSockCh(){
		return this.sockCh;
	}
	public StNetPacket getSignupPkt() {
		return this.signupPkt;
	}
	public StModelClient getMc() {
		return mc;
	}
}




/**
 * <h1> Event: A Queries B's client-info. </h1>
 *
 * Triggered by: conn-group when a A-query-B packet is received;
 * Handled by: server main listener.
 *
 * [Theodore: 2015-03-21] handle this event at once, in client-A's connection group?  
 *
 * [Theodore: 2015-03-11] 
 * NOTE: DO NOT handle this event in B's conn-group listener!  Send this event
 * to system's event-handler!  Reason: Client B maybe OFF-LINE. 
 * 
 * @deprecated [10-20] useless ??
 * 
 * @author mancook
 */
class StEvtServerAQueryB extends StEvtServer {
	StPktClientAQueryB packet;
	public StEvtServerAQueryB( StPktClientAQueryB pkt) {
		super(null);
		this.packet = pkt;
	}
}

/**
 * <h1>Ask server-system to send latest client-info to the ONLINE client.</h1>
 * 
 * <p> Triggered by: when the client-info changes, 
 * 	   e.g. add/delete a friend, change label or dscp, 
 * <p> Handled by: server's main listener
 * 
 * TODO [2016-10-20] useless ??
 * 
 * @author mancook
 * 
 * @deprecated
 */
class StEvtServerUpdateClt22 extends StEvtServer {
	StEvtServerUpdateClt22(StClientID remote_client){
		super(remote_client);
	}
}


/**
 * <h1> Server Network Event: Connection to remote client is Lost. </h1>
 * 
 * <pre>
 * This event occurs when the server connection-group thread fails to read from
 * a socket channel. It triggers this event to inform the main listener to do 
 * cleaning job:
 * - Mark the client status to OFFLINE in DB. 
 * - Remove connection from cache(RAM);
 * - update OFFLINE client's owners;
 * 
 * </pre>
 * 
 * @author mancook
 * 
 * 
 * @deprecated
 *
 */
class StEvtServerClientOffline extends StEvtServer {
	public StEvtServerClientOffline(StClientID clt_id) {
		super(clt_id);
	}
}


/**
 * <h1> Server Network Event: Relay a P2P Packet from Client A to B. </h1>
 * 
 * When P2P packet from client A arrives at server, A's connection group
 * triggers this event to inform B's connection group to send this relayed 
 * P2P packet.  <br/>
 * 
 * @author mancook
 * 
 * @deprecated
 * 
 */
class StEvtServerRelayP2p extends StEvtServerSendTo {
	public StEvtServerRelayP2p(StClientID dst_clt_id, StNetPacket pkt) {
		super(dst_clt_id, pkt);
	}
} // end of class: StEvtServerRelayP2p




/**
 * @deprecated not used!
 * 
 * @author mancook
 *
 */
class StEvtServerClientSignout extends StEvtServer {
	private StNetPacket		signoutPkt;
	public StEvtServerClientSignout(StNetPacket pkt) {
		super(null);
		this.signoutPkt  = pkt;
	}
	public StNetPacket getSignoutPkt() {
		return signoutPkt;
	}
}



/**
 * <h1> Server Network Event: Receives an Packet from Client. </h1>
 * 
 * Used by: 
 * - (Connection Group); 
 * 
 * @author mancook
 * 
 * @deprecated
 * 
 */
class StEvtServerRecvFromClient extends StEvtServer {
	private StNetPacket 	packet;

	public StNetPacket getPacket(){
		return this.packet;
	}

	/**
	 * Constructor. <br/>
	 * @param dst_clt_id
	 * @param pkt
	 */
	public StEvtServerRecvFromClient(StClientID dst_clt_id, StNetPacket pkt) {
		super(dst_clt_id);
		this.packet = pkt;
	}
}


/**
 * @deprecated
 * 
 * @author mancook
 *
 */
class StEvtServerAdminGetInfo  extends StEvtServer {
	private StNetPacket		 pkt;
	public StEvtServerAdminGetInfo(StNetPacket pkt)  {
		super(null);
		this.pkt = pkt;
	}
	StNetPacket getPkt(){
		return this.pkt;
	}
}



/**
 * 
 * <h1> server-system asks all conn-groups to check their connected clients </h1>
 *
 * Triggered by: heart-beat event of server-system 
 * Handled by: conn-group listener.
 * 
 * @author mancook
 * 
 * @deprecated
 *
 */
class StEvtServerCheckClientStatus22 extends StEvtServerBroadcast {
	private StNetPacket 	packet;
	public StNetPacket getPacket(){
		return this.packet;
	}
	public StEvtServerCheckClientStatus22(StNetPacket pkt) {
		super(StClientID.GEN_ID);
		this.packet = pkt;
	}
}



/**
 * <h1> HA server network DEBUG event. </h1>
 * 
 * Sub-class specify the detailed DEBUG action. <br/>
 * 
 * @author mancook
 */
class StEvtServerDebug extends StEvtNet { 
	static class DumpClient extends StEvtServerDebug { }
	static class DumpSummary extends StEvtServerDebug { }
	static class ShowCount extends StEvtServerDebug { }
	
	static class ShowPulsePool 			extends StEvtServerDebug { }
	static class ShowPulsePoolStatus 	extends StEvtServerDebug { }
}

