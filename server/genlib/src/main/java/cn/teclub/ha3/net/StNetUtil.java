package cn.teclub.ha3.net;

import cn.teclub.common.ChuyuObj;
import cn.teclub.common.ChuyuUtil;

/**
 * <h1> Utility Class. </h1>
 * 
 * @author mancook
 */

@SuppressWarnings("ALL")
public class StNetUtil extends ChuyuObj {
	public static String flagValueStr(int flag){
		ChuyuUtil util = ChuyuUtil.getInstance();
		String s = "0x" + util.to8CharHex(flag) + " (" + util.toBinaryString(flag) + ")";
		return s;
	}
	
}//EOF: 


