package cn.teclub.ha.net.serv.request;


import cn.teclub.ha.net.serv.StDBObject;
import cn.teclub.ha.net.serv.StModelClient;
import cn.teclub.ha.request.StNetPacket;


/**
 * <pre>
 * do most job in StService_PreLoginSignup. 
 * This service is called if sign-up success. It just marks this client ONLINE.
 * 
 * </pre>
 * @author mancook
 * 
 *
 */
public class StSrvService_Signup extends StSrvDbService 
{
	public StSrvService_Signup() {
		super(StNetPacket.Command.Signup);
	}

	protected void onRequest(
			StSrvConnLis conn_lis, StDBObject db_obj,
			StModelClient mc_self, StNetPacket recv_pkt) 
	{	
		final String clt_name = conn.getCltName();
		stLog.info("continue sign-up in connection: " + clt_name );
		
    	stLog.debug("[4] set connection ONLINE ...");
    	final StModelClient mc0 = db_obj.queryClientByName(clt_name);
    	final StModelClient mc1 = conn_lis.setOnline(mc0, db_obj, sockService);
    	
    	stLog.debug("[5] Send sign-up ALLOW packet");
    	finishRequest(recv_pkt.buildAlw(mc1.toBuffer(true)));
    	stLog.info(util.testMilestoneLog("[5/5] Signup Success: '" + mc1));
	}
	
	
}
