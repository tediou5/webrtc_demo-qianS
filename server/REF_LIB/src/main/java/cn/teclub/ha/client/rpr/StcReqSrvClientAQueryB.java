package cn.teclub.ha.client.rpr;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import cn.teclub.common.ChuyuUtil;
import cn.teclub.ha.client.StcException.ExpLocalClientOffline;
import cn.teclub.ha.lib.StConst;
import cn.teclub.ha.lib.StErrUserError;
import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.request.StNetPacket;
import cn.teclub.ha.request.StPktClientAQueryB;




public class StcReqSrvClientAQueryB extends StcReqSrv
{
    ////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS AND METHODS
	////////////////////////////////////////////////////////////////////////////

	private static final int MAX_B_COUNT = StConst.MAX_QUERY_B_COUNT;

	private static ByteBuffer buildData(final ArrayList<StClientID> clt_list ){
		ChuyuUtil.getInstance().assertTrue(clt_list.size() <= MAX_B_COUNT, "too many B objects");
		ByteBuffer data_buf = ByteBuffer.allocate(2 + clt_list.size() * StClientID.OBJLEN);
		data_buf.putShort((short) clt_list.size());
		for(StClientID e: clt_list){
			data_buf.putLong(e.getId());
		}
		data_buf.rewind();
		return data_buf;
	}
	
	
    
	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	

	public StcReqSrvClientAQueryB(final ArrayList<StClientID> clt_list , final String dscp) 
			throws ExpLocalClientOffline 
	{
		super(StNetPacket.Command.ClientAQueryB, StNetPacket.Code.NONE, buildData(clt_list), 3000, dscp);
	}

	
	@Override
	protected void onTimeout() {
		sharedVar.setStat(StcRprState.UPDATE_FRD);
	}
	

	protected void onResponse(final StNetPacket pkt) {
		sharedVar.setStat(StcRprState.UPDATE_FRD);
		super.onResponse(pkt);
		if(pkt.isTypeResponseAllow()){
			final StPktClientAQueryB pkt_allow = new StPktClientAQueryB(pkt);
			final ArrayList<StClientInfo> list = pkt_allow.getDataClientInfoListB();
			for(final StClientInfo ci: list){
				stLog.debug("Update Client Info of Client B: " + ci);
				sharedVar.updateRemoteClient(ci.getClientID(), ci);
			}
			stLog.info(util.testMilestoneLog("ALLOW " + cmd + " ---- Client B Count: " + list.size() ));
			rprObject.serObj.setCoreVar(sharedVar);
			rprObject.serObj.flush();  	// update the buffered object
			rprObject.sendEventToApp(new StcEvtRpr.InfoRemoteUpdate());
			//rprObject.sendMessage(StMessageToGui.REMOTE_UPDATE);
		}else{
			throw new StErrUserError("Impossible");
		}
	}
	
	
	@Override
	protected void onResAllow(byte code, ByteBuffer data) {
		// do nothing
	}

	@Override
	protected void onResDeny(byte code, ByteBuffer data) {
		throw new StErrUserError("Impossible");
	}
}
