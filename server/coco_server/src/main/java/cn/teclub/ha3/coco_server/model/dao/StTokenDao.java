package cn.teclub.ha3.coco_server.model.dao;


/**
 * @author Tao Zhang
 */
public interface StTokenDao {

    int deleteByPrimaryKey(Integer id);

    int insert(StBeanToken record);

    int insertSelective(StBeanToken record);

    StBeanToken selectByPrimaryKey(Integer id);

    StBeanToken selectByToken(StBeanToken Token);

    int updateByPrimaryKeySelective(StBeanToken record);

    int updateByPrimaryKey(StBeanToken record);

    int updateByUid(StBeanToken record);
}