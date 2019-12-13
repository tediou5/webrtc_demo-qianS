package cn.teclub.ha3.server.controller;

import cn.teclub.ha3.app.inner.StContact;
import cn.teclub.ha3.app.input.*;
import cn.teclub.ha3.app.output.StLoginResponse;
import cn.teclub.ha3.app.output.StUserContactsResponse;
import cn.teclub.ha3.app.output.StUserInfoResponse;
import cn.teclub.ha3.exception.FsExceptionBean;
import cn.teclub.ha3.net.StClientID;
import cn.teclub.ha3.net.StClientType;
import cn.teclub.ha3.request.StAppClient;
import cn.teclub.ha3.request.StAppFriend;
import cn.teclub.ha3.server.common.StServicesProvider;
import cn.teclub.ha3.server.common.StUtil;
import cn.teclub.ha3.server.exceptions.StBadRequestException;
import cn.teclub.ha3.server.exceptions.StForbiddenException;
import cn.teclub.ha3.server.exceptions.StInternalErrorException;
import cn.teclub.ha3.server.model.StAuthcode;
import cn.teclub.ha3.server.model.StClient;
import cn.teclub.ha3.server.model.StClientHas;
import cn.teclub.ha3.server.model.StToken;
import cn.teclub.ha3.server.service.StAuthcodeService;
import cn.teclub.ha3.server.service.StClientHasService;
import cn.teclub.ha3.server.service.StClientService;
import cn.teclub.ha3.server.service.StTokenService;
import cn.teclub.ha3.server.sys.StApplicationProperties;
import cn.teclub.ha3.server.sys.StSystemConstant;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * customer restful api and web backstage api
 */
@RestController
@RequestMapping("${rtc.api.prefix}/user")
public class StClientController {

    @Autowired
    StClientService clientService;

    @Autowired
    StAuthcodeService authcodeService;

    @Autowired
    StTokenService tokenService;

    @Autowired
    StApplicationProperties applicationProperties;

    @Autowired
    StClientHasService clientHasService;

    @Autowired
    StUtil stUtil;

    private static final Logger LOGGER = LoggerFactory.getLogger(StClientController.class);


    @PostMapping("/signin")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public StLoginResponse signin(@RequestBody StSigninRequest request) {

        if (StringUtils.isEmpty(request.getAuthCode()) || StringUtils.isEmpty(request.getPhone())) {
            LOGGER.warn("request error");
            throw new StBadRequestException();
        }

        StClient client = new StClient();
        client.setPhone(request.getPhone());

        StClient clientInDB = clientService.getClient(client);
        if (clientInDB == null) {
            StAuthcode authCode = new StAuthcode();
            authCode.setAuthcode(request.getAuthCode());
            StAuthcode authCodeInDB = authcodeService.getAuthcode(authCode);
            if (authCodeInDB != null) {
                if (authCodeInDB.getAtime().getTime() < System.currentTimeMillis()) {
                    LOGGER.warn("authCode has expired");
                    throw new StInternalErrorException(FsExceptionBean.authCodeExpired.getMessage(), FsExceptionBean.authCodeExpired.getErrorCode());
                }
                if(!authCodeInDB.getMobile().equals(request.getPhone())){
                    LOGGER.warn("phone number and  authCode do not match ");
                    throw new StInternalErrorException(FsExceptionBean.authCodeAndPhoneNotMatch.getMessage(), FsExceptionBean.authCodeAndPhoneNotMatch.getErrorCode());
                }
                StClient stClient = new StClient(StClientType.USER);

                stClient.setName(request.getPhone());
                stClient.setLabel(request.getPhone());
                stClient.setPasswd("123456");
                stClient.setDscp("client");
                stClient.setPhone(request.getPhone());
                stClient.setIconTS(1l);
                stClient.setCreateTime(new Date());
                stClient.setLastLogin(new Date());
                stClient.setLastLogoff(new Date());
                stClient.setOnlineTime(2);
                stClient.setPublicIP("0.0.0.0");

                clientService.saveOrUpdate(stClient);
                LOGGER.debug(" client signin successfully");

                authCodeInDB.setAtime(new Date());
                authcodeService.saveOrUpdate(authCodeInDB);
                return renewResponse(stClient);


            }
            LOGGER.warn("authCode not send");
            throw new StInternalErrorException(FsExceptionBean.authCodeNotFound.getMessage(), FsExceptionBean.authCodeNotFound.getErrorCode());
        }
        LOGGER.warn("this client has already registered");
        throw new StInternalErrorException(FsExceptionBean.userSigined.getMessage(), FsExceptionBean.userSigined.getErrorCode());
    }

