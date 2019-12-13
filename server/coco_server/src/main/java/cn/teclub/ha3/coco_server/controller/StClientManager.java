package cn.teclub.ha3.coco_server.controller;

import cn.teclub.ha3.coco_server.controller.exception.StException;
import cn.teclub.ha3.coco_server.controller.exception.StInternalErrorException;
import cn.teclub.ha3.coco_server.network.StServicesProvider;
import cn.teclub.ha3.coco_server.sys.StApplicationProperties;
import cn.teclub.ha3.coco_server.sys.StSystemConstant;
import cn.teclub.ha3.coco_server.util.StServerUtil;
import cn.teclub.ha3.coco_server.model.*;
import cn.teclub.ha3.coco_server.model.dao.StBeanAuthcode;
import cn.teclub.ha3.coco_server.model.dao.StBeanMessage;
import cn.teclub.ha3.coco_server.network.StTransportService;
import cn.teclub.ha3.exception.FsExceptionBean;
import cn.teclub.ha3.net.StClientID;
import cn.teclub.ha3.request.StWsMessage;
import cn.teclub.ha3.utils.StObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.*;

/**
 * client manager
 * @author Tao Zhang
 */
@Component
public class StClientManager extends StObject {

    private static StClientManager   _ins = new StClientManager();

    public static StClientManager instance(){
        return _ins;
    }

    private static LinkedHashMap<Long,Long> clientIsOnlineMap = new LinkedHashMap<>(16, 0.75f, true);

    private static HashMap<String,StBeanAuthcode> authMap = new HashMap<>();

    @Autowired
    StMessageService stMessageService;

    @Autowired
    StTransportService stWebsocketService;

    @Autowired
    StMessageManager messageManager;

    @Autowired
    StServerUtil serverUtil;

    @Autowired
    StAuthcodeService authcodeService;

    @Resource
    private StApplicationProperties applicationProperties;


    public void generateAuth(String phone){
        // [Theodor: 2019/11/28]
        String acode = serverUtil.random(100000, 999999);
        long current = System.currentTimeMillis();
        Timestamp atime = new Timestamp(current + Long.valueOf(applicationProperties.getAuthCodeExpired()));
        StBeanAuthcode authcode = new StBeanAuthcode();
        authcode.setAtime(atime);
        authcode.setAuthcode(acode);
        authMap.put(phone,authcode);
        try {
            StServicesProvider.getInstance(applicationProperties).getSMSSender(applicationProperties.getAuthCodeSmssender()).sendSMS(phone, acode);
        } catch (StException e) {
            log.warn("Failed to send message '{}'.", acode);
        }
    }


    public boolean verifyAuth(String phone, String code) {
        if(authMap.containsKey(phone)){
            StBeanAuthcode authcodeInMap = authMap.get(phone);

            if(!authcodeInMap.getAuthcode().equals(code)){
                log.warn(" authCode not equal ");
               return false;
            }
            if (authcodeInMap.getAtime().getTime() < System.currentTimeMillis()) {
                log.warn("authCode has expired");
               return false;
            }

            return true;
        }

        log.warn("authCode not send");
        return false;
    }


    public void deleteAuth(String phone){
        authMap.remove(phone);
    }



    public void clientIsOnlineListener(long uid){

        long now_time =System.currentTimeMillis();
        clientIsOnlineMap.put(uid,now_time);
        log.debug("client {} insert into map ,now time {}",uid,now_time);

        List<StWsMessage> stMessagesInCache = messageManager.getUnsentMessageMap(uid);
        if(stMessagesInCache.size()!=0){
            for (StWsMessage message : stMessagesInCache) {
                // [Theodor: 2019/11/29] TODO: save 'type'
                message.setType(StWsMessage.Type.Request);
                stWebsocketService.sendMessage(message.getDst(), message);
                if(!stMessageService.update(message, true)) {
                    log.warn("fail to update message");
                    throw new StInternalErrorException(FsExceptionBean.messageInsertError.getMessage(),
                            FsExceptionBean.messageInsertError.getErrorCode());
                }
                log.debug("message insert successfully");
            }
            messageManager.deleteSentMessages(uid);
        }
        Iterator<Map.Entry<Long,Long>> iterator = clientIsOnlineMap.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            if(now_time - StSystemConstant.IS_ONLINE_TIME <= (long)entry.getValue()){
                break;
            }else {
                iterator.remove();
            }
        }

    }

    public LinkedHashMap<Long,Long> getClientIsOnlineMap(){
        return clientIsOnlineMap;
    }


    /**
     *  [Theodor: 2019/11/28] check if a client is ONLINE
     *
     */
    boolean isOnline(StClientID id){
        return clientIsOnlineMap.containsKey(id.getId());
    }

}
