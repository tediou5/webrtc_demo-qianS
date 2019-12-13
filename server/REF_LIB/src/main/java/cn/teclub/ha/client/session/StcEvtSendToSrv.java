package cn.teclub.ha.client.session;

import cn.teclub.ha.request.StNetPacket;


class StcEvtPacket extends StcEvtSession {
	public final  StNetPacket 	packet;

	public StcEvtPacket(StNetPacket pkt) {
		this.packet = pkt;
	}
	
	
	public String toString(){
		return super.toString() + " {PKT}" + packet;
	}
	
	
	public void dumpSetup() {
		super.dumpSetup();
		this.dumpAddLine(" ---- ---- ---- ----");
		this.dumpAddObj(packet);
	}
	
}// EOF 



/**
 * <h1> Session Event: Send a packet to server. </h1>
 * 
 * <p> Triggered by:  
 * <p> Handled by: 	  
 * 
 * <p> If the remote client ID is NOT NULL, this is a P2P packet which is relayed by server.
 * 
 * @author mancook
 * 
 */
public class StcEvtSendToSrv extends StcEvtPacket 
{
	public StcEvtSendToSrv(StNetPacket pkt) {
		super(pkt);
	}
}// EOF