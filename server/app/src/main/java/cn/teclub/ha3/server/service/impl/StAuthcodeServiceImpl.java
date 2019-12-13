package cn.teclub.ha3.server.service.impl;



import cn.teclub.ha3.server.dao.StAuthcodeDao;
import cn.teclub.ha3.server.model.StAuthcode;
import cn.teclub.ha3.server.service.StAuthcodeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StAuthcodeServiceImpl implements StAuthcodeService {

    @Autowired
    private StAuthcodeDao authcodeDao;

    @Override
    public StAuthcode getAuthcode(StAuthcode csAuthcode) {
        return authcodeDao.selectByAuthcode(csAuthcode);
    }

    @Override
    public int saveOrUpdate(StAuthcode csAuthcode) {
        if (csAuthcode.getId() != null) {
            return authcodeDao.updateByPrimaryKeySelective(csAuthcode);
        } else {
            return authcodeDao.insertSelective(csAuthcode);
        }
    }
}
