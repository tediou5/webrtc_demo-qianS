package cn.teclub.ha.client.rpr;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.client.rpr.StcExpRpr.ExpReqTimeout;
import cn.teclub.ha.client.StcParams;
import cn.teclub.ha.client.StcTools;
import cn.teclub.ha.client.StcException;
import cn.teclub.ha.client.StcException.ExpFailToDownload;
import cn.teclub.ha.client.StcException.ExpLocalClientOffline;
import cn.teclub.ha.client.StcException.ExpRemoteClientNoFound;
import cn.teclub.ha.client.session.StcSessionObject;
import cn.teclub.ha.lib.StConst;
import cn.teclub.ha.lib.StErrUserError;
import cn.teclub.ha.lib.StEvent;
import cn.teclub.ha.lib.StTask;
import cn.teclub.ha.lib.StTask.ExpTaskTimeout;
import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.net.StFriend;
import cn.teclub.ha.net.StJniNatType;
import cn.teclub.ha.request.StNetPacket;
import cn.teclub.ha.request.StRequestID;
import static cn.teclub.ha.client.rpr.StcRprUploadLis.rprObject;


/**
 * <h1> The Representation Global Object </h1>
 * 
 * @author mancook
 *
 */
public class StcRprObject extends ChuyuObj 
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////
	private static final Object lock = new Object();
	private static final long PROCESS_STARTUP_TIME = System.currentTimeMillis();
	
	private static StcRprObject _ins;
	
	public static StcRprObject getInstance(){
		synchronized (lock) {
			assert _ins != null;
			return _ins;
		}
	}

	/*
	public static void initialize(final StBridgeToUser rpr_brg)
	{
		util.assertTrue( _ins == null, "DO NOT initialize RPR object, again!");
		_ins = new StcRprObject(rpr_brg);
		_ins.init();
	}
	*/

	
	////////////////////////////////////////////////////////////////////////////
    // Inner Classes
	////////////////////////////////////////////////////////////////////////////

    /**
     * This class is used by app-layer.
     * 
     * @author mancook
     *
     */
    public class Info
    {
    	/**
    	 * item[0] is the system boot time.
    	 */
    	private final ArrayList<Long> lstLoginTime;

    	/**
    	 * Constructor
    	 */
    	private Info(){
    		lstLoginTime = new ArrayList<>();
    		lstLoginTime.add(System.currentTimeMillis());
    	}
    	
    	
    	public long getBootTime(){
    		return lstLoginTime.get(0);
    	}
    	
    	public String getProcessStartTimeStr(){
    		return util.getTimeStamp( PROCESS_STARTUP_TIME );
    	}
    	
    	public String getBootTimeStr(){
    		return util.getTimeStamp( lstLoginTime.get(0) );
    	}
    	
    	
    	public void addLoginTime(){
    		lstLoginTime.add(System.currentTimeMillis());
    	}
    	
    	
    	public long getLastestLoginTime(){
    		if(lstLoginTime.size() < 2){
    			return 0;
    		}
    		return lstLoginTime.get(lstLoginTime.size()-1);
    	}
    	
       	public String getLastestLoginTimeStr(){
       		final long ms = getLastestLoginTime();
       		return util.getTimeStamp(ms);
    	}

    	
//    	/**
//    	 * <h2>Get file name of the current recording time lapse </h2>
//    	 *
//    	 * <p> NOTE: NOT the full pathname! Only the filename.
//    	 *
//    	 * @return name of the recording time lapse
//    	 */
//    	public String getRecordingTimelapse(){
//    		return sharedVar.getRecordingTimelapse();
//    	}
//
//
//    	/**
//    	 * <h2>Set file name of the current recording time lapse </h2>
//    	 *
//    	 * <p> NOTE: NOT the full pathname! Only the filename.
//    	 *
//    	 * @param filename name of the recording file
//    	 */
//    	public void setRecordingTimelapse(String filename){
//    		sharedVar.setRecordingTimelapse( filename );
//    	}
//

    	public boolean isConnected(){
    		return ssObj.isConnected();
    	}
    	
    	public boolean isOnline(){
    		final StcRprState stat = sharedVar.getStat();
    		return stat != StcRprState.LOGOUT && stat != StcRprState.OFFLINE && stat != StcRprState.LOGING;
    	}
    	
    	public boolean isOnlineRefreshedIn(final int s){
    		util.assertTrue(s > 0);
    		final long T0 = System.currentTimeMillis();
    		return isOnline() && ( T0 - sharedVar.getRefreshTime()  < s*1000);
    	}
    	
    	public long getRefreshTime(){
    		return sharedVar.getRefreshTime();
    	}
    	
    	public boolean isLogout(){
    		return sharedVar.getStat() == StcRprState.LOGOUT;
    	}
    	
    	public StcRprState getRprState(){
    		return sharedVar.getStat();
    	}
    	
    	public boolean hasLoginBefore(){
    		return (sharedVar.getPasswd() != null);
    	}
    	
    	public String getLibVersion(){
    		return StConst.getVersionInfo();
    	}
    	
    	public String getLocalIP(){
    		return sharedVar.getLocalIP();
    	}
    	
    	
    	public StClientInfo getLocalCopy(){
    		return sharedVar.getLocalCopy();
			//    		if(isLogin()){
			//    			return sharedVar.getLocalCopy();
			//    		}else{
			//    			stLog.info("Get local client-info from buffer");
			//    			return localBuffer.getLocalCopy22();
			//    		}
    	}


		/**
		 *
		 * used when client is offline.
		 *
		 * @return Local client-info from Local Buffer.
		 */
		public StClientInfo getLocalCopyFromBuffer(){
			return localBuffer.getLocalCopy22();
		}
    	
    	
    	public StClientID getClientID(){
    		if(isOnline()){
    			return sharedVar.getClientID();
    		}else{
    			stLog.info("Get local client-info from buffer");
    			return localBuffer.getClientID();
    		}
    	}
    	

    	public String getName(){
    		return sharedVar.getClientName();
    	}
    	
    	
    	public String getLabel(){
    		return sharedVar.getClientLabel();
    	}
    	
    	
    	public String getDscp(){
    		return sharedVar.getClientDscp();
    	}
    	
    	public StringBuffer debugRemoteClients(){
    		return sharedVar.debugRemoteClients();
    	}
    	
    	public StringBuffer dumpLocal(){
    		return sharedVar.dumpLocal();
    	}
    	
    	
    	/**
    	 * <h2> Get Friend Client-Info / Buffer </h2>
    	 * 
    	 * <li>from RAM, 	if local client is ONLINE
    	 * <li>from Buffer, if local client is OFFLINE
    	 * 
    	 * @param id client ID
    	 * @return client info
    	 * @throws ExpRemoteClientNoFound client info of the specified ID is not found
    	 */
    	public StClientInfo getFriend(final StClientID id) throws ExpRemoteClientNoFound{
    		if(id ==null || id.equalWith(StClientID.GEN_ID)){
    			throw new StcException.ExpRemoteClientNoFound(id);
    		}
    		if(isOnline()){
    			return sharedVar.getRemoteClientInfo(id);
    		}else{
    			stLog.warn("Client is OFFLINE. Get from buffer.");
    			return localBuffer.getRemoteClientInfo(id);
    		}
    	}
    	
    	
    	public StFriend getFriend2(final StClientID id) throws ExpRemoteClientNoFound{
    		if(id ==null || id.equalWith(StClientID.GEN_ID) || !isOnline()){
    			throw new StcException.ExpRemoteClientNoFound(id);
    		}
    		return sharedVar.getRemoteFriend(id);
    	}
    	
    	
    	public StClientInfo getFriendByName(String name) throws ExpRemoteClientNoFound{
    		if(isOnline()){
    			return sharedVar.getRemoteClientInfo(name);
    		}else{
    			stLog.warn("Client is OFFLINE. Get from buffer.");
    			return localBuffer.getRemoteClientInfo(name);
    		}
    	}
    	
    	
    	/**
    	 * <h2>Get the contact which is called in nearest past.</h2>
    	 * 
    	 * 
    	 * @return client info
    	 */
    	public StClientInfo getRecentConact(){
    		stLog.warn("TODO: ");
    		return null;
    	}
    	
    	
    	/**
    	 * <h2> Get client-info list of all friends. </h2>
    	 * 
    	 * <p> including gateways and family members. 
    	 * 
    	 * @return friend list
    	 */
    	public ArrayList<StClientInfo> getFriendList() {
    		if(!isOnline()){
    			stLog.info("Get friend list from buffer");
    			return localBuffer.getFriendList();
    		}
    		return sharedVar.getRemoteClientInfoList();
    	}
    	
    	
    	/**
    	 * <h2> Get Gateway List. </h2>
    	 * 
    	 * @return gateway list
    	 */
    	public ArrayList<StFriend> getGatewayList(boolean include_monitor)  {
    		if(!isOnline()){
    			stLog.info("Get gateway list from buffer");
    			return localBuffer.getGatewayList(include_monitor);
    		}
    		return sharedVar.getRemoteGatewayList(include_monitor);
    	}
    	
    	
    	/**
    	 * Get User Friend List.
    	 * 
    	 * @return user list
    	 */
    	public ArrayList<StClientInfo> getUserList() {
    		if(!isOnline()){
    			stLog.info("Get user list from buffer");
    			return localBuffer.getUserList();
    		}
    		return sharedVar.getRemoteUserList();
    	}
    	
    	String getPassword(){
    		return sharedVar.getPasswd();
    	}


    	/**
    	 * @deprecated
    	 * DO NOT use NAT TYPE to determine RELAY mode!
    	 * @return true: call is relayed
    	 */
        public boolean isCallRelay() {
			return sharedVar.isCallRelay();
		}


		public boolean isPassword(final String pass) {
			return sharedVar.isPassword(pass);
		}


		public ArrayList<StClientID> getSlave() {
    		if(!isOnline()){
    			stLog.warn("DO NOT return slave when Offline!");
    			return new ArrayList<>();
    		}
    		return sharedVar.getSlave();
		}


    }//EOF: Info
    
    
    public class LocalBuffer
    {
    	/**
    	 * Constructor
    	 */
    	private LocalBuffer(){
    	}
    	

		public StClientID getClientID() {
			return serObj.getClientID();
		}


		public void flushSer(){
    		serObj.flush();
    	}
    	
    	public StClientInfo getRemoteClientInfo(StClientID clt_id) 
    			throws StcException.ExpRemoteClientNoFound{
    		return serObj.getRemoteClientInfo(clt_id);
    	}
    	
    	public StClientInfo getRemoteClientInfo(String name)
    			throws ExpRemoteClientNoFound
    	{
    		return serObj.getRemoteClientInfo(name);
    	}
    	
    	
    	public ArrayList<StClientInfo> getFriendList() {
    		return serObj.getFriendList();
    	}
    	
    	public ArrayList<StClientInfo> getUserList() {
    		return serObj.getUserList();
    	}
    	
    	public ArrayList<StFriend> getGatewayList(boolean include_monitor) {
    		return serObj.getGatewayList(include_monitor);
    	}
    	
    	public StClientInfo getLocalCopy22(){
    		return serObj.getLocalCopy22();
    	}
    	
   	
		//    	/**
		//    	 * <p> Call this method after merging.
		//    	 * 
		//    	 * @deprecated 
		//    	 * 
		//    	 * @param tl
		//    	 */
		//    	public void updateTimeLapse(StcTimeLapse4Remote tl4r){
		//    		if(tl4r.getMergePath() == null){
		//    			throw new StErrUserError("Time lapse must be merged!");
		//    		}
		//    		params.objMgr.updateTimeLapse4Remote(tl4r);
		//    	}
    }
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Members
	////////////////////////////////////////////////////////////////////////////	
    public final StcParams 	params = StcParams.getInstance();
    public final StcTools 	tools = StcTools.getInstance();
    public final Info		info = new Info();
    private final LocalBuffer  localBuffer = new LocalBuffer();
    
	final StBridgeToUser rprBridge;
	final StcRprMainPulse 		mainPulse;
	//final StcRprUploadPulse 	uploadPulse;
	final StSerializedObject serObj = StSerializedObject.getInstance();
	
	final ConcurrentHashMap<StNetPacket.Command, StcServiceP2p>   mapP2pService;
	final ConcurrentHashMap<StNetPacket.Command, StcService4Srv>  mapSrvService;
	final StcSharedVar 		sharedVar = StcSharedVar.getInstance();
	final StcSessionObject 	ssObj; 
	
	

	@SuppressWarnings("deprecation")
	public StcRprObject( final StBridgeToUser rb )
	{
		synchronized (lock) {
			this.rprBridge = rb;
			this.mainPulse = new StcRprMainPulse();
			this.mapP2pService = new ConcurrentHashMap<>();
			this.mapSrvService = new ConcurrentHashMap<>();
			this.ssObj = new StcSessionObject(mainPulse);
			_ins = this;
		}


		// following does:
		// - initialize shared variable;
		// - add listener;
		// - create service;

		sharedVar.init();
		mainPulse.addListener(new StcRprMainPulseLis());

		mapSrvService.put(StNetPacket.Command.YouLogout, 	new StcService4SrvYouLogout());
		mapSrvService.put(StNetPacket.Command.SrvCheckClt, 	new StcService4SrvCheckClt());
		mapSrvService.put(StNetPacket.Command.SrvUpdateB, 	new StcService4SrvUpdateB());
		mapSrvService.put(StNetPacket.Command.SrvUpdateClt, new StcService4SrvUpdateClient());
		mapSrvService.put(StNetPacket.Command.SrvMessageToClt, new StcService4SrvMessageToClt());
		mapP2pService.put(StNetPacket.Command.P2pPing, new StcServiceP2pPing());
		mapP2pService.put(StNetPacket.Command.P2pUploadFile, new StcServiceP2pUploadFile());
		mapP2pService.put(StNetPacket.Command.P2pQueryFileCache, new StcServiceP2pQueryFileCache());

		stLog.info("RPR Object Constructed!");
	}

	
	StClientRequest getRequest(final StRequestID id){
		return StClientRequest.MANAGER.get(id);
	}
	
	
	StcServiceP2p getP2pService(StNetPacket.Command cmd){
		return mapP2pService.get(cmd);
	}
	
	
	StcService4Srv getClientService(StNetPacket.Command cmd){
		return mapSrvService.get(cmd);
	}

	
	/**
	 * <pre>
	 * If pkt is LOGIN/SIGN-UP REQUEST, client can be OFFLINE;
	 * Other packets: client must be ONLINE!
	 * 
	 * <pre>
	 * @param pkt packet
	 *
	 * @deprecated by sendRequest()
	 */
	void sendPacket(final StNetPacket pkt){
		ssObj.sendToSrv(pkt);
	}


	/**
	 * used by a client service to send a response to server/remote-client
	 * @param pkt - response packet
	 */
	void sendResponse(final StNetPacket pkt){
		ssObj.sendToSrv(pkt);
	}


	/**
	 * NOTE: called only in main-pulse!
	 *
	 * @param req request packet
	 */
	void sendRequest(final StClientRequest req){
		if( ! (rprObject.info.isOnline() || req.isPreLoginRequest())) 
		{
			stLog.error("This command cannot be sent when offline: " + req.toString());
			req.abort();
			return;
		}

		if(req.onPreSend()){
			StNetPacket pkt = req.buildOutPacket();
			stLog.debug(pkt.makeLogicFlow(true, false));
			ssObj.sendToSrv(pkt);
		}else{
			req.abort();
		}
	}


	/**
	 * @deprecated  do not block in RprObject! do it in app-object.
	 */
	public int pingRemote(final StClientInfo r_clt)
			throws ExpLocalClientOffline, ExpReqTimeout {
		StcReqP2pPing req = new StcReqP2pPing(r_clt, 3000, "Ping Remote Client: " + r_clt);
		req.send();
		req.waitForResult();
		if(req.getState() == StClientRequest.State.END){
			return req.getWaitTime();
		}
		else{
			throw new StErrUserError("Impossible!");
		}
	}


	/**
	 * @deprecated  do not block in RprObject! do it in app-object.
	 *
	 *
	 * <h2>Download a file on remote device. </h2>
	 * 
	 * <pre>
	 * NOTE: This method blocks.
	 *  
	 * This method will try to download from cache, first. 
	 * 
	 * If download is successful, RemoteFile properties file-on-cache and file-in-local-buffer will be set, 
	 * </pre>
	 * 
	 * @param r_clt - remote client 
	 * @param timeout - timeout for both upload and download, in ms
	 * @param rf - remote file object
	 * 
	 * @param local_dir	  Pathname under {AppHomeDir} <br/>
	 * 					 e.g. video/remote/gw02/date_2016-02-04 <br/>
	 * 
	 * @throws ExpFailToDownload fail to download
	 * 
	 * 
	 */
	public void downloadFile( 
			final StClientInfo 	r_clt, 
			final int 			timeout, 
			final StRemoteFile	rf, 
			final String 		local_dir 
		) throws ExpFailToDownload 
	{
		//final long ms_start = System.currentTimeMillis();
		if(!rf.hasCache()){
			stLog.trace("query file cache...");
			try {
				queryFileCache(r_clt, rf);
			} catch (ExpLocalClientOffline | ExpReqTimeout e) {
				e.printStackTrace();
				stLog.error(util.getExceptionDetails(e, ""));
				throw new ExpFailToDownload("Fail to Query File Cache: " + rf.dump());
			}
		}
		
		if(rf.hasCache()){
			stLog.trace("First, try to download from cache ...");
			try{
				downloadFileFromCache(rf, local_dir, timeout);
				stLog.info("Downloaded RF from cache: " + rf);
				return;
			}catch(ExpFailToDownload e){
				stLog.warn("Fail to download from cache, continue to download from device...");
			}
		}

		downloadFileFromDevice(r_clt, timeout, rf, local_dir);
		stLog.info("Remote File Downloaded: " + rf );
	}
	
	
	
	/**
	 * @deprecated  do not block in RprObject! do it in app-object.
	 *
	 *
	 * <h2>Download a file from FTP cache server.</h2>
	 * 
	 * <pre> file-in-local-buffer in remote-file object 'rf' is set, if downloading is successful.
	 * It is the pathname under {AppHomeDir} 
	 *    e.g. video/remote/gw02/date_2016-02-04/TimeLapse_20160204_123000.mp4
	 * 
	 * [Theodore: 2016-08-29] TODO: query file cache if fail to download from existing cache!
	 * 
	 * 
	 * </pre>
	 * 
	 * @param rf - remote file object, which has file-on-cache 
	 * @param local_dir  - Relative pathname under {AppHomeDir} <br/>
	 * 					e.g. video/remote/gw02/date_2016-02-04
	 * @param timeout - timeout of download from ftp cache, in millisecond
	 * 
	 * @throws ExpFailToDownload fail to download
	 * 
	 */
	private void downloadFileFromCache(
			final StRemoteFile rf, 
			final String local_dir, 
			final int timeout ) throws ExpFailToDownload 
	{
		try{
			stLog.trace(">>>>");
			final long ms_start = System.currentTimeMillis();
			
			stLog.trace("Downloading cache file: " +
					"\n\t " + util.stringFunc.wrap(rf.getFileOnCache()) + 
					"\n\t to local directory: " + util.stringFunc.wrap(local_dir ));

			if(!info.isOnline()){
				throw new ExpFailToDownload("Local Client is OFFLINE!");
			}
			
			// [Theodore: 2016-07-09] DO NOT download in labor listener! 
			// For the time-consuming job, create a thread to do the job. 
			// So that, if the job blocks or deadlock, Labor-Module still works then it times out. 
			//
	    	final StTask task = new StTaskFtpRecvFile(local_dir,  rf.getFileOnCache() ){
	    		@Override
				public void onFinish() {
	    			if(this.endCode == 0){
		    			// set file-in-local-buffer in remote-file 
		    			rf.setFileInLocalBuffer(this.localDir + File.separator + rf.filename );
						stLog.debug("Download finishes ^_^");
	    			}else{
	    				stLog.error("Fail to download remote file:  " + rf.dump() );
	    			}
				}
	    	};
	    	task.start();
	    	task.waitUntilTimeoutOrFinish(timeout);
	    	if(task.getEndCode() != 0){
	    		throw new ExpFailToDownload("Download task ends with error -- download cache file: " + rf.filename );
	    	}
			stLog.info("Download Cost(ms): " + (System.currentTimeMillis() - ms_start) + " -- cache file: " + rf.getFileOnCache() );
		} catch (ExpTaskTimeout e) {
			throw new ExpFailToDownload( "Task timeout. Fail to download cache file: " + rf.filename );
		}finally{
			stLog.trace("<<<<");
		}
	}
	
	
	/**
	 * @deprecated  do not block in RprObject! do it in app-object.
	 *
	 *
	 * Query file cache & set it in remote file.
	 * 
	 * @param ci remote client
	 * @param rf remote file object
	 * @return true: query success
	 * @throws ExpLocalClientOffline local client is offline
	 * @throws ExpReqTimeout    p2p request time out
	 */
	private boolean queryFileCache(final StClientInfo ci, final StRemoteFile rf) 
			throws ExpLocalClientOffline, ExpReqTimeout {
		final StcReqP2pQueryFileCache req = new StcReqP2pQueryFileCache(ci, rf, 3000);
		req.send();
		final Object res = req.waitForResult();

		if(req.isTimeout()){
			throw new ExpReqTimeout();
		}
		if(req.isResAllowed()){
			stLog.info("Get File Cache: '" + res + "'");
			rf.setFileOnCache((String)res);
			return true;
		}else{
			return false;
		}
	}
	
	
	
	/**
	 * @deprecated  do not block in RprObject! do it in app-object.
	 *
	 * <h2>Ask remote device to upload a file to cache, and download it from cache.</h2>
	 * 
	 * <p> This method:
	 * <li> Tells the remote client to upload file to FTP cache server; </li>
	 * <li> Downloads from the cache;  </li>
	 * </p>
	 * 
	 * <p> file-on-cache and file-in-local-buffer in remote-file object 'rf' are set, 
	 * if downloading is successful. Both are relative path. 
	 *    e.g. video/remote/gw02/date_2016-02-04/TimeLapse_20160204_123000.mp4
	 *    
	 *    
	 * @param ci  - Remote client
	 * 
	 * @param timeout  - Timeout for upload and download. Must be long enough for upload and download. <br/>
	 * e.g. A 3M video file takes about 10 seconds to upload to or download from the server, 
	 * when the FTP server has a band-width of 3M bps.
	 *  
	 * @param rf - remote file object
	 * @param local_dir - Directory's relative pathname under {AppHomeDir} 
	 * 
	 * @throws ExpFailToDownload  fail to download
	 * 
	 */
	private void downloadFileFromDevice(
			final StClientInfo 	ci, 
			final int 			timeout, 
			final StRemoteFile  rf,  
			final String 		local_dir 
			) throws ExpFailToDownload 
	{
		try{
			stLog.trace(">>>>");
			final long MS_START = System.currentTimeMillis();
			final int  LOOP_WAIT_MS = 2000;
			
			//stLog.debug("Remote file object: " + rf.dump() );
			if(rf.fileOnDevice == null || rf.fileOnDevice.length() < 1){
				stLog.warn("No device file is specified!");
				return;
			}
			
			rf.setFileOnCache(null);
			// [Theodore: 2016-08-29] upload request is allowed, as soon as upload begins.
			// So PING is not necessary. 
			// Keep checking if cache is available. 
			
			final StcReqP2pUploadFile req = new StcReqP2pUploadFile(ci, rf);
			req.send();
			req.waitForResult();
			if(!req.isResAllowed()){
				throw new ExpFailToDownload("Upload Request Fails. Abort upload: " + rf.fileOnDevice);
			}

			int ms_wait_upload = 0;
			stLog.trace("TIMEOUT of waiting upload done: " +  timeout );
			for(; ms_wait_upload < timeout; ms_wait_upload += LOOP_WAIT_MS){
				util.sleep(LOOP_WAIT_MS);
				stLog.trace("[" + (ms_wait_upload/LOOP_WAIT_MS) + "] Querying RF: " + rf);
				if(queryFileCache(ci, rf)){
					break;
				}
			}
			stLog.trace("Waited upload: " + ms_wait_upload );
			if(rf.getFileOnCache() == null){
				throw new ExpFailToDownload("Remote Upload Timeout");
			}
			
			downloadFileFromCache(rf, local_dir, timeout);
			stLog.info("Download Cost(ms): " + (System.currentTimeMillis() - MS_START)  + "-- Device File:  " + util.stringFunc.wrap(rf.fileOnDevice) );
		} catch (ExpLocalClientOffline e) {
			e.printStackTrace();
			throw new ExpFailToDownload(util.getExceptionDetails(e, "Client is OFFLINE"));
		} catch (ExpReqTimeout e) {
			e.printStackTrace();
			throw new ExpFailToDownload(util.getExceptionDetails(e, "Request Timeout!"));
		}finally{
			stLog.trace("<<<<");
		}
	}

	
	public void connect(){
		ssObj.connect();
	}
	
	public void disconnect(){
        //ssObj.disconnect();
		mainPulse.addNewEvent(new StcEvtDisconnect());
	}
	
	public void logout(){
		(new StExLogout()).trigger();
	}


	/**
	 * do not call in main-pulse.
	 */
	public void destroy() {
		mainPulse.stop();
		ssObj.destroy();
		StClientRequest.MANAGER.clear();
		_ins = null;
		stLog.info("RPR Object Destroyed!");
	}


	/**
	 * Set NAT type, if client is ONLINE. <br/>
	 *
	 * Called by android-pulse, when it recv NAT-CHANGE event from sip-pulse.
	 *
	 * @param nat latest NAT type
	 */
	public void setNatType(StJniNatType nat){
		sharedVar.setNatType(nat);
	}



	void sendEventToApp(StEvent e){
		rprBridge.addNewEvent(e);
	}


