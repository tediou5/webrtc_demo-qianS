package cn.teclub.ha.net.serv.request;

import java.nio.ByteBuffer;

import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.net.serv.StDBObject;
import cn.teclub.ha.net.serv.StModelClient;
import cn.teclub.ha.net.serv.StSrvGlobal;
import cn.teclub.ha.request.StNetPacket;




/**
 * 
 * @author mancook
 *
 */
public class StSrvService_AddContact extends StSrvDbService 
{
	public StSrvService_AddContact() {
		super(StNetPacket.Command.AddContact);
	}



	@Override
	protected void onRequest(
			final StSrvConnLis conn_lis, final StDBObject db_obj,
			final StModelClient mc_self, final StNetPacket pkt) 
	{
		final StClientID id1 = mc_self.getClientID();
		final StClientID id2 = pkt.dataGetClientID(0);
		final String clt2_key = pkt.dataGetString(StClientID.OBJLEN);
		
		final StClientInfo ci2 = db_obj.loadClient(id2);
		stLog.warn("TODO: add KEY for each client!");
		if(ci2 == null || !clt2_key.equals("ABCD1234")){
			finishRequest(pkt.buildDny(null));
			return;
		}
		
		global.debugCode(new StSrvGlobal.DebugRoutine() {
			@Override
			public void execute() {
				stLog.info("#### mc_self (before adding frienship) " + mc_self.dump());
			}
		});
		
		stLog.debug("[1] Add Contact {"+ ci2 +"} by Key '" + clt2_key + "'");
		if(!db_obj.addFriendship(id1, id2, false)){
			finishRequest(pkt.buildDny(null));
			return;
		}
		
		stLog.debug("[2] update model-client in both connections...");
		final StModelClient mc1 = db_obj.loadClient(id1, true);
		final StModelClient mc2 = db_obj.loadClient(id2, true);
		global.debugCode(new StSrvGlobal.DebugRoutine() {
			@Override
			public void execute() {
				stLog.info("#### mc1 (after adding frienship) " + mc1.dump());
				stLog.info("#### mc2 (after adding frienship) " + mc2.dump());
			}
		});
		
		conn_lis.setModelClient(mc1);
		updateFriendClientInfo(id2);
		
		stLog.info("Client " + mc1.getName() + " adds friend: " + mc2.getName() );
		
		stLog.debug("[3] ALLOW " + cmd);
		ByteBuffer buf1 = mc1.toBuffer(true);
		ByteBuffer buf2 = mc2.toBuffer(false);
		ByteBuffer buf = ByteBuffer.allocate(buf1.remaining() + buf2.remaining());
		buf.put(buf1); 
		buf.put(buf2);
		buf.rewind();
		finishRequest(pkt.buildAlw(buf));
		
		
	}
}
