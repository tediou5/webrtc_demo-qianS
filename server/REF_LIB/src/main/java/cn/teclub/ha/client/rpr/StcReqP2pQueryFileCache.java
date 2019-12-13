package cn.teclub.ha.client.rpr;

import java.nio.ByteBuffer;

import cn.teclub.ha.client.StcException.ExpLocalClientOffline;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.request.StNetPacket;



/**
 * Query a file cache from a remote client. 
 * 
 * <pre>
 * Query Result: 
 * - ALLOW: file cache 
 * - DENY/TIMEOUT: null
 * </pre>
 * @author mancook
 *
 */
public class StcReqP2pQueryFileCache extends StcReqP2p 
{
	public StcReqP2pQueryFileCache(
			final StClientInfo r_clt, 
			final StRemoteFile  rf,  
			final int timeout
		) throws ExpLocalClientOffline 
	{
		super(  r_clt, 
				StNetPacket.Command.P2pQueryFileCache, 
				StNetPacket.Code.NONE,
				util.stringFunc.toBuffer(rf.fileOnDevice), 
				timeout, 
				"Query RF Cache: " + rf );
	}

	
	@Override
	protected void onTimeout() {
	}
	

	@Override
	protected void onResAllow(final byte code, final ByteBuffer data) {
		this.resResult = util.stringFunc.fromBuffer(data);
	}

	@Override
	protected void onResDeny(byte code, ByteBuffer data) {
		this.resResult = null;
	}
}




class StcServiceP2pQueryFileCache 
	extends StcServiceP2p
	implements StcServiceP2p.StSyncService
{
	protected StcServiceP2pQueryFileCache() {
		super(StNetPacket.Command.P2pQueryFileCache);
	}
	
	
	@Override
	protected void onRequest(final StClientInfo r_clt, final StNetPacket pkt) {
		final String app_file_path = pkt.dataGetString(0);
		final String file_cache = getRprObject().params.objectMgr.getCacheName(app_file_path);
		stLog.info("#### Remote queries cache of: " + app_file_path);
		stLog.info("#### File Cache: " + file_cache);
		if(file_cache == null || file_cache.trim().length() < 1){
			sendResponse(pkt.buildDny(null));
			return;
		}
		
		final ByteBuffer ret_data = util.stringFunc.toBuffer(file_cache);
		sendResponse(pkt.buildAlw(ret_data));
	}
}