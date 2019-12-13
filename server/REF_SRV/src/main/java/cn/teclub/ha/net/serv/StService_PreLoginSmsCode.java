package cn.teclub.ha.net.serv;

import java.io.IOException;

import org.hibernate.HibernateException;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.lib.StExpBreak;
import cn.teclub.ha.request.StNetPacket;
import cn.teclub.ha.request.StNetPacket.Code;
import cn.teclub.ha.request.StSocket4Pkt;
import cn.teclub.sms.StSmsOpt_SendX;
import cn.teclub.sms.StSmsOpt_SendX.SmsType;

public class StService_PreLoginSmsCode extends ChuyuObj 
{

	private final StNetPacket pkt;
	private final StSocket4Pkt sock;
	private final StSrvGlobal global = StSrvGlobal.getInstance(); 
	
	private StDBObject dbObj = null;
	
	
	StService_PreLoginSmsCode(final StNetPacket req_pkt, final StSocket4Pkt sock ){
		this.pkt = req_pkt;
		this.sock = sock;
	}
	
	
	
	void process(){
		final byte req_code = pkt.getCode() == 0 ? Code.SmsVerifyCode.REQ_SIGNUP : pkt.getCode(); 
		final String phone = pkt.dataGetString(0);
		StNetPacket res_pkt = null;
		
		try{
			final StSmsOpt_SendX.SmsType sms_type;
			switch(req_code){
			case Code.SmsVerifyCode.REQ_SIGNUP:
				sms_type = SmsType.Signup;
				break;
			case Code.SmsVerifyCode.REQ_RESET_PASSWD:
				sms_type = SmsType.ResetPasswd;
				break;
			default:
				stLog.error("unknown code sms cmd:" + req_code);
				res_pkt = pkt.buildDny(StNetPacket.Code.SmsVerifyCode.DENY_UNKNOWN_REQ);
				throw new StExpBreak();
			}
			
			
			if(sms_type != SmsType.Signup){
				stLog.warn("## check if phone number exists");
				dbObj = global.dbObjMgr.getNextObject();
			
				final StModelClient mc = dbObj.queryModelClientByPhone(phone);
				if(mc == null){
					stLog.error("no client found by phone: '" + phone + "'");
					res_pkt = pkt.buildDny(StNetPacket.Code.SmsVerifyCode.DENY_UNREG_PHONE_NUM);
					throw new StExpBreak();
				}
				stLog.warn("## find client by phone: '" + phone + "'");
				stLog.warn("## -- " + mc.dump());
			}
			
			final StSrvSmsCode sms_rand_num = StSrvSmsCodeMgr.getInstance().createSmsCode(phone);
			new StSmsOpt_SendX(sms_rand_num.phone, sms_rand_num.code, sms_type);
			res_pkt = pkt.buildAlw(null);
		}catch(StExpBreak e){
			
		} catch (IOException e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "Fail to send SMS SingupCode"));
			res_pkt = pkt.buildDny(StNetPacket.Code.SmsVerifyCode.DENY_SEND_SMS_FAILURE);
		} catch (InterruptedException e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "Fail to get db-object"));
			res_pkt = pkt.buildDny(StNetPacket.Code.DENY_DB_BUSY);
		} catch (HibernateException e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "Hibernate Error!"));
			res_pkt = pkt.buildDny(StNetPacket.Code.DENY_DB_ERROR);
		}finally{
			if(dbObj != null){
				global.dbObjMgr.putObject(dbObj);
				dbObj = null;
			}
		}
		
		util.assertNotNull(res_pkt);
		util.assertTrue(res_pkt.isTypeResponseAllow() || res_pkt.isTypeResponseDeny());
		try {
			sock.sendPacket(res_pkt);
		} catch (IOException e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "Fail to send response!"));
		}finally{
			stLog.info("Close Socket for PreLogin SmsCode Request");
			sock.close();
		}
		
		
		
		/*
		// NOTE: make it work with old cmd: SmsSignUpCode (0x010D)
		final byte req_code = pkt.getCode() == 0 ? Code.SmsCode.REQ_SIGNUP : pkt.getCode(); 
		final String phone = pkt.dataGetString(0);
		
		
		final StSmsOpt_SendX.SmsType sms_type;
		switch(req_code){
		case Code.SmsCode.REQ_SIGNUP:
			sms_type = SmsType.Signup;
			break;
		case Code.SmsCode.REQ_RESET_PASSWD:
			//TODO: check if phone number exists
			sms_type = SmsType.ResetPasswd;
			break;
		default:
			sms_type = null;
			stLog.warn("unknown code sms cmd:" + req_code);
			break;
		}

		if(sms_type != null){
			final StSrvSmsCode sms_rand_num = StSrvSmsCodeMgr.getInstance().createSmsCode(phone);
			try {
				new StSmsOpt_SendX(sms_rand_num.phone, sms_rand_num.code);
			} catch (IOException e) {
				e.printStackTrace();
				stLog.error(util.getExceptionDetails(e, "Fail to send SMS SingupCode"));
			}
		}
		
		
		stLog.info("Close SMS Code Socket!");
		evt.sock.close();
		return;
		*/
		
		
		
		
		/*
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
			stLog.info("Close Socket for PreLogin Query!");
			sock.close();
		}
		*/
	}
	

}
