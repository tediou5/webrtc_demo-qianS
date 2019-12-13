package cn.teclub.ha3.coco_server.model.dao;


import cn.teclub.ha3.api.StLoginWNameRequest;
import cn.teclub.ha3.api.StLoginWNameRequest.StLoginType;
import cn.teclub.ha3.coco_server.model.StClientService;
import cn.teclub.ha3.coco_server.model.StModelException;
import cn.teclub.ha3.coco_server.sys.StSystemConstant;
import cn.teclub.ha3.exception.FsExceptionBean;
import cn.teclub.ha3.model.StFriendRole;
import cn.teclub.ha3.net.StClientID;
import cn.teclub.ha3.net.StClientType;
import cn.teclub.ha3.request.StAppClient;
import cn.teclub.ha3.request.StAppFriend;
import cn.teclub.ha3.request.StBasicClient;
import cn.teclub.ha3.utils.StObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author zhangtao
 */
@Service
public class StClientServiceImpl extends StObject implements StClientService {

    @Autowired
    StClientDao clientDao;

    @Autowired
    StClientHasDao clientHasDao;


    @Override
    public StBeanClient getClient(StBeanClient record) {
        return clientDao.selectByClient(record);
    }

    @Override
    public StBeanClient getClientById(Long id) {
        return clientDao.selectByPrimaryKey(id);
    }

    @Override
    public int saveOrUpdate(StBeanClient record) {
        int count;
        if(record.getId().getId() == 0){
            count = clientDao.insertSelective(record);
        }else {
            count = clientDao.updateByPrimaryKeySelective(record);
        }
        return count;
    }

    @Override
    public int updateByIdSelective(StBeanClient record) {
        return clientDao.updateByPrimaryKeySelective(record);
    }

    @Override
    public boolean deleteById(Long id,String password) throws StModelException {

        StBeanClient client = new StBeanClient();
        client.setClientId(id);
        client.setPasswd(password);

        StBeanClient clientInDB = clientDao.selectByClient(client);
        if (clientInDB == null) {
            log.warn("the password you entered is incorrect.");
            throw new StModelException(FsExceptionBean.passwdWrong.getMessage(), FsExceptionBean.passwdWrong.getErrorCode());
        }

        int deleteCounts = clientHasDao.deleteByUid(clientInDB.getId().getId());
        log.debug(" The number of deleted  client contacts is {}",deleteCounts);

        int counts = clientHasDao.deleteByDid(clientInDB.getId().getId());
        log.debug(" The number of deleted  client contacts is {}",counts);

        return clientDao.deleteByPrimaryKey(clientInDB.getId().getId())>0;
    }

    @Override
    public List<StBeanClient> getClientByIds(List<Long> ids) {
        return clientDao.selectByIds(ids);
    }

    @Override
    public List<StBeanClient> getClientsByKeyword(String keyword, Integer page, Integer size) {

        int firstResult = page - 1;
        if (firstResult < 0) {
            firstResult = 0;
        } else {
            firstResult = firstResult * size;
        }
        return clientDao.selectByKeyword(keyword,firstResult,size);
    }

    @Override
    public StClientID validLogin(String name, String pass, StLoginType type) throws StModelException {
        log.debug(" validLogin api is used ");
        StBeanClient beanClient = new StBeanClient();
        if (type==StLoginType.PHONE) {
            beanClient.setPhone(name);
        } else if (type==StLoginType.NAME) {
            beanClient.setName(name);
        } else {
            beanClient.setMacAddr(name);
        }

        StBeanClient beanClientInDB = clientDao.selectByClient(beanClient);
        if (type==StLoginType.MACADDR) {
            final String password = "123456";
            if (beanClientInDB == null) {
                StBeanClient client = new StBeanClient(StClientType.GATEWAY);
                client.setName(name);
                client.setMacAddr(name);
                client.setPasswd(password);
                client.setDscp("client");
                client.setIconTS(1l);
                client.setCreateTime(new Date());
                client.setLastLogin(new Date());
                client.setLastLogoff(new Date());
                client.setOnlineTime(2);
                client.setPublicIP("0.0.0.0");
                clientDao.updateByPrimaryKeySelective(client);

                log.debug("{} mac address insert successfully", name);
                StBeanClient clientInDB = clientDao.selectByPrimaryKey(client.getId().getId());
                clientInDB.setName(StSystemConstant.GW + client.getId().getId());
                clientInDB.setLabel(StSystemConstant.GW + client.getId().getId());

                clientDao.updateByPrimaryKeySelective(client);
                log.debug("{} mac address update successfully", name);

                return new StClientID(clientInDB.getClientId());

            }
        }
        if (beanClientInDB != null) {
            if(!beanClientInDB.getPasswd().equals(pass)){
                log.warn("password not equal");
                throw new StModelException(FsExceptionBean.passwdWrong.getMessage(), FsExceptionBean.passwdWrong.getErrorCode());
            }
            return new StClientID(beanClientInDB.getClientId());
        }
        log.warn("client in dataBase not found");
        throw new StModelException(FsExceptionBean.userNotFound.getMessage(), FsExceptionBean.userNotFound.getErrorCode());
    }

