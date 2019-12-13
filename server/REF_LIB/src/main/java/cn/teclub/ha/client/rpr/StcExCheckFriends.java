package cn.teclub.ha.client.rpr;

import java.util.ArrayList;

import cn.teclub.ha.client.StcException.ExpLocalClientOffline;
import cn.teclub.ha.client.StcException.ExpRemoteClientNoFound;
import cn.teclub.ha.net.StClientID;


/**
 * Make sure client-info of all friends are loaded from server. <br/>
 * 
 * To Fix: CMD_CLIENT_A_QUERY_B timeout and cause empty friend list.
 * 
 * 
 * @author mancook
 *
 */
public class StcExCheckFriends extends StcRprExecution
{
	@Override
	protected void perform() {
		final StcRprState STATE = sharedVar.getStat();
		if(STATE != StcRprState.UPDATE_FRD){
			return;
		}
		
		final ArrayList<StClientID> ID_LIST = sharedVar.getFriendIDList();
		ArrayList<StClientID>  list = new ArrayList<StClientID> ();
		for(StClientID id : ID_LIST){
			try {
				sharedVar.getRemoteClientInfo(id);
			} catch (ExpRemoteClientNoFound e) {
				list.add(id);
			}
		}
		if(list.size() == 0){
			sharedVar.setStat(StcRprState.IDEAL);
			stLog.info("do nothing -- local client has no friend.");
			return;
		}
		
		try {
			StcReqSrvClientAQueryB req;
			req = new StcReqSrvClientAQueryB(list, "Query missing frends. Count=" + list.size());
			req.startRequest();
			sharedVar.setStat(StcRprState.QUERYING_FRD);
			stLog.info("Query missing frends. Count=" + list.size());
		} catch (ExpLocalClientOffline e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e, "Fail to Query Friends!"));
		}
	}
}