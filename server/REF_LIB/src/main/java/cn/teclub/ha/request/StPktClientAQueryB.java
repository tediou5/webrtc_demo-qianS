package cn.teclub.ha.request;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import cn.teclub.common.ChuyuUtil;
import cn.teclub.ha.lib.StConst;
import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.StClientInfo;



/**
* <h1> client: A queries B's info </h1>
* 
* @author mancook
*
*/
public class StPktClientAQueryB extends StNetPacket 
{
	private static final int MAX_B_COUNT = StConst.MAX_QUERY_B_COUNT;;
	
	public static StNetPacket buildReq (
			StRequestID ss_id, 
			StClientID src_clt,
			ArrayList<StClientID> clt_list  )  
	{
		ChuyuUtil.getInstance().assertTrue(clt_list.size() <= MAX_B_COUNT, "too many B objects");
		ByteBuffer data_buf = ByteBuffer.allocate(2 + clt_list.size() * StClientID.OBJLEN);
		data_buf.putShort((short) clt_list.size());
		for(StClientID e: clt_list){
			data_buf.putLong(e.getId());
		}
		data_buf.rewind();
		return StNetPacket.build(
				Command.ClientAQueryB, 
				Service.REQUEST, Flow.CLIENT_TO_SERVER, 
				Code.NONE, 
				ss_id, 
				src_clt, null,
				data_buf 
				);
	}
	
	////////////////// instance members ////////////////////////////////////////
	
	public StPktClientAQueryB(StNetPacket pkt0){
		super(pkt0);
	}
	
	public StNetPacket buildAlw(ArrayList<StClientInfo> ci_list) {
		return super.buildAlw(StClientInfo.Util.toBuffer(ci_list));
	}
	
	public ArrayList<StClientID> getDataIdListB() {
		util.assertTrue(this.isTypeRequest() && this.isTypeFlowFromClientToSrv(), 
				"CANNOT call this method for current packet type: " + this.getTypeStr());
		return this.dataGetIdListB();
	}
	
	public ArrayList<StClientInfo> getDataClientInfoListB() {
		util.assertTrue(this.isTypeResponseAllow()&& this.isTypeFlowFromSrvToClient(), 
				"CANNOT call this method for current packet type: " + this.getTypeStr());
		return this.dataGetClientInfoListB();
	}
}
