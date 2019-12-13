package cn.teclub.ha3.coco_server.model.dao;

import cn.teclub.ha3.coco_server.model.dao.StBeanAuthcode;
import cn.teclub.ha3.coco_server.model.dao.StAuthcodeDao;
import cn.teclub.ha3.coco_server.model.StAuthcodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author Tao Zhang
 */
@Service
public class StAuthcodeServiceImpl implements StAuthcodeService {

    @Autowired
    private StAuthcodeDao authcodeDao;

    @Override
    public StBeanAuthcode getAuthcode(StBeanAuthcode csAuthcode) {
        return authcodeDao.selectByAuthcode(csAuthcode);
    }

    @Override
    public int saveOrUpdate(StBeanAuthcode csAuthcode) {
        if (csAuthcode.getId() != null) {
            return authcodeDao.updateByPrimaryKeySelective(csAuthcode);
        } else {
            return authcodeDao.insertSelective(csAuthcode);
        }
    }
}
