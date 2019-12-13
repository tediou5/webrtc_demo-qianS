package cn.teclub.ha3.api;

import cn.teclub.ha3.api.StAddContactRequest.RequestType;
import cn.teclub.ha3.request.StBasicClient;

/**
 * A message sent to client by server when applyAdd or grandAdd is processed.
 * It can be sent via websocket or other transport libraries(e.g. umeng.com)
 *
 * [Theodor: 2019/11/28]
 * rename from WsResponse --> AddContactMessage
 *
 * @author Tao Zhang
 */
public class StAddContactMessage {

    private StBasicClient sourceInfo;

    private RequestType type;

    private StBasicClient targetInfo;



    public void setType(RequestType type) {
        this.type = type;
    }

    public RequestType getType() {
        return type;
    }

    public void setSourceInfo(StBasicClient sourceInfo) {
        this.sourceInfo = sourceInfo;
    }

    public StBasicClient getSourceInfo() {
        return sourceInfo;
    }

    public StBasicClient getTargetInfo() {
        return targetInfo;
    }

    public void setTargetInfo(StBasicClient targetInfo) {
        this.targetInfo = targetInfo;
    }
}


