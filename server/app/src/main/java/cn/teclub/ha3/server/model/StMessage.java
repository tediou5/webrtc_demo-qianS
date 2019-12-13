package cn.teclub.ha3.server.model;

import java.util.Date;

public class StMessage {
    private Long id;

    private Integer flag;

    private Integer dataLen;

    private Long cltA;

    private Long cltB;

    private Date startTime;

    private Date endTime;

    private byte[] data;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public Integer getDataLen() {
        return dataLen;
    }

    public void setDataLen(Integer dataLen) {
        this.dataLen = dataLen;
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}