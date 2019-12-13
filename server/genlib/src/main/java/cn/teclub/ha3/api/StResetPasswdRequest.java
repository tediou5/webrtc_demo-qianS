package cn.teclub.ha3.api;

/**
 * StResetPasswdRequest
 * @author Tao Zhang
 */
public class StResetPasswdRequest extends StRequestBody {

    private String name;

    private String passwd;

    private String mobile;

    private String authcode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
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
}