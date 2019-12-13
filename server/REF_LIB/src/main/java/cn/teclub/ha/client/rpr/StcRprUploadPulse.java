package cn.teclub.ha.client.rpr;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.client.StFtpClient;
import cn.teclub.ha.lib.StConst;
import cn.teclub.ha.lib.StEvent;
import cn.teclub.ha.lib.StEventListener;
import cn.teclub.ha.lib.StEventPulse;


/**
 * @deprecated upload TL & Logs in lbr-pulse.
 *
 * Currently, TL is transferred in p2p mode.
 *
 */
public class StcRprUploadPulse extends StEventPulse 
{
	public static final int HB_INIT_MS 		= 10*1000;
	public static final int HB_PERIOD_MS 	= StConst.CLT_LBR_PULSE_PERIOD_MS;
	
	/**
	 * Construct RPR Upload Pulse. 
	 * 
	 */
	StcRprUploadPulse() {
		super("RPR-Upload-Pulse", HB_INIT_MS, HB_PERIOD_MS);
	}
}



class StcRprUploadLis 
		extends ChuyuObj 
		implements StEventListener
{
	protected static final StcSharedVar 	sharedVar = StcSharedVar.getInstance();
	protected static final StcRprObject 	rprObject = StcRprObject.getInstance();
	protected static final StcRprMainPulse  mainPulse = rprObject.mainPulse;

	private final StFtpClient ftpClient = new  StFtpClient();
	
	
	
	private void prcHeartBeat(final StEvent.HeartBeat evt) {
	}
	
	
	@SuppressWarnings("deprecation")
	private void processUploadTL( StcEvtUploadTL evt){
		try{
			// [2016-9-16] RPR Main Pulse sets uploading flag, when sending UploadTL-Event.
			util.assertTrue(sharedVar.isUploading());
			final String app_file_path = evt.filePath;
			final String srv_cache_dir = sharedVar.getLocalCopy().getName();
			stLog.trace("To upload file: '" + app_file_path + "' to cache: " + srv_cache_dir);
			
	        final String remoteFtpDir1 = "__" + srv_cache_dir + "__" ;
	        final String remoteFtpDir2 = "upload__" + util.getTimeStampForFile() ;
			final String file_on_cache = ftpClient.uploadFile(app_file_path, remoteFtpDir1, remoteFtpDir2, StFtpClient.TYPE_TimeLapse);
			if(file_on_cache != null){
				stLog.info("Upload Success: " + app_file_path);
				rprObject.params.objectMgr.setCacheName(app_file_path, file_on_cache ); 
			}else{
				stLog.error("Fail to upload file: " + app_file_path);
			}
		}finally{
			sharedVar.setUploading(false);
		}
	}
	
	
	private void processUploadLog(StcEvtUploadLogs evt){
		try{
			// [2016-9-16] RPR Main Pulse sets uploading flag, when sending UploadLogs-Event.
			util.assertTrue(sharedVar.isUploading());
			rprObject.tools.uploadLogs(sharedVar.getClientName(), evt.logfiles);
		}finally{
			sharedVar.setUploading(false);
		}
	}
	
	
	
	@Override
	public String getEvtLisName() {
		return "RPR-Upload-Pulse-Lis";
	}

	
	@SuppressWarnings("deprecation")
	@Override
	public void handleEvent(StEvent event) {
		if(event instanceof StEvent.HeartBeat){
			prcHeartBeat((StEvent.HeartBeat)event);
			return;
		}
		
		if(event instanceof StEvent.SystemShutdown){
			stLog.info("[RPR] Upload Pulse is shutting down ...");
			return;
		}
		
		stLog.info(">>>> " + event.dump() );
		if(event instanceof StcEvtUploadTL){
			processUploadTL((StcEvtUploadTL) event);
		}
		
		else if(event instanceof StcEvtUploadLogs){
			processUploadLog((StcEvtUploadLogs) event);
		}
		
		else{
			stLog.error("UnExpected Event: " + event.dump());
		}
		
		stLog.info("<<<< " + event.dump() );
	}
}