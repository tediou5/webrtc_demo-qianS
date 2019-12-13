package cn.teclub.ha.client.rpr;

import java.nio.ByteBuffer;

import cn.teclub.ha.client.StcException;
import cn.teclub.ha.client.StcException.ExpLocalClientOffline;
import cn.teclub.ha.lib.StErrUserError;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.request.StNetPacket;


public abstract class StcReqP2p extends StClientRequest
{
	public static final int DEFAULT_TIMEOUT_MS = 5000;
	

	protected StcReqP2p(
			final StClientInfo r_clt,
			final StNetPacket.Command cmd, 
			final byte code,
			final ByteBuffer data, 
			final int timeout, 
			final String dscp)
			throws ExpLocalClientOffline 
	{
		super(r_clt, cmd, code, StNetPacket.Flow.CLIENT_TO_CLIENT, data, 
				timeout, 
				dscp == null ? ("P2P Request " + cmd):dscp );
		if(local == null){
			throw new StcException.ExpLocalClientOffline();
		}
	}
	
	

	protected StcReqP2p(
			final StClientInfo r_clt,
			final StNetPacket.Command cmd, 
			final byte code,
			final ByteBuffer data, 
			final String dscp)
			throws ExpLocalClientOffline 
	{
		this(r_clt, cmd, code, data, DEFAULT_TIMEOUT_MS, dscp);
	}
		
	
	public void send(){
		try {
			super.startRequest();
		} catch (ExpLocalClientOffline e) {
			throw new StErrUserError("Impossible");
		}
	}
}
