package cn.teclub.ha.net.serv.request;

import java.util.ArrayList;

import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.serv.StDBObject;
import cn.teclub.ha.net.serv.StModelClient;
import cn.teclub.ha.request.StNetPacket;


/**
 * 
 * @author mancook
 *
 */
public class StSrvService_Signout extends StSrvDbService 
{
	public StSrvService_Signout() {
		super(StNetPacket.Command.Signout);
	}


	@Override
	protected void onRequest(StSrvConnLis conn_lis, StDBObject db_obj,
			StModelClient mc_self, StNetPacket recv_pkt) 
	{
		final StClientID clt_id = mc_self.getClientID();

		stLog.debug("To sign out: " + mc_self );
		util.assertTrue(clt_id.equalWith(recv_pkt.getSrcClientId()));
		
		// [2017-3-17] Currently, password is only verified locally. 
		// For security, server should verify the password, again!
		
		// 1. delete from DB 
		db_obj.deleteClient(clt_id);
		
		
		// 2. Remove self from ONLINE friends' friend-list
		//	  i.e. Send SRV_UPDATE_CLIENT to its ONLINE friends
		final ArrayList<StClientID> list = mc_self.getFriendList();
		for(StClientID f_id : list){
			updateFriendClientInfo(f_id);
		}
		
		// 3. Delete this connection
		//    This call does NOT close the socket!
		//	  It only delete the connection from conn-mgr,
		//		& send a SystemShutdown event for pulse-connection.
		//
		// super class will close socket for SIGNOUT request!
		conn_lis.die();
		
		// 4. send SINGOUT ALLOW
		finishRequest(recv_pkt.buildAlw(null));
		stLog.info( util.testMilestoneLog("[T] Signout Success: " + mc_self ));  
	}
}
