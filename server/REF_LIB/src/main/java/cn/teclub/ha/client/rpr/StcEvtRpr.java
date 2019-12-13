package cn.teclub.ha.client.rpr;

import java.util.ArrayList;

import cn.teclub.ha.client.StcEvtClient;
import cn.teclub.ha.request.StNetPacket;


/**
 * NOTE: getClassSimple() fails on inner class.
 * Add event-name for each inner class!
 * 
 * @author mancook
 *
 */
public class StcEvtRpr extends StcEvtClient 
{
	////////////////////////////////////////////////////////////////////////////
    // TODO: deprecate Info events
	////////////////////////////////////////////////////////////////////////////

	
	public static class InfoResetPasswdSuccess extends StcEvtRpr { }
	
	public static class InfoResetPasswdFail extends StcEvtRpr {
		public final byte denyCode;
		public InfoResetPasswdFail(final byte code){
			this.denyCode = code;
		}
	}
	
	
	
	
	/**
	 * triggered at a srv-req: SrvUpdateB
	 */
	public static class InfoRemoteUpdate extends StcEvtRpr  { }


	/**
	 * Login success, offline & failure event must be sent to Android core-pulse,
	 * which updates the app-states.
	 * Android message cannot be sent to core-pulse!
	 */
	public static class InfoLoginSuccess extends StcEvtRpr { }


	public static class InfoLoginFail extends StcEvtRpr {
		public final byte denyCode;
		public InfoLoginFail(final byte code){
			this.denyCode = code;
		}
	}

	public static class InfoOffline extends StcEvtRpr {  }


	/**
	 * <pre>
	 * inform app (core) when rpr state changes. e.g. OFFLINE -> LOGIN
	 *
	 * from: rpr
	 * to:   app core
	 *
	 * </pre>
	 */
	public static class InfoStateChange extends StcEvtRpr {  }


	public static class InfoReqTimeout extends StcEvtRpr {
		public final StNetPacket.Command cmd;
		public InfoReqTimeout(final StNetPacket.Command cmd){
			this.cmd = cmd;
		}
	}

	public static class InfoMessageFromSrv extends StcEvtRpr { }

	
	////////////////////////////////////////////////////////////////////////////
    // Inner Classes
	////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Members
	////////////////////////////////////////////////////////////////////////////	
	public StcEvtRpr(final String evt_name, final String evt_dscp) {
		super(evt_name, evt_dscp);
	}
	
	public StcEvtRpr(){
		super(null, null);
	}
}


class StcEvtPostPacket extends StcEvtRpr
{ 
	final StNetPacket 	packet;
	
	StcEvtPostPacket(final StNetPacket pkt ){
		this.packet = pkt;
	}
}



class StcEvtStartRequest extends StcEvtRpr
{ 
	final StClientRequest request;
	
	StcEvtStartRequest(final StClientRequest req ){
		this.request = req;
	}
}


/**
 * Triggered by: StcRprObject.disconnect(), which is called by app layer.
 * Handled by:   main pulse
 */
class StcEvtDisconnect extends StcEvtRpr { }


/**
 * @deprecated  Timelapse is sent in P2P mode
 */
class StcEvtUploadTL extends StcEvtRpr{
	/**
	 * Relative file path
	 */
	final String filePath;
	
	StcEvtUploadTL(final String file_path){
		this.filePath = file_path;
	}
}


/**
 * @author mancook
 *
 */
class StcEvtUploadLogs extends StcEvtRpr{
	/**
	 * Relative log path
	 */
	final ArrayList<String> logfiles; // = new ArrayList<String>();
	
	StcEvtUploadLogs(final ArrayList<String> logs){
		logfiles = logs;
	}
}

