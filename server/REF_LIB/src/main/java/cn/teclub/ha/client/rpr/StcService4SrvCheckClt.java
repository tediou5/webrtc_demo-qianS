package cn.teclub.ha.client.rpr;

import cn.teclub.ha.request.StNetPacket;


class StcService4SrvCheckClt extends StcService4Srv 
{
	protected StcService4SrvCheckClt() {
		super(StNetPacket.Command.SrvCheckClt);
	}

	@Override
	protected void onRequest(StNetPacket pkt) {
		sendResponse(pkt.buildAlw(null));
	}
}

