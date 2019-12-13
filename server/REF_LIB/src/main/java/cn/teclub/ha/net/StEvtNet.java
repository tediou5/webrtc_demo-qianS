package cn.teclub.ha.net;

import cn.teclub.ha.lib.StEvent;

/**
 * <h1>Event in HA network system. </h1>
 * 
 * <p> Used by: client and server
 * 
 * <p> all known sub-classes:  StEvtServer, StEvtClient
 * @author mancook
 *
 */
public class StEvtNet extends StEvent {
    ////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS
	////////////////////////////////////////////////////////////////////////////
	final static String dscp = "HA network events";
	
    ////////////////////////////////////////////////////////////////////////////
    // Instance Members and Methods
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructor
	 * 
	 * Used by sub-class constructor, so that the correct event name 
	 * and description is passed!
	 * 
	 * @param eventName
	 * @param eventDscp
	 * 
	 */
	public StEvtNet( String eventName,String eventDscp) {
		super(eventName, eventDscp);
	}
	
	/**
	 * Default constructor.
	 */
	public StEvtNet(){
		super(null, dscp);
	}
}

