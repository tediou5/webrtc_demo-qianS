package cn.teclub.ha.lib;

import java.util.Locale;
import java.util.ResourceBundle;

import cn.teclub.common.ChuyuFamily;


/**
 * StGenLib Configuration for HA Client in _SMOKE_TEST_. <br/><br/>
 *
 * DO NOT configure log4j in StGenLibConfig! <br/><br/>
 *
 * In a test case, HA clients, GW and user, use different log4j config file to log into <br/>
 * different output files. Log4j config file is passed as an argument. Log4j is <br/>
 * configured as soon as the testing application starts. As a result, DO NOT <br/>
 * configure Log4j in this class.  <br/><br/>
 * 
 * Properties are loaded from a bundle file.  <br/>
 * StGenLibConfig is used to initialize an StHaClient.Config object, which is <br/>
 * used to create HA client objects, i.e. HA user and GW.  ICE/STUN server <br/>
 * properties are used to initialize ICE client module. <br/> <br/>
 *
 * Property Flow: <br/>
 *    resource bundle <br/> 
 *      \--------> StTestConfig  <br/>
 *         \-----> StHaClient.Config  <br/>
 *            \--> StIceClientConfig  <br/>  <br/>
 * 
 *
 * NOTE: Used in SMOKE TEST of StGenLib.  <br/><br/>
 *
 * @author mancook
 * 
 */
public class StTestConfig extends ChuyuFamily {
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS
	////////////////////////////////////////////////////////////////////////////

	private static StTestConfig _ins = null;
	public static StTestConfig getInstance(){
		if(null == _ins){
			_ins = new StTestConfig();
		}
		return _ins;
	}
	private static final String buddleName = "st_gen_lib_client_test";
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Members
	////////////////////////////////////////////////////////////////////////////
	
	//private String log4jCfg;
	private String stunSrvIp;
	private String stunSrvPort;
	private String stun2SrvIp;
	private String stun2SrvPort;
	private String iceSrvIp;
	private String iceSrvPort;
	
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Methods
	////////////////////////////////////////////////////////////////////////////

	
	/**
	 * Constructor. <br/>
	 * 
	 */
	private StTestConfig(){
	    ////// load config file
		System.out.println("Loading configurations from buddle '" + buddleName +"'");
		ResourceBundle res = ResourceBundle.getBundle(buddleName,Locale.getDefault());
		this.stunSrvIp 		= res.getString("stun_srv_ip");
		this.stunSrvPort 	= res.getString("stun_srv_port");
		this.stun2SrvIp 	= res.getString("stun2_srv_ip");
		this.stun2SrvPort 	= res.getString("stun2_srv_port");
		this.iceSrvIp 		= res.getString("ice_srv_ip");
		this.iceSrvPort 	= res.getString("ice_srv_port");
		
		System.out.println("[HA Client] Configurations for Smoke Test: \n" + this.toStringXml("#### ") );
	}
	
	public String getStunSrvIp() {
		return stunSrvIp;
	}
	public int getStunSrvPort() {
		return Integer.parseInt(stunSrvPort);
	}
	public String getStun2SrvIp() {
		return stun2SrvIp;
	}
	public int getStun2SrvPort() {
		return Integer.parseInt(stun2SrvPort);
	}

	public String getIceSrvIp() {
		return iceSrvIp;
	}
	public int getIceSrvPort() {
		return Integer.parseInt(iceSrvPort);
	}
}


