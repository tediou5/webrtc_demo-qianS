package cn.teclub.ha3.coco_server.controller;

import cn.teclub.ha3.utils.StObject;

/**
 * request time model
 * @author Tao Zhang
 *
 */
public class StRequestTimeModel extends StObject  {

    /*
    public StRequestTimeModel(Long reqTime){
        this.reqTime=reqTime;
    }
    */
    public StRequestTimeModel(){
       this.reqTime = System.currentTimeMillis();
    }


    private final long reqTime;

    private Long  resTime;

    public Long getResTime() {
        return resTime;
    }

    public void setResTime(Long resTime) {
        this.resTime = resTime;
    }

    public Long getReqTime() {
        return reqTime;
    }

    public long getCost(){
        return resTime - reqTime;
    }


    @Override
    public String toString() {
        return "StRequestTimeModel{" +
                "reqTime=" + util.getTimeStampMS(reqTime) +
                ", resTime=" + util.getTimeStampMS(resTime) +
                ", cost=" + getCost() + "ms"+
                '}';
    }
}
