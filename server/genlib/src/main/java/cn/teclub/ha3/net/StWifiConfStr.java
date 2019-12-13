package cn.teclub.ha3.net;

import cn.teclub.common.ChuyuObj;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Convert between wifi SSID-PASSWORD pair and a string
 *
 * Created by mancook on 28/3/2018.
 */
@SuppressWarnings("ALL")
public class StWifiConfStr extends ChuyuObj
{
    public static StWifiConfStr.Security getWifiSecurity(String capabilities ){
        if(capabilities.contains("WPA")){
            return StWifiConfStr.Security.WPA;
        }
        if(capabilities.contains("WEP")){
            return StWifiConfStr.Security.WEP;
        }
        return StWifiConfStr.Security.NONE;
    }


    public enum Security {
        NONE, WEP, WPA,
    }


    public final String ssid, password;
    public final Security security;


    public StWifiConfStr(String ssid, String password, Security s){
        this.ssid       = ssid;
        this.password   = password;
        this.security   = s;
    }


    public StWifiConfStr(String raw_str){
        util.assertTrue(raw_str.length() >= 10 );
        /*
        int v = Integer.valueOf( raw_str.substring(0 , 1));
        util.assertTrue(0<= v && v <=2);
        security =  Security.values()[v]; */

        String p = "([012])(.+[^\\\\])/(.+)";
        Pattern r = Pattern.compile(p);
        Matcher m = r.matcher(raw_str);
        util.assertTrue(m.find());

        int v = Integer.valueOf( m.group(1) );
        this.security   = Security.values()[v];
        this.ssid       = m.group(2).replaceAll( "\\\\\\\\", "\\\\" ).replaceAll("\\\\/", "/" );
        this.password   = m.group(3).replaceAll( "\\\\\\\\", "\\\\" ).replaceAll("\\\\/", "/" );
    }



    /**
     * <pre>
     * [0]: 0: NONE, 1: WEP, 2: WPA
     * [1,...): SSID & password, separated by '/';
     *
     * ~~~~~~~~~~~~~~~~~~~~~~
     *     meta character
     * ~~~~~~~~~~~~~~~~~~~~~~
     * - add prefix char '$';
     * - all meta char: ';', '$';
     *
     * e.g. password '/[]abcD[]\' will be encoded to '\/[]abcD[]\\';
     * </pre>
     *
     *
     * @return encoded string
     */
    public String encode(){
        StringBuffer sbuf = new StringBuffer(64);

        sbuf.append(security.ordinal());
        sbuf.append(ssid    .replaceAll( "\\\\", "\\\\\\\\" ).replaceAll("/", "\\\\/" ));
        sbuf.append("/");
        sbuf.append(password.replaceAll( "\\\\", "\\\\\\\\" ).replaceAll("/", "\\\\/" ));

        return sbuf.toString();
    }


    public String toString(){
        return "[" + security + "] SSID: " + ssid + ", PASS: " + password;
    }



    public static void main(String[] args){
        System.out.println("=======================");
        StWifiConfStr w01 = new StWifiConfStr("st_mobile_lib", "qweR1234", Security.WPA);
        StWifiConfStr w02 = new StWifiConfStr("st_mobile_lib", ";;/qweR/\\1234\\", Security.WPA);

        System.out.println("w01: " + w01 + "  -- '" + w01.encode()  + "'" );
        System.out.println("w02: " + w02 + "  -- '" + w02.encode()  + "'" );

        StWifiConfStr w11 = new StWifiConfStr(w01.encode());
        StWifiConfStr w12 = new StWifiConfStr(w02.encode());

        System.out.println("w11: " + w11  );
        System.out.println("w12: " + w12  );


        util.assertTrue(w11.toString().equals(w01.toString()));
        util.assertTrue(w12.toString().equals(w02.toString()));
    }

}
