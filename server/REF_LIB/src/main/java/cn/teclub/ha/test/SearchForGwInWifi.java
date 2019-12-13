package cn.teclub.ha.test;

import java.util.ArrayList;

import cn.teclub.ha.client.StcException.ExpLocalClientOffline;
import cn.teclub.ha.client.StcException.ExpRemoteClientNoFound;
import cn.teclub.ha.client.rpr.StcExpRpr;
import cn.teclub.ha.client.rpr.StcReqSrv;
import cn.teclub.ha.client.rpr.StcReqSrvQueryGwInWifi;
import cn.teclub.ha.lib.StConst;
import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.net.StExpNet;


/**
 * <h1> Test Case: Client logs in and logs out. </h1>
 * 
 * @author mancook
 *
 */
public class SearchForGwInWifi extends ClientDriver 
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	
	
	public static void main(String[] args) throws Exception{
		if(args.length !=3){
			System.out.println("Usage: java <cmd> <name> <passwd> <home-dir> \n");
			System.out.println("");
			System.out.println("Example: <CMD> user00 abcD1234  test-suite/fake-dev/__user00/sdcard/AA-FAMBO   \n");

			System.exit(-1);
		}
		final String clt_name 	= args[0];
		final String clt_passwd = args[1];
		final String home_dir 	= args[2];
		new SearchForGwInWifi(clt_name, clt_passwd, home_dir);
	}
	
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	


	////////////////////////////////////////////////////////////////////////////
    // Instance Methods
	////////////////////////////////////////////////////////////////////////////	

	/**
	 * 
	 * @param name
	 * @param passwd
	 * @param home_dir
	 * @throws StExpNet
	 */
	SearchForGwInWifi(final String name, final String passwd, final String home_dir) 
			throws StExpNet 
	{
		System.out.println("\n\n******** Client logs in  ********\n\n");
		initParams.homeDir = home_dir;
		TestClientObj.initialize(initParams, null);
		this.haClient = TestClientObj.getInstance();
		
		loginServer(name, passwd);
		message("Client '"+ name  +"' logs in, successfully ^_^ ");
		message("Lib Version: " + StConst.getVersionInfo() );
		
		runTest();
		
		message("\n\n******** Client logs out  ********\n\n");
		haClient.logout();
		haClient.destroy();
	}
	
	
	private void runTest() throws ExpLocalClientOffline, StcExpRpr.ExpReqTimeout {
		message("[1] Search GW in WIFI... ");
		sleepSecond(5);
		
		final StcReqSrv req = new StcReqSrvQueryGwInWifi();
		req.startRequest();
		Object result = req.waitForResult();
		if(!req.isResAllowed()){
			message("ERR: Query GW Failure!");
			return;
		}
		@SuppressWarnings("unchecked")
		final ArrayList<StClientInfo>  gw_list = (ArrayList<StClientInfo>) result;
		
		for(StClientInfo gw: gw_list){
			message("Find GW: " + gw);
		}
		
		message(util.testMilestoneLog("DONE! Search WIFI GW Success!" ));
		sleepSecond(5);
	}
	
	
	
	StClientInfo getFriend(final StClientID id){
		try {
			return haClient.info.getFriend(id);
		} catch (ExpRemoteClientNoFound e) {
		}
		return null;
	}
	
}
