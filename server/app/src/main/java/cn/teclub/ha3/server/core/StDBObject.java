package cn.teclub.ha3.server.core;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha3.server.core.StDbHiberMgr.SessionCB;
import cn.teclub.ha3.net.StClientID;
import cn.teclub.ha3.net.StClientInfo;
import cn.teclub.ha3.net.StMessage;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;


/**
 * 
 * <pre>
 * NON-THREAD SAFETY!
 * Only call this object in the SAME thread!
 * 
 * Each connection has one instance of this class, 
 * so that 1st-level cache of hibernate can be used.
 * 
 * </pre>
 * 
 * @author mancook
 *
 */

@SuppressWarnings("ALL")
public class StDBObject extends ChuyuObj
{
	private static final StSrvConfig 	cfg = StSrvConfig.instance();
	
	// accept DB operation time (ms)
	public static final int MS_CREATE_SESSION =  500;
	public static final int MS_ACCESS_SESSION =  500;
	
	
	public static class ObjectMgr extends ChuyuObj
	{
		public static final int OBJ_COUNT = cfg.dbObjCount;
		
		private static final ObjectMgr _ins = new ObjectMgr();
		public static ObjectMgr getInstance(){
			return _ins;
		}
		
		private final StDBObject[] 	objList = new StDBObject[OBJ_COUNT];
		private final Semaphore 	available = new Semaphore(OBJ_COUNT, true);
		
		private ObjectMgr(){ }
		
		private final StringBuffer sbufDebug = new StringBuffer(128);
		
		public StDBObject getNextObject() throws InterruptedException{
			final long ms_start = System.currentTimeMillis();
			available.acquire();
			StDBObject db_obj = getNextAvailableItem();
			final long ms_cost = util.getCostMillis(ms_start);
			
			sbufDebug.setLength(0);
			sbufDebug.append("==== Cost: Create DB Object: " + ms_cost);
			if(ms_cost > MS_CREATE_SESSION){
				StDbHiberMgr.getInstance().debug_statistics(sbufDebug);
				if(ms_cost > MS_CREATE_SESSION * 10){
					stLog.error(sbufDebug.toString());
				}else{
					stLog.warn(sbufDebug.toString());
				}
			}else{
				stLog.debug(sbufDebug.toString());
			}
			
			return db_obj;
		}
		
		
		public void putObject(StDBObject db_obj) {
			if(db_obj == null){
				return;
			}
			if(markAsUnused(db_obj)){
				available.release();
			}
		}
		
		
		
		 private synchronized StDBObject getNextAvailableItem() {
		     for (int i = 0; i < OBJ_COUNT; ++i) {
		       if (objList[i] == null) {
		    	   objList[i] = new StDBObject();
		    	   return objList[i];
		       }
		     }
		     return null; 
		     // 2017-1-14: because semaphore is used, program will never reach here.
		 }
		 
		 

