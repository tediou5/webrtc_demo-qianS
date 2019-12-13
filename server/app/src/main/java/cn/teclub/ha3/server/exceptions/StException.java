package cn.teclub.ha3.server.exceptions;

/**
 * Base exception for the services.
 */
public class StException extends Exception {
    public StException() {
    }

    public StException(String message) {
        super(message);
    }

    public StException(String message, Throwable cause) {
        super(message, cause);
    }

    public StException(Throwable cause) {
        super(cause);
    }

    public StException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
