package cn.teclub.ha.request;

import cn.teclub.ha.net.StTransAddr;


/**
 * <h1>Client: Login packet. </h1>
 * 
 * @author mancook
 */
public class StPktPublicAddr extends StNetPacket
{
	public static StNetPacket buildReq(){
		return StNetPacket.build(
				Command.PublicAddr, Service.REQUEST, Flow.CLIENT_TO_SERVER, Code.NONE, 
				StRequestID.NULL_ID,
				null, null,
				null);
	}
	
	//////////////////instance members ////////////////////////////////////////
	StPktPublicAddr(StNetPacket pkt0){
		super(pkt0);
	}
	
	
	
	public StTransAddr getDataPublicAddr() {
		util.assertTrue(this.isTypeResponseAllow() && this.isTypeFlowFromSrvToClient(), 
				"CANNOT call this method for current packet type: " + this.getTypeStr());
    	util.assertTrue( this.isFrozen(), "DONOT get data from an un-frozen  packet");
    	this.positionAtData();
    	return new StTransAddr(this.buffer);
	}
}
