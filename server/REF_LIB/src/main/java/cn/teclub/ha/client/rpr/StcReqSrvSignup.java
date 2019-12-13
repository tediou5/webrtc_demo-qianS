package cn.teclub.ha.client.rpr;

import java.nio.ByteBuffer;

import cn.teclub.ha.lib.StCoder;
import cn.teclub.ha.lib.StErrUserError;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.request.StNetPacket;


public class StcReqSrvSignup extends StcReqSrv
{
    ////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS AND METHODS
	////////////////////////////////////////////////////////////////////////////
	

	private static ByteBuffer buildData(
			String name, 
			String passwd, 
			String label, 
			String phone, 
			String sms_code,
			String mac_addr
			)   
	{
		final StCoder coder  = StCoder.getInstance();
		final ByteBuffer data_buf = ByteBuffer.allocate(StCoder.N_ENC_STR_LEN * 6);
		data_buf.put(coder.encString64(name));  	
		data_buf.put(coder.encString64(passwd)); 
		data_buf.put(coder.encString64(  (label == null    ? "":label  )));
		data_buf.put(coder.encString64(  (phone == null    ? "":phone  )));
		data_buf.put(coder.encString64(  (sms_code == null ? "":sms_code  )));
		data_buf.put(coder.encString64(  (mac_addr == null ? "":mac_addr  )));
		data_buf.rewind();
		return data_buf;
	}

	
	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	

	private final String name, password, phone, macAddr;
	

	public StcReqSrvSignup(
			final byte code, 
			final String name, 
			final String passwd, 
			final String label, 
			final String phone, 
			final String sms_code,
			final String mac_addr,
			final int timeout
			) 
	{
		super(	StNetPacket.Command.Signup, 
				code, 
				buildData(name, passwd, label, phone, sms_code, mac_addr),
				timeout, 
				"Client '" + name + "' signs up");
		this.name = name;
		this.password = passwd;
		this.phone = phone;
		this.macAddr = mac_addr;
	}

	
	public String getName() {
		return name;
	}
	public String getPhone() {
		return phone;
	}
	public String getMacAddr() {
		return macAddr;
	}
	public String getPassword() {
		return password;
	}
	
	
	@Override
	protected void onTimeout() {
		stLog.error("Signup TIMEOUT \n\t" + this.dump() );
	}

	
	@Override
	protected void onResAllow(final byte code, final ByteBuffer data) {
		final StClientInfo ci  = new StClientInfo(data);
		this.resResult = ci;
		switch(code){
		case StNetPacket.Code.NONE:
			stLog.info(util.testMilestoneLog("Signup Success: " + ci ));
			break;
		case StNetPacket.Code.Signup.ALLOW_USE_OLD:
			stLog.warn("Previous Signup Info: " + ci.dumpSimple());
			rprObject.logout();
			return;
		default:
			throw new StErrUserError("Unknown Code: " + code);
		}
		
		// After signup success, it is online!
		sharedVar.setLocal(ci, password);
		sharedVar.setAutoLogin(rprObject.params.autoRelogin);
		//rprObject.sendMessage(StMessageToGui.LOGIN_SUCCESS);
		rprObject.sendEventToApp(new StcEvtRpr.InfoLoginSuccess());
		sharedVar.setStat(StcRprState.IDEAL);
		rprObject.info.addLoginTime();
	}
	
	
	
	@Override
	protected void onResDeny(byte code, ByteBuffer data) {
		stLog.error("SignUp Failure!");
		rprObject.logout();
	}
}


