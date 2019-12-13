package cn.teclub.ha3.coco_server.sys;

import cn.teclub.ha3.coco_server.controller.exception.StInternalErrorException;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

/**
 * An ErrorAttributes to append more customized fields.
 * @author Tao Zhang
 */
public class StServerErrorAttributes extends DefaultErrorAttributes {
    private static final String ERROR_CODE = "errorCode";

    public StServerErrorAttributes() {
        super();
    }

    public StServerErrorAttributes(boolean includeException) {
        super(includeException);
    }

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, includeStackTrace);

        // Customize the response body.
        Throwable error = getError(webRequest);
        if (error instanceof StInternalErrorException) {
            StInternalErrorException exception = (StInternalErrorException) error;
            errorAttributes.put(ERROR_CODE, exception.getErrorCode());
        }
        return errorAttributes;
    }
}
