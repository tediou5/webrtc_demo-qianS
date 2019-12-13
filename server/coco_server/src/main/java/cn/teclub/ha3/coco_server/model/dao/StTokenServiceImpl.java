package cn.teclub.ha3.coco_server.model.dao;


import cn.teclub.ha3.coco_server.model.dao.StBeanToken;
import cn.teclub.ha3.coco_server.model.dao.StTokenDao;
import cn.teclub.ha3.coco_server.model.StTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

/**
 * @author zhangtao
 */
@Service
@CacheConfig(cacheNames = "Token")
public class StTokenServiceImpl implements StTokenService {
    @Autowired
    private StTokenDao tokenDao;


    @Override
    public StBeanToken getToken(StBeanToken record) {
        return tokenDao.selectByToken(record);
    }

    @Override
    public StBeanToken getTokenById(Integer id) {
        return tokenDao.selectByPrimaryKey(id);
    }

    @Override
    @CachePut(key = "#record.uid")
    public StBeanToken saveOrUpdate(StBeanToken record) {
        int count;
        if (record.getId() == null) {
            count = tokenDao.insertSelective(record);
        } else {
            count = tokenDao.updateByPrimaryKeySelective(record);
        }

        return count > 0 ? record : null;
    }

    @Override
    @CachePut(key = "#record.uid")
    public StBeanToken updateByIdSelective(StBeanToken record) {
        int count = tokenDao.updateByPrimaryKeySelective(record);
        return count > 0 ? record : null;
    }

    @Override
    @CacheEvict(allEntries = true)
    public int deleteById(Integer id) {
        return 0;
    }

    @Override
    @CachePut(key = "#record.uid")
    public StBeanToken updateToken(StBeanToken record) {
        int count = tokenDao.updateByUid(record);
        return count > 0 ? record : null;
    }

}