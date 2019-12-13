package cn.teclub.ha.test;

import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import cn.teclub.common.ChuyuLog;
import cn.teclub.common.ChuyuObj;
import cn.teclub.common.ChuyuUtil;
import cn.teclub.ha.client.StcInitParams;
import cn.teclub.ha.client.StcException.ExpLocalClientOffline;
import cn.teclub.ha.client.app.StcExpApp.AddContactFailure;
import cn.teclub.ha.client.app.StcExpApp.LoginFailure;
import cn.teclub.ha.client.app.StcAppObj;
import cn.teclub.ha.client.app.StcExpApp;
import cn.teclub.ha.client.rpr.StcExpRpr;
import cn.teclub.ha.client.rpr.StcReqSrvAddContact;
import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.net.StObjectMgrInterface;
import cn.teclub.ha.net.StMessage;


@SuppressWarnings("deprecation")
class MyObjectMgr 
		extends ChuyuObj 
		implements StObjectMgrInterface
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	

	private static MyObjectMgr _ins;
	public static MyObjectMgr getInstance(){
		if(null == _ins){
			_ins = new MyObjectMgr();
		}
		return _ins;
	}
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	

	private final ConcurrentHashMap<String, String>  					 cacheList ;
	
	
	private MyObjectMgr(){
		this.cacheList = new ConcurrentHashMap<>();
	}
	
	////////////////////////////////////////////////////////////////////////////
	// StFileCacheMgr
	////////////////////////////////////////////////////////////////////////////
	
	@Override
	public String getCacheName(String local_file) {
		return cacheList.get(local_file);
	}

	@Override
	public void setCacheName(String local_file, String cache_path) {
		cacheList.put(local_file, cache_path);
	}
	
	public String toString(){
		return "{File Cache Manager for Testing}";
	}

	
	
	@Override
	public ArrayList<StMessage> getApplyMessage(boolean all, long create_time) {
		stLog.warn("TOOD: implemment a fake message manger");
		return null;
		//throw new StErrUserError("TODO");
	}

	@Override
	public void saveMessage(StMessage msg) {
		stLog.warn("TOOD: implemment a fake message manger");
		//throw new StErrUserError("TODO");
	}
	
}



/**
 * A client object for testing <br/>
 * 
 * It is a sub-class of StcAppObj.
 * 
 * @author mancook
 *
 */
class TestClientObj extends StcAppObj
{
	private static TestClientObj _ins;
	
	public  static void initialize( StcInitParams p )
	{
		ChuyuUtil.getInstance().assertTrue(_ins == null, "FATAL: DO NOT initialize again!");
		_ins = new TestClientObj(p);
	}

	public static TestClientObj getInstance(){
		ChuyuUtil.getInstance().assertNotNull(_ins, "Initialize Before Using!");
		return _ins;
	}
	
	
	private TestClientObj( final StcInitParams p )
	{
		super(p);
		stLog.info("Test Client Constructed!");
	}
	
	
	protected void destroy(){
		super.destroy();
	}

	/**
	 * only use in test
	 * @param id2 adding-friend ID
	 *
	 */
	@SuppressWarnings("deprecation")
	public void addContact(final StClientID id2) throws AddContactFailure, StcExpRpr.ExpReqTimeout {
		final StcReqSrvAddContact req = new StcReqSrvAddContact(id2, "ABCD1234");
		try {
			req.startRequest();
		} catch (ExpLocalClientOffline e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, ""));
			throw new StcExpApp.AddContactFailure();
		}
		req.waitForResult();
		if(!req.isResAllowed()){
			throw new StcExpApp.AddContactFailure();
		}
	}
}





/**
 * <p> Log4j is initialized in test driver. 
 * 
 * <p> DO NOT initialize StClient in test driver!!! 
 * Otherwise, you cannot change the parameters. e.g. ftp-server-host, video-dir, etc.
 * 
 * @author mancook
 *
 */
public class ClientDriver extends ChuyuObj{
	private final String BUDDLE_NAME; // = "st_test";
	
	protected TestClientObj 	haClient;
	protected StcInitParams 	initParams;
	
	
	public ClientDriver(){
		this("st_test");
	}
	
	
	public ClientDriver(final String buddle_name){
		this.BUDDLE_NAME = buddle_name;
		System.out.println("[Client Test Driver] Loading client's resource bundle '" + BUDDLE_NAME  + "' in CLASSPATH ...");
		ResourceBundle res  = ResourceBundle.getBundle(BUDDLE_NAME, Locale.getDefault());
		URL location = getClass().getClassLoader().getResource(BUDDLE_NAME + ".properties");
		if(location == null){
			throw new RuntimeException("fail to get buddle location: " + BUDDLE_NAME);
		}

		String log4j_cfg		= location.getFile() ;

    	System.out.println("[Client Test Driver] INFO: initialize log4j with: " + log4j_cfg);
    	ChuyuLog.setLog4jConf(log4j_cfg);
    	ChuyuLog.getInstance();
    	
    	initParams = new StcInitParams();
    	initParams.keyPath	= res.getString("key_path").trim();
    	initParams.srvHost 	= res.getString("srv_host").trim();
    	initParams.srvPort 	= Integer.parseInt(res.getString("srv_port").trim());
    	initParams.homeDir 	= "/Users/mancook/tmp/fake-dev/sdcard/AA-FAMBO";
    	initParams.objectMgr 	= MyObjectMgr.getInstance();
    	initParams.debugMode 	= true;
    	initParams.msConnectionCheckPeriod	= 30*1000;
    	initParams.msRequestCheckPeriod 	=    1000;
    	initParams.msTaskCheckPeriod 		= 20*1000;
    	initParams.useSSL = false;
    	stLog.warn("#### DO NOT initialize StClient in test driver! ");
    	stLog.warn("#### initialize in sub class, so that client parameters can be changed. ");
		stLog.info("Client-Test-Driver is created!");
	}
	
	
	
	protected void loginServer(
			final String name,
			final String passwd
			) throws LoginFailure, StcExpRpr.ExpReqTimeout {
		message("\n******** Client '"+ name +"/" + passwd + "' logs in     ********\n");
		
		final StClientInfo local = haClient.login(name, passwd);
		util.sleep(1000);
		util.assertTrue(haClient.getRprInfo().isOnline(), "Client is NOT ONLINE!");
		message("Client Online: " + local );
		util.sleep(2000);
	}
	
	
	protected void logoutServer(){
		message("\n******** Client logs out  ********\n");
		haClient.logout();
		haClient.destroy();
		util.sleep(1000);
	}
	
	protected static void sleepSecond(final int sec){
		message("-------- Sleep "+ sec + " seconds ...  --------\n");  
		util.sleep(sec*1000);
	}
	
	
	protected static void message(final String msg){
		System.out.println( msg);
		util.sleep(500);
	}
	
}