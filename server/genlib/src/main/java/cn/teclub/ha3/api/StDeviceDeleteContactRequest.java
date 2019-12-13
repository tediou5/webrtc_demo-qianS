package cn.teclub.ha3.api;

/**
 * Request to delete the contact of the managed device.
 *
 * This is a sensitive request.
 * [2019/12/2]
 * @author Tao Zhang
 */
public class StDeviceDeleteContactRequest extends StDeviceRequest{

    private Long cid ;

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public Long getCid() {
        return cid;
    }
}
