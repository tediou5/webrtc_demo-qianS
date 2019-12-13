package cn.teclub.ha.test;

import cn.teclub.ha.client.StcException.ExpLocalClientOffline;
import cn.teclub.ha.client.rpr.StcReqSrvSignout;
import cn.teclub.ha.lib.StExpFamily;


/**
 * <h1> Test Case: Client logs in and logs out. </h1>
 * 
 * @author mancook
 *
 */
public class ClientSignout extends ClientDriver {
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	

	
	public static void main(String[] args) throws StExpFamily {
		if(args.length != 3){
			System.out.println("Usage: java <cmd> <name> <passwd> <home_dir> \n");
			System.exit(-1);
		}
		String clt_name = args[0];
		String clt_passwd = args[1];
		String clt_home_dir = args[2];
		new ClientSignout(clt_name, clt_passwd, clt_home_dir);
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
	 * @throws ExpLocalClientOffline 
	 */
	ClientSignout( 
			final String name, 
			final String passwd, 
			final String home_dir
			) throws StExpFamily 
	{
		initParams.homeDir = home_dir;
		TestClientObj.initialize(this.initParams, null);
		this.haClient = TestClientObj.getInstance();
		
		loginServer(name, passwd);
		
		StcReqSrvSignout req = new StcReqSrvSignout(name, passwd);
		req.startRequest();
		req.waitForResult();
		
		
		sleepSecond(10);
		System.out.println("\n======== Client logs out  ========\n");
		// haClient.logout();
		haClient.destroy();
		
		//		System.out.println("\n******** client signs out   ********\n");
		//		haClient.signOut(null);
		//		util.sleep(5000);
		//		haClient.clearup();
	}

}
