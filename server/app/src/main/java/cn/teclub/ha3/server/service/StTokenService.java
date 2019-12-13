package cn.teclub.ha3.server.service;


import cn.teclub.ha3.server.model.StToken;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface StTokenService {
    StToken getToken(StToken record);

    StToken getTokenById(Integer id);

    @Transactional(propagation = Propagation.REQUIRED)
    StToken saveOrUpdate(StToken record);

    @Transactional(propagation = Propagation.REQUIRED)
    StToken updateByIdSelective(StToken record);

    @Transactional(propagation = Propagation.REQUIRED)
    int deleteById(Integer id);

    @Transactional(propagation = Propagation.REQUIRED)
    StToken updateToken(StToken record);
}

