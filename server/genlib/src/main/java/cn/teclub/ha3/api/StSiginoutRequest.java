package cn.teclub.ha3.api;

/**
 * @author Tao Zhang
 */
public class StSiginoutRequest extends  StSensitiveRequest {
    private String passwd;

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getPasswd() {
        return passwd;
    }
}
