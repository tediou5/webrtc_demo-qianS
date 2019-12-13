package cn.teclub.ha.client.rpr;

import java.nio.ByteBuffer;

import cn.teclub.ha.request.StNetPacket;



public class StcReqSrvSmsSignupCode 
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
	public StcReqSrvSmsSignupCode(final String phone)
	{
		super(StNetPacket.Command.SmsVerifyCode, 
				StNetPacket.Code.NONE, 
				util.stringFunc.toBuffer(phone),
				5000, 
				"Apply For Signup Code via. SMS" );
	}

	
	@Override
	protected void onTimeout() {
		stLog.info("TIMEOUT: " + cmd);
	}

	
	@Override
	protected void onResAllow(byte code, ByteBuffer data) {
		stLog.info("server has sent the SMS code!");
		// util.assertTrue(false);
	}

	
	@Override
	protected void onResDeny(byte code, ByteBuffer data) {
		util.assertTrue(false);
	}
}
