package cn.teclub.ha.client.session;

import cn.teclub.ha.request.StNetPacket;


/**
 * <h1> Session Event: receive a packet from server. </h1>
 * 
 * <p> Triggered by:  Recv-Thread
 * <p> Handled by: 	  Core Pulse
 * 
 */
public class StcEvtRecvFromSrv extends StcEvtPacket
{
	public StcEvtRecvFromSrv(final StNetPacket pkt){
		super(pkt);
	}
}