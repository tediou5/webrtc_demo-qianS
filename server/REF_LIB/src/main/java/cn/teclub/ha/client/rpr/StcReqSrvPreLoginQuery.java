package cn.teclub.ha.client.rpr;

import java.nio.ByteBuffer;

import cn.teclub.ha.request.StNetPacket;



public class StcReqSrvPreLoginQuery 
		extends StcReqSrv
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
	 * @param code
	 * @param data
	 */
	public StcReqSrvPreLoginQuery(final byte code, final ByteBuffer data) {
		super(StNetPacket.Command.PreLoginQuery, 
				code, data, 5000, 
				"Query Before Login" );
	}

	
	@Override
	protected void onTimeout() {
		stLog.warn("TIMEOUT: " + cmd);
		resResult = null;
	}

	
	@Override
	protected void onResAllow(byte code, ByteBuffer data) {
		stLog.debug("recv allow: " + cmd);
		switch(code){
		case StNetPacket.Code.PreLoginQuery.QUERY_NAME_BY_MAC:
		case StNetPacket.Code.PreLoginQuery.QUERY_NAME_BY_PHONE:
			resResult = util.stringFunc.fromBuffer(data);
			break;
		default: 
			stLog.error("Unexpected Code: " + code);
			resResult = null;
			break;
		}
	}

	
	@Override
	protected void onResDeny(byte code, ByteBuffer data) {
		resResult = null;
	}
}
