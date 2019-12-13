package cn.teclub.ha.request;

import java.nio.ByteBuffer;

import cn.teclub.ha.lib.StCoder;
import cn.teclub.ha.lib.StExpUserError;
import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.StClientInfo;


/**
 * <h1>Client: Sign out packet. </h1>
 * 
 * @author mancook
 */
public class StPktSignout extends StNetPacket
{
	public static StNetPacket build(
		final StRequestID ss_id, 
		final StClientID src_clt, 
		final String name, String passwd ) 
	{
		StCoder coder  = StCoder.getInstance();
		ByteBuffer data_buf = ByteBuffer.allocate(StCoder.N_ENC_STR_LEN * 2);
		data_buf.put(coder.encString64(name));  	// name max len is checked 
		data_buf.put(coder.encString64(passwd));   	// password max len is checked 
		data_buf.rewind();
		
		return StNetPacket.build(
				Command.Signout, Service.REQUEST, Flow.CLIENT_TO_SERVER, Code.NONE, 
				ss_id, 
				src_clt, null,
				data_buf);
	}
	
	//////////////////instance members ////////////////////////////////////////

	/**
	 * <h2> Construct a sign-out packet from an instance of super-class. </h2>
	 * 
	 * Called in builder method of super-class. 
	 * 
	 * @param pkt0
	 */
	StPktSignout(StNetPacket pkt0){
		super(pkt0);
	}
	
	public String getDataName() throws StExpUserError{
		this.assertClientRequest();		
		ByteBuffer buffer = this.dataGetBufferBlock(0,  StCoder.N_ENC_STR_LEN);
		return StCoder.getInstance().decString64(buffer);
	}
	
	public String getDataPassword() throws StExpUserError {
		this.assertClientRequest();		
		ByteBuffer buffer = this.dataGetBufferBlock(StCoder.N_ENC_STR_LEN,  StCoder.N_ENC_STR_LEN);
		return StCoder.getInstance().decString64(buffer);
	}
	
	public StClientInfo getDataClientInfo(){
		util.assertTrue(this.isTypeResponseAllow() && this.isTypeFlowFromSrvToClient(), 
				"CANNOT call this method for current packet type: " + this.getTypeStr());
		return this.dataGetClientInfo(0);
	}
}
