package cn.teclub.ha.test;

import cn.teclub.ha.client.StcException.ExpPublicAddrFail;
import cn.teclub.ha.lib.StConst;
import cn.teclub.ha.net.StExpNet;


/**
 * <h1> Test Case: Client logs in and logs out. </h1>
 * 
 * @author mancook
 *
 */
public class ClientResetPasswd extends ClientDriver 
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	
	
	public static void main(String[] args) throws Exception{
		if(args.length != 3){
			System.out.println("Usage: java <cmd> <name> <passwd> <home-dir> \n");
			System.out.println("");
			System.out.println("Example: <CMD> user00 abcD1234  test-suite/fake-dev/__user00/sdcard/AA-FAMBO   \n");

			System.exit(-1);
		}
		final String clt_name = args[0];
		final String clt_passwd = args[1];
		final String home_dir = args[2];
		new ClientResetPasswd(clt_name, clt_passwd, home_dir);
	}
	
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	


	////////////////////////////////////////////////////////////////////////////
    // Instance Methods
	////////////////////////////////////////////////////////////////////////////	

	/**
	 * Constructor
	 * 
	 * @param name  - must be a phone number. e.g. 18964895454
	 * @param passwd
	 * @param home_dir
	 * @throws ExpPublicAddrFail 
	 */
	ClientResetPasswd(final String name, final String passwd, final String home_dir) 
			throws StExpNet 
	{
		System.out.println("\n\n******** Client logs in  ********\n\n");
		initParams.homeDir = home_dir;
		TestClientObj.initialize(initParams);
		this.haClient = TestClientObj.getInstance();
		
		//final String PHONE = "18964895454";
		final String NEW_PASSWD = "ABCD1234";
		final String OLD_PASSWD = "asdf1234";
		
		message("**** [1] reset passwd ****");
		haClient.resetPasswd(name, NEW_PASSWD, "000000");
		sleepSecond(5);
		
		
		message("**** [2] logins with new passwd '"+ NEW_PASSWD +"' ****");
		loginServer(":ph:" + name, NEW_PASSWD);
		stLog.info("Client '"+ name  +"' logs in, successfully ^_^ ");
		stLog.info(util.testMilestoneLog("Lib Version: " + StConst.getVersionInfo() ) );
		sleepSecond(5);
		
		message("**** [3] logs out ****");
		haClient.logout();
		sleepSecond(5);
		
		
		message("**** [4] reset passwd back ****");
		haClient.resetPasswd(name, OLD_PASSWD, "000000");
		sleepSecond(5);
		
		
		
		
		
		
		
		haClient.destroy();
	}
}
