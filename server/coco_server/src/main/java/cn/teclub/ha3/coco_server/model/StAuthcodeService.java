package cn.teclub.ha3.coco_server.model;



import cn.teclub.ha3.coco_server.model.dao.StBeanAuthcode;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface StAuthcodeService {

   StBeanAuthcode getAuthcode(StBeanAuthcode Authcode);

    @Transactional(propagation = Propagation.REQUIRED)
    int saveOrUpdate(StBeanAuthcode Authcode);
}
