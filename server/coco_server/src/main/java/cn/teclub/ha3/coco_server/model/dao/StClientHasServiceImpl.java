package cn.teclub.ha3.coco_server.model.dao;

import cn.teclub.ha3.coco_server.model.dao.StBeanClientHas;
import cn.teclub.ha3.coco_server.model.dao.StClientHasDao;
import cn.teclub.ha3.coco_server.model.StClientHasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhangtao
 */
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
    public int saveOrUpdate(StBeanClientHas record) {
        int count;
        if(record.getId() == null){
            count = clientHasDao.insertSelective(record);
        }else {
            count = clientHasDao.updateByPrimaryKeySelective(record);
        }
        return count;
    }

    @Override
    public StBeanClientHas getClientHasById(Long id) {
        return clientHasDao.selectByPrimaryKey(id);
    }

    @Override
    public StBeanClientHas getClientHasByRecord(StBeanClientHas record) {
        return clientHasDao.selectByRecord(record);
    }

    @Override
    public List<StBeanClientHas> getByUid(Long uid) {
        return clientHasDao.selectByUid(uid);
    }

    @Override
    public List<StBeanClientHas> getByDid(Long did) {
        return clientHasDao.selectByDid(did);
    }

    @Override
    public int deleteByRecord(StBeanClientHas record) {
        return clientHasDao.deleteByRecord(record);
    }
}
