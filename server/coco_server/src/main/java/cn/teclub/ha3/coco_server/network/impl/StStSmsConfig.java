package cn.teclub.ha3.coco_server.network.impl;

import cn.teclub.ha3.coco_server.sys.StApplicationProperties;
import cn.teclub.ha3.utils.StObject;

import java.util.Locale;
import java.util.ResourceBundle;


/**
 * <h1>Server Configurations.</h1>
 * 
 * <p> Used by both Single & Group Server. 
 * 
 * @author Tao Zhang
 *
 */
public class StStSmsConfig extends StObject
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	
	
	private static final String BUDDLE_NAME = "application";
	private static StStSmsConfig _ins;
	
	public static StStSmsConfig getInstance(StApplicationProperties properties){
		if(_ins == null){
			_ins = new StStSmsConfig(properties);
		}
		return _ins;
	}

/*	public static StStSmsConfig getInstance(){
		if(_ins == null){
			_ins = new StStSmsConfig();
		}
		return _ins;
	}*/
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Members
	////////////////////////////////////////////////////////////////////////////
	
	public final String SMS_URL_SENDX;
	public final String SMS_USER, SMS_KEY;
	public final String TEMPLATE_ID;
	
	

/*	private StStSmsConfig() {
		System.out.println("[SMS] Loading config from '" + BUDDLE_NAME  + "'...");
		ResourceBundle res  = ResourceBundle.getBundle(BUDDLE_NAME, Locale.getDefault());
		
		SMS_URL_SENDX	= res.getString("rtc.authCode.sms_url_sendx").trim();
		SMS_USER		= res.getString("rtc.authCode.sms_user").trim();
		SMS_KEY			= res.getString("rtc.authCode.sms_key").trim();
		TEMPLATE_ID		= res.getString("rtc.authCode.template_id").trim();
	}*/

	/**
	 * Constructor.
	 */
	private StStSmsConfig(StApplicationProperties properties){
		SMS_URL_SENDX = properties.getAuthCodeSmsUrlSendx();
		SMS_USER = properties.getAuthCodeSmsUser();
		SMS_KEY = properties.getAuthCodeSmsKey();
		TEMPLATE_ID = properties.getAuthCodeTemplateId();
	}

}

