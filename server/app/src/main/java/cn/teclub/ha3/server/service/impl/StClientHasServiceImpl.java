package cn.teclub.ha3.server.service.impl;

import cn.teclub.ha3.server.dao.StClientHasDao;
import cn.teclub.ha3.server.model.StClientHas;
import cn.teclub.ha3.server.service.StClientHasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StClientHasServiceImpl implements StClientHasService {

    @Autowired
    StClientHasDao clientHasDao;

    @Override
    public int deleteById(Long id) {
        return clientHasDao.deleteByPrimaryKey(id);
    }

    @Override
    public int deleteByUid(Long id) {
        return clientHasDao.deleteByUid(id);
    }

    @Override
    public int deleteByDid(Long id) {
        return clientHasDao.deleteByDid(id);
    }

    @Override
    public int saveOrUpdate(StClientHas record) {
        int count;
        if(record.getId() == null){
            count = clientHasDao.insertSelective(record);
        }else {
            count = clientHasDao.updateByPrimaryKeySelective(record);
        }
        return count;
    }

    @Override
    public StClientHas getClientHasById(Long id) {
        return clientHasDao.selectByPrimaryKey(id);
    }

    @Override
    public StClientHas getClientHasByRecord(StClientHas record) {
        return clientHasDao.selectByRecord(record);
    }

    @Override
    public List<StClientHas> getByUid(Long uid) {
        return clientHasDao.selectByUid(uid);
    }

    @Override
    public List<StClientHas> getByDid(Long did) {
        return clientHasDao.selectByDid(did);
    }

    @Override
    public int deleteByRecord(StClientHas record) {
        return clientHasDao.deleteByRecord(record);
    }
}
