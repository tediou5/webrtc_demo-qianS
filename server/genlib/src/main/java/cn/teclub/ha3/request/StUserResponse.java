package cn.teclub.ha3.request;

import cn.teclub.ha3.utils.StErrCode;
import cn.teclub.ha3.utils.StObject;


/**
 * super class for all user response
 */
public abstract class StUserResponse extends StObject
{
    private StErrCode errcode = StErrCode.SUCCESS;

    public StUserResponse() {
    }

    public StUserResponse(StErrCode ec) {
        this.errcode = ec;
    }


    public StErrCode getErrcode() {
        return errcode;
    }

    public void setErrcode(StErrCode errcode) {
        this.errcode = errcode;
    }
}
