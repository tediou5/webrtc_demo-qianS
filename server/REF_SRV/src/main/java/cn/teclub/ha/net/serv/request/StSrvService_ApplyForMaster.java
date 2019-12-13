package cn.teclub.ha.net.serv.request;

import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.net.serv.StDBObject;
import cn.teclub.ha.net.serv.StModelClient;
import cn.teclub.ha.request.StNetPacket;
import cn.teclub.ha.request.StNetPacket.Code;


/**
 * 
 * @author mancook
 *
 */
public class StSrvService_ApplyForMaster extends StSrvDbService 
{

	public StSrvService_ApplyForMaster() {
		super(StNetPacket.Command.ApplyForMaster);
	}


	@Override
	protected void onRequest(StSrvConnLis conn_lis, StDBObject db_obj,
			StModelClient mc_self, StNetPacket recv_pkt) 
	{
		final StClientID admin_id = recv_pkt.getSrcClientId();
		final StClientID gw_id = recv_pkt.dataGetClientID(0);
		
		stLog.debug("1. Check if GW has admin");
		StModelClient gw_mc0 = db_obj.loadClient(gw_id, true);
		final StClientID master_id = gw_mc0.getMaster();
		if(master_id != null){
			stLog.warn("Gateway admin-user exists! Deny the request");
			StClientInfo old_admin_ci = db_obj.loadClient(master_id);
			finishRequest(recv_pkt.buildDny(Code.ApplyForAdmin.DENY_ADMIN_EXIST, old_admin_ci.toBuffer(false)));
			return;
		}

		stLog.debug("2. Add Admin Relationship");
		db_obj.addFriendship(admin_id, gw_id, true);
		
		stLog.debug("[3] update model-client in both connections...");
		final StModelClient mc = db_obj.loadClient(admin_id, true);
		//stLog.info("#### [Check] mc_self & mc RFs are " + (mc_self == mc ? "SAME" : "Diff"));
		conn_lis.setModelClient(mc);
		updateFriendClientInfo(gw_id);
		
		stLog.debug("[4] Send ALLOW, with updated client-info");
		finishRequest(recv_pkt.buildAlw(mc.toBuffer(true)));
		stLog.info("[4/4] User(" + mc_self.getName() + " admins GW(" + gw_id +")");
	}
}
