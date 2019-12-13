package cn.teclub.ha.client.rpr;

import java.util.ArrayList;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.client.rpr.StcService;


/**
 * All Service instances are created at app starts up,
 * and destroyed when it ends.
 * 
 * 
 * @author mancook
 */
public class StcServiceTank extends ChuyuObj 
{
	private static StcServiceTank _ins = new StcServiceTank();
	public static StcServiceTank getInstance(){
		return _ins;
	}
	
	public final ArrayList<StcService> servList = new ArrayList<StcService>();
	
	private StcServiceTank(){
		//		servList.add(new StcAServiceP2pCallStart());
		//		servList.add(new StcAServiceP2pCallEnd());
		//		servList.add(new StcAServiceP2pQueryState());
		//		servList.add(new StcAServiceP2pRecordStart());
		//		servList.add(new StcAServiceP2pRecordStop());
		//		servList.add(new StcAServiceP2pRestartApp());
		//		servList.add(new StcAServiceP2pQueryTL());
		//		servList.add(new StcAServiceP2pCallGetRec());
	}
	
}
