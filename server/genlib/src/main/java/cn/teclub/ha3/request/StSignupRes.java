package cn.teclub.ha3.request;

import cn.teclub.ha3.utils.StErrCode;

public class StSignupRes extends StUserResponse
{
    public StSignupRes(StErrCode ec){
        super(ec);
    }

    public StSignupRes(){ }
}
