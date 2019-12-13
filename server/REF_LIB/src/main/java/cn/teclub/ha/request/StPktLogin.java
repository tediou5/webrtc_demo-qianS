package cn.teclub.ha.request;

import java.nio.ByteBuffer;

import cn.teclub.ha.lib.StCoder;
import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.StClientInfo;



/**
 * <h1>Client: Login packet. </h1>
 * 
 * @author mancook
 * 
 */
public class StPktLogin extends StNetPacket
{
	
	/**
	 * @deprecated StcReqLogin
	 * 
	 * @param ss_id
	 * @param src_clt
	 * @param name
	 * @param passwd
	 * @param public_ip
	 * @return
	 */
	public static StNetPacket buildReq(
			final StRequestID ss_id, 
			final StClientID src_clt, 
			final String name, 
			final String passwd, 
			final String public_ip) {
		StCoder coder  = StCoder.getInstance();
		ByteBuffer data_buf = ByteBuffer.allocate(StCoder.N_ENC_STR_LEN * 3);
		data_buf.put(coder.encString64(name));  	// name max len is checked 
		data_buf.put(coder.encString64(passwd));   	// password max len is checked 
		data_buf.put(coder.encString64(public_ip));
		data_buf.rewind();
		return StNetPacket.build(
				Command.Login, Service.REQUEST, Flow.CLIENT_TO_SERVER, Code.NONE, 
				ss_id, 
				src_clt, null,
				data_buf);
	}
	
	//////////////////instance members ////////////////////////////////////////

	/**
	 * <h2> Construct a login packet from an instance of super-class. </h2>
	 * 
	 * Called in builder method of super-class. 
	 * 
	 * @param pkt0
	 */
	public StPktLogin(StNetPacket pkt0){
		super(pkt0);
	}
	
	public String getDataName() {
		util.assertTrue(this.isTypeRequest() && this.isTypeFlowFromClientToSrv(), 
				"CANNOT call this method for current packet type: " + this.getTypeStr());
		ByteBuffer buffer = this.dataGetBufferBlock(0,  StCoder.N_ENC_STR_LEN);
		return StCoder.getInstance().decString64(buffer);
	}
	
	public String getDataPassword() {
		util.assertTrue(this.isTypeRequest() && this.isTypeFlowFromClientToSrv(), 
					"CANNOT call this method for current packet type: " + this.getTypeStr());
		ByteBuffer buffer = this.dataGetBufferBlock(StCoder.N_ENC_STR_LEN,  StCoder.N_ENC_STR_LEN);
		return StCoder.getInstance().decString64(buffer);
	}
	
	public String getDataPublicIP() {
		util.assertTrue(this.isTypeRequest() && this.isTypeFlowFromClientToSrv(), 
					"CANNOT call this method for current packet type: " + this.getTypeStr());
		ByteBuffer buffer = this.dataGetBufferBlock(StCoder.N_ENC_STR_LEN*2,  StCoder.N_ENC_STR_LEN);
		return StCoder.getInstance().decString64(buffer);
	}
	
	public StClientInfo getDataClientInfo() {
		util.assertTrue(this.isTypeResponseAllow() && this.isTypeFlowFromSrvToClient(), 
				"CANNOT call this method for current packet type: " + this.getTypeStr());
		return this.dataGetClientInfo(0);
	}
}

