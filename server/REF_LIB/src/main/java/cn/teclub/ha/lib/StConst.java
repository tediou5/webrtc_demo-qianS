package cn.teclub.ha.lib;

import java.nio.charset.Charset;

import cn.teclub.common.ChuyuFamily;

/**
 * <h1>Constants. </h1>
 * 
 * @author mancook
 *
 */
public class StConst extends ChuyuFamily
{
	private static final int		VERSION_CODE = 7652;
	private static final String 	VERSION_NAME = "v4.4.3 Beta";
	
	// 'Alpha': when coding gen-lib;
	// 'Beta':  finish coding gen-lib and coding other projects;
	
	
	/**
	 * <pre>
	 * 
	 * v3.2.1
	 * ~~~~~~
	 * Implemented:
	 * - StLinphone2 is added, but not stable. Fall back to StLinphone.
	 * - FAMBO user-app can demonstrate following features: 
	 *   1) AC control, 2) Security, 3) Private Cloud, 4) Auto binding when sign-up, 5) ...
	 *   But they are NOT implemented! 
	 * 
	 * 
	 * v3.2.2
	 * ~~~~~~
	 * Implemented:
	 * - Use my own Flexisip server;
	 * 
	 * TO Be Implemented:
	 * - In next release, a simple version with only 
	 *   video-call feature will be implemented;
	 * 
	 * 
	 *  v3.2.3
	 *  ~~~~~~
	 *  - Add project 'Monitor', which is for Android smart-phone and tablet; 
	 *  - Time lapse can be recorded by Monitor;
	 *  - Video call works when recording time lapse;
	 * 
	 * 
	 *  v3.2.5 (7037)
	 *  ~~~~~~~~~~~~~
	 *  [Theodor: 2016-3-4]
	 *  - Replace StOperation with StExecution in StClient and StClientCore;
	 *  - Fix re-open bug: B16FEB2401 (Dialog was not ACK'd within T1*64 seconds);
	 *  - Use sip.teclub.cn;
	 *  
	 *  
	 *  v3.2.7 (7069)
	 *  ~~~~~~~~~~~~~
	 *  [Theodor: 2016-3-26]
	 *  - DO NOT send YOU_LOGOUT when fail to send/recv or SERVER_CHECK_CLINET timeout!
	 *    To fix bug: B16MAR2601
	 *  
	 *  
	 *  v4.0.1 (xxxx)
	 *  ~~~~~~~~~~~~~
	 *  [Theodor: 2016-6-15] TODO
	 *  - Start version v4.0 in branch dev-v4;
	 *  - Based on commit in dev-v3 branch: 
	 *          commit 010aab5c799af54e76989f7f54e08ac56ff906d8
	 *          Author: mancook <mancook@TdDevMac15>
	 *          Date:   Sun May 15 11:11:55 2016 +0800
	 *
     *          add README_cpp
	 * 
	 * 
	 *  v4.4.3 (n7165)
	 *  ~~~~~~~~~~~~~~~
	 *  [Theodor: 2016-8-24] 
	 *  - Video Call & TimeLapse works;
	 *  - Fambo & Monitor runs stable!
	 *  
	 *  
	 *  
	 *  =================================================================================
	 *  repository: st_server.git
	 *  =================================================================================
	 *  v4.0.11 (n7620+)
	 *  ~~~~~~~~~~~~~~~
	 *  [Theodore: 2018-01-12] 
	 *  - encrypt packet when sending.
	 *  
	 *  
	 *  </pre>
	 *  
	 *  
	 */
	
	private static final String  VERSION_INFO = VERSION_NAME + " -- n" + VERSION_CODE;
	
	public static final int  	PKT_MAX_LENGTH 			= 1024* 60;
	public static final Charset STRING_CODEC 			= Charset.forName("UTF8");

	
	// Heart Beat & Pulse
	public static final int  SYS_PULSE_POOL_PERIOD 	= 30 * 1000;	// defualt: 60*1000
	public static final int  SYS_PULSE_PERIOD_MS	= 500;
	public static final int  SYS_PULSE_RATE 		= 1000 / SYS_PULSE_PERIOD_MS;
	public static final int	 SYS_PULSE_SKIP_FRAME_TOO_MUCH = 10;  		// Default: 10; NOTE: we assume FRAME TIME as PUSLSE PERIOD

	public static final int  CLT_PULSE_PERIOD_MS	= 500;
	public static final int  CLT_PULSE_RATE 		= 1000 / CLT_PULSE_PERIOD_MS;
	public static final int  CLT_LBR_PULSE_PERIOD_MS	= 60*1000;
	
	public static final int  SRV_CORE_PULSE_PERIOD_MS	= 500;
	public static final int  SRV_CORE_PULSE_RATE 		= 1000 / SRV_CORE_PULSE_PERIOD_MS;

	
	
	////////////////////////////////////////////////////////////////////////////
	// server constants
	////////////////////////////////////////////////////////////////////////////
	public static final int SRV_CONN_GROUP_LOGCYCLE 	= 10*1000;
	public static final int SRV_CONN_GROUP_SLEEP_MILLI 	= 50; 

	
	////////////////////////////////////////////////////////////////////////////
	// constants used in classes
	////////////////////////////////////////////////////////////////////////////
	public static final int MAX_FRIEND 			= 128;
	public static final int MAX_QUERY_B_COUNT 	= MAX_FRIEND;
	public static final int WAIT_SLEEP_MS 		= 50; 			// ms: waiting sleep snap-period.
	public static final int WAIT_MAX 			= 200;			// loop max count when waiting
	public static final int GW_LOGIN_RETRY 		= 2;			// gateway retry times; Default: 5;
	public static final int GW_LOGIN_WAIT_MS 	= 1000*4;
	
	
	public static String getVersionInfo(){
		return VERSION_INFO;
	}
	
	
	
	public static void main(String[] args){
		System.out.println("--------------------------------------------------------");
		System.out.println("StGenLib Version: " + getVersionInfo() );
		System.out.println("--------------------------------------------------------");
		System.out.println("");
	}
}
