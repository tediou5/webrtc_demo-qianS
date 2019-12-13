package cn.teclub.ha3.server.service;

import cn.teclub.ha3.server.model.StClientHas;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StClientHasService {

    @Transactional(propagation = Propagation.REQUIRED)
    int deleteById(Long id);

    @Transactional(propagation = Propagation.REQUIRED)
    int deleteByUid(Long id);

    @Transactional(propagation = Propagation.REQUIRED)
    int deleteByDid(Long id);

    @Transactional(propagation = Propagation.REQUIRED)
    int saveOrUpdate(StClientHas record);

    StClientHas getClientHasById(Long id);

    StClientHas getClientHasByRecord(StClientHas record);

    List<StClientHas> getByUid(Long uid);

    List<StClientHas> getByDid(Long did);

    @Transactional(propagation = Propagation.REQUIRED)
    int deleteByRecord(StClientHas record);



}
