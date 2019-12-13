package cn.teclub.ha.client.rpr;

import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Logger;

import cn.teclub.common.ChuyuObj;
import cn.teclub.ha.net.StClientInfo;
import cn.teclub.ha.request.StNetPacket;


/**
 * a simple service is manged by app-layer
 */
public abstract class StcP2pSimpleService extends ChuyuObj
{
    private static HashMap<StNetPacket.Command, StcP2pSimpleService> servList = new HashMap<>();

    /**
     * delete all p2p-simple services when destroying andr-app object.
     */
    public static void deleteAll(){
        servList.clear();
    }


    public static void dumpAll(){
        Logger ssLog = Logger.getLogger("StcP2pSimpleService");
        ssLog.info("---- Dump All App Network Services ----------------");
        Collection<StcP2pSimpleService> list1 = servList.values();
        int i=0;
        for(StcP2pSimpleService s : list1){
            ssLog.info("---- ["+ i++ +"] app network service: " + s);
        }
        ssLog.info("----------------------------------------------------");
    }

    public static StcP2pSimpleService getService(StNetPacket.Command cmd){
        return servList.get(cmd);
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////



    protected final StNetPacket.Command  cmd;

    public StcP2pSimpleService(StNetPacket.Command cmd){
        this.cmd = cmd;
        util.assertTrue(servList.get(cmd) == null);
        servList.put(cmd, this);
    }


    protected void sendResponse(StNetPacket res_pkt){
        StcRprObject.getInstance().sendResponse(res_pkt);
    }


    /**
     * called in app-pulse
     */
    public void prc(final StClientInfo r_clt, final StNetPacket req){
        onRprRequest(r_clt, req);
    }



    protected abstract void onRprRequest(final StClientInfo r_clt, final StNetPacket pkt);


    public String toString(){
        return "[P2P Serv]" + cmd;
    }
}
