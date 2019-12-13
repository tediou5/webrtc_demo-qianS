package cn.teclub.ha.net.serv.request;

import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.StMessage;
import cn.teclub.ha.net.serv.StEvtServer;
import cn.teclub.ha.request.StNetPacket;
import cn.teclub.ha.request.StSocket4Pkt;

public abstract class StEvtConn extends StEvtServer { 
	final StClientID    cltID;
	final String		cltName;
	StEvtConn(final StClientID id, final String name){
		this.cltID = id;
		this.cltName = name;
	}
	
//	StEvtConn(){
//		this(null, null);
//	}
}





class StEvtConnCheck extends StEvtConn {
	StEvtConnCheck(StClientID id, String name) {
		super(id, name);
	} 
}

class StEvtConnUpdateClientInfo extends StEvtConn {

	StEvtConnUpdateClientInfo(StClientID id, String name) {
		super(id, name);
	}
	
}

class StEvtConnLoss extends StEvtConn {
	final StSocket4Pkt sock;
	public StEvtConnLoss(StClientID id, String name, StSocket4Pkt s) {
		super(id, name);
		this.sock = s;
	} 
}



class StEvtConnNewSocket extends StEvtConn {
	final StSocket4Pkt sock;
	final StNetPacket  firstPkt;
	final long tsAccept;
	
	StEvtConnNewSocket(StClientID id, String name, StSocket4Pkt sock, StNetPacket pkt, long ts_start){
		super(id, name);
		this.sock = sock;
		this.firstPkt = pkt;
		this.tsAccept = ts_start;
	}
	
	public String toString(){
		return " {" + getClass().getSimpleName() + "," + firstPkt.getCmd() + "} "; 
	}
}






/**
 * Make connection to send a packet.
 * 
 * @author mancook
 *
 */
class StEvtConnSend extends StEvtConn {
	final StNetPacket 	packet;
	
	StEvtConnSend(StClientID id, String name, final StNetPacket pkt){
		super(id, name);
		this.packet = pkt;
	}
}



class StEvtConnSendMessage extends StEvtConn {
	final StMessage msg;
	StEvtConnSendMessage(StClientID id, String name, final StMessage msg){
		super(id, name);
		this.msg = msg;
	}
}



/**
 * <pre>
 * [Theodore: 2016-11-06] Add socket for the received packet.  
 * Reason: connection changes anytime! 
 * e.g. client may log in from another device, before listener processing this received packet! 
 * 
 * <pre/>
 * 
 * @author mancook
 *
 */
class StEvtConnRecv extends StEvtConn {
	final StNetPacket 	packet;
	final StSocket4Pkt 	sock;
	StEvtConnRecv(StClientID id, String name, final StNetPacket pkt, StSocket4Pkt s){
		super(id, name);
		this.packet = pkt;
		this.sock = s;
	}
}





/**
 * @deprecated
 * 
 * @author mancook
 *
 */
class StEvtConnLogin extends StEvtServer { 
	final StSocket4Pkt sock;
	final String name, passwd;
	StEvtConnLogin(StSocket4Pkt sock, String name, String passwd){
		this.sock = sock;
		this.name = name;
		this.passwd = passwd;
	}
}


/**
 * Close the target connection
 * 
 * <pre>
 * listener does: 
 * - send YOU_LOGOUT or mark OFFLINE flag;
 * - close the socket;
 * - delete from connection manager;
 * 
 * youLogout: 
 * - TRUE: send YOU_LOGOUT packet, and close the socket;
 *   NOTE: DO NOT mark OFFLINE flag, when sending YOU_LOGOUT. 
 *   In this case, the same client logs in from another device. 
 *   
 * - FALSE: mark OFFLINE flag, and close the socket;
 * </pre>
 *                           
 * @author mancook
 * 
 * @deprecated
 *
 */
class StEvtConnClose extends StEvtServer {
	final boolean youLogout;
	StEvtConnClose(boolean you_logout){
		this.youLogout = you_logout;
	}
}


/**
 * TODO: NOT used!
 * @author mancook
 *
 * @deprecated
 */
class StEvtConnRuntimeError extends StEvtServer { }
