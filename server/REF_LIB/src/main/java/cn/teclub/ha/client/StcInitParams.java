package cn.teclub.ha.client;

import javax.net.ssl.SSLSocketFactory;

import cn.teclub.common.ChuyuFamily;
import cn.teclub.ha.net.StObjectMgrInterface;


/**
 * <h1>Initialization Parameters</h1>
 * 
 * <p> used to initialize the client class.
 * 
 * @author mancook
 *
 */
public class StcInitParams extends ChuyuFamily {
	public String   homeDir		= "/sdcar/AA-FAMBO";
	
	public String 	srvHost 	= "git.teclub.cn";
	public int	  	srvPort 	= 0;
	public String 	ftpSrv 		= "git.teclub.cn";
	public int		ftpPort 	= 21;
	
	public String 	ftpUser 	= "mancook";
	public String 	ftpPasswd 	= "qweR1234";
	public String 	ftpUploadHomeDir = "/home/mancook/AA-FAMBO/cache";
	
	public StObjectMgrInterface  	objectMgr;
	
	
	public int msConnectionCheckPeriod	= 21*1000;	
	public int msRequestCheckPeriod 	=     500; 	 
	public int msTaskCheckPeriod 	 	= 60*1000; 	
	public boolean debugMode 	 		= false;
	
	public boolean autoRelogin = true;
	
	// TODO:
	public boolean useSSL = true;
	public String  keyPath = "./src/keystore.jks";
	public SSLSocketFactory  sockFac;
}