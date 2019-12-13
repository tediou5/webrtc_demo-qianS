package cn.teclub.ha3.server.common;


import cn.teclub.ha3.server.exceptions.StException;

/**
 * Provides short message sending services.
 *
 * @author juewu
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
     * @param sms    StMessage content.
     * @throws StException Failed to send message.
     */
    void sendSMS(String mobile, String sms) throws StException, StException;
}
