package cn.teclub.ha3.server.common;



import cn.teclub.ha3.server.common.impl.StAwsUploadFileImpl;
import cn.teclub.ha3.server.common.impl.StDummySender;
import cn.teclub.ha3.server.common.impl.StStSmsOptSender;
import cn.teclub.ha3.server.common.impl.StUploadFileDummyImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StServicesProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(StServicesProvider.class);
    public static final StServicesProvider INSTANCE = new StServicesProvider();

    private StISMSSender smsSender = null;
    private StIUploadFile uploadFile = null;

    private StServicesProvider() {
        // Singleton
    }

    public StISMSSender getSMSSender(String sender) {
        if (smsSender == null) {
            synchronized (this) {
                if (smsSender == null) {
                    if ("stsms".equalsIgnoreCase(sender)) {
                        smsSender = new StStSmsOptSender();
                    } else {
                        smsSender = new StDummySender();
                    }
                }
            }
        }
        return smsSender;
    }

    public StIUploadFile getUploadFile(String loader) {
        if (uploadFile == null) {
            synchronized (this) {
                if (uploadFile == null) {
                    if("aws".equalsIgnoreCase(loader)){
                        uploadFile = new StAwsUploadFileImpl();
                    }else{
                        uploadFile = new StUploadFileDummyImpl();
                    }
                }
            }
        }
        return uploadFile;
    }
}
