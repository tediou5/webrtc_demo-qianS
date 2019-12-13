package cn.teclub.ha.client.rpr;

import java.nio.ByteBuffer;


import cn.teclub.ha.request.StNetPacket;



/**
 * Request to server.
 * 
 * @author mancook
 *
 */
public abstract class StcReqSrv extends StClientRequest
{
	protected StcReqSrv(
			final StNetPacket.Command cmd, 
			final byte code,
			final ByteBuffer data, 
			final int timeout, 
			final String dscp )
	{
		super(null, cmd, code, StNetPacket.Flow.CLIENT_TO_SERVER, data, timeout, dscp);
	}

	

}


