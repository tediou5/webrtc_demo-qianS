package cn.teclub.ha3.server.service.impl;


import cn.teclub.ha3.server.dao.StTokenDao;
import cn.teclub.ha3.server.model.StToken;
import cn.teclub.ha3.server.service.StTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

@Service
@CacheConfig(cacheNames = "Token")
public class StTokenServiceImpl implements StTokenService {
    @Autowired
    private StTokenDao tokenDao;


    @Override
    public StToken getToken(StToken record) {
        return tokenDao.selectByToken(record);
    }

    @Override
    public StToken getTokenById(Integer id) {
        return tokenDao.selectByPrimaryKey(id);
    }

    @Override
    @CachePut(key = "#record.uid")
    public StToken saveOrUpdate(StToken record) {
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
    public StToken updateByIdSelective(StToken record) {
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
    public StToken updateToken(StToken record) {
        int count = tokenDao.updateByUid(record);
        return count > 0 ? record : null;
    }

}