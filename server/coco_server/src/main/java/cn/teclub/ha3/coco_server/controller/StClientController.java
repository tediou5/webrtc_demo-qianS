package cn.teclub.ha3.coco_server.controller;

import cn.teclub.ha3.api.*;
import cn.teclub.ha3.coco_server.controller.exception.StBadRequestException;
import cn.teclub.ha3.coco_server.controller.exception.StInternalErrorException;
import cn.teclub.ha3.coco_server.network.StServicesProvider;
import cn.teclub.ha3.coco_server.model.*;
import cn.teclub.ha3.coco_server.model.dao.StBeanToken;
import cn.teclub.ha3.coco_server.sys.StApplicationProperties;
import cn.teclub.ha3.coco_server.sys.StSystemConstant;
import cn.teclub.ha3.exception.FsExceptionBean;
import cn.teclub.ha3.net.StClientID;
import cn.teclub.ha3.net.StClientType;
import cn.teclub.ha3.request.StAppClient;
import cn.teclub.ha3.request.StBasicClient;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;

/**
 * client restful api .
 * @author Tao Zhang
 */
@RestController
@RequestMapping("${rtc.api.prefix}/user")
public class StClientController extends StControllerBase {


    @Autowired
    StTokenService tokenService;

    @Autowired
    StApplicationProperties applicationProperties;

    @Autowired
    StClientManager clientManager;


    

    @PostMapping("/signin")
    public StLoginResponse signin(@RequestBody StSigninRequest request) {

        if (StringUtils.isEmpty(request.getAuthCode()) || StringUtils.isEmpty(request.getPhone())) {
            log.warn("request parameter error");
            throw new StBadRequestException();
        }
        final String password = "123456";
        StBasicClient clientVo = new StBasicClient();
        clientVo.setPhone(request.getPhone());
        StAppClient appClient = clientService.getClientBy(clientVo);

        if(appClient==null){
            if(clientManager.verifyAuth(request.getPhone(),request.getAuthCode())){
                StBasicClient basicClient = new StBasicClient(StClientType.USER);
                basicClient.setPhone(request.getPhone());
                basicClient.setName(request.getPhone());
                basicClient.setLabel(request.getPhone());
                basicClient.setDscp("client");
                try {
                    clientService.saveNewClient(basicClient,password);
                } catch (StModelException e) {
                    e.printStackTrace();
                    log.error(util.getExceptionDetails(e, "fail to save new client!"));
                    throw new StInternalErrorException(e.getMessage(),e.errorCode);
                }
                StAppClient client = clientService.getClientBy(clientVo);
                String token = renewToken(client.getId().getId());

                StLoginResponse response = new StLoginResponse();
                response.setClient(client);
                response.setToken(token);
                clientManager.deleteAuth(client.getPhone());

                return response;
            }
        }
        log.warn("this client has already registered");
        throw new StInternalErrorException(FsExceptionBean.userSigined.getMessage(), FsExceptionBean.userSigined.getErrorCode());
    }

    @PostMapping("/login/name")
    public StLoginResponse loginWithName(@RequestBody StLoginWNameRequest loginRequest) {

        if (StringUtils.isEmpty(loginRequest.getName()) || StringUtils.isEmpty(loginRequest.getPasswd())
                    || loginRequest.getType() == null) {
            log.warn("request parameter error");
            throw new StBadRequestException();
        }

        StClientID clientID = null;
        try {
            clientID = clientService.validLogin(loginRequest.getName(),loginRequest.getPasswd(),
                    loginRequest.getType());
        }catch (StModelException e){
            e.printStackTrace();
            log.error(util.getExceptionDetails(e, "fail to login with name !"));
            throw new StInternalErrorException(e.getMessage(),e.errorCode);
        }


        StAppClient appClient = clientService.loadClient(clientID);

        String token = renewToken(clientID.getId());
        StLoginResponse response = new StLoginResponse();
        response.setToken(token);
        response.setClient(appClient);

        return response;

    }

