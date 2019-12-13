package cn.teclub.ha.client.rpr;

import java.nio.ByteBuffer;

import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.request.StNetPacket;



public class StcReqSrvEditInfo 
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
	public StcReqSrvEditInfo(final byte code, final ByteBuffer data)
	{
		super(StNetPacket.Command.EditInfo, 
				code, 
				//util.stringFunc.toBuffer(search_item),
				data,
				5000, 
				"Edit Local Client-Info" );
	}

	
//	protected boolean onPreSend(){ 
//		reqData = ByteBuffer.allocate(StClientID.OBJLEN + infoData.remaining());
//		reqData.put(local.getClientID().toBuffer());
//		reqData.put(infoData);
//		reqData.rewind();
//		return true;
//	}
	
	
	@Override
	protected void onTimeout() {
	}

	
	@Override
	protected void onResAllow(byte code, ByteBuffer data) {
		final StClientInfo updated_ci = new StClientInfo(data);
		rprObject.sharedVar.setLocal(updated_ci);
		rprObject.sendEventToApp(new StcEvtRpr.InfoStateChange());
		stLog.debug("edit local-info success!");
		//rprObject.sendMessage(StMessageToGui.STATE_CHANGE);
	}

	
	@Override
	protected void onResDeny(byte code, ByteBuffer data) {
		resResult = null;
	}
}