		 private synchronized boolean markAsUnused(StDBObject item) {
		     for (int i = 0; i < OBJ_COUNT; ++i) {
		       if (item == objList[i]) {
		    	   item.close();
		    	   objList[i] = null;
		    	   return true;
		       }
		     }
		     return false;
		   }
		 
	}
	
	
	private final Session 	session ;
	private final long 		tid; 
	
	
	/**
	 * Constructor
	 * 
	 */
	private StDBObject()  {
		Session ss;
		long    id;
		try {
			ss = StDbHiberMgr.getInstance().openSession();
			id = Thread.currentThread().getId();
		} catch (StExpHibernate e) {
			e.printStackTrace();
			stLog.error(util.getExceptionDetails(e.hiberExp, "Open Hibernate Session Failure!"));
			stLog.warn("Use Global Session!");
			ss = StDbHiberMgr.getInstance().globalSession;
			id = 0;
		}
		this.session = ss;
		this.tid = id;
	}
	
	
	private Object doJob(StDbHiberMgr.SessionCB cb){
		if(tid ==0){
			util.assertTrue(session == StDbHiberMgr.getInstance().globalSession);
			return StDbHiberMgr.getInstance().executeInGlobalSession(cb);
		}
		else{
			util.assertTrue(tid ==0 || tid == Thread.currentThread().getId(), "DO NOT call from out thread!");
			final long MS_START =  System.currentTimeMillis();
			
			session.beginTransaction();
			Object obj = cb.execute(session);
			session.getTransaction().commit();
			
			final long ms_cost =  util.getCostMillis(MS_START);
			final String log_msg = "==== Cost: Access DB Session: " + ms_cost;
			if(ms_cost > MS_ACCESS_SESSION){
				if(ms_cost > MS_ACCESS_SESSION * 10){
					stLog.error(log_msg);
				}else{
					stLog.warn(log_msg);
				}
			}else{
				stLog.debug(log_msg);
			}
	        return obj;
		}
	}
	
	
	public void close(){
		if(session == StDbHiberMgr.getInstance().globalSession){
			return;
		}
		stLog.debug("Close db-object");
		session.close();
	}

	
	@SuppressWarnings("rawtypes")
	private List queryRecord(final String cond) {
		return (List)doJob(new StDbHiberMgr.SessionCB() {
			@Override
			public Object execute(Session ss) {
				stLog.debug("HQL: " + cond);
				Query qq =  ss.createQuery(cond);
				return qq.list();
			}
		});
	}

	
	public StModelClient loadClient(final StClientID id) {
		return loadClient(id, false);
	}
	
	
	public StModelClient loadClient(final StClientID id, final boolean load_friends) {
		stLog.debug("loading client: " + id );
		return (StModelClient) doJob(new StDbHiberMgr.SessionCB() {
			@Override
			public Object execute(Session ss) {
				StModelClient mc = (StModelClient) ss.load(StModelClient.class, new Long(id.getId()));
				stLog.debug("Refresh Client after loading");
				ss.refresh(mc);
				if(load_friends){
					stLog.debug("loading friends for: " + mc);
					mc.loadRelation();
					stLog.debug("Make sure: friends are loaded: " + mc );
				}
				return mc;
			}
		});
	}
	
	
	/**
	 * Add a friendship.
	 * 
	 * if 'admin' is TRUE, only client-A is admin.
	 * 
	 * 
	 * @param id1
	 * @param id2
	 * @param admin
	 * @return
	 */
	public boolean addFriendship(
			final StClientID id1, 
			final StClientID id2,
			final boolean admin) 
	{
		final StModelClient clt_1 =  loadClient(id1, true);
		final StModelClient clt_2 =  loadClient(id2, true);
		
		final StModelClientHas mch  = (StModelClientHas) doJob(new SessionCB() {
			@Override
			public Object execute(Session ss) {
				//final StModelClient clt_1 =  (StModelClient) ss.load(StModelClient.class, new Long(id1.getId()));
				//final StModelClient clt_2 =  (StModelClient) ss.load(StModelClient.class, new Long(id2.getId()));
				
				util.assertTrue(clt_1 != null && clt_2 != null);
				if(admin){
					util.assertTrue(
							(clt_1.isFlag_User() && clt_2.isFlag_Gateway())
						 || (clt_1.isFlag_User() && clt_2.isFlag_Monitor())
						 || (clt_1.isFlag_Gateway() && clt_2.isFlag_Monitor()) , 
							"Admin ONLY valid for: User~GW & GW~Monitor !");
				}
				
				if(clt_1.hasFriend(id2) || clt_2.hasFriend(id1)){
					stLog.error("Existing FriendShip!");
					stLog.error("Client 1: " + clt_1.dump());
					stLog.error("Client 2: " + clt_2.dump());
					return null;
				}
				
				stLog.info("#### insert relation ships...");
				int flag = 0;
				flag = StClientInfo.UtilHas.setFlag_OwnerClientType(flag, clt_1.getFlag_ClientType());
				flag = StClientInfo.UtilHas.setFlag_OwnerAdmin(flag, admin);
				flag = StClientInfo.UtilHas.setFlag_FriendClientType(flag, clt_2.getFlag_ClientType());
				flag = StClientInfo.UtilHas.setFlag_FriendAdmin(flag, false);
				final StModelClientHas mch = new StModelClientHas(id1, id2, flag);
				ss.save(mch);
				stLog.info(util.testMilestoneLog("[T] Added Friendship: " + mch));
				return mch;
			}
		});
		return mch != null;
	}
	
	
	/**
	 * <h2>Query All online HA clients. </h2>
	 * 
	 * This is a DEBUG method. You shall NOT load all HA clients <br/>
	 * for performance concern. <br/>
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<StModelClient> queryOnlineAll(){
		return (ArrayList<StModelClient>) doJob(new SessionCB() {
			@Override
			public Object execute(Session ss) {
				ArrayList<StModelClient> mc_list = new ArrayList<StModelClient>();
				// [2016-11-1] Fix N+1 select issue.
				//     Query q = ss.createQuery("FROM StModelClient st_client " +
				//			"JOIN FETCH st_client.sipAcct StModelSipAcct WHERE bitwise_and(st_client.flag, :ck_musk)=:ck_flag");
				Query q = ss.createQuery("FROM StModelClient WHERE bitwise_and(flag, :ck_musk)=:ck_flag");
				final int musk = 0x08, flag = 0x08;
				stLog.debug("flag musk/value:  0x" + Integer.toHexString(musk) + "/0x" + Integer.toHexString(flag) ) ;
				q.setInteger("ck_musk", musk);
				q.setInteger("ck_flag", flag);
				for(Object obj : q.list()){
					StModelClient mc = (StModelClient)obj;
					mc_list.add(mc);
					//stLog.info("#### find online model-client: " + mc.dump());
				}
				return mc_list;
			}
		});
	}
	
	

	public ArrayList<StModelClient> queryClients(final StClientID[] ids) {
		ArrayList<StModelClient> mc_list = new ArrayList<StModelClient>();
		if(ids != null && ids.length > 0){
			@SuppressWarnings("unchecked")
			List<StModelClient> list = (List<StModelClient>) doJob(new SessionCB() {
				@Override
				public Object execute(Session ss) {
					util.assertTrue(ids.length > 0);
					StringBuffer  sbuf = new StringBuffer(128);
					sbuf.append("from StModelClient st_client where st_client.id IN (");
					// [Theodore: 2016-11-11] 'join fetch' fails to get sip-acct object!
					//      sbuf.append("from StModelClient st_client join fetch st_client.sipAcct StModelSipAcct where st_client.id IN (");
					for(StClientID id: ids){
						sbuf.append(id.getId());
						sbuf.append(",");
					}
					final int POS = sbuf.length() - 1;
					sbuf.delete(POS, POS+1);
					sbuf.append(")");
					
					Query qq =  ss.createQuery(sbuf.toString());
					return qq.list();
				}
			});
			for(StModelClient mc: list){
				mc_list.add(mc);
			}
		}
		return mc_list;
	}
	
	
	
	public StModelClient deleteClient(final StClientID id){
		StModelClient mc = (StModelClient) doJob(new SessionCB() {
			@Override
			public Object execute(Session ss) {
				stLog.info("Delete Client & Free SIP");
				StModelClient  mc =  (StModelClient) ss.load(StModelClient.class, new Long(id.getId()));
				StModelSipAcct m_sip = mc.getSipAcct();
				m_sip.setFlag(0x00);
				ss.delete(mc);
		        ss.update(m_sip);
		        return mc;
			}
		});
		return mc;
	}
	
	
	/**
	 * Get Apply Message of clt_id or its slaves
	 * @param clt_id
	 * @return
	 */
	public ArrayList<StMessage> getMessageApply(final StClientID clt_id){
		@SuppressWarnings("unchecked")
		ArrayList<StMessage> list = (ArrayList<StMessage>)doJob(new SessionCB() {
			public Object execute(Session ss) {
				final StModelClient mc = (StModelClient) ss.load(StModelClient.class, new Long(clt_id.getId()));
				mc.loadRelation();
				stLog.debug("Getting messages of: " + mc.dump() );
				
				final ArrayList<StMessage> msg_list = new ArrayList<StMessage>();
				final StringBuffer sbuf = new StringBuffer(128);
				sbuf.append("FROM StModelMessage WHERE ( bitwise_and(flag, 3) = 1 AND clt_b IN (")
					// [2016-10-21] HEX seems NOT working! e.g. 0x0A00
					.append( clt_id.getId());
				if(mc.isFlag_User()){
					// get pending applies of GW slaves
					final ArrayList<StClientID> slave_list = mc.getSlave();
					for(StClientID f: slave_list){
						sbuf.append(", " + f.getId());
					}
				}
				sbuf.append(")");
				sbuf.append(") OR ( bitwise_and(flag, 3)>1 AND bitwise_and(flag, 8)=0 AND clt_a=" + clt_id.getId());
				sbuf.append(")");

				stLog.debug("HQL: " + sbuf);
				Query qq = ss.createQuery(sbuf.toString());
				for(Object obj: qq.list()){
					StModelMessage mm = (StModelMessage )obj;
					stLog.info("#### Get Model Message: " + mm.dump());
					msg_list.add((StModelMessage)obj);
				}
				return msg_list;
			}
		});
		return list;
	}

	

