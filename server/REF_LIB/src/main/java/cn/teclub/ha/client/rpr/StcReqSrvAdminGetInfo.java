package cn.teclub.ha.client.rpr;

import java.nio.ByteBuffer;

import cn.teclub.ha.request.StNetPacket;


public class StcReqSrvAdminGetInfo extends StcReqSrv
{
    ////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS AND METHODS
	////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	
	
	/**
	 * Constructor
	 * 
	 */
	public StcReqSrvAdminGetInfo(byte code, ByteBuffer data) 
	{
		super(StNetPacket.Command.AdminGetInfo, 
				code,
				data, 
				3000, 
				"Admin gets info from server" );
	}

	
	@Override
	protected void onTimeout() {
	}

	
	@Override
	protected void onResAllow(byte code, ByteBuffer data) {
		stLog.info("#### admin gets " + data.remaining() + " B");
		resResult = util.stringFunc.fromBuffer(data);
	}

	
	@Override
	protected void onResDeny(byte code, ByteBuffer data) {
	}
}
