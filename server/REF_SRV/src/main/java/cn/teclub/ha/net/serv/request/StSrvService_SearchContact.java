package cn.teclub.ha.net.serv.request;

import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.net.serv.StDBObject;
import cn.teclub.ha.net.serv.StModelClient;
import cn.teclub.ha.request.StNetPacket;



/**
 * NOTE: Called in Recv Thread.
 * 
 * @author mancook
 *
 */
public class StSrvService_SearchContact extends StSrvDbService 
{

	public StSrvService_SearchContact() {
		super(StNetPacket.Command.SearchContact);
	}


	@Override
	protected void onRequest(
			final StSrvConnLis conn_lis, 
			final StDBObject db_obj,
			final StModelClient mc_self, 
			final StNetPacket pkt)
	{
		final String si_raw = pkt.dataGetString(0);
		stLog.debug("[1] verify string is legal: '" + si_raw + "'");
		
		final String si = si_raw.trim();
		if(!si.matches("^[-\\w.]+$")){
			stLog.warn("Search String Error: " + util.stringFunc.wrap(si));
			finishRequest(pkt.buildDny(null));
			return;
		}
		
		final StClientInfo ci_contact;
		if(si.matches("^(86-)?1[0-9]{10}$")){
			stLog.debug("[2] Search Client by CellPhone: " + si);
			final String conn_phone = mc_self.getPhone();
			if(conn_phone != null  && conn_phone.equalsIgnoreCase(si)){
				stLog.warn("DO NOT Search Self!");
				finishRequest(pkt.buildDny(null));
				return;
			}
			ci_contact = db_obj.queryModelClientByPhone(si); 
		}else{
			stLog.debug("[2] Search Client by Name: " + si);
			if(mc_self.getName().equalsIgnoreCase(si)){
				stLog.warn("DO NOT Search Self!");
				finishRequest(pkt.buildDny(null));
				return;
			}
			ci_contact = db_obj.queryModelClientByName(si);
		}
		
		if(ci_contact == null){
			stLog.warn("Fail to find client by '" + si + "'");
			finishRequest(pkt.buildDny(null));
			return;
		}
		
		stLog.info("Find Contact: " + ci_contact);
		stLog.debug("[3/3] ALLOW " + cmd );
		finishRequest(pkt.buildAlw(ci_contact.toBuffer(false)) );
	}
}
