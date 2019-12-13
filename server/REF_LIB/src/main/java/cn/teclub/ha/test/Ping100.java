package cn.teclub.ha.test;

import java.util.ArrayList;

import cn.teclub.ha.client.StcException.ExpPublicAddrFail;
import cn.teclub.ha.lib.StExpFamily;
import cn.teclub.ha.net.StClientInfo;


/**
 * <h1> Test Case: Client logs in and keeps online. </h1>
 * 
 * @author mancook
 *
 */
public class Ping100 
		extends ClientDriver 
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	
	

	public static void main(String[] args) 
	{
		if(args.length != 3 && args.length != 4 ){
			System.out.println("Usage: java <cmd> <name> <passwd> <homeDir> [buddle] \n");
			System.exit(-1);
		}
		String clt_name = args[0];
		String clt_passwd = args[1];
		String home_dir = args[2];
		String buddle_name = "st_test";
		if(args.length == 4){
			buddle_name = args[3];
		}
		try{
			final long ms_start = System.currentTimeMillis();
			new Ping100(clt_name, clt_passwd, home_dir, buddle_name);
			final long ms_cost = System.currentTimeMillis() - ms_start;
			
			System.out.println("Test Case Cost: " + ms_cost + " ms");
		}catch(StExpFamily e){
			System.out.println("ERROR: Test Failure!");
			e.printStackTrace();
			sleepSecond(10);
			System.exit(-1);
		}
	}
	
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	

	////////////////////////////////////////////////////////////////////////////
    // Instance Methods
	////////////////////////////////////////////////////////////////////////////	

	private final int PING_COUNT = 100;
	//private final int PING_COUNT = 30;
	
	
	/**
	 * <h2> constructor </h2>
	 * 
	 * @throws ExpPublicAddrFail 
	 */
	Ping100(
			final String name, 
			final String passwd, 
			final String home_dir, 
			final String buddle_name
			) throws StExpFamily
	{
		super(buddle_name);
		initParams.homeDir = home_dir;
		TestClientObj.initialize(this.initParams, null);
		this.haClient = TestClientObj.getInstance();
		loginServer(name, passwd);
		
		StClientInfo online_friend = null;
		ArrayList<StClientInfo> list = haClient.info.getFriendList();
		for(StClientInfo ci : list){
			if(ci.isFlag_Online() && ci.getName().contains("gw04")){
				online_friend = ci;
				break;
			}
		}
		util.assertNotNull(online_friend);
		System.out.println(util.testMilestoneLog("==== Start Ping Friend: " + online_friend ));
		
		int cost_ms = 0;
		for(int i=0; i<PING_COUNT; i++) {
			cost_ms = haClient.pingRemote(online_friend);
			System.out.println(util.testMilestoneLog("[" + i +"] Ping "+ online_friend  +" Cost: " + cost_ms +" ms" ));
		}

		System.out.println("\n\n******** Client logs out  ********\n\n");
		haClient.logout();
		haClient.destroy();
	}
}