    @PostMapping("/login/name")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public StLoginResponse loginWithName(HttpServletRequest request, @RequestBody StLoginWNameRequest loginRequest) throws Exception {

        if (StringUtils.isEmpty(loginRequest.getName()) || StringUtils.isEmpty(loginRequest.getPasswd())) {
            LOGGER.warn("request error");
            throw new StBadRequestException();
        }

        StClient stClient = new StClient();
        if (loginRequest.getType().equals(StSystemConstant.CLIENT_PHONE_LOGIN_TYPE)) {
            stClient.setPhone(loginRequest.getName());
        } else if (loginRequest.getType().equals(StSystemConstant.CLIENT_NAME_LOGIN_TYPE)) {
            stClient.setName(loginRequest.getName());
        } else {
            stClient.setMacAddr(loginRequest.getName());
        }
        //stClient.setPasswd(loginRequest.getPasswd());

        StClient clientInDataBase = clientService.getClient(stClient);
        if (loginRequest.getType().equals(StSystemConstant.CLIENT_MAC_ADDR_LOGIN_TYPE)) {
            if (clientInDataBase == null) {
                StClient client = new StClient(StClientType.GATEWAY);
                client.setName(loginRequest.getName());
                client.setMacAddr(loginRequest.getName());
                client.setPasswd(loginRequest.getPasswd());
                client.setDscp("client");
                client.setIconTS(1l);
                client.setCreateTime(new Date());
                client.setLastLogin(new Date());
                client.setLastLogoff(new Date());
                client.setOnlineTime(2);
                client.setPublicIP("0.0.0.0");
                try{
                    clientService.saveOrUpdate(client);
                }catch (Exception e){
                    e.printStackTrace();
                    LOGGER.warn("mac address fail to signin");
                    throw new  StInternalErrorException(FsExceptionBean.userSiginedError.getMessage(),
                            FsExceptionBean.userSiginedError.getErrorCode());
                }
                LOGGER.debug("{} mac address insert successfully", loginRequest.getName());
                StClient clientInDB = clientService.getClientById(client.getClientID().getId());
                clientInDB.setName(StSystemConstant.GW + client.getClientID().getId());
                clientInDB.setLabel(StSystemConstant.GW + client.getClientID().getId());

                clientService.saveOrUpdate(clientInDB);
                return renewResponse(clientInDB);

            }
        }

        if (clientInDataBase != null) {
            if(!clientInDataBase.getPasswd().equals(loginRequest.getPasswd())){
                LOGGER.warn("password not common");
                throw new StInternalErrorException(FsExceptionBean.passwdWrong.getMessage(), FsExceptionBean.passwdWrong.getErrorCode());
            }
            return renewResponse(clientInDataBase);
        }
        LOGGER.warn("client in dataBase not found");
        throw new StInternalErrorException(FsExceptionBean.userNotFound.getMessage(), FsExceptionBean.userNotFound.getErrorCode());
    }

