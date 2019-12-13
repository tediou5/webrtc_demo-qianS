package cn.teclub.ha.test;

import java.util.ArrayList;

import cn.teclub.ha.client.rpr.StRemoteFile;
import cn.teclub.ha.lib.StErrUserError;
import cn.teclub.ha.lib.StExpFamily;
import cn.teclub.ha.net.StClientInfo;


/**
 * <h1> Test Case: Client logs in and logs out. </h1>
 * 
 * @author mancook
 *
 */
public class ClientDownloadFile extends ClientDriver 
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	
	
	public static void main(String[] args) throws Exception{
		if(args.length != 6){
			System.out.println("Usage: java <cmd> <name> <passwd> <home-dir> <date> <hour> <remote> \n");
			System.out.println("");
			System.out.println("Example: <CMD> user00 abcD1234  test-suite/fake-dev/__user00/sdcard/AA-FAMBO  2016-2-3 10 gw04  \n");

			System.exit(-1);
		}
		new ClientDownloadFile(args);
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
	 * @param HDL_NAME
	 * @param passwd
	 * @param home_dir
	 * @throws StExpFamily 
	 */
	ClientDownloadFile(final String[] args) 
			throws StExpFamily 
	{
		super();
		String name 	= args[0];
		String passwd 	= args[1];
		String home_dir	= args[2];
		String[] date_str 		= args[3].split("-");
		int hour				= util.numberFunc.parseInt(args[4]);
		String remote_gw_name 	= args[5];
		
		initParams.homeDir = home_dir;
		TestClientObj.initialize(this.initParams, null);
		this.haClient = TestClientObj.getInstance();
	
		if(date_str.length != 3){
			throw new StErrUserError("DATE Argument Error: " + args[2]);
		}
		int year 	= util.numberFunc.parseInt(date_str[0]);
		int month 	= util.numberFunc.parseInt(date_str[1]);
		int day 	= util.numberFunc.parseInt(date_str[2]);
		
		loginServer(name, passwd);
		
		////////////////////////////////////////////////////////////////////////
		runTest(year, month, day, hour, remote_gw_name, home_dir);
		////////////////////////////////////////////////////////////////////////

		logoutServer();
	}
	
	
	void runTest(int year, int month, int day, int hour, String r_clt_name, String home_dir)
			throws StExpFamily
	{
		StClientInfo online_gw = null;
		ArrayList<StClientInfo> f_list = haClient.info.getFriendList();
		for(StClientInfo f: f_list){
			if(f.isFlag_Online() && f.isFlag_Gateway() && f.getName().equals(r_clt_name)){
				online_gw = f;
			}
		}
		if(online_gw == null){
			stLog.warn("\n\t !!!!  WARN: Friend "+ r_clt_name +" is not found or OFFLINE! Do nothing !!!!");
			return;
		}
		
		int ping_cost = haClient.pingRemote(online_gw);
		stLog.info("Ping Cost(ms): " + ping_cost );
		util.testMilestoneLog("Ping Success to Gateway: " + online_gw );
		
		final StRemoteFile rf = new StRemoteFile("video/local/date_2016-03-04/TimeLapse_20160304_072000.mp4", null);
		final String local_dir = "video/remote/"+ online_gw.getName() +"/date_2016-03-04/";
		
		message("#### Download RF: " + rf.dump());
		haClient.downloadFile(online_gw, 20*1000, rf, local_dir);
		util.testMilestoneLog("Download Success: " + rf.dump());
		
		message("#### Download RF again: " + rf.dump());
		haClient.downloadFile(online_gw, 20*1000, rf, local_dir);
		util.testMilestoneLog("Download Success: " + rf.dump());
	}
}
