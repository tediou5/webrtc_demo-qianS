package cn.teclub.ha3.api;

/**
 * request body for applyAdd & grantAdd APIs
 *
 * @author zhangtao
 */
public class StAddContactRequest extends StSensitiveRequest {

    private Long sid;

    private Long tid;

    private RequestType type;

    public void setType(RequestType type) {
        this.type = type;
    }

    public RequestType getType() {
        return type;
    }

    public void setSid(Long sid) {
        this.sid = sid;
    }

    public Long getSid() {
        return sid;
    }

    public void setTid(Long tid) {
        this.tid = tid;
    }

    public Long getTid() {
        return tid;
    }

    public enum RequestType{
        APPLY,
        APPROVE,    // grandAdd approves the APPLY request
        REJECT,     // grandAdd rejects  the APPLY request
    }
}
