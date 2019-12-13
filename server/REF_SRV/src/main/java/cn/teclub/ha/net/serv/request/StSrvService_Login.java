package cn.teclub.ha.net.serv.request;

import java.util.ArrayList;

import cn.teclub.ha.net.StMessage;
import cn.teclub.ha.net.serv.StDBObject;
import cn.teclub.ha.net.serv.StModelClient;
import cn.teclub.ha.request.StNetPacket;
import cn.teclub.ha.request.StPktLogin;
import cn.teclub.ha.request.StNetPacket.Code;
import cn.teclub.ha.request.StNetPacket.Flow;



/**
 * 
 * @author mancook
 *
 */
public class StSrvService_Login extends StSrvDbService 
{
	public StSrvService_Login() {
		super(StNetPacket.Command.Login);
	}


	@Override
	protected void onRequest(
			final StSrvConnLis conn_lis, 
			final StDBObject db_obj,
			final StModelClient mc_self, 
			final StNetPacket recv_pkt)
	{
		final StPktLogin pkt = new StPktLogin(recv_pkt);
    	final String raw_name = pkt.getDataName();
		final String clt_passwd = pkt.getDataPassword();
		final StModelClient mc0; 
		
		if(mc_self == null){
			// [2017-3-17] When this client logs in for the 1st time, mc_self is null!
			if(raw_name.indexOf(":gw:") == 0 && raw_name.length() > 20){
				final String mac = raw_name.substring(4);
				stLog.debug("GW logs in with mac: " + mac);
				mc0 = db_obj.queryModelClientByMacAddr(mac);
			}
			else if(raw_name.indexOf(":ph:") == 0){
				final String phone = raw_name.substring(4);
				stLog.debug("User logs in with cellphone number: " + phone);
				mc0 = db_obj.queryModelClientByPhone(phone);
			}
			else{
				mc0 = db_obj.queryClientByName(raw_name);
			}
			
		}else{
			stLog.debug("ReLogin / Login from another device: " + raw_name);
			mc0 = mc_self;
		}
		
		stLog.debug("[1] Check name & password ...");
		final StNetPacket deny_pkt; 
		
		do{
			if(mc0 == null){
				deny_pkt = pkt.buildDny(Code.Login.DENY_USER_NAME_ERROR , null);
				stLog.warn("Login Failure: " + raw_name +" -- Reason: Name does not exist!");
				break;
			}
			
			if(!mc0.getPasswd().equals(clt_passwd)){
				deny_pkt = pkt.buildDny(Code.Login.DENY_PASSWD_ERROR, null);
				stLog.warn("Login Failure: " + raw_name +" -- Reason: Password Error!");
				break;
			}
			deny_pkt = null;
		}while(false);
		
		if(deny_pkt != null){
			finishRequest(deny_pkt, true);
			return;
		}
		
		stLog.debug("[2] update client-info in DB ...");
		StModelClient mc1 = conn_lis.setOnline(mc0, db_obj, sockService);
		
		stLog.debug("[3] send LOGIN ALLOW ...");
		finishRequest(pkt.buildAlw(mc1.toBuffer(true)));
		
		stLog.debug("[4] send pending APPLY messages...");
		// [2016-10-10] As this request requires NO result, 
		// DO NOT create the request object, which waits for the result. 
		final ArrayList<StMessage> msg_list = db_obj.getMessageApply(mc0.getClientID());
		if(msg_list.size() > 0)  {
			// [2016-11-18]  send message to client in NEXT event. 
			// Because 'finishRequest()' does NOT send at once,
			// and message must be sent after Login, 
			//
			conn.sendPacketSafe(StNetPacket.buildReq(
							StNetPacket.Command.SrvMessageToClt, 
							Flow.SERVER_TO_CLIENT, (byte)0, 
							null, 
							null, mc0.getClientID(), 
							StMessage.toBuffer(msg_list)) 
						);
		}
	}
}
