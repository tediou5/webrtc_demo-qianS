package cn.teclub.ha.net.serv;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Scanner;

import org.hibernate.Session;

import cn.teclub.common.ChuyuObj;
import cn.teclub.common.ChuyuUtil;
import cn.teclub.ha.lib.StConst;
import cn.teclub.ha.lib.StEventPulsePool;
import cn.teclub.ha.lib.StExpUserError;
import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.StExpNet;
import cn.teclub.ha.net.serv.request.StSrvConnection;


class StSngSrvExceptionHandler 
		extends ChuyuObj 
		implements UncaughtExceptionHandler, Runnable		
{
	
	
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		try {
			logAndExit(t, e);
		} catch (Throwable e1) {
			e1.printStackTrace();
			stLog.error("\n\n!!!!!!!!!!!!!!!!!! Error in Exception Handler !!!!!!!!!!!!!!!!!!!!! \n\n");
		    stLog.error("\n\n!!!!!!!!!!!!!!!!!! S.T. SERVER TERMINATES !!!!!!!!!!!!!!!!!!!!!!!!! \n\n\n\n");
		    System.exit(-4);
		}
	}
	
	
	private void logAndExit(Thread t, Throwable e) throws InterruptedException{
		stLog.error("###############################################################");
	    e.printStackTrace();
	    util.sleep(500);
	    stLog.error(util.getExceptionDetails(e, "FATAL! Uncaught Throwable !!!!"));
	    stLog.error("###############################################################");

	    final Thread clearup_thread = new Thread(this);
	    clearup_thread.start();
	    clearup_thread.join(5000);
	    
	    stLog.error("\n\n!!!!!!!!!!!!!!!!!! S.T. SERVER TERMINATES !!!!!!!!!!!!!!!!!!!!!!!!! \n\n\n\n");
	    System.exit(-2);
	}

	@Override
	public void run() {
	    final StSrvComp srv_comp = StSrvComp.getInstance();
	    if(srv_comp != null){
	    	srv_comp.clearup();
	    }
	}
}




/**
 * <h1>Single Server Application</h1>
 * 
 * <p> A recv-thread receives from a SINGLE client connection. 
 * 
 * <p> NOTE: Support both SSL and Plain sockets.
 * 
 * @author mancook
 *
 */
