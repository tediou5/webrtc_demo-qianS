package cn.teclub.ha3.api;

/**
 * Request to delete the contact.
 *
 * This is a sensitive request.
 * [2019/12/3]
 * @author Tao Zhang
 */
public class StDeleteContactRequest extends StSensitiveRequest{

    private Long cid ;

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public Long getCid() {
        return cid;
    }
}
