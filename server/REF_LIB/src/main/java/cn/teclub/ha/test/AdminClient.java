package cn.teclub.ha.test;

import java.nio.ByteBuffer;
import java.util.Scanner;

import cn.teclub.ha.client.StcException.ExpLocalClientOffline;
import cn.teclub.ha.client.rpr.StcExpRpr;
import cn.teclub.ha.client.rpr.StcReqSrvAdminGetInfo;
import cn.teclub.ha.lib.StConst;
import cn.teclub.ha.net.StExpNet;
import cn.teclub.ha.request.StNetPacket.Code;




/**
 * <h1> Test Case: Client logs in and logs out. </h1>
 * 
 * @author mancook
 *
 */
public class AdminClient extends ClientDriver 
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
		new AdminClient(clt_name, clt_passwd, home_dir);
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
	private AdminClient(final String name, final String passwd, final String home_dir) 
			throws StExpNet 
	{
		super("st_admin_client");
		initParams.homeDir = home_dir;
		TestClientObj.initialize(initParams);
		this.haClient = TestClientObj.getInstance();
		
		loginServer(name, passwd);
		message("==== Client '"+ name  +"' logs in, successfully ^_^ ====");
		message("Lib Version: " + StConst.getVersionInfo() );
		
		consoleMainLoop();
		
		logoutServer();
	}
	
	
	
	private void consoleMainLoop() {
        this.stLog.info("Start Main Loop. You can enter console command.");
        message("Enter console command ... \n");
        Scanner input = new Scanner(System.in);
        String str = "" ;
        
        while(true){
            System.out.print(">> ");
            str = input.nextLine().trim();
            
            if(str.equalsIgnoreCase("quit")) {
                break ;
            }
            if(str.trim().length() ==0){
                continue;
            }
            
            try {
                consoleOneLoop(str);
            } catch (StExpNet e) {
                this.stLog.warn(util.getExceptionDetails(e, ""));
            }
            
            util.sleep(500);
        }//while
        input.close();
        
        message("\nAdmin client stops. Bye! \n");
	}
	
	
	
	private void consoleUsage(){
        String helpMsg =   
        		  "============================================================== \n"
                + "S.T. Administration Client  \n" 
                + "  \n"
                + "This client queries info from remote server. \n"
                + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n"
                + "Usage: \n" 
                + "~~~~~~~~ \n"
                + " \n"
                + " \n"
	            + "conn <name>     Print connection statistics  \n"
	            + " \n"
                + "help            Show this help message.\n"
                + " \n"
                + "quit            Exit this program.\n"
                + " \n"
                + "dump all | <clt_id>     \n" 
                +"                 all: dump all clients in client-manager. \n"
                +"                 <clt_id>: dump specific client info. \n"
                + " \n"
                + "show count|cache|online|conn_grp|sum|pp    \n" 
                +"                 count: show online/cached client count. \n"
                +"                 cache: show cached clients. \n"
                +"                 online: show online clients. \n"
                +"                 conn_grp: show conn_grp. \n"
                +"                 sum: client summary.  \n"
                +"                 pp: Pulse Pool.  \n"
                + " \n"
                + "version [srv]   Show [server] Version Number.\n"
                + " \n"
                + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n"
                + "";
        message(helpMsg);
	}
	    
	
    private void consoleOneLoop(String str_cmd) throws StExpNet {
    	if(str_cmd.equalsIgnoreCase("help")){
    		consoleUsage();
    		return;
    	}
    	
    	if(str_cmd.equalsIgnoreCase("version")){
    		message("Client Version: " + StConst.getVersionInfo() );
    		return;
    	}
    	
    	if(str_cmd.equalsIgnoreCase("version srv")){
    		message("Show server version \n");
     		message(adminGetInfo(Code.AdminGetInfo.REQUEST_VERSION_SRV, null) );
    		return;
    	}
    	
    	if(str_cmd.equalsIgnoreCase("show cache")){
    		message("Show Cached clients \n");
    		message(adminGetInfo(Code.AdminGetInfo.REQUEST_CACHE_CLIENT, null) );
    		return;
    	}
    	
    	if(str_cmd.equalsIgnoreCase("show online")){
    		message("Show ONLINE clients \n");
    		message(adminGetInfo(Code.AdminGetInfo.REQUEST_ONLINE_CLIENT, null) );
    		return;
    	}
    	
    	if(str_cmd.equalsIgnoreCase("show conn_grp")){
    		message("Show all connection groups \n");
    		message(adminGetInfo(Code.AdminGetInfo.REQUEST_CONN_GRP, null) );
    		return;
    	}
    	
    	if(str_cmd.equalsIgnoreCase("show sum")){
    		message("Show summary of clients \n");
       		message(adminGetInfo(Code.AdminGetInfo.REQUEST_SUM_CLIENT, null) );
    		return;
    	}
    	
    	if(str_cmd.equalsIgnoreCase("show count")){
    		message("Show ONLINE/CACHED clients count \n");
       		message(adminGetInfo(Code.AdminGetInfo.REQUEST_COUNT_CLIENT, null) );
    		return;
    	}
    	
    	if(str_cmd.equalsIgnoreCase("dump all")){
    		message("DUMP ALL clients in client-manager. \n");
       		message(adminGetInfo(Code.AdminGetInfo.REQUEST_DUMP_ALL, null) );
    		return;
    	}
    	
    	if(str_cmd.startsWith("dump clt") && str_cmd.length() > 8){
    		String clt_name = str_cmd.substring(8).trim();
    		message("DUMP client: '" + clt_name + "' ... \n");
       		message(adminGetInfo(Code.AdminGetInfo.REQUEST_DUMP_CLIENT, util.stringFunc.toBuffer(clt_name) ) );
    		return;
    	}
    	
    	
    	
		// ---------------------------------------------------------------------
		// commands with argument
		// ---------------------------------------------------------------------
		String[] cmds = str_cmd.split("\\s+");
		
		if(cmds[0].equalsIgnoreCase("show")){
			if(cmds[1] == null || cmds[1].length() == 0) {
				return;
			}
			if(cmds[1].equals("pp")){
				message("Show Pulse Pool...");
				message(adminGetInfo(Code.AdminGetInfo.REQUEST_PULSE_POOL, null ) );
			}
			return;
		}
		
		
		
		if(cmds[0].equalsIgnoreCase("conn")){
			if(cmds[1] == null || cmds[1].length() == 0) {
				return;
			}
			message("Show Connection of: " + cmds[1]);
			message(adminGetInfo(Code.AdminGetInfo.REQUEST_CONNECTION, util.stringFunc.toBuffer(cmds[1]) ) );
			return;
		}
		
		message("ERROR: unknonw command: '" + str_cmd + "'");
    }
    
    
	private String adminGetInfo(byte code, ByteBuffer data) throws ExpLocalClientOffline, StcExpRpr.ExpReqTimeout {
		StcReqSrvAdminGetInfo req = new StcReqSrvAdminGetInfo(code, data);
		req.startRequest();
		final Object res = req.waitForResult();
		if(req.isResAllowed()){
			return (String) res;
		}
		return null;
	}

}
