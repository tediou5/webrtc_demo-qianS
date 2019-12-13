package cn.teclub.ha.net.serv;

import java.util.List;


import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.stat.Statistics;

import cn.teclub.common.ChuyuObj;



/**
 * <h1>Super Hibernate Manager. </h1>
 * 
 * General DB access APIs are defined. <br/>
 * 
 * @author mancook
 *
 */
public class  StDbHiberMgr extends ChuyuObj {

	public interface QueryCB {
		void setQuery(Query q);
	}
	
	public interface SessionCB {
		Object execute(Session ss);
	}
	
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	
	private static StDbHiberMgr _instance = new StDbHiberMgr();;
	public static StDbHiberMgr getInstance(){
		return _instance;
	}
	
	
	////////////////////////////////////////////////////////////////////////////
    // Inner Classes
	////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Attributes
	////////////////////////////////////////////////////////////////////////////	
	private final Configuration 			configuration;
	private final ServiceRegistryBuilder 	registry ;
    private final ServiceRegistry 			serviceRegistry;
    private final SessionFactory 			sessionFactory;
    
    final Session	globalSession;
    
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Methods
	////////////////////////////////////////////////////////////////////////////	
	
	private StDbHiberMgr(){
		this.configuration = new Configuration().configure();
        this.registry = new ServiceRegistryBuilder();
        this.registry.applySettings(this.configuration.getProperties());
        this.serviceRegistry = registry.buildServiceRegistry();
        this.sessionFactory = configuration.buildSessionFactory(this.serviceRegistry);
        this.globalSession = sessionFactory.openSession();
        
        Statistics stats = sessionFactory.getStatistics();
        stats.setStatisticsEnabled(true);
	}
	
	
	public void close(){
		stLog.info("Close global session & session factory!");
		globalSession.close();
		sessionFactory.close();
	}

	
	@SuppressWarnings("rawtypes")
	public List  queryByCond(String query_str){
		return queryByCond(query_str, null);
    }
	
	
	/**
     * Query Method
     * 
     * @param query_str, HQL query string. 
     * <pre>
     *     Format: "FROM <MappedObject> [WHERE <condition>]"
     *     e.g.  "FROM AcctPeriod WHERE ayId=101" 
     * </pre>
     * 
     * @param cb query callback, can be NULL;
     * 
     * @return
     */
	@SuppressWarnings("rawtypes")
	public List queryByCond(String query_str, QueryCB cb){
        stLog.debug("HQL: " + query_str);
        final long MS_START = System.currentTimeMillis();
        
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Query qq = session.createQuery(query_str ); 
        if (cb != null) { 
        	cb.setQuery(qq); 
        }
        session.getTransaction().commit();
		List l = qq.list();
        session.close();
        
        stLog.debug("Query " + l.size() + " Records from DB -- Cost:" + util.getCostStr(MS_START));
        return l;
	}
	
	
	
	public Object executeInNewSession( SessionCB cb){
		final long MS_START =  System.currentTimeMillis();
        Session session = sessionFactory.openSession();
        Object obj = cb.execute(session);
        session.close();
        stLog.info("==== Cost (DB New Session): " + util.getCostMillis(MS_START) + "ms");
        return obj;
	}

	
	public  Object executeInGlobalSession( SessionCB cb){
		final Object obj;
		final long MS_START =  System.currentTimeMillis();
		long cost0 = 0;
		synchronized(globalSession){
			final long MS_START0 =  System.currentTimeMillis();
			globalSession.beginTransaction();
			obj = cb.execute(globalSession);
			globalSession.getTransaction().commit();
			cost0 = util.getCostMillis(MS_START0);
		}
		stLog.info("==== Cost (DB Global Session): " + util.getCostMillis(MS_START) + "/" + cost0 + "ms");
		return obj;
	}
	
	
	
	public Session openSession() throws StExpHibernate{
		try{
			return sessionFactory.openSession();
		}catch(HibernateException e){
			e.printStackTrace();
			throw new StExpHibernate(e);
		}
	}
	
	
	
	@SuppressWarnings({ "unchecked" })
	public StModelClient queryClientByKey(final String cond, boolean load_friends){
		stLog.debug("HQL: " + cond + (load_friends ? ", With Friends" : ", Without Friends"));
        final long MS_START = System.currentTimeMillis();
        final StModelClient mc;
		
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        
        final Query qq = session.createQuery(cond);
        List<StModelClient> l = qq.list();
        if(l.size() > 0){
        	util.assertTrue(l.size() == 1);
        	mc = l.get(0);
        	if(load_friends){
        		// hibernate lazy fetch
        		stLog.info("#### Make sure friends are loaded!");
        		mc.getFriendList();
        	}
        }else{
        	mc = null;
        }
        session.getTransaction().commit();
        session.close();
        
        stLog.debug("Query Client -- Cost:" + util.getCostStr(MS_START));
		return mc;
	}
	
	
	
