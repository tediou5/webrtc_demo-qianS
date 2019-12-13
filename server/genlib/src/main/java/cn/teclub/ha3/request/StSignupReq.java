package cn.teclub.ha3.request;


import cn.teclub.ha3.utils.StObject;

public class StSignupReq extends StObject {
    //private String acct;
    private String pass;
    private String mobile;
    private String authcode;

    public StSignupReq(){
    }


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAuthcode() {
        return authcode;
    }

    public void setAuthcode(String authcode) {
        this.authcode = authcode;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }


    /*
    public String getAcct() {
        return acct;
    }

    public void setAcct(String acct) {
        this.acct = acct;
    }
    */
}


