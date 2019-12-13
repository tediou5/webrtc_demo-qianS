package cn.teclub.ha3.server.interceptor;

import cn.teclub.ha3.server.sys.StApplicationProperties;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * Interceptor to collect API statistics.
 *
 * @author juewu
 */
@Component
public class StStatsInterceptor implements HandlerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(StStatsInterceptor.class);

    //@Autowired
    //FsUtil fsUtil;
    @Autowired
    StApplicationProperties fsApplicationProperties;
    //@Autowired
    //FsIStatsReporter fsIStatsReporter;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if ("/error".equals(request.getRequestURI())) {
            return;
        }

        try {
            MDC.put("fs.stats.http_status", String.valueOf(response.getStatus()));
            MDC.put("fs.stats.request_url", request.getRequestURL().toString());
            MDC.put("fs.stats.http_method", request.getMethod());

            MDC.put("fs.stats.version", request.getHeader("version"));
            MDC.put("fs.stats.client_ip", request.getHeader("x-forwarded-for"));


            long end = System.currentTimeMillis();
            //MDC.put("fs.stats.request_end", fsUtil.formatTimestamp(end));
            Long begin = Long.valueOf(MDC.get("fs.stats.request_begin"));
            //MDC.put("fs.stats.request_begin", fsUtil.formatTimestamp(begin));
            // Collect elapsed time of this request.
            MDC.put("fs.stats.request_cost", (end - begin) + " ms");
           // MDC.put("fs.stats.client_id", fsApplicationProperties.getClient());
            //MDC.put("fs.stats.server_nn", fsApplicationProperties.getNodeName());

            String statsJson = JSON.toJSON(MDC.getCopyOfContextMap()).toString();
            //fsIStatsReporter.report(statsJson);
        } finally {
            // clear MDC.
            MDC.clear();
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        MDC.put("51fs.stats.request_id", UUID.randomUUID().toString());
        MDC.put("fs.stats.request_begin", String.valueOf(System.currentTimeMillis()));
        return true;
    }
}
