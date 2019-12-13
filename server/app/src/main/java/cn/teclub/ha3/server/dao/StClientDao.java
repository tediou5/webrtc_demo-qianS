package cn.teclub.ha3.server.dao;

import cn.teclub.ha3.server.model.StClient;

import java.util.List;

public interface StClientDao {
    int deleteByPrimaryKey(Long id);

    int insert(StClient record);

    int insertSelective(StClient record);

    StClient selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StClient record);

    int updateByPrimaryKey(StClient record);

    StClient selectByClient(StClient record);

    List<StClient> selectByIds(List<Long> ids);
}