package cn.teclub.ha3.coco_server.model;


import cn.teclub.ha3.coco_server.model.dao.StBeanClientHas;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * @deprecated
 */
public interface StClientHasService {

    @Transactional(propagation = Propagation.REQUIRED)
    int deleteById(Long id);

    @Transactional(propagation = Propagation.REQUIRED)
    int deleteByUid(Long id);

    @Transactional(propagation = Propagation.REQUIRED)
    int deleteByDid(Long id);

    @Transactional(propagation = Propagation.REQUIRED)
    int saveOrUpdate(StBeanClientHas record);

    StBeanClientHas getClientHasById(Long id);

    StBeanClientHas getClientHasByRecord(StBeanClientHas record);

    List<StBeanClientHas> getByUid(Long uid);

    List<StBeanClientHas> getByDid(Long did);

    @Transactional(propagation = Propagation.REQUIRED)
    int deleteByRecord(StBeanClientHas record);



}
