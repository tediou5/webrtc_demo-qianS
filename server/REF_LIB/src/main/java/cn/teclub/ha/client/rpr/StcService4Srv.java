package cn.teclub.ha.client.rpr;

import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.request.StNetPacket;


/**
 * Client Service: for request from server
 * 
 * @author mancook
 *
 */
abstract class StcService4Srv 
		extends StcService
{
	protected StcService4Srv(final StNetPacket.Command cmd){
		super(cmd);
	}	
	
	
	protected void onRequest(final StClientInfo r_clt, final StNetPacket pkt){
		util.assertTrue(r_clt == null, "[SevReq] Client Not NULL: " + (r_clt == null ? "<null>" : r_clt.toString()) );
		onRequest(pkt);
	}
	
	protected abstract void onRequest(StNetPacket pkt);
}
