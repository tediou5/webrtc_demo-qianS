package cn.teclub.ha.test;

import cn.teclub.ha.client.rpr.StcReqSrvSignup;
import cn.teclub.ha.lib.StExpUserError;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.net.StExpNet;
import cn.teclub.ha.net.StExpNet.StExpSessionTimeout;
import cn.teclub.ha.request.StNetPacket;



/**
 * <h1> Test Case: client signs up. </h1>
 * 
 * @author mancook
 *
 */
public class ClientSignup extends ClientDriver {
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	

	
	public static void main(String[] args) throws StExpUserError, StExpSessionTimeout, StExpNet{
		if(args.length != 5){
			System.out.println("Usage: java <cmd> <name> <passwd> <label> <phone> <home_dir>\n");
			System.exit(-1);
		}
		String clt_name   = args[0];
		String clt_passwd = args[1];
		String clt_label  = args[2];
		String clt_phone  = args[3];
		String home_dir	  = args[4];
		new ClientSignup(clt_name, clt_passwd, clt_label, clt_phone, home_dir);
	}
	
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	

	////////////////////////////////////////////////////////////////////////////
    // Instance Methods
	////////////////////////////////////////////////////////////////////////////	
	
	/**
	 * <h2> constructor </h2>
	 * @throws StExpSessionTimeout 
	 * @throws StExpNet
	 * @throws StExpUserError 
	 */
	ClientSignup ( 
			final String name, 
			final String passwd, 
			final String label, 
			final String phone, 
			final String home_dir
			) throws StExpSessionTimeout, StExpNet, StExpUserError 
	{
		this.initParams.homeDir = home_dir;
		TestClientObj.initialize(this.initParams, null);
		
		this.haClient = TestClientObj.getInstance();
		
		/**
		 * Verify Code '000000' will always OK
		 */
		final StcReqSrvSignup req_singup = new StcReqSrvSignup(
				StNetPacket.Code.Signup.REQUST_USER, 
				name, passwd, label, phone, "000000", null, 3000);
		req_singup.startRequest();
		final StClientInfo ci = (StClientInfo)req_singup.waitForResult();
		util.assertTrue(req_singup.getResCode() != StNetPacket.Code.Signup.ALLOW_USE_OLD, "Client has already signed up: " + ci.dumpSimple());
		sleepSecond(10);
		
		
		//		System.out.println("\n******** Login After Signup  ********\n");
		//		final StcReqSrvLogin req_login = new StcReqSrvLogin(name, passwd, "Login after Signup");
		//		req_login.startRequest();
		//		final StClientInfo local = (StClientInfo) req_login.waitForResult();
		//		stLog.info(util.testMilestoneLog("Client Info after Login: " + local));
		//		sleepSecond(10);
		//		
		//		
		//		System.out.println("\n======== Client logs out  ========\n");
		//		haClient.logout();
		//		haClient.destroy();
	}
}
