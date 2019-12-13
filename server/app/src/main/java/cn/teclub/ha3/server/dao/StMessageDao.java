package cn.teclub.ha3.server.dao;

import cn.teclub.ha3.server.model.StMessage;

public interface StMessageDao {
    int deleteByPrimaryKey(Long id);

    int insert(StMessage record);

    int insertSelective(StMessage record);

    StMessage selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StMessage record);

    int updateByPrimaryKeyWithBLOBs(StMessage record);

    int updateByPrimaryKey(StMessage record);
}