package cn.teclub.ha.net.serv;


import java.io.IOException;

import org.hibernate.HibernateException;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.lib.StCoder;
import cn.teclub.ha.request.StNetPacket;
import cn.teclub.ha.request.StSocket4Pkt;
import cn.teclub.ha.request.StNetPacket.Code;

public class StService_PreLoginResetPasswd extends ChuyuObj 
{
	private final StNetPacket pkt;
	private final StSocket4Pkt sock;
	private final StSrvGlobal global = StSrvGlobal.getInstance(); 
	
	private StDBObject dbObj;
	
	
	StService_PreLoginResetPasswd(final StNetPacket req_pkt, final StSocket4Pkt sock ){
		this.pkt = req_pkt;
		this.sock = sock;
	}
	
	
	
	
	private StNetPacket prcRequest(){
		String clt_phone 	= pkt.dataGetEncString(0);
		String new_passwd	= pkt.dataGetEncString(StCoder.N_ENC_STR_LEN);
		String clt_sms_code = pkt.dataGetEncString(StCoder.N_ENC_STR_LEN * 2);
		
		
    	stLog.debug("[1] Verify SMS Code...");
    	if(!clt_sms_code.equalsIgnoreCase("000000")){
        	final StSrvSmsCode sms_code = global.getSmsCode(clt_sms_code);
        	if(sms_code == null || !sms_code.phone.equalsIgnoreCase( clt_phone) ){
        		stLog.warn("Cellphone "+ clt_phone +" Verify Failure: \n" + sms_code); 
        		return  pkt.buildDny(Code.ResetPasswd.DENY_SMS_CODE_INVALID, null);
        	}
    	}
    	
    	
    	stLog.debug("[2] query client record ...");
		final StModelClient mc = dbObj.queryModelClientByPhone(clt_phone);
		if(mc == null){
			return pkt.buildDny(Code.ResetPasswd.DENY_PHONE_NOT_FOUND, null);
		}
		
		stLog.debug("[3] update new passwd ...");
		mc.setPasswd(new_passwd);
		dbObj.updateRecord(mc);
		return pkt.buildAlw(null);
	}
	
	
	
	void process(){
		StNetPacket res_pkt = null;
		try {
			dbObj = global.dbObjMgr.getNextObject();
			res_pkt = prcRequest();
		} catch (InterruptedException e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "Fail to get db-object"));
			res_pkt = pkt.buildDny(StNetPacket.Code.DENY_DB_BUSY);
		} catch (HibernateException e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "Hibernate Error!"));
			res_pkt = pkt.buildDny(StNetPacket.Code.DENY_DB_ERROR);
		}finally{
			global.dbObjMgr.putObject(dbObj);
			dbObj = null;
		}
		
		
		util.assertNotNull(res_pkt);
		util.assertTrue(res_pkt.isTypeResponseAllow() || res_pkt.isTypeResponseDeny());
		try {
			sock.sendPacket(res_pkt);
		} catch (IOException e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "Fail to send response!"));
		}finally{
			stLog.info("Close Socket for reset passwd!");
			sock.close();
		}
	}
	
}
