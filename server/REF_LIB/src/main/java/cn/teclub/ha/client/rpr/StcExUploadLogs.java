package cn.teclub.ha.client.rpr;




///**
// * Execute in RPR Main Pulse
// *
// * @author mancook
// *
// */
//public class StcExUploadLogs extends StcRprExecution
//{
//	/*
//	protected final StcRprUploadPulse  uploadPulse = StcRprObject.getInstance().uploadPulse;
//
//	/**
//	 * Relative log path
//	 */
//	public final ArrayList<String> logfiles;
//
//	public StcExUploadLogs(final ArrayList<String> logs)
//	{
//		super("Upload Logs", null);
//		logfiles = logs;
//	}
//
//
//	@Override
//	protected void perform() {
//		if(sharedVar.isUploading()){
//			stLog.error("Abort Upload Logs! -- Is Uploading!");
//			return;
//		}
//
//		sharedVar.setUploading(true);
//		uploadPulse.addNewEvent(new StcEvtUploadLogs(logfiles));
//	}
//}