    @PostMapping("/login/phone")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public StLoginResponse loginWithPhone(HttpServletRequest request, @RequestBody StLoginWPhoneRequest loginRequest) throws Exception {

        if (StringUtils.isEmpty(loginRequest.getPhone()) || StringUtils.isEmpty(loginRequest.getAuthCode())) {
            LOGGER.warn("request error");
            throw new StBadRequestException();
        }
        StAuthcode authCode = new StAuthcode();
        authCode.setAuthcode(loginRequest.getAuthCode());
        StAuthcode authCodeInDB = authcodeService.getAuthcode(authCode);
        if (authCodeInDB != null) {
            if (authCodeInDB.getAtime().getTime() < System.currentTimeMillis()) {
                LOGGER.warn("authCode has expired");
                throw new StInternalErrorException(FsExceptionBean.authCodeExpired.getMessage(), FsExceptionBean.authCodeExpired.getErrorCode());
            }
            if(!authCodeInDB.getMobile().equals(loginRequest.getPhone())){
                LOGGER.warn("phone number and  authCode do not match ");
                throw new StInternalErrorException(FsExceptionBean.authCodeAndPhoneNotMatch.getMessage(), FsExceptionBean.authCodeAndPhoneNotMatch.getErrorCode());
            }
            StClient client = new StClient();
            client.setPhone(loginRequest.getPhone());

            StClient clientInDB = clientService.getClient(client);
            if (clientInDB != null) {

                authCodeInDB.setAtime(new Date());
                authcodeService.saveOrUpdate(authCodeInDB);
                return renewResponse(clientInDB);
            }
            LOGGER.warn("user in dataBase not found");
            throw new StInternalErrorException(FsExceptionBean.userNotFound.getMessage(), FsExceptionBean.userNotFound.getErrorCode());

        }
        LOGGER.warn("authCode not send");
        throw new StInternalErrorException(FsExceptionBean.authCodeNotFound.getMessage(), FsExceptionBean.authCodeNotFound.getErrorCode());
    }

    @PostMapping("/signout")
    @Transactional
    public void signout(HttpServletRequest request, @RequestBody StSiginoutRequest siginoutRequest) {
        if (StringUtils.isEmpty(siginoutRequest.getPasswd()) || siginoutRequest.getUid() == null) {
            LOGGER.warn("request parameter error");
            throw new StBadRequestException();
        }
        String uidHeader = request.getAttribute("uid").toString();

        if (!uidHeader.equals(String.valueOf(siginoutRequest.getUid()))) {
            LOGGER.warn("id not common ,forbidden");
            throw new StForbiddenException();
        }

        StClient client = new StClient();
        client.setClientID(new StClientID(siginoutRequest.getUid()));
        client.setPasswd(siginoutRequest.getPasswd());

        StClient clientInDB = clientService.getClient(client);
        if (clientInDB == null) {
            LOGGER.warn("the password you entered is incorrect.");
            throw new StInternalErrorException(FsExceptionBean.passwdWrong.getMessage(), FsExceptionBean.passwdWrong.getErrorCode());
        }

        int deleteCounts = clientHasService.deleteByUid(clientInDB.getClientID().getId());
        LOGGER.debug(" The number of deleted  client contacts is {}",deleteCounts);

        int counts = clientHasService.deleteByDid(clientInDB.getClientID().getId());
        LOGGER.debug(" The number of deleted  client contacts is {}",counts);

        if(clientService.deleteById(clientInDB.getClientID().getId())>0){
            return;
        }
        LOGGER.warn("fail to delete client");
        throw new StInternalErrorException(FsExceptionBean.userDeleteError.getMessage(), FsExceptionBean.userDeleteError.getErrorCode());

    }

