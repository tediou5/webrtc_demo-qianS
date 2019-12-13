package cn.teclub.ha.net.serv.request;

import java.util.ArrayList;

import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.serv.StDBObject;
import cn.teclub.ha.net.serv.StModelClient;
import cn.teclub.ha.net.serv.StModelClientHas;
import cn.teclub.ha.request.StNetPacket;



/**
 * 
 * @author mancook
 *
 */
public class StSrvService_SlaveDelContact extends StSrvDbService 
{

	public StSrvService_SlaveDelContact() {
		super(StNetPacket.Command.SlaveDelContact);
	}


	@Override
	protected void onRequest(
			final StSrvConnLis conn_lis, 
			final StDBObject db_obj,
			final StModelClient mc_self, 
			final StNetPacket pkt)
	{
		ArrayList<StClientID> ids = StClientID.fromBuffer(pkt.getDataBuffer());
		util.assertTrue(ids.size() == 2);
		final StClientID slave_id = ids.get(0);
		final StClientID contact_id = ids.get(1);
		StModelClient mc_slave = db_obj.loadClient(slave_id, true);
		
		stLog.info("#### [1] Slave " + mc_slave.getName() + " deletes contact: " + contact_id);
		final StModelClientHas mch = mc_slave.getFriendship(contact_id);
		if(mch == null){
			stLog.error("NO frienship! " + mc_slave + " ~ " + contact_id );
			finishRequest(pkt.buildDny(null));
			return;
		}
		db_obj.deleteRecord(mch);
		
		stLog.info("#### [2] update client-info in both connections...");
		updateFriendClientInfo(slave_id);
		updateFriendClientInfo(contact_id);
		
		stLog.info("Slave " + mc_slave.getName() + " deletes contact: " + contact_id);
		mc_slave = db_obj.loadClient(slave_id, true);
		finishRequest(pkt.buildAlw(mc_slave.toBuffer(true)));
		
		/*
		final StClientID id = pkt.dataGetClientID(0);
		final StModelClient mc = db_obj.loadClient(id, true);
		ArrayList<StModelClient> f_list = db_obj.queryClients(mc.getFriendList().toArray(new StClientID[0]));
		final ByteBuffer data = StClientInfo.Util.toBuffer( StModelClient.toClientInfoList(f_list) );
		finishRequest(pkt.buildAlw(data));;
		*/
	}
}
