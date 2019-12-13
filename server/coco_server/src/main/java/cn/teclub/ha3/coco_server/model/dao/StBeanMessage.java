package cn.teclub.ha3.coco_server.model.dao;

import cn.teclub.ha3.request.StWsMessage;

import java.util.Date;

/**
 * A message stored in DB and relayed by server.
 *
 * @author Tao Zhang, Guilin Cao
 */
public class StBeanMessage {
    private Long id;


    /**
     * type of this message. e.g. TEXT, APPLY_ADD_DEVICE, GRANT_ADD_DEVICE
     */
    private Integer cmd;

    /**
     * 1: message is received by DB;
     * 0: message is sent to its dest;
     */
    private Integer  state;


    /**
     * currently NOT used
     */
    private Integer flag;

    private Integer len;

    private Long src;

    private Long dst;

    private Date startTime;

    private Date endTime;

    private String  info;

    public StBeanMessage() { }


    public StBeanMessage(StWsMessage ws_msg) {
        this.id = ws_msg.getSsid();
        this.setInfo(ws_msg.getInfo());
        //this.setStartTime(new Date());
        //this.setEndTime(new Date());
        this.setCmd(ws_msg.getCommand().ordinal());
        this.setSrc(ws_msg.getSrc());
        this.setDst(ws_msg.getDst());
        this.setLen(ws_msg.getInfo().length());
        this.setInfo(ws_msg.getInfo());
    }

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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getCmd() {
        return cmd;
    }

    public void setCmd(Integer cmd) {
        this.cmd = cmd;
    }

    public Integer getLen() {
        return len;
    }

    public void setLen(Integer len) {
        this.len = len;
    }

    public Long getSrc() {
        return src;
    }

    public void setSrc(Long src) {
        this.src = src;
    }

    public Long getDst() {
        return dst;
    }

    public void setDst(Long dst) {
        this.dst = dst;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public enum MessageState{
       HASSENT,UNSENT
    }
}