    @Override
    public StAppClient loadClient(StClientID id) {
        log.debug(" loadClient api is used ");
        StBeanClient clientInDB =  clientDao.selectByPrimaryKey(id.getId());
        if(clientInDB==null)
            return null;
        List<Long> ids = new ArrayList<>();
        HashMap<Long,Integer> typeMap = new HashMap<>();

        for(StBeanClientHas clientHas : clientInDB.getList1()){
            ids.add(clientHas.getCltB());
            typeMap.put(clientHas.getCltB(),clientHas.getType());
        }

        ArrayList<StAppFriend> friends = null;
        if(ids.size() != 0){
            List<StBeanClient> list = clientDao.selectByIds(ids);
            friends = new ArrayList<>();
            for (StBeanClient c : list) {
                friends.add(new StAppFriend(c,StFriendRole.getRole(typeMap.get(c.getId().getId()))));
            }
        }
        StAppClient appClient = new StAppClient(clientInDB);
        appClient.setFriends(friends);
        return appClient;
    }

    @Override
    public void updateClientInfo(StBasicClient client) throws StModelException {
        log.debug(" updateClientInfo api is used ");
        StBeanClient beanClientInDB = clientDao.selectByPrimaryKey(client.getId().getId());
        if (beanClientInDB == null) {
            log.warn("user not exist");
            throw new StModelException(FsExceptionBean.userNotFound.getMessage()
                    , FsExceptionBean.userNotFound.getErrorCode());
        }
        if (client.getName() != null) {
            if (!beanClientInDB.getName().equals(client.getPhone())) {
                log.warn("user name can only be modified once");
                throw new StModelException(FsExceptionBean.nameModifyError.getMessage()
                        , FsExceptionBean.nameModifyError.getErrorCode());
            }
        }

        StBeanClient beanClient = new StBeanClient();
        beanClient.setClientId(client.getId().getId());
        beanClient.setName(client.getName());
        beanClient.setLabel(client.getLabel());
        beanClient.setDscp(client.getDscp());
        beanClient.setPhone(client.getPhone());
        beanClient.setAvatar(client.getAvatar());

        if(clientDao.updateByPrimaryKeySelective(beanClient)>0){
            log.debug("update client info successfully");
        }else {
            log.warn("update client info  failed,username or phone already exists ");
            throw new StModelException(FsExceptionBean.usernameOrPhoneAlreadyExistsError.getMessage()
                    , FsExceptionBean.usernameOrPhoneAlreadyExistsError.getErrorCode());
        }
    }

    @Override
    public void updatePassword(StClientID id, String old_pass, String new_pass , boolean isReset) throws StModelException {
        log.debug(" updatePassword api is used ");
        StBeanClient clientInDB = clientDao.selectByPrimaryKey(id.getId());
        if (clientInDB != null) {
            if(!isReset){
                if (!old_pass.equals(clientInDB.getPasswd())) {
                    log.warn(" password not equal ");
                    throw new StModelException(FsExceptionBean.passwdWrong.getMessage(), FsExceptionBean.passwdWrong.getErrorCode());
                }
            }

            StBeanClient client = new StBeanClient();
            client.setClientId(id.getId());
            client.setPasswd(new_pass);

            if (clientDao.updateByPrimaryKeySelective(client) > 0) {
                log.debug("update password successful");
                return;
            }
            log.warn("fail to update password ");
            throw new StModelException(FsExceptionBean.passwdUpdateError.getMessage(), FsExceptionBean.passwdUpdateError.getErrorCode());


        }
        log.warn("user not exist");
        throw new StModelException(FsExceptionBean.userNotFound.getMessage()
                , FsExceptionBean.userNotFound.getErrorCode());
    }