	@SuppressWarnings("unchecked")
	public StMessage getMessageApply(StClientID clt_a, StClientID clt_b) {
		final String cond = "FROM StModelMessage WHERE bitwise_and(flag, 3) = 1 AND clt_a=" 
							+ clt_a.getId() + " AND clt_b=" + clt_b.getId();
		List<StModelMessage> list = queryRecord(cond);
		if(list == null || list.size() == 0){
			return null;
		}

		//util.assertTrue(list.size() <= 1);
		if(list.size() > 1){
			stLog.error("More than ONE APPLY messages -- " + list.size());
		}
		return list.get(0);
	}
	
	
	public void updateRecord(final StDbTable record) {
		stLog.debug("Update Record: " + record);
		doJob(new StDbHiberMgr.SessionCB() {
			@Override
			public Object execute(Session ss) {
				ss.update(record);
				return null;
			}
		});
	}
	
	
	public void updateRecords(final StDbTable[] records) {
		doJob(new SessionCB() {
			@Override
			public Object execute(Session ss) {
				for(StDbTable r: records){
					ss.update(r);
				}
				return null;
			}
		});
	}
	
	
	public void addRecord(final StDbTable record) {
		doJob(new SessionCB() {
			@Override
			public Object execute(Session ss) {
				ss.save(record);
				return null;
			}
		});
	}
	
	
	
