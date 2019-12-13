package cn.teclub.ha.net.serv.request;

import java.nio.ByteBuffer;

import cn.teclub.ha.lib.StConst;
import cn.teclub.ha.lib.StEventPulsePool;
import cn.teclub.ha.net.serv.StDBObject;
import cn.teclub.ha.net.serv.StModelClient;
import cn.teclub.ha.net.serv.StSrvComp;
import cn.teclub.ha.request.StNetPacket;
import cn.teclub.ha.request.StNetPacket.Code;


/**
 * 
 * @author mancook
 *
 */
public class StSrvService_AdminGetInfo extends StSrvDbService {

	public StSrvService_AdminGetInfo() {
		super(StNetPacket.Command.AdminGetInfo);
	}


	private StringBuffer req_connInfo(String clt_name){
		if(clt_name == null || clt_name.length() < 1){
			return new StringBuffer("ERROR: Client Name is wrong: '" + clt_name +"' !!");
		}
		stLog.debug("Get conn-info of: " + clt_name);
		StringBuffer sbuf = null;
		final StSrvConnection conn = global.connMgr.getConnection(clt_name);
		if(conn  == null) {
			sbuf = new StringBuffer(128);
			sbuf.append("ERROR: No Connection to: " + clt_name + "\n");
		}else{
			sbuf = conn.debug_getStatistics();
			sbuf.append("\n");
		}
		return sbuf;
	}
	
	
	
	

	@Override
	protected void onRequest(StSrvConnLis conn_lis, StDBObject db_obj,
			StModelClient mc_self, StNetPacket pkt) 
	{
		final String SSL_SEVER_NO_GROUP = "[SSL] No client group in SSL server!";
		stLog.info("Process admin-client request ...");
		byte code = pkt.getCode();
		ByteBuffer data = pkt.getDataBuffer();
		StringBuffer sbuf = null;
		
		switch( code ){
		case Code.AdminGetInfo.REQUEST_CONNECTION:
			sbuf = req_connInfo(util.stringFunc.fromBuffer(data));
			break;
			
		case Code.AdminGetInfo.REQUEST_PULSE_POOL:
			sbuf = StEventPulsePool.getInstance().checkAllPulse();
			break;
			
		case Code.AdminGetInfo.REQUEST_SUM_CLIENT:
			sbuf = db_obj.debug_summary(null);
			sbuf.append("\n");
			break;

		case Code.AdminGetInfo.REQUEST_CONN_GRP:
			sbuf = new StringBuffer(SSL_SEVER_NO_GROUP);
			break;
			
		case Code.AdminGetInfo.REQUEST_CACHE_CLIENT:
			//sbuf = clientMgr.debug_getCachedClients(null);
			sbuf = new StringBuffer("<NoCache>");
			break;

		case Code.AdminGetInfo.REQUEST_ONLINE_CLIENT:
			sbuf = db_obj.debug_getOnlineClients(null);
			break;
			
		case Code.AdminGetInfo.REQUEST_COUNT_CLIENT:
			sbuf = StSrvComp.getInstance().debug_showCount(null, db_obj);
			break;
			
		case Code.AdminGetInfo.REQUEST_DUMP_ALL:
			//sbuf = dbObj.dump();
			sbuf = new StringBuffer("<DO NOT Dump All>");
			break;
			
		case Code.AdminGetInfo.REQUEST_DUMP_CLIENT:
			String clt_name = util.stringFunc.fromBuffer(data);
			stLog.info("DEBUG: dump client connection: '" + clt_name + "'");
			sbuf = this.dump();
			break;
			
			
		case Code.AdminGetInfo.REQUEST_VERSION_SRV:
			sbuf = new StringBuffer(128);
			sbuf.append("Server Version: ");
			sbuf.append(StConst.getVersionInfo());
			break;
			
		default:
			stLog.error("unknonw sub-command(code) in ADMIN-GET-INFO packet: " + code);
		}
		if(sbuf != null){
			stLog.info("Return admin-client-info query");
			finishRequest(pkt.buildAlw(code, util.stringFunc.toBuffer(sbuf.toString())));
		}
		
	}
}
