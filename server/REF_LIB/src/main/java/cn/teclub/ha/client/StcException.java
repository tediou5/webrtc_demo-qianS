package cn.teclub.ha.client;

import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.StExpNet;


/**
 * <h1>HA Client Exception. </h1>
 * 
 * Used by: client 
 * 
 * @author mancook
 *
 */
@SuppressWarnings("serial")
public class StcException extends StExpNet 
{
	////////////////////////////////////////////////////////////////////////////
    // Inner Classes
	////////////////////////////////////////////////////////////////////////////	
	public static class ExpPublicAddrFail  extends StcException{}
	
	
	public static class ExpLocalClientOffline  extends StcException{ }

	
    
	/**
	 * 
	 * @author mancook
	 * 
	 * @deprecated use ExpFailToSignup
	 */
 	public static class ExpSignupErrorNoMacAddress  extends StcException{}

 	
	
	public static class ExpSignoutErrorPasswordIncorrect  extends StcException{}

    
//	/**
//	 * 
//	 * @author mancook
//	 *
//	 * @deprecated use SessionTimeout Exception 
//	 */
//	public static class ExpOperationTimeout  extends StExpClient{
//    	public ExpOperationTimeout(){
//    	}
//    	
//    	public ExpOperationTimeout(String msg){
//    		super(msg);
//    	}
//    }

	public static class ExpTimeLapseVideoMissing  extends StcException{ 
		public final String videoOnDisk;
		public ExpTimeLapseVideoMissing(String video_on_disk){
			this.videoOnDisk = video_on_disk;
		}
	}
    
	public static class ExpPingTimeout  extends StcException{ }

	public static class ExpTaskTimeout22  extends StcException{ }
    
    public static class ExpFailToConnectServer  extends StcException{   } 
    
    
    public static class ExpFailToDownload  extends StcException{
    	public ExpFailToDownload(final String err_msg){
    		super(err_msg);
    	}
    }


	/**
     * This exception occurs when fail to get a client-info from core-var.
     * 
     * @author mancook
     */
    public static class ExpRemoteClientNoFound  extends StcException{
    	public ExpRemoteClientNoFound(StClientID r_id){
    		super("Fail to get client-info by ID: " + r_id );
    	}
    	public ExpRemoteClientNoFound(String name){
    		super("Fail to get client-info by Name: " + name );
    	}
    }
    
    
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	

	// private static final long serialVersionUID = -6712458225027074689L;

	
	////////////////////////////////////////////////////////////////////////////
    // Instance Members 
	////////////////////////////////////////////////////////////////////////////	

	
	public StcException(String msg){
		super(msg);
	}
	
	public StcException(){
		super("[StExpClient]");
	}
}


/**
 * <h1>Client fails to send a network packet. </h1>
 * 
 * @author mancook
 * 
 * 
 * @deprecated [2016-7-30] just disconnect with server, if sending failure
 * 
 */
@SuppressWarnings("serial")
class StExpClientCoreFailToSend extends StcException {  
	public StExpClientCoreFailToSend(String msg){
		super(msg);
	}
}




