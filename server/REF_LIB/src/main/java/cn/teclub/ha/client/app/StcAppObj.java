package cn.teclub.ha.client.app;

import java.nio.ByteBuffer;



//import cn.teclub.android.ha.tl.StcTimeLapse4RemoteMgr;
import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.client.StcInitParams;
import cn.teclub.ha.client.StcParams;
import cn.teclub.ha.client.StcTools;
import cn.teclub.ha.client.StcException.ExpFailToDownload;
import cn.teclub.ha.client.StcException.ExpLocalClientOffline;
import cn.teclub.ha.client.app.StcExpApp.DelContactFailure;
import cn.teclub.ha.client.app.StcExpApp.LoginFailure;
import cn.teclub.ha.client.app.StcExpApp.SearchContactFailure;
import cn.teclub.ha.client.app.StcExpApp.SignoutFailure;
import cn.teclub.ha.client.app.StcExpApp.SignupFailure;
import cn.teclub.ha.client.rpr.StRemoteFile;
import cn.teclub.ha.client.rpr.StBridgeToUser;
import cn.teclub.ha.client.rpr.StcExpRpr;
import cn.teclub.ha.client.rpr.StcReqSrvDelContact;
import cn.teclub.ha.client.rpr.StcReqSrvEditInfo;
import cn.teclub.ha.client.rpr.StcReqSrvLogin;
import cn.teclub.ha.client.rpr.StcReqSrvResetPasswd;
import cn.teclub.ha.client.rpr.StcReqSrvSearchContact;
import cn.teclub.ha.client.rpr.StcReqSrvSignout;
import cn.teclub.ha.client.rpr.StcReqSrvSignup;
import cn.teclub.ha.client.rpr.StcRprObject;
import cn.teclub.ha.lib.StErrUserError;
import cn.teclub.ha.lib.StEvent;
import cn.teclub.ha.lib.StEventListener;
import cn.teclub.ha.lib.StEventPulse;
import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.request.StNetPacket;


/**
 * <h1> Client Application Object </h1>
 * 
 * <pre>
 * API for GUI/Laborer.
 * 
 * Subclass must keep ONLY ONE instance.
 * 
 * </pre>
 * 
 * @author mancook
 *
 */
