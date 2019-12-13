package cn.teclub.ha.net.serv;

import java.io.IOException;

import org.hibernate.HibernateException;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.request.StNetPacket;
import cn.teclub.ha.request.StSocket4Pkt;

public class StService_PreLoginQuery extends ChuyuObj 
{
	private final StNetPacket pkt;
	private final StSocket4Pkt sock;
	private final StSrvGlobal global = StSrvGlobal.getInstance(); 
	
	private StDBObject dbObj;
	
	
	StService_PreLoginQuery(final StNetPacket req_pkt, final StSocket4Pkt sock ){
		this.pkt = req_pkt;
		this.sock = sock;
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
			stLog.info("Close Socket for PreLogin Query!");
			sock.close();
		}
	}
	
	
	private StNetPacket prcRequest_byMac(){
		final String mac_addr = pkt.dataGetString(0).trim().toUpperCase();
		final StModelClient mc = dbObj.queryModelClientByMacAddr(mac_addr);
		final String clt_name = (mc == null ? "" : mc.getName());
		return pkt.buildAlw(StNetPacket.Code.PreLoginQuery.QUERY_NAME_BY_MAC, util.stringFunc.toBuffer(clt_name));
	}
	
	
	private StNetPacket prcRequest_byPhone(){
		final String phone_num = pkt.dataGetString(0).trim();
		final StModelClient mc = dbObj.queryModelClientByPhone(phone_num);
		final String clt_name = (mc == null ? "" : mc.getName());
		return pkt.buildAlw(StNetPacket.Code.PreLoginQuery.QUERY_NAME_BY_PHONE, util.stringFunc.toBuffer(clt_name));
	}
	
	
	private StNetPacket prcRequest(){
		switch(pkt.getCode()){
		case StNetPacket.Code.PreLoginQuery.QUERY_NAME_BY_MAC:
			return prcRequest_byMac();
			
		case StNetPacket.Code.PreLoginQuery.QUERY_NAME_BY_PHONE:
			return prcRequest_byPhone();
			
		default:
			stLog.error("Unknown Code: " + pkt.getCode() + " -- Request Packet: " + pkt.dump());
			return pkt.buildDny(StNetPacket.Code.DENY_ERROR);
		}
	}
}
