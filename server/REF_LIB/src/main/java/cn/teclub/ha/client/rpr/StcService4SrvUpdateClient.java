package cn.teclub.ha.client.rpr;

import java.nio.ByteBuffer;

import cn.teclub.ha.client.StcException.ExpLocalClientOffline;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.request.StNetPacket;

class StcService4SrvUpdateClient extends StcService4Srv 
{
	protected StcService4SrvUpdateClient() {
		super(StNetPacket.Command.SrvUpdateClt);
	}

	@Override
	protected void onRequest(StNetPacket recv_pkt) {
		final StClientInfo ci = recv_pkt.dataGetClientInfo(0);
		stLog.info(util.testMilestoneLog("Local Client-Info Updated: " + ci.dump()));

		sharedVar.setLocal(ci);
		sendResponse(recv_pkt.buildAlw(null));
		
		try {
			StcReqSrvClientAQueryB req = new StcReqSrvClientAQueryB(
					sharedVar.getFriendIDList(), 
					"Refresh Friends after SrvUpateClt"){
				protected void onResAllow(byte code, ByteBuffer data) {
					stLog.info("Delete Previous Client Info");
					sharedVar.clearRemoteClients();
				}
			};
			req.startRequest();
		} catch (ExpLocalClientOffline e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "Fail to Refresh Friends"));
		}
	}
}

