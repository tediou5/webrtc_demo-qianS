package cn.teclub.ha.client.app;


import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.client.StcParams;
import cn.teclub.ha.client.StcTools;
import cn.teclub.ha.client.StcException.ExpFailToDownload;
import cn.teclub.ha.client.StcException.ExpLocalClientOffline;
import cn.teclub.ha.client.rpr.StRemoteFile;
import cn.teclub.ha.client.rpr.StBridgeToUser;
import cn.teclub.ha.client.rpr.StcExpRpr;
import cn.teclub.ha.client.rpr.StcRprObject;
import cn.teclub.ha.lib.StEventPulsePool;
import cn.teclub.ha.net.StClientInfo;


/**
 * <h1>Application Component</h1>
 * 
 * 
 * @author mancook
 *
 */
class StcAppComp extends ChuyuObj 
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	

	private static StcAppComp _ins = null;
	
	static void initialize(StBridgeToUser rb){
		util.assertTrue(_ins == null);
		_ins = new StcAppComp(rb);
	}
	
	static StcAppComp getInstance(){
		util.assertNotNull(_ins);
		return _ins;
	}
	
	
	////////////////////////////////////////////////////////////////////////////
    // Inner Classes
	////////////////////////////////////////////////////////////////////////////
	

	////////////////////////////////////////////////////////////////////////////
    // Instance Members
	////////////////////////////////////////////////////////////////////////////	

	final StcParams 	params;
	final StcTools 		tools;
	final StcRprObject.Info info;
	
	final StcPulseApp 	appPulse;
	final StcPulseLbr 	lbrPulse;
	final StcRprObject  rprObj;
	
	private boolean initialized = false;


	/**
	 * Constructor
	 */
	private StcAppComp(StBridgeToUser rb){
		StEventPulsePool.initialize();
		this.params = StcParams.getInstance();
		this.tools =  StcTools.getInstance();
		
		this.appPulse = new StcPulseApp();
		this.lbrPulse = new StcPulseLbr();

		this.rprObj = new StcRprObject(rb);
		this.info = rprObj.info;
		stLog.info("App Component Constructed!");
	}
	
	
	void init(){
		util.assertTrue(!initialized, "Init ONLY ONCE!");
		appPulse.addListener(new StcPulseAppLis());
		initialized = true;
	}

	void connect(){
		rprObj.connect();
	}
	
	void disconnect(){
		rprObj.disconnect();
	}

	@SuppressWarnings("deprecation")
	int pingRemote(final StClientInfo r_clt)
			throws ExpLocalClientOffline, StcExpRpr.ExpReqTimeout {
		return rprObj.pingRemote(r_clt);
	}


	@SuppressWarnings("deprecation")
	void downloadFile( 
			final StClientInfo 	r_clt, 
			final int 			timeout, 
			final StRemoteFile	remote_file, 
			final String 		local_dir 
		) throws ExpFailToDownload 
	{
		rprObj.downloadFile(r_clt, timeout, remote_file, local_dir);
	}

	
	void logout(){
		rprObj.logout();
	}
	
	
	void destroy(){
		final long ms_start = System.currentTimeMillis();
		StcParams.destroy();
		lbrPulse.stop();
		appPulse.stop();

		// [2017-12-29]
		// as 'rprObj' is created when constructing StcAppComp, it should be destroyed here!
		//
		// NOTE: It is NOT a good idea to destroy 'rprObj' in app-pulse at SystemShutdown event.
		// Reason: 'rprObj' is NOT destroyed, if app-pulse is dead.
		//
		rprObj.destroy();

		StEventPulsePool.getInstance().stop();
		_ins = null;
		stLog.warn("Client Component Destroyed!  Cost: " + (System.currentTimeMillis() - ms_start) + "ms");
	}
}
