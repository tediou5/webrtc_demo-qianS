package cn.teclub.ha.net.serv.request;

import java.nio.ByteBuffer;

import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.net.StMessage;
import cn.teclub.ha.net.serv.StDBObject;
import cn.teclub.ha.net.serv.StModelClient;
import cn.teclub.ha.net.serv.StModelMessage;
import cn.teclub.ha.net.serv.StSrvGlobal;
import cn.teclub.ha.request.StNetPacket;



/**
 * 
 * @author mancook
 *
 */
public class StSrvService_MessageToSrv extends StSrvDbService
{
	public StSrvService_MessageToSrv() {
		super(StNetPacket.Command.MessageToSrv);
	}

	
	/**
	 * <pre>
	 * 
	 * When an APPLY message arrives,
	 *    1) if applied client is a GW/Monitor, 	send message to its master;
	 *    2) if applied client is a user, 			send message to it;
	 *    
	 * Currently, Monitor is processed as a gateway. i.e. The APPLY message is sent to its master.
	 * In future, monitor can be managed by a gateway, so that, when user selects a gateway in FamboU, 
	 * all managed monitors are displayed. In this case, monitor is managed as: 
	 *    MONITOR <-- GATEWAY <-- Master (USER)
	 *    
	 * </pre>
	 * 
	 * @param conn
	 * @param msg
	 * @param pkt
	 */
	private void onRequest_Apply(
			final StSrvConnLis conn_lis, final StDBObject db_obj, 
			final StMessage msg, final StNetPacket pkt )
	{
		final StClientID id_a = msg.getCltA();
		final StClientID id_b = msg.getCltB();
		
		final StModelClient mc_contact = db_obj.loadClient(id_b, true);
		if(mc_contact.hasFriend(id_a)){
			final StClientInfo ci_a = msg.getApplicantInfo();
			final StClientInfo ci_b = msg.getContactInfo();
			stLog.warn("Alread Friend: " + ci_a +  " ~ " + ci_b);
			finishRequest(pkt.buildDny(StNetPacket.Code.MessageToSrv.DENY_FRIEND_EXIST));
			return;
		}
		final StClientID  target_id; 
		if( mc_contact.isFlag_Gateway() || mc_contact.isFlag_Monitor()) {
			target_id = mc_contact.getMaster();
			if(target_id == null){
				stLog.warn("GW/Monitor has no master -- " + mc_contact);
				finishRequest(pkt.buildDny(StNetPacket.Code.MessageToSrv.DENY_DEVICE_NO_MASTER));
				return;
			}
		}else{
			target_id = mc_contact.getClientID();
		}
		
		StMessage apply_msg = db_obj.getMessageApply(msg.getCltA(), msg.getCltB());
		if(apply_msg == null){
			db_obj.addMessage(msg);
			stLog.info("Add a new APPLY in DB: " + msg);
			apply_msg = msg;
		}else{
			stLog.info("Pending APPLY Exist: " + apply_msg.dump());
		}
		
		final StSrvConnection target_conn = StSrvGlobal.getInstance().connMgr.getConnection(target_id);
		if(target_conn != null){
			stLog.info("Send APPLY Message To: " + target_conn);
			target_conn.sendMessage(apply_msg);
		}else{
			stLog.debug("No Connection to target client: " + target_id.getHex());
		}
		finishRequest(pkt.buildAlw(null));
	}

	
	
