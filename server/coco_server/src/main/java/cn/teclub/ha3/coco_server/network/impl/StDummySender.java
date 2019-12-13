package cn.teclub.ha3.coco_server.network.impl;



import cn.teclub.ha3.coco_server.controller.exception.StException;
import cn.teclub.ha3.coco_server.network.StISMSSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SMS sender for Luosimao.
 *
 * @author Tao Zhang
 */
public class StDummySender implements StISMSSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(StDummySender.class);

    @Override
    public void init() {
    }

    @Override
    public void sendSMS(String mobile, String sms) throws StException {
        LOGGER.info("Sent message: '{}' to mobile: '{}'.", sms, mobile);
    }
}
