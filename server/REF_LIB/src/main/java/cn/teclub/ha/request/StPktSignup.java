package cn.teclub.ha.request;

import java.nio.ByteBuffer;

import cn.teclub.ha.lib.StCoder;
import cn.teclub.ha.lib.StExpUserError;
import cn.teclub.ha.net.StClientInfo;


/**
 * <h1>Client: Signup Packet. </h1>
 * 
 * @author Mancook
 *
 */
public class StPktSignup extends StNetPacket{
	
	/**
	 * 
	 * @param ss_id
	 * @param code
	 * @param name
	 * @param passwd
	 * @param label
	 * @param phone
	 * @param mac_addr
	 * @return
	 * @throws StExpUserError
	 */
	public static StNetPacket buildReq(
			StRequestID ss_id, 
			byte code, 
			String name, 
			String passwd, 
			String label, 
			String phone, 
			String mac_addr
			) throws StExpUserError  
	{
		StCoder coder  = StCoder.getInstance();
		ByteBuffer data_buf = ByteBuffer.allocate(StCoder.N_ENC_STR_LEN * 5);
		data_buf.put(coder.encString64(name));  	
		data_buf.put(coder.encString64(passwd)); 
		data_buf.put(coder.encString64(  (label == null    ? "":label  )));
		data_buf.put(coder.encString64(  (phone == null    ? "":phone  )));
		data_buf.put(coder.encString64(  (mac_addr == null ? "":mac_addr  )));
		data_buf.rewind();
		
		return StNetPacket.buildReq(
				Command.Signup, Flow.CLIENT_TO_SERVER, code, 
				ss_id, 
				null, null,
				data_buf);
	}
	
	
	public StPktSignup(StNetPacket pkt0){
		super(pkt0);
	}
	
	public String getDataName() {
		this.assertClientRequest();
		ByteBuffer buffer = this.dataGetBufferBlock(0,  StCoder.N_ENC_STR_LEN);
		return StCoder.getInstance().decString64(buffer);
	}
	
	public String getDataPassword() {
		this.assertClientRequest();		
		ByteBuffer buffer = this.dataGetBufferBlock(StCoder.N_ENC_STR_LEN*1,  StCoder.N_ENC_STR_LEN);
		return StCoder.getInstance().decString64(buffer);
	}
	
	public String getDataLabel() {
		this.assertClientRequest();		
		ByteBuffer buffer = this.dataGetBufferBlock(StCoder.N_ENC_STR_LEN*2,  StCoder.N_ENC_STR_LEN);
		return StCoder.getInstance().decString64(buffer);
	}
	
	public String getDataPhone() {
		this.assertClientRequest();		
		ByteBuffer buffer = this.dataGetBufferBlock(StCoder.N_ENC_STR_LEN*3,  StCoder.N_ENC_STR_LEN);
		return StCoder.getInstance().decString64(buffer);
	}
	
	public String getDataSmsCode() {
		this.assertClientRequest();		
		ByteBuffer buffer = this.dataGetBufferBlock(StCoder.N_ENC_STR_LEN*4,  StCoder.N_ENC_STR_LEN);
		return StCoder.getInstance().decString64(buffer);
	}
	
	public String getDataMacAddr() {
		this.assertClientRequest();		
		ByteBuffer buffer = this.dataGetBufferBlock(StCoder.N_ENC_STR_LEN*5,  StCoder.N_ENC_STR_LEN);
		return StCoder.getInstance().decString64(buffer);
	}
	
	public StClientInfo getDataClientInfo() {
		util.assertTrue(this.isTypeResponseAllow() && this.isTypeFlowFromSrvToClient(), 
				"CANNOT call this method for current packet type: " + this.getTypeStr());
		return this.dataGetClientInfo(0);
	}
}
