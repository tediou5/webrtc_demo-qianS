package cn.teclub.ha.net.serv.request;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.lib.StErrUserError;
import cn.teclub.ha.lib.StEvent;
import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.serv.StSrvConfig;
import cn.teclub.ha.request.StNetPacket;
import cn.teclub.ha.request.StSocket4Pkt;





class StSrvLightConnMgr 
	extends ChuyuObj 
	implements StSrvConnMgr 
{
    ////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS AND METHODS
	////////////////////////////////////////////////////////////////////////////
    private static final StSrvConfig 	cfg = StSrvConfig.getInstance();
    private static final int 	MAX_CONN_NUM = cfg.getGrpMaxConn();
    private static final int 	GRP_NUM = cfg.getGrpNum();
    private static final int	MS_LIGHT_CONN_OPT_SYNC = 10;
    private static final int	MS_LIGHT_CONN_OPT = MS_LIGHT_CONN_OPT_SYNC * 50;
    
    private static StSrvLightConnMgr _ins = new StSrvLightConnMgr();
    
    public static StSrvConnMgr getInstance(){
        return _ins;
    }

    
    
    interface Callback{
    	Object execute();
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Instance Members
	////////////////////////////////////////////////////////////////////////////
    
    
    
	private final StSrvLightConnGroup[]  	groupList;
	
    private final ConcurrentHashMap<StClientID, StSrvConnection> 	remoteList;
    private final ConcurrentHashMap<String, StSrvConnection> 		remoteListByName;
    
	
	/**
	 * Constructor
	 * 
	 */
	public StSrvLightConnMgr(){
		this.groupList = new StSrvLightConnGroup[GRP_NUM];
		this.remoteList = new ConcurrentHashMap<StClientID, StSrvConnection>();
		this.remoteListByName = new ConcurrentHashMap<String, StSrvConnection>();
		
		for(int i=0; i<GRP_NUM; i++){
			groupList[i] = new StSrvLightConnGroup("LightConnGroup__" +i);
			//stLog.info("#### Dump Group: \n" + groupList[i].toStringXml());
			
		}
		//stLog.info("#### GRP_NUM=" + GRP_NUM);
		stLog.info("[LightConn] Create Manager ");
	}

	
	private synchronized Object doJobSync(Callback cb, String opt_name){
		final long MS_START =  System.currentTimeMillis();

		final Object obj = cb.execute();
		 
		final long ms_cost =  util.getCostMillis(MS_START);
		final String log_msg = "==== Cost: LightConn_Sync[" + opt_name +"] " + ms_cost;
		if(ms_cost > MS_LIGHT_CONN_OPT_SYNC){
			if(ms_cost > MS_LIGHT_CONN_OPT_SYNC * 10){
				stLog.error(log_msg);
			}else{
				stLog.warn(log_msg);
			}
		}else{
			// stLog.debug(log_msg);
		}
        return obj;
	}
	
	
	private Object doJob(String opt_name, Callback cb){
		final long MS_START =  System.currentTimeMillis();

		final Object obj = doJobSync(cb, opt_name);
		 
		final long ms_cost =  util.getCostMillis(MS_START);
		final String log_msg = "==== Cost: LightConn[" + opt_name +"] " + ms_cost;
		if(ms_cost > MS_LIGHT_CONN_OPT){
			if(ms_cost > MS_LIGHT_CONN_OPT * 10){
				stLog.error(log_msg);
			}else{
				stLog.warn(log_msg);
			}
		}else{
			// stLog.debug(log_msg);
			// stLog.info("####" + log_msg);
		}
        return obj;
	}
	
	
	@Override
	public  void onNewSocket(
			final StSocket4Pkt sock, final String clt_name,
			final StNetPacket su_pkt, final long ts_start ) 
	{
		doJob("onNewSocket", new Callback() {
			@Override
			public Object execute() {
				StSrvConnection conn = remoteListByName.get(clt_name);
				if(conn == null){
					stLog.debug("create a new connection for: " + clt_name);
					StSrvLightConnGroup free_grp = groupList[0];
					for(StSrvLightConnGroup grp : groupList){
						if(free_grp.getConnCount() > grp.getConnCount()){
							free_grp = grp;
						}
					}
					
					if(free_grp.getConnCount() >= MAX_CONN_NUM ){
						throw new StErrUserError("TOO Many Connections! Max:" + MAX_CONN_NUM * GRP_NUM );
					}
					
					conn = free_grp.putLightConnection(clt_name);
					util.assertTrue(conn != null);
					remoteListByName.put(clt_name, conn);
				}else{
					stLog.debug("## use previous connection for : " + clt_name );
					stLog.debug("## [previous connection details] " + conn.debug_getStatistics() );
				}
				conn.addNewEvent(new StEvtConnNewSocket(null, clt_name, sock, su_pkt, ts_start));
				return null;
			}
		});
	}
	
	
	
	@Override
	public void deleteConnection(final StSrvConnection conn) {
		// different between pulse & light connection
		if(conn == null){
			return;
		}
		
		doJob("deleteConnection", new Callback() {
			@Override
			public Object execute() {
				final String  name = conn.getCltName();
				StSrvConnection c1 = remoteListByName.remove(name);
				
				// stLog.info("#### remove from group...");
				StSrvConnection c2 = null;
				for(StSrvLightConnGroup grp : groupList){
					c2 = grp.removeLightConnection(name);
					if(c2 != null){
						break;
					}
				}
				
				util.assertTrue(c1 != null );
				util.assertTrue(c2 != null );
				util.assertTrue(c1 == c2 );
			
				final StClientID id = conn.getClientID();
				if(id != null){
					remoteList.remove(id, conn);
				}
				
				stLog.info(util.testMilestoneLog("[T] Delete connection: " + conn ));
				return null;
			}
		});
	}
	
	
	
	@Override
	public StSrvConnection getConnection(final StClientID id) {
		return (StSrvConnection) doJob("getConnection", new Callback(){
			@Override
			public Object execute() {
				return remoteList.get(id);
			}
		});
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<StSrvConnection> getConnection( final ArrayList<StClientID> id_list) {
		return (ArrayList<StSrvConnection> ) doJob("getConnectionList", new Callback(){
			@Override
			public Object execute() {
				ArrayList<StSrvConnection> list = new ArrayList<StSrvConnection>();
				for(StClientID id: id_list){
					StSrvConnection conn = remoteList.get(id);
					if(conn != null){
						list.add(conn);
					}
				}
				return list;
			}
		});
	}
	
	

	
	@Override
	public void deleteAll() {
		// different between pulse & light connections
		doJob("deleteAll", new Callback() {
			@Override
			public Object execute() {
				for(StSrvLightConnGroup grp : groupList){
					grp.addNewEvent(new StEvent.SystemShutdown());
				}
				stLog.info("Sleep after stop all groups...");
				util.sleep(2*1000);
				remoteList.clear();
				
				return null;
			}
		});
	}
	
	

	@Override
	public StringBuffer debug_getCount(StringBuffer sbuf_in) {
		if(sbuf_in == null){
			sbuf_in = new StringBuffer(256);
		}
		final StringBuffer sbuf = sbuf_in;
		doJob("debug_getCountInMap", new Callback() {
			@Override
			public Object execute() {
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
		});
		
		return debug_getCountInGroup(sbuf);
	}

	private java.text.DecimalFormat df3	= new java.text.DecimalFormat("000"); 
	
	public StringBuffer debug_getCountInGroup(StringBuffer sbuf_in) {
		if(sbuf_in == null){
			sbuf_in = new StringBuffer(256);
		}
		final StringBuffer sbuf = sbuf_in;
		
		return (StringBuffer) doJob("debug_getCountInGroup", new Callback() {
			@Override
			public Object execute() {
				StringBuffer buf = new StringBuffer(256);
				
				int total_n = 0;
				for(int i=0; i<groupList.length; i++){
					if(i%16==0)
						buf.append((i==0 ? "":"\n") + "  -- ");
					else if(i>0 && i%8 ==0){
						buf.append("  ");
					}
					int n = groupList[i].getConnCount();
					total_n += n; 
					buf.append(df3.format(n) + ",");
				}
		    	util.dumpFunc.addDumpLine(sbuf, ">> {LigntConn Group} Total=" + total_n + ", Group=" + groupList.length );
		    	util.dumpFunc.addDumpLine(sbuf, "   [Config] GRP_NUM=" + GRP_NUM + ", MAX_CONN_NUM=" + MAX_CONN_NUM + ", Capacity=" + GRP_NUM*MAX_CONN_NUM );
		    	util.dumpFunc.addDumpLine(sbuf, buf.toString());
		    	return sbuf;
			}
		});
	}
	
	
	@Override
	public void addConnection(final StClientID clt_id, final StSrvConnection conn) {
		doJob("addConnection", new Callback() {
			@Override
			public Object execute() {
				final String clt_name = conn.getCltName();
				final StSrvConnection conn1 = remoteList.get(clt_id);
				final StSrvConnection conn2 = remoteListByName.get(clt_name);
				util.assertTrue( conn != null );
				util.assertTrue( conn == conn2 , "added connection must be in remote-name-list!");
				if(conn1 == null){
					remoteList.put(clt_id, conn);
					stLog.debug(util.testMilestoneLog("[T] Added Connection to Client: " + clt_name ));
					stLog.debug("Added Connection: " + conn);
				}else{
					stLog.info("connection from id map: " + conn1 );
					stLog.info("new connection        : " + conn  );
					if( conn != conn1){
						//
						// TODO: [2018-1-12] close previous connection in remote-id-list
						//
						stLog.error("added connection:      " + conn.debug_getStatistics() );
						stLog.error("connection in id-list: " + conn1.debug_getStatistics() );
						stLog.error(dump());
						throw new StErrUserError("added connection is NOT same to the the one in remote-id-list! ");
					}
				}
				return null;
			}
		});
	}


	@Override
	public StSrvConnection getConnection(final String name) {
		return (StSrvConnection) doJob("getConnection", new Callback(){
			@Override
			public Object execute() {
				return remoteListByName.get(name);
			}
		});
	}
	
	
	synchronized public void dumpSetup() {
		dumpAddLine("==========================================================");
		dumpAddLine("## remote list (key: ID) ");
		int i = 0;
		for(StSrvConnection conn : remoteList.values()){
			dumpAddLine("   [" + i++ + "] "+ conn + "\n" + conn.debug_getStatistics());
		}
		
		
		dumpAddLine("==========================================================");
		dumpAddLine("## remote list (key: Name) ");
		i=0;
		for(StSrvConnection conn : remoteListByName.values()){
			dumpAddLine("   [" + i++ + "] "+ conn + "\n" + conn.debug_getStatistics());
		}
		
		
		dumpAddLine("==========================================================");
		dumpAddLine("## conn-group list ");
		i=0;
		for(StSrvLightConnGroup grp: this.groupList){
			dumpAddLine("   [" + i++ + "] Connection Count: "+ grp.getConnCount() );
		}
		dumpAddLine("");
	}
}
