package cn.teclub.ha.test;

import cn.teclub.ha.client.StcException.ExpRemoteClientNoFound;
import cn.teclub.ha.client.app.StcExpApp.AddContactFailure;
import cn.teclub.ha.client.app.StcExpApp.DelContactFailure;
import cn.teclub.ha.client.app.StcExpApp.SearchContactFailure;
import cn.teclub.ha.client.rpr.StcExpRpr;
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
public class SearchAddDelContact extends ClientDriver 
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	
	
	public static void main(String[] args) throws Exception{
		if(args.length != 4){
			System.out.println("Usage: java <cmd> <name> <passwd> <home-dir> <search-item> \n");
			System.out.println("");
			System.out.println("Example: <CMD> user03 abcd1234  test-suite/fake-dev/__user00/sdcard/AA-FAMBO gw04   \n");

			System.exit(-1);
		}
		final String clt_name 	= args[0];
		final String clt_passwd = args[1];
		final String home_dir 	= args[2];
		new SearchAddDelContact(clt_name, clt_passwd, home_dir, args[3]);
	}
	
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	


	////////////////////////////////////////////////////////////////////////////
    // Instance Methods
	////////////////////////////////////////////////////////////////////////////	

	/**
	 * 
	 * 
	 * 
	 * @param name
	 * @param passwd
	 * @param home_dir
	 * @param search_item
	 * @throws StExpNet
	 */
	SearchAddDelContact(final String name, final String passwd, final String home_dir, final String search_item) 
			throws StExpNet 
	{
		System.out.println("\n\n******** Client logs in  ********\n\n");
		initParams.homeDir = home_dir;
		TestClientObj.initialize(initParams, null);
		this.haClient = TestClientObj.getInstance();
		
		loginServer(name, passwd);
		message("Client '"+ name  +"' logs in, successfully ^_^ ");
		message("Lib Version: " + StConst.getVersionInfo() );
		
		runTest(search_item);
		
		message("\n\n******** Client logs out  ********\n\n");
		haClient.logout();
		haClient.destroy();
	}
	
	
	private void runTest(final String search_item)
			throws SearchContactFailure, AddContactFailure, DelContactFailure, StcExpRpr.ExpReqTimeout {
		message("[1] Search client by " + util.stringFunc.wrap(search_item));
		sleepSecond(5);
		
		final StClientInfo ci2 = haClient.searchContact(search_item);
		if(ci2 == null){
			message(util.testMilestoneLog("NO Contact is found by '" + search_item + "'"));
			return;
		}
		message(util.testMilestoneLog("Find Contact: "  + ci2.dump()));
		
		
		message("[2] Try to add contact: " + ci2 );
		final StClientID id2 =  ci2.getClientID();
		StClientInfo f_ci2 = getFriend(id2);
		if(f_ci2 == null){
			message("This is NOT friend: " + ci2);
			haClient.addContact(id2);
		}else{
			message("Client already has friend: " + f_ci2.dump());
		}
		
		f_ci2 = getFriend(id2);
		util.assertNotNull(f_ci2);
		message(util.testMilestoneLog("Added Contact: "  + f_ci2 ));
		
		
		message("[3] Delete Contact: " + f_ci2 );
		haClient.deleteContact(id2);
		
		f_ci2 = getFriend(id2);
		util.assertTrue(f_ci2 == null);
		
		message(util.testMilestoneLog("[3/3] Search/Add/Delete Success!" ));
	}
	
	
	
	StClientInfo getFriend(final StClientID id){
		try {
			return haClient.info.getFriend(id);
		} catch (ExpRemoteClientNoFound e) {
		}
		return null;
	}
	
}
