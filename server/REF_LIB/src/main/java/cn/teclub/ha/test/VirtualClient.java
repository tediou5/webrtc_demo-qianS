package cn.teclub.ha.test;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Timer;

import cn.teclub.common.ChuyuLog;
import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.client.rpr.StcReqSrvLogin;
import cn.teclub.ha.lib.StExpBreak;
import cn.teclub.ha.lib.StExpFamily;
import cn.teclub.ha.lib.StLoopThread;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.request.StNetPacket;
import cn.teclub.ha.request.StRequestID;
import cn.teclub.ha.request.StSocket;



public class VirtualClient extends ChuyuObj 
{
	private final static String BUDDLE_NAME = "st_virtual_client";
	
	private class OneClient extends StLoopThread
	{
		private long fakeReqID = System.currentTimeMillis() % 0x10000;
		
		StSocket		sockToSrv;
		StClientInfo	local;
		
		final String	user, passwd;
		final Timer 	heartbeatTimer;

		
		/**
		 * Constructor
		 * @param user
		 * @param passwd
		 */
		OneClient(final String user, final String passwd){
			super();
			this.user = user;
			this.passwd = passwd;
			
			this.heartbeatTimer = new Timer(true);
		}
		
		
		synchronized StRequestID getReqID(){
			return new StRequestID(fakeReqID++);
		}
		
		
		protected boolean loopStart(){
			try {
				sockToSrv = new StSocket(new Socket(srvHost, srvPort));
				stLog.debug("Client logs in: " + user + "/" + passwd);
				StNetPacket pkt = StNetPacket.buildReq (
						StNetPacket.Command.Login, 
						StNetPacket.Flow.CLIENT_TO_SERVER, StNetPacket.Code.NONE, 
						getReqID(),
						null, null,
						StcReqSrvLogin.buildData(user, passwd) );
				sockToSrv.sendPacket(pkt);
				
				stLog.debug("Start Timer ");
				heartbeatTimer.schedule(
						new java.util.TimerTask() {
							@Override
							public void run() {
								// [2016-9-17] a new thread a created for each timer.
								// stLog.info("======== HB Timer Task: "+ HDL_NAME);
								
								stLog.debug("send client-status: " + local);
								StNetPacket pkt = StNetPacket.buildReq (
										StNetPacket.Command.CltStatus, StNetPacket.Flow.CLIENT_TO_SERVER, StNetPacket.Code.NONE, 
										getReqID(),
										local.getClientID(), null,
										null );
								try {
									sockToSrv.sendPacket(pkt);
								} catch (IOException e) {
									sockToSrv.close();
									e.printStackTrace();
								}
							}
						},
						40*1000,
						30*1000 ); 
				return true;
			} catch (IOException e) {
				sockToSrv = null;
				e.printStackTrace();
				if(null == sockToSrv){
					stLog.error(util.getExceptionDetails(e, "Fail to connect to server! user: "  + user));
				}else{
					stLog.error(util.getExceptionDetails(e, "Fail to send Login request! user: "  + user));
				}
			}
			return false;
		}

		
		protected void loopEnd(){
			stLog.debug("Cancel Timer ");
			heartbeatTimer.cancel();
		}
		
		
		@Override
		protected void loopOnce() throws StExpFamily {
			if(sockToSrv == null || sockToSrv.isClosed()){
				stLog.error("No Connection to server from client: " + user);
				throw new StExpBreak();
			}
			
			try {
				final StNetPacket recv_pkt = sockToSrv.recvPacket();
				final StNetPacket.Command cmd = recv_pkt.getCmd();

				if(recv_pkt.isTypeResponseAllow()){
					if(cmd == StNetPacket.Command.Login){
						local = recv_pkt.dataGetClientInfo(0);
						stLog.debug("Login Success:" + local );
					}
					else if(cmd == StNetPacket.Command.CltStatus){
					}
					else{
						stLog.debug("Ignore ALLOW packet: " + recv_pkt);
					}
				}
				else if(recv_pkt.isTypeResponseDeny()){
					stLog.debug("Ignore DENY packet: " + recv_pkt);
				}
				else{
					stLog.debug("Server Request...");
					if(cmd == StNetPacket.Command.SrvCheckClt){
						stLog.debug("Server check connection to: " + local );
						sockToSrv.sendPacket(recv_pkt.buildAlw(null));
					}
					else if(cmd == StNetPacket.Command.SrvUpdateClt){
						local = recv_pkt.dataGetClientInfo(0);
						stLog.debug("Local Client-Info is updated:" + local );
						sockToSrv.sendPacket(recv_pkt.buildAlw(null));
					}
					else{
							stLog.debug("Ignore REQUST packet: " + recv_pkt);
					}
				}
			} catch (IOException | StExpFamily e) {
				e.printStackTrace();
				stLog.error("Client Abort: " + user);
				throw new StExpBreak();
			}
		}
	}//EOF
	
	
	final String keyPath;
	final String srvHost;
	final int	 srvPort;
	final int    COUNT; 
	final long 	 USER_START;
	final long 	 GW_START  ; 
	

	/**
	 * Constructor
	 */
	public VirtualClient(String[] args) {
		if(args.length != 3){
			System.out.println("Usage: java "+ getClass()  +" <COUNT> <USER-Start> <GW-Start> \n");
			System.out.println("Example: java "+ getClass()  + " 64  0xA0800 0xB0800\n");
			System.exit(-1);
		}
		this.COUNT = Integer.parseInt(args[0]);
		this.USER_START = Long.parseLong(args[1], 16);
		this.GW_START = Long.parseLong(args[2], 16);
		
		System.out.println("[Virtual Client] Loading resource bundle '" + BUDDLE_NAME  + "' in CLASSPATH ...");
		ResourceBundle res  = ResourceBundle.getBundle(BUDDLE_NAME, Locale.getDefault());
		URL location = getClass().getClassLoader().getResource(BUDDLE_NAME + ".properties");
		String log4j_cfg		= location.getFile() ; 
    	System.out.println("[Virtual Client] initialize log4j with: " + log4j_cfg);
    	ChuyuLog.setLog4jConf(log4j_cfg);
    	ChuyuLog.getInstance();
    	
    	StLoopThread.LoopMgr.initialize();
    	
    	this.keyPath	= res.getString("key_path").trim();
    	this.srvHost 	= res.getString("srv_host").trim();
    	this.srvPort 	= Integer.parseInt(res.getString("srv_port").trim());
    	
    	
    	stLog.info("\n\n");
    	stLog.info("[Virtual Client]" + COUNT +" Users from: user-" + Long.toHexString(USER_START) );
    	stLog.info("[Virtual Client]" + COUNT +" GWs   from:   gw-" + Long.toHexString(GW_START) );
    	
    	for(int i=0; i<COUNT; i++){
    		if(i>0 && i%4==0){
    			util.sleep(1000);
    			stLog.info("Created " + i + " Users & GWs");
    		}
    		
    		final String clt_name = "user-" + Long.toHexString(USER_START + i).toUpperCase();
    		OneClient clt = new OneClient(clt_name, "abcd1234");
        	clt.start();
        	
        	final String gw_name = "gw-" + Long.toHexString(GW_START + i).toUpperCase();
    		OneClient gw = new OneClient(gw_name, "abcd1234");
        	gw.start();
    	}
    	
    	stLog.info("[DONE] Created " + COUNT + " Users & GWs");
	}
	

	
	public static void main(String[] args){
		new VirtualClient(args);
	}

}




