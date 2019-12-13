package cn.teclub.ha3.server.controller;

import cn.teclub.ha3.app.input.StAddDeviceRequest;
import cn.teclub.ha3.app.input.StDeviceInfoRequest;
import cn.teclub.ha3.app.output.StDeviceInfoResponse;
import cn.teclub.ha3.exception.FsExceptionBean;
import cn.teclub.ha3.server.exceptions.StBadRequestException;
import cn.teclub.ha3.server.exceptions.StForbiddenException;
import cn.teclub.ha3.server.exceptions.StInternalErrorException;
import cn.teclub.ha3.server.model.StClient;
import cn.teclub.ha3.server.model.StClientHas;
import cn.teclub.ha3.server.service.StClientHasService;
import cn.teclub.ha3.server.service.StClientService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("${rtc.api.prefix}/device")
public class StDeviceController {

private static final Logger LOGGER = LoggerFactory.getLogger(StDeviceController.class);

    @Autowired
    StClientHasService clientHasService;

    @Autowired
    StClientService clientService;

    @PostMapping("/add")
    public void add(@RequestBody StAddDeviceRequest deviceRequest, HttpServletRequest request){

        if(deviceRequest.getDid() == null || deviceRequest.getUid() == null){
            LOGGER.warn("request parameter is incorrect");
            throw new StBadRequestException();
        }

        String uidHeader = request.getAttribute("uid").toString();

        if (!uidHeader.equals(String.valueOf(deviceRequest.getUid()))) {
            LOGGER.warn("id not common ,forbidden");
            throw new StForbiddenException();
        }

        StClientHas clientHas = new StClientHas();
        clientHas.setCltA(deviceRequest.getUid());
        clientHas.setCltB(deviceRequest.getDid());

        StClientHas clientHasInDB = clientHasService.getClientHasByRecord(clientHas);

        if(clientHasInDB != null){
            LOGGER.debug("uid {} and did {} add successfully",deviceRequest.getUid(),deviceRequest.getDid());
            return;
        }

        if(clientHasService.saveOrUpdate(clientHas)>0){
            LOGGER.debug("uid {} and did {} add successfully",deviceRequest.getUid(),deviceRequest.getDid());
            return;
        }

        LOGGER.debug("device add failed");
        throw new StInternalErrorException(FsExceptionBean.deviceAddError.getMessage()
        ,FsExceptionBean.deviceAddError.getErrorCode());
    }
    @PostMapping("/info/{did}")
    public StDeviceInfoResponse info(HttpServletRequest request , @PathVariable("did") String did){
        if(did == null){
            LOGGER.warn("request parameter is incorrect");
            throw new StBadRequestException();
        }

        StClient clientInDB = clientService.getClientById(Long.valueOf(did));
        if(clientInDB==null){
            LOGGER.warn("device not exist");
            throw new StInternalErrorException(FsExceptionBean.userNotFound.getMessage()
                    , FsExceptionBean.userNotFound.getErrorCode());
        }
        StDeviceInfoResponse response = new StDeviceInfoResponse();
        response.setDid(clientInDB.getId());
        response.setName(clientInDB.getName());
        response.setLabel(clientInDB.getLabel());
        response.setDesp(clientInDB.getDscp());

        return  response;
    }

    @PostMapping("/edit")
    public void edit(@RequestBody StDeviceInfoRequest infoRequest,HttpServletRequest request){
        if(StringUtils.isEmpty(infoRequest.getLabel()) || infoRequest.getDid()==null){
            LOGGER.warn("request parameter is incorrect");
            throw new StBadRequestException();
        }

        StClient client = new StClient();
        client.setId(infoRequest.getDid());
        client.setLabel(infoRequest.getLabel());
        client.setDscp(infoRequest.getDesp());

        if(clientService.saveOrUpdate(client)>0){
            LOGGER.debug("device {} information updated successfully",client.getId());
            return;
        }

        LOGGER.warn("device information updated failed");
        throw new StInternalErrorException(FsExceptionBean.deviceInfoUpdateError.getMessage()
                , FsExceptionBean.deviceInfoUpdateError.getErrorCode());

    }

    @PostMapping("/delete")
    public void delete(@RequestBody StAddDeviceRequest deviceRequest,HttpServletRequest request){

        if(deviceRequest.getDid() == null || deviceRequest.getUid() == null){
            LOGGER.warn("request parameter is incorrect");
            throw new StBadRequestException();
        }

        String uidHeader = request.getAttribute("uid").toString();

        if (!uidHeader.equals(String.valueOf(deviceRequest.getUid()))) {
            LOGGER.warn("id not common ,forbidden");
            throw new StForbiddenException();
        }

        StClientHas clientHas = new StClientHas();
        clientHas.setCltA(deviceRequest.getUid());
        clientHas.setCltB(deviceRequest.getDid());

        if(clientHasService.getClientHasByRecord(clientHas) != null){
            if(clientHasService.deleteByRecord(clientHas)>0){
                LOGGER.debug("device delete successfully");
                return;
            }

            LOGGER.warn("device delete failed");
            throw new StInternalErrorException(FsExceptionBean.deviceDeleteError.getMessage()
                    , FsExceptionBean.deviceDeleteError.getErrorCode());
        }
        LOGGER.warn("user {} is not associated with this device {} ",deviceRequest.getUid(),deviceRequest.getDid());
        throw new StInternalErrorException(FsExceptionBean.deviceUserAssociateError.getMessage()
                , FsExceptionBean.deviceUserAssociateError.getErrorCode());
    }
}
