package cn.teclub.ha3.server.util;

import cn.teclub.ha3.utils.StConst;
import cn.teclub.ha3.utils.StObject;

import java.util.HashMap;
import java.util.Random;

class StToken extends StObject
{
    /**
     * release: StConst.TOKEN_LIFE_MS (> 30 days);
     * test:    5*60*1000 (5 min)
     * test:    3600*1000 (1 hour)
     */
    static final long LIFE_MAX_MS = 5*60*1000;

    static final Random RANDOM = new Random(0xFFFF00FF);
    final long   uid;
    final String value;
    final long   tsCreate;


    StToken(long uid) {
        this.uid = uid;
        this.tsCreate = System.currentTimeMillis();
        this.value = "STTOKEN" + util.to16CharHex(uid) +
                "TIME" + util.getTimeStampMS(tsCreate) +
                util.to16CharHex(RANDOM.nextLong());
    }


    boolean expired(){
        final long life = System.currentTimeMillis() - tsCreate;
        return life > LIFE_MAX_MS;
    }


    public String toString(){
        return "[Token]0x" +  util.to16CharHex(uid) + "," + util.getTimeStamp(tsCreate) + "," + value ;
    }
}


public class StTokenMgr extends StObject
{
    private static StTokenMgr _ins = new StTokenMgr();
    public static StTokenMgr instance() {return _ins;}
    private StTokenMgr(){  }


    // todo: use thread-safe map
    private HashMap<Long, StToken> tokenMap = new HashMap<>();


    private StToken getToken(long uid) {
         final StToken tk = tokenMap.get(uid);
        if(tk == null ){
            return null;
        }
        if( tk.expired()) {
            log.warn("delete expired token: {} ",  tk);
            tokenMap.remove(uid);
            return null;
        }
        return tk;
    }


    public String getTokenStr(long uid){
        final StToken tk = getToken(uid);
        return tk == null ? null: tk.value;
    }


    public void deleteToken(long uid) {
        final StToken tk = tokenMap.remove(uid);
        log.debug("delete token: {} ",  tk);
    }


    public String createToken(long uid) {
        final StToken tk = tokenMap.remove(uid);
        if(tk != null) {
            log.warn("delete previous token: {}", tk);
        }

        final StToken tk_new = new StToken(uid);
        tokenMap.put(uid, tk_new);
        log.debug("create new token: {}", tk_new);
        return tk_new.value;
    }


    public boolean isTokenValid(long uid, String token){
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
