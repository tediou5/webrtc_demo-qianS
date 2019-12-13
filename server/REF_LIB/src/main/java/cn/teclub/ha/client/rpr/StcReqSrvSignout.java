package cn.teclub.ha.client.rpr;

import java.nio.ByteBuffer;

import cn.teclub.ha.client.StcException.ExpLocalClientOffline;
import cn.teclub.ha.lib.StCoder;
import cn.teclub.ha.lib.StErrUserError;
import cn.teclub.ha.request.StNetPacket;



public class StcReqSrvSignout extends StcReqSrv
{
    ////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS AND METHODS
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Called by Constructor
	 * 
	 * @param name
	 * @param passwd
	 * @return
	 */
	private static ByteBuffer buildData(
			final String name, 
			final String passwd ) 
	{
		StCoder coder  = StCoder.getInstance();
		ByteBuffer data_buf = ByteBuffer.allocate(StCoder.N_ENC_STR_LEN * 2);
		data_buf.put(coder.encString64(name));  	// name max len is checked 
		data_buf.put(coder.encString64(passwd));   	// password max len is checked 
		data_buf.rewind();
		return data_buf;
	}
	
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	
	
	/**
	 * Constructor
	 * 
	 * @param timeout
	 * @throws ExpLocalClientOffline 
	 */
	public StcReqSrvSignout(
			final String name, 
			final String passwd
			) throws ExpLocalClientOffline 
	{
		super(StNetPacket.Command.Signout, 
				StNetPacket.Code.NONE, 
				buildData(name, passwd), 
				3000, 
				"Client '"+ name +"' signs out" );
	}

	
	@Override
	protected void onTimeout() {
	}

	
	@Override
	protected void onResAllow(byte code, ByteBuffer data) {
		stLog.info(util.testMilestoneLog("Client '" + local.getName() + "' Signout Success"));
		sharedVar.setAutoLogin(false);
		sharedVar.setLocal(null, null);
		sharedVar.setStat(StcRprState.OFFLINE);
		rprObject.disconnect();
		stLog.debug("Change RPR State & Close the Socket!");
	}

	
	@Override
	protected void onResDeny(byte code, ByteBuffer data) {
		throw new StErrUserError("TODO:");
	}
}
