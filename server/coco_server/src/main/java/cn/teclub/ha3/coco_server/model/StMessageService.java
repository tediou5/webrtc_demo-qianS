package cn.teclub.ha3.coco_server.model;

import cn.teclub.ha3.coco_server.model.dao.StBeanMessage;
import cn.teclub.ha3.request.StWsMessage;

import java.util.List;


/**
 *
 *
 * @author Tao Zhang, Guilin Cao
 */
public interface StMessageService {

    int deleteByPrimaryKey(Long id);

    /**
     * @deprecated
     */
    int insert(StBeanMessage record);

    /**
     * @deprecated
     */
    StBeanMessage selectByPrimaryKey(Long id);

    /**
     * @deprecated
     */
    List<StBeanMessage> selectByRecord(StBeanMessage record);

    /**
     * @deprecated
     */
    int saveOrUpdate(StBeanMessage record);


    /**
     * save a ws-message into DB
     *
     * @param msg  ws-message object without ID
     * @param sent true for a SENT Ws-message
     * @return the ID in DB record
     * @throws StModelException failure
     */
    long save(StWsMessage msg, boolean sent) throws StModelException;


    /**
     * update a ws-message
     *
     * @param msg  ws-message object with ID
     * @param sent true for a SENT Ws-message
     * @return true for success
     */
    boolean update(StWsMessage msg, boolean sent);


    /**
     * query all un-sent messages from DB
     */
    List<StWsMessage> queryPendingMessages();
}