	/**
	 * Query DB by key. 
	 * 
	 * @param table
	 * @param key_name
	 * @param key_value
	 * @return
	 */
	public Object queryByKey(
			final String table, 
			final String key_name, 
			final long key_value)
	{
	  	stLog.debug("Query " + table +" by KEY(" + key_name + ")=0x" + Long.toHexString(key_value));
	  	return this.queryByKey(table, key_name, "" + key_value );
	}
	
	
	/**
	 * Query DB by key. <br/><br/>
	 * 
	 * @param table
	 * @param key_name
	 * @param key_value
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Object queryByKey(
			final String table, 
			final String key_name, 
			final String key_value) 
	{
	  	final List l = this.queryByCond(" FROM " + table + " WHERE " + key_name + "='" + key_value +"'");
        util.assertTrue(l.size() <=1, ">=2 records!");
        return l.size() == 0 ? null : l.get(0);
	}
	
	
	/**
	 *  Query DB by ID. <br/><br/>
	 * 
	 * @param table
	 * @param key_value
	 * @return
	 */
	public Object queryById(final String table, final long key_value)  {
		return this.queryByKey(table, "id", key_value);
	}
	
	
	/**
	 * Update a record.
	 * 
	 * @param db_record
	 */
	public void update(final StDbTable db_record){
		if(db_record == null){
			return;
		}
		
		final long MS_START = System.currentTimeMillis();
		//stLog.debug("Updating: " + db_record.dump());
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.update(db_record);
		session.getTransaction().commit();
		session.close();
		stLog.debug("Updated record: " + db_record + "-- Cost:" + util.getCostStr(MS_START));
		
		// [2016-9-4] db_record is NOT the one saved in DB. No need to log it!
		// stLog.debug("Updated record: " + db_record.toString());
	}
	
	
	/**
	 * Update Records. 
	 * 
	 * @param records
	 */
	public void updateList(final StDbTable[]  records){
		if(records == null || records.length < 1){
			return;
		}
		final long MS_START = System.currentTimeMillis();
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		for(StDbTable obj : records){
			session.update(obj);
			stLog.debug("Updated record: " + obj);
		}
		session.getTransaction().commit();
		session.close();
		stLog.debug("Updated " + records.length + "records! -- Cost:" + util.getCostStr(MS_START));
	}
	
	
	
    /**
     *  <h2> Add a record. </h2>
	 * 
	 * @param db_record
	 */
	public void add(StDbTable  db_record){
		if(db_record == null){
			return;
		}
		final long MS_START = System.currentTimeMillis();
		//stLog.debug("Adding Record: " + db_record.dump());
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(db_record);
        session.getTransaction().commit();
        session.close();
        stLog.debug("Added Record: " + db_record + " -- Cost:" + util.getCostStr(MS_START));
        //stLog.debug("Added Record: " + db_record.dump());
    }
	
	
	/**
	 * <h2> Delete a record. </h2>
	 * 
	 * @param db_record
	 */
	public void delete(final StDbTable  db_record){
		if(db_record == null){
			return;
		}
		final long MS_START = System.currentTimeMillis();
		//stLog.debug("Deleting: " + db_record.dump());
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.delete(db_record);
		session.getTransaction().commit();
		session.close();
        stLog.debug("Deleted Record: " + db_record + " -- Cost:" + util.getCostStr(MS_START));
	}
	
	
	public StringBuffer debug_statistics(StringBuffer sbuf){
		if(sbuf == null){
			sbuf = new StringBuffer(256);
		}
		
		Statistics stt = sessionFactory.getStatistics();
		util.dumpFunc.addDumpHeaderLine(sbuf, "  Hibernate Info  ");
		//util.dumpFunc.addDumpLine(sbuf, ">> Stattictics " +  (stt.isStatisticsEnabled()? "Enabled" : "Disabled" ));
		//stt.setStatisticsEnabled(true);
		util.dumpFunc.addDumpLine(sbuf, ">> Stattictics " +  (stt.isStatisticsEnabled()? "Enabled" : "Disabled" ));
		util.dumpFunc.addDumpLine(sbuf, ">> Connection Count: " + stt.getConnectCount());
		util.dumpFunc.addDumpLine(sbuf, ">> Open  Session: " 	+ stt.getSessionOpenCount());
		util.dumpFunc.addDumpLine(sbuf, ">> Close Session: "	+ stt.getSessionCloseCount());
		util.dumpFunc.addDumpEndLine(sbuf);
		
		return sbuf;
	}
	
	
}//EOF: StDbHiberMgr

