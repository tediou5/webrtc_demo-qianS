package cn.teclub.ha3.coco_server.network;


import cn.teclub.ha3.coco_server.controller.exception.StException;

/**
 * Provides short message sending services.
 *
 * @author Tao Zhang
 */
public interface StISMSSender {

    /**
     * Init method. Must execute before invoke other APIs.
     */
    void init();

    /**
     * Send a short message according to a specific mobile number.
     *
     * @param mobile Mobile number to send short message to.
     * @param sms    StBeanMessage content.
     * @throws StException Failed to send message.
     */
    void sendSMS(String mobile, String sms) throws StException;
}
