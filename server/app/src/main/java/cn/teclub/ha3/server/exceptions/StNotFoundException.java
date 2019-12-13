package cn.teclub.ha3.server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class StNotFoundException extends RuntimeException {
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
