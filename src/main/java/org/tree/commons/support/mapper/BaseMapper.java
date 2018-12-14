package org.tree.commons.support.mapper;

import java.util.List;

public interface BaseMapper<T> {
    long countByExample(Example example);

    int deleteByExample(Example example);

    int deleteByPrimaryKey(Long id);

    int insert(T record);

    int insertSelective(T record);

    java.util.List<T> selectByExample(Example example);

    T selectByPrimaryKey(Long id);

    int updateByExampleSelective(T record, Example example);

    int updateByExample(T record, Example example);

    int updateByPrimaryKeySelective(T record);

    int updateByPrimaryKey(T record);

    List<T> querySelective(String sql);

    List<T> querySelectiveByExample(Example example);

    int insertBatchSelective(List<T> records);
}