//	void sendMessage(int what){
//		rprBridge.sendMessageToGui(what, 0, 0, null);
//	}
//
//
//	void sendMessage(int what, int arg1, int arg2, Object obj){
//		rprBridge.sendMessageToGui(what, arg1, arg2, obj);
//	}
//
//
//	void sendMessage(int what,  Object obj){
//		rprBridge.sendMessageToGui(what, 0,0, obj);
//	}
}


class StExLogout extends StcRprExecution 
{
	private final StcSessionObject ssObj = StcRprObject.getInstance().ssObj;
	
	public StExLogout() {
		super("Client Logout", "");
	}

	
	@Override
	protected void perform() {
		sharedVar.setAutoLogin(false);
		sharedVar.clear();
		sharedVar.setStat(StcRprState.LOGOUT);
		ssObj.disconnect();
		
		stLog.debug("Save the cleared sharedVar, so that previous friends are NOT displayed any more!");
		final StcRprObject rprObject = StcRprObject.getInstance();
		rprObject.serObj.setCoreVar(sharedVar);
		rprObject.serObj.flush();  	// update the buffered object
		// rprObject.sendMessage(StMessageToGui.REMOTE_UPDATE);
		rprObject.sendEventToApp(new StcEvtRpr.InfoRemoteUpdate());
	}
}
