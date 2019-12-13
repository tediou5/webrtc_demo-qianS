package cn.teclub.ha.client.rpr;

import java.nio.ByteBuffer;

import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.request.StNetPacket;



public class StcReqSrvSearchContact 
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
	 * @param search_item
	 * - Make sure it contains only legal characters and does not overflow the net-packet.
	 * 
	 */
	public StcReqSrvSearchContact( final String search_item)
	{
		super(StNetPacket.Command.SearchContact, 
				StNetPacket.Code.NONE, 
				util.stringFunc.toBuffer(search_item),
				5000, 
				"Search Contact by '" + search_item + "'" );
	}

	
	@Override
	protected void onTimeout() {
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
