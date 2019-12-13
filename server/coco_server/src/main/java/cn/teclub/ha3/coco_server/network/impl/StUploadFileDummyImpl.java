package cn.teclub.ha3.coco_server.network.impl;


import cn.teclub.ha3.coco_server.network.StIUploadFile;
import cn.teclub.ha3.utils.StObject;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author Tao Zhang
 */
@Component
public class StUploadFileDummyImpl extends StObject implements StIUploadFile {
    @Override
    public String uploadImage(String fileName, File file) {
        if (log.isDebugEnabled()){
            log.debug("uploadFile is success,but it dummy");
        }
        return null;
    }

}
