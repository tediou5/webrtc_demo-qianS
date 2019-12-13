package cn.teclub.ha3.coco_server.model.dao;


import java.util.List;

/**
 * @author Tao Zhang
 */
public interface StMessageDao {
    int deleteByPrimaryKey(Long id);

    int insert(StBeanMessage record);

    int insertSelective(StBeanMessage record);

    StBeanMessage selectByPrimaryKey(Long id);

    List<StBeanMessage> selectByRecord(StBeanMessage record);

    int updateByPrimaryKeySelective(StBeanMessage record);

    int updateByPrimaryKeyWithBLOBs(StBeanMessage record);

    int updateByPrimaryKey(StBeanMessage record);
}