	public void deleteRecord(final StDbTable record) {
		doJob(new SessionCB() {
			@Override
			public Object execute(Session ss) {
				ss.delete(record);
				return null;
			}
		});
	}
	
	
	public void refreshRecord(final StDbTable record) {
		doJob(new SessionCB() {
			@Override
			public Object execute(Session ss) {
				ss.refresh(record);
				return null;
			}
		});
	}
	
	
	
	public void clearCache(){
		doJob(new SessionCB() {
			@Override
			public Object execute(Session ss) {
				ss.clear();
				return null;
			}
		});
	}

    
	public StModelClient queryClientByName(final String clt_name) {
		// [Theodore: 2016-11-10] fix: hibernate N+1 issue 
		// ArrayList<StModelClient> mc_list = queryClients("from StModelClient st_client join fetch st_client.sipAcct StModelSipAcct where st_client.name='" + clt_name + "'");
		ArrayList<StModelClient> mc_list = queryClients("FROM StModelClient WHERE name='" + clt_name + "'");
		util.assertTrue(mc_list.size() <=1);
		if(mc_list.size() < 1){
			return null;
		}
		return mc_list.get(0);
		
	}
	
	
	public ArrayList<StModelClient> queryClients(final String cond) {
		@SuppressWarnings("unchecked")
		List<StModelClient> list = queryRecord(cond);
		ArrayList<StModelClient> mc_list = new ArrayList<StModelClient>();
		for(StModelClient mc: list){
			mc_list.add(mc);
		}
		return mc_list;
	}
	
	
	/**
     * 
     * @param pub_ip
     * @return
     */
	public ArrayList<StModelClient> queryGwByPublicIp(final String pub_ip) {
		return queryClients("FROM StModelClient WHERE public_ip='"+ pub_ip +"' AND bitwise_and(flag, 7)=1");
	}
	

	public ArrayList<StModelClient> queryDevByPublicIp(final String pub_ip) {
		return queryClients("FROM StModelClient WHERE public_ip='"+ pub_ip +"' AND bitwise_and(flag, 7)>0");
	}
	
	public StModelClient queryModelClientByMacAddr(final String mac_addr) {
		ArrayList<StModelClient> l = queryClients("FROM StModelClient WHERE mac_addr='"+ mac_addr +"'");
		if(l.size() == 0){
			return null;
		}
		if(l.size() > 1){
			stLog.error("More than one clients with mac_addr: " + mac_addr);
		}
		return l.get(0);
	}
	
	
	public StModelClient queryModelClientByPhone(final String phone) {
		ArrayList<StModelClient> l = queryClients("FROM StModelClient WHERE phone='"+ phone +"'");
		if(l.size() == 0){
			return null;
		}
		if(l.size() > 1){
			stLog.error("More than one clients with phone: " + phone);
		}
		return l.get(0);
	}
	
