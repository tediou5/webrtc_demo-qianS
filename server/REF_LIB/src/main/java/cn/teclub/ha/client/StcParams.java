package cn.teclub.ha.client;

import javax.net.ssl.SSLSocketFactory;

import cn.teclub.common.ChuyuObj;
import cn.teclub.common.ChuyuUtil;
import cn.teclub.ha.net.StObjectMgrInterface;



/**
 * <h1>Client Parameters</h1>
 * 
 * 1. Make all fields in Params 'final', so that they do not change during the runtime.
 * 2. Create a shadow class ParamsInit to pass the initialization parameters.
 * 
 * @author mancook
 *
 */
public class StcParams 
	extends ChuyuObj 
	implements ChuyuObj.DumpAttribute 
{
	private static StcParams _ins;
	public static void initialize(final StcInitParams init_p){
		ChuyuUtil.getInstance().assertTrue(
				_ins == null, "DO NOT Initialize Params Again!");
		_ins = new StcParams(init_p);
	}
	
	
	public static void destroy(){
		_ins = null;
	}
	
	public static StcParams getInstance(){
		ChuyuUtil.getInstance().assertNotNull(_ins, "Initialize Params Before Using!");
		return _ins;
	}
	
	public final String   	homeDir; 	
	
	public final String 	srvHost 	; 
	public final int	  	srvPort 	; 
	public final String 	ftpSrv 		;
	public final int		ftpPort 	; 
	
	public final String 	ftpUser 	; 
	public final String 	ftpPasswd 	; 
	public final String 	ftpUploadHomeDir ; 

	public final StObjectMgrInterface  	objectMgr;
	

	/**
	 * How often the online client report its status.
	 * Preferred Value: 30,000ms (30 s)
	 */
	public final int msConnectionCheckPeriod ;
	
	
	/**
	 * How often the core pulse checks task & loop list and delete the FINISHED threads. 
	 * Preferred Value: 300,000ms (5 min)
	 */
	public final int msTaskCheckPeriod ;
	
	/**
	 * How often the RPR pulse ticks all requests. 
	 * Preferred Value: 200ms
	 */
	public final int msRequestCheckPeriod ; 
	
	/**
	 * [Theodore: 2016-09-20] DO NOT DISABLE auto-relogin on Android!!!!
	 * On Android, use network state receiver. ReLogin is performed when network is available.
	 * But DO NOT RELAY ON the network state receiver!!!!
	 */
	public final boolean autoRelogin;
	
	public final boolean debugMode;
	public final boolean useSSL;
	public final String  keyPath ;
	
	/**
	 * Only set by android app! If set, keyPath is ignored!
	 */
	public final SSLSocketFactory  sockFac;
	
	
	/**
     * Constructor
     * @param p
     */
    private StcParams(StcInitParams p){
    	this.homeDir	= p.homeDir;
    	
		this.srvHost	= p.srvHost;
		this.srvPort	= p.srvPort;
		this.ftpSrv		= p.ftpSrv;
		this.ftpPort	= p.ftpPort;
		
		this.ftpUser	= p.ftpUser;
		this.ftpPasswd	= p.ftpPasswd;
		this.ftpUploadHomeDir = p.ftpUploadHomeDir;
		this.objectMgr 	= p.objectMgr;
		
		
		this.msConnectionCheckPeriod= p.msConnectionCheckPeriod;
		this.msTaskCheckPeriod 		= p.msTaskCheckPeriod;
		this.msRequestCheckPeriod 	= p.msRequestCheckPeriod;
		
		this.autoRelogin= p.autoRelogin;
		this.debugMode	= p.debugMode;
		this.useSSL		= p.useSSL;
		this.keyPath	= p.keyPath;
		this.sockFac	= p.sockFac;
		
		ChuyuUtil util = ChuyuUtil.getInstance();
		stLog.trace("Default Dump in ChuyuFamily: \n"  + this.toStringXml());
		stLog.debug("Check Client Param:"  + dump());
		util.assertNotNull(homeDir,  "Home Dir is NULL");
		//util.assertNotNull(objMgr,  "SavedObject-Manager is NULL");
		util.assertNotNull(objectMgr, "FileCache-Manager is NULL");
		util.assertTrue(srvHost != null && srvPort > 1000 , "Server Host/Port is NOT Correct: " + srvHost + "/" + srvPort);
		util.assertTrue( ftpSrv != null && ftpPort > 0 && ftpUser != null && ftpPasswd != null && ftpUploadHomeDir != null, "FTP Config NOT Correct!");
		util.assertTrue( msConnectionCheckPeriod >= 1000 && msRequestCheckPeriod >= 100 && msTaskCheckPeriod >= 1000, "Check Period NOT Correct!");
	}

	
	@Override
	public void dumpSetup() {
		this.dumpSetBufferSize(1024);
		this.dumpAddLine(" Application Home Dir   : " + this.homeDir);
		this.dumpAddLine(" Server Host: " + this.srvHost);
		this.dumpAddLine(" Server Port: " + this.srvPort);
		this.dumpAddLine(" FTP Server : " + this.ftpSrv);
		this.dumpAddLine(" FTP Port   : " + this.ftpPort);
		
		this.dumpAddLine(" FTP User    : " + this.ftpUser);
		this.dumpAddLine(" FTP Password: " + this.ftpPasswd);
		this.dumpAddLine(" FTP Upload  Home Dir: " + this.ftpUploadHomeDir );
		
		this.dumpAddLine(" -------------------------------------");
		this.dumpAddLine(" Status Connection Period : " + this.msConnectionCheckPeriod);
		this.dumpAddLine(" Task Check Period        : " + this.msTaskCheckPeriod );
		this.dumpAddLine(" Session Check Period     : " + this.msRequestCheckPeriod );
		this.dumpAddLine(" Debug Mode             : " + this.debugMode);
		this.dumpAddLine(" Use SSL                : " + ( this.useSSL ? "YES" : "NO") );
		this.dumpAddLine(" Key Path               : " + this.keyPath );
		this.dumpAddLine(" Socket Factory:        : " + this.sockFac );
		
		this.dumpAddLine(" Cache Manager: \n" + objectMgr);

	}
}