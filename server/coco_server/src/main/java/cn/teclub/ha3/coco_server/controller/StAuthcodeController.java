package cn.teclub.ha3.coco_server.controller;


import cn.teclub.ha3.coco_server.controller.exception.StBadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller for authCode.
 * @author Tao Zhang
 */
@RestController
@RequestMapping("${rtc.api.prefix}/common")
public class StAuthcodeController extends StControllerBase {

    @Autowired
    StClientManager clientManager;

    @GetMapping("/authcode/{mobile}")
    public void authcode(HttpServletRequest request, @PathVariable("mobile") String mobile) {
        if (!serverUtil.isMobile(mobile)) {
            log.warn("Mobile is error.'{}'", mobile);
            throw new StBadRequestException();
        }
        clientManager.generateAuth(mobile);
    }
}
