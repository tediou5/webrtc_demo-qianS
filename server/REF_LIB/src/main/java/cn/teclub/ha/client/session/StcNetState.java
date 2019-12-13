package cn.teclub.ha.client.session;

import java.util.Vector;

import cn.teclub.ha.lib.StGenState;

/**
 * Network State of Client
 * 
 * @author mancook
 *
 */
class StcNetState  
		extends StGenState 
		implements java.io.Serializable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1535201377577163846L;
	private static final Vector<StcNetState>  values = new Vector<StcNetState>();
	public static StringBuffer dumpElements(){
		return StGenState.dumpElements(values, "Dump Client Net States");
	}
	
	public  static final StcNetState Disconnected 	= new StcNetState(0x00, "Disconnected");	// Client cannot connect to server: 1) No WIFI or Mobile Network is available. 2) Server is down!
	public  static final StcNetState Connected  	= new StcNetState(0x04, "Connected");		// Socket Connection with server is established. NOTE: This state is very short! 
	//public  static final StcNetState Login 		= new StcNetState(0x08, "Login");			// Client logs in server.
	//public  static final StcNetState Logout 		= new StcNetState(0x0C, "Logout");			// 1) Receive YOU_LOGOUT from server. The same client account logs in from another device! 
																								// 2)  User clicks LOGOUT!
	// ---------------------------------------------
	// non-static members
	// ---------------------------------------------
	
	private StcNetState(int v, String n){
		super(v, n);
	}
	
	protected void addElement(StGenState s) {
		values.add((StcNetState) s);
	}
}

