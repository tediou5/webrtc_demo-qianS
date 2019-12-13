package cn.teclub.ha.net.serv.request;

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
public class StSrvService_QueryDevInWifi extends StSrvDbService 
{
	public StSrvService_QueryDevInWifi() {
		super(StNetPacket.Command.QueryDevInWifi);
	}


	@Override
	protected void onRequest(
			final StSrvConnLis conn_lis, 
			final StDBObject db_obj,
			final StModelClient mc_self, 
			final StNetPacket pkt)
	{
		final StClientID id_a = pkt.getSrcClientId();
		util.assertTrue(mc_self.getClientID().equalWith(id_a));
		final String public_ip = mc_self.getPublicIP();
		ArrayList<StModelClient> dev_list = db_obj.queryDevByPublicIp(public_ip);
		
		final StNetPacket ack_pkt;
		if(dev_list.size() < 1 ){
			stLog.warn("NO Online Device (GW/Monitor) in WIFI!");
			ack_pkt = pkt.buildDny(null);
		}else{
			stLog.info("Number of online Device(s) in WIFI: " + dev_list.size());
			ack_pkt = pkt.buildAlw( StClientInfo.Util.toBuffer( StModelClient.toClientInfoList(dev_list)) ); 
		}
		finishRequest(ack_pkt);
	}
}
