package cn.teclub.ha3.server.common.impl;


import cn.teclub.ha3.server.common.StIUploadFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class StUploadFileDummyImpl implements StIUploadFile {
    private static final Logger LOGGER = LoggerFactory.getLogger(StUploadFileDummyImpl.class);
    @Override
    public String uploadImage(String fileName, File file) {
        if (LOGGER.isDebugEnabled()){
            LOGGER.debug("uploadFile is success,but it dummy");
        }
        return null;
    }

}
