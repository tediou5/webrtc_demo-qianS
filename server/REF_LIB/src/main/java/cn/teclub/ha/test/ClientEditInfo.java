package cn.teclub.ha.test;

import cn.teclub.ha.client.StcException.ExpPublicAddrFail;
import cn.teclub.ha.lib.StExpFamily;


/**
 * <h1> Test Case: Client logs in and keeps online. </h1>
 * 
 * @author mancook
 *
 */
public class ClientEditInfo extends ClientDriver 
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	

	
	public static void main(String[] args) throws StExpFamily {
		if(args.length != 3 ){
			System.out.println("Usage: java <cmd> <name> <passwd> <homeDir> \n");
			System.exit(-1);
		}
		String clt_name = args[0];
		String clt_passwd = args[1];
		String home_dir = args[2];
		new ClientEditInfo(clt_name, clt_passwd, home_dir);
	}
	
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	

	////////////////////////////////////////////////////////////////////////////
    // Instance Methods
	////////////////////////////////////////////////////////////////////////////	

	
	/**
	 * <h2> constructor </h2>
	 * 
	 * @throws ExpPublicAddrFail 
	 */
	ClientEditInfo(
			final String name, 
			final String passwd, 
			final String home_dir
			) throws StExpFamily
	{
		initParams.homeDir = home_dir;
		TestClientObj.initialize(this.initParams, null);
		this.haClient = TestClientObj.getInstance();
		loginServer(name, passwd);
		System.out.println("HA Client '"+ name  +"' logs in, successfully ^_^ ");
		
		final String pre_label = haClient.info.getLabel();
		haClient.updateLabel(pre_label + "_new");
		message("wait enough time for client updated");
		sleepSecond(5);
		message("Updated Client Label: " + haClient.info.getLabel());
		
		haClient.updateLabel(pre_label);
		message("wait enough time for client updated");
		sleepSecond(5);
		message("Updated Client Label: " + haClient.info.getLabel());
	}
}