    @Override
    public StAppClient getClientBy(StBasicClient client) {
        log.debug(" getClientBy api is used ");
        StBeanClient beanClientVo = new StBeanClient();
        beanClientVo.setName(client.getName());
        beanClientVo.setPhone(client.getPhone());

        StBeanClient beanClientInDB = clientDao.selectByClient(beanClientVo);
        if(beanClientInDB==null)
            return null;
        return new StAppClient(beanClientInDB);
    }

    @Override
    public List<StAppFriend> searchFriend(StClientID clientID) {
        log.debug(" getClientByName api is used ");

        List<StBeanClientHas> clientHasListA = clientHasDao.selectByUid(clientID.getId());
        List<Long> ids  = new ArrayList<>();

        for (StBeanClientHas clientHas:clientHasListA) {
            ids.add(clientHas.getCltB());
        }
        if(ids.size() == 0){
            log.debug("this client is not friends");
            return null;
        }
        List<StBeanClient>  contacts = clientDao.selectByIds(ids);
        List<StAppFriend> contactList = new ArrayList<>();
        for(StBeanClient contact :contacts){
            StAppFriend friend = new StAppFriend(contact);
            contactList.add(friend);
        }
        return contactList;
    }

    @Override
    public List<StAppFriend> searchContactBy(String keyword, int page, int size) {
        log.debug(" searchContactBy api is used ");
        int firstResult = page - 1;
        if (firstResult < 0) {
            firstResult = 0;
        } else {
            firstResult = firstResult * size;
        }
        List<StBeanClient> list = clientDao.selectByKeyword(keyword,firstResult,size);
        ArrayList<StAppFriend>  res = new ArrayList<>();
        for(StBeanClient e: list) {
            res.add(new StAppFriend((e)));
        }
        return res;
    }

    @Override
    public StAppFriend getContact(StClientID id) {
        log.debug(" getContact api is used ");

        StBeanClient beanClientInDB = clientDao.selectByPrimaryKey(id.getId());
        return new StAppFriend(beanClientInDB);
    }

    @Override
    public void saveNewClient(StBasicClient client, String pass) throws StModelException {
        log.debug(" loadClient api is used ");
        StBeanClient beanClient = new StBeanClient(client.getFlag_ClientType());
        beanClient.setName(client.getName());
        beanClient.setLabel(client.getLabel());
        beanClient.setPasswd(pass);
        beanClient.setDscp(client.getDscp());
        beanClient.setPhone(client.getPhone());
        beanClient.setAvatar(client.getAvatar());
        beanClient.setIconTS(1l);
        beanClient.setCreateTime(new Date());
        beanClient.setLastLogin(new Date());
        beanClient.setLastLogoff(new Date());
        beanClient.setOnlineTime(2);
        beanClient.setPublicIP("0.0.0.0");

        if(clientDao.insertSelective(beanClient)>0){
            log.debug(" save client successfully");
        }else {
            log.warn(" new client fail to save ");
            throw new  StModelException(FsExceptionBean.userSiginedError.getMessage(),
                    FsExceptionBean.userSiginedError.getErrorCode());
        }
    }


    @Override
    public void addRelation(StClientID clt_a, StClientID clt_b) throws StModelException {
        log.debug(" addRelation api is used ");


        StBeanClientHas clientHas = new StBeanClientHas();
        clientHas.setCltA(clt_a.getId());
        clientHas.setCltB(clt_b.getId());
        if(clientHasDao.selectByRecord(clientHas) == null){
            clientHas.setType(StFriendRole.NONE.ordinal());
            if(clientHasDao.insertSelective(clientHas)>0){
                log.debug("uid {} and did {} add successfully",clientHas.getCltA(),clientHas.getCltB());
            }
        }

        clientHas.setCltA(clt_b.getId());
        clientHas.setCltB(clt_a.getId());
        clientHas.setType(null);
        if(clientHasDao.selectByRecord(clientHas) == null) {
            clientHas.setType(StFriendRole.NONE.ordinal());
            if (clientHasDao.insertSelective(clientHas) > 0) {
                log.debug("uid {} and did {} add successfully", clientHas.getCltB(), clientHas.getCltA());
            }
        }

    }

