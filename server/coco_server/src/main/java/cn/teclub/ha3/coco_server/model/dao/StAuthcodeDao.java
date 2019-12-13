package cn.teclub.ha3.coco_server.model.dao;

/**
 * @author Tao Zhang
 */
public interface StAuthcodeDao {

    int deleteByPrimaryKey(Integer id);

    int insert(StBeanAuthcode record);

    StBeanAuthcode selectByAuthcode(StBeanAuthcode record);

    int insertSelective(StBeanAuthcode record);

    StBeanAuthcode selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(StBeanAuthcode record);

    int updateByPrimaryKey(StBeanAuthcode record);
}