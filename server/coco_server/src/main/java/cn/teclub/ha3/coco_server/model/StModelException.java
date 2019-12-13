package cn.teclub.ha3.coco_server.model;

import cn.teclub.ha3.coco_server.controller.exception.StException;


/**
 * exception in model layer
 *
 * @author Guilin Cao
 */
public class StModelException extends StException {
    public final int errorCode;

    public StModelException() {
        this("<null>", 0);
    }

    public StModelException(String message) {
        this(message, 0);
    }

    public StModelException(String message, final int errorCode){
        super(message);
        this.errorCode = errorCode;
    }


}
