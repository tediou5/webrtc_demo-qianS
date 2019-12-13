package cn.teclub.ha3.server.interceptor;

import cn.teclub.ha3.app.http.StHeaders;
import cn.teclub.ha3.server.exceptions.StUnauthorizedException;
import cn.teclub.ha3.server.model.StToken;
import cn.teclub.ha3.server.service.StTokenService;
import cn.teclub.ha3.server.sys.StSystemConstant;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;

/**
 * Interceptor to check request.
 */
@Component
public class StUserInterceptor implements HandlerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(StUserInterceptor.class);

    @Autowired
    private StTokenService tokenService;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse arg1, Object arg2, Exception arg3)
            throws Exception {
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object arg2, ModelAndView arg3)
            throws Exception {
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
        String token = request.getHeader(StHeaders.TOKEN);
        if (StringUtils.isNotEmpty(token)) {
            String decoded = new String(Base64.decodeBase64(token));
            if (StringUtils.isNotEmpty(decoded)) {
                String[] parts = decoded.split(StSystemConstant.TOKEN_SPLITTER);
                if (parts.length == 2) {
                    String suid = parts[0];
                    long uid = Long.parseLong(suid);

                    StToken csToken = new StToken();
                    csToken.setUid(uid);
                    StToken csTokenResult = tokenService.getToken(csToken);
                    if (csTokenResult != null) {
                        Timestamp atime = csTokenResult.getAtime();
                        if (atime.getTime() < System.currentTimeMillis()) {
                            LOGGER.warn(" token has expired");
                            throw new StUnauthorizedException();
                        }

                        if (token == null || !token.equals(csTokenResult.getToken())) {
                            throw new StUnauthorizedException();
                        }

                        request.setAttribute("uid", suid);
                        return true;
                    }
                }
            }
        }

        throw new StUnauthorizedException();
    }
}
