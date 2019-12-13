package cn.teclub.ha.client.rpr;

import java.nio.ByteBuffer;

import cn.teclub.ha.net.StMessage;
import cn.teclub.ha.request.StNetPacket;


public class StcReqSrvMessageToSrv extends StcReqSrv
{
    ////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS AND METHODS
	////////////////////////////////////////////////////////////////////////////
	
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	
	private final StMessage outMsg;
	
	
	/**
	 * Constructor
	 */
	public StcReqSrvMessageToSrv(final StMessage msg) 
	{
		super(StNetPacket.Command.MessageToSrv, 
				StNetPacket.Code.NONE, 
				msg.toBuffer(), 
				3000, 
				"Message to Server" );
		this.outMsg = msg;
	}

	
	@Override
	protected void onTimeout() {
	}

	
	@Override
	protected void onResAllow(byte code, ByteBuffer data) {
		if(outMsg.isFlagData()){
			outMsg.setId(data.getLong());
			rprObject.params.objectMgr.saveMessage(outMsg);
		}
		else if(outMsg.isFlagApply()){
			// do nothing
		}
		else if(outMsg.isFlagApplyApproved()){
			rprObject.params.objectMgr.saveMessage(outMsg);
		}
		else if(outMsg.isFlagApplyRejected()){
			// reject message is sent when deleting the message
			// rprObject.params.objectMgr.saveMessage(outMsg);
		}
		else{
			util.assertTrue(false);
		}

		// [Theodore: 2017-11-09] update GUI on all allowed request???
		rprObject.sendEventToApp(new StcEvtRpr.InfoMessageFromSrv());
		//rprObject.sendMessage(StMessageToGui.MSG_FROM_SRV);
	}

	
	@Override
	protected void onResDeny(byte code, ByteBuffer data) {
	}
}
