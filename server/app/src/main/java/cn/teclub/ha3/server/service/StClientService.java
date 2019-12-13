package cn.teclub.ha3.server.service;


import cn.teclub.ha3.server.model.StClient;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Interface used to process client additions, deletions, modifications, and queries
 * @author zt
 */
public interface StClientService {


    StClient getClient(StClient record);

    StClient getClientById(Long id);

    @Transactional(propagation = Propagation.REQUIRED)
    int saveOrUpdate(StClient record);

    @Transactional(propagation = Propagation.REQUIRED)
    int updateByIdSelective(StClient record);

    @Transactional(propagation = Propagation.REQUIRED)
    int deleteById(Long id);

    List<StClient> getClientByIds(List<Long> ids);

}