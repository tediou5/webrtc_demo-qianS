package cn.teclub.ha3.coco_server.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author zhangtao
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class StNotFoundException extends StCtrlError {
    public StNotFoundException() {
    }

    public StNotFoundException(String message) {
        super(message);
    }

    public StNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public StNotFoundException(Throwable cause) {
        super(cause);
    }

    public StNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
