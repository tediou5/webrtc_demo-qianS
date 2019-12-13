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
public class ClientLogin extends ClientDriver 
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
		new ClientLogin(clt_name, clt_passwd, home_dir);
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
	 * @param name
	 * @param passwd
	 * @param home_dir
	 * @throws ExpPublicAddrFail 
	 */
	ClientLogin(final String name, final String passwd, final String home_dir) 
			throws StExpNet 
	{
		System.out.println("\n\n******** Client logs in  ********\n\n");
		initParams.homeDir = home_dir;
		TestClientObj.initialize(initParams);
		this.haClient = TestClientObj.getInstance();
		
		loginServer(name, passwd);
		stLog.info("Client '"+ name  +"' logs in, successfully ^_^ ");
		stLog.info(util.testMilestoneLog("Lib Version: " + StConst.getVersionInfo() ) );
		
		stLog.info("Sleep for a while");
		sleepSecond(10);
		
		System.out.println("\n\n******** Client logs out  ********\n\n");
		haClient.logout();
		haClient.destroy();
	}
}
