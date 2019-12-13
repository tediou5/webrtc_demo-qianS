package cn.teclub.ha.net.serv;

import java.util.ArrayList;
import java.util.HashMap;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.lib.StEvent;
import cn.teclub.ha.lib.StEventListener;
import cn.teclub.ha.lib.StEventPulse;



/**
 * SMS Code for Registration, etc.
 * 
 * @author mancook
 *
 */
public class StSrvSmsCode extends ChuyuObj 
{
	public final String phone;
	public final String code;
	public final long   tsCreate;
	public final long   MS_TIMEOUT;
	
	/**
	 * ONLY call this method in sms-code-manager
	 */
	StSrvSmsCode(final String code, final String phone){
		this.code = code;
		this.phone = phone;
		this.tsCreate = System.currentTimeMillis();
		this.MS_TIMEOUT = 2*60*1000; // 2min
		stLog.info("Created SmsCode Object: \n" + this.toStringXml());
	}
	
	
	boolean isTimeout(){
		return (System.currentTimeMillis() - tsCreate ) > MS_TIMEOUT;  
	}
}




class StSrvSmsCodeMgr 
		extends ChuyuObj  
		implements StEventListener
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBER
	////////////////////////////////////////////////////////////////////////////	
	
	////////////////////////////////////////////////////////////////////////////
    // Inner Class
	////////////////////////////////////////////////////////////////////////////
	
	private static final Object 	_initLock = new Object();
	private static StSrvSmsCodeMgr 	_instance = null;
	
	
	/**
	 * ATTENTION: Without '_initLock', this method is NOT Thread Safe! <br/>
	 * In this case, call this method only in one thread!
	 * 
	 * @param pulse
	 */
	static void initialize(final StEventPulse pulse){
		synchronized (_initLock) {
			util.assertTrue(_instance == null);
			_instance = new StSrvSmsCodeMgr(pulse);
		}
	}

	
	public static StSrvSmsCodeMgr getInstance(){
		util.assertNotNull(_initLock);
		return _instance;
	}
	
	
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Member
	////////////////////////////////////////////////////////////////////////////
	
	private final StEventPulse pulse;
	private final HashMap<String, StSrvSmsCode> map = new HashMap<String, StSrvSmsCode>();
	
	private int count;
	private final int CHECK_PERIOD_HB; 
	
	
	/**
	 * Constructor
	 * 
	 * @param pulse
	 */
	private StSrvSmsCodeMgr(StEventPulse pulse){
		this.pulse = pulse;
		this.pulse.addListener(this);
		this.CHECK_PERIOD_HB =  (int) (10*1000 / pulse.msPeriod);
	}

	
	synchronized StSrvSmsCode createSmsCode(final String phone) {
		while(true){
			long d1 =  System.currentTimeMillis() % (1000*1000);
			if(d1 < 100*1000 ){
				d1 += 100*1000;
			}
			
			final String code = "" + d1;
			if(map.get(code) == null ){
				// find un-used sms-code
				StSrvSmsCode sms_code = new StSrvSmsCode(code, phone);
				map.put(code,  sms_code);
				return sms_code;
			}
		}
	}

	
	synchronized void checkTimeout(){
		ArrayList<String> del_list = new ArrayList<String>();
		for(StSrvSmsCode e: map.values()){
			if(e.isTimeout()){
				del_list.add(e.code);
			}
		}
		
		for(String c: del_list){
			StSrvSmsCode e = map.remove(c);
			stLog.info("#### Delete TIMEOUT SmsCode: " + e );
		}
	}
	
	

	synchronized StSrvSmsCode get(String sms_code) {
		return map.get(sms_code);
	}
	
	// -------------------------------------------------------------------------
	// Event Listener
	// -------------------------------------------------------------------------
	
	
	
	@Override
	public String getEvtLisName() {
		return "Lis-SmsCodeMgr";
	}


	@Override
	public void handleEvent(StEvent event) {
		if(event instanceof StEvent.HeartBeat){
			count++;
			if(count % CHECK_PERIOD_HB == 0){
				checkTimeout();
			}
			return;
		}
	}

}