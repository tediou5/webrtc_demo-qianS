package cn.teclub.ha3.api;

/**
 * StUserInfoRequest
 * @author  Tao Zhang
 */
public class StUserInfoRequest extends StSensitiveRequest {


    private String name;

    private String birthday;

    private String phone;

    private String label;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }
}