    @GetMapping("/info/{uid}")
    public StUserInfoResponse info(@PathVariable("uid") Long uid, HttpServletRequest request) {
        String uidHeader = request.getAttribute("uid").toString();
        if (!uidHeader.equals(String.valueOf(uid))) {
            LOGGER.warn("id not common ,forbidden");
            throw new StForbiddenException();
        }
        StClient clientInDB = clientService.getClientById(uid);
        if (clientInDB == null) {
            LOGGER.warn("user not exist");
            throw new StInternalErrorException(FsExceptionBean.userNotFound.getMessage(), FsExceptionBean.userNotFound.getErrorCode());
        }
        StUserInfoResponse response = new StUserInfoResponse();
        response.setName(clientInDB.getName());
        response.setBirthday(clientInDB.getBirthday() == null ? null : stUtil.formatDate(clientInDB.getBirthday()));
        response.setLabel(clientInDB.getLabel());
        response.setAvatar(clientInDB.getAvatar());
        response.setPhone(clientInDB.getPhone());

        LOGGER.debug("get user info successfully");
        return response;

    }

    @PutMapping("/info")
    public void updateInfo(@RequestBody StUserInfoRequest userInfoRequest, HttpServletRequest request) throws ParseException {
        String uidHeader = request.getAttribute("uid").toString();

        if (!uidHeader.equals(String.valueOf(userInfoRequest.getUid()))) {
            LOGGER.warn("id not common , forbidden");
            throw new StForbiddenException();
        }

        StClient clientInDB = clientService.getClientById(userInfoRequest.getUid());
        if (clientInDB == null) {
            LOGGER.warn("user not exist");
            throw new StInternalErrorException(FsExceptionBean.userNotFound.getMessage()
                    , FsExceptionBean.userNotFound.getErrorCode());
        }
        if (userInfoRequest.getName() != null) {
            if (!clientInDB.getName().equals(clientInDB.getPhone())) {
                LOGGER.warn("user name can only be modified once");
                throw new StInternalErrorException(FsExceptionBean.nameModifyError.getMessage()
                        , FsExceptionBean.nameModifyError.getErrorCode());
            }
            clientInDB.setName(userInfoRequest.getName());
        }

        clientInDB.setBirthday(stUtil.parseString(userInfoRequest.getBirthday()));
        clientInDB.setPhone(userInfoRequest.getPhone());
        clientInDB.setLabel(userInfoRequest.getLabel());

        try {
            clientService.saveOrUpdate(clientInDB);
            LOGGER.debug("update user successfully");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.warn("update user failed,username or phone already exists ");
            throw new StInternalErrorException(FsExceptionBean.usernameOrPhoneAlreadyExistsError.getMessage()
                    , FsExceptionBean.usernameOrPhoneAlreadyExistsError.getErrorCode());
        }

    }


    @PutMapping("/passwd")
    public void updatePasswd(@RequestBody StPasswdRequest passwdRequest, HttpServletRequest request) {

        if (StringUtils.isEmpty(passwdRequest.getPasswd()) || StringUtils.isEmpty(passwdRequest.getNewPasswd())) {
            LOGGER.warn("request parameter error");
            throw new StBadRequestException();
        }
        String uidHeader = request.getAttribute("uid").toString();
        if (!passwdRequest.getUid().equals(Long.valueOf(uidHeader))) {
            LOGGER.warn("fail to update password , id not common , forbidden");
            throw new StForbiddenException();
        }
        StClient clientInDB = clientService.getClientById(passwdRequest.getUid());
        if (clientInDB != null) {
            if (passwdRequest.getPasswd().equals(clientInDB.getPasswd())) {
                StClient client = new StClient();
                client.setId(passwdRequest.getUid());
                client.setPasswd(passwdRequest.getNewPasswd());

                if (clientService.saveOrUpdate(client) > 0) {
                    LOGGER.debug("update password successful");
                    return;
                }
                LOGGER.warn("fail to update password ");
                throw new StInternalErrorException(FsExceptionBean.passwdUpdateError.getMessage(), FsExceptionBean.passwdUpdateError.getErrorCode());
            }
            LOGGER.warn("password input error");
            throw new StInternalErrorException(FsExceptionBean.passwdWrong.getMessage(), FsExceptionBean.passwdWrong.getErrorCode());

        }
        LOGGER.warn("user not exist");
        throw new StInternalErrorException(FsExceptionBean.userNotFound.getMessage()
                , FsExceptionBean.userNotFound.getErrorCode());
    }

