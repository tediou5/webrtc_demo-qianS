package cn.teclub.ha3.api;


/**
 * rename from StDeviceInfoRequest to StEditDeviceRequest
 */
public class StDeviceInfoRequest extends  StDeviceRequest {

    private String label;

    private String desp;


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }
}
