package cn.teclub.ha3.coco_server.network;

import cn.teclub.ha3.request.StWsMessage;

public interface StTransportService {
    void sendMessage(Long userId, StWsMessage message);
}
