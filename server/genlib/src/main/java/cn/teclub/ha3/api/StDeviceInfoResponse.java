package cn.teclub.ha3.api;

/**
 * @author Tao Zhang
 */
public class StDeviceInfoResponse {

    private Long did;

    private String name;

    private String label;

    private String desp;

    public void setDid(Long did) {
        this.did = did;
    }

    public Long getDid() {
        return did;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