    @PostMapping("/resetPasswd")
    public void resetPasswd(@RequestBody StResetPasswdRequest resetpw, HttpServletRequest request) {
        if (StringUtils.isEmpty(resetpw.getMobile()) || StringUtils.isEmpty(resetpw.getPasswd()) ||
                StringUtils.isEmpty(resetpw.getAuthcode())) {
            LOGGER.warn("request parameter error");
            throw new StBadRequestException();
        }

        StAuthcode acVo = new StAuthcode();
        acVo.setMobile(resetpw.getMobile());
        acVo.setAuthcode(resetpw.getAuthcode());

        StAuthcode csAuthcode = authcodeService.getAuthcode(acVo);
        if (csAuthcode != null) {
            Date atime = csAuthcode.getAtime();
            if (atime.getTime() < System.currentTimeMillis()) {
                LOGGER.warn("authCode has expired");
                throw new StInternalErrorException(FsExceptionBean.authCodeExpired.getMessage(), FsExceptionBean.authCodeExpired.getErrorCode());
            }
            StClient client = new StClient();
            client.setPhone(resetpw.getMobile());
            client.setName(resetpw.getName());
            StClient clientInDB = clientService.getClient(client);
            if (clientInDB != null) {
                StClient stClient = new StClient();
                stClient.setId(clientInDB.getId());
                stClient.setPasswd(resetpw.getPasswd());

                if (clientService.saveOrUpdate(stClient) > 0) {
                    LOGGER.debug("update password successful by authCode");
                    return;
                }
                LOGGER.warn("fail to update password ");
                throw new StInternalErrorException(FsExceptionBean.passwdUpdateError.getMessage(),
                        FsExceptionBean.passwdUpdateError.getErrorCode());
            }
            LOGGER.warn("fail to update password , user not exist , forbidden");
            throw new StInternalErrorException(FsExceptionBean.userNotFound.getMessage()
                    , FsExceptionBean.userNotFound.getErrorCode());
        }
        LOGGER.warn("authCode not send");
        throw new StInternalErrorException(FsExceptionBean.authCodeNotFound.getMessage(),
                FsExceptionBean.authCodeNotFound.getErrorCode());

    }

