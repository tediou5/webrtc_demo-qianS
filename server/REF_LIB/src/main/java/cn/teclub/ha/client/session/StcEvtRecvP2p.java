package cn.teclub.ha.client.session;

import cn.teclub.ha.request.StNetPacket;


/**
 * <h1> Session Event: receive a P2P packet from remote client. </h1>
 * 
 * <p> Triggered by:  Recv-Thread
 * <p> Handled by: 	  Core Pulse
 * 
 * <p> e.g. query-time-lapses
 * 
 * @author mancook
 * 
 */
public class StcEvtRecvP2p extends StcEvtPacket 
{
	public StcEvtRecvP2p(StNetPacket pkt) {
		super(pkt);
	}
}// EOF