package cn.teclub.ha.request;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import cn.teclub.common.ChuyuUtil;
import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.StClientInfo;



/**
 * <h1> Server: update client B's info </h1>
 * 
 * @author mancook
 *
 */
public class StPktSrvUpdateClientB extends StNetPacket 
{
	public static StNetPacket buildSrvReq( 
			StRequestID ss_id, 
			StClientID dst_clt, 
			ArrayList<StClientInfo> ci_list
			)
	{
		ByteBuffer data_buf = null;
		if(ci_list != null){
			ChuyuUtil.getInstance().assertTrue(ci_list.size() <= MAX_CLIENT_B_COUNT, "too many B objects");
			data_buf = ByteBuffer.allocate(PKT_DATA_MAX_LENGTH);
			data_buf.putShort((short) ci_list.size());
			for(StClientInfo ci: ci_list){
				data_buf.put(ci.toBuffer(false));
			}
		}else{
			data_buf = ByteBuffer.allocate(4);
			data_buf.putShort((short) 0);
		}
		data_buf.limit(data_buf.position());
		data_buf.rewind();
		return StNetPacket.build(
				Command.SrvUpdateB, 
				Service.REQUEST, Flow.SERVER_TO_CLIENT, 
				Code.NONE, 
				null, 
				null, dst_clt,
				data_buf 
				);
	}
	
	
	////////////////// instance members ////////////////////////////////////////
	
	public StPktSrvUpdateClientB(StNetPacket pkt0){
		super(pkt0);
	}
	
	public ArrayList<StClientInfo> getDataClientInfoListB() {
		util.assertTrue(this.isTypeRequest()&& this.isTypeFlowFromSrvToClient(), 
				"CANNOT call this method for current packet type: " + this.getTypeStr());
		return super.dataGetClientInfoListB();
	}
}