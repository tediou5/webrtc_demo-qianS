package cn.teclub.ha3.request;



public class StMobileAuthRes extends StUserResponse {
    private String mobile;

    private String authcode;

    public StMobileAuthRes(){
    }

    public String getAuthcode() {
        return authcode;
    }

    public void setAuthcode(String authcode) {
        this.authcode = authcode;
    }


    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

}


