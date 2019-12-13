package cn.teclub.ha3.api;

/**
 * @author Tao Zhang
 */
public class StLoginWPhoneRequest extends StRequestBody {

    private String phone;

    private String authCode;

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }
}