public class StServerMain extends ChuyuObj
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	
    
    
    /**
     * <p> NOTE: For development, it can be started from Tomcat listener
     * 
     * @param args
     * @throws StExpUserError
     * @throws IOException
     */
    public static void main(String[] args) throws StExpUserError, IOException{
        if(args.length != 1) {
            System.out.println("Usage: <CMD> {console|none}");
            System.exit(-1);
        }
        boolean is_console = args[0].equalsIgnoreCase("console");
        StSrvConfig.getInstance();
        StServerMain srv = new StServerMain();
        String msg = "SSL Server has been started up. Waiting for user command ...";
        System.out.println(msg);
    	if(is_console){
    		srv.consoleLoop();
    	}
    }
    
    
	////////////////////////////////////////////////////////////////////////////
    // Inner Classes
	////////////////////////////////////////////////////////////////////////////
	
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Members
	////////////////////////////////////////////////////////////////////////////
	
	private final StSrvGlobal global; 
	private final StSrvComp   servComp;

	/**
	 * Constructor
	 * 
	 * @throws IOException
	 * @throws StExpUserError
	 */
	private StServerMain() throws IOException, StExpUserError {
		Thread.setDefaultUncaughtExceptionHandler(new StSngSrvExceptionHandler());
		stLog.info("############################################");
		stLog.info("#### Start:  " + util.getTimeStamp());
		stLog.info("#### Version:" + StConst.getVersionInfo());
		stLog.info("############################################");
		StEventPulsePool.initialize();
		this.servComp = StSrvComp.getInstance(); 
        this.global = StSrvGlobal.getInstance();
	}
	

	
	private void consoleLoop() throws StExpUserError, IOException{
        stLog.trace("Main Loop Stars! Enter console command...");
        final Scanner input = new Scanner(System.in);
        String str_cmd = "" ;
        
        while(true){
            System.out.print(">> ");
            //System.out.println("Input an expresion or 'quit' to quit");
            str_cmd = input.nextLine().trim();
            
    		if(str_cmd.equalsIgnoreCase("c")){
    			str_cmd = "show count";
    		}
    		else if(str_cmd.equalsIgnoreCase("d")){
    			str_cmd = "dump";
    		}
    		else if(str_cmd.equalsIgnoreCase("f")){
    			str_cmd = "config";
    		}
    		else if(str_cmd.equalsIgnoreCase("h")){
    			str_cmd = "help";
    		}
    		else if(str_cmd.equalsIgnoreCase("p")){
    			str_cmd = "show pp";
    		}
    		else if(str_cmd.equalsIgnoreCase("ps")){
    			str_cmd = "show pp status";
    		}
    		else if(str_cmd.equalsIgnoreCase("q")){
    			str_cmd = "quit";
    		}
    		else if(str_cmd.equalsIgnoreCase("s")){
    			str_cmd = "show sum";
    		}
    		else if(str_cmd.equalsIgnoreCase("v")){
    			str_cmd = "version";
    		}
    		
    		if(str_cmd.equalsIgnoreCase("quit")) {
                break ;
            }
    		if(str_cmd.length() ==0){
                continue;
            }
    		
    		
    		if(str_cmd.equals("T")) {
    			// [2016-9-2] This is a TESTING command
    			test_hiber1stCache();
    			continue;
            }

            try {
                consoleOneLoop(str_cmd);
            } catch (StExpNet e) {
                stLog.warn(util.getExceptionDetails(e, ""));
            }
            
            ChuyuUtil.getInstance().sleep(1000); // wait for the end of previous command
        }//while
        
        input.close();
        servComp.clearup();
        System.out.println("\nServer has stopped. Bye! \n");
	}
    
	    
	void test_hiber1stCache(){
			StDbHiberMgr.getInstance().executeInNewSession(new StDbHiberMgr.SessionCB() {
				StModelClient doQuery(Session ss, StClientID id){
					final long MS_START =  System.currentTimeMillis();
					
					ss.beginTransaction();
					StModelClient mc = (StModelClient) ss.load(StModelClient.class, new Long(id.getId()));
					
					//	Query qq = ss.createQuery("from StModelClient st_client where st_client.id=" + id.getId());
					//	List<StModelClient> l = qq.list();
					//	if(l.size() < 1) return;
					//	StModelClient mc = l.get(0);
									        
			        ss.getTransaction().commit();
			        stLog.info("==== Cost: " + util.getCostMillis(MS_START) + ", Client: " + mc);
			        return mc;
				}
				
				
				public Object execute(Session ss) {
					stLog.info("NOTE: 1) The 1st-Level Cache is enabled by default ");
					stLog.info("      2) Query result seems NOT in 1st-level cache ");
					
					StModelClient mc0 = doQuery(ss, new StClientID(0xb00));
					doQuery(ss, new StClientID(0xb01));
					doQuery(ss, new StClientID(0xb02));
					doQuery(ss, new StClientID(0xb03));
					
					stLog.info("\n\n==== Test 1st-Level Cache... ====    \n");
					StModelClient mc1 = doQuery(ss, new StClientID(0xb00));
					doQuery(ss, new StClientID(0xb01));
					doQuery(ss, new StClientID(0xb02));
					doQuery(ss, new StClientID(0xb03));
					
					stLog.info("==== 1st-Level Cache return " + 
								(mc0 == mc1 ? "SAME" : "DIFFERENT") + " objects ====");
					return null;
				}
			});
	}
	
	
	
	private void consoleUsage(){
	    String helpMsg =   
	    		  "============================================================== \n"
	            + "S.T. HA Server Console \n" 
	            + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n"
	            + "Usage: \n" 
	            + "~~~~~~ \n"
	            + " \n"
	            + "config          [f] Print config \n"
	            + " \n"
	            + "conn <name>     Print connection statistics  \n"
	            + " \n"
	            + "dump            [Deprecated] DUMP HA server element: client-manager, ...\n"
	            + " \n"
	            + "help            [h] Show this help message.\n"
	            + " \n"
	            + "quit            [q] Exit this program.\n"
	            + " \n"
	            + "show cache|count|sum|pp|pp status    \n" 
	            +"                 cache: [Deprecated] show cached clients. \n"
	            +"                 [c] count: show various count info. \n"
	            +"                 [s] sum: show online clients from DB. \n"
	            +"                 [p] pp: check & show pulse pool \n"
	            +"                 [ps] pp status: show previous pulse pool status \n"
	            + " \n"
	            + "version         [v] Show HA Version Number.\n"
	            + " \n"
	            + " \n"
	            + "T               For Inner Testing \n"
	            + " \n"
	            + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ \n"
	            + "";
	    message(helpMsg);
	}
	    
	
	
	private void consoleOneLoop(final String raw_str_cmd) throws StExpNet {
		String str_cmd = raw_str_cmd;

		if(str_cmd.equalsIgnoreCase("config") ){
			message("Show Config: \n" + global.cfg.toStringXml(" !!!! "));
			return;
		}
		
		if(str_cmd.equalsIgnoreCase("help") ){
			this.consoleUsage();
			return;
		}
		
		if(str_cmd.equalsIgnoreCase("version")){
			message("Server  Version: " + StSrvConfig.VERSION_INFO);
			message("GenLib4 Version: " + StConst.getVersionInfo() );
			return;
		}
		
		if(str_cmd.equalsIgnoreCase("show cache")){
			message("[Deprecated] NO Cache!");
			return;
		}
		
		if(str_cmd.equalsIgnoreCase("show pp")){
			message("Show Pulse Pool..."); 
			servComp.sendEventToCore(new StEvtServerDebug.ShowPulsePool());
			return;
		}
		
		if(str_cmd.equalsIgnoreCase("show pp status")){
			message("Show Pulse Pool..."); 
			servComp.sendEventToCore(new StEvtServerDebug.ShowPulsePool());
			return;
		}
		
		
		if(str_cmd.equalsIgnoreCase("show sum")){
			message("Show Clients Summary..."); 
			servComp.sendEventToCore(new StEvtServerDebug.DumpSummary());
			return;
		}
		
		if(str_cmd.equalsIgnoreCase("show count")){
			message("Show Online/Cached Clients Count ...");
			servComp.sendEventToCore(new StEvtServerDebug.ShowCount());
			return;
		}
		
		if(str_cmd.equalsIgnoreCase("dump")){
			message("Dump All Clients ..." );
			servComp.sendEventToCore(new StEvtServerDebug.DumpClient());
			return;
		}
		
		
		
		// ---------------------------------------------------------------------
		// commands with argument
		// ---------------------------------------------------------------------
		String[] cmds = str_cmd.split("\\s+");
		
		if(cmds[0].equalsIgnoreCase("conn")){
			if(cmds[1] == null || cmds[1].length() == 0) {
				return;
			}
			
			message("Show Connection of: " + cmds[1]);
			final StSrvConnection conn = global.connMgr.getConnection(cmds[1]);
			if(conn  == null) {
				message("ERROR: No Connection!");
				return;
			}
			message(conn.debug_getStatistics().toString());
			return;
		}
		
		message("ERROR: unknonw command: '" + str_cmd + "'");
	}
	
	
	
	private void message(final String msg){
		System.out.println(msg);
		util.sleep(500);
	}
	
}//EOF StServerSSL