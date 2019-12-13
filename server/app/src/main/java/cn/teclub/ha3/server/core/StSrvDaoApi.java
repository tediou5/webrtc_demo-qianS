package cn.teclub.ha3.server.core;

import cn.teclub.ha3.net.StClientID;

import java.util.ArrayList;

public interface  StSrvDaoApi {

    StModelClient loadClient(final StClientID id, final boolean load_friends);

    boolean addFriendship( final StClientID id1, final StClientID id2, final boolean admin);

    ArrayList<StModelClient> queryOnlineAll();

    ArrayList<StModelClient> queryClients(final StClientID[] ids);

    void updateRecord(final StDbTable record);

    StModelClient queryClientByName(final String clt_name) ;

}
