package cn.teclub.ha.test;

import cn.teclub.common.ChuyuUtil;
import junit.framework.TestCase;



class FooDbObject 
{
	static int count = 0;
	
	final String name ;
	FooDbObject(String name){
		count++;
		this.name = name;
	}
}



class FooObject
{
	static int count = 0;
	
	FooObject(){
		count++;
	}
}


/**
 * Run with JUnit3
 * 
 * @author mancook
 *
 */
public class TestBasic extends TestCase {
	static ChuyuUtil util = ChuyuUtil.getInstance();
	
	// opertion count: 10M
	static final int M =1024*10;
	static final int N = 1024;
	
	
	
	public void setUp(){
		System.out.println("--------------------------------------------------------------");
	}
	
	
	public void tearDown(){
		System.out.println("----  END  ---------------------------------------------------");
	}

	
	private void finishTC(final long ms_start,  int MAX_MS, String msg){
		long ms_cost = util.getCostMillis(ms_start);
		System.out.println("" + msg + " -- cost: " + ms_cost + "ms  (MAX: " + MAX_MS + "ms)");
		assertTrue("Cost < " + MAX_MS + "ms", util.getCostMillis(ms_start) < MAX_MS);
	}
	
	
	
	public void testAssignCost(){
		final long MS_START = System.currentTimeMillis();
		
		int c = 0;
		for(int i=0; i<M; i++){
			for(int j=0; j<N; j++){
				c++;
			}
		}
		System.out.println("c=" + c + "/0x" + Integer.toHexString(c) );
		
		finishTC(MS_START, 1000, "int assignment");
		//System.out.println("[testAssignCost] Cost: " + util.getCostStr(MS_START));
		//assertTrue("Cost < 1000ms", util.getCostMillis(MS_START) < 1000);
	}
	
	
	public void testStringOpt(){
		final long MS_START = System.currentTimeMillis();
		//final int M=1024*20, N = 1024;
		String s0 = "<null>";
		int c = 0;
		for(int i=0; i<M; i++){
			for(int j=0; j<N; j++){
				c++;
				s0 = "[String] + [Int]" + c;
			}
		}
		c--;
		String s1 = "String + [Int]0x" + Integer.toHexString(c);
		System.out.println("s0=" + s0 );
		System.out.println("s1=" + s1 );
		finishTC(MS_START, 1000, "string add operation: [String] + [Int] ");
		//System.out.println("[testStringOpt] Cost: " + util.getCostStr(MS_START));
		//assertTrue("Cost < 1000ms", util.getCostMillis(MS_START) < 1000);
	}
	
	
	public void testStringOpt2(){
		final long MS_START = System.currentTimeMillis();
		// final int M=1024*4 *10, N = 256;
		String s0 = "<null>";
		int c = 0;
		for(int i=0; i<M; i++){
			s0 = "";
			for(int j=0; j<N; j++){
				c++;
				s0 += "A";  
			}
		}
		
		String s1 = "String + [Int]0x" + Integer.toHexString(c);
		//final long ms_cost = util.getCostMillis(MS_START);
		//final String COST_STR = util.getCostStr(MS_START);
		System.out.println("s0=" + s0 );
		System.out.println("s1=" + s1 );
		
		finishTC(MS_START, 1000, "string add operation: [string] += [string]");
		//System.out.println("[testStringOpt2] Count: 0x" + util.to8CharHex(c) + ", Cost: " + COST_STR);
		//assertTrue("Cost < 1000ms", ms_cost < 1000);
	}
	
	
	public void testStringBufferOpt(){
		final long MS_START = System.currentTimeMillis();
		//final int M=1024*4 *10, N = 256;
		int c = 0;
		StringBuilder sbuf = new StringBuilder(1024);
		for(int i=0; i<M; i++){
			sbuf.setLength(0);
			for(int j=0; j<N; j++){
				c++;
				sbuf.append("A");
			}
			sbuf.append(c);
		}
		//final String COST_STR = util.getCostStr(MS_START);
		sbuf.setLength(12);
		System.out.println("sbuf=" + sbuf);
		
		finishTC(MS_START, 1000, "stringbuffer append operation");
		//System.out.println("[testStringBufferOpt] Count: 0x" + util.to8CharHex(c) + ", Cost:" + COST_STR);
		//assertTrue("Cost < 1000ms", util.getCostMillis(MS_START) < 1000);
	}
	
	
	public void testCreateInstance() throws IllegalAccessException{
		{
			FooDbObject f1 = new FooDbObject("Dennis");
			FooDbObject f2 = null;
			try {
				f2 = f1.getClass().newInstance();
			} catch (InstantiationException e) {
				System.out.println("[NOT ERROR] newInstance() throws exception: " + e);
				// e.printStackTrace();
			}
			assertTrue("One Objects", FooDbObject.count == 1);
			assertTrue("f2 is null", f2 == null);
		}
		
		
		{
			FooObject f1 = new FooObject();
			FooObject f2 = null;
			try {
				f2 = f1.getClass().newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			}
			assertTrue("Two Objects", FooObject.count == 2);
			assertTrue("f2 is NOT null", f2 != null);
		}
	}
	
	
	public void testMacAddress() {
		String[] strs = {
			"7c-d1-c3-91-f6-9c", 
			"7c-d1-c3-91-F6-9C", 
			"7c-d1-c3-91-f6:9c",
			"are your OK"
		};

		for(final String s: strs){
			final boolean is_mac = util.stringFunc.isMacAddr(s);
			System.out.println("Number '" + s + "' " + (is_mac ? "is" : "is NOT") + " a mac address.");
		}
		
		assertTrue("valid mac address", util.stringFunc.isMacAddr(strs[0]));
		assertTrue("valid mac address", util.stringFunc.isMacAddr(strs[1]));
		assertTrue("invalid mac address", !util.stringFunc.isMacAddr(strs[2]));
		assertTrue("invalid mac address", !util.stringFunc.isMacAddr(strs[3]));
	}
	
	
	public void testPhoneNumber() {
		String[] strs = {
			"13918081932", 
			"18918081932",  
			"139-18081932", 
			"139-1808-1932",
			
			"1391808193x", 
			"1918081932",
			"139-1808-1932"
		};
		
		for(final String s: strs){
			final boolean is_phone = util.stringFunc.isPhoneNumber(s);
			System.out.println("Number '" + s + "' " + (is_phone ? "is" : "is NOT") + " a phone number.");
		}
		
		assertTrue("valid phone number",    util.stringFunc.isPhoneNumber(strs[0]));
		assertTrue("valid phone number",    util.stringFunc.isPhoneNumber(strs[1]));
		assertTrue("invalid phone number", !util.stringFunc.isPhoneNumber(strs[2]));
		assertTrue("invalid phone number", !util.stringFunc.isPhoneNumber(strs[3]));
		assertTrue("invalid phone number", !util.stringFunc.isPhoneNumber(strs[4]));
		assertTrue("invalid phone number", !util.stringFunc.isPhoneNumber(strs[5]));
	}
	
}