public abstract class StcAppObj extends ChuyuObj implements StBridgeToUser, StBridgeToAppObj
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	
	private static StcAppObj _ins;


	////////////////////////////////////////////////////////////////////////////
    // Inner Classes
	////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Members
	////////////////////////////////////////////////////////////////////////////	
	public  final StcParams 				params;
	public  final StcTools 					tools;

	private final StcRprObject.Info 		info;
	private final StcAppComp				appComp;

	
	/**
	 * Constructor
	 */
	protected StcAppObj(final StcInitParams p )
	{
		util.assertTrue(_ins == null, "[FATAL] DO NOT Create Application Object, Again!");
		StcParams.initialize(p);
		StcAppComp.initialize(this);
		this.appComp= StcAppComp.getInstance();
		this.params = appComp.params;
		this.tools 	= appComp.tools;
		this.info  	= appComp.info;
		
		appComp.init();
		_ins = this;
	}

	
	
	/**
	 * 
	 * <pre>
	 * This method does:
	 * - Destroy StcParams;
	 * - Clear Loop, Task & request Manager;
	 * - Stop all event pulses & the pulse pool;
	 * - Unset '_ins' to null;
	 * 
	 * </pre>
	 * 
	 */
	protected void destroy(){
		appComp.destroy();
		_ins = null;
	}


	////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// NON-Blocking Request
	//
	////////////////////////////////////////////////////////////////////////////////////////////////

	public StcRprObject.Info getRprInfo(){
		return info;
	}


	public void login2(final String username, final String password) {
		if(info.isOnline()){
			stLog.warn("stop login -- client is online");
			return;
		}

		try {
			final StcReqSrvLogin req = new StcReqSrvLogin(username, password,  "Client " + username + " Logs in...");
			req.startRequest();
		} catch (ExpLocalClientOffline expLocalClientOffline) {
			throw new StErrUserError("Impossible");
		}
	}


	public void deleteContact2(final StClientID id2){
		final StcReqSrvDelContact req = new StcReqSrvDelContact(id2);
		try {
			req.startRequest();
		} catch (ExpLocalClientOffline e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "local client is OFFLINE!"));
		}
	}




	////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Blocking Request
	//
	////////////////////////////////////////////////////////////////////////////////////////////////

	public StClientInfo login(final String username, final String password) throws LoginFailure, StcExpRpr.ExpReqTimeout {
		try {
			final StcReqSrvLogin req = new StcReqSrvLogin(username, password,  "Client " + username + " Logs in...");
			req.startRequest();
			final StClientInfo result = (StClientInfo)req.waitForResult();
			if(!req.isResAllowed()){
				throw new StcExpApp.LoginFailure();
			}
			return result;
		} catch (ExpLocalClientOffline e) {
			throw new StErrUserError("Impossible");
		}
	}


	/**
	 * 
	 * @param r_clt remote client-info
	 * @return Ping Cost (ms)
	 */
	public int pingRemote(final StClientInfo r_clt)
			throws ExpLocalClientOffline,  StcExpRpr.ExpReqTimeout {
		return appComp.pingRemote(r_clt);
	}
	
	
	public void downloadFile( 
			final StClientInfo 	r_clt, 
			final int 			timeout, 
			final StRemoteFile	remote_file, 
			final String 		local_dir 
		) throws ExpFailToDownload 
	{
		appComp.downloadFile(r_clt, timeout, remote_file, local_dir);
	}
	
	
	
	public void logout(){
		appComp.logout();
	}
	
	
	/**
	 * Android App start a login request with previously saved user/password.
	 *
	 * @deprecated use RPR StExCheckAndLogin
	 *
	 */
	public void relogin(){
		throw new StErrUserError("DO NOT use!");
	}


	public StClientInfo searchContact(final String search_item)
			throws SearchContactFailure, StcExpRpr.ExpReqTimeout
	{
		final StcReqSrvSearchContact req = new StcReqSrvSearchContact(search_item);
		try {
			req.startRequest();
		} catch (ExpLocalClientOffline e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, ""));
			throw new StcExpApp.SearchContactFailure();
		}
		final StClientInfo ci = (StClientInfo) req.waitForResult();
		if(!req.isResAllowed()){
			throw new StcExpApp.SearchContactFailure();
		}
		return ci;
	}


	public void deleteContact(final StClientID id2)
			throws DelContactFailure, StcExpRpr.ExpReqTimeout
	{
		final StcReqSrvDelContact req = new StcReqSrvDelContact(id2);
		try {
			req.startRequest();
		} catch (ExpLocalClientOffline e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, ""));
			throw new StcExpApp.DelContactFailure();
		}
		req.waitForResult();
		if(!req.isResAllowed()){
			throw new StcExpApp.DelContactFailure();
		}
	}


	public void connect(){
		appComp.connect();
	}

	
	public void disconnect(){
		appComp.disconnect();
	}
	
	
	public void updateIconTS(final long icon_ts) throws ExpLocalClientOffline {
		ByteBuffer buf = ByteBuffer.allocate(8);
		buf.putLong(icon_ts);
		buf.rewind();
		final StcReqSrvEditInfo req  = new StcReqSrvEditInfo(
				StNetPacket.Code.EditInfo.REQ_ICON_TS, buf);
		req.startRequest();
	}
	
	
	public void updateLabel(final String label) throws ExpLocalClientOffline{
		final StcReqSrvEditInfo req  = new StcReqSrvEditInfo(
				StNetPacket.Code.EditInfo.REQ_LABEL, util.stringFunc.toBuffer(label));
		req.startRequest();
	}


	public void updateDscp(final String dscp) throws ExpLocalClientOffline{
		final StcReqSrvEditInfo req  = new StcReqSrvEditInfo(
				StNetPacket.Code.EditInfo.REQ_DSCP, util.stringFunc.toBuffer(dscp));
		req.startRequest();
	}


	public void updatePasswd(final String passwd) throws ExpLocalClientOffline{
		final StcReqSrvEditInfo req  = new StcReqSrvEditInfo(
				StNetPacket.Code.EditInfo.REQ_PASSWORD, util.stringFunc.toBuffer(passwd));
		req.startRequest();
	}


	public void resetPasswd(String phone, String new_passwd, String sms_code) 
	throws StcExpApp.ResetPasswdFailure, StcExpRpr.ExpReqTimeout
	{
		StcReqSrvResetPasswd req = new StcReqSrvResetPasswd(phone, new_passwd, sms_code, 5000);
		try {
			req.startRequest();
		} catch (ExpLocalClientOffline e) {
			throw new StErrUserError("Impossible");
		}
		
		req.waitForResult();
		if(!req.isResAllowed()){
			stLog.error("Reset Passwd Failure");
			throw new StcExpApp.ResetPasswdFailure();
		}
	}
	
	
	public StClientInfo signupAsUser(
			final String name, 
			final String passwd, 
			final String label, 
			final String phone, 
			final String sms_code,
			final String home_dir) throws SignupFailure, StcExpRpr.ExpReqTimeout {
		final StcReqSrvSignup req = new StcReqSrvSignup(
				StNetPacket.Code.Signup.REQUST_USER, 
				name, passwd, label, phone, sms_code, null, 3000);
		try {
			req.startRequest();
		} catch (ExpLocalClientOffline e) {
			throw new StErrUserError("Impossible");
		}
		
		Object result = req.waitForResult();
		if(!req.isResAllowed()){
			stLog.error("Signup Failure");
			throw new StcExpApp.SignupFailure();
		}
		final StClientInfo ci = (StClientInfo)result;
		if( req.getResCode() == StNetPacket.Code.Signup.ALLOW_USE_OLD ){
			stLog.warn("User Previous SignUp Info: " +  ci.dump() );
			throw new StcExpApp.SignupFailure();
		}
		stLog.info("SignUp Success: " + ci);
		return ci;
	}
	
	
	public void signout(final String passwd)
			throws ExpLocalClientOffline, SignoutFailure, StcExpRpr.ExpReqTimeout {
		StcReqSrvSignout req = new StcReqSrvSignout(info.getName(), passwd);
		req.startRequest();
		req.waitForResult();
		if(!req.isResAllowed()){
			throw new StcExpApp.SignoutFailure();
		}
	}


	public void queryFriends(StClientID clientID) {
		throw new StErrUserError("un-implemented");
	}


	public void addAppListener(StEventListener lis){
		appComp.appPulse.addListener(lis);
	}


	public void delAppListener(StEventListener lis){
		appComp.appPulse.delListener(lis);
	}

	
	public void sendEventToLbr(StcEvtLbr evt){
		appComp.lbrPulse.addNewEvent(evt);
	}


	public void addLbrListener(StEventListener lis){
		appComp.lbrPulse.addListener(lis);
	}

	public void delLbrListener(StEventListener lis){
		appComp.lbrPulse.delListener(lis);
	}


	@Override
	public void addNewEvent(StEvent evt){
		appComp.appPulse.addNewEvent(evt);
	}

	/**
	 * use this method carefully! <br/>
	 *
	 * Current Usage: Create TL4RPlayer instance.
	 */
    public StEventPulse getPulse(){
		return appComp.appPulse;
	}
}
