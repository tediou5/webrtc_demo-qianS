package cn.teclub.ha3.coco_server.model.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
/**
 * @author Tao Zhang
 */
public interface StClientDao {
    int deleteByPrimaryKey(Long id);

    int insert(StBeanClient record);

    int insertSelective(StBeanClient record);

    StBeanClient selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StBeanClient record);

    //int updateByPrimaryKey(StBeanClient record);

    StBeanClient selectByClient(StBeanClient record);

    List<StBeanClient> selectByIds(List<Long> ids);

    List<StBeanClient> selectByKeyword(@Param("keyword") String keyword,
                                       @Param("page") Integer page, @Param("size") Integer size);
}