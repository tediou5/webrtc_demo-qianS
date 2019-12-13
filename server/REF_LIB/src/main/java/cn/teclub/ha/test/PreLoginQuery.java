package cn.teclub.ha.test;

import java.nio.ByteBuffer;

import cn.teclub.ha.client.StcException.ExpPublicAddrFail;
import cn.teclub.ha.client.rpr.StcReqSrvPreLoginQuery;
import cn.teclub.ha.lib.StExpFamily;
import cn.teclub.ha.request.StNetPacket;


/**
 * <h1> Test Case: Client logs in and keeps online. </h1>
 * 
 * @author mancook
 *
 */
public class PreLoginQuery extends ClientDriver 
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	

	
	public static void main(String[] args) throws StExpFamily {
		if(args.length != 2 ){
			System.out.println("Usage: java <cmd> <homeDir> <macAddress> \n");
			System.exit(-1);
		}
		//String clt_name = args[0];
		//String clt_passwd = args[1];
		/*
		String buddle_name = "st_client_online";
		if(args.length == 2){
			buddle_name = args[1];
		}
		*/
		new PreLoginQuery(args[0], args[1]);
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
	PreLoginQuery(
			final String home_dir, 
			final String mac_addr
			) throws StExpFamily
	{
		initParams.homeDir = home_dir;
		TestClientObj.initialize(this.initParams, null);
		this.haClient = TestClientObj.getInstance();
		//loginServer(name, passwd);
		//System.out.println("HA Client '"+ name  +"' logs in, successfully ^_^ ");
		
		final ByteBuffer data = util.stringFunc.toBuffer(mac_addr);
		final StcReqSrvPreLoginQuery req = new StcReqSrvPreLoginQuery(StNetPacket.Code.PreLoginQuery.QUERY_NAME_BY_MAC, data);
		req.startRequest();
		final String clt_name = (String) req.waitForResult();
		message("Client Name: " + clt_name);
		
		sleepSecond(10);
		message("\n======== Client Destroys  ========\n");
		haClient.destroy();
	}
}

