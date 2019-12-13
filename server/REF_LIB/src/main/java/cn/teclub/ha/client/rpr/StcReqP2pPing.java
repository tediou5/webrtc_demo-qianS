package cn.teclub.ha.client.rpr;

import java.nio.ByteBuffer;

import cn.teclub.ha.client.StcException.ExpLocalClientOffline;
import cn.teclub.ha.lib.StErrUserError;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.request.StNetPacket;



public class StcReqP2pPing extends StcReqP2p 
{
	protected StcReqP2pPing(
			final StClientInfo r_clt, 
			final int timeout, 
			final String dscp 
		) throws ExpLocalClientOffline 
	{
		this(r_clt,timeout, null, dscp);
	}

	
	public StcReqP2pPing(
			final StClientInfo r_clt, 
			final int timeout, 
			final ByteBuffer data,
			final String dscp
		) throws ExpLocalClientOffline 
	{
		super(r_clt, StNetPacket.Command.P2pPing, StNetPacket.Code.NONE, data, timeout, dscp);
	}
	
	
	@Override
	protected void onTimeout() {
		stLog.warn("Ping client '" + remote + "' TIMEOUT !!");
	}

	@Override
	protected void onResAllow(byte code, ByteBuffer data) {
		stLog.debug("Ping client '" + remote + "' Success !");
	}

	@Override
	protected void onResDeny(byte code, ByteBuffer data) {
		if(code != StNetPacket.Code.DENY_P2P_Service_BUSY){
			throw new StErrUserError("Impossible");
		}
	}
}


class StcServiceP2pPing extends StcServiceP2p
{
	protected StcServiceP2pPing() {
		super(StNetPacket.Command.P2pPing);
	}
	
	@Override
	protected void onRequest(StClientInfo r_clt, StNetPacket pkt) {
		sendResponse(pkt.buildAlw(null));
	}

}
