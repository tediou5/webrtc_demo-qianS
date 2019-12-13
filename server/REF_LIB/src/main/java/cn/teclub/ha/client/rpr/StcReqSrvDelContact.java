package cn.teclub.ha.client.rpr;

import java.nio.ByteBuffer;

import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.request.StNetPacket;



public class StcReqSrvDelContact 
		extends StcReqSrv
{
    ////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS AND METHODS
	////////////////////////////////////////////////////////////////////////////
	
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	
	private final StClientID 	clientID;
	
	public StcReqSrvDelContact(final StClientID clt_id)
	{
		super(StNetPacket.Command.DelContact, 
				StNetPacket.Code.NONE, 
				clt_id.toBuffer(),
				5000, 
				"Delete Contact '" + clt_id + "'" );
		this.clientID = clt_id;
		util.assertTrue(sharedVar.hasFriend(clientID));
	}

	
	@Override
	protected void onTimeout() {
	}

	
	@Override
	protected void onResAllow(byte code, ByteBuffer data) {
		final StClientInfo updated_ci = new StClientInfo(data);
		sharedVar.setLocal(updated_ci);
		sharedVar.updateRemoteClient(clientID, null);
		util.assertTrue(!updated_ci.hasFriend(clientID));
		rprObject.sendEventToApp(new StcEvtRpr.InfoRemoteUpdate());
		//rprObject.sendMessage(StMessageToGui.REMOTE_UPDATE);
		resResult = updated_ci;
	}

	
	@Override
	protected void onResDeny(byte code, ByteBuffer data) {
		resResult = null;
	}
}
