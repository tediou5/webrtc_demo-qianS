package cn.teclub.ha.client.rpr;

import java.util.Vector;

import cn.teclub.ha.lib.StGenState;

/**
 * Client RPR State
 * 
 * <pre>
 * OFFLINE: 		Fail to connect with server
 * LOGOUT:  		Server sends YOU_LOGOUT or client clicks "Logout" to log out as the current account
 * LOGING:  		There is a pending login request
 * UPDATE_FRD:		Set when LOGIN ALLOW is received. In next heat beat, RPR trigger an event to query friend list.
 * QUERYING_FRD: 	A ClientAQueryB request is pending.
 * 
 * </pre>
 * @author mancook
 *
 *
 * TODO: use a simple rpr-state object
 *
 */
public class StcRprState  
		extends StGenState 
		implements java.io.Serializable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7624709292151271512L;
	
	private static final Vector<StcRprState>  values = new Vector<StcRprState>();
	public static StringBuffer dumpElements(){
		return StGenState.dumpElements(values, "Dump Client RPR States");
	}
	

	public final static StcRprState OFFLINE 	= new StcRprState(0x00, "OFFLINE");
	public final static StcRprState IDEAL 		= new StcRprState(0x01, "IDEAL");
	
	public final static StcRprState LOGOUT 			= new StcRprState(0x10, "LOGOUT");
	public final static StcRprState UPDATE_FRD 		= new StcRprState(0x11, "UPDATE_FRD");
	public final static StcRprState QUERYING_FRD 	= new StcRprState(0x12, "QUERYING_FRD");
	public static final StcRprState LOGING 			= new StcRprState(0x13, "LOGING");

	 
	
	public static StcRprState fromInt(int v){
		return (StcRprState) StGenState.fromInt(values, v);
	}
	
	// ---------------------------------------------
	// non-static members
	// ---------------------------------------------


	private StcRprState(int v, String n){
		super(v, n);
	}
	
	protected void addElement(StGenState s) {
		values.add((StcRprState) s);
	}
}

