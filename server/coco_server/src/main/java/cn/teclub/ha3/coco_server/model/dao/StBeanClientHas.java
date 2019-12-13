package cn.teclub.ha3.coco_server.model.dao;

/**
 * clientHas model:  mapped to a record in DB table tb_client_has
 *
 * @author Tao Zhang
 */
public class StBeanClientHas {
    private Long id;

    private Long cltA;

    private Long cltB;

    private Integer flag;

    private Integer type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCltA() {
        return cltA;
    }

    public void setCltA(Long cltA) {
        this.cltA = cltA;
    }

    public Long getCltB() {
        return cltB;
    }

    public void setCltB(Long cltB) {
        this.cltB = cltB;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public enum StClientHasType{
        NOTADMIN,ISADMIN,ADMINBY
    }
}