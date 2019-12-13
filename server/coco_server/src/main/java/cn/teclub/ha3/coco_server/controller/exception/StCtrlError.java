package cn.teclub.ha3.coco_server.controller.exception;

/**
 * @author zhangtao
 */
public class StCtrlError extends StServerError {

    public StCtrlError() {
    }

    public StCtrlError(String message) {
        super(message);
    }

    public StCtrlError(String message, Throwable cause) {
        super(message, cause);
    }

    public StCtrlError(Throwable cause) {
        super(cause);
    }

    public StCtrlError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
