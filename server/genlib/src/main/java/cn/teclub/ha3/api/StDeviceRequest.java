package cn.teclub.ha3.api;


/**
 * Request to operate a managed device.
 *
 * This is a sensitive request.
 *
 * [Theodor: 2019/11/27] rename from StAddDeviceRequest to StDeviceRequest
 * Reason: both add & delete controller use it!
 *
 * @author Guilin Cao
 */
public class StDeviceRequest extends  StSensitiveRequest {

    private Long did;

    public void setDid(Long did) {
        this.did = did;
    }

    public Long getDid() {
        return did;
    }
}
