package cn.teclub.ha.net.serv.request;

import java.nio.ByteBuffer;

import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.serv.StDBObject;
import cn.teclub.ha.net.serv.StModelClient;
import cn.teclub.ha.request.StNetPacket;



/**
 * 
 * @author mancook
 *
 */
public class StSrvService_EditInfo  extends StSrvDbService
{

	public StSrvService_EditInfo() {
		super(StNetPacket.Command.EditInfo);
	}


	@Override
	protected void onRequest(StSrvConnLis conn_lis, StDBObject db_obj,
			StModelClient mc_self, StNetPacket pkt) 
	{
		final ByteBuffer data = pkt.getDataBuffer();
		final StClientID clt_id = pkt.getSrcClientId();
		final StModelClient mc = mc_self;
		
		switch(pkt.getCode()){
		case StNetPacket.Code.EditInfo.REQ_ICON_TS:
			mc.setIconTS(data.getLong());
			break;
			
		case StNetPacket.Code.EditInfo.REQ_LABEL:
			mc.setLabel(util.stringFunc.fromBuffer(data));
			break;
			
		case StNetPacket.Code.EditInfo.REQ_DSCP:
			mc.setDscp(util.stringFunc.fromBuffer(data));
			break;	
			
		case StNetPacket.Code.EditInfo.REQ_CELLPHONE:
			mc.setPhone(util.stringFunc.fromBuffer(data));
			break;
			
		case StNetPacket.Code.EditInfo.REQ_PASSWORD:
			mc.setPasswd(util.stringFunc.fromBuffer(data));
			break;
			
		default:
			stLog.error("Unknown EditInfo Code: " + pkt.getCode());
			return;
		}
		db_obj.updateRecord(mc);
		
		StModelClient mc1 = db_obj.loadClient(clt_id, true);
		stLog.info("#### Check Previous & Current Clients are " + (mc == mc1 ? "SAME" : "Diff"));
		conn_lis.setModelClient(mc1);
		
		finishRequest(pkt.buildAlw(mc.toBuffer(true)));
		// [Theodore: 2016-10-20]
		// TODO: send this modified client-info to its online friends
	}
}
