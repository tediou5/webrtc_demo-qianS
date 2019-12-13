package cn.teclub.ha.client.rpr;

import java.nio.ByteBuffer;

import cn.teclub.ha.client.StcException.ExpLocalClientOffline;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.request.StNetPacket;



/**
 * Ask remote to upload a file to cache. 
 * 
 * <pre>
 * This request does NOT wait for the remote finishing uploading. 
 * As soon as remote triggers upload-event to upload-pulse, this request is allowed.
 * </pre>
 * 
 * @author mancook
 *
 * @deprecated  upload in app-layer
 */
public class StcReqP2pUploadFile extends StcReqP2p 
{
	public StcReqP2pUploadFile(
			final StClientInfo r_clt, 
			final StRemoteFile  rf
		) throws ExpLocalClientOffline 
	{
		super(  r_clt, 
				StNetPacket.Command.P2pUploadFile, 
				StNetPacket.Code.NONE,
				util.stringFunc.toBuffer(rf.fileOnDevice), 
				"Upload Remote File: " + rf );
	}

	
	@Override
	protected void onTimeout() {
	}
	

	@Override
	protected void onResAllow(final byte code, final ByteBuffer data) {
		this.resResult = null;
	}

	@Override
	protected void onResDeny(byte code, ByteBuffer data) {
		this.resResult = null;
	}
}


/**
 * @deprecated [2017-12-29] upload-file is NOT supported!
 * Just deny any incoming request!
 */
class StcServiceP2pUploadFile
		extends StcServiceP2p
		implements StcServiceP2p.StSyncService
{
	protected StcServiceP2pUploadFile() {
		super(StNetPacket.Command.P2pUploadFile);
	}

	protected void onRequest(final StClientInfo r_clt, final StNetPacket pkt) {
		sendResponse(pkt.buildDny(StNetPacket.Code.DENY_ERROR));
	}
}


/*
class StcServiceP2pUploadFile 
	extends StcServiceP2p
	implements StcServiceP2p.StSyncService
{
	protected final StcRprUploadPulse  uploadPulse = StcRprObject.getInstance().uploadPulse;

	protected StcServiceP2pUploadFile() {
		super(StNetPacket.Command.P2pUploadFile);
	}
	
	
	@Override
	protected void onRequest(final StClientInfo r_clt, final StNetPacket pkt) {
		if(sharedVar.isUploading()){
			sendResponse(pkt.buildDny(StNetPacket.Code.P2PUploadFile.DENY_IS_UPLOADING));
			return;
		}
		
		// [Theodore: 2016-08-29] delete previous cache info, 
		// so that query-cache fails until current upload finishes.
		final String app_file_path = pkt.dataGetString(0);
		getRprObject().params.objectMgr.setCacheName(app_file_path, ""); // NULL works?
		
		// [Theodore: 2016-09-16]
		// DO NOT set UPLOADING flag here! Set in Upload Pulse ??
		sharedVar.setUploading(true);
		
		uploadPulse.addNewEvent(new StcEvtUploadTL(app_file_path));
		sendResponse(pkt.buildAlw(null));
	}
}
*/