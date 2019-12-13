package cn.teclub.ha3.coco_server.controller;

import cn.teclub.ha3.api.*;
import cn.teclub.ha3.coco_server.controller.exception.StBadRequestException;
import cn.teclub.ha3.coco_server.controller.exception.StInternalErrorException;
import cn.teclub.ha3.coco_server.model.StModelException;
import cn.teclub.ha3.net.StClientID;
import cn.teclub.ha3.request.StAppClient;
import cn.teclub.ha3.request.StBasicClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * device restful api
 *
 * [Guilin: 2019/11/27]
 * (1) use new model service methods, get rid of database table beans;
 * (2) do checking in super class;
 * (3) calculate API cost in interceptor;
 *
 *
 *
 * @author Tao Zhang, Guilin Cao
 */
@RestController
@RequestMapping("${rtc.api.prefix}/device")
public class StDeviceController extends StControllerBase {


    @Autowired
    StClientManager clientManager;


    @Autowired
    StMessageManager messageManager;


    @PostMapping("/add")
    public void add(@RequestBody StAddAdminRequest addAdminRequest, HttpServletRequest request){
        validRequest(addAdminRequest, request);

        final StClientID  uid = new StClientID(addAdminRequest.getUid());
        final StBasicClient device = getDevice(addAdminRequest.getDid());

        try {
            clientService.addAdminRelation(uid, device.getId());
            log.info("add admin relation success: {} --> {}", uid, device);
        } catch (StModelException e) {
            e.printStackTrace();
            log.error(util.getExceptionDetails(e, "fail to add admin relation!"));
            throw new StInternalErrorException(e.getMessage(), e.errorCode);
        }
    }


    @PostMapping("/edit")
    public void edit(@RequestBody StDeviceInfoRequest infoRequest,HttpServletRequest request){
        validRequest(infoRequest, request);
        if(StringUtils.isEmpty(infoRequest.getLabel()) || infoRequest.getDid()==null){
            log.warn("request parameter is incorrect: " + infoRequest);
            throw new StBadRequestException();
        }

        StBasicClient client = new StBasicClient();
        client.setId(new StClientID(infoRequest.getDid()));
        client.setLabel(infoRequest.getLabel());
        client.setDscp(infoRequest.getDesp());

        try {
            clientService.updateClientInfo(client);
            log.info("edit device info success: {}", client );
        } catch (StModelException e) {
            e.printStackTrace();
            log.error(util.getExceptionDetails(e, "fail to add admin relation!"));
            throw new StInternalErrorException(e.getMessage(), e.errorCode);
        }
    }

    @PostMapping("/deleteContact")
    public void  deleteContact(@RequestBody StDeviceDeleteContactRequest deleteRequest, HttpServletRequest request){
        validRequest(deleteRequest, request);

        final StBasicClient device = getDevice(deleteRequest.getDid());
        final StBasicClient target = getClient(deleteRequest.getCid());
        clientService.deleteRelation(device.getId(), target.getId());
        log.info("delete relation success: {} --> {}", device ,target);
    }

    @PostMapping("/contacts")
    public StUserContactsResponse contacts(HttpServletRequest request,@RequestBody StDeviceRequest deviceRequest){

        validRequest(deviceRequest,request);
        StAppClient client =  clientService.loadClient(new StClientID(deviceRequest.getDid()));
        StUserContactsResponse response = new StUserContactsResponse();
        response.setContacts(client.getFriends());
        return  response;
    }
}
