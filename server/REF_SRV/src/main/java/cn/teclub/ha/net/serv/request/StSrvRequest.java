package cn.teclub.ha.net.serv.request;

import java.nio.ByteBuffer;

import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.serv.StSrvGlobal;
import cn.teclub.ha.request.StEvtStartRequest;
import cn.teclub.ha.request.StNetPacket;
import cn.teclub.ha.request.StRequest;
import cn.teclub.ha.request.StNetPacket.Command;


/**
 * Server Request
 * 
 * @author mancook
 *
 */
public abstract class StSrvRequest  extends StRequest
{
	protected final static int 		MS_TIMEOUT = 5000;
	protected final StSrvGlobal 	global = StSrvGlobal.getInstance(); 
	protected final StSrvConnection	conn;
	
	protected StSrvRequest(
			final StSrvConnection 	remote_conn,
			final Command 		cmd, 
			final byte 			code, 
			final ByteBuffer 	data, 
			final int 			timeout, 
			final String 		dscp ) 
	{
		super(null, remote_conn.getModelClient(), cmd, code, StNetPacket.Flow.SERVER_TO_CLIENT, data, 
				timeout, 
				dscp != null ? dscp:  ("[SRV-REQ] " + cmd + " to " + remote_conn ) );
		this.conn = remote_conn;
	}
	
	
	protected StSrvRequest(
			final StSrvConnection 	remote_conn,
			final Command 		cmd, 
			final ByteBuffer 	data, 
			final String		dscp )
	{
		this(remote_conn, cmd, StNetPacket.Code.NONE, data, MS_TIMEOUT,  dscp );
	}
	

	@Override
	protected void onTimeout() {
		stLog.error("Server Request " + cmd + " TIMEOUT -- " + dscp);
		stLog.warn("TODO: check if connection is available ???");
		// [2016-11-5] This is a server-request. DO NOT send StSrvReqCheckClient here!
	}
	
	
	public StClientID getRemoteID22(){
		return remote.getClientID();
	}
	
	
	/**
	 * [2016-1-5] trigger event to connection pulse
	 */
	public void startRequest(){
		conn.addNewEvent(new StEvtStartRequest(this));
	}
	
}
