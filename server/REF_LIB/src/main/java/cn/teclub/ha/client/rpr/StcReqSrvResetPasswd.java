package cn.teclub.ha.client.rpr;

import java.nio.ByteBuffer;

import cn.teclub.ha.lib.StCoder;
import cn.teclub.ha.request.StNetPacket;



public class StcReqSrvResetPasswd 
		extends StcReqSrv
{
    ////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS AND METHODS
	////////////////////////////////////////////////////////////////////////////
	private static ByteBuffer buildData(
			String phone, 
			String passwd, 
			String sms_code
			)   
	{
		final StCoder coder  = StCoder.getInstance();
		final ByteBuffer data_buf = ByteBuffer.allocate(StCoder.N_ENC_STR_LEN * 3);
		data_buf.put(coder.encString64(phone));  	
		data_buf.put(coder.encString64(passwd)); 
		data_buf.put(coder.encString64(sms_code));
		data_buf.rewind();
		return data_buf;
	}
	
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	
	
	
	public StcReqSrvResetPasswd(			
			String phone, 
			String new_passwd,
			String sms_code,
			final int timeout  
			) {
		super(	StNetPacket.Command.ResetPasswd, 
				StNetPacket.Code.NONE, 
				buildData(phone, new_passwd, sms_code), 
				timeout, 
				"Reset password Before Login" );
	}

	
	@Override
	protected void onTimeout() {
		stLog.warn("TIMEOUT: " + cmd);
		resResult = null;
	}

	
	@Override
	protected void onResAllow(byte code, ByteBuffer data) {
		stLog.debug("recv allow: " + cmd);
		rprObject.sendEventToApp(new StcEvtRpr.InfoResetPasswdSuccess());
		// TODO:
	}

	
	@Override
	protected void onResDeny(byte code, ByteBuffer data) {
		rprObject.sendEventToApp(new StcEvtRpr.InfoResetPasswdFail(code));
		resResult = null;
	}
}
