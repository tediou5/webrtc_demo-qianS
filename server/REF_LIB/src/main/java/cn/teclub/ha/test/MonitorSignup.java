package cn.teclub.ha.test;

import cn.teclub.ha.client.rpr.StcReqSrvSignout;
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
public class MonitorSignup extends ClientDriver {
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	

	
	public static void main(String[] args) throws StExpUserError, StExpSessionTimeout, StExpNet{
		if(args.length != 1){
			System.out.println("Usage: java <cmd>  <home_dir>\n");
			System.exit(-1);
		}
		String home_dir   = args[0];
		new MonitorSignup(home_dir);
	}
	
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	

	////////////////////////////////////////////////////////////////////////////
    // Instance Methods
	////////////////////////////////////////////////////////////////////////////	
	
	
	MonitorSignup (  final String home_dir ) 
			throws StExpSessionTimeout, StExpNet, StExpUserError 
	{
		this.initParams.homeDir = home_dir;
		TestClientObj.initialize(this.initParams);
		
		this.haClient = TestClientObj.getInstance();
		
		message("==== [1] Signup ... ");
		/*
		 * Verify Code '000000' will always OK
		 */
		final StcReqSrvSignup req_singup = new StcReqSrvSignup(
				StNetPacket.Code.Signup.REQUST_MONITOR, 
				"TT01", "<password>", "<label>", "<phone01>", "000000", "40-6c-8f-53-8f-c5", 3000);
		req_singup.startRequest();
		final StClientInfo ci = (StClientInfo)req_singup.waitForResult();
		util.assertTrue(req_singup.getResCode() != StNetPacket.Code.Signup.ALLOW_USE_OLD, "Client has already signed up: " + ci.dumpSimple());
		message("sign up success!");
	
		
		message("==== [2] After Signup  ");
		stLog.info(util.testMilestoneLog("Client Info after signup: " + ci));
		sleepSecond(10);
		
		
		message("==== [3] logs out ");
		haClient.logout();
		sleepSecond(5);
		
		
		message("==== [4] logs in again ");
		final String mon_name = ci.getName();
		final String mon_pass = "1234ABCD";
		loginServer(mon_name, mon_pass);
		
		message("==== [5] sign out ");
		StcReqSrvSignout req_signout = new StcReqSrvSignout(mon_name, mon_pass);
		req_signout.startRequest();
		req_signout.waitForResult();
		
		
		sleepSecond(10);
		message("==== [6/6] destroy client object! ");
		haClient.destroy();
	}
}
