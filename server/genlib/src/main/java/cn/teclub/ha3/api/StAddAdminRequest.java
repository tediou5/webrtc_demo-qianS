package cn.teclub.ha3.api;

/**
 * Request to add a device.
 *
 * This is a sensitive request.
 * Reason:  add controller use it!
 * @author Tao Zhang
 */
public class StAddAdminRequest extends StSensitiveRequest{
    private Long did;

    public void setDid(Long did) {
        this.did = did;
    }

    public Long getDid() {
        return did;
    }
}
