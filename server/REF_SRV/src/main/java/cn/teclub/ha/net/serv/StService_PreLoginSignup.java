package cn.teclub.ha.net.serv;

import java.io.IOException;

import org.hibernate.HibernateException;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.net.StClientType;
import cn.teclub.ha.request.StNetPacket;
import cn.teclub.ha.request.StPktSignup;
import cn.teclub.ha.request.StSocket4Pkt;
import cn.teclub.ha.request.StNetPacket.Code;



/**
 * <h1> Sign up in pre-process pulse </h1>
 * 
 *  DO NOT sign up in connection pulse! <br/>
 *  Reason:  connection is indexed by 'client-name' in the global connection manager. 
 *  There is NO valid client-name before sign-up!
 * 
 * 
 * 
 * @author mancook
 *
 */
public class StService_PreLoginSignup extends ChuyuObj 
{
	private final StPktSignup pkt;
	private final StSocket4Pkt sock;
	private final long evtTS;
	private final StSrvGlobal global = StSrvGlobal.getInstance(); 
	
	private StDBObject dbObj;
	
	
	StService_PreLoginSignup(final StNetPacket req_pkt, final StSocket4Pkt sock, long evt_ts ){
		this.pkt = new StPktSignup(req_pkt);
		this.sock = sock;
		this.evtTS = evt_ts;
	}
	
	
	void process(){
		StNetPacket err_pkt = null;
		try {
			dbObj = global.dbObjMgr.getNextObject();
			prcRequest(dbObj);
		} catch (InterruptedException e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "Fail to get db-object"));
			err_pkt = pkt.buildDny(StNetPacket.Code.DENY_DB_BUSY);
		} catch (HibernateException e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "Hibernate Error!"));
			err_pkt = pkt.buildDny(StNetPacket.Code.DENY_DB_ERROR);
		}finally{
			global.dbObjMgr.putObject(dbObj);
			dbObj = null;
		}

		if(err_pkt != null){
			finishRequest(err_pkt, true);
		}
	}

	
	
	private void finishRequest(StNetPacket res_pkt, boolean close_socket){
		util.assertNotNull(res_pkt);
		util.assertTrue(res_pkt.isTypeResponseAllow() || res_pkt.isTypeResponseDeny());
		try {
			sock.sendPacket(res_pkt);
		} catch (IOException e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "Fail to send response!"));
		}
		
		if(close_socket){
			stLog.info("Close Socket for PreLogin SmsCode Request");
			sock.close();
		}
	}
	
	
	protected void prcRequest(  StDBObject db_obj ) 
	{
	   	String clt_name 	= pkt.getDataName();
    	String clt_passwd 	= pkt.getDataPassword();
    	String clt_label 	= pkt.getDataLabel();
    	String clt_phone 	= pkt.getDataPhone();
    	String clt_sms_code = pkt.getDataSmsCode();
    	String clt_mac_addr = pkt.getDataMacAddr();
    	final byte req_code = pkt.getCode();
    	
    	stLog.debug("[1] Check if Phone/MAC has registerd...");
    	StModelClient old_mc = null; // query old client
    	final boolean is_gw_mon;
    	
    	if(req_code == Code.Signup.REQUST_GATEWAY || req_code == Code.Signup.REQUST_MONITOR ){
    		is_gw_mon = true;
    		stLog.debug("Check if MAC address has been registered, before.");
    		old_mc = db_obj.queryModelClientByMacAddr(clt_mac_addr);
    	} else{
    		is_gw_mon = false;
    		stLog.debug("Check if phone number has been registered, before.");
    		old_mc = db_obj.queryModelClientByPhone(clt_phone);
    	}
    	
    	if(old_mc != null){
	    	final String msg = "Mac/Phone has signed up " +
	    			"-- Client Exists: (Phone:"+old_mc.getPhone() + 
	    			", Mac:"+old_mc.getMacAddr()+", Name:"+  old_mc.getName() +") exists!";
	    	stLog.warn(util.testMilestoneLog(msg)); 
	    	stLog.warn("Sign Up Abort. However, still send ALLOW packet! ");
	    	StClientInfo ci = old_mc;  //global.clientMgr.getClient(old_mc.getClientID());
	    	finishRequest( pkt.buildAlw(Code.Signup.ALLOW_USE_OLD, ci.toBuffer(false)), true);
	    	return;
    	}
    	
    	stLog.debug("[1.1] Verify SMS Code...");
    	if(!clt_sms_code.equalsIgnoreCase("000000")){
        	final StSrvSmsCode sms_code = global.getSmsCode(clt_sms_code);
        	if(sms_code == null || !sms_code.phone.equalsIgnoreCase( clt_phone) ){
        		stLog.warn("Cellphone "+ clt_phone +" Verify Failure: \n" + sms_code); 
        		finishRequest( pkt.buildDny(Code.Signup.DENY_SMS_CODE_INVALID, null), true);
    	    	return;
        	}
    	}
    
    	
    	if(is_gw_mon){
    		stLog.debug("[2] use a temp unique name for inserting");
    		clt_name 	= clt_mac_addr;
    		clt_passwd 	= StSrvConfig.SIGNUP_GW_PASSWD;
    	}else{
    		stLog.debug("[2] Check name ONLY for user ...");
    		old_mc = db_obj.queryClientByName(clt_name);
    		if(old_mc != null){
    			stLog.warn(util.testMilestoneLog("Name '" + clt_name + "' has been used!")); 
    			finishRequest( pkt.buildDny(Code.Signup.DENY_NAME_EXIST, null), true);
    	    	return;
    		}	
    	}

		
		stLog.debug("[3] Add Client & Update SIP...");
    	final StModelClient mc0 ;

    	if(req_code == Code.Signup.REQUST_GATEWAY  ){
    		mc0  = new StModelClient(StClientType.GATEWAY);
    		mc0.setMacAddr(clt_mac_addr);
    	}
    	else if(req_code == Code.Signup.REQUST_MONITOR  ){
    		mc0  = new StModelClient(StClientType.MONITOR);
    		mc0.setMacAddr(clt_mac_addr);
    	}
    	else {
    		mc0  = new StModelClient(StClientType.USER);
    		mc0.setPhone(clt_phone);
    	}
    	mc0.setName(clt_name);
    	mc0.setPasswd(clt_passwd);
    	mc0.setLabel(clt_label);
    	mc0.setDscp("<No description...>");

    	
    	final StModelSipAcct m_sip = db_obj.getModelSipAcctFree(is_gw_mon);
    	m_sip.setFlag(0x01); 	// set ASSIGN bit
    	mc0.setSipAcct(m_sip);
    	db_obj.addRecord(mc0); 
    	db_obj.updateRecord(m_sip);
    	stLog.warn("## add new client record:" + mc0.dump());
    	
    	if(is_gw_mon){
    		stLog.debug("[3.1] GW/Mon updates name & label ");
    		final String clt_id = "" + mc0.getClientID().getId();
        	mc0.setName( (req_code == Code.Signup.REQUST_MONITOR ? "m" : "gw") + clt_id);
        	mc0.setLabel((req_code == Code.Signup.REQUST_MONITOR ? "MON " : "GW ") + clt_id);
        	db_obj.updateRecord(mc0);
        	stLog.warn("## update monitor/gateway name & label: " + mc0.dump() );
        	clt_name = mc0.getName();
    	}
    
    	stLog.info("PreLogin sign-up OK: "+ clt_name +" -- Add a new connection ...");
    	
    	global.connMgr.onNewSocket(sock, clt_name, pkt, evtTS);
	}
}
