package cn.teclub.ha3.server.exceptions;

/**
 *
 */
public class StPacketHeadException extends RuntimeException {
    public StPacketHeadException() {
    }

    public StPacketHeadException(String message) {
        super(message);
    }

    public StPacketHeadException(String message, Throwable cause) {
        super(message, cause);
    }

    public StPacketHeadException(Throwable cause) {
        super(cause);
    }

    public StPacketHeadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
