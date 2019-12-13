package cn.teclub.ha.net.serv.request;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.lib.StEvent;
import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.request.StNetPacket;
import cn.teclub.ha.request.StSocket4Pkt;

class StSrvPulseConnMgr extends ChuyuObj implements StSrvConnMgr
{
    ////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS AND METHODS
	////////////////////////////////////////////////////////////////////////////
    private static StSrvPulseConnMgr _ins = new StSrvPulseConnMgr();
    
    public static StSrvConnMgr getInstance(){
        return _ins;
    }
    
    

    ////////////////////////////////////////////////////////////////////////////
    // Instance Members
	////////////////////////////////////////////////////////////////////////////
    private final ConcurrentHashMap<StClientID, StSrvConnection> 	remoteList;
    private final ConcurrentHashMap<String, StSrvConnection> 		remoteListByName;
    
    /**
     * Constructor
     */
    private StSrvPulseConnMgr(){
		this.remoteList = new ConcurrentHashMap<StClientID, StSrvConnection>();
		this.remoteListByName = new ConcurrentHashMap<String, StSrvConnection>();
		stLog.info("[PulseConn] Create Manager ");
    }
    
    
	public synchronized StSrvConnection getConnection(final StClientID id){
		return remoteList.get(id);
	}
	
	
	public synchronized ArrayList<StSrvConnection> getConnection(final ArrayList<StClientID> id_list){
		ArrayList<StSrvConnection> list = new ArrayList<StSrvConnection>();
		for(StClientID id: id_list){
			StSrvConnection conn = remoteList.get(id);
			if(conn != null){
				list.add(conn);
			}
		}
		return list;
	}
	
	
	/**
	 * Send Event New-Socket to connection pulse.
	 * 
	 * @param sock
	 * @param clt_name
	 * @param su_pkt
	 */
	public synchronized void onNewSocket(
			final StSocket4Pkt sock, 
			final String clt_name, 
			final StNetPacket su_pkt, 
			final long ts_start)
	{
		StSrvConnection conn = remoteListByName.get(clt_name);
		if(conn == null){
			conn = new StSrvPulseConnection(clt_name);
			remoteListByName.put(clt_name, conn);
		}
		conn.addNewEvent(new StEvtConnNewSocket(null, clt_name, sock, su_pkt, ts_start));
	}

	
	/**
	 * 
	 * @param clt_id
	 * @param conn
	 */
	public synchronized void addConnection(final StClientID clt_id,  final StSrvConnection conn){
		final String clt_name = conn.getCltName();
		final StSrvConnection conn1 = remoteList.get(clt_id);
		final StSrvConnection conn2 = remoteListByName.get(clt_name);
		//util.assertTrue(conn instanceof StSrvPulseConnection);
		util.assertTrue(conn == conn2 && conn != null);
		if(conn1 == null){
			remoteList.put(clt_id, conn);
			stLog.debug(util.testMilestoneLog("[T] Added Connection to Client: " + clt_name ));
			stLog.debug("Added Connection: " + conn);
		}else{
			util.assertTrue(conn1 == conn);
		}
	}
	
    
	public synchronized void deleteConnection(StSrvConnection conn){
		if(conn == null){
			return;
		}
		final StClientID id = conn.getClientID();
		if(id != null){
			remoteList.remove(id, conn);
		}
		remoteListByName.remove(conn.getCltName(), conn);
		conn.addNewEvent(new StEvent.SystemShutdown());
		stLog.info(util.testMilestoneLog("[T] Delete connection: " + conn ));
	}
	
	
	public synchronized void deleteAll(){
		for(StSrvConnection c : remoteListByName.values()){
			c.addNewEvent(new StEvent.SystemShutdown());
		}
		stLog.info("Sleep after closing all conn...");
		util.sleep(2*1000);
		remoteList.clear();
	}
	
	
//	/**
//	 * Check connection state & send packet! 
//	 * 
//	 * <pre>
//	 * NOTE: DO NOT call this method, when sending a server-request or client-result!
//	 * 
//	 * </pre>
//	 * 
//	 * @deprecated used?
//	 * 
//	 * @param id
//	 * @param pkt
//	 * @return
//	 */
//    synchronized boolean sendPacket22(final StClientID id,  final StNetPacket pkt){
//		final StSrvPulseConnection conn = remoteList.get(id);
//		if(null == conn){
//			stLog.info("#### No Socket connected to remote: " + id);
//			return false;
//		}
//		conn.sendPacketSafe(pkt);
//		return true;
//	}

    
	public synchronized StringBuffer debug_getCount(StringBuffer sbuf){
		if(sbuf == null){
			sbuf = new StringBuffer(128);
		}
    	util.dumpFunc.addDumpLine(sbuf, ">> {Connection Count} ID/Name_Map=" + remoteList.size()  + "/" + remoteListByName.size());
    	
    	int online_num = 0;
    	int offline_num = 0;
    	int ok_num = 0;
    	int err1_num = 0, err2_num=0;
    	
    	for(StSrvConnection conn : remoteList.values()){
    		if(conn.getOfflineMS() > 0) {
    			offline_num++;
    		}else{
    			online_num++;
    		}
    		
    		StSrvConnection conn0 = remoteListByName.get(conn.getCltName());
    		if(conn0 == null){
    			// ERROR: connection in id-map is NOT found in name-map!
    			err1_num++;
    		}else if(conn0 != conn){
    			// ERROR: connection in TWO maps are different!
    			err2_num++;
    		}else{
    			ok_num++;
    		}
    	}
    	
    	util.dumpFunc.addDumpLine(sbuf, 
    			"   ONLINE=" + online_num  + 
    			", OFFLINE=" + offline_num +
    			", OK/E1/E2=" + ok_num + "/" + err1_num + "/" + err2_num
    			);
    	
    	util.dumpFunc.addDumpLine(sbuf, "   -- E1: connections in id-map are NOT found in name-map");
    	util.dumpFunc.addDumpLine(sbuf, "   -- E2: connections in TWO maps are different!");
    	util.dumpFunc.addDumpLine(sbuf, "");
    	
    	return sbuf;
    }
	
    
    /**
     *  
     * @return
     */
	@SuppressWarnings("unused")
	synchronized private ArrayList<StSrvConnection> debug_copyConnectionList22(){
		final long  t_start = System.currentTimeMillis();
		try{
	    	Collection<StSrvConnection> col = remoteListByName.values();
	    	ArrayList<StSrvConnection> list = new ArrayList<StSrvConnection>();
	    	for(StSrvConnection c: col){
	    		list.add(c);
	    	}
	    	return list;
    	}finally{
    		stLog.trace("Cost (ms): " + (System.currentTimeMillis() - t_start ));
    	}
    }


	@Override
	public synchronized StSrvConnection getConnection(String name) {
		return remoteListByName.get(name);
	}

}
