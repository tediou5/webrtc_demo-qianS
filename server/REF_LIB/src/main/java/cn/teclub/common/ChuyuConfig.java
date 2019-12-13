package cn.teclub.common;

import java.util.Locale;
import java.util.ResourceBundle;


/**
 * [2014-10-31] 
 * - DEFINE YOUR OWN CONFIG class in your application!!!
 * - To make ChuyuLog portable, ChuyuLog uses its own independent config file.
 * 
 * 
 * @author mancook
 */
public class ChuyuConfig {

	ChuyuConfig(String dir){
	}
	
	ChuyuConfig(){
		// You should use your own config class and file
		ResourceBundle res = ResourceBundle.getBundle("ChuyuConfig",Locale.getDefault());
	    res.getString("AppName");	
	}
	
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS
	////////////////////////////////////////////////////////////////////////////
    private static ChuyuConfig _Instance = null;
    
    public static ChuyuConfig GetInstance(){
        if(null == _Instance ){
            _Instance = new ChuyuConfig();
        }
        return _Instance;
    }
    
    
    /**
     * for testing on local PC
     * 
     * @return ChuyuConfig Object
     */
    public static ChuyuConfig GetInstanceLocalTesting(){
        if(null == _Instance ){
    		String localDir = System.getProperty("user.dir") + "/local-runtime/";
    		System.out.println("[INFO] [static ChuyuConfig.GetInstanceLocalTesting] Local Runtime Dir: " + localDir	);
            _Instance = new ChuyuConfig(localDir);
        }
        return _Instance;
    }

}
