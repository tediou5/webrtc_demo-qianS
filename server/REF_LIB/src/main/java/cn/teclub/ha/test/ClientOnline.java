package cn.teclub.ha.test;

import cn.teclub.ha.client.StcException.ExpPublicAddrFail;
import cn.teclub.ha.lib.StExpFamily;


/**
 * <h1> Test Case: Client logs in and keeps online. </h1>
 * 
 * @author mancook
 *
 */
public class ClientOnline extends ClientDriver 
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	

	
	public static void main(String[] args) throws StExpFamily {
		if(args.length != 3 && args.length != 4 ){
			System.out.println("Usage: java <cmd> <name> <passwd> <homeDir> [buddle] \n");
			System.exit(-1);
		}
		String clt_name = args[0];
		String clt_passwd = args[1];
		String home_dir = args[2];
		String buddle_name = "st_client_online";
		if(args.length == 4){
			buddle_name = args[3];
		}
		new ClientOnline(clt_name, clt_passwd, home_dir, buddle_name);
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
	ClientOnline(
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
		System.out.println("HA Client '"+ name  +"' logs in, successfully ^_^ ");
	}
}

