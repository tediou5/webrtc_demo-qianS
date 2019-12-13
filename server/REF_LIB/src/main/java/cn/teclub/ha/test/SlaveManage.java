package cn.teclub.ha.test;

import java.util.ArrayList;

import cn.teclub.ha.client.StcException.ExpPublicAddrFail;
import cn.teclub.ha.client.rpr.StcReqSrvSlaveManage;
import cn.teclub.ha.lib.StConst;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.net.StExpNet;
import cn.teclub.ha.net.StFriend;


/**
 * <h1> Test Case: Client logs in and logs out. </h1>
 * 
 * @author mancook
 *
 */
public class SlaveManage extends ClientDriver 
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
		new SlaveManage(clt_name, clt_passwd, home_dir);
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
	SlaveManage(final String name, final String passwd, final String home_dir) 
			throws StExpNet 
	{
		message("\n\n******** [1] Client logs in  ********\n\n");
		initParams.homeDir = home_dir;
		TestClientObj.initialize(initParams);
		this.haClient = TestClientObj.getInstance();
		
		loginServer(name, passwd);
		stLog.info("Client '"+ name  +"' logs in, successfully ^_^ ");
		stLog.info(util.testMilestoneLog("Lib Version: " + StConst.getVersionInfo()) );
		sleepSecond(5);
		
		
		
		message("\n\n******** [2] get slave  ********");
		StFriend slave_friend = null;
		ArrayList<StFriend> list = this.haClient.getRprInfo().getGatewayList(true);
		for(StFriend f: list){
			if(f.isSlave){
				slave_friend = f;
				break;
			}
		}
		
		message("\n\n******** [3] edit slave info  ********");
		
		if(slave_friend != null){
			stLog.info("find slave: " + slave_friend );
	
			final StClientInfo slave_ci = haClient.getRprInfo().getFriend(slave_friend.getClientID());
			final String old_label = slave_ci.getLabel();
			final String old_dscp = slave_ci.getDscp();
			
			stLog.info("[3.1] original slave info: " + slave_ci );
			slave_ci.setLabel("update label [318]");
			slave_ci.setDscp("update dscp [318]" );
			
			stLog.info("[3.2] update slave info... " );
			StcReqSrvSlaveManage req = new StcReqSrvSlaveManage(slave_ci);
			req.startRequest();
			req.waitForResult();
			
			final StClientInfo new_slave_ci = haClient.getRprInfo().getFriend(slave_friend.getClientID());
			stLog.info("[3.3] updated slave: " + new_slave_ci );
			sleepSecond(3);
			
			
			stLog.info("[3.4] restore slave info ... ");
			new_slave_ci.setLabel(old_label);
			new_slave_ci.setDscp(old_dscp );
			req = new StcReqSrvSlaveManage(new_slave_ci);
			req.startRequest();
			req.waitForResult();
			
			final StClientInfo new_slave_ci2 = haClient.getRprInfo().getFriend(slave_friend.getClientID());
			stLog.info("[3.5] restored slave: " + new_slave_ci2 );
			sleepSecond(2);
		}else{
			stLog.error("no slave!");
		}
		
		
		message("\n\n******** [*] Client logs out  ********");
		haClient.logout();
		haClient.destroy();
	}
}
