package org.tree.commons.support.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BaseMapper<T> {

    T selectByPrimaryKey(Long id);

    int insert(T record);

    int insertSelective(T record);

    int updateByPrimaryKeySelective(T record);

    int updateByPrimaryKey(T record);

    int insertSelectiveWithGeneratedKey(T record);

    int insertBatchSelective(List<T> records);

    int deleteByPrimaryKey(Long id);

    // 下面的方法将被重载
    // 如 BaseMapper::countByExample(Example<T>) 和 UserMapper::countByExample(UserExample) 不一样
    // Example<T> 和 UserExample 被重载了
    // 不影响使用，但是日常写代码不应该出现这样的写法

    long countByExample(Example<T> example);

    List<T> querySelective(@Param("args") Args<T> args, @Param("example") Example<T> example);

    List<T> selectByExample(Example<T> example);

    int updateByExampleSelective(@Param("record") T record, @Param("example") Example<T> example);

    int updateByExample(@Param("record") T record, @Param("example") Example<T> example);

    int deleteByExample(Example<T> example);

}