package cn.teclub.ha.client.rpr;

import java.nio.ByteBuffer;

import cn.teclub.ha.client.StcException;
import cn.teclub.ha.lib.StErrUserError;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.request.StNetPacket;

public class StcP2pSimpleRequest extends StClientRequest
{
	public StcP2pSimpleRequest(
			final StClientInfo r_clt,
			final StNetPacket.Command cmd,
			final ByteBuffer data)
	{
		this(r_clt, cmd, StNetPacket.Code.NONE, data, 5000, null);
	}

	public StcP2pSimpleRequest(
			final StClientInfo r_clt,
			final StNetPacket.Command cmd,
			final ByteBuffer data,
			final int timeout)
	{
		this(r_clt, cmd, StNetPacket.Code.NONE, data, timeout, null);
	}


	public StcP2pSimpleRequest(
			final StClientInfo r_clt,
			final StNetPacket.Command cmd,
			final byte code,
			final ByteBuffer data,
			final int timeout,
			final String dscp)
	{
		super(r_clt, cmd, code, StNetPacket.Flow.CLIENT_TO_CLIENT, data,
				timeout,
				dscp == null ? ("Simple P2P Request " + cmd):dscp);
		util.assertNotNull(local);
	}



	@Override
	protected void onTimeout() {
	}

	@Override
	protected void onResAllow(byte code, ByteBuffer data) {

	}

	@Override
	protected void onResDeny(byte code, ByteBuffer data) {

	}


	public void send(){
		try {
			this.startRequest();
		} catch (StcException.ExpLocalClientOffline expLocalClientOffline) {
			throw new StErrUserError("Impossible");
		}
	}
}
