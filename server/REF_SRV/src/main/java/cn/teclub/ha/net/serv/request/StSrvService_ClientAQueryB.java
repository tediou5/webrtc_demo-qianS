package cn.teclub.ha.net.serv.request;

import java.util.ArrayList;

import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.serv.StDBObject;
import cn.teclub.ha.net.serv.StModelClient;
import cn.teclub.ha.request.StNetPacket;
import cn.teclub.ha.request.StPktClientAQueryB;


/**
 * 
 * @author mancook
 *
 */
public class StSrvService_ClientAQueryB extends StSrvDbService 
{
	public StSrvService_ClientAQueryB() {
		super(StNetPacket.Command.ClientAQueryB);
	}



	@Override
	protected void onRequest(StSrvConnLis conn_lis, StDBObject db_obj,
			StModelClient mc_self, StNetPacket recv_pkt) 
	{
		final StPktClientAQueryB pkt= new StPktClientAQueryB (recv_pkt);
		final StClientID clt_a_id 	= pkt.getSrcClientId();
		
		// [Theodore: 2016-07-10] DB Access is time-consuming. 
		// As each connection has its own recv-thread, Just access DB here!
		//			for(StClientID id: id_list_b){
		//				StClientInfo ci = clientMgr.getClient( id );
		//				ci_list_b.add(ci);
		//				stLog.trace("Client-A queries B's: " + ci);
		//			}
		//
		final ArrayList<StModelClient> list_b = db_obj.queryClients(pkt.getDataIdListB().toArray(new StClientID[0]));
		
		// DEBUG
		for(StModelClient mc: list_b){
		      stLog.debug("client b: " + mc);
		}
		
		finishRequest(pkt.buildAlw(StModelClient.toClientInfoList(list_b)));
		stLog.debug("Client A ("+ clt_a_id +") queries its friends. ");
	}
}
