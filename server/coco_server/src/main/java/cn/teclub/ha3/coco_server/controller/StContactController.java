package cn.teclub.ha3.coco_server.controller;

import cn.teclub.ha3.api.*;
import cn.teclub.ha3.coco_server.controller.exception.StBadRequestException;
import cn.teclub.ha3.coco_server.controller.exception.StForbiddenException;
import cn.teclub.ha3.coco_server.controller.exception.StInternalErrorException;
import cn.teclub.ha3.coco_server.model.StModelException;
import cn.teclub.ha3.exception.FsExceptionBean;
import cn.teclub.ha3.net.StClientID;
import cn.teclub.ha3.net.StClientType;
import cn.teclub.ha3.request.StAppClient;
import cn.teclub.ha3.request.StAppFriend;
import cn.teclub.ha3.request.StBasicClient;
import cn.teclub.ha3.request.StWsMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * contact restful api
 *
 * [2019/12/2]
 * (1) Move these methods from StDeviceController;
 * (2) no need to distinguish between device and user
 * @author Tao Zhang
 */
@RestController
@RequestMapping("${rtc.api.prefix}/contact")
public class StContactController extends StControllerBase{


    @Autowired
    StMessageManager messageManager;

    @PostMapping("/info/{did}")
    public StDeviceInfoResponse info(HttpServletRequest request , @PathVariable("did") String did){
        if(did == null){
            log.warn("request parameter is incorrect");
            throw new StBadRequestException();
        }
        final StBasicClient clientInDB = getClient(Long.valueOf(did));

        StDeviceInfoResponse response = new StDeviceInfoResponse();
        response.setDid(clientInDB.getId().getId());
        response.setName(clientInDB.getName());
        response.setLabel(clientInDB.getLabel());
        response.setDesp(clientInDB.getDscp());

        log.info("get device info success: {}", clientInDB);
        return  response;
    }


    @PostMapping("/search")
    public StUserContactsResponse search(@RequestBody StSearchRequest keywordRequest){

        if(keywordRequest.getKeyword() == null || keywordRequest.getPage() == null
                || keywordRequest.getSize() ==null){
            log.warn("request parameter error");
            throw new StBadRequestException();
        }

        StUserContactsResponse response = new StUserContactsResponse();
        response.setContacts(clientService.searchContactBy(keywordRequest.getKeyword(),keywordRequest.getPage(),keywordRequest.getSize()));

        log.info("search success by keyword: {}", keywordRequest.getKeyword());
        return  response;
    }


    @PostMapping("/delete")
    public void delete(@RequestBody StDeleteContactRequest contactRequest, HttpServletRequest request){
        validRequest(contactRequest, request);

        final StBasicClient sClient = getClient(contactRequest.getUid());
        final StBasicClient tClient = getClient(contactRequest.getCid());
        clientService.deleteRelation(sClient.getId(), tClient.getId());
        log.info("delete relation success: {} --> {}", sClient, tClient);

    }




    @PostMapping("/applyAdd")
    public void applyAdd(HttpServletRequest request , @RequestBody StAddContactRequest applyRequst){
        validRequest(applyRequst, request);
        processAddContact(applyRequst, StWsMessage.Command.APPLY_ADD_DEVICE);

    }

    @PostMapping("/grantAdd")
    @Transactional
    public void grantAdd(@RequestBody StAddContactRequest grantAddRequest, HttpServletRequest request ){
        validRequest(grantAddRequest, request);
        processAddContact(grantAddRequest,StWsMessage.Command.GRANT_ADD_DEVICE);
    }



    private void processAddContact(StAddContactRequest grantAddRequest, StWsMessage.Command cmd) {
        // ERROR checking
        if(grantAddRequest.getTid()==null || grantAddRequest.getSid()==null || grantAddRequest.getType()==null){
            log.warn("request parameter error");
            throw new StBadRequestException();
        }

        StAppFriend verifyClient = getMessageHandler(grantAddRequest.getSid());
        StClientID uid = new StClientID(grantAddRequest.getUid());
        if(!uid.equalWith(verifyClient.getId())){
            log.warn("UID {}, is NOT message handler {}, Forbidden", uid, verifyClient);
            throw new StForbiddenException();
        }

        final StClientID sid = new StClientID(grantAddRequest.getSid());
        final StClientID tid = new StClientID(grantAddRequest.getTid());
        final StAppClient source = clientService.loadClient(sid);
        final StAppClient target = clientService.loadClient(tid);
        if(clientService.hasRelation(sid, tid)){
            log.warn("both clients are already friends");
            throw new StInternalErrorException(FsExceptionBean.isAlreadyFriends.getMessage()
                    ,FsExceptionBean.isAlreadyFriends.getErrorCode());
        }
        if(source == null){
            log.warn("not find source client by id: " + sid);
            throw new StInternalErrorException(FsExceptionBean.notFoundDevice.getMessage(),
                    FsExceptionBean.notFoundDevice.getErrorCode());
        }

        if(target == null){
            log.warn("not find target client by id: " + tid);
            throw new StInternalErrorException(FsExceptionBean.notFoundDevice.getMessage(),
                    FsExceptionBean.notFoundDevice.getErrorCode());
        }


        // MAIN PROCESS
        StAddContactMessage msg = new StAddContactMessage();
        msg.setSourceInfo(source);
        msg.setTargetInfo(target);
        msg.setType(grantAddRequest.getType());

        if(StClientType.USER == target.getFlag_ClientType()) {
            messageManager.postMessage(cmd, msg, source, target);
        }else if(StClientType.GATEWAY == target.getFlag_ClientType()) {
            final StBasicClient admin = getDeviceAdmin(tid.getId());
            messageManager.postMessage(cmd, msg, source, admin);
        }else {
            log.warn("unknown target type: {}", target);
        }

        if (grantAddRequest.getType() == StAddContactRequest.RequestType.APPROVE){
            try {
                clientService.addRelation(sid, tid);
                log.info("add relation success: {} <-> {}", source, target);
            } catch (StModelException e) {
                e.printStackTrace();
                log.error(util.getExceptionDetails(e, "fail to add relation"));
            }
        }
    }


    /**
     * message handler (sender & receiver) is
     *  - a current client, if ID is a user;
     *  - its admin, if ID is a device;
     *
     */
    private StAppFriend getMessageHandler(long raw_id) {
        final StClientID id = new StClientID(raw_id);
        StAppFriend clientInDB  = clientService.getContact(id);
        if(clientInDB==null){
            log.warn("client in dataBase not found");
            throw new StInternalErrorException(FsExceptionBean.userNotFound.getMessage(), FsExceptionBean.userNotFound.getErrorCode());
        }
        if(clientInDB.getFlag_ClientType()==StClientType.USER){
            return clientInDB;
        }else {
            StAppFriend appFriend = clientService.getDeviceAdmin(id);
            if(appFriend == null) {
                log.warn("this device has no an administrator");
                throw new StInternalErrorException(FsExceptionBean.adminNotExists.getMessage(),
                        FsExceptionBean.adminNotExists.getErrorCode());
            }
            return appFriend;

        }
    }

}
