package cn.teclub.ha3.coco_server.network.websocket;

import cn.teclub.ha3.coco_server.network.StTransportService;
import cn.teclub.ha3.request.StWsMessage;
import cn.teclub.ha3.utils.StObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class StWebsocketServiceImpl extends StObject implements StTransportService {

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void sendMessage(Long userId, StWsMessage message) {
        log.info("start to send message to {}",userId);
        simpMessagingTemplate.convertAndSend("/u/"+userId,message);
    }
}
