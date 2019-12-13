package cn.teclub.ha3.request;


@SuppressWarnings("unused")
public class StLoginReq extends StUserRequest {
    private String pass;
    private String mobile;
    private String authcode;

    public StLoginReq(){
        //this.acct = "tom";
        //this.pass = "abcd1234";
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


}


