package cn.teclub.ha3.coco_server.sys.interceptor;

import cn.teclub.ha3.api.StHeaders;

import cn.teclub.ha3.coco_server.controller.exception.StUnauthorizedException;
import cn.teclub.ha3.coco_server.controller.StRequestTimeModel;
import cn.teclub.ha3.coco_server.model.dao.StBeanToken;
import cn.teclub.ha3.coco_server.model.StTokenService;
import cn.teclub.ha3.coco_server.sys.StSystemConstant;
import cn.teclub.ha3.utils.StObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;

/**
 * Interceptor to check request.
 * @author Tao Zhang
 */
@Component
public class StUserInterceptor extends StObject implements HandlerInterceptor {

    private final String API_COST_TRACK = "requestTimeModel";

    @Autowired
    private StTokenService tokenService;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse arg1, Object arg2, Exception arg3)
            throws Exception {
        StRequestTimeModel api_cost = (StRequestTimeModel) request.getAttribute(API_COST_TRACK);
        api_cost.setResTime(System.currentTimeMillis());
        log.info(request.getRequestURI() + " ---- afterCompletion API COST: " + api_cost);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object arg2, ModelAndView arg3)
            throws Exception {
        StRequestTimeModel api_cost = (StRequestTimeModel) request.getAttribute(API_COST_TRACK);
        api_cost.setResTime(System.currentTimeMillis());
        log.info(request.getRequestURI() + " ---- postHandle API COST: " + api_cost);
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
        log.warn("## preHandle");
        String token = request.getHeader(StHeaders.TOKEN);
        if (StringUtils.isNotEmpty(token)) {
            String decoded = new String(Base64.decodeBase64(token));
            if (StringUtils.isNotEmpty(decoded)) {
                String[] parts = decoded.split(StSystemConstant.TOKEN_SPLITTER);
                if (parts.length == 2) {
                    String suid = parts[0];
                    long uid = Long.parseLong(suid);

                    StBeanToken csToken = new StBeanToken();
                    csToken.setUid(uid);
                    StBeanToken csTokenResult = tokenService.getToken(csToken);
                    if (csTokenResult != null) {
                        Timestamp atime = csTokenResult.getAtime();
                        if (atime.getTime() < System.currentTimeMillis()) {
                            log.warn(" token has expired");
                            throw new StUnauthorizedException();
                        }

                        if (token == null || !token.equals(csTokenResult.getToken())) {
                            throw new StUnauthorizedException();
                        }
                        request.setAttribute(API_COST_TRACK, new StRequestTimeModel());
                        request.setAttribute("uid", suid);
                        return true;
                    }
                }
            }
        }

        throw new StUnauthorizedException();
    }
}
