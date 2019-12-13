package cn.teclub.ha.client.rpr;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.client.StcParams;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.request.StNetPacket;
import cn.teclub.ha.request.StNetPacket.Code;


/**
 * <pre>
 * ATTENTION: Each service has ONLY ONE instance during client life!  
 * It is NOT LIKE request, which creates a new instance for each incoming request.
 * </pre>
 * 
 * @author mancook
 *
 */
public abstract class StcService
		extends ChuyuObj 
		implements ChuyuObj.DumpAttribute
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBER
	////////////////////////////////////////////////////////////////////////////	

	public interface StSyncService
	{
	}

	protected static final StcSharedVar 	sharedVar = StcSharedVar.getInstance();

	
	/**
	 * <pre>
	 * Make sure process such requests one by one! 
	 * This flag is only useful, if a new thread is started to do some time-consuming job.
	 * e.g. P2P_CALL_START;
	 * 
	 * Used by a synchronized service.
	 * </pre>
	 */
	private static boolean busy = false;
	
	////////////////////////////////////////////////////////////////////////////
    // Inner Class
	////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Member
	////////////////////////////////////////////////////////////////////////////

	
	/**
	 * ID in service map in StcRprObject.
	 */
	public final StNetPacket.Command 	cmd;

	/**
	 * <pre>
	 * - Log service cost;
	 * - Make sure response packet is sent ONLY ONCE!
	 * </pre>
	 */
	private long reqStartMS = 0;
	
	private StClientInfo	remoteClient = null;
	private StNetPacket 	reqPacket = null;
	
	

	protected StcService(final StNetPacket.Command cmd){
		this.cmd = cmd;
	}

	protected StcRprObject getRprObject(){
		return StcRprObject.getInstance();
	}
	
	protected StcParams getParams(){
		return StcRprObject.getInstance().params;
	}
	
	protected void addRprEvent(StcEvtRpr evt){
		getRprObject().mainPulse.addNewEvent(evt);
	}
	
	public StNetPacket getReqPacket() {
		return reqPacket;
	}

	
	/**
	 * Used by a P2P service.
	 * Invalid for service for server.
	 *
	 * @return the remote client
	 */
	public StClientInfo getRemoteClient() {
		return remoteClient;
	}


	/**
	 * <pre>
	 * Called by main pulse. 
	 * 
	 * </pre>
	 * 
	 * @param r_clt - null of a server request;
	 * @param pkt - request packet
	 */
	public void onRecvRequest(final StClientInfo r_clt, final StNetPacket pkt){
		if(this instanceof StSyncService){
			if(busy){
				// DO NOT call this.sendResponse()
				getRprObject().sendResponse(pkt.buildDny(Code.DENY_P2P_Service_BUSY));
				return;
			}
			busy  = true;
		}
		this.remoteClient = r_clt;
		this.reqPacket = pkt;
		reqStartMS = System.currentTimeMillis();
		stLog.debug("Process Request: " + pkt.getCmd() );
		onRequest(r_clt, pkt);
	}
	
	
	/**
	 * Implemented in a specific service.
	 * 
	 * @param r_clt - null of a server request;
	 * @param pkt - request packet
	 */
	protected abstract void onRequest(final StClientInfo r_clt, final StNetPacket pkt);
	
	
	public StNetPacket.Command  getCommand(){
		return this.cmd;
	}
	
	
	/**
	 * <pre>
	 * For each request, this method MUST be called ONLY ONCE!
	 * For requests which require no response, call this method with NULL packet. 
	 * 
	 *
	 * This method does:
	 * - Send Response Packet;
	 * - reset busy and start-ms properties;
	 * </pre>
	 * 
	 * @param pkt - the response (deny/allow) packet
	 */
	public void sendResponse(final StNetPacket pkt){
		util.assertTrue(reqStartMS > 0, "DO NOT Send Response for a non-start service!");
		
		if(pkt != null){
			//util.assertTrue(pkt != null, 	"Response Packet CANNOT Be NULL!" );
			getRprObject().sendResponse(pkt);
		}
		final long ms_cost = System.currentTimeMillis() - reqStartMS;
		stLog.debug("Service For "+ cmd +" Cost(ms): " + ms_cost );
		reqStartMS = 0;
		
		if(this instanceof StSyncService){
			busy = false;
		}
	}
	
	
	public void dumpSetup(){ 
		dumpAddLine("Process Req: " + this.cmd );
		dumpAddLine("Processing : " + (reqStartMS > 0 ? "YES" : "NO") );
		dumpAddLine("Is Sync    : " + (this instanceof StSyncService ? "YES" : "NO") );
		
		if(reqStartMS > 0){
			dumpAddLine("    Request: " + reqPacket );
			dumpAddLine("    Remote : " + remoteClient );
			dumpAddLine("    Current Cost (ms): " + (System.currentTimeMillis() - reqStartMS) );
		}
	}
}

