package cn.teclub.ha.client.rpr;

import java.nio.ByteBuffer;

import cn.teclub.ha.request.StNetPacket;


public class StcReqSrvQueryDevInWifi extends StcReqSrv
{
    ////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS AND METHODS
	////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Constructor
	 *
	 */
	public StcReqSrvQueryDevInWifi()
	{
		super(StNetPacket.Command.QueryDevInWifi,
				StNetPacket.Code.NONE, 
				null, 
				3000, 
				"Search for ONLINE device(GW/Monitor) in WIFI" );
	}

	
	@Override
	protected void onTimeout() {
	}

	
	protected void onResponse(final StNetPacket pkt){
		super.onResponse(pkt);
		if(pkt.isTypeResponseAllow()){
			resResult = pkt.dataGetClientInfoListB();
		}
	}
	
	
	@Override
	protected void onResAllow(byte code, ByteBuffer data) {
	}

	
	@Override
	protected void onResDeny(byte code, ByteBuffer data) {
	}
}
