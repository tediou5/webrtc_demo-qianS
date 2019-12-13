package cn.teclub.ha3.coco_server.model.dao;

import cn.teclub.ha3.coco_server.model.StMessageService;
import cn.teclub.ha3.coco_server.model.StModelException;
import cn.teclub.ha3.request.StWsMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StMessageServiceImpl  implements StMessageService {

    @Autowired
    StMessageDao messageDao;

    @Override
    public int deleteByPrimaryKey(Long id) {
        return messageDao.deleteByPrimaryKey(id);
    }

    @Override
    public int insert(StBeanMessage record) {
        return messageDao.insert(record);
    }

    @Override
    public StBeanMessage selectByPrimaryKey(Long id) {
        return messageDao.selectByPrimaryKey(id);
    }

    @Override
    public List<StBeanMessage> selectByRecord(StBeanMessage record) {
        return messageDao.selectByRecord(record);
    }

    @Override
    public int saveOrUpdate(StBeanMessage record) {
        int count = 0;
        if(record.getId() == null){
            count = messageDao.insertSelective(record);
        }else {
            count = messageDao.updateByPrimaryKeySelective(record);
        }
        return count;
    }



    @Override
    public long save(StWsMessage msg, boolean sent) throws StModelException {
        StBeanMessage db_msg = new StBeanMessage(msg);
        db_msg.setState(sent ? StBeanMessage.MessageState.HASSENT.ordinal() : StBeanMessage.MessageState.UNSENT.ordinal());
        if (messageDao.insertSelective(db_msg) < 1){
            throw new StModelException("fail to insert message into DB");
        }
        return db_msg.getId();
    }

    @Override
    public boolean update(StWsMessage msg, boolean sent) {
        StBeanMessage db_msg = new StBeanMessage(msg);
        db_msg.setState(sent ? StBeanMessage.MessageState.HASSENT.ordinal() : StBeanMessage.MessageState.UNSENT.ordinal());
        return  messageDao.updateByPrimaryKeySelective(db_msg)> 0;
    }


    @Override
    public List<StWsMessage> queryPendingMessages() {
        StBeanMessage messageVo = new StBeanMessage();
        messageVo.setState(StBeanMessage.MessageState.UNSENT.ordinal());
        List<StBeanMessage> list =  messageDao.selectByRecord(messageVo);
        ArrayList<StWsMessage> res = new ArrayList<>();
        for(StBeanMessage e : list) {
            StWsMessage ws_msg = new StWsMessage();
            ws_msg.setSsid(e.getId());
            ws_msg.setCmd(e.getCmd().shortValue());
            ws_msg.setSsid(e.getSrc());
            ws_msg.setDst(e.getDst());
            ws_msg.setInfo(e.getInfo());
            ws_msg.setLen(e.getLen().shortValue());
            res.add(ws_msg);
        }
        return res;
    }
}
