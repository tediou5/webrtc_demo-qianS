package cn.teclub.ha3.server.common.impl;



import cn.teclub.ha3.server.common.StISMSSender;
import cn.teclub.ha3.server.exceptions.StException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SMS sender for Luosimao.
 *
 * @author juewu
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
