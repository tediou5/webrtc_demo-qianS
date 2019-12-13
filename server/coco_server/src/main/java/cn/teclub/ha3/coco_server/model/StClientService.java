package cn.teclub.ha3.coco_server.model;


import cn.teclub.ha3.api.StLoginWNameRequest.StLoginType;
import cn.teclub.ha3.coco_server.model.dao.StBeanClient;
import cn.teclub.ha3.net.StClientID;
import cn.teclub.ha3.request.StAppClient;
import cn.teclub.ha3.request.StAppFriend;
import cn.teclub.ha3.request.StBasicClient;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Interface used to process client additions, deletions, modifications, and queries
 * @author zt
 */
public interface StClientService {


    /**
     * @deprecated
     */
    StBeanClient getClient(StBeanClient record);


    /**
     * @deprecated
     */
    StBeanClient getClientById(Long id);

    /**
     * @deprecated
     */
    @Transactional(propagation = Propagation.REQUIRED)
    int saveOrUpdate(StBeanClient record);

    /**
     * @deprecated
     */
    @Transactional(propagation = Propagation.REQUIRED)
    int updateByIdSelective(StBeanClient record);


    /**
     * TODO: [Theodor: 2019/11/27] add param 'password'; change return type to 'boolean';
     */
    @Transactional(propagation = Propagation.REQUIRED)
    boolean deleteById(Long id,String password) throws StModelException;


    /**
     * @deprecated
     */
    List<StBeanClient> getClientByIds(List<Long> ids);


    /**
     * @deprecated
     */
    List<StBeanClient> getClientsByKeyword(String keyword, Integer page, Integer size);


    /**
     *
     * @return null if name or password error
     */
    StClientID validLogin(String name, String pass, StLoginType type) throws StModelException;


    /**
     * load a client with friend list
     */
    StAppClient loadClient(StClientID id);


    void updateClientInfo(StBasicClient client) throws StModelException;


    /**
     *
     * @throws StModelException fail to update
     */
    void updatePassword(StClientID id, String old_pass, String new_pass , boolean isReset) throws StModelException;


    /**
     * @return null if client does not exist
     * TODO: review this method
     */
    StAppClient getClientBy(StBasicClient client);


    /**
     * todo: change param to client-id
     */
    List<StAppFriend>  searchFriend(StClientID clientID);



    /**
     * Search for client with keyword.
     *
     * @param keyword used in 'name' and 'phone'
     * @param page TODO
     * @param size TODO
     * @return the clients with the keyword, NOT its friends!
     */
    List<StAppFriend>  searchContactBy(String keyword, int page, int size);


    StAppFriend getContact(StClientID id);


    /**
     * insert a new client.
     *
     * @param client a new client object, without ID
     * @throws StModelException DB operation fails
     */
    void saveNewClient(StBasicClient client, String pass) throws StModelException;


    /**
     *
     * @param clt_a ID of client A in tb_client_has
     * @param clt_b ID of client A in tb_client_has
     * @throws StModelException fail to add the relation
     */
    void addRelation(StClientID clt_a, StClientID clt_b) throws StModelException;


    /**
     * add an admin relationship for a device
     *
     * @throws StModelException fail to add the relation
     */
    void addAdminRelation(StClientID userId, StClientID deviceID) throws StModelException;


    /**
     * delete the relation ship
     */
    void deleteRelation(StClientID userId, StClientID deviceID);


    boolean hasRelation(StClientID clt_a, StClientID clt_b);


    boolean hasClient(StClientID id);


    /**
     * @param device_id Device ID
     * @return null if device or its admin is NOT found
     */
    StAppFriend getDeviceAdmin(StClientID device_id);

}