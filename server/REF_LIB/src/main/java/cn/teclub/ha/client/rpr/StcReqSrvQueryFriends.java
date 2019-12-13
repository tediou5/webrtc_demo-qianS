package cn.teclub.ha.client.rpr;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.request.StNetPacket;



/**
 * 
 * @author mancook
 * 
 */
public class StcReqSrvQueryFriends 
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
	public StcReqSrvQueryFriends(final StClientID clt_id)
	{
		super(StNetPacket.Command.QueryFriends, 
				StNetPacket.Code.NONE, 
				clt_id.toBuffer(),
				5000, 
				"Query Friends of '" + clt_id + "'" );
	}

	
	
	@Override
	protected void onTimeout() {
		resResult = null;
	}

	
	@Override
	protected void onResAllow(byte code, ByteBuffer data) {
		ArrayList<StClientInfo> list = new ArrayList<StClientInfo>();
		StClientInfo[] array = StClientInfo.Util.fromBuffer(data);
		for(StClientInfo ci : array){
			list.add(ci);
		}
		resResult = list;
	}

	
	@Override
	protected void onResDeny(byte code, ByteBuffer data) {
		resResult = null;
	}
}
