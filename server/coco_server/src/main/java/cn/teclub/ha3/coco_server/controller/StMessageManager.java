package cn.teclub.ha3.coco_server.controller;

import cn.teclub.ha3.coco_server.controller.exception.StInternalErrorException;
import cn.teclub.ha3.coco_server.model.StModelException;
import cn.teclub.ha3.coco_server.model.StMessageService;
import cn.teclub.ha3.coco_server.network.StTransportService;
import cn.teclub.ha3.exception.FsExceptionBean;
import cn.teclub.ha3.request.StBasicClient;
import cn.teclub.ha3.request.StWsMessage;
import cn.teclub.ha3.utils.StObject;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * message manager
 *
 * [Theodor: 2019/11/28]
 * - cache message in RAM;
 * - save message into DB;
 * - get message from DB;
 *
 * - get rid of StBeanMessage
 *
 *
 * @author Tao Zhang, Guilin Cao
 */
@SuppressWarnings("WeakerAccess")
@Component
public class StMessageManager extends StObject {

    private static HashMap<Long, List<StWsMessage>> unsentMessageMap = new HashMap<>();

    @Autowired
    StMessageService messageService;

    @Autowired
    StClientManager clientManager;

    @Autowired
    StTransportService websocketService;


    @PostConstruct
    public void loadData(){

        /*
        StBeanMessage messageVo = new StBeanMessage();
        messageVo.setState(StBeanMessage.MessageState.UNSENT.ordinal());
        List<StBeanMessage> unsentMessages = messageService.selectByRecord(messageVo);
        */
        List<StWsMessage> unsentMessages = messageService.queryPendingMessages();
        for (StWsMessage message : unsentMessages){
            addUnsentMessage(message);
        }

    }

    public List<StWsMessage> getUnsentMessageMap(Long uid){
        return unsentMessageMap.get(uid) == null ? new ArrayList<>() : unsentMessageMap.get(uid);
    }


    /**
     * [Theodor: 2019/11/28]
     * TODO: cache StWsMessage, NOT beanMessage!
     *
     * @param message
     */
    public void addUnsentMessage(StWsMessage message){
        if(unsentMessageMap.containsKey(message.getDst())){
            unsentMessageMap.get(message.getDst()).add(message);
        }else {
            List messageList = new ArrayList<>();
            messageList.add(message);
            unsentMessageMap.put(message.getDst(),messageList);
        }
    }

    public void deleteSentMessages(long uid){
        log.debug("delete client {}  from map",uid);
        unsentMessageMap.remove(uid);
    }


    /**
     * send a message to remote client and save into DB.
     *
     * If the client is OFFLINE, cache it and sent it later;
     *
     */
    public void postMessage(StWsMessage.Command cmd, Object msg_body, StBasicClient from, StBasicClient to){
        StWsMessage wsMessage = new StWsMessage(0L , cmd,
                StWsMessage.Type.Request, JSON.toJSONString(msg_body),
                from.getId().getId(), to.getId().getId());

        final boolean sent;
        if(clientManager.isOnline(to.getId())) {
            websocketService.sendMessage(to.getId().getId(), wsMessage);
            sent = true;
            log.debug("sent message {}", wsMessage);
        } else {
            sent = false;
            log.debug("cache message {}", wsMessage);
        }

        // save message into DB
        try {
            final long id = messageService.save(wsMessage, sent);
            wsMessage.setSsid(id);
            if(!sent) {
                wsMessage.setSsid(id);
                addUnsentMessage(wsMessage);
            }

        } catch (StModelException e) {
            e.printStackTrace();
            log.warn("fail to insert message");
            throw new StInternalErrorException(FsExceptionBean.messageInsertError.getMessage(),
                    FsExceptionBean.messageInsertError.getErrorCode());
        }

    }

}
