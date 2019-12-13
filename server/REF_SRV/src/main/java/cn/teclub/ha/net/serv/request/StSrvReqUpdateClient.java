package cn.teclub.ha.net.serv.request;

import java.nio.ByteBuffer;

import cn.teclub.ha.lib.StErrUserError;
import cn.teclub.ha.request.StNetPacket;



/**
 * <h1> Server Update Remote Client Info </h1>
 * 
 * <pre>
 * This happens when:
 * - Add/Delete a friendship;
 * - A client signs up/out;
 * </pre>
 * 
 * @author mancook
 *
 */
public class StSrvReqUpdateClient extends StSrvRequest 
{

	/**
	 * Constructor
	 * @param remote
	 */
	public StSrvReqUpdateClient(final StSrvConnection remote) 
	{
		super(remote, StNetPacket.Command.SrvUpdateClt, remote.getModelClient().toBuffer(true), "Update Owner Client-Info --> " + remote);
	}

	
	@Override
	protected void onResAllow(byte code, ByteBuffer data) {
	}

	
	@Override
	protected void onResDeny(byte code, ByteBuffer data) {
		throw new StErrUserError("Impossible");
	}
}
