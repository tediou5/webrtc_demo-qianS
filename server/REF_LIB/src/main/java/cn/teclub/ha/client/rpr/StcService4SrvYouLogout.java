package cn.teclub.ha.client.rpr;

import cn.teclub.ha.request.StNetPacket;


class StcService4SrvYouLogout extends StcService4Srv 
{
	protected StcService4SrvYouLogout() {
		super(StNetPacket.Command.YouLogout);
	}

	@Override
	protected void onRequest(StNetPacket pkt) {
		stLog.warn("YOU_LOGOUT packet is received! Same client has logged in on another device!");
		
		// If State is LOGOUT, check-connection shall NOT auto relogin!
		getRprObject().logout();
		
		// delete user/password. reportOnlineStatus() will not re-login!
		sharedVar.setLocal(null, null);
		
		// TODO: Delete SIP account !!!
		stLog.info("TODO: process this event in android app! ");
		
		sendResponse(null);
	}
}

