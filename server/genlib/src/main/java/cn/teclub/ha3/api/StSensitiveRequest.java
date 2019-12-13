package cn.teclub.ha3.api;

/**
 * request needs UID, which is used to verify token owner in controller
 *
 * @author  Guilin Cao
 */
public class StSensitiveRequest extends StRequestBody {

    private long uid;

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Long getUid() {
        return uid;
    }

}
