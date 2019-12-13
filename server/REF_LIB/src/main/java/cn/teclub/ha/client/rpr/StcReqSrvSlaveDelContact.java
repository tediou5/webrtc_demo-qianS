package cn.teclub.ha.client.rpr;

import java.nio.ByteBuffer;

import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.request.StNetPacket;



/**
 * 
 * @author mancook
 * 
 */
public class StcReqSrvSlaveDelContact 
		extends StcReqSrv
{
    ////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS AND METHODS
	////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	
	
	/**
	 * 
	 */
	public StcReqSrvSlaveDelContact(final StClientID slave_id, final StClientID contact_id)
	{
		super(StNetPacket.Command.SlaveDelContact, 
				StNetPacket.Code.NONE, 
				StClientID.toBuffer(new StClientID[]{slave_id, contact_id}),
				5000, 
				"[Admin Slave] Del Contact: '" + contact_id + "'" );
	}
	
	
	@Override
	protected void onTimeout() {
		resResult = null;
	}

	
	@Override
	protected void onResAllow(byte code, ByteBuffer data) {
		resResult = new StClientInfo(data);
	}

	
	@Override
	protected void onResDeny(byte code, ByteBuffer data) {
		resResult = null;
	}
}
