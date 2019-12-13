package cn.teclub.ha3.coco_server.network;


import cn.teclub.ha3.coco_server.network.impl.StAwsUploadFileImpl;
import cn.teclub.ha3.coco_server.network.impl.StDummySender;
import cn.teclub.ha3.coco_server.network.impl.StStSmsOptSender;
import cn.teclub.ha3.coco_server.network.impl.StUploadFileDummyImpl;
import cn.teclub.ha3.coco_server.sys.StApplicationProperties;
import cn.teclub.ha3.utils.StObject;
import com.amazonaws.services.dynamodbv2.xspec.S;
import org.springframework.stereotype.Component;

/**
 * factory for SMS, Upload provide service
 *
 * @author Tao Zhang
 */
@Component
public class StServicesProvider extends StObject {
    public static  StServicesProvider INSTANCE;

    private StISMSSender smsSender = null;
    private StIUploadFile uploadFile = null;

    private StApplicationProperties properties;

    private StServicesProvider(StApplicationProperties properties){
        this.properties=properties;
    }

    public final static StServicesProvider getInstance(StApplicationProperties properties){
        if(INSTANCE==null){
            return  new StServicesProvider(properties);
        }
        return INSTANCE;
    }

    public StISMSSender getSMSSender(String sender) {
        if (smsSender == null) {
            synchronized (this) {
                if (smsSender == null) {
                    log.debug("authcode smssender is {}",sender);
                    if ("stsms".equalsIgnoreCase(sender)) {
                        smsSender = StStSmsOptSender.getInstance(properties);
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
                    log.debug("file uploader is {}",loader);
                    if("aws".equalsIgnoreCase(loader)){
                        uploadFile = new StAwsUploadFileImpl(properties);
                    }else{
                        uploadFile = new StUploadFileDummyImpl();
                    }
                }
            }
        }
        return uploadFile;
    }
}
