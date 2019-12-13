package cn.teclub.ha3.server.ctrl;

import cn.teclub.ha3.net.StClientID;
import cn.teclub.ha3.net.StClientType;
import cn.teclub.ha3.request.*;
import cn.teclub.ha3.server.core.StModelClient;
import cn.teclub.ha3.utils.StErrCode;
import cn.teclub.ha3.utils.StError;
import cn.teclub.sms.StSmsOpt_SendX;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;


/**
 * demo controller
 * ONLY for testing
 */
@SuppressWarnings("ALL")
@RestController
public class StAccountCtrl extends StController
{
    StAccountCtrl(){
        log.info("create controller!");
    }


    private StLoginRes createLoginResult(final StModelClient mc1, String token) {
        log.debug("load client: " + mc1.dumpSimple());

        final StClientID[] ids = mc1.getFriendList().toArray(new StClientID[]{});
        final ArrayList<StModelClient> cl  = global.dao.queryClients(ids);
        final ArrayList<StAppFriend>  fl = new ArrayList<>();
        for(StModelClient c: cl) {
            fl.add(new StAppFriend(c));
        }

        StLoginRes res = new StLoginRes();
        if(token == null) {
            token = global.tokenMgr.createToken(mc1.getRawId());
        }
        res.setToken(token);
        res.setClient(new StAppClient(mc1, fl));
        log.info( "Login success: {} ", mc1.getName());
        return res;
    }


    @PostMapping("/v1/login/pass")
    public StLoginRes pass(@RequestBody StLoginReq req) {
        log.debug("login with password: %s/%s ", req.getAcct(), req.getPass() );
        final StModelClient mc0 = global.dao.queryClientByName(req.getAcct());
        log.warn("## model client: " + mc0);
        if(mc0 == null) {
            log.warn("account not found: {}", req.getAcct());
            return failLogin(StErrCode.ERR_SRV_ACCT_NOT_FOUND);
        }
        final StClientType ct = mc0.getFlag_ClientType();
        if( ct == StClientType.USER){
            if (!req.getPass().equals(mc0.getPasswd())) {
                log.warn("pass mismatch: {} != {}", req.getPass(), mc0.getPasswd());
                return failLogin(StErrCode.ERR_SRV_PASS_WRONG);
            }
        }else{
            log.warn("TODO: check password on GW/Monitor");
        }

        final StModelClient mc1 = global.dao.loadClient(mc0.getClientID(), true);
        return createLoginResult(mc1, null);
    }


    private StLoginRes failLogin(StErrCode errCode) {
        StLoginRes res = new StLoginRes();
        res.setToken("");
        res.setClient(null);
        res.setErrcode(errCode);
        return res;
    }



    @PostMapping("/v1/mobile/auth")
    public StMobileAuthRes mobileAuth(@RequestBody StMobileAuthReq req) {
        final String phone = req.getMobile();
        log.debug( "get mobile authCode: {} ", req.getMobile());

        final String auth = global.authCodeMgr.createAuth(req.getMobile());
        StMobileAuthRes res = new StMobileAuthRes();

        try {
            log.warn("##inf: send auth code: {} ", auth );
            new StSmsOpt_SendX(phone, auth);
            res.setAuthcode("NULL"); // NOTE: auth-code is sent via mobile
            res.setMobile(phone);
        } catch (IOException e) {
            e.printStackTrace();
            res.setErrcode(StErrCode.ERR_SRV_AUTH_CODE_NOT_SEND);
        }
        return  res;
    }



    @PostMapping("/v1/signup/mobile")
    public StSignupRes signup(@RequestBody StSignupReq req) {
        log.debug( "signup with mobile, auth-code & password: {}/{}/{} ", req.getMobile(), req.getAuthcode(), req.getPass());
        log.warn("TODO...");

        final String auth = global.authCodeMgr.getAuthStr(req.getMobile());
        if(auth == null) {
            return new StSignupRes(StErrCode.ERR_SRV_AUTH_CODE_NOT_FOUND);
        }

        if(! auth.equals(req.getAuthcode())) {
            return new StSignupRes(StErrCode.ERR_SRV_AUTH_CODE_NOT_EQUAL);
        }

        //return new StSignupRes();
        return new StSignupRes(StErrCode.ERR_TODO);
    }



    @PostMapping("/v1/login/token")
    public StLoginRes token(@RequestBody StLoginReq req) {
        final StClientID CID        = req.fetchClientID();
        final String     TOKEN_STR  = req.getToken();
        log.debug( "{}/{} login with token: {} ", req.getAcct(), CID, TOKEN_STR);

        if( ! global.tokenMgr.isTokenValid(CID.getId(), TOKEN_STR)) {
            log.warn("INVALID Token: {} !", TOKEN_STR);
            StLoginRes res = new StLoginRes();
            res.setToken("");
            res.setClient(null);
            res.setErrcode(StErrCode.ERR_SRV_TOKEN_WRONG);
            return res;
        }

        final StModelClient mc0 = global.dao.loadClient(CID, true);
        return createLoginResult(mc0, TOKEN_STR);
    }



    @PostMapping("/v1/acct/queryFrd")
    public StLoginRes queryFrd(@RequestBody StUserRequest req) {
        // query the lastest friend list;
        throw new StError("TODO");
    }

}
