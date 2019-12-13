package cn.teclub.ha3.coco_server.controller;

import cn.teclub.ha3.api.StDeviceRequest;
import cn.teclub.ha3.api.StRequestBody;
import cn.teclub.ha3.api.StSensitiveRequest;
import cn.teclub.ha3.coco_server.controller.exception.StForbiddenException;
import cn.teclub.ha3.coco_server.controller.exception.StInternalErrorException;
import cn.teclub.ha3.coco_server.util.StServerUtil;
import cn.teclub.ha3.coco_server.model.StClientService;
import cn.teclub.ha3.exception.FsExceptionBean;
import cn.teclub.ha3.net.StClientID;
import cn.teclub.ha3.net.StClientType;
import cn.teclub.ha3.request.StAppFriend;
import cn.teclub.ha3.request.StBasicClient;
import cn.teclub.ha3.utils.StObject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;


/**
 * base controller in server
 *
 * @author Guilin Cao
 */
abstract class StControllerBase extends StObject {

    @Autowired
    StClientService clientService;


    @Autowired
    StServerUtil serverUtil;


    /**
     * Validate UID with token saved in http-request by interceptor
     *
     * Used by RESET API which has NOT sensitive request body.
     */
    void validRequestUid(long raw_id, HttpServletRequest request){
        final String id_str = (String) request.getAttribute("uid");
        if(id_str == null) {
            log.warn("NO TOKEN ID in HTTP Request");
            return;
        }
        final StClientID token_uid = new StClientID(Long.valueOf(id_str));
        if(!token_uid.valid()) {
            log.error("token ID is invalid: {} ", token_uid);
            return;
        }

        final StClientID uid = new StClientID(raw_id);
        clientService.hasClient(uid);
        if(!token_uid.equalWith(uid)) {
            log.warn("API is Forbidden! UID {} does not equal to token ID{}!", uid, token_uid );
            throw new StForbiddenException();
        }
    }


    /**
     * valid right for a sensitive/device request.
     *
     * For a sensitive request:         1) UID client exist; 2) token ID is same to UID;
     * For a device management request: 1) DID client exist; 2) UID is same to device admin ID;
     *
     */
    void validRequest(StRequestBody body, HttpServletRequest request){
        final String id_str = (String) request.getAttribute("uid");
        if(id_str == null) {
            log.warn("NO TOKEN ID in HTTP Request");
            return;
        }
        final StClientID token_uid = new StClientID(Long.valueOf(id_str));
        if(!token_uid.valid()) {
            log.error("token ID is invalid: {} ", token_uid);
            return;
        }

        if(body instanceof StSensitiveRequest){
            StSensitiveRequest req = (StSensitiveRequest )body;
            final StClientID uid = new StClientID(req.getUid());
            clientService.hasClient(uid);
            if(! token_uid.equalWith(uid)) {
                log.warn("API is Forbidden! UID {} does not equal to token ID{}!", uid, token_uid );
                throw new StForbiddenException();
            }

            if(body instanceof StDeviceRequest ){
                final StClientID    did = new StClientID(((StDeviceRequest)req).getDid());
                final StBasicClient admin = getDeviceAdmin(did.getId());
                if(! uid.equalWith(admin.getId())){
                    log.warn("UID {} != admin {} ,forbidden", uid, admin);
                    throw new StForbiddenException();
                }
            }
        }
    }



    @SuppressWarnings("unused")
    StBasicClient getClient(long raw_id) {
        final StClientID id = new StClientID(raw_id);
        final StAppFriend f = clientService.getContact(id);
        if(f == null){
            log.warn("fail to find device by ID: " + id);
            throw new StInternalErrorException(FsExceptionBean.notFoundDevice.getMessage(),
                    FsExceptionBean.notFoundDevice.getErrorCode());
        }
        return f;
   }





    StBasicClient getDevice(long did) {
        final StClientID device_id = new StClientID(did);

        final StAppFriend device = clientService.getContact(device_id);
        if(device == null){
            log.warn("fail to find device by ID: " + device_id);
            throw new StInternalErrorException(FsExceptionBean.notFoundDevice.getMessage(),
                    FsExceptionBean.notFoundDevice.getErrorCode());
        }
        if(device.getFlag_ClientType() != StClientType.GATEWAY) {
        log.warn("client is NOT a device: " + device );
        throw new StInternalErrorException(FsExceptionBean.notIsDevice.getMessage(),
                FsExceptionBean.notIsDevice.getErrorCode());
        }
        return device;
    }


    StBasicClient getDeviceAdmin(long did){
        final StClientID id = new StClientID(did);
        if(!clientService.hasClient(id)){
            log.warn("fail to find device by ID: " + id);
            throw new StInternalErrorException(FsExceptionBean.userNotFound.getMessage(), FsExceptionBean.userNotFound.getErrorCode());
        }

        final StAppFriend admin = clientService.getDeviceAdmin(id);
        if(admin == null) {
            log.warn("device has no an admin: " + id );
            throw new StInternalErrorException(FsExceptionBean.adminNotExists.getMessage(),
                    FsExceptionBean.adminNotExists.getErrorCode());
        }

        return admin;
    }

}
