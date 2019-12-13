package cn.teclub.ha.net.serv.request;

import java.util.ArrayList;

import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.request.StNetPacket;
import cn.teclub.ha.request.StSocket4Pkt;


public interface StSrvConnMgr {
	public void onNewSocket(
			final StSocket4Pkt sock, 
			final String clt_name, 
			final StNetPacket su_pkt, 
			final long ts_start);
	
	public StSrvConnection getConnection(final String name);
	public StSrvConnection getConnection(final StClientID id);
	public ArrayList<StSrvConnection> getConnection(final ArrayList<StClientID> id_list);
	public void deleteAll();
	public StringBuffer debug_getCount(StringBuffer sbuf);
	
	void addConnection(final StClientID clt_id,  final StSrvConnection conn);
	void deleteConnection(StSrvConnection conn);
	
	StringBuffer dump();
}
