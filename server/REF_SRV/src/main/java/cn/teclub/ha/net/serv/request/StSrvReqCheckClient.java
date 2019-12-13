package cn.teclub.ha.net.serv.request;

import java.nio.ByteBuffer;

import cn.teclub.ha.lib.StErrUserError;
import cn.teclub.ha.request.StNetPacket;
import cn.teclub.ha.request.StSocket4Pkt;





/**
 * [Server Request] Server Check Client Status
 * 
 * @author mancook
 *
 */
public class StSrvReqCheckClient extends StSrvRequest 
{
	final StSocket4Pkt sock;
	/**
	 * Constructor
	 * @param remote
	 */
	public StSrvReqCheckClient(final StSrvConnection remote_conn, StSocket4Pkt s) 
	{
		super(remote_conn, StNetPacket.Command.SrvCheckClt,  null, "[SRV] Ping Client: " + remote_conn);
		this.sock = s;
	}

	
	@Override
	protected void onTimeout() {
		stLog.debug("Remote Connection NOT Alive: " + conn);
		conn.addNewEvent(new StEvtConnLoss(null, conn.getCltName(), sock));
	}

	
	@Override
	protected void onResAllow(byte code, ByteBuffer data) {
		stLog.debug("Remote Client is Alive: " + remote);
	}

	
	@Override
	protected void onResDeny(byte code, ByteBuffer data) {
		throw new StErrUserError("Impossible");
	}
}
