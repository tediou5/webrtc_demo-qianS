package cn.teclub.ha.net.serv.request;


import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.net.serv.StDBObject;
import cn.teclub.ha.net.serv.StModelClient;
import cn.teclub.ha.request.StNetPacket;
import cn.teclub.ha.request.StNetPacket.Code;



/**
 * 
 * @author mancook
 * 
 *
 */
public class StSrvService_SlaveManage  extends StSrvDbService
{
	public StSrvService_SlaveManage() {
		super(StNetPacket.Command.SlaveManage);
	}


	@Override
	protected void onRequest(StSrvConnLis conn_lis, StDBObject db_obj,
			StModelClient mc_self, StNetPacket pkt) 
	{
		final StClientInfo ci_slave = pkt.dataGetClientInfo(0);
		
		if(ci_slave == null){
			this.finishRequest(pkt.buildDny(Code.SlaveManage.DENY_INPUT_ERROR));
			return;
		}
		final StModelClient mc_slave = db_obj.loadClient(ci_slave.getClientID());
		if(mc_slave == null){
			this.finishRequest(pkt.buildDny(Code.SlaveManage.DENY_SLAVE_NOT_FOUND));
			return;
		}

		
		switch(pkt.getCode()){
		case StNetPacket.Code.SlaveManage.REQ_EDIT_INFO:
			stLog.debug("currently, only update label, dscp & icon.");
			mc_slave.setLabel(ci_slave.getLabel());
			mc_slave.setDscp(ci_slave.getDscp());
			mc_slave.setIconTS(ci_slave.getIconTS());
			db_obj.updateRecord(mc_slave);
			stLog.info("update slave info: " + mc_slave);
			finishRequest(pkt.buildAlw(mc_slave.toBuffer(false))); // send the new salve info
			break;
			
		default:
			stLog.error("Unknown AdminSlave Code: " + pkt.getCode());
			return;
		}
	}
}
