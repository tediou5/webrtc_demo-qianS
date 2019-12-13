package cn.teclub.ha3.coco_server.controller.exception;

/**
 * @author zhangtao
 */
public class StServerError extends RuntimeException {
    public StServerError() {
    }

    public StServerError(String message) {
        super(message);
    }

    public StServerError(String message, Throwable cause) {
        super(message, cause);
    }

    public StServerError(Throwable cause) {
        super(cause);
    }

    public StServerError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
