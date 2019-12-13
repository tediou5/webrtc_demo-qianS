package cn.teclub.ha3.server.dao;

import cn.teclub.ha3.server.model.StClientHas;

import java.util.List;

public interface StClientHasDao {
    int deleteByPrimaryKey(Long id);

    int deleteByUid(Long id);

    int deleteByDid(Long id);

    int insert(StClientHas record);

    int insertSelective(StClientHas record);

    StClientHas selectByPrimaryKey(Long id);

    StClientHas selectByRecord(StClientHas record);

    int updateByPrimaryKeySelective(StClientHas record);

    int updateByPrimaryKey(StClientHas record);

    List<StClientHas> selectByUid(Long uid);

    List<StClientHas> selectByDid(Long did);

    int deleteByRecord(StClientHas record);
}