    @PostMapping("/login/phone")
    public StLoginResponse loginWithPhone(@RequestBody StLoginWPhoneRequest loginRequest) {

        if (StringUtils.isEmpty(loginRequest.getPhone()) || StringUtils.isEmpty(loginRequest.getAuthCode())) {
            log.warn("request parameter error");
            throw new StBadRequestException();
        }
        if (clientManager.verifyAuth(loginRequest.getPhone(), loginRequest.getAuthCode())) {

            StBasicClient clientVo = new StBasicClient();
            clientVo.setPhone(loginRequest.getPhone());
            StAppClient clientInDB = clientService.getClientBy(clientVo);

            if (clientInDB != null) {
                String token = renewToken(clientInDB.getId().getId());
                StLoginResponse response = new StLoginResponse();
                response.setToken(token);
                response.setClient(clientInDB);
                clientManager.deleteAuth(clientInDB.getPhone());
                return response;
            }
            log.warn("user in dataBase not found");
            throw new StInternalErrorException(FsExceptionBean.userNotFound.getMessage(), FsExceptionBean.userNotFound.getErrorCode());

        }
        log.warn("authCode not send");
        throw new StInternalErrorException(FsExceptionBean.authCodeNotFound.getMessage(), FsExceptionBean.authCodeNotFound.getErrorCode());
/*
        StBeanAuthcode authCode = new StBeanAuthcode();
        authCode.setAuthcode(loginRequest.getAuthCode());
        StBeanAuthcode authCodeInDB = authcodeService.getAuthcode(authCode);
        if (authCodeInDB != null) {
            if (authCodeInDB.getAtime().getTime() < System.currentTimeMillis()) {
                log.warn("authCode has expired");
                throw new StInternalErrorException(FsExceptionBean.authCodeExpired.getMessage(), FsExceptionBean.authCodeExpired.getErrorCode());
            }
            if(!authCodeInDB.getMobile().equals(loginRequest.getPhone())){
                log.warn("phone number and  authCode do not match ");
                throw new StInternalErrorException(FsExceptionBean.authCodeAndPhoneNotMatch.getMessage(), FsExceptionBean.authCodeAndPhoneNotMatch.getErrorCode());
            }
            StBeanClient client = new StBeanClient();
            client.setPhone(loginRequest.getPhone());

            StBeanClient clientInDB = clientService.getClient(client);
            if (clientInDB != null) {

                authCodeInDB.setAtime(new Date());
                authcodeService.saveOrUpdate(authCodeInDB);
                return renewResponse(clientInDB);
            }
            log.warn("user in dataBase not found");
            throw new StInternalErrorException(FsExceptionBean.userNotFound.getMessage(), FsExceptionBean.userNotFound.getErrorCode());

        }
        log.warn("authCode not send");
        throw new StInternalErrorException(FsExceptionBean.authCodeNotFound.getMessage(), FsExceptionBean.authCodeNotFound.getErrorCode());
    */
    }

    @PostMapping("/signout")
    @Transactional
    public void signout(HttpServletRequest request, @RequestBody StSiginoutRequest siginoutRequest) {
        if (StringUtils.isEmpty(siginoutRequest.getPasswd()) || siginoutRequest.getUid() == null) {
            log.warn("request parameter error");
            throw new StBadRequestException();
        }
        
        validRequest(siginoutRequest,request);
        
        try{
            clientService.deleteById(siginoutRequest.getUid(),siginoutRequest.getPasswd());
        }catch (StModelException e){
            e.printStackTrace();
            log.error(util.getExceptionDetails(e, "fail to sign out!"));
            throw new StInternalErrorException(e.getMessage(),e.errorCode);
        }

    }

    @GetMapping("/info/{uid}")
    public StUserInfoResponse info(@PathVariable("uid") Long uid, HttpServletRequest request) {

        validRequestUid(Long.valueOf(uid),request);
        StAppClient appClient = clientService.loadClient(new StClientID(uid));
        if (appClient == null) {
            log.warn("user not exist");
            throw new StInternalErrorException(FsExceptionBean.userNotFound.getMessage(), FsExceptionBean.userNotFound.getErrorCode());
        }
        StUserInfoResponse response = new StUserInfoResponse();
        response.setName(appClient.getName());
        response.setLabel(appClient.getLabel());
        response.setAvatar(appClient.getAvatar());
        response.setPhone(appClient.getPhone());

        log.debug("get user info successfully");
        return response;

    }

