package cn.teclub.ha3.api;

/**
 * StPasswdRequest
 * @author Tao Zhang
 */
public class StPasswdRequest extends StSensitiveRequest{

    private String passwd;

    private String newPasswd;

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getNewPasswd() {
        return newPasswd;
    }

    public void setNewPasswd(String newPasswd) {
        this.newPasswd = newPasswd;
    }
}