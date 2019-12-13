package cn.teclub.ha3.coco_server.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author zhangtao
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class StUnauthorizedException extends StCtrlError {
    public StUnauthorizedException() {
    }

    public StUnauthorizedException(String message) {
        super(message);
    }

    public StUnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public StUnauthorizedException(Throwable cause) {
        super(cause);
    }

    public StUnauthorizedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