	private void onRequest_ApplyApproved(
			final StSrvConnLis conn_lis, final StDBObject db_obj, 
			final StMessage msg, final StNetPacket pkt )
	{
		// add contact...
		final StClientInfo ci_applicant = msg.getApplicantInfo();
		final StClientInfo ci_contact = msg.getContactInfo();
		final StClientID id_a = msg.getCltA();
		final StClientID id_b = msg.getCltB();
		util.assertTrue(ci_applicant.getClientID().equalWith(id_a));
		util.assertTrue(ci_contact.getClientID().equalWith(id_b));
		
		
		stLog.debug("[1] Add Friendship: "+ ci_applicant +" <----> " + ci_contact);
		if(!db_obj.addFriendship(ci_applicant.getClientID(), ci_contact.getClientID(), false)){
			finishRequest(pkt.buildDny(null));
			return;
		}
		
		// NOTE: remote client may be client-B OR its admin user 
		stLog.debug("[2] update ONLINE clients");
		updateFriendClientInfo(ci_applicant.getClientID());
		updateFriendClientInfo(ci_contact.getClientID());
		
		stLog.debug("[3] Approvd APPLY & Send ALLOW");
		db_obj.updateRecord(new StModelMessage(msg));
		finishRequest(pkt.buildAlw(null));
		stLog.info("Approved APPLY: " + msg);
		
		
		final StModelClient mc_applicant = db_obj.loadClient(id_a, true);
		final StClientID  src_id;
		if(mc_applicant.isFlag_Gateway() || mc_applicant.isFlag_Monitor()){
			src_id = mc_applicant.getMaster();
			//stLog.info("#### To inform applicant's master  -- [applicant]" + mc_applicant);
		}else{
			//stLog.info("#### To inform applicant -- [applicant]" + mc_applicant);
			src_id = id_a;
		}
		
		final StSrvConnection conn_src = global.connMgr.getConnection(src_id);
		if(conn_src != null){
			stLog.info("Send APPLY Approval To: " + conn_src);
			conn_src.sendMessage(msg);
		}else{
			stLog.debug("No Connection to: " + src_id );
		}
	}
	
	
	private void onRequest_ApplyRejected(
			final StSrvConnLis conn_lis, 
			final StDBObject db_obj, 
			final StMessage msg, 
			final StNetPacket pkt) 
	{
		final StClientInfo ci_applicant = msg.getApplicantInfo();
		final StClientInfo ci_contact = msg.getContactInfo();
		final StClientID id_a = msg.getCltA();
		final StClientID id_b = msg.getCltB();
		util.assertTrue(ci_applicant.getClientID().equalWith(id_a));
		util.assertTrue(ci_contact.getClientID().equalWith(id_b));
		
		
		db_obj.updateRecord(new StModelMessage(msg));
		finishRequest(pkt.buildAlw(null));
		stLog.info("Rejected APPLY: " + msg);
		
		final StModelClient mc_applicant = db_obj.loadClient(id_a, true);
		final StClientID  src_id;
		if(mc_applicant.isFlag_Gateway() || mc_applicant.isFlag_Monitor()){
			src_id = mc_applicant.getMaster();
			stLog.info("#### To inform applicant's master  -- [applicant]" + mc_applicant);
		}else{
			stLog.info("#### To inform applicant -- [applicant]" + mc_applicant);
			src_id = id_a;
		}
		
		final StSrvConnection conn_src = global.connMgr.getConnection(src_id);
		if(conn_src != null){
			stLog.info("Send APPLY Reject To: " + conn_src);
			conn_src.sendMessage(msg);
		}else{
			stLog.info("#### No Connection to: " + src_id );
		}
	}
	
	
	private void onRequest_Data(final StSrvConnLis conn_lis, final StDBObject db_obj, final StMessage msg, final StNetPacket pkt){
		db_obj.addMessage(msg);
		final ByteBuffer buf = ByteBuffer.allocate(8);
		buf.putLong(msg.getId());
		buf.rewind();
		finishRequest(pkt.buildAlw(buf));
		util.assertTrue(false, "UnImplemented!");
	}


	@Override
	protected void onRequest(
			final StSrvConnLis conn_lis, 
			final StDBObject db_obj,
			final StModelClient mc_self, 
			final StNetPacket pkt)
	{
		final StMessage msg = new StMessage(pkt.getDataBuffer());
		stLog.debug("Recv Message: " + msg.dump());
		msg.setEndTime(null);
		
		if(msg.isFlagData()){
			onRequest_Data(conn_lis, db_obj, msg, pkt);
		}
		else if(msg.isFlagApply()){
			onRequest_Apply(conn_lis, db_obj, msg, pkt);
		}
		else if(msg.isFlagApplyApproved()){
			onRequest_ApplyApproved(conn_lis, db_obj, msg, pkt);
		}
		else if(msg.isFlagApplyRejected()){
			onRequest_ApplyRejected(conn_lis, db_obj, msg, pkt);
		} 
		else {
			util.assertTrue(false, "Impossible !");
		}
	}
}
