package cn.teclub.ha.net.serv.request;

import cn.teclub.ha.net.serv.StDBObject;
import cn.teclub.ha.net.serv.StModelClient;
import cn.teclub.ha.request.StNetPacket;


/**
 * 
 * @author mancook
 *
 */
public class StSrvService_CltStatus extends StSrvService {

	public StSrvService_CltStatus() {
		super(StNetPacket.Command.CltStatus);
	}


	@Override
	protected void onRequest(StSrvConnLis conn_lis, StDBObject db_obj,
			StModelClient mc_self, StNetPacket pkt) {
		stLog.trace("Client Status from: " + conn_lis.getModelClient());
		finishRequest(pkt.buildAlw(null));
		
	}
}
