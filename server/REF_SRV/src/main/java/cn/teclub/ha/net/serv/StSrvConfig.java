package cn.teclub.ha.net.serv;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import cn.teclub.common.ChuyuFamily;
import cn.teclub.common.ChuyuLog;




/**
 * <h1>Server Configurations.</h1>
 * 
 * <p> Used by both Single & Group Server. 
 * 
 * @author mancook
 *
 */
public class StSrvConfig extends ChuyuFamily 
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	
	private static final int		VERSION_CODE = 1024;
	private static final String 	VERSION_NAME = "v0.2.2 Alpha"; 
	public  static final String  	VERSION_INFO = VERSION_NAME + " -- n" + VERSION_CODE;
	public  static final String		SIGNUP_GW_TEMP_NAME = "GW-FakeName-????";
	public  static final String		SIGNUP_GW_PASSWD = "1234ABCD";
	
	
	
	@SuppressWarnings("unused")
	private static String buddlePath = null;
	
	private static final String BUDDLE_NAME = "st_ha_srv";
	private static StSrvConfig   _ins = new StSrvConfig();
	
	public static StSrvConfig getInstance(){
		return _ins;
	}
	
	
	////////////////////////////////////////
	private final int		srvPort1, srvPort2;
	private final int		stunSrvPort;
	
	public final boolean 	isConnPulse;
	public final int 		preprocPoolSize;
	public final int		grpNum;		// only valid for group-server
	public final int		grpMaxConn;	// only valid for group-server
	
	public final int checkMgrTime;
	public final int checkClientTime;
	public final int connMaxOffline;
	public final int dbObjCount;
	
	public final boolean	useSSL;
	public final String		keyStore;
	public final String		keyStorePass;
	
	public final String  	log4jCfg;
	
	
	/**
	 * Constructor.
	 */
	private StSrvConfig() {
		/*
		 *  DO NOT DELETE
		 *  
		 *  Old Implementation: Load from a fixed file path
		 * 
				ResourceBundle res = null;
				this.buddleFile = buddlePath  + "/" + buddleName + ".properties" ;
				System.out.println("[CS] Loading config from '" + this.buddleFile  + "' ...");
				
				URL[] urls = {(new File(buddlePath)).toURI().toURL()};  
				ClassLoader loader = new URLClassLoader(urls);  
				res  = ResourceBundle.getBundle(buddleName, Locale.getDefault(), loader);  
		*/
		System.out.println("[CS] Loading config from '" + BUDDLE_NAME  + "' in CLASSPATH ...");
		ResourceBundle res  = ResourceBundle.getBundle(BUDDLE_NAME, Locale.getDefault());
		URL location = getClass().getClassLoader().getResource(BUDDLE_NAME + ".properties");
		this.log4jCfg		= location.getFile() ; 
		
		////// load config
		//this.srvHost 		= res.getString("srv_host");
		this.srvPort1 		= Integer.parseInt(res.getString("srv_port").trim());
		this.srvPort2		= this.srvPort1 + 1;
		this.stunSrvPort 	= Integer.parseInt(res.getString("stun_srv_port").trim());
		
		this.preprocPoolSize=Integer.parseInt(res.getString("preproc_pool_size").trim());
		this.isConnPulse	= res.getString("conn_pulse").trim().equalsIgnoreCase("true");
		this.grpNum 		= Integer.parseInt(res.getString("grp_num").trim());
		this.grpMaxConn 	= Integer.parseInt(res.getString("grp_max_conn").trim());
		
		this.useSSL			= res.getString("use_ssl").trim().equalsIgnoreCase("true");
		this.keyStore		= res.getString("key_store").trim();
		this.keyStorePass	= res.getString("key_store_pass").trim();
		
		this.checkMgrTime	= Integer.parseInt(res.getString("check_mgr_time").trim());
		this.checkClientTime= Integer.parseInt(res.getString("check_client_time").trim());
		this.connMaxOffline	= Integer.parseInt(res.getString("conn_max_offline").trim());
		this.dbObjCount		= Integer.parseInt(res.getString("db_obj_count").trim());
		
		// init log4j
    	System.out.println("[StSrvConfig] initialize log4j with: \n" + log4jCfg );
    	ChuyuLog.setLog4jConf(this.log4jCfg);   
    	ChuyuLog.getInstance();
    	
    	//System.out.println("[StSrvConfig] Dump Configs: \n" + this.toStringXml(" @@@@ "));
	}

	public int getGrpMaxConn() {
		return grpMaxConn;
	}

	public int getGrpNum() {
		return grpNum;
	}

	
	public long getClientTimeout(){
		return 10*1000;
	}
	
	public int getSrvPort1() {
		return srvPort1;
	}

	public int getStunSrvPort() {
		return stunSrvPort;
	}

	public int getSrvPort2() {
		return srvPort2;
	}

	public boolean isDebug() {
		return false;
		// return true;
	}


	public boolean usePulseConn() {
		return this.isConnPulse;
	}
}

