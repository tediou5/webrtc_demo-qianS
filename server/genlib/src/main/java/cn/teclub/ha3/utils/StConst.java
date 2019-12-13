package cn.teclub.ha3.utils;

import java.nio.charset.Charset;

import cn.teclub.common.ChuyuFamily;

/**
 * <h1>Constants. </h1>
 * 
 * @author mancook
 *
 */
@SuppressWarnings("WeakerAccess")
public class StConst extends ChuyuFamily
{
	public static final Charset STRING_CODEC 			= Charset.forName("UTF8");


	////////////////////////////////////////////////////////////////////////////
	// constants used in classes
	////////////////////////////////////////////////////////////////////////////
	public static final int PKT_MAX_LENGTH 		= 1024*32;
	public static final int MAX_FRIEND 			= 128;
	public static final int MAX_QUERY_B_COUNT 	= MAX_FRIEND;
	public static final int WAIT_SLEEP_MS 		= 50; 			// ms: waiting sleep snap-period.
	public static final int WAIT_MAX 			= 200;			// loop max count when waiting
	public static final int GW_LOGIN_RETRY 		= 2;			// gateway retry times; Default: 5;
	public static final int GW_LOGIN_WAIT_MS 	= 1000*4;


	public static final long TOKEN_LIFE_MS		= 100*24*3600*1000; //  30 days

    /**
     * use auto-generated StVersion by genlib/build.gradle
     */
	public static String getVersionInfo(){
		return "v1.0.11-snapshot";
	}

	public static void main(String[] args){
		System.out.println("--------------------------------------------------------");
		System.out.println("StGenLib Version: " + getVersionInfo() );
		System.out.println("--------------------------------------------------------");
		System.out.println("");
	}
}
