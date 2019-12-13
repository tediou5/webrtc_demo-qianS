package cn.teclub.common;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


public class ChuyuLog {
	Logger log4j;

	private ChuyuLog(String log4j_cfg_file){	
		if(log4j_cfg_file != null){
			System.out.println("[ChuyuLog] Initialize Log4j with config file: " + log4j_cfg_file );
			PropertyConfigurator.configure(log4j_cfg_file);
		}
		this.log4j = Logger.getLogger(ChuyuLog.class);
	}
	
	////////////////////////////////////////////////////////////////////////////
    // Methods
	////////////////////////////////////////////////////////////////////////////
	public void trace(String msg){
		log4j.trace(msg);
	}
	
	public void debug(String msg){
		log4j.debug(msg);
	}
	
	public void info(String msg){
		log4j.info(msg);
	}
	
	public void warn(String msg){
		log4j.warn(msg);
	}
	
	public void error(String msg){
		log4j.error(msg);
	}
	
	
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS
	////////////////////////////////////////////////////////////////////////////
    private static ChuyuLog _Instance = null;
    
    
    //
    // [Theodore: 2016-06-16] Calling ChuyuLog.getInstance() on Android causes 
    // FileNotFoundException for the default config file! 
    //
    // CAUSE: ChuyuUtil.testMilestoneLog() calls ChuyuLog to log milestone message. 
    // For Android, Log4j is NOT configured in ChuyuLog with a config file! 
    // 
    // SOLUTION: DO NOT use the default log4j config ifle!
    //
    //		private static String   log4j_conf = "./chuyu_log4j.cfg";
    private static String log4j_conf = null;
    
    
    /**
     * Call this method, before first calling ChuyuLog.getInstance()!
     * 
     * @param log4j_conf pathname of the log4j config file
     */
    public static void setLog4jConf(String log4j_conf){
    	ChuyuLog.log4j_conf = log4j_conf;
    }
    
    
    
    public static ChuyuLog getInstance(){
        if(null == _Instance ){
            _Instance = new ChuyuLog(log4j_conf);
        }
        return _Instance;
    }
    
    
    /**
     * Main Entry: Smoke Test ChuyuLog
     *
     * @param args command arguments
     */
    public static void main(String[] args){
    	System.out.println("user.dir:  " + System.getProperty("user.dir") );
    	System.out.println("user.name: " + System.getProperty("user.name") );
    	System.out.println("user.home: " + System.getProperty("user.home") );
    	
    	// initialize log4j with specific config file
    	String log4j_cfg = System.getProperty("user.dir")  + "/local-runtime/conf/chuyu_log4j.cfg";
    	ChuyuLog.setLog4jConf(log4j_cfg);
    	ChuyuLog logger = ChuyuLog.getInstance();
    	
    	// testing logging methods
    	logger.debug("testing chuyulog: debug");
    	logger.info("testing chuyulog: info");
    }
}
