package cn.teclub.ha.client.app;


import cn.teclub.ha.client.StcException;
import cn.teclub.ha.client.rpr.StRemoteFile;
import cn.teclub.ha.client.rpr.StcExpRpr;
import cn.teclub.ha.client.rpr.StcRprObject;
import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.StClientInfo;

public interface StBridgeToAppObj
{

	////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// async API (calling thread DOES NOT block)
	// ---- a event to sent to app/rpr/session pulse to do the work
	//
	////////////////////////////////////////////////////////////////////////////////////////////////

	void login2(final String username, final String password);
	void deleteContact2(final StClientID rem_id);

	void logout();

	void connect();
	void disconnect();
	void updateIconTS(final long icon_ts) throws StcException.ExpLocalClientOffline;
	void updateLabel(final String label) throws StcException.ExpLocalClientOffline;
	void updateDscp(final String dscp) throws StcException.ExpLocalClientOffline;
	void updatePasswd(final String passwd) throws StcException.ExpLocalClientOffline;

	//void uploadLogs(ArrayList<String> logs);


	/**
	 * Message is sent to GUI at ALLOW / DENY / TIMEOUT <br/>
	 *
	 * TODO: make StSDAdminSlaveActivity use this API to query friends of a slave
	 */
	void queryFriends(StClientID clientID);



	////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// sync API (calling thread blocks)
	//
	////////////////////////////////////////////////////////////////////////////////////////////////

	StcRprObject.Info getRprInfo();

	void resetPasswd(String phone, String new_passwd, String sms_code) 
			throws StcExpApp.ResetPasswdFailure, StcExpRpr.ExpReqTimeout;
	
	
	StClientInfo signupAsUser(
            final String name,
            final String passwd,
            final String label,
            final String phone,
            final String sms_code,
            final String home_dir)
			throws StcExpApp.SignupFailure, StcExpRpr.ExpReqTimeout;


	void signout(final String passwd)
			throws StcException.ExpLocalClientOffline, StcExpApp.SignoutFailure, StcExpRpr.ExpReqTimeout;


	StClientInfo login(final String username, final String password)
			throws StcExpApp.LoginFailure, StcExpRpr.ExpReqTimeout;


	int pingRemote(final StClientInfo r_clt)
			throws StcException.ExpLocalClientOffline,  StcExpRpr.ExpReqTimeout;


	StClientInfo searchContact(final String search_item)
			throws StcExpApp.SearchContactFailure, StcExpRpr.ExpReqTimeout;


	void deleteContact(final StClientID rem_id)
			throws StcExpApp.DelContactFailure, StcExpRpr.ExpReqTimeout;


	/**
	 * @deprecated  this is the old method to download a remote file, which is relayed by FTP
	 */
	void downloadFile(
            final StClientInfo r_clt,
            final int timeout,
            final StRemoteFile remote_file,
            final String local_dir
    ) throws StcException.ExpFailToDownload;



}