	public StModelClient queryModelClientByName(final String name) {
		ArrayList<StModelClient> l = queryClients("FROM StModelClient WHERE name='"+ name +"'");
		if(l.size() == 0){
			return null;
		}
		if(l.size() > 1){
			stLog.error("More than one clients with name: " + name);
		}
		return l.get(0);
	}
	
	
	

	@SuppressWarnings("unchecked")
	public StModelSipAcct getModelSipAcctFree(boolean is_gw) {
		StModelSipAcct r = null;
		while(true){
			List<StModelSipAcct> list = queryRecord("FROM StModelSipAcct WHERE sip_id like " + 
						(is_gw ? "'stgF%'" : "'stuF%'") + 
						" AND flag=0 ORDER BY id ASC 1");
			util.assertTrue(list != null && list.size() > 0);
			r = list.get(0);
			List<StModelClient> mc_list = queryClients("from StModelClient where sip_acct_id=" +  r.getId());
			if(mc_list.size() == 0){
				break;
			}
			
			stLog.error("!!!!!!!! ASSIGN bit is NOT set:  !!!!!!!! " + r.dump() );
			stLog.error("!!!!!!!! But used by Clients     !!!!!!!! " + mc_list.get(0).dump() );
			
			// [Theodore: 2016-09-04] TOOD: correct in SQL script!
			// 
			//			r.setFlag(0x01);
			//			hiberMgr.update(r);
			//			
			//			StModelSipAcct r2 = (StModelSipAcct) hiberMgr.queryById("StModelSipAcct", r.getId()); 
			//			stLog.info("Corrected SIP Account: " + r2.dump());

		}
		return r;
	}
	
	

    /**
     * Get all online clients from DB.
     * 
     * @param sbuf
     * @return
     */
    public StringBuffer debug_getOnlineClients(StringBuffer sbuf){
    	if(sbuf == null){
    		sbuf = new StringBuffer(512);
    	}
    	// [2016-11-3] Q: if NOT clear cache, returned clients are NOT online ones! 
    	clearCache();
    	
    	List<StModelClient> list  = queryOnlineAll();
    	util.dumpFunc.addDumpHeaderLine(sbuf, " " + list.size() +" Clients are ONLINE  ", "=");
    	int i=0;
    	for(StModelClient mc: list){
    		util.dumpFunc.addDumpLine(sbuf, "[" + i++  + "] " + mc );
    		if(i>0 && i%16==0){
    			util.dumpFunc.addDumpLine(sbuf, "");
    		}
    	}
    	util.dumpFunc.addDumpSectionLine(sbuf);
    	return sbuf;
    }

    
	public int debug_getOnlineCount(){
    	// [2016-11-3] Q: if NOT clear cache, returned clients are NOT online ones! 
    	clearCache();
		Long n = (Long) doJob(new SessionCB() {
			@Override
			public Object execute(Session ss) {
		    	Query q = ss.createQuery("select count(*) FROM StModelClient WHERE bitwise_and(flag, :ck_musk)=:ck_flag");
				final int musk = 0x08, flag = 0x08;
				stLog.debug("flag musk/value:  0x" + Integer.toHexString(musk) + "/0x" + Integer.toHexString(flag) ) ;
				q.setInteger("ck_musk", musk);
				q.setInteger("ck_flag", flag);
				return (Long)q.uniqueResult();
			}
		});
		return n.intValue();
	}


	/**
	 * 
	 * 
	 * @return
	 */
	public StringBuffer debug_summary(StringBuffer sbuf){
		if(sbuf == null){
			sbuf = new StringBuffer(1024);
		}
		debug_getOnlineClients(sbuf);
		// [10-30] is there other info???
		return sbuf;
	}


	public void addMessage(StMessage msg) {
		util.assertTrue(msg.getId() == 0);
		StModelMessage mm = new StModelMessage(msg);
		this.addRecord(mm);
		msg.setId(mm.getId());
	}


}
