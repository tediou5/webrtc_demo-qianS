package cn.teclub.ha.client.rpr;




/**
 * Check & Login with previous login info. <br/>
 * 
 * <pre>
 * - Do nothing, if status is SEND_STATUS / LOGGING / LOGOUT;
 * - Send CLIENT_STATUS to server if ONLINE;
 * - Login with new/previous login-info if OFFLINE;
 * 
 * </pre>
 * 
 * 
 * @author mancook
 *
 */
public class StcExCheckAndLogin extends StcRprExecution
{
	private final int timeout; // login timeout
	private final String username, password;

	public StcExCheckAndLogin(){
		this(null, null);
	}


	public StcExCheckAndLogin(String user, String pass){
		this(user, pass, 0);
	}

	public StcExCheckAndLogin(String user, String passwd, int timeout) {
		this.username 	= user;
		this.password	= passwd;
		this.timeout	= timeout;
	}


	private void login(String user, String pass){
		sendRequest(new StcReqSrvLogin(user, pass, timeout,  "Login as " + user));
	}


	@Override
	protected void perform() {
		final StcRprObject.Info info = getRprInfo();
		final StcRprState stat = sharedVar.getStat();
		final long T0 = System.currentTimeMillis();
		
		if(stat == StcRprState.LOGING ){
			stLog.info("Do nothing -- RPR State is: " + stat);
			return;
		}
		if(stat == StcRprState.LOGOUT ){
			stLog.warn("Abort Relogin -- RPR State is:" + stat);
			return;
		}
		
		if( info.isOnline() ){
			stLog.trace("Client is ONLINE ...");
			if( T0 - sharedVar.getRefreshTime() < 1000*30){
				// 2017-3-16: NO need to report status to server. 
				stLog.debug("Connection is refreshed <30s. RPR State:" + stat);
			} 
			else if(sharedVar.isSendStatus()){
				stLog.debug("Client is LOGIN & waiting for CLT_STATUS... ");
			}
			else {
				sendRequest(new StcReqClientStatus());
			}
		}else {
			stLog.trace("Client is OFFLINE...");
			if(username != null && password != null){
				stLog.debug("login with input user/pass");
				login(username, password);
			}else{
				stLog.debug("re-login with previous login-info");
				if (!sharedVar.isAutoLogin()) {
					stLog.warn("Abort Relogin -- AutoLogin is OFF!");
					return;
				}
				final String user = sharedVar.getClientName();
				final String pass = sharedVar.getPasswd();
				if (user == null || pass == null) {
					// client has not logged in, since startup!
					stLog.warn("Abort ReLogin -- no previous login-info: " + user + "/" + pass);
					return;
				}
				login(user, pass);
			}
		}


	}
}