    @PutMapping("/info")
    public void updateInfo(@RequestBody StUserInfoRequest userInfoRequest, HttpServletRequest request) {
        
        validRequest(userInfoRequest,request);
        
        StBasicClient basicClient = new StBasicClient();
        basicClient.setId(new StClientID(userInfoRequest.getUid()));
        basicClient.setName(userInfoRequest.getName());
        basicClient.setLabel(userInfoRequest.getLabel());
        basicClient.setPhone(userInfoRequest.getPhone());
        try{
            clientService.updateClientInfo(basicClient);
        }catch (StModelException e){
            e.printStackTrace();
            log.error(util.getExceptionDetails(e, "fail to update client info !"));
            throw new StInternalErrorException(e.getMessage(), e.errorCode);
        }

    }


    @PutMapping("/passwd")
    public void updatePasswd(@RequestBody StPasswdRequest passwdRequest, HttpServletRequest request) {

        if (StringUtils.isEmpty(passwdRequest.getPasswd()) || StringUtils.isEmpty(passwdRequest.getNewPasswd())) {
            log.warn("request parameter error");
            throw new StBadRequestException();
        }
        validRequest(passwdRequest,request);
        try{
            clientService.updatePassword(new StClientID(passwdRequest.getUid()),passwdRequest.getPasswd()
                    ,passwdRequest.getNewPasswd(),false);
        }catch (StModelException e){
            e.printStackTrace();
            log.error(util.getExceptionDetails(e, "fail to update password!"));
            throw new  StInternalErrorException(e.getMessage(),e.errorCode);
        }

    }

    @PostMapping("/resetPasswd")
    public void resetPasswd(@RequestBody StResetPasswdRequest resetPasswdRequest, HttpServletRequest request) {
        if (StringUtils.isEmpty(resetPasswdRequest.getMobile()) || StringUtils.isEmpty(resetPasswdRequest.getPasswd()) ||
                StringUtils.isEmpty(resetPasswdRequest.getAuthcode())) {
            log.warn("request parameter error");
            throw new StBadRequestException();
        }
        if(clientManager.verifyAuth(resetPasswdRequest.getMobile(),resetPasswdRequest.getAuthcode())){
            StBasicClient client = new StBasicClient();
            client.setPhone(resetPasswdRequest.getMobile());
            client.setName(resetPasswdRequest.getName());

            StAppClient clientInDB = clientService.getClientBy(client);
            if(clientInDB != null){
                try{
                    clientService.updatePassword(clientInDB.getId(),null
                            ,resetPasswdRequest.getPasswd(),true);
                }catch (StModelException e){
                    e.printStackTrace();
                    log.error(util.getExceptionDetails(e, "fail to reset password!"));
                    throw new  StInternalErrorException(e.getMessage(),e.errorCode);
                }
            }


        }
        
       /*
        StBeanAuthcode acVo = new StBeanAuthcode();
        acVo.setMobile(resetPasswdRequest.getMobile());
        acVo.setAuthcode(resetPasswdRequest.getAuthcode());

        StBeanAuthcode csAuthcode = authcodeService.getAuthcode(acVo);
        if (csAuthcode != null) {
            Date atime = csAuthcode.getAtime();
            if (atime.getTime() < System.currentTimeMillis()) {
                log.warn("authCode has expired");
                throw new StInternalErrorException(FsExceptionBean.authCodeExpired.getMessage(), FsExceptionBean.authCodeExpired.getErrorCode());
            }
            StBeanClient client = new StBeanClient();
            client.setPhone(resetPasswdRequest.getMobile());
            client.setName(resetPasswdRequest.getName());
            StBeanClient clientInDB = clientService.getClient(client);
            if (clientInDB != null) {
                StBeanClient stClient = new StBeanClient();
                stClient.setId(clientInDB.getId());
                stClient.setPasswd(resetPasswdRequest.getPasswd());

                if (clientService.saveOrUpdate(stClient) > 0) {
                    log.debug("update password successful by authCode");
                    return;
                }
                log.warn("fail to update password ");
                throw new StInternalErrorException(FsExceptionBean.passwdUpdateError.getMessage(),
                        FsExceptionBean.passwdUpdateError.getErrorCode());
            }
            log.warn("fail to update password , user not exist , forbidden");
            throw new StInternalErrorException(FsExceptionBean.userNotFound.getMessage()
                    , FsExceptionBean.userNotFound.getErrorCode());
        }
        log.warn("authCode not send");
        throw new StInternalErrorException(FsExceptionBean.authCodeNotFound.getMessage(),
                FsExceptionBean.authCodeNotFound.getErrorCode());
*/
    }

