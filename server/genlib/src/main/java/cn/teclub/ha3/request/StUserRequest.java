package cn.teclub.ha3.request;

import cn.teclub.ha3.net.StClientID;
import cn.teclub.ha3.net.StClientInfo;
import cn.teclub.ha3.utils.StObject;


/**
 * super class for all user requests, except for SignUp
 */
public abstract class StUserRequest extends StObject {
    private long   uid;

    private String acct;

    private String token;


    public StUserRequest(){
    }

    public StClientID  fetchClientID(){
        return  new StClientID(uid);
    }


    public boolean isTokenValid(){
        return uid > 0 && token != null && token.length() > 10;
    }

    public String getAcct() {
        return acct;
    }

    public void setAcct(String acct) {
        this.acct = acct;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}