    @Override
    public void addAdminRelation(StClientID userId, StClientID deviceID) throws StModelException {
        log.debug(" loadClient api is used ");

        StBeanClient clientInDB = clientDao.selectByPrimaryKey(deviceID.getId());
        if(clientInDB == null){
            log.warn("not find this device");
            throw new StModelException(FsExceptionBean.notFoundDevice.getMessage(),
                    FsExceptionBean.notFoundDevice.getErrorCode());
        }
        if(StClientType.GATEWAY!=clientInDB.getFlag_ClientType()){
            log.warn("this client is not a device");
            throw new StModelException(FsExceptionBean.notIsDevice.getMessage(),
                    FsExceptionBean.notIsDevice.getErrorCode());
        }
        StBeanClientHas clientHas = new StBeanClientHas();
        clientHas.setCltB(deviceID.getId());
        clientHas.setType(StFriendRole.SLAVE.ordinal());
        StBeanClientHas clientHasInDB = clientHasDao.selectByRecord(clientHas);

        if(clientHasInDB != null){
            log.warn("this device already exists as an administrator");
            throw new StModelException(FsExceptionBean.adminAlreadyExists.getMessage(),
                    FsExceptionBean.adminAlreadyExists.getErrorCode());
        }

        clientHas.setCltA(userId.getId());
        clientHas.setType(null);
        StBeanClientHas chInDB = clientHasDao.selectByRecord(clientHas);

        clientHas.setType(StFriendRole.SLAVE.ordinal());
        if(chInDB != null){
            clientHas.setId(chInDB.getId());
            if(clientHasDao.updateByPrimaryKeySelective(clientHas)>0)
                log.debug("uid {} and did {} add successfully",userId.getId(),deviceID.getId());

        }else {
            if(clientHasDao.insertSelective(clientHas)>0)
                log.debug("uid {} and did {} add successfully",userId.getId(),deviceID.getId());
        }

        StBeanClientHas deviceHas = new StBeanClientHas();
        deviceHas.setCltA(deviceID.getId());
        deviceHas.setCltB(userId.getId());

        StBeanClientHas deviceHasInDB = clientHasDao.selectByRecord(deviceHas);
        deviceHas.setType(StFriendRole.MASTER.ordinal());
        if(deviceHasInDB != null){
            deviceHas.setId(deviceHasInDB.getId());
            if(clientHasDao.updateByPrimaryKeySelective(deviceHas)>0)
                log.debug("the device {} was successfully added by the user {}",userId.getId(),deviceID.getId());

        }else {

            if(clientHasDao.insertSelective(deviceHas)>0)
                log.debug("the device {} was successfully added by the user {}",userId.getId(),deviceID.getId());
        }

    }


    @Override
    public void deleteRelation(StClientID userId, StClientID deviceId){

        StBeanClientHas clientHas = new StBeanClientHas();
        clientHas.setCltA(userId.getId());
        clientHas.setCltB(deviceId.getId());

        if(clientHasDao.selectByRecord(clientHas) != null) {
            if (clientHasDao.deleteByRecord(clientHas) > 0) {
                log.debug("client {} delete device {} successfully",userId.getId(),deviceId.getId());
            }
        }
        clientHas.setCltB(userId.getId());
        clientHas.setCltA(deviceId.getId());

        if (clientHasDao.selectByRecord(clientHas) != null) {
            if (clientHasDao.deleteByRecord(clientHas) > 0) {
                log.debug("client {} delete device {} successfully",userId.getId(),deviceId.getId());
            }

        }
    }

    @Override
    public boolean hasRelation(StClientID clt_a, StClientID clt_b) {
        int count = 0;
        StBeanClientHas beanClientHasVo = new StBeanClientHas();
        beanClientHasVo.setCltA(clt_a.getId());
        beanClientHasVo.setCltB(clt_b.getId());
        if(clientHasDao.selectByRecord(beanClientHasVo)!=null){
            count += 1;
        }

        beanClientHasVo.setCltA(clt_b.getId());
        beanClientHasVo.setCltB(clt_a.getId());
        if(clientHasDao.selectByRecord(beanClientHasVo)!=null){
            count += 1;
        }

        return count!=0;
    }

    @Override
    public boolean hasClient(StClientID id) {

        return clientDao.selectByPrimaryKey(id.getId())!=null;

    }

    @Override
    public StAppFriend getDeviceAdmin(StClientID device_id) {
        StBasicClient device = clientDao.selectByPrimaryKey(device_id.getId());
        if(device==null)
            return null;
        StBeanClientHas clientHas = new StBeanClientHas();
        clientHas.setCltB(device_id.getId());
        clientHas.setType(StFriendRole.SLAVE.ordinal());

        StBeanClientHas adminHas = clientHasDao.selectByRecord(clientHas);
        if(adminHas==null)
            return null;

        StBeanClient admin = clientDao.selectByPrimaryKey(adminHas.getCltA());
        if(admin==null)
            return null;

        return new StAppFriend(admin);
    }
}
