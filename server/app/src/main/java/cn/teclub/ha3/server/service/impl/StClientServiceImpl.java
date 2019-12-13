package cn.teclub.ha3.server.service.impl;



import cn.teclub.ha3.server.dao.StClientDao;
import cn.teclub.ha3.server.model.StClient;
import cn.teclub.ha3.server.service.StClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class StClientServiceImpl implements StClientService {

    @Autowired
    StClientDao clientDao;


    @Override
    public StClient getClient(StClient record) {
        return clientDao.selectByClient(record);
    }

    @Override
    public StClient getClientById(Long id) {
        return clientDao.selectByPrimaryKey(id);
    }

    @Override
    public int saveOrUpdate(StClient record) {
        int count;
        if(record.getClientID().getId() == 0){
            count = clientDao.insertSelective(record);
        }else {
            count = clientDao.updateByPrimaryKeySelective(record);
        }
        return count;
    }

    @Override
    public int updateByIdSelective(StClient record) {
        return clientDao.updateByPrimaryKeySelective(record);
    }

    @Override
    public int deleteById(Long id) {
        return clientDao.deleteByPrimaryKey(id);
    }

    @Override
    public List<StClient> getClientByIds(List<Long> ids) {
        return clientDao.selectByIds(ids);
    }
}
