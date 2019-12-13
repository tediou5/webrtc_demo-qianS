package cn.teclub.ha.net.serv;

import java.io.IOException;

import org.hibernate.HibernateException;

import cn.teclub.ha.lib.StEvent;
import cn.teclub.ha.lib.StEventListener;
import cn.teclub.ha.lib.StEventPulse;
import cn.teclub.ha.net.StExpConnectionLoss;
import cn.teclub.ha.request.StNetPacket;
import cn.teclub.ha.request.StPktLogin;
import cn.teclub.ha.request.StSocket4Pkt;



/**
 * Each pre-process is an event pulse.  <br/>
 * Server keeps a pool of pre-process objects, which handle the 1st packet from remote client. 
 * 
 * @author mancook
 *
 */
public class StSrvPreprocess extends StEventPulse implements StEventListener
{
	final static int MS_PERIOD 		= 5*1000;
	final static int MS_RECV_SLEEP 	= 20;
	
	class EvtNewSocket extends StEvtServer {
		final StSocket4Pkt sock;
		EvtNewSocket(StSocket4Pkt sock){
			this.sock = sock;
		}
	}
	
	
	private final String 		LIS_NAME;
	private final StSrvGlobal 	global = StSrvGlobal.getInstance(); 
	
	
	/**
	 * Constructor
	 * 
	 * @param evt_hdl_name
	 */
	StSrvPreprocess(String evt_hdl_name) {
		super(evt_hdl_name, 500, MS_PERIOD);
		LIS_NAME = "[Lis]" + evt_hdl_name;
		addListener(this);
	}
	
	
	/**
	 * trigger an event to pre-process (event pulse). 
	 * @param sock
	 */
	void onNewSocket(StSocket4Pkt sock){
		addNewEvent(new EvtNewSocket(sock));
	}

	
	// -------------------------------------------------------------------------
	// Listener Methods
	// -------------------------------------------------------------------------
	
	@Override
	public String getEvtLisName() {
		return LIS_NAME;
	}

	
	@Override
	public void handleEvent(StEvent event) {
		if(event instanceof EvtNewSocket){
			prcEvtNewSocket((EvtNewSocket)event);
		}
	}

	
	/**
	 * <pre>
	 * Get name in tb_client in DB by MAC address or PHONE number. 
	 * 
	 * Input format: 
	 *       [MAC]   :gw:{MAC_ADDRESS}
	 *       [Phone] :ph:{PHONE_NUMBER}
	 * e.g. 
	 * 		:gw:AA-CC-FF-FF-00-01
	 * 		:ph:18918085454
	 * 
	 * </pre>
	 * 
	 * @param raw_name
	 * @return
	 */
	private String getClientName(final String raw_name){
		String mac = null;
		String phone = null;
		if(raw_name.indexOf(":gw:") == 0 && raw_name.length() > 20){
			mac = raw_name.substring(4);
		}
		else if(raw_name.indexOf(":ph:") == 0 && raw_name.length() > 12){
			phone = raw_name.substring(4);
		}
		else{
			return raw_name;
		}
		
		String name = null;
		StDBObject dbObj = null;
		try {
			dbObj = global.dbObjMgr.getNextObject();
			StModelClient mc = null;
			if(mac != null){
				mc = dbObj.queryModelClientByMacAddr(mac);
			}
			if(phone != null){
				mc = dbObj.queryModelClientByPhone(phone);
			}
			name = (mc == null) ? null : mc.getName();
		} catch (InterruptedException e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "Fail to get db-object"));
		} catch (HibernateException e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "Hibernate Error!"));
		}finally{
			global.dbObjMgr.putObject(dbObj);
			dbObj = null;
		}
		
		return name;
	}

	
	
	/**
	 * <pre>
	 * There is a small bug for attack: 
	 * if un-existing clients keep on logging in, useless connection objects increase. 
	 * Because a connection is marked by CLIENT-NAME.
	 * 
	 * </pre>
	 * 
	 * @param evt
	 */
	private void prcEvtNewSocket(EvtNewSocket evt) {
		StNetPacket pkt = null;
		
		stLog.debug("[1] recv 1st packet in " + LIS_NAME + "...");
		try {
			for(int i=0; i<MS_PERIOD; i+=MS_RECV_SLEEP){
				pkt = evt.sock.recvPacket();
				if(pkt != null){
					break;
				}
				util.sleep(MS_RECV_SLEEP);
			}
		} catch (StExpConnectionLoss | IOException e) {
			e.printStackTrace();
			stLog.error("");
			stLog.error(util.getExceptionDetails(e, "Fail to Recv 1st Packet: " + toString()));
		}
		if(pkt == null){
			stLog.error("Close New Socket!");
			evt.sock.close();
			return;
		}
		
		
		
		if(pkt.getCmd() == StNetPacket.Command.PreLoginQuery){
			final StService_PreLoginQuery service = new StService_PreLoginQuery(pkt, evt.sock);
			service.process();
			return;
		}
		
		if(pkt.getCmd() == StNetPacket.Command.ResetPasswd){
			final StService_PreLoginResetPasswd service = new StService_PreLoginResetPasswd(pkt, evt.sock);
			service.process();
			return;
		}
		
		
		if(pkt.getCmd() == StNetPacket.Command.SmsVerifyCode){
			final StService_PreLoginSmsCode service = new StService_PreLoginSmsCode(pkt, evt.sock);
			service.process();
			return;
		}
		
		if(pkt.getCmd() == StNetPacket.Command.Signup){
			final StService_PreLoginSignup service = new StService_PreLoginSignup(pkt, evt.sock, evt.TS);
			service.process();
			return;
		}
		
		if(pkt.getCmd() != StNetPacket.Command.Login){
			stLog.error("Unexpected 1st Packet: " + pkt.dump());
			evt.sock.close();
			return;
		}
		
		//
		// for LOGIN, keep the connection for other commands
		//
		final String raw_name = (new StPktLogin(pkt)).getDataName();
		final String clt_name = getClientName(raw_name);
		if(clt_name == null){
			stLog.error("fail to find client '" + raw_name + "'"); 
			try {
				evt.sock.sendPacket(pkt.buildDny(StNetPacket.Code.Login.DENY_USER_NAME_ERROR));
			} catch (IOException e) {
				stLog.error(util.getExceptionDetails(e, "Fail to send LOGIN DENY"));
			}
			evt.sock.close();
			return;
		}

		global.connMgr.onNewSocket(evt.sock, clt_name, pkt, evt.TS);
	}
}
