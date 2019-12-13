package cn.teclub.ha3.server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class StForbiddenException extends RuntimeException {
    public StForbiddenException() {
    }

    public StForbiddenException(String message) {
        super(message);
    }

    public StForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }

    public StForbiddenException(Throwable cause) {
        super(cause);
    }

    public StForbiddenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
