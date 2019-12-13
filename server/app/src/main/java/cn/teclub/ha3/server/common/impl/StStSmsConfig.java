package cn.teclub.ha3.server.common.impl;

import java.util.Locale;
import java.util.ResourceBundle;


/**
 * <h1>Server Configurations.</h1>
 * 
 * <p> Used by both Single & Group Server. 
 * 
 * @author mancook
 *
 */
public class StStSmsConfig
{
	////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS 
	////////////////////////////////////////////////////////////////////////////	
	
	private static final String BUDDLE_NAME = "application";
	private static StStSmsConfig _ins;
	
	public static StStSmsConfig getInstance(){
		if(_ins == null){
			_ins = new StStSmsConfig();
		}
		return _ins;
	}
	
	////////////////////////////////////////////////////////////////////////////
    // Instance Members
	////////////////////////////////////////////////////////////////////////////
	
	public final String SMS_URL_SENDX;
	public final String SMS_USER, SMS_KEY;
	public final String TEMPLATE_ID;
	
	
	/**
	 * Constructor.
	 */
	private StStSmsConfig() {
		System.out.println("[SMS] Loading config from '" + BUDDLE_NAME  + "'...");
		ResourceBundle res  = ResourceBundle.getBundle(BUDDLE_NAME, Locale.getDefault());
		
		SMS_URL_SENDX	= res.getString("feisuo.authCode.sms_url_sendx").trim();
		SMS_USER		= res.getString("feisuo.authCode.sms_user").trim();
		SMS_KEY			= res.getString("feisuo.authCode.sms_key").trim();
		TEMPLATE_ID		= res.getString("feisuo.authCode.template_id").trim();
	}

}

