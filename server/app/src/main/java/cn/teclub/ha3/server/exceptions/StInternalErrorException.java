package cn.teclub.ha3.server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class StInternalErrorException extends RuntimeException {
    public StInternalErrorException() {
    }

    private int errorCode = 0;

    public StInternalErrorException(String message) {
        super(message);
    }

    public StInternalErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public StInternalErrorException(Throwable cause) {
        super(cause);
    }

    public StInternalErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public StInternalErrorException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public StInternalErrorException(String message, Throwable cause, int errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public StInternalErrorException(Throwable cause, int errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public StInternalErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace
            , int errorCode) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}

