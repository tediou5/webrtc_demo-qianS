package cn.teclub.ha3.coco_server.model;


import cn.teclub.ha3.coco_server.model.dao.StBeanToken;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * @deprecated
 */
public interface StTokenService {
    StBeanToken getToken(StBeanToken record);

    StBeanToken getTokenById(Integer id);

    @Transactional(propagation = Propagation.REQUIRED)
    StBeanToken saveOrUpdate(StBeanToken record);

    @Transactional(propagation = Propagation.REQUIRED)
    StBeanToken updateByIdSelective(StBeanToken record);

    @Transactional(propagation = Propagation.REQUIRED)
    int deleteById(Integer id);

    @Transactional(propagation = Propagation.REQUIRED)
    StBeanToken updateToken(StBeanToken record);
}

