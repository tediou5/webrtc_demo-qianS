package cn.teclub.ha3.server.controller;



import cn.teclub.ha3.server.common.StServicesProvider;
import cn.teclub.ha3.server.common.StUtil;
import cn.teclub.ha3.server.exceptions.StBadRequestException;
import cn.teclub.ha3.server.exceptions.StException;
import cn.teclub.ha3.server.exceptions.StInternalErrorException;
import cn.teclub.ha3.server.model.StAuthcode;
import cn.teclub.ha3.server.service.StAuthcodeService;
import cn.teclub.ha3.server.sys.StApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;

/**
 * Controller for common.
 */
@RestController
@RequestMapping("${rtc.api.prefix}/common")
public class StAuthcodeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(StAuthcodeController.class);

    @Autowired
    private StUtil stUtil;
    @Autowired
    private StAuthcodeService csAuthcodeService;
    @Resource
    private StApplicationProperties applicationProperties;

    @GetMapping("/authcode/{mobile}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void authcode(HttpServletRequest request, @PathVariable("mobile") String mobile) {
        if (!stUtil.isMobile(mobile)) {
            LOGGER.warn("Mobile is error.'{}'", mobile);
            throw new StBadRequestException();
        }

        String acode = stUtil.random(100000, 999999);
        long current = System.currentTimeMillis();
        Timestamp atime = new Timestamp(current + Long.valueOf(applicationProperties.getAuthCodeExpired()));
        Timestamp ctime = new Timestamp(current);
        StAuthcode csAuthcode = new StAuthcode();
        csAuthcode.setMobile(mobile);
        StAuthcode resultCsAuthcode = csAuthcodeService.getAuthcode(csAuthcode);
        if (null != resultCsAuthcode) {
            if (System.currentTimeMillis() - resultCsAuthcode.getCtime().getTime() < Long.valueOf(applicationProperties.getAuthCodeInterval())) {
                LOGGER.warn("Sending message too frequently for mobile:'{}'.", mobile);
                throw new StInternalErrorException();
            } else {
                resultCsAuthcode.setAuthcode(acode);
                resultCsAuthcode.setAtime(atime);
                resultCsAuthcode.setCtime(ctime);
                int count = csAuthcodeService.saveOrUpdate(resultCsAuthcode);
                if (count < 0) {
                    LOGGER.warn("Failed to update authentication code '{}'.", mobile);
                    throw new StInternalErrorException();
                }
            }
        } else {
            csAuthcode.setAuthcode(acode);
            csAuthcode.setAtime(atime);
            csAuthcode.setCtime(ctime);
            int count = csAuthcodeService.saveOrUpdate(csAuthcode);
            if (count < 0) {
                LOGGER.warn("Failed save sms authentication code '{}'.", mobile);
                throw new StInternalErrorException();
            }
        }
        try {
            StServicesProvider.INSTANCE.getSMSSender(applicationProperties.getAuthCodeSmssender()).sendSMS(mobile, acode);
        } catch (StException e) {
            LOGGER.warn("Failed to send message '{}'.", acode);
        }
    }
}
