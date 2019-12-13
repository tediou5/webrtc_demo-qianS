package cn.teclub.sms;

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
public class StSmsConfig
{
    ////////////////////////////////////////////////////////////////////////////
    // STATIC MEMBERS
    ////////////////////////////////////////////////////////////////////////////

    private static final String BUDDLE_NAME = "st_sms";
    private static StSmsConfig   _ins;

    public static StSmsConfig getInstance(){
        if(_ins == null){
            _ins = new StSmsConfig();
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
    private StSmsConfig() {
        System.out.println("[SMS] Loading config from '" + BUDDLE_NAME  + "'...");
        ResourceBundle res  = ResourceBundle.getBundle(BUDDLE_NAME,Locale.ENGLISH);
        //ResourceBundle res  = ResourceBundle.getBundle(BUDDLE_NAME, Locale.getDefault());

        SMS_URL_SENDX	= res.getString("sms_url_sendx").trim();
        SMS_USER		= res.getString("sms_user").trim();
        SMS_KEY			= res.getString("sms_key").trim();
        TEMPLATE_ID		= res.getString("template_id").trim();
    }

}

