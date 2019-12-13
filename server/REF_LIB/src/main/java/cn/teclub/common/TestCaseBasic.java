package cn.teclub.common;

import org.apache.log4j.Logger;

import cn.teclub.common.ChuyuLog;
import cn.teclub.common.ChuyuUtil;
import cn.teclub.ha.lib.StExpUserError;
import junit.framework.TestCase;


public class TestCaseBasic extends TestCase {

	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	
	static{
       	System.out.println("user.dir:  " + System.getProperty("user.dir") );
    	System.out.println("user.name: " + System.getProperty("user.name") );
    	System.out.println("user.home: " + System.getProperty("user.home") );
    	
    	// initialize log4j with specific config file
    	String log4j_cfg = System.getProperty("user.dir")  + "/local-runtime/conf/chuyu_log4j.cfg";
    	ChuyuLog.setLog4jConf(log4j_cfg);
    	ChuyuLog.getInstance();
	}

	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	
	ChuyuUtil util = ChuyuUtil.getInstance();
 	protected Logger stLog = Logger.getLogger(TestCaseBasic.class);

	
	////////////////////////////////////////////////////////////////////////////
    // Instance Method
	////////////////////////////////////////////////////////////////////////////
	
	public TestCaseBasic() throws StExpUserError {
		System.out.println("==== Constructor ====");
	}
	
	@Override
	protected void setUp() throws Exception {
		System.out.println("\n********************************************************");
		System.out.println("[[[ Set Up Test Env ]]] ");
	}

	@Override
	protected void tearDown() throws Exception {
		System.out.println("[[[ Tear Down Test Env ]]]");
	}
	
	
	////////////////////////////////////////////////////////////////////////////
    // Test Cases
	////////////////////////////////////////////////////////////////////////////	

	public void testToStringXml(){
//		try{
//			StCsSystemConfig cfg  = new StCsSystemConfig();
//			cfg.load();
//			System.out.println("CS System Config: \n" + cfg.toStringXml("    "));
//			
//			StTestConfig test_cfg = StTestConfig.getInstance();
//			System.out.println("(HA Client) Test Config: \n" + test_cfg.toStringXml("    "));
//			assertTrue(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.out.println(this.util.getExceptionDetails(e, ""));
//			assertTrue(false);
//		}
	}
}