    @GetMapping("/logout/{uid}")
    public void logout(HttpServletRequest request, @PathVariable("uid") String uid) {

        validRequestUid(Long.valueOf(uid),request);

        StBeanToken csToken = new StBeanToken();
        csToken.setUid(Long.valueOf(uid));
        csToken.setAtime(new Timestamp(System.currentTimeMillis()));
        if (tokenService.updateToken(csToken) == null) {
            log.warn("fail to update token");
            throw new StInternalErrorException(FsExceptionBean.tokenUpdateError.getMessage(), FsExceptionBean.tokenUpdateError.getErrorCode());
        }
    }

    @GetMapping("/contacts/{uid}")
    public StUserContactsResponse contacts(HttpServletRequest request,@PathVariable("uid") String uid){

        validRequestUid(Long.valueOf(uid),request);
        StAppClient client =  clientService.loadClient(new StClientID(Long.valueOf(uid)));
        StUserContactsResponse response = new StUserContactsResponse();
        if(client==null){
            log.warn("user not exist");
            throw new StInternalErrorException(FsExceptionBean.userNotFound.getMessage()
                    , FsExceptionBean.userNotFound.getErrorCode());
        }
        response.setContacts(client.getFriends());
        return  response;

    }

    @PutMapping("/avatar")
    public StUserInfoResponse addAvatar(HttpServletRequest request, @RequestParam(value = "file") MultipartFile file,
                            @RequestParam(value = "uid") Integer uid,
                            @RequestParam(value = "fileType") String fileType) {


        // Get the file name
        String fileName = file.getOriginalFilename();
        if (log.isTraceEnabled()) {
            log.trace("Uploaded file name：{}", fileName);
        }
        // Get the file suffix name
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        if (log.isTraceEnabled()) {
            log.trace("Uploaded suffix : {}", suffixName);
        }
        // file upload path
        //String filePath = fsApplicationProperties.getUploadDir();
        fileName = StSystemConstant.IMAGE_AVATAR + uid + suffixName;
        File convFile = new File(file.getOriginalFilename());
        if (log.isDebugEnabled()) {
            log.debug("avatar-filename: {}", file.getOriginalFilename());
            log.debug("filename: {}", file.getOriginalFilename());
        }

        try {
            convFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(convFile);
            fos.write(file.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String uploadImageUrl = StServicesProvider.getInstance(applicationProperties).getUploadFile(applicationProperties.getUploader()).uploadImage(fileName, convFile);
        convFile.delete();

        StBasicClient client = new StBasicClient();
        client.setId(new StClientID(uid));
        client.setAvatar(uploadImageUrl);

        try {
            clientService.updateClientInfo(client);
            StUserInfoResponse response =  new StUserInfoResponse();
            response.setAvatar(uploadImageUrl);
            log.debug("update avatar successfully");
            return response;
        } catch (StModelException e) {
            e.printStackTrace();
            log.error(util.getExceptionDetails(e, "fail to update avatar !"));
            throw new  StInternalErrorException(e.getMessage(),e.errorCode);
        }

    }

    private String renewToken(Long uid) {
        long current = System.currentTimeMillis();
        String t = uid + StSystemConstant.TOKEN_SPLITTER + current;
        String token = Base64.encodeBase64URLSafeString(t.getBytes());
        Timestamp atime = new Timestamp(current + Long.valueOf(applicationProperties.getTokenExpired()));
        Timestamp utime = new Timestamp(current);

        StBeanToken csToken = new StBeanToken();
        csToken.setUid(uid);
        StBeanToken csTokenResult = tokenService.getToken(csToken);
        if (csTokenResult != null) {
            csTokenResult.setAtime(atime);
            csTokenResult.setToken(token);
            csTokenResult.setCtime(utime);
        } else {
            csTokenResult = new StBeanToken();
            csTokenResult.setUid(uid);
            csTokenResult.setAtime(atime);
            csTokenResult.setToken(token);
            csTokenResult.setCtime(utime);
        }
        log.debug("user {} ,token is {}",uid,token);
        return tokenService.saveOrUpdate(csTokenResult) != null ? token : null;
    }

}



