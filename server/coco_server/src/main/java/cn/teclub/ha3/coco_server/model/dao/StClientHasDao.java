package cn.teclub.ha3.coco_server.model.dao;


import java.util.List;
/**
 * @author Tao Zhang
 */
public interface StClientHasDao {
    int deleteByPrimaryKey(Long id);

    int deleteByUid(Long id);

    int deleteByDid(Long id);

    int insert(StBeanClientHas record);

    int insertSelective(StBeanClientHas record);

    StBeanClientHas selectByPrimaryKey(Long id);

    StBeanClientHas selectByRecord(StBeanClientHas record);

    int updateByPrimaryKeySelective(StBeanClientHas record);

    int updateByPrimaryKey(StBeanClientHas record);

    List<StBeanClientHas> selectByUid(Long uid);

    List<StBeanClientHas> selectByDid(Long did);

    int deleteByRecord(StBeanClientHas record);
}