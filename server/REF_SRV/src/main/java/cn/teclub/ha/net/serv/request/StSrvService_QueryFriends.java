package cn.teclub.ha.net.serv.request;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.net.serv.StDBObject;
import cn.teclub.ha.net.serv.StModelClient;
import cn.teclub.ha.request.StNetPacket;



/**
 * 
 * @author mancook
 *
 */
public class StSrvService_QueryFriends extends StSrvDbService 
{

	public StSrvService_QueryFriends() {
		super(StNetPacket.Command.QueryFriends);
	}


	@Override
	protected void onRequest(
			final StSrvConnLis conn_lis, 
			final StDBObject db_obj,
			final StModelClient mc_self, 
			final StNetPacket pkt)
	{
		final StClientID id = pkt.dataGetClientID(0);
		final StModelClient mc = db_obj.loadClient(id, true);
		ArrayList<StModelClient> f_list = db_obj.queryClients(mc.getFriendList().toArray(new StClientID[0]));
		final ByteBuffer data = StClientInfo.Util.toBuffer( StModelClient.toClientInfoList(f_list) );
		finishRequest(pkt.buildAlw(data));;
	}
}
