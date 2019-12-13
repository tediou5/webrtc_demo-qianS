package cn.teclub.ha.client.rpr;

import java.util.ArrayList;

import cn.teclub.ha.net.StMessage;
import cn.teclub.ha.net.StObjectMgrInterface;
import cn.teclub.ha.request.StNetPacket;


class StcService4SrvMessageToClt extends StcService4Srv 
{
	protected StcService4SrvMessageToClt() {
		super(StNetPacket.Command.SrvMessageToClt);
	}

	@Override
	protected void onRequest(StNetPacket pkt) {
		ArrayList<StMessage> list = StMessage.fromBuffer(pkt.getDataBuffer());
		for(StMessage m: list){
			stLog.debug("Message from server: " + m);
			final StObjectMgrInterface obj_mgr = getParams().objectMgr;
			obj_mgr.saveMessage(m);
		}

	}
}

