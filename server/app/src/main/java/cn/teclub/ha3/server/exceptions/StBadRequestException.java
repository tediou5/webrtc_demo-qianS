package cn.teclub.ha3.server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StBadRequestException extends RuntimeException {
    public StBadRequestException() {
    }

    public StBadRequestException(String message) {
        super(message);
    }

    public StBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public StBadRequestException(Throwable cause) {
        super(cause);
    }

    public StBadRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
