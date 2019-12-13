package cn.teclub.ha.client.rpr;

import java.util.ArrayList;

import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.request.StNetPacket;
import cn.teclub.ha.request.StPktSrvUpdateClientB;


class StcService4SrvUpdateB extends StcService4Srv 
{
	protected StcService4SrvUpdateB() {
		super(StNetPacket.Command.SrvUpdateB);
	}

	@Override
	protected void onRequest(StNetPacket recv_pkt) {
    	final StPktSrvUpdateClientB pkt = new StPktSrvUpdateClientB(recv_pkt);
		ArrayList<StClientInfo> ci_list = pkt.getDataClientInfoListB();
		for(StClientInfo ci : ci_list){
			sharedVar.updateRemoteClient(ci.getClientID(), ci);
			//stLog.debug("Update friend (client B info): " + ci.dumpSimple() );
			stLog.info(util.testMilestoneLog("Update Client-B:" + ci));
		}
		stLog.info(util.testMilestoneLog("[" + StNetPacket.Command.SrvUpdateB + "] Updated-B Count: " + ci_list.size() ));
		
		getRprObject().serObj.setCoreVar(sharedVar);
		getRprObject().serObj.flush();  	// update the buffered object
		
		stLog.debug("Inform App Layer about SrvUpdateB...");
		getRprObject().sendEventToApp(new StcEvtRpr.InfoRemoteUpdate());
		//getRprObject().sendMessage(StMessageToGui.REMOTE_UPDATE);
		
		sendResponse(null);
	}
}

