package cn.teclub.ha.client.rpr;

import java.nio.ByteBuffer;
import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.request.StNetPacket;



/**
 * 
 * @author mancook
 * 
 * @deprecated [2016-10-16] use MESSAGE_TO_SRV with APPLY flag
 */
public class StcReqSrvAddContact 
		extends StcReqSrv
{
    ////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS AND METHODS
	////////////////////////////////////////////////////////////////////////////
	
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	
	private final StClientID 	clientID;
	private final String		clientKey;
	
	
	/**
	 * - Make sure it contains only legal characters and does not overflow the net-packet.
	 * 
	 */
	public StcReqSrvAddContact(final StClientID clt_id, final String clt_key)
	{
		super(StNetPacket.Command.AddContact, 
				StNetPacket.Code.NONE, 
				null,
				5000, 
				"Add Contact '" + clt_id + "'" );
		this.clientID = clt_id;
		this.clientKey = clt_key;
		//util.assertTrue(!sharedVar.hasFriend(clientID));
	}

	
	protected boolean onPreSend(){ 
		final ByteBuffer key_buf = util.stringFunc.toBuffer(clientKey);
		reqData = ByteBuffer.allocate(StClientID.OBJLEN + key_buf.capacity());
		reqData.put(clientID.toBuffer());
		reqData.put(key_buf);
		reqData.rewind();
		return true;
	}
	
	
	@Override
	protected void onTimeout() {
	}

	
	@Override
	protected void onResAllow(byte code, ByteBuffer data) {
		final StClientInfo updated_ci = new StClientInfo(data);
		final StClientInfo f_ci = new StClientInfo(data);
		stLog.trace("Updated Local Client: " + updated_ci.dump() );
		stLog.trace("Added Contact: " + f_ci.dump() );
		sharedVar.setLocal(updated_ci);
		sharedVar.updateRemoteClient(f_ci.getClientID(), f_ci);
		util.assertTrue(updated_ci.hasFriend(clientID));
		//rprObject.sendMessage(StMessageToGui.REMOTE_UPDATE);
		rprObject.sendEventToApp(new StcEvtRpr.InfoRemoteUpdate());
		resResult = f_ci;
		
		stLog.debug(sharedVar.debugRemoteClients());
	}

	
	@Override
	protected void onResDeny(byte code, ByteBuffer data) {
		resResult = null;
	}
}
