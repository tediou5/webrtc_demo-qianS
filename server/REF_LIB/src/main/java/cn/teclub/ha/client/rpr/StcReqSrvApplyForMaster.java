package cn.teclub.ha.client.rpr;

import java.nio.ByteBuffer;

import cn.teclub.ha.client.StcException.ExpLocalClientOffline;
import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.request.StNetPacket;



public class StcReqSrvApplyForMaster extends StcReqSrv
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
	 * @param gw_id
	 */
	public StcReqSrvApplyForMaster( final StClientID gw_id ) 
	{
		super(StNetPacket.Command.ApplyForMaster, 
				StNetPacket.Code.NONE, 
				gw_id.toBuffer(), 
				3000, 
				"Apply For Admin" );
	}

	
	@Override
	protected void onTimeout() {
	}

	
	@Override
	protected void onResAllow(byte code, ByteBuffer data) {
		final StClientInfo new_local = new StClientInfo(data);
		sharedVar.setLocal(new_local);
		
		try {
			StcReqSrvClientAQueryB req = new StcReqSrvClientAQueryB(sharedVar.getFriendIDList(), "Refresh Friends"){
				protected void onResAllow(byte code, ByteBuffer data) {
					sharedVar.clearRemoteClients();
				}
			};
			req.startRequest();
		} catch (ExpLocalClientOffline e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "Fail to Refresh Friends"));
		}
		resResult = new_local;
	}

	
	@Override
	protected void onResDeny(byte code, ByteBuffer data) {
	}
}
