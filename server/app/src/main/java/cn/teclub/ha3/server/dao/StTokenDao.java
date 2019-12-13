package cn.teclub.ha3.server.dao;


import cn.teclub.ha3.server.model.StToken;

public interface StTokenDao {

    int deleteByPrimaryKey(Integer id);

    int insert(StToken record);

    int insertSelective(StToken record);

    StToken selectByPrimaryKey(Integer id);

    StToken selectByToken(StToken Token);

    int updateByPrimaryKeySelective(StToken record);

    int updateByPrimaryKey(StToken record);

    int updateByUid(StToken record);
}