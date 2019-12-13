package cn.teclub.ha3.server.service;


import cn.teclub.ha3.server.model.StAuthcode;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface StAuthcodeService {

   StAuthcode getAuthcode(StAuthcode Authcode);

    @Transactional(propagation = Propagation.REQUIRED)
    int saveOrUpdate(StAuthcode Authcode);
}
