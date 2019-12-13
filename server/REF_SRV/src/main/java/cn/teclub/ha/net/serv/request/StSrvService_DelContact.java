package cn.teclub.ha.net.serv.request;

import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.serv.StDBObject;
import cn.teclub.ha.net.serv.StModelClient;
import cn.teclub.ha.net.serv.StModelClientHas;
import cn.teclub.ha.net.serv.StSrvGlobal;
import cn.teclub.ha.request.StNetPacket;


/**
 * 
 * @author mancook
 *
 */
public class StSrvService_DelContact extends StSrvDbService 
{

	public StSrvService_DelContact() {
		super(StNetPacket.Command.DelContact);
	}



	@Override
	protected void onRequest(StSrvConnLis conn_lis, StDBObject db_obj,
			final StModelClient mc_self, final StNetPacket pkt) 
	{
		//final StModelClient cur_client = mc_self;
		final StClientID id1 = mc_self.getClientID();
		final StClientID id2 = pkt.dataGetClientID(0);
		
		global.debugCode(new StSrvGlobal.DebugRoutine() {
			@Override
			public void execute() {
				stLog.info("#### mc_self (before deleting frienship) " + mc_self.dump());
			}
		});
		
		stLog.debug("[1] Client " + mc_self.getName() + " deletes friend: " + id2);
		final StModelClientHas mch = mc_self.getFriendship(id2);
		if(mch == null){
			stLog.error("NO frienship! " + mc_self + " ~ " + id2 );
			finishRequest(pkt.buildDny(null));
			return;
		}
		db_obj.deleteRecord(mch);
		
		stLog.debug("[2] update model-client in both connections...");
		final StModelClient mc1 = db_obj.loadClient(id1, true);
		global.debugCode(new StSrvGlobal.DebugRoutine() {
			@Override
			public void execute() {
				stLog.info("#### mc1 (after deleting frienship) " + mc1.dump());
			}
		});
		
		conn_lis.setModelClient(mc1);
		updateFriendClientInfo(id2);
		
		stLog.debug("[3] ALLOW " + cmd);
		finishRequest(pkt.buildAlw(mc1.toBuffer(true)));
	}
}
