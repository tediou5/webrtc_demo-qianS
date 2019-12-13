package cn.teclub.ha.client.rpr;

import java.nio.ByteBuffer;

import cn.teclub.ha.client.StcParams;
import cn.teclub.ha.lib.StCoder;
import cn.teclub.ha.lib.StErrUserError;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.net.StTransAddr;
import cn.teclub.ha.request.StNetPacket;
import cn.teclub.ha.request.StPktLogin;


public class StcReqSrvLogin extends StcReqSrv
{
    ////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS AND METHODS
	////////////////////////////////////////////////////////////////////////////

	public static ByteBuffer buildData(
			final String name, 
			final String passwd
			) 
	{
		// 2016-8-7: DO NOT query public address. Server socket has public IP.
		// Use a fake public IP, so that both new & old clients can log in. 
		final StTransAddr pub_addr = new StTransAddr(0x0A01010A, 0);
		
		final StCoder coder  = StCoder.getInstance();
		final ByteBuffer data_buf = ByteBuffer.allocate(StCoder.N_ENC_STR_LEN * 3);
		data_buf.put(coder.encString64(name));  	// name max len is checked 
		data_buf.put(coder.encString64(passwd));   	// password max len is checked 
		data_buf.put(coder.encString64(pub_addr.getIpStr()));
		data_buf.rewind();
		return data_buf;
	}
	
	
    
	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	

	private final String password;
	

	public StcReqSrvLogin(
			final String name,
			final String passwd,
			final String dscp 
			) 
	{
		this(name, passwd, 0, dscp);
	}

	
	
	public StcReqSrvLogin(
			final String name,
			final String passwd,
			final int timeout, 
			final String dscp
			) 
	{
		// [Theodore: 2016-09-14] Connection is not available when sending LOGIN request 
		// It takes about 3-5s to establish the connection!
		super(	StNetPacket.Command.Login, 
				StNetPacket.Code.NONE, 
				buildData(name, passwd), 
				timeout > 0 ? timeout : 4000+2000 , dscp);
		this.password = passwd;
	}
	
	
	private StcRprState preState;


	protected boolean onPreSend(){
		// [2017-3-17] you have to: make sure ONLY ONE login-request is sent in your application!
		//
		// To do that, send the login-request in an RPR execution, when RPR state is NOT LOGING.
		// 
		// Error occurs, when several login-requests are sent in a very short time.
		// e.g. In LoginActivity on Android, if every call to onResume() sends a login request, 
		// the 2nd login-request may cause runtime exception here!
		// 
		// This is a wrong implementation. If onPreSend() return false, onTimeout() still throws 
		// runtime exception!
		//      ---------------------------------------------------------------------------
		//		preState = sharedVar.getStat();
		//		if(preState == StcRprState.LOGING){
		//			stLog.debug("Abort -- a login request is sent, just now.");
		//			return false;
		//		}else{
		//			sharedVar.setStat(StcRprState.LOGING);
		//			return true;
		//		}
		//      ----------------------------------------------------------------------------
		//
		/*
		util.assertTrue(sharedVar.getStat() != StcRprState.LOGING, "RPR state("+ sharedVar.getStat() +") is LOGING!");
		preState = sharedVar.getStat();
		sharedVar.setStat(StcRprState.LOGING);
		return true;
		*/


		//
		// [Theodore: 2017-10-25] main-pulse abort client request, if this method returns false
		//
		preState = sharedVar.getStat();
		if(preState == StcRprState.LOGING){
			stLog.debug("Abort -- a login request is sent, just now.");
			return false;
		}else{
			sharedVar.setStat(StcRprState.LOGING);
			return true;
		}
	}
	
	
	@Override
	protected void onTimeout() {
		stLog.error("Login TIMEOUT! PreSend-State:"  + preState + ", RPR-State:" + sharedVar.getStat() + this.dump());
		util.assertTrue(sharedVar.getStat() == StcRprState.LOGING, "RPR state("+ sharedVar.getStat() +") is NOT LOGING!");
		rprObject.sharedVar.setStat(preState);
		preState = null;
	}

	
	private void loginAllow(final StClientInfo local ){
		util.assertTrue(sharedVar.getStat() == StcRprState.LOGING);
		sharedVar.setStat(StcRprState.UPDATE_FRD);
		preState = null;
		
		sharedVar.setLocal(local, password);
		sharedVar.setAutoLogin(rprObject.params.autoRelogin);
		rprObject.sendEventToApp(new StcEvtRpr.InfoLoginSuccess());
		//rprObject.sendMessage(StMessageToGui.LOGIN_SUCCESS);

		rprObject.info.addLoginTime();
		
		stLog.debug("Delete Previous Friends");
		sharedVar.clearRemoteClients();
		
		sharedVar.setRefreshTime(System.currentTimeMillis());
		
		this.resResult = local;
		stLog.info(util.testMilestoneLog("^_^ Login OK: "+ local));
	}
	
	
	private void loginDeny(byte deny_code){
		util.assertTrue(sharedVar.getStat() == StcRprState.LOGING);
		rprObject.sharedVar.setStat(preState);
		preState = null;
		
		switch(deny_code){
		case StNetPacket.Code.Login.DENY_USER_NAME_ERROR:
			stLog.error("User Name Error");
			break;
		case StNetPacket.Code.Login.DENY_PASSWD_ERROR:
			stLog.error("User Password Error");
			break;
		default:
			throw new StErrUserError("Unknown Code: " + getResCode());
		}
		rprObject.sendEventToApp(new StcEvtRpr.InfoLoginFail(deny_code));
		//rprObject.sendMessage(StMessageToGui.LOGIN_FAILURE, deny_code, 0, null);
	}

	
	protected void onResponse(final StNetPacket pkt){
		// call super.onResponse() to call user defined onResAllow() & onResDeny()
		// must be called first!
		//
		// [2017-3-16] Seems unnecessary to override onResponse(). 
		// It seems that StPktLogin.getDataClientInfo() is the ONLY reason to process the raw packet: 
		// 
		super.onResponse(pkt);
		if(pkt.isTypeResponseAllow()){
			util.assertTrue(pkt instanceof StPktLogin, "Not Login Packet!");
			final StClientInfo local = ((StPktLogin) pkt).getDataClientInfo();
			this.resResult = local;
			loginAllow(local);
		}else{
			loginDeny(pkt.getCode());
		}
	}
	
	
	@Override
	protected void onResAllow(byte code, ByteBuffer data) {
		// do nothing.
	}
	
	@Override
	protected void onResDeny(byte code, ByteBuffer data) {
		// do nothing.
	}
}



class StcReqClientStatus extends StcReqSrv
{
	protected StcReqClientStatus()  
	{
		super(StNetPacket.Command.CltStatus,
				StNetPacket.Code.NONE, 
				null, 
				(StcParams.getInstance().msConnectionCheckPeriod / 4)  < 5*1000 ? 5*1000 : StcParams.getInstance().msConnectionCheckPeriod /4,
				"Online Client Reports Status");
	}


	@Override
	protected boolean onPreSend(){
		rprObject.sharedVar.setSendStatus(true);
		return true;
	}
	
	@Override
	protected void onTimeout() {
		stLog.warn("UserDisconnect with server");
		rprObject.ssObj.disconnect();
		rprObject.sharedVar.setSendStatus(false);
	}

	@Override
	protected void onResAllow(byte code, ByteBuffer data) {
		stLog.debug("Report Status OK");
		sharedVar.setSendStatus(false);
	}

	@Override
	protected void onResDeny(byte code, ByteBuffer data) {
		throw new StErrUserError("Impossible");
	}
}
