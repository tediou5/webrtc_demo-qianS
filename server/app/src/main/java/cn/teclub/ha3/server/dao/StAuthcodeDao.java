package cn.teclub.ha3.server.dao;

import cn.teclub.ha3.server.model.StAuthcode;

public interface StAuthcodeDao {

    int deleteByPrimaryKey(Integer id);

    int insert(StAuthcode record);

    StAuthcode selectByAuthcode(StAuthcode record);

    int insertSelective(StAuthcode record);

    StAuthcode selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(StAuthcode record);

    int updateByPrimaryKey(StAuthcode record);
}