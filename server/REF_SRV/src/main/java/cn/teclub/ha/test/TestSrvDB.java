package cn.teclub.ha.test;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import cn.teclub.common.ChuyuUtil;
import cn.teclub.ha.net.StClientID;
import cn.teclub.ha.net.serv.StDBObject;
import cn.teclub.ha.net.serv.StDbHiberMgr;
import cn.teclub.ha.net.serv.StModelClient;
import cn.teclub.ha.net.serv.StSrvConfig;
import junit.framework.TestCase;


/**
 * Run with JUnit3
 * 
 * @author mancook
 *
 */
public class TestSrvDB extends TestCase 
{
	static ChuyuUtil util = ChuyuUtil.getInstance();
	static StSrvConfig cfg = StSrvConfig.getInstance();
	static StDbHiberMgr hiber = StDbHiberMgr.getInstance();
	
	
	private StDBObject.ObjectMgr dbMgr = StDBObject.ObjectMgr.getInstance();
	
	
	private String buildTitle(String tc_name, String dscp){
		StringBuffer sbuf = new StringBuffer(256);
		sbuf.append("\n");
		util.dumpFunc.addDumpHeaderLine(sbuf, " [Test Case]" + tc_name+ " ");
		util.dumpFunc.addDumpLine(sbuf, "  Descripton:" + dscp);
		util.dumpFunc.addDumpHeaderLine(sbuf, "");
		return sbuf.toString();
	}
	
	
	public void testMasterAndSlave() throws InterruptedException{
		System.out.println(
				buildTitle("000: Master & Slave", 
						   "Show Slave Monitor of a Gateway"));
		
		final long MS_START = System.currentTimeMillis();
		
		final String gw00 = "gw00";
		final String gw01 = "gw01";

		final StDBObject db_obj = dbMgr.getNextObject();
		final StModelClient mc0 = db_obj.queryClientByName(gw00);
		final StModelClient mc1 = db_obj.queryClientByName(gw01);
		mc0.loadRelation(); mc1.loadRelation();
		
		System.out.println(mc0.dump());
		System.out.println(mc1.dump());
		
		System.out.println("#### Slave of: " + mc0.getName());
		ArrayList<StClientID> slave_list0 = mc0.getSlave();
		for(StClientID id: slave_list0){
			System.out.println("Slave: " + id);
		}
		
		System.out.println("#### Slave of: " + mc1.getName());
		ArrayList<StClientID> slave_list1 = mc1.getSlave();
		for(StClientID id: slave_list1){
			System.out.println("Slave: " + id);
		}
		
		System.out.println("[Test] Cost: " + util.getCostStr(MS_START));
		assertTrue("Cost < 1000ms", util.getCostMillis(MS_START) < 1000);
	}
	
	
	
	
	public void testFirstLevelCache_LoadObject(){
		final long MS_START = System.currentTimeMillis();
		
		System.out.println(
				buildTitle("001: 1st-Level Cache", 
						   "The 1st-Level Cache is enabled by default"));
		
		hiber.executeInNewSession(new StDbHiberMgr.SessionCB() {
			StModelClient queryClient(Session ss, StClientID id){
				final long MS_START =  System.currentTimeMillis();
				
				ss.beginTransaction();
				StModelClient mc = (StModelClient) ss.load(StModelClient.class, new Long(id.getId()));
		        ss.getTransaction().commit();
		        System.out.println("---- Load Cost: " + util.getCostMillis(MS_START) + ", " + mc.toString());
		        return mc;
			}
			
			
			public Object execute(Session ss) {
				StModelClient mc0 = queryClient(ss, new StClientID(0xa00));
				queryClient(ss, new StClientID(0xa01));		System.out.println("-- Cost1: " + util.getCostStr(MS_START));
				queryClient(ss, new StClientID(0xa02));		System.out.println("-- Cost2: " + util.getCostStr(MS_START));
				queryClient(ss, new StClientID(0xa03));		System.out.println("-- Cost3: " + util.getCostStr(MS_START));
				
				System.out.println("\n\nINF: load same objects again!...   \n");
				StModelClient mc1 = queryClient(ss, new StClientID(0xa00));
				queryClient(ss, new StClientID(0xa01));
				queryClient(ss, new StClientID(0xa02));
				queryClient(ss, new StClientID(0xa03));
				
				System.out.println("==== [1st-Level Cache] TWO loadSimpleClient() calls return " + 
							(mc0 == mc1 ? "SAME" : "DIFF") + " objects");
				assertTrue("[1st-Level Cache] return SAME objects!", mc0 == mc1);
				return null;
			}
		});
		
		System.out.println("[Test] Cost: " + util.getCostStr(MS_START));
		assertTrue("Cost < 2000ms", util.getCostMillis(MS_START) < 1000);
	}
	
	
	
	
	/**
	 * If query 3 records, QUEYR is faster than LOAD in hibernate. 
	 * 59ms v.s. 1154ms
	 */
	public void testFirstLevelCache_queryObject(){
		final long MS_START = System.currentTimeMillis();
		
		System.out.println(
				buildTitle("002: Query Result", 
						   "Query result seems NOT in 1st-level cacheß"));
		
		hiber.executeInNewSession(new StDbHiberMgr.SessionCB() {
			@SuppressWarnings("unchecked")
			StModelClient queryClient(Session ss, StClientID id){
				final long MS_START =  System.currentTimeMillis();
				
				ss.beginTransaction();
				Query qq = ss.createQuery("from StModelClient st_client where st_client.id=" + id.getId());
				List<StModelClient> l = qq.list();
				
				final StModelClient mc;
				if(l.size() < 1) {
					mc = null;
				}else{
					mc = l.get(0);
				}
		        ss.getTransaction().commit();
		        
		        System.out.println("---- Query Cost:" + util.getCostMillis(MS_START) + ", " + mc);
		        return mc;
			}
			
			
			public Object execute(Session ss) {
				StModelClient mc0 = queryClient(ss, new StClientID(0xb00));
				queryClient(ss, new StClientID(0xb01));
				queryClient(ss, new StClientID(0xb02));
				queryClient(ss, new StClientID(0xb03));
				
				System.out.println("\n\nINF: query same objects again!...   \n");
				StModelClient mc1 = queryClient(ss, new StClientID(0xb00));
				queryClient(ss, new StClientID(0xb01));
				queryClient(ss, new StClientID(0xb02));
				queryClient(ss, new StClientID(0xb03));
				
				System.out.println("==== [1st-Level Cache] TWO querySimpleClient() calls return " + 
							(mc0 == mc1 ? "SAME" : "DIFF") + " objects");
				assertTrue("[1st-Level Cache] return SAME objects!", mc0 == mc1);
				return null;
			}
		});
		
		System.out.println("[Test] Cost: " + util.getCostStr(MS_START));
		assertTrue("Cost < 1000ms", util.getCostMillis(MS_START) < 1000);
	}
	

	
	
	public void testAdminGateway() throws InterruptedException{
		final long MS_START = System.currentTimeMillis();
		System.out.println(
				buildTitle("000: Admin GW", 
						   "Show Admin Gateways of a User"));
		
		final String clt_name = "user00";
		final StDBObject db_obj = dbMgr.getNextObject();
		final StModelClient mc0 = db_obj.queryClientByName(clt_name);
		mc0.loadRelation(); 
		
		System.out.println("#### Slave of: " + mc0.getName());
		System.out.println(mc0.dump());
		
		ArrayList<StClientID> slave_list0 = mc0.getSlave();
		for(StClientID id: slave_list0){
			System.out.println("Slave: " + id);
		}
		
		System.out.println("[Test] Cost: " + util.getCostStr(MS_START));
		assertTrue("Cost < 1000ms", util.getCostMillis(MS_START) < 1000);
	}
	
}