    @GetMapping("/logout/{uid}")
    public void logout(HttpServletRequest request, @PathVariable("uid") String uid) {
        String uidHeader = request.getAttribute("uid").toString();
        if (!uid.equals(uidHeader)) {
            LOGGER.warn("fail to logout , id not common , forbidden");
            throw new StForbiddenException();
        }

        StToken csToken = new StToken();
        csToken.setUid(Long.valueOf(uid));
        csToken.setAtime(new Timestamp(System.currentTimeMillis()));
        if (tokenService.updateToken(csToken) == null) {
            LOGGER.warn("fail to update token");
            throw new StInternalErrorException(FsExceptionBean.tokenUpdateError.getMessage(), FsExceptionBean.tokenUpdateError.getErrorCode());
        }
    }
    @GetMapping("/contacts/{uid}")
    public StUserContactsResponse contacts(HttpServletRequest request,@PathVariable("uid") String uid){
        String uidHeader = request.getAttribute("uid").toString();
        if (!uid.equals(uidHeader)) {
            LOGGER.warn("id not common , forbidden");
            throw new StForbiddenException();
        }

        List<StClientHas> clientHasListA = clientHasService.getByUid(Long.valueOf(uid));
        List<Long> ids  = new ArrayList<>();

        for (StClientHas clientHas:clientHasListA) {
            ids.add(clientHas.getCltB());
        }
        StUserContactsResponse response = new StUserContactsResponse();
        if(ids.size() == 0){
          LOGGER.debug("this client is not friends");
          return response;
        }
        List<StClient>  contacts = clientService.getClientByIds(ids);
        List<StContact> contactList = new ArrayList<>();
        for(StClient client :contacts){
            StContact contact = new StContact();
            contact.setId(client.getClientID().getId());
            contact.setName(client.getName());
            contact.setDesp(client.getDscp());
            contact.setAvatar(client.getAvatar());
            contactList.add(contact);
        }
        response.setContacts(contactList);

        return  response;
    }
    @PutMapping("/avatar")
    public String addAvatar(HttpServletRequest request, @RequestParam(value = "file") MultipartFile file,
                            @RequestParam(value = "uid") Integer uid,
                            @RequestParam(value = "fileType") String fileType) {


        // Get the file name
        String fileName = file.getOriginalFilename();
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Uploaded file name：{}", fileName);
        }
        // Get the file suffix name
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Uploaded suffix : {}", suffixName);
        }
        // file upload path
        //String filePath = fsApplicationProperties.getUploadDir();
        fileName = StSystemConstant.IMAGE_AVATAR + uid + suffixName;
        File convFile = new File(file.getOriginalFilename());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("avatar-filename: {}", file.getOriginalFilename());
            LOGGER.debug("filename: {}", file.getOriginalFilename());
        }

        try {
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {

        } catch (IOException e) {

        }
        String uploadImageUrl = StServicesProvider.INSTANCE.getUploadFile(applicationProperties.getUploader()).uploadImage(fileName, convFile);
        convFile.delete();
        StClient client = new StClient();
        client.setId(uid);
        client.setAvatar(uploadImageUrl);
        if (clientService.saveOrUpdate(client) > 0) {
            return uploadImageUrl;
        } else {
            LOGGER.warn("fail to update avatar");
            throw new StInternalErrorException(FsExceptionBean.avatarUpdateError.getMessage(), FsExceptionBean.avatarUpdateError.getErrorCode());
        }
    }

    private String renewToken(Long uid) {
        long current = System.currentTimeMillis();
        String t = uid + StSystemConstant.TOKEN_SPLITTER + current;
        String token = Base64.encodeBase64URLSafeString(t.getBytes());
        Timestamp atime = new Timestamp(current + Long.valueOf(applicationProperties.getTokenExpired()));
        Timestamp utime = new Timestamp(current);

        StToken csToken = new StToken();
        csToken.setUid(uid);
        StToken csTokenResult = tokenService.getToken(csToken);
        if (csTokenResult != null) {
            csTokenResult.setToken(token);
            csTokenResult.setAtime(atime);
            csTokenResult.setCtime(utime);
        } else {
            csTokenResult = new StToken();
            csTokenResult.setUid(uid);
            csTokenResult.setToken(token);
            csTokenResult.setAtime(atime);
            csTokenResult.setCtime(utime);
        }
        return tokenService.saveOrUpdate(csTokenResult) != null ? token : null;
    }

    private StLoginResponse renewResponse(StClient client) {
        String token = renewToken(client.getClientID().getId());
        if (StringUtils.isNotEmpty(token)) {
            StLoginResponse response = new StLoginResponse();

            List<Long> ids = new ArrayList<>();

            if(client.getList1() != null){
                for(StClientHas clientHas : client.getList1()){
                    ids.add(clientHas.getCltB());
                }
            }

            if(client.getList2() != null){
                for(StClientHas ch : client.getList2()){
                    ids.add(ch.getCltA());
                }
            }

            ArrayList<StAppFriend> fl = null;
            if(ids.size() != 0){
                List<StClient> list = clientService.getClientByIds(ids);
                fl = new ArrayList<>();
                for (StClient c : list) {
                    fl.add(new StAppFriend(c));
                }
            }

            response.setClient(new StAppClient(client, fl));
            response.setToken(token);
            return response;
        }
        LOGGER.warn("fail to update token");
        throw new StInternalErrorException(FsExceptionBean.tokenUpdateError.getMessage(), FsExceptionBean.tokenUpdateError.getErrorCode());

    }
}



