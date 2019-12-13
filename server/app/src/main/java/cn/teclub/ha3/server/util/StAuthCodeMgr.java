package cn.teclub.ha3.server.util;

import cn.teclub.ha3.utils.StObject;

import java.util.HashMap;
import java.util.Random;

@SuppressWarnings("WeakerAccess")
class StAuthCode extends StObject
{
    /**
     * release:  120*1000 (2 min)
     * test:      30*1000 (30s)
     */
    static final long LIFE_MAX_MS = 30*1000;

    static final Random RANDOM = new Random(0xFFFF00FF);
    final String phone;
    final String value;
    final long   tsCreate;


    StAuthCode(String phone ) {
        this.phone = phone;
        this.tsCreate = System.currentTimeMillis();
        final int v =  200000 + RANDOM.nextInt(800000);
        this.value = "" + v;
    }


    boolean expired(){
        final long life = System.currentTimeMillis() - tsCreate;
        return life > LIFE_MAX_MS;
    }


    public String toString(){
        return "[AuthCode]ph:" + phone + "," + util.getTimeStamp(tsCreate) + "," + value ;
    }
}


@SuppressWarnings("unused")
public class StAuthCodeMgr extends StObject
{
    private static StAuthCodeMgr _ins = new StAuthCodeMgr();
    public static StAuthCodeMgr instance() {return _ins;}
    private StAuthCodeMgr(){  }


    // todo: use thread-safe map
    // map: phone-number --> auto code
    private HashMap<String, StAuthCode> itemMap = new HashMap<>();


    private StAuthCode getAuthCode(String phone) {
        final StAuthCode tk = itemMap.get(phone);
        if(tk == null ){
            return null;
        }
        if( tk.expired()) {
            log.warn("delete expired code: {} ",  tk);
            itemMap.remove(phone);
            return null;
        }
        return tk;
    }


    public String getAuthStr(String phone){
        final StAuthCode tk = getAuthCode(phone);
        return tk == null ? null: tk.value;
    }


    public void deleteAuth(String phone) {
        final StAuthCode tk = itemMap.remove(phone);
        log.debug("delete token: {} ",  tk);
    }


    public String createAuth(String phone) {
        final StAuthCode tk = itemMap.remove(phone);
        if(tk != null) {
            log.warn("delete previous token: {}", tk);
        }

        final StAuthCode tk_new = new StAuthCode(phone);
        itemMap.put(phone, tk_new);
        log.debug("create new token: {}", tk_new);
        return tk_new.value;
    }


    public boolean isAuthValid(String uid, String token){
        if(token == null) {
            return false;
        }

        // TODO: [Theodor: 2019/5/16] save token in DB; Otherwise, token cannot live long
        //
        // final StToken tk = getToken(uid);
        // return tk != null && token.equals(tk.value);
        //

        // NOTE: this is a temp solution!
        log.warn("TODO: get token from DB & check if it is valid... {}", token);
        return token.contains("STTOKEN") && token.contains("TIME");
    }


}
