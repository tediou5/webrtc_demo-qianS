package cn.teclub.ha3.server.core;

import cn.teclub.ha3.net.StClientID;
import cn.teclub.ha3.utils.StErrUnimplemented;
import cn.teclub.ha3.utils.StObject;

import java.util.ArrayList;


/**
 * [Theodor: 2019/1/31] use StDBObject in old StServer project, as the current DB is not changed.
 */
@SuppressWarnings("ALL")
public class StSrvDao extends StObject implements StSrvDaoApi
{
    private static StSrvDao _ins = new StSrvDao();

    public static StSrvDao instance(){
        return _ins;
    }


    private final StDBObject.ObjectMgr objectMgr;
    private final StSrvConfig conf;

    private StSrvDao() {
        this.conf = StSrvConfig.instance();
        this.objectMgr = StDBObject.ObjectMgr.getInstance();
    }


    @Override
    public StModelClient loadClient(StClientID id, boolean load_friends) {
            StModelClient ci = null;
        try {
            StDBObject dbobj = objectMgr.getNextObject();
            ci  = dbobj.loadClient(id, load_friends);
            objectMgr.putObject(dbobj);
            if(ci == null) {
                log.error("fail to load client!");
            }else {
                log.debug("client-info: " + ci.dumpSimple());
                log.debug("get client-info: " + ci.dump());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ci;
    }


    @Override
    public StModelClient queryClientByName(final String clt_name) {
        StModelClient ci = null;
        try {
            StDBObject dbobj = objectMgr.getNextObject();
            ci  = dbobj.queryClientByName(clt_name);
            objectMgr.putObject(dbobj);
            log.debug("get client-info: " + ci.dumpSimple());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return ci;
    }

    @Override
    public ArrayList<StModelClient> queryClients(StClientID[] ids) {
        ArrayList<StModelClient> list = null;
        try {
            StDBObject dbobj = objectMgr.getNextObject();
            list = dbobj.queryClients(ids);
            objectMgr.putObject(dbobj);
            log.debug( String.format("get %d clients from DB ", list.size())) ;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return list;
    }



    @Override
    public boolean addFriendship(StClientID id1, StClientID id2, boolean admin) {
        throw new StErrUnimplemented();
    }

    @Override
    public ArrayList<StModelClient> queryOnlineAll() {
        throw new StErrUnimplemented();
    }


    @Override
    public void updateRecord(StDbTable record) {
        throw new StErrUnimplemented();
    }
}
